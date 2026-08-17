package com.example.bartthekeeper.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.StarHalf
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bartthekeeper.ui.theme.RatingGold

@Composable
fun CompactRatingBadge(
    rating: Int,
    modifier: Modifier = Modifier
) {
    val clamped = rating.coerceIn(1, 10)
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = RatingGold.copy(alpha = 0.15f),
        contentColor = RatingGold
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = RatingGold
            )
            Text(
                text = "$clamped/10",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = if (MaterialTheme.colorScheme.background == Color.White) Color(0xFFB45309) else RatingGold
            )
        }
    }
}

@Composable
fun InteractiveRatingPicker(
    rating: Int,
    onRatingSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val clamped = rating.coerceIn(1, 10)
    val ratingDescription = when (clamped) {
        10 -> "🏆 Meisterwerk (10/10)"
        9 -> "🌟 Hervorragend (9/10)"
        8 -> "✨ Sehr lecker (8/10)"
        7 -> "👍 Gut & erfrischend (7/10)"
        6 -> "👌 Solide (6/10)"
        5 -> "⚖️ Durchschnittlich (5/10)"
        4 -> "😕 Geht so (4/10)"
        3 -> "👎 Nicht mein Fall (3/10)"
        else -> "❌ No-Go (1-2/10)"
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Bewertung (1 - 10):",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = ratingDescription,
                style = MaterialTheme.typography.labelLarge,
                color = RatingGold,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Numbered selector chips 1 to 10
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            for (i in 1..10) {
                val isSelected = i == clamped
                val isUpTo = i <= clamped
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(
                            if (isSelected) RatingGold
                            else if (isUpTo) RatingGold.copy(alpha = 0.25f)
                            else MaterialTheme.colorScheme.surfaceVariant
                        )
                        .clickable { onRatingSelected(i) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$i",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                            fontSize = 13.sp
                        ),
                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}
