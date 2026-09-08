package com.example.data

import androidx.compose.ui.graphics.Color
import kotlin.random.Random

// 18 Word Highlight Colors (cycle through) at 35% opacity
val HIGHLIGHT_COLORS = listOf(
    Color(0xFFE53935), // Red
    Color(0xFF1E88E5), // Blue
    Color(0xFF43A047), // Green
    Color(0xFFFDD835), // Yellow
    Color(0xFFE91E63), // Pink
    Color(0xFFFB8C00), // Orange
    Color(0xFF8E24AA), // Purple
    Color(0xFF00897B), // Teal
    Color(0xFFF43F5E), // Rose
    Color(0xFF00ACC1), // Cyan
    Color(0xFFC0CA33), // Lime
    Color(0xFF7C3AED), // Violet
    Color(0xFFFFB300), // Amber
    Color(0xFF10B981), // Emerald
    Color(0xFFD946EF), // Fuchsia
    Color(0xFF0284C7), // Sky
    Color(0xFF7CB342), // Light-Green
    Color(0xFFFF7043)  // Light-Orange
)

// Light Theme Palette (defined in Color.kt)

enum class GameMode(val displayName: String, val description: String) {
    CLASSIC("Classic Mode", "Relaxed play without time limits. Timer counts UP from 0:00."),
    TIME("Time Mode", "2-minute countdown! Race against the clock with time bonus points.")
}

data class Category(
    val id: String,
    val name: String,
    val emoji: String,
    val words: List<String>
)

val CATEGORIES: List<Category> = listOf(
    Category(
        id = "animals",
        name = "Animals",
        emoji = "🐾",
        words = listOf("CAT", "DOG", "LION", "TIGER", "ELEPHANT", "GIRAFFE", "ZEBRA", "MONKEY", "PENGUIN", "DOLPHIN", "EAGLE", "WOLF", "BEAR", "FOX", "RABBIT", "SNAKE", "TURTLE", "HORSE")
    ),
    Category(
        id = "food",
        name = "Food",
        emoji = "🍕",
        words = listOf("PIZZA", "BURGER", "SUSHI", "PASTA", "SALAD", "RICE", "BREAD", "SOUP", "TACO", "WAFFLE", "PANCAKE", "NOODLE", "STEAK", "CHEESE", "COOKIE")
    ),
    Category(
        id = "sports",
        name = "Sports",
        emoji = "⚽",
        words = listOf("SOCCER", "TENNIS", "BASKETBALL", "BASEBALL", "SWIMMING", "CYCLING", "BOXING", "GOLF", "RUGBY", "CRICKET", "HOCKEY", "RUNNING", "SKIING", "SURFING")
    ),
    Category(
        id = "countries",
        name = "Countries",
        emoji = "🌍",
        words = listOf("FRANCE", "JAPAN", "BRAZIL", "CANADA", "INDIA", "CHINA", "SPAIN", "ITALY", "EGYPT", "MEXICO", "RUSSIA", "AUSTRALIA", "GERMANY", "NIGERIA", "THAILAND")
    ),
    Category(
        id = "colors",
        name = "Colors",
        emoji = "🎨",
        words = listOf("RED", "BLUE", "GREEN", "YELLOW", "PURPLE", "ORANGE", "PINK", "BLACK", "WHITE", "BROWN", "GRAY", "CYAN", "GOLD", "SILVER", "VIOLET")
    ),
    Category(
        id = "fruits",
        name = "Fruits",
        emoji = "🍎",
        words = listOf("APPLE", "MANGO", "BANANA", "GRAPE", "LEMON", "PEACH", "PLUM", "ORANGE", "CHERRY", "MELON", "PAPAYA", "GUAVA", "KIWI", "PEAR", "BERRY")
    ),
    Category(
        id = "space",
        name = "Space",
        emoji = "🚀",
        words = listOf("MOON", "STAR", "SUN", "PLANET", "COMET", "GALAXY", "NEBULA", "ASTEROID", "ORBIT", "SATURN", "JUPITER", "MARS", "VENUS", "MERCURY", "COSMOS")
    ),
    Category(
        id = "ocean_life",
        name = "Ocean Life",
        emoji = "🌊",
        words = listOf("SHARK", "WHALE", "OCTOPUS", "CRAB", "LOBSTER", "SHRIMP", "CORAL", "JELLYFISH", "SEAHORSE", "CLAM", "OTTER", "SEAL", "TUNA", "SQUID")
    ),
    Category(
        id = "music",
        name = "Music",
        emoji = "🎵",
        words = listOf("GUITAR", "PIANO", "DRUMS", "VIOLIN", "TRUMPET", "FLUTE", "BASS", "RHYTHM", "MELODY", "CHORD", "TEMPO", "JAZZ", "ROCK", "BLUES", "OPERA")
    ),
    Category(
        id = "movies",
        name = "Movies",
        emoji = "🎬",
        words = listOf("ACTION", "DRAMA", "COMEDY", "HORROR", "ROMANCE", "THRILLER", "FANTASY", "MYSTERY", "WESTERN", "SEQUEL", "TRAILER", "CINEMA", "ACTOR", "SCENE")
    ),
    Category(
        id = "nature",
        name = "Nature",
        emoji = "🌿",
        words = listOf("FOREST", "RIVER", "MOUNTAIN", "OCEAN", "DESERT", "JUNGLE", "VALLEY", "ISLAND", "VOLCANO", "GLACIER", "PRAIRIE", "CANYON", "MARSH", "TUNDRA")
    ),
    Category(
        id = "technology",
        name = "Technology",
        emoji = "💻",
        words = listOf("COMPUTER", "INTERNET", "ROBOT", "PHONE", "TABLET", "KEYBOARD", "MONITOR", "BATTERY", "CIRCUIT", "CAMERA", "LASER", "SERVER", "NETWORK", "SOFTWARE")
    ),
    Category(
        id = "jobs",
        name = "Jobs",
        emoji = "👔",
        words = listOf("DOCTOR", "TEACHER", "PILOT", "CHEF", "LAWYER", "NURSE", "ENGINEER", "ARTIST", "FARMER", "WRITER", "POLICE", "FIREFIGHTER", "ARCHITECT", "SCIENTIST")
    ),
    Category(
        id = "clothing",
        name = "Clothing",
        emoji = "👗",
        words = listOf("SHIRT", "PANTS", "DRESS", "JACKET", "SHOES", "SOCKS", "HAT", "SCARF", "GLOVES", "BELT", "BOOTS", "COAT", "SKIRT", "SWEATER", "SHORTS")
    ),
    Category(
        id = "vegetables",
        name = "Vegetables",
        emoji = "🥕",
        words = listOf("CARROT", "POTATO", "TOMATO", "ONION", "GARLIC", "PEPPER", "CORN", "BROCCOLI", "SPINACH", "CABBAGE", "CELERY", "RADISH", "PUMPKIN", "ZUCCHINI")
    )
)

