package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CATEGORIES
import com.example.data.Category
import com.example.ui.theme.BG_LIGHT
import com.example.ui.theme.BRAND_BLUE
import com.example.ui.theme.FOUND_GRAY
import com.example.ui.theme.TEXT_DARK
import com.example.ui.theme.ThemeGoldCoin

@Composable
fun CategorySelectScreen(
    coins: Int,
    completedLevels: Set<String>,
    onSelectCategory: (Category) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BG_LIGHT)
            .padding(16.dp)
    ) {
        // Top Header Row
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
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
                    .testTag("category_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = TEXT_DARK
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "SELECT CATEGORY",
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp,
                    color = BRAND_BLUE
                )
                Text(
                    text = "15 Puzzles Available",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Gray
                )
            }

            // Coins pill
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                shadowElevation = 1.dp
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
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
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TEXT_DARK
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Scrollable Grid of 15 Categories
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(bottom = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxSize()
                .testTag("category_grid")
        ) {
            itemsIndexed(CATEGORIES) { index, category ->
                // Level 0 (Animals) is always unlocked.
                // Level i is unlocked if and only if CATEGORIES[i-1].id is in completedLevels.
                val isUnlocked = index == 0 || completedLevels.contains(CATEGORIES[index - 1].id)
                val isCompleted = completedLevels.contains(category.id)

                CategoryCard(
                    category = category,
                    index = index,
                    isUnlocked = isUnlocked,
                    isCompleted = isCompleted,
                    onClick = {
                        if (isUnlocked) {
                            onSelectCategory(category)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun CategoryCard(
    category: Category,
    index: Int,
    isUnlocked: Boolean,
    isCompleted: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isUnlocked) Color.White else Color(0xFFE8E8EC)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isUnlocked) 3.dp else 0.dp
        ),
        modifier = modifier
            .fillMaxWidth()
            .height(130.dp)
            .alpha(if (isUnlocked) 1f else 0.65f)
            .clickable(enabled = isUnlocked, onClick = onClick)
            .testTag("category_card_${category.id}")
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            // Status Badge top-right
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isCompleted) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Completed",
                        tint = Color(0xFF10B981),
                        modifier = Modifier.size(20.dp)
                    )
                } else if (!isUnlocked) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Locked",
                        tint = Color.Gray,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Main Info
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = category.emoji,
                    fontSize = 32.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = category.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isUnlocked) TEXT_DARK else FOUND_GRAY,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = if (isUnlocked) "${category.words.size} Words" else "Locked (Level ${index + 1})",
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isUnlocked) BRAND_BLUE else Color.Gray,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
