package com.example

import com.example.data.BONUS_WORDS
import com.example.data.CATEGORIES
import com.example.data.calculateGridSize
import com.example.data.generateWordGrid
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testCategoryCountAndOrdering() {
        assertEquals("Should have exactly 15 categories", 15, CATEGORIES.size)
        assertEquals("Level 0 should be Animals", "Animals", CATEGORIES[0].name)
        assertEquals("Level 1 should be Food", "Food", CATEGORIES[1].name)
        assertEquals("Level 2 should be Sports", "Sports", CATEGORIES[2].name)
        assertEquals("Level 3 should be Countries", "Countries", CATEGORIES[3].name)
        assertEquals("Level 4 should be Colors", "Colors", CATEGORIES[4].name)
        assertEquals("Level 14 should be Vegetables", "Vegetables", CATEGORIES[14].name)
    }

    @Test
    fun testGridSizeRules() {
        // >= 16 words -> 12x12
        val animalsWords = CATEGORIES[0].words
        assertTrue("Animals has 18 words", animalsWords.size >= 16)
        assertEquals(12, calculateGridSize(animalsWords))

        // 8 words with short length -> 8x8
        val shortWords = listOf("CAT", "DOG", "BAT", "BEE")
        assertEquals(8, calculateGridSize(shortWords))
    }

    @Test
    fun testWordGridGeneration() {
        val words = listOf("CAT", "DOG", "LION", "TIGER")
        val grid = generateWordGrid(words)
        assertNotNull(grid)
        assertTrue(grid.size >= 8)
        assertEquals(4, grid.placedWords.size)
    }

    @Test
    fun testBonusWordDictionary() {
        assertTrue("Bonus word dictionary contains common words", BONUS_WORDS.size >= 600)
        assertTrue(BONUS_WORDS.contains("THE"))
        assertTrue(BONUS_WORDS.contains("AND"))
        assertTrue(BONUS_WORDS.contains("RUN"))
    }
}
