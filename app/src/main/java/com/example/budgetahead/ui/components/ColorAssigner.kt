package com.example.budgetahead.ui.components

import androidx.compose.ui.graphics.Color

class ColorAssigner(
    private val availableColors: List<Color>,
) {
    private val temporarilyExcludedColors: MutableSet<Int> = HashSet()
    private val assignedColors: MutableMap<String, Color> = HashMap()

    fun assignColor(key: String): Color {
        // If already assigned, return the assigned color
        if (assignedColors.containsKey(key)) {
            return assignedColors[key]!!
        }

        if (temporarilyExcludedColors.size == availableColors.size) {
            temporarilyExcludedColors.clear()
        }

        val index = findAvailableColorIndex(key.hashCode())

        val color = availableColors[index]
        temporarilyExcludedColors.add(index)

        assignedColors[key] = color
        return color
    }

    private fun findAvailableColorIndex(hashCode: Int): Int {
        if (availableColors.isEmpty()) {
            throw IllegalStateException("No colors available.")
        }

        var index = (hashCode % availableColors.size + availableColors.size) % availableColors.size
        while (temporarilyExcludedColors.contains(index)) {
            index = (index + 1) % availableColors.size
        }

        return index
    }
}
