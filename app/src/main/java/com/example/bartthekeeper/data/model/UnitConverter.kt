package com.example.bartthekeeper.data.model

import java.text.DecimalFormat

object UnitConverter {

    private val df = DecimalFormat("#.##")

    /**
     * Converts a given amount in a given unit into a base unit (usually 'ml' for liquids or 'Stk' for items).
     */
    fun toBaseUnit(amount: Double, unit: String): Pair<Double, String> {
        val cleanUnit = unit.trim().lowercase()
        return when (cleanUnit) {
            "cl" -> Pair(amount * 10.0, "ml")
            "l", "liter" -> Pair(amount * 1000.0, "ml")
            "dl" -> Pair(amount * 100.0, "ml")
            "ml", "milliliter" -> Pair(amount, "ml")
            "bl", "barlöffel", "tl", "teelöffel" -> Pair(amount * 5.0, "ml")
            "el", "esslöffel" -> Pair(amount * 15.0, "ml")
            "spritzer", "dash", "dashes" -> Pair(amount * 1.0, "ml")
            "tropfen" -> Pair(amount * 0.05, "ml")
            
            "blatt", "blätter", "mint leaves" -> Pair(amount, "Blatt")
            "scheibe", "scheiben" -> Pair(amount, "Scheibe")
            "stk", "stk.", "stück", "stueck" -> Pair(amount, "Stk")
            "zweig", "zweige" -> Pair(amount, "Zweig")
            "g", "gramm" -> Pair(amount, "g")
            "kg" -> Pair(amount * 1000.0, "g")
            "prise", "prisen" -> Pair(amount * 1.0, "g")
            "portion", "portionen" -> Pair(amount, "Portion")
            else -> Pair(amount, unit.trim())
        }
    }

    /**
     * Converts an amount from one unit to another compatible unit.
     * Returns null if units are incompatible.
     */
    fun convert(amount: Double, fromUnit: String, toUnit: String): Double? {
        if (fromUnit.equals(toUnit, ignoreCase = true)) {
            return amount
        }

        val (baseAmount, baseUnit) = toBaseUnit(amount, fromUnit)
        val (targetOneBaseAmount, targetBaseUnit) = toBaseUnit(1.0, toUnit)

        if (baseUnit.equals(targetBaseUnit, ignoreCase = true) && targetOneBaseAmount > 0) {
            return baseAmount / targetOneBaseAmount
        }

        // If units are not directly convertible, check if both are liquid or pieces
        return null
    }

    /**
     * Formats a double amount nicely for UI (e.g. 2 instead of 2.0, 1.5 instead of 1.5000000000000002).
     */
    fun formatAmount(amount: Double): String {
        return if (amount % 1.0 == 0.0) {
            amount.toInt().toString()
        } else {
            df.format(amount)
        }
    }

    val COMMON_UNITS = listOf(
        "ml",
        "cl",
        "l",
        "TL",
        "BL",
        "EL",
        "Spritzer",
        "Stk",
        "Blätter",
        "Scheiben",
        "Zweige",
        "g",
        "Prise"
    )
}
