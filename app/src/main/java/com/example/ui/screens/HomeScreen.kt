package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.automirrored.filled.VolumeMute
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.BG_LIGHT
import com.example.ui.theme.BRAND_BLUE
import com.example.ui.theme.BRAND_ORANGE
import com.example.ui.theme.BRAND_PINK
import com.example.ui.theme.TEXT_DARK
import com.example.ui.theme.ThemeGoldCoin

@Composable
fun HomeScreen(
    coins: Int,
    isSoundEnabled: Boolean,
    onPlayClick: () -> Unit,
    onCategoryClick: () -> Unit,
    onToggleSound: () -> Unit,
    totalScore: Int,
    completedCount: Int,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "play_btn_scale"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BG_LIGHT)
            .padding(16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxSize()
        ) {
            // Top Bar: Coins + Sound
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                // Coins display
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White,
                    shadowElevation = 2.dp,
                    modifier = Modifier.testTag("home_coins_pill")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MonetizationOn,
                            contentDescription = "Coins",
                            tint = ThemeGoldCoin,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "$coins",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TEXT_DARK
                        )
                    }
                }

                // Sound toggle
                IconButton(
                    onClick = onToggleSound,
                    modifier = Modifier
                        .size(44.dp)
                        .background(Color.White, CircleShape)
                        .testTag("home_sound_toggle")
                ) {
                    Icon(
                        imageVector = if (isSoundEnabled) Icons.AutoMirrored.Filled.VolumeUp else Icons.AutoMirrored.Filled.VolumeMute,
                        contentDescription = "Toggle Sound",
                        tint = if (isSoundEnabled) BRAND_BLUE else Color.Gray
                    )
                }
            }

            // Hero Section: App Logo & Animated Title
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(vertical = 12.dp)
            ) {
                // App Logo Image
                Card(
                    shape = RoundedCornerShape(28.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    modifier = Modifier
                        .size(130.dp)
                        .padding(bottom = 16.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_word_pro_foreground),
                        contentDescription = "Word Pro Icon",
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Text(
                    text = "WORD PRO",
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.5.sp,
                    color = BRAND_BLUE
                )

                Text(
                    text = "THE ULTIMATE WORD SEARCH",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                    color = BRAND_ORANGE,
                    modifier = Modifier.padding(top = 4.dp)
                )

                // Letter Tiles Decoration W-O-R-D
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(top = 18.dp)
                ) {
                    listOf('W' to BRAND_BLUE, 'O' to BRAND_PINK, 'R' to BRAND_ORANGE, 'D' to Color(0xFF10B981)).forEach { (char, color) ->
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(color)
                        ) {
                            Text(
                                text = char.toString(),
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 20.sp
                            )
                        }
                    }
                }
            }

            // Primary Play Actions & Stats
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            ) {
                // Animated Play Button
                Button(
                    onClick = onPlayClick,
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BRAND_BLUE),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .scale(scale)
                        .testTag("animated_play_button")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Play",
                            modifier = Modifier.size(32.dp),
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "PLAY PUZZLE",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Categories Button
                Button(
                    onClick = onCategoryClick,
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    border = ButtonDefaults.outlinedButtonBorder,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("categories_button")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.GridOn,
                            contentDescription = "Categories",
                            tint = TEXT_DARK,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "15 Categories ($completedCount/15 Unlocked)",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TEXT_DARK
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Stats Row
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    shadowElevation = 1.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 12.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "TOTAL SCORE", fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                            Text(text = "$totalScore", fontSize = 18.sp, color = BRAND_BLUE, fontWeight = FontWeight.ExtraBold)
                        }
                        Box(modifier = Modifier.width(1.dp).height(28.dp).background(Color(0xFFE5E7EB)))
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "CATEGORIES", fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                            Text(text = "$completedCount / 15", fontSize = 18.sp, color = BRAND_ORANGE, fontWeight = FontWeight.ExtraBold)
                        }
                    }
                }
            }
        }
    }
}
