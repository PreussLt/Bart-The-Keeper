package com.example.bartthekeeper.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalBar
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bartthekeeper.data.model.MixResult
import com.example.bartthekeeper.data.model.RecipeMixability
import com.example.bartthekeeper.data.model.UnitConverter
import com.example.bartthekeeper.ui.components.CompactRatingBadge
import com.example.bartthekeeper.ui.components.MixConfirmationDialog
import com.example.bartthekeeper.ui.components.MixSuccessDialog
import com.example.bartthekeeper.ui.theme.InStockGreen
import com.example.bartthekeeper.ui.theme.LowStockOrange
import com.example.bartthekeeper.ui.viewmodel.RecipeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MixStudioScreen(
    viewModel: RecipeViewModel,
    onRecipeClick: (Long) -> Unit,
    onNavigateToInventory: () -> Unit
) {
    val allMixabilities by viewModel.filteredRecipes.collectAsState()
    val quickMixResult by viewModel.quickMixResult.collectAsState()

    val mixableNow = allMixabilities.filter { it.isFullyMixable }
    val almostMixable = allMixabilities.filter { !it.isFullyMixable && it.mixableIngredientsCount >= (it.totalIngredientsCount - 1) && it.totalIngredientsCount > 1 }
    val isAutoMode by viewModel.isAutoModeEnabled.collectAsState()

    var mixCandidate by remember { mutableStateOf<RecipeMixability?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "🍹 Mix-Studio",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "${mixableNow.size} Mocktails sofort zubereitbar",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Status Header Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (mixableNow.isNotEmpty()) InStockGreen.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(text = if (mixableNow.isNotEmpty()) "🎉" else "⏳", fontSize = 32.sp)
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (mixableNow.isNotEmpty())
                                    "${mixableNow.size} Mocktails bereit zum Mixen!"
                                else
                                    "Aktuell keine Mocktails vollständig mixbar",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = if (mixableNow.isNotEmpty()) InStockGreen else MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (mixableNow.isNotEmpty())
                                    "Klicke auf 'Mixen', um die Zutaten direkt aus dem Vorrat abzuziehen."
                                else
                                    "Fülle fehlende Zutaten in deinem Bar-Vorrat auf.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Mixable Now Section
            if (mixableNow.isNotEmpty()) {
                item {
                    Text(
                        text = "✨ Sofort mixbereit (${mixableNow.size})",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }

                items(mixableNow, key = { it.recipeWithIngredients.recipe.id }) { item ->
                    MixableRecipeCard(
                        item = item,
                        onClick = { onRecipeClick(item.recipeWithIngredients.recipe.id) },
                        onMixClick = {
                            if (isAutoMode) {
                                viewModel.quickMix(item.recipeWithIngredients.recipe.id, servings = 1)
                            } else {
                                mixCandidate = item
                            }
                        }
                    )
                }
            }

            // Almost Mixable Section (Missing only 1 ingredient)
            if (almostMixable.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "💡 Fast fertig gemixt (nur 1 Zutat fehlt)",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }

                items(almostMixable, key = { "almost_${it.recipeWithIngredients.recipe.id}" }) { item ->
                    AlmostMixableCard(
                        item = item,
                        onClick = { onRecipeClick(item.recipeWithIngredients.recipe.id) },
                        onInventoryClick = onNavigateToInventory
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(72.dp))
            }
        }
    }

    // Mix Confirmation Dialog
    mixCandidate?.let { candidate ->
        MixConfirmationDialog(
            mixability = candidate,
            isMixing = false,
            onConfirm = {
                viewModel.quickMix(candidate.recipeWithIngredients.recipe.id, servings = 1)
                mixCandidate = null
            },
            onDismiss = { mixCandidate = null }
        )
    }

    // Mix Success Dialog
    (quickMixResult as? MixResult.Success)?.let { success ->
        MixSuccessDialog(
            success = success,
            onDismiss = { viewModel.clearQuickMixResult() }
        )
    }
}

@Composable
fun MixableRecipeCard(
    item: RecipeMixability,
    onClick: () -> Unit,
    onMixClick: () -> Unit
) {
    val recipe = item.recipeWithIngredients.recipe

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = recipe.name,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    CompactRatingBadge(rating = recipe.rating)
                }
                Text(
                    text = "${recipe.glassType} • ${item.totalIngredientsCount} Zutaten",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Button(
                onClick = onMixClick,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(
                    text = "🍹 Mixen",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}

@Composable
fun AlmostMixableCard(
    item: RecipeMixability,
    onClick: () -> Unit,
    onInventoryClick: () -> Unit
) {
    val recipe = item.recipeWithIngredients.recipe
    val missing = item.missingIngredients.firstOrNull()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = recipe.name,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                CompactRatingBadge(rating = recipe.rating)
            }

            if (missing != null) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = LowStockOrange.copy(alpha = 0.12f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(text = "🛒", fontSize = 14.sp)
                        Text(
                            text = "Es fehlt nur: ${missing.ingredient.name} (${UnitConverter.formatAmount(missing.requiredAmount)} ${missing.ingredient.unit})",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                            color = LowStockOrange
                        )
                    }
                }
            }
        }
    }
}
