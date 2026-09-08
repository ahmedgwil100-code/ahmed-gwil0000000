package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CATEGORIES
import com.example.data.GameMode
import com.example.ui.theme.BG_LIGHT
import com.example.ui.theme.BRAND_BLUE
import com.example.ui.theme.BRAND_ORANGE
import com.example.ui.theme.BRAND_PINK
import com.example.ui.theme.TEXT_DARK
import com.example.ui.theme.ThemeGoldCoin
import com.example.ui.viewmodel.GameUiState

@Composable
fun ResultsScreen(
    state: GameUiState,
    onPlayAgain: () -> Unit,
    onNextLevel: () -> Unit,
    onHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isWin = state.isLevelCompleted
    val currentIndex = CATEGORIES.indexOfFirst { it.id == state.selectedCategory.id }
    val hasNextLevel = currentIndex >= 0 && currentIndex < CATEGORIES.size - 1

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val trophyScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "trophy_scale"
    )

    val timeFormatted = if (state.selectedMode == GameMode.CLASSIC) {
        val m = state.timerSeconds / 60
        val s = state.timerSeconds % 60
        String.format("%02d:%02d", m, s)
    } else {
        val elapsed = (120 - state.timerSeconds).coerceAtLeast(0)
        val m = elapsed / 60
        val s = elapsed % 60
        String.format("%02d:%02d", m, s)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BG_LIGHT)
            .padding(20.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxSize()
        ) {
            // Header Trophy & Title
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 24.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(90.dp)
                        .scale(if (isWin) trophyScale else 1f)
                        .background(
                            if (isWin) Brush.radialGradient(listOf(Color(0xFFFFD54F), Color(0xFFF59E0B)))
                            else Brush.radialGradient(listOf(Color(0xFFEF4444), Color(0xFFB91C1C))),
                            CircleShape
                        )
                ) {
                    Icon(
                        imageVector = if (isWin) Icons.Default.EmojiEvents else Icons.Default.Timer,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(52.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = if (isWin) "PUZZLE COMPLETED!" else "TIME'S UP!",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Black,
                    color = if (isWin) BRAND_BLUE else Color(0xFFEF4444)
                )

                Text(
                    text = "${state.selectedCategory.emoji} ${state.selectedCategory.name} • ${state.selectedMode.displayName}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // Summary Card
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
                    .testTag("results_summary_card")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    // Total Score Header
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "TOTAL SCORE",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TEXT_DARK
                        )
                        Text(
                            text = "${state.score} pts",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            color = BRAND_BLUE
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color(0xFFF0F0F3)))
                    Spacer(modifier = Modifier.height(14.dp))

                    // Breakdown lines
                    ScoreRow(
                        label = "Words Found (${state.foundWords.size}/${state.selectedCategory.words.size})",
                        value = "+${state.foundWords.size * 10} pts"
                    )

                    if (state.foundBonusWords.isNotEmpty()) {
                        ScoreRow(
                            label = "Bonus Words (${state.foundBonusWords.size})",
                            value = "+${state.foundBonusWords.size * 5} pts",
                            valueColor = BRAND_ORANGE
                        )
                    }

                    if (state.selectedMode == GameMode.TIME && isWin) {
                        ScoreRow(
                            label = "Time Bonus (${state.timerSeconds}s left x 2)",
                            value = "+${state.timerSeconds * 2} pts",
                            valueColor = BRAND_PINK
                        )
                    }

                    ScoreRow(
                        label = "Time Taken",
                        value = timeFormatted,
                        valueColor = TEXT_DARK
                    )

                    if (isWin) {
                        Spacer(modifier = Modifier.height(12.dp))
                        // Coins Earned Banner
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = Color(0xFFFEF3C7),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier.padding(vertical = 10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MonetizationOn,
                                    contentDescription = "Coins",
                                    tint = ThemeGoldCoin,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "+50 COINS EARNED!",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 15.sp,
                                    color = Color(0xFFB45309)
                                )
                            }
                        }
                    }
                }
            }

            // Buttons: Play Again / Next Level / Home
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                if (isWin && hasNextLevel) {
                    Button(
                        onClick = onNextLevel,
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BRAND_BLUE),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .testTag("next_level_button")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "NEXT LEVEL",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Default.NavigateNext,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                Button(
                    onClick = onPlayAgain,
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (!isWin || !hasNextLevel) BRAND_BLUE else Color.White
                    ),
                    border = if (isWin && hasNextLevel) ButtonDefaults.outlinedButtonBorder else null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("play_again_button")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Replay,
                            contentDescription = null,
                            tint = if (!isWin || !hasNextLevel) Color.White else TEXT_DARK,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "PLAY AGAIN",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (!isWin || !hasNextLevel) Color.White else TEXT_DARK
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedButton(
                    onClick = onHome,
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("home_button")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.GridOn,
                            contentDescription = null,
                            tint = TEXT_DARK,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "CATEGORIES",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TEXT_DARK
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ScoreRow(
    label: String,
    value: String,
    valueColor: Color = BRAND_BLUE
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color.Gray,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = value,
            fontSize = 15.sp,
            fontWeight = FontWeight.ExtraBold,
            color = valueColor
        )
    }
}
