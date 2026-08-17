package com.example.bartthekeeper.data.local

import com.example.bartthekeeper.data.model.IngredientCategory
import com.example.bartthekeeper.data.model.InventoryItemEntity
import com.example.bartthekeeper.data.model.RecipeEntity
import com.example.bartthekeeper.data.model.RecipeIngredientEntity

object InitialData {

    fun getDefaultInventory(): List<InventoryItemEntity> = listOf(
        // Sirup
        InventoryItemEntity(name = "Grenadine", category = IngredientCategory.SIRUP, amount = 500.0, unit = "ml", minThreshold = 100.0, notes = "Klassischer Granatapfelsirup"),
        InventoryItemEntity(name = "Kokossirup", category = IngredientCategory.SIRUP, amount = 350.0, unit = "ml", minThreshold = 100.0, notes = "Für Coladas"),
        InventoryItemEntity(name = "Rohrzuckersirup", category = IngredientCategory.SIRUP, amount = 400.0, unit = "ml", minThreshold = 80.0, notes = "Zuckersirup / Simple Syrup"),
        InventoryItemEntity(name = "Himbeersirup", category = IngredientCategory.SIRUP, amount = 250.0, unit = "ml", minThreshold = 50.0, notes = "Fruchtig süß"),
        InventoryItemEntity(name = "Mangosirup", category = IngredientCategory.SIRUP, amount = 300.0, unit = "ml", minThreshold = 50.0, notes = "Exotische Süße"),
        InventoryItemEntity(name = "Vanillesirup", category = IngredientCategory.SIRUP, amount = 200.0, unit = "ml", minThreshold = 50.0, notes = "Feine Vanillenote"),

        // Säfte
        InventoryItemEntity(name = "Orangensaft", category = IngredientCategory.SAEFTE, amount = 1000.0, unit = "ml", minThreshold = 200.0, notes = "Direktsaft mit Fruchtfleisch"),
        InventoryItemEntity(name = "Ananassaft", category = IngredientCategory.SAEFTE, amount = 1000.0, unit = "ml", minThreshold = 200.0, notes = "100% Fruchtgehalt"),
        InventoryItemEntity(name = "Maracujanektar", category = IngredientCategory.SAEFTE, amount = 750.0, unit = "ml", minThreshold = 150.0, notes = "Tropisch fruchtig"),
        InventoryItemEntity(name = "Limettensaft", category = IngredientCategory.SAEFTE, amount = 500.0, unit = "ml", minThreshold = 100.0, notes = "Frisch gepresst oder Direktsaft"),
        InventoryItemEntity(name = "Cranberrysaft", category = IngredientCategory.SAEFTE, amount = 750.0, unit = "ml", minThreshold = 150.0, notes = "Herb-fruchtig"),
        InventoryItemEntity(name = "Apfelsaft naturtrüb", category = IngredientCategory.SAEFTE, amount = 1000.0, unit = "ml", minThreshold = 200.0, notes = "Regionaler Apfelsaft"),
        InventoryItemEntity(name = "Zitronensaft", category = IngredientCategory.SAEFTE, amount = 300.0, unit = "ml", minThreshold = 80.0, notes = "Frisch gepresst"),

        // Add-Ins
        InventoryItemEntity(name = "Minzblätter", category = IngredientCategory.ADD_INS, amount = 40.0, unit = "Blatt", minThreshold = 10.0, notes = "Frische Minze"),
        InventoryItemEntity(name = "Rohrzucker", category = IngredientCategory.ADD_INS, amount = 500.0, unit = "g", minThreshold = 100.0, notes = "Brauner Rohrzucker"),
        InventoryItemEntity(name = "Ginger Ale", category = IngredientCategory.ADD_INS, amount = 1000.0, unit = "ml", minThreshold = 250.0, notes = "Würzige Ingwer-Limonade"),
        InventoryItemEntity(name = "Soda / Mineralwasser", category = IngredientCategory.ADD_INS, amount = 1500.0, unit = "ml", minThreshold = 300.0, notes = "Kohlensäurereich"),
        InventoryItemEntity(name = "Tonic Water", category = IngredientCategory.ADD_INS, amount = 800.0, unit = "ml", minThreshold = 200.0, notes = "Leicht bitter"),
        InventoryItemEntity(name = "Sahne", category = IngredientCategory.ADD_INS, amount = 250.0, unit = "ml", minThreshold = 50.0, notes = "Schlagsahne flüssig"),
        InventoryItemEntity(name = "Limettenachtel", category = IngredientCategory.ADD_INS, amount = 16.0, unit = "Stk", minThreshold = 4.0, notes = "Frische Bio-Limetten"),
        InventoryItemEntity(name = "Frische Beeren", category = IngredientCategory.ADD_INS, amount = 200.0, unit = "g", minThreshold = 50.0, notes = "Himbeeren & Blaubeeren"),
        InventoryItemEntity(name = "Crushed Ice", category = IngredientCategory.ADD_INS, amount = 20.0, unit = "Portion", minThreshold = 3.0, notes = "Gestoßenes Eis"),
        InventoryItemEntity(name = "Eiswürfel", category = IngredientCategory.ADD_INS, amount = 50.0, unit = "Stk", minThreshold = 10.0, notes = "Gefrorene Eiswürfel")
    )

