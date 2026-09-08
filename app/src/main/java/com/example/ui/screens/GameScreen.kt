package com.example.ui.screens

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Cell
import com.example.ui.components.FloatingNoticeBanner
import com.example.ui.components.GameHeader
import com.example.ui.components.GameStatusBar
import com.example.ui.components.LetterGridView
import com.example.ui.components.WordListPanel
import com.example.ui.theme.BG_LIGHT
import com.example.ui.theme.BRAND_BLUE
import com.example.ui.theme.BRAND_ORANGE
import com.example.ui.theme.BRAND_PINK
import com.example.ui.theme.TEXT_DARK
import com.example.ui.viewmodel.GameUiState

@Composable
fun GameScreen(
    state: GameUiState,
    onBack: () -> Unit,
    onToggleSound: () -> Unit,
    onDragStart: (Cell) -> Unit,
    onDragMove: (Cell) -> Unit,
    onDragEnd: (Activity?) -> Unit,
    onUseHint: () -> Unit,
    onWatchAdForHints: (Activity?) -> Unit,
    onDismissRewardDialog: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activity = context as? Activity
    var showQuitConfirmDialog by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BG_LIGHT)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            // Header Bar: Category + Back + Coins + Sound
            GameHeader(
                categoryEmoji = state.selectedCategory.emoji,
                categoryName = state.selectedCategory.name,
                coins = state.coins,
                isSoundEnabled = state.isSoundEnabled,
                onBack = { showQuitConfirmDialog = true },
                onToggleSound = onToggleSound
            )

            // Game Status Bar: Score + Mode/Timer + Hint Button
            GameStatusBar(
                score = state.score,
                mode = state.selectedMode,
                timerSeconds = state.timerSeconds,
                hintsRemaining = state.hintsRemaining,
                onUseHint = onUseHint,
                onWatchAdForHints = { onWatchAdForHints(activity) }
            )

            // Active Word Selection Preview Pill
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(38.dp)
                    .padding(horizontal = 16.dp)
            ) {
                if (state.activeWordPreview.isNotEmpty()) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = BRAND_BLUE,
                        shadowElevation = 2.dp,
                        modifier = Modifier.testTag("active_word_preview")
                    ) {
                        Text(
                            text = state.activeWordPreview.chunked(1).joinToString(" · "),
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 15.sp,
                            letterSpacing = 1.2.sp,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            // Letter Grid
            val grid = state.grid
            if (grid != null) {
                LetterGridView(
                    grid = grid,
                    foundWords = state.foundWords,
                    activeSelection = state.activeSelection,
                    hintedCells = state.hintedCells,
                    onDragStart = onDragStart,
                    onDragMove = onDragMove,
                    onDragEnd = { onDragEnd(activity) },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Word List Panel (Found Words Crossed out and grayed)
            WordListPanel(
                words = state.selectedCategory.words,
                foundWords = state.foundWords,
                modifier = Modifier.weight(0.6f)
            )

            Spacer(modifier = Modifier.height(12.dp))
        }

        // Floating celebratory notice banner (Bonus Word found, Hint earned, etc.)
        FloatingNoticeBanner(
            notice = state.floatingNotice,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 90.dp)
        )

        // Quit Game Confirmation Dialog
        if (showQuitConfirmDialog) {
            AlertDialog(
                onDismissRequest = { showQuitConfirmDialog = false },
                title = { Text(text = "Leave Puzzle?", fontWeight = FontWeight.Bold) },
                text = { Text("Your progress in this puzzle will be lost.") },
                confirmButton = {
                    Button(
                        onClick = {
                            showQuitConfirmDialog = false
                            onBack()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                    ) {
                        Text("Leave", color = Color.White)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showQuitConfirmDialog = false }) {
                        Text("Continue Playing")
                    }
                }
            )
        }

        // Rewarded Ad Simulation / Loading Dialog (Ensures offline resilience)
        if (state.isWatchingRewardAd && state.rewardAdDialogMessage != null) {
            AlertDialog(
                onDismissRequest = onDismissRewardDialog,
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.PlayCircle,
                            contentDescription = null,
                            tint = BRAND_ORANGE,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Sponsored Ad", fontWeight = FontWeight.Bold)
                    }
                },
                text = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
                    ) {
                        CircularProgressIndicator(
                            color = BRAND_ORANGE,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = state.rewardAdDialogMessage,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TEXT_DARK
                        )
                    }
                },
                confirmButton = {}
            )
        }
    }
}
