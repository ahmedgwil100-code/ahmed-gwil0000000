package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.FoundWord
import com.example.data.HIGHLIGHT_COLORS
import com.example.ui.theme.FOUND_GRAY
import com.example.ui.theme.TEXT_DARK

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun WordListPanel(
    words: List<String>,
    foundWords: List<FoundWord>,
    modifier: Modifier = Modifier
) {
    val foundMap = foundWords.associateBy { it.word.uppercase() }

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 2.dp,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .testTag("word_list_panel")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Words to Find",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = TEXT_DARK
                )
                Text(
                    text = "${foundWords.size} / ${words.size}",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                for (word in words) {
                    val upper = word.uppercase()
                    val isFound = foundMap.containsKey(upper)
                    val colorIndex = foundMap[upper]?.colorIndex ?: 0
                    val wordColor = if (isFound) {
                        HIGHLIGHT_COLORS[colorIndex % HIGHLIGHT_COLORS.size]
                    } else Color.Transparent

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (isFound) wordColor.copy(alpha = 0.18f) else Color(0xFFF3F4F6)
                            )
                            .then(
                                if (isFound) Modifier.border(
                                    1.dp,
                                    wordColor.copy(alpha = 0.5f),
                                    RoundedCornerShape(8.dp)
                                ) else Modifier
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .testTag("word_chip_$word")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            if (isFound) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .size(14.dp)
                                        .background(wordColor, CircleShape)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Found",
                                        tint = Color.White,
                                        modifier = Modifier.size(10.dp)
                                    )
                                }
                            }
                            Text(
                                text = word,
                                fontSize = 12.5.sp,
                                fontWeight = if (isFound) FontWeight.SemiBold else FontWeight.Bold,
                                color = if (isFound) FOUND_GRAY else TEXT_DARK,
                                textDecoration = if (isFound) TextDecoration.LineThrough else TextDecoration.None
                            )
                        }
                    }
                }
            }
        }
    }
}
