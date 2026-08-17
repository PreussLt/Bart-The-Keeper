package com.example.bartthekeeper.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocalBar
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.bartthekeeper.data.model.IngredientCategory
import com.example.bartthekeeper.data.model.IngredientStockStatus
import com.example.bartthekeeper.data.model.MixResult
import com.example.bartthekeeper.ui.components.CategoryBadge
import com.example.bartthekeeper.ui.components.CompactRatingBadge
import com.example.bartthekeeper.ui.components.IngredientStockRow
import com.example.bartthekeeper.ui.components.InteractiveRatingPicker
import com.example.bartthekeeper.ui.components.MixConfirmationDialog
import com.example.bartthekeeper.ui.components.MixSuccessDialog
import com.example.bartthekeeper.ui.components.ServingSelector
import com.example.bartthekeeper.ui.theme.AddInsColor
import com.example.bartthekeeper.ui.theme.InStockGreen
import com.example.bartthekeeper.ui.theme.LowStockOrange
import com.example.bartthekeeper.ui.theme.SaefteColor
import com.example.bartthekeeper.ui.theme.SirupColor
import com.example.bartthekeeper.ui.viewmodel.RecipeDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeDetailScreen(
    recipeId: Long,
    viewModel: RecipeDetailViewModel,
    onBack: () -> Unit,
    onEditRecipe: (Long) -> Unit
) {
    LaunchedEffect(recipeId) {
        viewModel.loadRecipe(recipeId)
    }

    val details by viewModel.recipeDetails.collectAsState()
    val servings by viewModel.servings.collectAsState()
    val isMixing by viewModel.isMixing.collectAsState()
    val mixResult by viewModel.mixResult.collectAsState()
    val isAutoMode by viewModel.isAutoModeEnabled.collectAsState()

    var showMixConfirm by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(details?.recipeWithIngredients?.recipe?.name ?: "Mocktail Details") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Zurück"
                        )
                    }
                },
                actions = {
                    details?.let { d ->
                        IconButton(onClick = { viewModel.toggleFavorite() }) {
                            Icon(
                                imageVector = if (d.recipeWithIngredients.recipe.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Favorit",
                                tint = if (d.recipeWithIngredients.recipe.isFavorite) Color(0xFFE11D48) else MaterialTheme.colorScheme.onSurface
                            )
                        }
                        IconButton(onClick = { onEditRecipe(d.recipeWithIngredients.recipe.id) }) {
                            Icon(imageVector = Icons.Default.Edit, contentDescription = "Bearbeiten")
                        }
                        IconButton(onClick = { showDeleteConfirm = true }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Löschen",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            details?.let { d ->
                Surface(
                    shadowElevation = 8.dp,
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                if (isAutoMode) {
                                    viewModel.mixCocktail()
                                } else {
                                    showMixConfirm = true
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Text(
                                text = "🍹 Cocktails mixen ($servings Portion${if (servings > 1) "en" else ""})",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    ) { padding ->
        if (details == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Rezept wird geladen...")
            }
        } else {
            val d = details!!
            val recipe = d.recipeWithIngredients.recipe
            val syrups = d.ingredientStatuses.filter { it.ingredient.category == IngredientCategory.SIRUP }
            val juices = d.ingredientStatuses.filter { it.ingredient.category == IngredientCategory.SAEFTE }
            val addIns = d.ingredientStatuses.filter { it.ingredient.category == IngredientCategory.ADD_INS }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header Hero Card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = recipe.name,
                                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
                                    )
                                    Text(
                                        text = "Glas: ${recipe.glassType}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                CompactRatingBadge(rating = recipe.rating)
                            }

                            if (recipe.description.isNotBlank()) {
                                Text(
                                    text = recipe.description,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                            // Interactive Rating Modifier
                            InteractiveRatingPicker(
                                rating = recipe.rating,
                                onRatingSelected = { newRating ->
                                    viewModel.updateRating(newRating)
                                }
                            )
                        }
                    }
                }

                // Servings Selector & Stock Overview
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ServingSelector(
                            servings = servings,
                            onServingsChange = { viewModel.setServings(it) }
                        )

                        if (d.isFullyMixable) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = InStockGreen.copy(alpha = 0.15f),
                                contentColor = InStockGreen
                            ) {
                                Text(
                                    text = "Vollständig mixbar ✓",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = InStockGreen,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        } else {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = LowStockOrange.copy(alpha = 0.15f),
                                contentColor = LowStockOrange
                            ) {
                                Text(
                                    text = "${d.missingIngredients.size} Zutat(en) fehlen",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = LowStockOrange,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }

                // Inhaltsgruppe 1: SIRUP
                if (syrups.isNotEmpty()) {
                    item {
                        IngredientGroupSection(
                            category = IngredientCategory.SIRUP,
                            items = syrups
                        )
                    }
                }

                // Inhaltsgruppe 2: SÄFTE
                if (juices.isNotEmpty()) {
                    item {
                        IngredientGroupSection(
                            category = IngredientCategory.SAEFTE,
                            items = juices
                        )
                    }
                }

                // Inhaltsgruppe 3: ADD-INS
                if (addIns.isNotEmpty()) {
                    item {
                        IngredientGroupSection(
                            category = IngredientCategory.ADD_INS,
                            items = addIns
                        )
                    }
                }

                // Instructions / Zubereitung
                if (recipe.instructions.isNotBlank()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "📖 Zubereitung",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = recipe.instructions,
                                    style = MaterialTheme.typography.bodyMedium,
                                    lineHeight = 22.sp
                                )
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }

    // Mix Confirmation Dialog
    if (showMixConfirm && details != null) {
        MixConfirmationDialog(
            mixability = details!!,
            isMixing = isMixing,
            onConfirm = {
                viewModel.mixCocktail()
                showMixConfirm = false
            },
            onDismiss = { showMixConfirm = false }
        )
    }

    // Mix Success Dialog
    (mixResult as? MixResult.Success)?.let { success ->
        MixSuccessDialog(
            success = success,
            onDismiss = { viewModel.clearMixResult() }
        )
    }

    // Delete Confirmation Dialog
    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Rezept löschen?") },
            text = { Text("Möchtest du dieses Mocktail-Rezept wirklich unwiderruflich löschen?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteRecipe(onDeleted = onBack)
                        showDeleteConfirm = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Löschen")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("Abbrechen")
                }
            }
        )
    }
}

@Composable
fun IngredientGroupSection(
    category: IngredientCategory,
    items: List<IngredientStockStatus>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CategoryBadge(category = category)
                Text(
                    text = "(${items.size} Zutat${if (items.size > 1) "en" else ""})",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                items.forEach { status ->
                    IngredientStockRow(status = status)
                }
            }
        }
    }
}