    fun getDefaultRecipes(): List<Pair<RecipeEntity, List<RecipeIngredientEntity>>> = listOf(
        Pair(
            RecipeEntity(
                id = 1L,
                name = "Virgin Mojito",
                description = "Der erfrischende kubanische Klassiker ganz ohne Alkohol mit viel Minze und spritziger Limette.",
                instructions = "1. Minzblätter und Rohrzucker ins Glas geben und mit einem Stößel leicht andrücken.\n2. Limettensaft und Rohrzuckersirup hinzufügen.\n3. Glas mit Crushed Ice füllen.\n4. Mit Apfelsaft und kaltem Sodawasser aufgießen und vorsichtig umrühren.\n5. Mit Minzzweig und Limettenscheibe garnieren.",
                glassType = "Highball Glas",
                rating = 9,
                isFavorite = true
            ),
            listOf(
                RecipeIngredientEntity(recipeId = 1L, name = "Rohrzuckersirup", category = IngredientCategory.SIRUP, amount = 2.0, unit = "cl"),
                RecipeIngredientEntity(recipeId = 1L, name = "Limettensaft", category = IngredientCategory.SAEFTE, amount = 3.0, unit = "cl"),
                RecipeIngredientEntity(recipeId = 1L, name = "Apfelsaft naturtrüb", category = IngredientCategory.SAEFTE, amount = 4.0, unit = "cl"),
                RecipeIngredientEntity(recipeId = 1L, name = "Minzblätter", category = IngredientCategory.ADD_INS, amount = 8.0, unit = "Blatt"),
                RecipeIngredientEntity(recipeId = 1L, name = "Rohrzucker", category = IngredientCategory.ADD_INS, amount = 10.0, unit = "g"),
                RecipeIngredientEntity(recipeId = 1L, name = "Soda / Mineralwasser", category = IngredientCategory.ADD_INS, amount = 100.0, unit = "ml"),
                RecipeIngredientEntity(recipeId = 1L, name = "Crushed Ice", category = IngredientCategory.ADD_INS, amount = 1.0, unit = "Portion")
            )
        ),
        Pair(
            RecipeEntity(
                id = 2L,
                name = "Ipanema",
                description = "Die fruchtige Caipirinha-Alternative aus Brasilien mit Maracujanektar und spritzigem Ginger Ale.",
                instructions = "1. Die Limettenachtel mit dem braunen Rohrzucker in ein stabiles Tumbler-Glas geben und mit einem Holzstößel kräftig zerdrücken.\n2. Das Glas randvoll mit Crushed Ice füllen.\n3. Maracujanektar und Rohrzuckersirup darüber gießen.\n4. Mit Ginger Ale auffüllen und mit einem Barlöffel gut durchrühren.\n5. Mit zwei kurzen Trinkhalmen servieren.",
                glassType = "Tumbler Glas",
                rating = 10,
                isFavorite = true
            ),
            listOf(
                RecipeIngredientEntity(recipeId = 2L, name = "Rohrzuckersirup", category = IngredientCategory.SIRUP, amount = 1.0, unit = "cl"),
                RecipeIngredientEntity(recipeId = 2L, name = "Limettensaft", category = IngredientCategory.SAEFTE, amount = 2.0, unit = "cl"),
                RecipeIngredientEntity(recipeId = 2L, name = "Maracujanektar", category = IngredientCategory.SAEFTE, amount = 8.0, unit = "cl"),
                RecipeIngredientEntity(recipeId = 2L, name = "Limettenachtel", category = IngredientCategory.ADD_INS, amount = 4.0, unit = "Stk"),
                RecipeIngredientEntity(recipeId = 2L, name = "Rohrzucker", category = IngredientCategory.ADD_INS, amount = 10.0, unit = "g"),
                RecipeIngredientEntity(recipeId = 2L, name = "Ginger Ale", category = IngredientCategory.ADD_INS, amount = 100.0, unit = "ml"),
                RecipeIngredientEntity(recipeId = 2L, name = "Crushed Ice", category = IngredientCategory.ADD_INS, amount = 1.0, unit = "Portion")
            )
        ),
        Pair(
            RecipeEntity(
                id = 3L,
                name = "Virgin Colada",
                description = "Cremig, samtig und herrlich exotisch – der tropische Traum mit Kokos, Sahne und Ananas.",
                instructions = "1. Alle flüssigen Zutaten zusammen mit 4 Eiswürfeln in einen Cocktail-Shaker geben.\n2. Für etwa 15 Sekunden kräftig shaken, bis sich eine sahnige Schaumkrone bildet.\n3. In ein mit frischen Eiswürfeln gefülltes Hurricaneglas abseihen.\n4. Mit einem Ananasstück servieren.",
                glassType = "Hurricaneglas",
                rating = 9,
                isFavorite = false
            ),
            listOf(
                RecipeIngredientEntity(recipeId = 3L, name = "Kokossirup", category = IngredientCategory.SIRUP, amount = 3.0, unit = "cl"),
                RecipeIngredientEntity(recipeId = 3L, name = "Ananassaft", category = IngredientCategory.SAEFTE, amount = 14.0, unit = "cl"),
                RecipeIngredientEntity(recipeId = 3L, name = "Orangensaft", category = IngredientCategory.SAEFTE, amount = 4.0, unit = "cl"),
                RecipeIngredientEntity(recipeId = 3L, name = "Sahne", category = IngredientCategory.ADD_INS, amount = 2.0, unit = "cl"),
                RecipeIngredientEntity(recipeId = 3L, name = "Eiswürfel", category = IngredientCategory.ADD_INS, amount = 5.0, unit = "Stk")
            )
        ),
        Pair(
            RecipeEntity(
                id = 4L,
                name = "Sunrise Paradise",
                description = "Fruchtige Symphonie mit wunderschönem Farbverlauf wie ein tropischer Sonnenaufgang.",
                instructions = "1. Highball Glas mit Eiswürfeln füllen.\n2. Orangensaft, Ananassaft und Zitronensaft mit etwas Eis im Shaker kurz vermengen und ins Glas gießen.\n3. Langsam die Grenadine über einen Löffelrücken ins Glas laufen lassen, damit sie auf den Boden sinkt.\n4. Nicht umrühren! Genießen.",
                glassType = "Highball Glas",
                rating = 8,
                isFavorite = false
            ),
            listOf(
                RecipeIngredientEntity(recipeId = 4L, name = "Grenadine", category = IngredientCategory.SIRUP, amount = 2.0, unit = "cl"),
                RecipeIngredientEntity(recipeId = 4L, name = "Orangensaft", category = IngredientCategory.SAEFTE, amount = 12.0, unit = "cl"),
                RecipeIngredientEntity(recipeId = 4L, name = "Ananassaft", category = IngredientCategory.SAEFTE, amount = 6.0, unit = "cl"),
                RecipeIngredientEntity(recipeId = 4L, name = "Zitronensaft", category = IngredientCategory.SAEFTE, amount = 2.0, unit = "cl"),
                RecipeIngredientEntity(recipeId = 4L, name = "Eiswürfel", category = IngredientCategory.ADD_INS, amount = 4.0, unit = "Stk")
            )
        ),
        Pair(
            RecipeEntity(
                id = 5L,
                name = "Berry Lavender Sparkle",
                description = "Fruchtig-blumiger Mocktail mit aromatischen Beeren, Cranberry und feinperligem Tonic.",
                instructions = "1. Ballonglas mit reichlich Eiswürfeln füllen.\n2. Himbeersirup, Vanillesirup, Cranberrysaft und Limettensaft hinzugeben.\n3. Vorsichtig mit kaltem Tonic Water auffüllen.\n4. Frische Beeren als Highlight hinzufügen.",
                glassType = "Ballonglas",
                rating = 9,
                isFavorite = false
            ),
            listOf(
                RecipeIngredientEntity(recipeId = 5L, name = "Himbeersirup", category = IngredientCategory.SIRUP, amount = 2.0, unit = "cl"),
                RecipeIngredientEntity(recipeId = 5L, name = "Vanillesirup", category = IngredientCategory.SIRUP, amount = 1.0, unit = "cl"),
                RecipeIngredientEntity(recipeId = 5L, name = "Cranberrysaft", category = IngredientCategory.SAEFTE, amount = 8.0, unit = "cl"),
                RecipeIngredientEntity(recipeId = 5L, name = "Limettensaft", category = IngredientCategory.SAEFTE, amount = 2.0, unit = "cl"),
                RecipeIngredientEntity(recipeId = 5L, name = "Tonic Water", category = IngredientCategory.ADD_INS, amount = 100.0, unit = "ml"),
                RecipeIngredientEntity(recipeId = 5L, name = "Frische Beeren", category = IngredientCategory.ADD_INS, amount = 20.0, unit = "g"),
                RecipeIngredientEntity(recipeId = 5L, name = "Eiswürfel", category = IngredientCategory.ADD_INS, amount = 5.0, unit = "Stk")
            )
        ),
        Pair(
            RecipeEntity(
                id = 6L,
                name = "Mango Passion Twist",
                description = "Sonnig-exotischer Mocktail mit Mangosirup, Passionsfrucht und einem Spritzer frischer Limette.",
                instructions = "1. Shaker mit Eis, Mangosirup, Maracujanektar, Orangensaft und Limettensaft füllen und kräftig schütteln.\n2. In ein Glas mit frischem Eis abseihen.\n3. Mit Soda toppen und mit Minzblättern dekorieren.",
                glassType = "Longdrinkglas",
                rating = 8,
                isFavorite = false
            ),
            listOf(
                RecipeIngredientEntity(recipeId = 6L, name = "Mangosirup", category = IngredientCategory.SIRUP, amount = 2.0, unit = "cl"),
                RecipeIngredientEntity(recipeId = 6L, name = "Maracujanektar", category = IngredientCategory.SAEFTE, amount = 10.0, unit = "cl"),
                RecipeIngredientEntity(recipeId = 6L, name = "Orangensaft", category = IngredientCategory.SAEFTE, amount = 6.0, unit = "cl"),
                RecipeIngredientEntity(recipeId = 6L, name = "Limettensaft", category = IngredientCategory.SAEFTE, amount = 2.0, unit = "cl"),
                RecipeIngredientEntity(recipeId = 6L, name = "Soda / Mineralwasser", category = IngredientCategory.ADD_INS, amount = 60.0, unit = "ml"),
                RecipeIngredientEntity(recipeId = 6L, name = "Minzblätter", category = IngredientCategory.ADD_INS, amount = 4.0, unit = "Blatt"),
                RecipeIngredientEntity(recipeId = 6L, name = "Eiswürfel", category = IngredientCategory.ADD_INS, amount = 4.0, unit = "Stk")
            )
        )
    )
}
