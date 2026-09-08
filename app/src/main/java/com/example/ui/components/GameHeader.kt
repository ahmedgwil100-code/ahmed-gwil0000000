package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeMute
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.GameMode
import com.example.ui.theme.BRAND_BLUE
import com.example.ui.theme.BRAND_ORANGE
import com.example.ui.theme.BRAND_PINK
import com.example.ui.theme.TEXT_DARK
import com.example.ui.theme.ThemeGoldCoin
import com.example.ui.theme.ThemeTimerRedBg
import com.example.ui.theme.ThemeTimerRedText
import com.example.ui.viewmodel.FloatingNotice

@Composable
fun GameHeader(
    categoryEmoji: String,
    categoryName: String,
    coins: Int,
    isSoundEnabled: Boolean,
    onBack: () -> Unit,
    onToggleSound: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.White, CircleShape)
                    .testTag("back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = TEXT_DARK
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column {
                Text(
                    text = "$categoryEmoji $categoryName",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp,
                    color = TEXT_DARK
                )
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Coins pill
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                shadowElevation = 1.dp
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                        .testTag("coin_balance_display")
                ) {
                    Icon(
                        imageVector = Icons.Default.MonetizationOn,
                        contentDescription = "Coins",
                        tint = ThemeGoldCoin,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "$coins",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = TEXT_DARK
                    )
                }
            }

            // Sound Toggle
            IconButton(
                onClick = onToggleSound,
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.White, CircleShape)
                    .testTag("sound_toggle")
            ) {
                Icon(
                    imageVector = if (isSoundEnabled) Icons.AutoMirrored.Filled.VolumeUp else Icons.AutoMirrored.Filled.VolumeMute,
                    contentDescription = "Toggle Sound",
                    tint = if (isSoundEnabled) BRAND_BLUE else Color.Gray
                )
            }
        }
    }
}

@Composable
fun GameStatusBar(
    score: Int,
    mode: GameMode,
    timerSeconds: Int,
    hintsRemaining: Int,
    onUseHint: () -> Unit,
    onWatchAdForHints: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isTimerLow = mode == GameMode.TIME && timerSeconds < 30
    val minutes = timerSeconds / 60
    val seconds = timerSeconds % 60
    val formattedTime = String.format("%02d:%02d", minutes, seconds)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Score Pill
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color.White,
            shadowElevation = 1.dp,
            modifier = Modifier.testTag("score_counter")
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "SCORE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "$score",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = BRAND_BLUE
                )
            }
        }

        // Timer Pill (Mode Indicator)
        val timerBg = if (isTimerLow) ThemeTimerRedBg else Color.White
        val timerText = if (isTimerLow) ThemeTimerRedText else TEXT_DARK

        Surface(
            shape = RoundedCornerShape(12.dp),
            color = timerBg,
            shadowElevation = 1.dp,
            modifier = Modifier.testTag("timer_display")
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Timer,
                    contentDescription = "Timer",
                    tint = timerText,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = formattedTime,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = timerText
                )
            }
        }

        // Hint Button with Red Badge
        if (hintsRemaining > 0) {
            BadgedBox(
                badge = {
                    Badge(
                        containerColor = Color(0xFFE53935),
                        contentColor = Color.White,
                        modifier = Modifier
                            .offset(x = (-4).dp, y = 4.dp)
                            .testTag("hint_badge")
                    ) {
                        Text(text = "$hintsRemaining", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White,
                    shadowElevation = 1.dp,
                    modifier = Modifier
                        .clickable(onClick = onUseHint)
                        .testTag("hint_button")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lightbulb,
                            contentDescription = "Hint",
                            tint = Color(0xFFF59E0B),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "HINT",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TEXT_DARK
                        )
                    }
                }
            }
        } else {
            // Watch ad for extra hints button
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = BRAND_ORANGE,
                shadowElevation = 1.dp,
                modifier = Modifier
                    .clickable(onClick = onWatchAdForHints)
                    .testTag("watch_ad_hint_button")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Lightbulb,
                        contentDescription = "Get Hints",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "+2 Hints (Ad)",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun FloatingNoticeBanner(
    notice: FloatingNotice?,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = notice != null,
        enter = fadeIn() + slideInVertically(initialOffsetY = { -it }),
        exit = fadeOut() + slideOutVertically(targetOffsetY = { -it }),
        modifier = modifier
    ) {
        if (notice != null) {
            val bgGradient = if (notice.isBonus) {
                Brush.horizontalGradient(listOf(Color(0xFFF59E0B), Color(0xFFFF7043)))
            } else {
                Brush.horizontalGradient(listOf(BRAND_BLUE, BRAND_PINK))
            }

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .padding(horizontal = 32.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(bgGradient)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = notice.message,
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 14.sp
                )
            }
        }
    }
}
