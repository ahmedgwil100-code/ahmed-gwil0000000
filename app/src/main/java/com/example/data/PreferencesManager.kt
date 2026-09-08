package com.example.data

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("word_pro_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_COINS = "user_coins"
        private const val KEY_COMPLETED_LEVELS = "completed_levels"
        private const val KEY_SOUND_ENABLED = "sound_enabled"
        private const val KEY_TOTAL_SCORE = "total_score"
        private const val KEY_PUZZLES_PLAYED = "puzzles_played"
        private const val DEFAULT_STARTING_COINS = 300
    }

    fun getCoins(): Int {
        return prefs.getInt(KEY_COINS, DEFAULT_STARTING_COINS)
    }

    fun addCoins(amount: Int): Int {
        val current = getCoins()
        val updated = current + amount
        prefs.edit().putInt(KEY_COINS, updated).apply()
        return updated
    }

    fun spendCoins(amount: Int): Boolean {
        val current = getCoins()
        if (current >= amount) {
            prefs.edit().putInt(KEY_COINS, current - amount).apply()
            return true
        }
        return false
    }

    fun getCompletedLevels(): Set<String> {
        return prefs.getStringSet(KEY_COMPLETED_LEVELS, emptySet()) ?: emptySet()
    }

    fun markLevelCompleted(categoryId: String) {
        val current = getCompletedLevels().toMutableSet()
        current.add(categoryId)
        prefs.edit().putStringSet(KEY_COMPLETED_LEVELS, current).apply()
    }

    fun isLevelUnlocked(index: Int): Boolean {
        if (index <= 0) return true
        val prevCategoryId = CATEGORIES.getOrNull(index - 1)?.id ?: return false
        return getCompletedLevels().contains(prevCategoryId)
    }

    fun isSoundEnabled(): Boolean {
        return prefs.getBoolean(KEY_SOUND_ENABLED, true)
    }

    fun setSoundEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_SOUND_ENABLED, enabled).apply()
    }

    fun getTotalScore(): Int {
        return prefs.getInt(KEY_TOTAL_SCORE, 0)
    }

    fun addScore(points: Int) {
        prefs.edit().putInt(KEY_TOTAL_SCORE, getTotalScore() + points).apply()
    }

    fun incrementPuzzlesPlayed() {
        prefs.edit().putInt(KEY_PUZZLES_PLAYED, prefs.getInt(KEY_PUZZLES_PLAYED, 0) + 1).apply()
    }

    fun getPuzzlesPlayed(): Int {
        return prefs.getInt(KEY_PUZZLES_PLAYED, 0)
    }
}
