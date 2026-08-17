package com.example.bartthekeeper.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.LocalBar
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bartthekeeper.data.model.MixResult
import com.example.bartthekeeper.data.model.RecipeMixability
import com.example.bartthekeeper.ui.components.CategoryBadge
import com.example.bartthekeeper.ui.components.CompactRatingBadge
import com.example.bartthekeeper.ui.components.MixConfirmationDialog
import com.example.bartthekeeper.ui.components.MixSuccessDialog
import com.example.bartthekeeper.ui.theme.InStockGreen
import com.example.bartthekeeper.ui.theme.LowStockOrange
import com.example.bartthekeeper.ui.theme.OutOfStockRed
import com.example.bartthekeeper.ui.viewmodel.RecipeFilter
import com.example.bartthekeeper.ui.viewmodel.RecipeSort
import com.example.bartthekeeper.ui.viewmodel.RecipeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeListScreen(
    viewModel: RecipeViewModel,
    onRecipeClick: (Long) -> Unit,
    onAddRecipeClick: () -> Unit
) {
    val recipes by viewModel.filteredRecipes.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedFilter by viewModel.selectedFilter.collectAsState()
    val selectedSort by viewModel.selectedSort.collectAsState()
    val quickMixResult by viewModel.quickMixResult.collectAsState()
    val isAutoMode by viewModel.isAutoModeEnabled.collectAsState()

    var mixCandidate by remember { mutableStateOf<RecipeMixability?>(null) }
    var showSortMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "🍹 Bart The Keeper",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                },
                actions = {
                    Box {
                        IconButton(onClick = { showSortMenu = true }) {
                            Icon(imageVector = Icons.AutoMirrored.Filled.Sort, contentDescription = "Sortieren")
                        }
                        DropdownMenu(
                            expanded = showSortMenu,
                            onDismissRequest = { showSortMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Beste Bewertung (10-1)") },
                                onClick = {
                                    viewModel.setSort(RecipeSort.RATING_DESC)
                                    showSortMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Name (A-Z)") },
                                onClick = {
                                    viewModel.setSort(RecipeSort.NAME_ASC)
                                    showSortMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Neueste zuerst") },
                                onClick = {
                                    viewModel.setSort(RecipeSort.NEWEST)
                                    showSortMenu = false
                                }
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddRecipeClick,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Neues Rezept erstellen")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Mocktail oder Zutat suchen...") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = "Suchen")
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.setSearchQuery("") }) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = "Löschen")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                )
            )

            // Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedFilter == RecipeFilter.ALL,
                    onClick = { viewModel.setFilter(RecipeFilter.ALL) },
                    label = { Text("Alle (${recipes.size})") }
                )
                FilterChip(
                    selected = selectedFilter == RecipeFilter.MIXABLE_NOW,
                    onClick = { viewModel.setFilter(RecipeFilter.MIXABLE_NOW) },
                    label = { Text("🍹 Jetzt mixbar") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = InStockGreen.copy(alpha = 0.2f),
                        selectedLabelColor = InStockGreen
                    )
                )
                FilterChip(
                    selected = selectedFilter == RecipeFilter.FAVORITES,
                    onClick = { viewModel.setFilter(RecipeFilter.FAVORITES) },
                    label = { Text("❤️ Favoriten") }
                )
                FilterChip(
                    selected = selectedFilter == RecipeFilter.TOP_RATED,
                    onClick = { viewModel.setFilter(RecipeFilter.TOP_RATED) },
                    label = { Text("🏆 Top 8-10 ⭐") }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Recipe List
            if (recipes.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(text = "🍹", fontSize = 48.sp)
                        Text(
                            text = "Keine Mocktails gefunden",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = if (selectedFilter == RecipeFilter.MIXABLE_NOW)
                                "Fülle deinen Vorrat auf, um wieder leckere Mocktails mixen zu können!"
                            else
                                "Erstelle dein erstes eigenes Mocktail-Rezept mit dem '+' Button.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(recipes, key = { it.recipeWithIngredients.recipe.id }) { item ->
                        RecipeCard(
                            item = item,
                            onClick = { onRecipeClick(item.recipeWithIngredients.recipe.id) },
                            onFavoriteToggle = {
                                viewModel.toggleFavorite(
                                    item.recipeWithIngredients.recipe.id,
                                    item.recipeWithIngredients.recipe.isFavorite
                                )
                            },
                            onMixClick = {
                                if (isAutoMode) {
                                    viewModel.quickMix(item.recipeWithIngredients.recipe.id, servings = 1)
                                } else {
                                    mixCandidate = item
                                }
                            }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(72.dp))
                    }
                }
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
fun RecipeCard(
    item: RecipeMixability,
    onClick: () -> Unit,
    onFavoriteToggle: () -> Unit,
    onMixClick: () -> Unit
) {
    val recipe = item.recipeWithIngredients.recipe
    val withIngs = item.recipeWithIngredients

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
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
            // Header: Name, Glass Type, Rating, Favorite
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = recipe.name,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = recipe.glassType,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    CompactRatingBadge(rating = recipe.rating)

                    IconButton(
                        onClick = onFavoriteToggle,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = if (recipe.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorit",
                            tint = if (recipe.isFavorite) Color(0xFFE11D48) else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            if (recipe.description.isNotBlank()) {
                Text(
                    text = recipe.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Inhaltsgruppen Counts (Sirup, Säfte, Add-Ins)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (withIngs.syrups.isNotEmpty()) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                    ) {
                        Text(
                            text = "🍯 ${withIngs.syrups.size} Sirup",
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                }
                if (withIngs.juices.isNotEmpty()) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                    ) {
                        Text(
                            text = "🍊 ${withIngs.juices.size} Säfte",
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                }
                if (withIngs.addIns.isNotEmpty()) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                    ) {
                        Text(
                            text = "🌿 ${withIngs.addIns.size} Add-Ins",
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            // Bottom row: Vorrat status & Quick Mix button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (item.isFullyMixable) {
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
                                text = "Alles im Vorrat ✓",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = InStockGreen
                            )
                        }
                    }
                } else {
                    val missing = item.missingIngredients.size
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
                                text = "$missing Zutat(en) fehlen",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = LowStockOrange
                            )
                        }
                    }
                }

                Button(
                    onClick = onMixClick,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (item.isFullyMixable) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "🍹 Mixen",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }
}
