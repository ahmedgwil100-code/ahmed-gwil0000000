package com.example.ui.viewmodel

import android.app.Activity
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ads.AdManager
import com.example.data.BONUS_WORDS
import com.example.data.CATEGORIES
import com.example.data.Category
import com.example.data.Cell
import com.example.data.FoundWord
import com.example.data.GameMode
import com.example.data.HIGHLIGHT_COLORS
import com.example.data.PlacedWord
import com.example.data.PreferencesManager
import com.example.data.WordGrid
import com.example.data.generateWordGrid
import com.example.sound.SoundManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.sign

enum class Screen {
    HOME,
    CATEGORY_SELECT,
    MODE_SELECT,
    GAME,
    RESULTS
}

data class FloatingNotice(
    val message: String,
    val isBonus: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

data class GameUiState(
    val currentScreen: Screen = Screen.HOME,
    val selectedCategory: Category = CATEGORIES[0],
    val selectedMode: GameMode = GameMode.CLASSIC,
    val grid: WordGrid? = null,
    val foundWords: List<FoundWord> = emptyList(),
    val foundBonusWords: Set<String> = emptySet(),
    val score: Int = 0,
    val coins: Int = 300,
    val hintsRemaining: Int = 3,
    val hintedCells: Set<Cell>? = null,
    val activeSelection: List<Cell> = emptyList(),
    val activeWordPreview: String = "",
    val timerSeconds: Int = 0, // Classic: seconds elapsed, Time: seconds remaining
    val isGameOver: Boolean = false,
    val isLevelCompleted: Boolean = false,
    val isSoundEnabled: Boolean = true,
    val completedCategoryIds: Set<String> = emptySet(),
    val floatingNotice: FloatingNotice? = null,
    val isWatchingRewardAd: Boolean = false,
    val rewardAdDialogMessage: String? = null
)

class GameViewModel(application: Application) : AndroidViewModel(application) {

    val prefs = PreferencesManager(application)
    val soundManager = SoundManager()
    val adManager = AdManager(application)

    private val _uiState = MutableStateFlow(
        GameUiState(
            coins = prefs.getCoins(),
            completedCategoryIds = prefs.getCompletedLevels(),
            isSoundEnabled = prefs.isSoundEnabled()
        )
    )
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private var hintPulseJob: Job? = null
    private var activeDragStartCell: Cell? = null

    init {
        soundManager.isSoundEnabled = prefs.isSoundEnabled()
    }

    fun toggleSound() {
        val newState = !soundManager.isSoundEnabled
        soundManager.isSoundEnabled = newState
        prefs.setSoundEnabled(newState)
        _uiState.update { it.copy(isSoundEnabled = newState) }
        soundManager.playTap()
    }

    fun navigateTo(screen: Screen) {
        soundManager.playTap()
        _uiState.update { it.copy(currentScreen = screen) }
    }

    fun selectCategory(category: Category) {
        soundManager.playTap()
        _uiState.update { it.copy(selectedCategory = category, currentScreen = Screen.MODE_SELECT) }
    }

    fun selectMode(mode: GameMode) {
        soundManager.playTap()
        _uiState.update { it.copy(selectedMode = mode) }
        startNewGame(mode, _uiState.value.selectedCategory)
    }

    fun startNewGame(mode: GameMode = _uiState.value.selectedMode, category: Category = _uiState.value.selectedCategory) {
        timerJob?.cancel()
        hintPulseJob?.cancel()

        val generated = generateWordGrid(category.words)
        val initialTimer = if (mode == GameMode.TIME) 120 else 0

        _uiState.update {
            it.copy(
                currentScreen = Screen.GAME,
                selectedCategory = category,
                selectedMode = mode,
                grid = generated,
                foundWords = emptyList(),
                foundBonusWords = emptySet(),
                score = 0,
                hintsRemaining = 3,
                hintedCells = null,
                activeSelection = emptyList(),
                activeWordPreview = "",
                timerSeconds = initialTimer,
                isGameOver = false,
                isLevelCompleted = false,
                floatingNotice = null
            )
        }

        prefs.incrementPuzzlesPlayed()
        startTimer()
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000L)
                val state = _uiState.value
                if (state.currentScreen != Screen.GAME || state.isGameOver || state.isLevelCompleted) {
                    break
                }

                if (state.selectedMode == GameMode.CLASSIC) {
                    _uiState.update { it.copy(timerSeconds = it.timerSeconds + 1) }
                } else {
                    // Time mode: countdown from 120
                    val next = state.timerSeconds - 1
                    if (next <= 0) {
                        _uiState.update { it.copy(timerSeconds = 0, isGameOver = true) }
                        soundManager.playGameOver()
                        triggerInterstitialAd(null)
                        _uiState.update { it.copy(currentScreen = Screen.RESULTS) }
                        break
                    } else {
                        if (next < 30) {
                            soundManager.playTick()
                        }
                        _uiState.update { it.copy(timerSeconds = next) }
                    }
                }
            }
        }
    }

    // Touch gesture handling
    fun onDragStart(cell: Cell) {
        val grid = _uiState.value.grid ?: return
        if (cell.row !in 0 until grid.size || cell.col !in 0 until grid.size) return
        activeDragStartCell = cell
        val wordStr = grid.letters[cell.row][cell.col].toString()
        _uiState.update {
            it.copy(
                activeSelection = listOf(cell),
                activeWordPreview = wordStr
            )
        }
        soundManager.playSwoosh()
    }

    fun onDragMove(cell: Cell) {
        val start = activeDragStartCell ?: return
        val grid = _uiState.value.grid ?: return
        if (cell.row !in 0 until grid.size || cell.col !in 0 until grid.size) return

        val dRow = cell.row - start.row
        val dCol = cell.col - start.col

        if (dRow == 0 && dCol == 0) {
            _uiState.update {
                it.copy(
                    activeSelection = listOf(start),
                    activeWordPreview = grid.letters[start.row][start.col].toString()
                )
            }
            return
        }

        // Project to straight line in 8 directions
        val absRow = abs(dRow)
        val absCol = abs(dCol)

        val stepRow: Int
        val stepCol: Int
        val length: Int

        if (absRow >= absCol && absCol < max(1, absRow / 2)) {
            // Vertical
            stepRow = sign(dRow.toDouble()).toInt()
            stepCol = 0
            length = absRow
        } else if (absCol >= absRow && absRow < max(1, absCol / 2)) {
            // Horizontal
            stepRow = 0
            stepCol = sign(dCol.toDouble()).toInt()
            length = absCol
        } else {
            // Diagonal
            stepRow = sign(dRow.toDouble()).toInt()
            stepCol = sign(dCol.toDouble()).toInt()
            length = max(absRow, absCol)
        }

        val cells = mutableListOf<Cell>()
        val letters = StringBuilder()
        for (i in 0..length) {
            val r = start.row + stepRow * i
            val c = start.col + stepCol * i
            if (r in 0 until grid.size && c in 0 until grid.size) {
                cells.add(Cell(r, c))
                letters.append(grid.letters[r][c])
            } else {
                break
            }
        }

        if (cells != _uiState.value.activeSelection) {
            soundManager.playSwoosh()
            _uiState.update {
                it.copy(
                    activeSelection = cells,
                    activeWordPreview = letters.toString()
                )
            }
        }
    }

    fun onDragEnd(activity: Activity? = null) {
        activeDragStartCell = null
        val state = _uiState.value
        val selection = state.activeSelection
        val grid = state.grid ?: return

        if (selection.isEmpty()) return

        val wordChars = selection.map { grid.letters[it.row][it.col] }.joinToString("")
        val reversedWordChars = wordChars.reversed()

        // 1. Check if matches an unfound puzzle word
        val categoryWords = state.selectedCategory.words
        val unfoundCategoryWords = categoryWords.filter { word ->
            state.foundWords.none { it.word.equals(word, ignoreCase = true) }
        }

        val matchedWord = unfoundCategoryWords.find {
            it.equals(wordChars, ignoreCase = true) || it.equals(reversedWordChars, ignoreCase = true)
        }

        if (matchedWord != null) {
            // Found a puzzle word!
            val nextColorIndex = state.foundWords.size % HIGHLIGHT_COLORS.size
            val newFoundWord = FoundWord(matchedWord, selection, nextColorIndex)
            val updatedFoundList = state.foundWords + newFoundWord
            val newScore = state.score + 10

            soundManager.playCorrectWord()
            showFloatingNotice("Word Found: $matchedWord (+10)", isBonus = false)

            val isAllCompleted = updatedFoundList.size >= categoryWords.size

            if (isAllCompleted) {
                // Complete level!
                timerJob?.cancel()
                val earnedCoins = 50
                val totalNewCoins = prefs.addCoins(earnedCoins)
                prefs.markLevelCompleted(state.selectedCategory.id)

                // Time bonus calculation if Time Mode: timeLeft * 2
                val timeBonus = if (state.selectedMode == GameMode.TIME) state.timerSeconds * 2 else 0
                val finalScore = newScore + timeBonus
                prefs.addScore(finalScore)

                soundManager.playVictory()

                _uiState.update {
                    it.copy(
                        foundWords = updatedFoundList,
                        score = finalScore,
                        coins = totalNewCoins,
                        isLevelCompleted = true,
                        completedCategoryIds = prefs.getCompletedLevels(),
                        activeSelection = emptyList(),
                        activeWordPreview = ""
                    )
                }

                viewModelScope.launch {
                    delay(1200L)
                    triggerInterstitialAd(activity)
                    _uiState.update { it.copy(currentScreen = Screen.RESULTS) }
                }
            } else {
                _uiState.update {
                    it.copy(
                        foundWords = updatedFoundList,
                        score = newScore,
                        activeSelection = emptyList(),
                        activeWordPreview = ""
                    )
                }
            }
            return
        }

        // 2. Check if matches a bonus word
        // Must be >=3 chars, in BONUS_WORDS, NOT in category words, not already found
        val isBonus = (wordChars.length >= 3 && BONUS_WORDS.contains(wordChars.uppercase()) && !categoryWords.contains(wordChars.uppercase())) ||
                (reversedWordChars.length >= 3 && BONUS_WORDS.contains(reversedWordChars.uppercase()) && !categoryWords.contains(reversedWordChars.uppercase()))

        val bonusCandidate = if (wordChars.length >= 3 && BONUS_WORDS.contains(wordChars.uppercase()) && !categoryWords.contains(wordChars.uppercase())) {
            wordChars.uppercase()
        } else if (reversedWordChars.length >= 3 && BONUS_WORDS.contains(reversedWordChars.uppercase()) && !categoryWords.contains(reversedWordChars.uppercase())) {
            reversedWordChars.uppercase()
        } else null

        if (bonusCandidate != null && !state.foundBonusWords.contains(bonusCandidate)) {
            // Found bonus word!
            val updatedBonus = state.foundBonusWords + bonusCandidate
            val newScore = state.score + 5
            prefs.addScore(5)
            soundManager.playBonusWord()
            showFloatingNotice("Bonus Word: $bonusCandidate (+5)", isBonus = true)

            _uiState.update {
                it.copy(
                    foundBonusWords = updatedBonus,
                    score = newScore,
                    activeSelection = emptyList(),
                    activeWordPreview = ""
                )
            }
            return
        }

        // 3. Otherwise, wrong selection
        soundManager.playWrong()
        _uiState.update {
            it.copy(
                activeSelection = emptyList(),
                activeWordPreview = ""
            )
        }
    }

    private fun showFloatingNotice(message: String, isBonus: Boolean) {
        _uiState.update { it.copy(floatingNotice = FloatingNotice(message, isBonus)) }
        viewModelScope.launch {
            delay(2000L)
            _uiState.update {
                if (it.floatingNotice?.message == message) it.copy(floatingNotice = null) else it
            }
        }
    }

    // Hint feature: picks a random unfound word and flashes cells in yellow (3 pulses over 1.5s)
    fun useHint() {
        val state = _uiState.value
        if (state.hintsRemaining <= 0) {
            soundManager.playWrong()
            return
        }

        val grid = state.grid ?: return
        val unfoundPlacedWords = grid.placedWords.filter { placed ->
            state.foundWords.none { it.word.equals(placed.word, ignoreCase = true) }
        }

        if (unfoundPlacedWords.isEmpty()) return

        val targetWord = unfoundPlacedWords.random()
        val targetCells = targetWord.cells.toSet()

        soundManager.playTap()
        _uiState.update {
            it.copy(
                hintsRemaining = it.hintsRemaining - 1,
                hintedCells = targetCells
            )
        }

        hintPulseJob?.cancel()
        hintPulseJob = viewModelScope.launch {
            // 3 pulses over 1.5 seconds (each pulse ~500ms)
            for (p in 0 until 3) {
                _uiState.update { it.copy(hintedCells = targetCells) }
                delay(300L)
                _uiState.update { it.copy(hintedCells = emptySet()) }
                delay(200L)
            }
            _uiState.update { it.copy(hintedCells = null) }
        }
    }

    /**
     * Watch Rewarded Ad to get extra hints (+2 hints).
     */
    fun watchRewardedAdForHints(activity: Activity?) {
        soundManager.playTap()
        _uiState.update { it.copy(isWatchingRewardAd = true) }

        adManager.showRewardedAd(
            activity = activity,
            onRewardEarned = { amount ->
                val addedHints = max(2, amount)
                _uiState.update {
                    it.copy(
                        hintsRemaining = it.hintsRemaining + addedHints,
                        isWatchingRewardAd = false,
                        rewardAdDialogMessage = null
                    )
                }
                soundManager.playBonusWord()
                showFloatingNotice("Earned +$addedHints Extra Hints!", isBonus = true)
            },
            onAdUnavailable = {
                // Friendly offline simulation dialog so user can still earn hints
                viewModelScope.launch {
                    _uiState.update {
                        it.copy(rewardAdDialogMessage = "Playing sponsored video for hints...")
                    }
                    delay(1500L)
                    _uiState.update {
                        it.copy(
                            hintsRemaining = it.hintsRemaining + 2,
                            isWatchingRewardAd = false,
                            rewardAdDialogMessage = null
                        )
                    }
                    soundManager.playBonusWord()
                    showFloatingNotice("Earned +2 Extra Hints!", isBonus = true)
                }
            }
        )
    }

    fun dismissRewardDialog() {
        _uiState.update { it.copy(isWatchingRewardAd = false, rewardAdDialogMessage = null) }
    }

    /**
     * Eligible trigger checkpoint for Interstitial Ad
     * Frequency cap: every 2nd eligible trigger, minimum 60-second interval
     */
    fun triggerInterstitialAd(activity: Activity?, onComplete: () -> Unit = {}) {
        adManager.checkAndShowInterstitial(activity, onComplete)
    }

    fun playNextLevel() {
        soundManager.playTap()
        val currentIndex = CATEGORIES.indexOfFirst { it.id == _uiState.value.selectedCategory.id }
        val nextIndex = currentIndex + 1
        if (nextIndex in CATEGORIES.indices) {
            val nextCategory = CATEGORIES[nextIndex]
            _uiState.update { it.copy(selectedCategory = nextCategory) }
            startNewGame(_uiState.value.selectedMode, nextCategory)
        } else {
            // Restart current or back to categories
            _uiState.update { it.copy(currentScreen = Screen.CATEGORY_SELECT) }
        }
    }

    override fun onCleared() {
        super.onCleared()
        soundManager.release()
    }
}
