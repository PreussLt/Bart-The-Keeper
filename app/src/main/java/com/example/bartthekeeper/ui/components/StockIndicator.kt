package com.example.bartthekeeper.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.example.bartthekeeper.data.model.IngredientStockStatus
import com.example.bartthekeeper.data.model.UnitConverter
import com.example.bartthekeeper.ui.theme.InStockGreen
import com.example.bartthekeeper.ui.theme.LowStockOrange
import com.example.bartthekeeper.ui.theme.OutOfStockRed

@Composable
fun IngredientStockRow(
    status: IngredientStockStatus,
    modifier: Modifier = Modifier
) {
    val ingredient = status.ingredient
    val formattedRequired = "${UnitConverter.formatAmount(status.requiredAmount)} ${ingredient.unit}"

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left: Name and Required Amount
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = ingredient.name,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Benötigt: $formattedRequired",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Right: Vorrat Status Badge
            if (status.inventoryItem != null) {
                val availableFormatted = "${UnitConverter.formatAmount(status.inventoryItem.amount)} ${status.inventoryItem.unit}"
                if (status.isSufficient) {
                    // Fully in stock
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = InStockGreen.copy(alpha = 0.15f),
                        contentColor = InStockGreen
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = InStockGreen
                            )
                            Text(
                                text = "Vorrat: $availableFormatted",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = InStockGreen
                            )
                        }
                    }
                } else {
                    // Insufficient stock
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = LowStockOrange.copy(alpha = 0.15f),
                        contentColor = LowStockOrange
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = LowStockOrange
                            )
                            Text(
                                text = "Nur $availableFormatted da",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = LowStockOrange
                            )
                        }
                    }
                }
            } else {
                // Completely missing from inventory
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = OutOfStockRed.copy(alpha = 0.15f),
                    contentColor = OutOfStockRed
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = OutOfStockRed
                        )
                        Text(
                            text = "Nicht im Vorrat",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = OutOfStockRed
                        )
                    }
                }
            }
        }
    }
}
