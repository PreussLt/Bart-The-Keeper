package com.example.bartthekeeper.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocalBar
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bartthekeeper.data.model.IngredientCategory
import com.example.bartthekeeper.data.model.InventoryItemEntity
import com.example.bartthekeeper.data.model.UnitConverter
import com.example.bartthekeeper.ui.components.CategoryBadge
import com.example.bartthekeeper.ui.theme.InStockGreen
import com.example.bartthekeeper.ui.theme.LowStockOrange
import com.example.bartthekeeper.ui.theme.OutOfStockRed
import com.example.bartthekeeper.ui.viewmodel.InventoryCategoryFilter
import com.example.bartthekeeper.ui.viewmodel.InventoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryScreen(
    viewModel: InventoryViewModel
) {
    val items by viewModel.filteredInventory.collectAsState()
    val allItems by viewModel.rawInventory.collectAsState()
    val selectedFilter by viewModel.selectedFilter.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val itemToEdit by viewModel.itemToEdit.collectAsState()
    val isAddEditOpen by viewModel.isAddEditOpen.collectAsState()

    var showResetDialog by remember { mutableStateOf(false) }

    val lowStockCount = allItems.count { it.amount <= it.minThreshold && it.minThreshold > 0 || it.amount == 0.0 }
    val sirupCount = allItems.count { it.category == IngredientCategory.SIRUP }
    val saefteCount = allItems.count { it.category == IngredientCategory.SAEFTE }
    val addInsCount = allItems.count { it.category == IngredientCategory.ADD_INS }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "📦 Bar-Vorrat",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "${allItems.size} Zutaten registriert",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showResetDialog = true }) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = "Standard-Bar wiederherstellen")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.openAddItem() },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Zutat hinzufügen")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Search Field
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Zutat im Vorrat suchen...") },
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

            // Category Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedFilter == InventoryCategoryFilter.ALL,
                    onClick = { viewModel.setFilter(InventoryCategoryFilter.ALL) },
                    label = { Text("Alle (${allItems.size})") }
                )
                FilterChip(
                    selected = selectedFilter == InventoryCategoryFilter.SIRUP,
                    onClick = { viewModel.setFilter(InventoryCategoryFilter.SIRUP) },
                    label = { Text("🍯 Sirup ($sirupCount)") }
                )
                FilterChip(
                    selected = selectedFilter == InventoryCategoryFilter.SAEFTE,
                    onClick = { viewModel.setFilter(InventoryCategoryFilter.SAEFTE) },
                    label = { Text("🍊 Säfte ($saefteCount)") }
                )
                FilterChip(
                    selected = selectedFilter == InventoryCategoryFilter.ADD_INS,
                    onClick = { viewModel.setFilter(InventoryCategoryFilter.ADD_INS) },
                    label = { Text("🌿 Add-Ins ($addInsCount)") }
                )
                if (lowStockCount > 0) {
                    FilterChip(
                        selected = selectedFilter == InventoryCategoryFilter.LOW_STOCK,
                        onClick = { viewModel.setFilter(InventoryCategoryFilter.LOW_STOCK) },
                        label = { Text("⚠ Knapp ($lowStockCount)") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = LowStockOrange.copy(alpha = 0.2f),
                            selectedLabelColor = LowStockOrange
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Inventory List
            if (items.isEmpty()) {
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
                        Text(text = "📦", fontSize = 48.sp)
                        Text(
                            text = "Keine Zutaten im Vorrat",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Füge deine Vorräte hinzu, damit Bart The Keeper den Bestand beim Mixen automatisch abziehen kann.",
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
                    items(items, key = { it.id }) { item ->
                        InventoryItemCard(
                            item = item,
                            onAdjustAmount = { delta -> viewModel.adjustAmount(item.id, delta) },
                            onEdit = { viewModel.openEditItem(item) },
                            onDelete = { viewModel.deleteItem(item) }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(72.dp))
                    }
                }
            }
        }
    }

    // Add/Edit Item Dialog
    if (isAddEditOpen) {
        AddEditInventoryDialog(
            item = itemToEdit,
            onSave = { id, name, cat, amount, unit, min, notes ->
                viewModel.saveItem(id, name, cat, amount, unit, min, notes)
            },
            onDismiss = { viewModel.closeAddEdit() }
        )
    }

    // Reset Defaults Dialog
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Standard-Bar wiederherstellen?") },
            text = { Text("Möchtest du den Bar-Vorrat und die Beispielrezepte auf den Anfangszustand zurücksetzen?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.resetToDefaults()
                        showResetDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Wiederherstellen")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Abbrechen")
                }
            }
        )
    }
}

