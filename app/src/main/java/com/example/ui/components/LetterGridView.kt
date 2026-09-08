package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Cell
import com.example.data.FoundWord
import com.example.data.HIGHLIGHT_COLORS
import com.example.data.WordGrid
import com.example.ui.theme.BRAND_BLUE
import com.example.ui.theme.TEXT_DARK

@Composable
fun LetterGridView(
    grid: WordGrid,
    foundWords: List<FoundWord>,
    activeSelection: List<Cell>,
    hintedCells: Set<Cell>?,
    onDragStart: (Cell) -> Unit,
    onDragMove: (Cell) -> Unit,
    onDragEnd: () -> Unit,
    modifier: Modifier = Modifier
) {
    val size = grid.size

    // Pre-calculate cell to found color mapping
    val cellColorMap = remember(foundWords) {
        val map = mutableMapOf<Cell, Color>()
        for (found in foundWords) {
            val color = HIGHLIGHT_COLORS[found.colorIndex % HIGHLIGHT_COLORS.size]
            for (cell in found.cells) {
                map[cell] = color
            }
        }
        map
    }

    val activeCellSet = remember(activeSelection) { activeSelection.toSet() }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .testTag("letter_grid_card")
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .aspectRatio(1f)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFF7F7F9))
                .testTag("letter_grid")
        ) {
            val gridWidthPx = constraints.maxWidth.toFloat()
            val gridHeightPx = constraints.maxHeight.toFloat()
            val cellSizePx = gridWidthPx / size

            // Detect touch gestures across the grid
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(grid, size) {
                        detectDragGestures(
                            onDragStart = { offset ->
                                val col = (offset.x / cellSizePx).toInt().coerceIn(0, size - 1)
                                val row = (offset.y / cellSizePx).toInt().coerceIn(0, size - 1)
                                onDragStart(Cell(row, col))
                            },
                            onDrag = { change, _ ->
                                change.consume()
                                val col = (change.position.x / cellSizePx).toInt().coerceIn(0, size - 1)
                                val row = (change.position.y / cellSizePx).toInt().coerceIn(0, size - 1)
                                onDragMove(Cell(row, col))
                            },
                            onDragEnd = { onDragEnd() },
                            onDragCancel = { onDragEnd() }
                        )
                    }
            ) {
                // Background Found Lines Overlay Canvas
                Canvas(modifier = Modifier.fillMaxSize()) {
                    for (found in foundWords) {
                        if (found.cells.size >= 2) {
                            val color = HIGHLIGHT_COLORS[found.colorIndex % HIGHLIGHT_COLORS.size]
                            val startCell = found.cells.first()
                            val endCell = found.cells.last()

                            val startOffset = Offset(
                                (startCell.col + 0.5f) * cellSizePx,
                                (startCell.row + 0.5f) * cellSizePx
                            )
                            val endOffset = Offset(
                                (endCell.col + 0.5f) * cellSizePx,
                                (endCell.row + 0.5f) * cellSizePx
                            )

                            drawLine(
                                color = color.copy(alpha = 0.40f),
                                start = startOffset,
                                end = endOffset,
                                strokeWidth = cellSizePx * 0.72f,
                                cap = StrokeCap.Round
                            )
                        }
                    }

                    // Active dragging selection line
                    if (activeSelection.size >= 2) {
                        val start = activeSelection.first()
                        val end = activeSelection.last()
                        val startOffset = Offset(
                            (start.col + 0.5f) * cellSizePx,
                            (start.row + 0.5f) * cellSizePx
                        )
                        val endOffset = Offset(
                            (end.col + 0.5f) * cellSizePx,
                            (end.row + 0.5f) * cellSizePx
                        )

                        drawLine(
                            color = BRAND_BLUE.copy(alpha = 0.45f),
                            start = startOffset,
                            end = endOffset,
                            strokeWidth = cellSizePx * 0.76f,
                            cap = StrokeCap.Round
                        )
                    }
                }

                // Grid Cells Content (Letters)
                Column(modifier = Modifier.fillMaxSize()) {
                    for (row in 0 until size) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        ) {
                            for (col in 0 until size) {
                                val cell = Cell(row, col)
                                val letter = grid.letters[row][col]

                                val isHinted = hintedCells?.contains(cell) == true
                                val isSelected = activeCellSet.contains(cell)
                                val foundColor = cellColorMap[cell]

                                val targetBgColor = when {
                                    isHinted -> Color(0xFFFFEB3B)
                                    isSelected && activeSelection.size == 1 -> BRAND_BLUE.copy(alpha = 0.40f)
                                    foundColor != null -> foundColor.copy(alpha = 0.35f)
                                    else -> Color.Transparent
                                }

                                val animatedBgColor by animateColorAsState(
                                    targetValue = targetBgColor,
                                    animationSpec = tween(durationMillis = 150),
                                    label = "cell_color"
                                )

                                val fontSize = when {
                                    size <= 8 -> 20.sp
                                    size <= 10 -> 17.sp
                                    else -> 14.5.sp
                                }

                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxSize()
                                        .padding(1.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(animatedBgColor)
                                        .then(
                                            if (isHinted) Modifier.border(
                                                2.dp,
                                                Color(0xFFFFB300),
                                                RoundedCornerShape(6.dp)
                                            ) else Modifier
                                        )
                                ) {
                                    Text(
                                        text = letter.toString(),
                                        fontSize = fontSize,
                                        fontWeight = if (foundColor != null || isSelected || isHinted) FontWeight.ExtraBold else FontWeight.Bold,
                                        color = if (isHinted) Color(0xFFE65100) else TEXT_DARK
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
