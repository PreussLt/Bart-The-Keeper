package com.example.bartthekeeper.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bartthekeeper.data.model.IngredientCategory
import com.example.bartthekeeper.ui.theme.AddInsColor
import com.example.bartthekeeper.ui.theme.SaefteColor
import com.example.bartthekeeper.ui.theme.SirupColor

@Composable
fun CategoryBadge(
    category: IngredientCategory,
    modifier: Modifier = Modifier,
    showEmoji: Boolean = true
) {
    val (bgColor, textColor) = when (category) {
        IngredientCategory.SIRUP -> Pair(SirupColor.copy(alpha = 0.15f), SirupColor)
        IngredientCategory.SAEFTE -> Pair(SaefteColor.copy(alpha = 0.15f), SaefteColor)
        IngredientCategory.ADD_INS -> Pair(AddInsColor.copy(alpha = 0.15f), AddInsColor)
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(6.dp),
        color = bgColor,
        contentColor = textColor
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (showEmoji) {
                Text(text = category.iconEmoji, fontSize = 12.sp)
            }
            Text(
                text = category.displayName,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = textColor
            )
        }
    }
}
