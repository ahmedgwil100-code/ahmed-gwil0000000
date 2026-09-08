package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AllInclusive
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Category
import com.example.data.GameMode
import com.example.ui.theme.BG_LIGHT
import com.example.ui.theme.BRAND_BLUE
import com.example.ui.theme.BRAND_ORANGE
import com.example.ui.theme.BRAND_PINK
import com.example.ui.theme.TEXT_DARK

@Composable
fun ModeSelectScreen(
    category: Category,
    onSelectMode: (GameMode) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BG_LIGHT)
            .padding(16.dp)
    ) {
        // Top Bar
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(42.dp)
                    .background(Color.White, CircleShape)
                    .testTag("mode_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = TEXT_DARK
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = "CHOOSE GAME MODE",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = BRAND_BLUE
                )
                Text(
                    text = "${category.emoji} ${category.name} (${category.words.size} Words)",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Mode 1: Classic Mode
        ModeOptionCard(
            title = "Classic Mode",
            tagline = "Relaxed & Untimed",
            description = "No time limit. Relax and search words at your own pace. Timer counts UP from 0:00.",
            icon = Icons.Default.AllInclusive,
            accentColor = BRAND_BLUE,
            badgeText = "CASUAL",
            onClick = { onSelectMode(GameMode.CLASSIC) },
            testTag = "classic_mode_card"
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Mode 2: Time Mode
        ModeOptionCard(
            title = "Time Mode",
            tagline = "2-Minute Countdown",
            description = "Race against the clock! 2:00 countdown timer with thrilling +timeLeft x 2 score bonus.",
            icon = Icons.Default.Timer,
            accentColor = BRAND_PINK,
            badgeText = "SPEED & BONUS",
            onClick = { onSelectMode(GameMode.TIME) },
            testTag = "time_mode_card"
        )

        Spacer(modifier = Modifier.weight(1f))

        // Rules Reminder Card
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            shadowElevation = 1.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "💡 HOW TO PLAY",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 13.sp,
                    color = BRAND_ORANGE
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "• Drag across connected letters in any of the 8 directions.\n• Find all category words (+10 pts each).\n• Discover hidden English bonus words (+5 pts each)!\n• Complete the level to earn +50 Coins and unlock the next category.",
                    fontSize = 12.5.sp,
                    lineHeight = 18.sp,
                    color = TEXT_DARK
                )
            }
        }
    }
}

@Composable
fun ModeOptionCard(
    title: String,
    tagline: String,
    description: String,
    icon: ImageVector,
    accentColor: Color,
    badgeText: String,
    onClick: () -> Unit,
    testTag: String,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag(testTag)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(accentColor.copy(alpha = 0.15f))
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = title,
                            tint = accentColor,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = title,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = TEXT_DARK
                        )
                        Text(
                            text = tagline,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = accentColor
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(accentColor.copy(alpha = 0.12f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = badgeText,
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = accentColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = description,
                fontSize = 13.sp,
                color = Color.Gray,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "SELECT MODE",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = accentColor
                )
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
