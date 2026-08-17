package com.example.bartthekeeper.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.bartthekeeper.ui.screens.AddEditRecipeScreen
import com.example.bartthekeeper.ui.screens.HistoryScreen
import com.example.bartthekeeper.ui.screens.InventoryScreen
import com.example.bartthekeeper.ui.screens.MixStudioScreen
import com.example.bartthekeeper.ui.screens.RecipeDetailScreen
import com.example.bartthekeeper.ui.screens.RecipeListScreen
import com.example.bartthekeeper.ui.screens.SettingsScreen
import com.example.bartthekeeper.ui.viewmodel.AddEditRecipeViewModel
import com.example.bartthekeeper.ui.viewmodel.InventoryViewModel
import com.example.bartthekeeper.ui.viewmodel.MixHistoryViewModel
import com.example.bartthekeeper.ui.viewmodel.RecipeDetailViewModel
import com.example.bartthekeeper.ui.viewmodel.RecipeViewModel
import com.example.bartthekeeper.ui.viewmodel.SettingsViewModel
import com.example.bartthekeeper.ui.viewmodel.ViewModelFactory

@Composable
fun BartNavGraph(
    viewModelFactory: ViewModelFactory,
    navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute in Screen.bottomNavItems.map { it.route }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp
                ) {
                    Screen.bottomNavItems.forEach { screen ->
                        val isSelected = currentRoute == screen.route
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                if (currentRoute != screen.route) {
                                    navController.navigate(screen.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) screen.selectedIcon else screen.unselectedIcon,
                                    contentDescription = screen.title
                                )
                            },
                            label = {
                                Text(
                                    text = screen.title,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                            )
                        )
                    }
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Recipes.route,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Tab 1: Rezepte
            composable(Screen.Recipes.route) {
                val recipeViewModel: RecipeViewModel = viewModel(factory = viewModelFactory)
                RecipeListScreen(
                    viewModel = recipeViewModel,
                    onRecipeClick = { id ->
                        navController.navigate("recipe_detail/$id")
                    },
                    onAddRecipeClick = {
                        navController.navigate("add_edit_recipe/0")
                    }
                )
            }

            // Tab 2: Mix-Studio
            composable(Screen.MixStudio.route) {
                val recipeViewModel: RecipeViewModel = viewModel(factory = viewModelFactory)
                MixStudioScreen(
                    viewModel = recipeViewModel,
                    onRecipeClick = { id ->
                        navController.navigate("recipe_detail/$id")
                    },
                    onNavigateToInventory = {
                        navController.navigate(Screen.Inventory.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }

            // Tab 3: Bar-Vorrat
            composable(Screen.Inventory.route) {
                val inventoryViewModel: InventoryViewModel = viewModel(factory = viewModelFactory)
                InventoryScreen(viewModel = inventoryViewModel)
            }

            // Tab 4: Historie
            composable(Screen.History.route) {
                val historyViewModel: MixHistoryViewModel = viewModel(factory = viewModelFactory)
                HistoryScreen(viewModel = historyViewModel)
            }

            // Tab 5: Einstellungen (Settings)
            composable(Screen.Settings.route) {
                val settingsViewModel: SettingsViewModel = viewModel(factory = viewModelFactory)
                SettingsScreen(
                    viewModel = settingsViewModel,
                    onBack = {
                        navController.navigate(Screen.Recipes.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }

            // Recipe Detail Screen
            composable(
                route = "recipe_detail/{recipeId}",
                arguments = listOf(navArgument("recipeId") { type = NavType.LongType })
            ) { backStackEntry ->
                val recipeId = backStackEntry.arguments?.getLong("recipeId") ?: 0L
                val detailViewModel: RecipeDetailViewModel = viewModel(factory = viewModelFactory)
                RecipeDetailScreen(
                    recipeId = recipeId,
                    viewModel = detailViewModel,
                    onBack = { navController.popBackStack() },
                    onEditRecipe = { id ->
                        navController.navigate("add_edit_recipe/$id")
                    }
                )
            }

            // Add/Edit Recipe Screen
            composable(
                route = "add_edit_recipe/{recipeId}",
                arguments = listOf(navArgument("recipeId") { type = NavType.LongType })
            ) { backStackEntry ->
                val recipeId = backStackEntry.arguments?.getLong("recipeId") ?: 0L
                val addEditViewModel: AddEditRecipeViewModel = viewModel(factory = viewModelFactory)
                AddEditRecipeScreen(
                    recipeId = recipeId,
                    viewModel = addEditViewModel,
                    onBack = { navController.popBackStack() },
                    onSaved = { savedId ->
                        navController.popBackStack()
                        if (recipeId == 0L) {
                            navController.navigate("recipe_detail/$savedId")
                        }
                    }
                )
            }
        }
    }
}