// Cell coordinate in letter grid
data class Cell(val row: Int, val col: Int)

// Placed word representation
data class PlacedWord(
    val word: String,
    val cells: List<Cell>
)

// Found word with assigned highlight color
data class FoundWord(
    val word: String,
    val cells: List<Cell>,
    val colorIndex: Int
)

// Grid sizing calculation per requirements:
// ≥16 words OR longest word ≥10 chars → 12×12 grid
// ≥12 words OR longest word ≥7 chars → 10×10 grid
// Otherwise → 8×8 grid
fun calculateGridSize(words: List<String>): Int {
    val count = words.size
    val maxLen = words.maxOfOrNull { it.length } ?: 0
    return when {
        count >= 16 || maxLen >= 10 -> 12
        count >= 12 || maxLen >= 7 -> 10
        else -> 8
    }
}

// 8 Directions
val DIRECTIONS = listOf(
    0 to 1,    // Horizontal right
    0 to -1,   // Horizontal left
    1 to 0,    // Vertical down
    -1 to 0,   // Vertical up
    1 to 1,    // Diagonal down-right
    -1 to -1,  // Diagonal up-left
    1 to -1,   // Diagonal down-left
    -1 to 1    // Diagonal up-right
)

// Grid Generation result
data class WordGrid(
    val size: Int,
    val letters: Array<CharArray>,
    val placedWords: List<PlacedWord>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as WordGrid
        return size == other.size && letters.contentDeepEquals(other.letters) && placedWords == other.placedWords
    }

    override fun hashCode(): Int {
        var result = size
        result = 31 * result + letters.contentDeepHashCode()
        result = 31 * result + placedWords.hashCode()
        return result
    }
}

// Generator with retry logic
fun generateWordGrid(words: List<String>): WordGrid {
    val size = calculateGridSize(words)
    val sortedWords = words.sortedByDescending { it.length }

    // Try up to 50 attempts to fit all words
    for (attempt in 0 until 50) {
        val grid = Array(size) { CharArray(size) { ' ' } }
        val placed = mutableListOf<PlacedWord>()
        var allPlaced = true

        for (word in sortedWords) {
            val placement = tryPlaceWord(grid, size, word)
            if (placement != null) {
                placed.add(placement)
            } else {
                allPlaced = false
                break
            }
        }

        if (allPlaced) {
            // Fill empty cells with random uppercase English letters
            for (r in 0 until size) {
                for (c in 0 until size) {
                    if (grid[r][c] == ' ') {
                        grid[r][c] = ('A'.code + Random.nextInt(26)).toChar()
                    }
                }
            }
            return WordGrid(size, grid, placed)
        }
    }

    // Fallback: enlarge by 1 or 2 if still needed
    val fallbackSize = (size + 2).coerceAtMost(14)
    val fallbackGrid = Array(fallbackSize) { CharArray(fallbackSize) { ' ' } }
    val placedFallback = mutableListOf<PlacedWord>()
    for (word in sortedWords) {
        val placement = tryPlaceWord(fallbackGrid, fallbackSize, word)
        if (placement != null) {
            placedFallback.add(placement)
        }
    }
    for (r in 0 until fallbackSize) {
        for (c in 0 until fallbackSize) {
            if (fallbackGrid[r][c] == ' ') {
                fallbackGrid[r][c] = ('A'.code + Random.nextInt(26)).toChar()
            }
        }
    }
    return WordGrid(fallbackSize, fallbackGrid, placedFallback)
}

private fun tryPlaceWord(grid: Array<CharArray>, size: Int, word: String): PlacedWord? {
    val directions = DIRECTIONS.shuffled()
    val startRows = (0 until size).shuffled()
    val startCols = (0 until size).shuffled()

    for ((dr, dc) in directions) {
        for (r in startRows) {
            for (c in startCols) {
                val endR = r + dr * (word.length - 1)
                val endC = c + dc * (word.length - 1)

                if (endR in 0 until size && endC in 0 until size) {
                    var canFit = true
                    for (i in word.indices) {
                        val currR = r + dr * i
                        val currC = c + dc * i
                        val cellChar = grid[currR][currC]
                        if (cellChar != ' ' && cellChar != word[i]) {
                            canFit = false
                            break
                        }
                    }

                    if (canFit) {
                        val cells = mutableListOf<Cell>()
                        for (i in word.indices) {
                            val currR = r + dr * i
                            val currC = c + dc * i
                            grid[currR][currC] = word[i]
                            cells.add(Cell(currR, currC))
                        }
                        return PlacedWord(word, cells)
                    }
                }
            }
        }
    }
    return null
}