@Composable
fun InventoryItemCard(
    item: InventoryItemEntity,
    onAdjustAmount: (Double) -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val isLowStock = (item.minThreshold > 0 && item.amount <= item.minThreshold) || item.amount == 0.0
    val stepDelta = when (item.unit.lowercase()) {
        "ml" -> 50.0
        "cl" -> 5.0
        "g" -> 50.0
        "stk", "blatt", "blätter", "scheibe", "scheiben", "portion" -> 1.0
        else -> 1.0
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header: Name, Category, Edit & Delete
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (item.notes.isNotBlank()) {
                        Text(
                            text = item.notes,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    CategoryBadge(category = item.category)
                    IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = "Bearbeiten", modifier = Modifier.size(16.dp))
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Löschen", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                    }
                }
            }

            // Current Stock & Quick Stepper
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Stock Badge
                Column {
                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = UnitConverter.formatAmount(item.amount),
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
                            color = if (item.amount == 0.0) OutOfStockRed else if (isLowStock) LowStockOrange else InStockGreen
                        )
                        Text(
                            text = item.unit,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 2.dp)
                        )
                    }

                    if (isLowStock) {
                        Text(
                            text = if (item.amount == 0.0) "❌ Nicht mehr auf Vorrat" else "⚠ Knapper Bestand (Min: ${UnitConverter.formatAmount(item.minThreshold)} ${item.unit})",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = if (item.amount == 0.0) OutOfStockRed else LowStockOrange
                        )
                    }
                }

                // Quick +/- Buttons
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Minus Button
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .clickable(enabled = item.amount > 0) { onAdjustAmount(-stepDelta) },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = "- $stepDelta",
                            modifier = Modifier.size(18.dp),
                            tint = if (item.amount > 0) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                        )
                    }

                    // Plus Button
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                            .clickable { onAdjustAmount(stepDelta) },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "+ $stepDelta",
                            modifier = Modifier.size(18.dp),
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditInventoryDialog(
    item: InventoryItemEntity?,
    onSave: (Long, String, IngredientCategory, Double, String, Double, String) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(item?.name ?: "") }
    var category by remember { mutableStateOf(item?.category ?: IngredientCategory.SIRUP) }
    var amountText by remember { mutableStateOf(item?.amount?.let { UnitConverter.formatAmount(it) } ?: "500") }
    var unit by remember { mutableStateOf(item?.unit ?: "ml") }
    var minThresholdText by remember { mutableStateOf(item?.minThreshold?.let { UnitConverter.formatAmount(it) } ?: "100") }
    var notes by remember { mutableStateOf(item?.notes ?: "") }
    var showCategoryMenu by remember { mutableStateOf(false) }
    var showUnitMenu by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (item == null) "➕ Neue Zutat zum Vorrat" else "✏️ Vorrats-Zutat bearbeiten",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name der Zutat *") },
                    placeholder = { Text("z.B. Maracujanektar") },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                // Category Selector
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = "${category.iconEmoji} ${category.displayName}",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Kategorie *") },
                        trailingIcon = {
                            IconButton(onClick = { showCategoryMenu = true }) {
                                Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "Kategorie wählen")
                            }
                        },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    DropdownMenu(
                        expanded = showCategoryMenu,
                        onDismissRequest = { showCategoryMenu = false }
                    ) {
                        IngredientCategory.entries.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text("${cat.iconEmoji} ${cat.displayName}") },
                                onClick = {
                                    category = cat
                                    showCategoryMenu = false
                                }
                            )
                        }
                    }
                }

                // Amount & Unit
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = amountText,
                        onValueChange = { amountText = it },
                        label = { Text("Aktueller Vorrat") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1.2f)
                    )

                    Box(modifier = Modifier.weight(0.8f)) {
                        OutlinedTextField(
                            value = unit,
                            onValueChange = { unit = it },
                            label = { Text("Einheit") },
                            trailingIcon = {
                                IconButton(onClick = { showUnitMenu = true }) {
                                    Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "Einheit wählen")
                                }
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                        DropdownMenu(
                            expanded = showUnitMenu,
                            onDismissRequest = { showUnitMenu = false }
                        ) {
                            UnitConverter.COMMON_UNITS.forEach { u ->
                                DropdownMenuItem(
                                    text = { Text(u) },
                                    onClick = {
                                        unit = u
                                        showUnitMenu = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Minimum threshold for warning
                OutlinedTextField(
                    value = minThresholdText,
                    onValueChange = { minThresholdText = it },
                    label = { Text("Warnung bei Mindestbestand ($unit)") },
                    placeholder = { Text("z.B. 100") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notiz / Marke (optional)") },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        val parsedAmount = amountText.replace(',', '.').toDoubleOrNull() ?: 0.0
                        val parsedMin = minThresholdText.replace(',', '.').toDoubleOrNull() ?: 0.0
                        onSave(
                            item?.id ?: 0L,
                            name,
                            category,
                            parsedAmount,
                            unit,
                            parsedMin,
                            notes
                        )
                    }
                },
                enabled = name.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Speichern")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Abbrechen")
            }
        }
    )
}
