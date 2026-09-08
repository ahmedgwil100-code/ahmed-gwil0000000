package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.screens.CategorySelectScreen
import com.example.ui.screens.GameScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ModeSelectScreen
import com.example.ui.screens.ResultsScreen
import com.example.ui.theme.BG_LIGHT
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.GameViewModel
import com.example.ui.viewmodel.Screen

class MainActivity : ComponentActivity() {

    private val viewModel: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = BG_LIGHT
                ) {
                    val state by viewModel.uiState.collectAsStateWithLifecycle()

                    // Back button handling
                    BackHandler(enabled = state.currentScreen != Screen.HOME) {
                        when (state.currentScreen) {
                            Screen.GAME -> {
                                viewModel.triggerInterstitialAd(this@MainActivity) {
                                    viewModel.navigateTo(Screen.CATEGORY_SELECT)
                                }
                            }
                            Screen.RESULTS -> viewModel.navigateTo(Screen.CATEGORY_SELECT)
                            Screen.MODE_SELECT -> viewModel.navigateTo(Screen.CATEGORY_SELECT)
                            Screen.CATEGORY_SELECT -> viewModel.navigateTo(Screen.HOME)
                            Screen.HOME -> Unit
                        }
                    }

                    when (state.currentScreen) {
                        Screen.HOME -> {
                            HomeScreen(
                                coins = state.coins,
                                isSoundEnabled = state.isSoundEnabled,
                                onPlayClick = {
                                    viewModel.navigateTo(Screen.CATEGORY_SELECT)
                                },
                                onCategoryClick = {
                                    viewModel.navigateTo(Screen.CATEGORY_SELECT)
                                },
                                onToggleSound = {
                                    viewModel.toggleSound()
                                },
                                totalScore = viewModel.prefs.getTotalScore(),
                                completedCount = state.completedCategoryIds.size
                            )
                        }

                        Screen.CATEGORY_SELECT -> {
                            CategorySelectScreen(
                                coins = state.coins,
                                completedLevels = state.completedCategoryIds,
                                onSelectCategory = { category ->
                                    viewModel.selectCategory(category)
                                },
                                onBack = {
                                    viewModel.navigateTo(Screen.HOME)
                                }
                            )
                        }

                        Screen.MODE_SELECT -> {
                            ModeSelectScreen(
                                category = state.selectedCategory,
                                onSelectMode = { mode ->
                                    viewModel.selectMode(mode)
                                },
                                onBack = {
                                    viewModel.navigateTo(Screen.CATEGORY_SELECT)
                                }
                            )
                        }

                        Screen.GAME -> {
                            GameScreen(
                                state = state,
                                onBack = {
                                    viewModel.triggerInterstitialAd(this@MainActivity) {
                                        viewModel.navigateTo(Screen.CATEGORY_SELECT)
                                    }
                                },
                                onToggleSound = {
                                    viewModel.toggleSound()
                                },
                                onDragStart = { cell ->
                                    viewModel.onDragStart(cell)
                                },
                                onDragMove = { cell ->
                                    viewModel.onDragMove(cell)
                                },
                                onDragEnd = { activity ->
                                    viewModel.onDragEnd(activity)
                                },
                                onUseHint = {
                                    viewModel.useHint()
                                },
                                onWatchAdForHints = { activity ->
                                    viewModel.watchRewardedAdForHints(activity)
                                },
                                onDismissRewardDialog = {
                                    viewModel.dismissRewardDialog()
                                }
                            )
                        }

                        Screen.RESULTS -> {
                            ResultsScreen(
                                state = state,
                                onPlayAgain = {
                                    viewModel.startNewGame()
                                },
                                onNextLevel = {
                                    viewModel.playNextLevel()
                                },
                                onHome = {
                                    viewModel.triggerInterstitialAd(this@MainActivity) {
                                        viewModel.navigateTo(Screen.CATEGORY_SELECT)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.soundManager.release()
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = "Word Pro: $name", modifier = modifier)
}
