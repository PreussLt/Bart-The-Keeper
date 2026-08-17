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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bartthekeeper.data.model.IngredientCategory
import com.example.bartthekeeper.data.model.InventoryItemEntity
import com.example.bartthekeeper.data.model.UnitConverter
import com.example.bartthekeeper.ui.components.CategoryBadge
import com.example.bartthekeeper.ui.components.InteractiveRatingPicker
import com.example.bartthekeeper.ui.theme.AddInsColor
import com.example.bartthekeeper.ui.theme.SaefteColor
import com.example.bartthekeeper.ui.theme.SirupColor
import com.example.bartthekeeper.ui.viewmodel.AddEditRecipeViewModel
import com.example.bartthekeeper.ui.viewmodel.EditableIngredient

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditRecipeScreen(
    recipeId: Long,
    viewModel: AddEditRecipeViewModel,
    onBack: () -> Unit,
    onSaved: (Long) -> Unit
) {
    LaunchedEffect(recipeId) {
        if (recipeId != 0L) {
            viewModel.loadRecipe(recipeId)
        }
    }

    val name by viewModel.name.collectAsState()
    val description by viewModel.description.collectAsState()
    val instructions by viewModel.instructions.collectAsState()
    val glassType by viewModel.glassType.collectAsState()
    val rating by viewModel.rating.collectAsState()
    val ingredients by viewModel.ingredients.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val availableStock by viewModel.availableInventoryItems.collectAsState()

    var showGlassDropdown by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (recipeId == 0L) "Neuer Mocktail" else "Mocktail bearbeiten") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Zurück"
                        )
                    }
                },
                actions = {
                    Button(
                        onClick = {
                            viewModel.saveRecipe { savedId ->
                                onSaved(savedId)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Speichern")
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
            // Error Message Banner
            if (errorMessage != null) {
                item {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.errorContainer,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = errorMessage!!,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }

            // Basic Info Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "🍹 Allgemeine Angaben",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )

                        OutlinedTextField(
                            value = name,
                            onValueChange = { viewModel.setName(it) },
                            label = { Text("Name des Mocktails *") },
                            placeholder = { Text("z.B. Tropical Sunset") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )

                        OutlinedTextField(
                            value = description,
                            onValueChange = { viewModel.setDescription(it) },
                            label = { Text("Kurzbeschreibung / Geschmack") },
                            placeholder = { Text("z.B. Fruchtig-frisch mit leichter Minznote") },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 2,
                            shape = RoundedCornerShape(12.dp)
                        )

                        // Glass Type Dropdown
                        Box(modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = glassType,
                                onValueChange = { viewModel.setGlassType(it) },
                                label = { Text("Glasart") },
                                modifier = Modifier.fillMaxWidth(),
                                trailingIcon = {
                                    IconButton(onClick = { showGlassDropdown = true }) {
                                        Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "Glas wählen")
                                    }
                                },
                                shape = RoundedCornerShape(12.dp)
                            )
                            DropdownMenu(
                                expanded = showGlassDropdown,
                                onDismissRequest = { showGlassDropdown = false }
                            ) {
                                viewModel.GLASS_TYPES.forEach { glass ->
                                    DropdownMenuItem(
                                        text = { Text(glass) },
                                        onClick = {
                                            viewModel.setGlassType(glass)
                                            showGlassDropdown = false
                                        }
                                    )
                                }
                            }
                        }

                        // Rating Picker (1 to 10)
                        InteractiveRatingPicker(
                            rating = rating,
                            onRatingSelected = { viewModel.setRating(it) }
                        )
                    }
                }
            }

            // Inhaltsgruppe 1: SIRUP
            item {
                IngredientCategoryEditorSection(
                    category = IngredientCategory.SIRUP,
                    ingredients = ingredients.filter { it.category == IngredientCategory.SIRUP },
                    availableStock = availableStock.filter { it.category == IngredientCategory.SIRUP },
                    onAdd = { viewModel.addIngredient(IngredientCategory.SIRUP) },
                    onUpdate = { tempId, name, cat, amount, unit ->
                        viewModel.updateIngredient(tempId, name, cat, amount, unit)
                    },
                    onSelectStock = { tempId, item ->
                        viewModel.selectInventorySuggestion(tempId, item)
                    },
                    onDelete = { viewModel.removeIngredient(it) }
                )
            }

            // Inhaltsgruppe 2: SÄFTE
            item {
                IngredientCategoryEditorSection(
                    category = IngredientCategory.SAEFTE,
                    ingredients = ingredients.filter { it.category == IngredientCategory.SAEFTE },
                    availableStock = availableStock.filter { it.category == IngredientCategory.SAEFTE },
                    onAdd = { viewModel.addIngredient(IngredientCategory.SAEFTE) },
                    onUpdate = { tempId, name, cat, amount, unit ->
                        viewModel.updateIngredient(tempId, name, cat, amount, unit)
                    },
                    onSelectStock = { tempId, item ->
                        viewModel.selectInventorySuggestion(tempId, item)
                    },
                    onDelete = { viewModel.removeIngredient(it) }
                )
            }

            // Inhaltsgruppe 3: ADD-INS
            item {
                IngredientCategoryEditorSection(
                    category = IngredientCategory.ADD_INS,
                    ingredients = ingredients.filter { it.category == IngredientCategory.ADD_INS },
                    availableStock = availableStock.filter { it.category == IngredientCategory.ADD_INS },
                    onAdd = { viewModel.addIngredient(IngredientCategory.ADD_INS) },
                    onUpdate = { tempId, name, cat, amount, unit ->
                        viewModel.updateIngredient(tempId, name, cat, amount, unit)
                    },
                    onSelectStock = { tempId, item ->
                        viewModel.selectInventorySuggestion(tempId, item)
                    },
                    onDelete = { viewModel.removeIngredient(it) }
                )
            }

            // Zubereitung / Instructions Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "📖 Zubereitungsschritte",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )

                        OutlinedTextField(
                            value = instructions,
                            onValueChange = { viewModel.setInstructions(it) },
                            placeholder = { Text("1. Glas mit Eis füllen...\n2. Zutaten im Shaker vermischen...\n3. Mit Minze garnieren.") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(130.dp),
                            maxLines = 8,
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }
            }

            // Save Button
            item {
                Button(
                    onClick = {
                        viewModel.saveRecipe { savedId ->
                            onSaved(savedId)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(
                        text = "💾 Rezept speichern",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun IngredientCategoryEditorSection(
    category: IngredientCategory,
    ingredients: List<EditableIngredient>,
    availableStock: List<InventoryItemEntity>,
    onAdd: () -> Unit,
    onUpdate: (String, String?, IngredientCategory?, String?, String?) -> Unit,
    onSelectStock: (String, InventoryItemEntity) -> Unit,
    onDelete: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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
                CategoryBadge(category = category)

                OutlinedButton(
                    onClick = onAdd,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${category.displayName} hinzufügen",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }

            if (ingredients.isEmpty()) {
                Text(
                    text = "Keine ${category.displayName}-Zutaten hinzugefügt.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    ingredients.forEach { ing ->
                        IngredientEditorRow(
                            ingredient = ing,
                            availableStock = availableStock,
                            onUpdate = { name, amount, unit ->
                                onUpdate(ing.tempId, name, null, amount, unit)
                            },
                            onSelectStock = { stockItem ->
                                onSelectStock(ing.tempId, stockItem)
                            },
                            onDelete = { onDelete(ing.tempId) }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IngredientEditorRow(
    ingredient: EditableIngredient,
    availableStock: List<InventoryItemEntity>,
    onUpdate: (String?, String?, String?) -> Unit,
    onSelectStock: (InventoryItemEntity) -> Unit,
    onDelete: () -> Unit
) {
    var showUnitMenu by remember { mutableStateOf(false) }
    var showSuggestions by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Ingredient Name Input
            OutlinedTextField(
                value = ingredient.name,
                onValueChange = {
                    onUpdate(it, null, null)
                    showSuggestions = it.isNotBlank()
                },
                placeholder = { Text("Zutat z.B. Grenadine") },
                modifier = Modifier.weight(1.5f),
                singleLine = true,
                shape = RoundedCornerShape(8.dp)
            )

            // Amount Input
            OutlinedTextField(
                value = ingredient.amountText,
                onValueChange = { onUpdate(null, it, null) },
                placeholder = { Text("Menge") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.weight(0.9f),
                singleLine = true,
                shape = RoundedCornerShape(8.dp)
            )

            // Unit Selector Dropdown
            Box(modifier = Modifier.weight(0.9f)) {
                OutlinedTextField(
                    value = ingredient.unit,
                    onValueChange = { onUpdate(null, null, it) },
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        IconButton(onClick = { showUnitMenu = true }) {
                            Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "Einheit")
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp)
                )
                DropdownMenu(
                    expanded = showUnitMenu,
                    onDismissRequest = { showUnitMenu = false }
                ) {
                    UnitConverter.COMMON_UNITS.forEach { u ->
                        DropdownMenuItem(
                            text = { Text(u) },
                            onClick = {
                                onUpdate(null, null, u)
                                showUnitMenu = false
                            }
                        )
                    }
                }
            }

            // Delete Button
            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Zutat entfernen",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }

        // Quick Suggestions from Stock
        if (availableStock.isNotEmpty()) {
            val suggestions = availableStock.filter {
                ingredient.name.isBlank() || it.name.lowercase().contains(ingredient.name.lowercase())
            }.take(4)

            if (suggestions.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Vorschlag:",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    suggestions.forEach { stock ->
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                            modifier = Modifier.clickable { onSelectStock(stock) }
                        ) {
                            Text(
                                text = stock.name,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
