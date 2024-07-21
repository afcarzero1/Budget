package com.example.budgetahead.use_cases

import com.example.budgetahead.data.categories.Category
import java.time.YearMonth

interface ClassifyCategoriesUseCase {
    fun execute(
        balances: Map<YearMonth, Map<Category, Float>>,
        pos: Boolean,
    ): Map<YearMonth, Map<Category, Float>>
}

class ClassifyCategoriesUseCaseImpl : ClassifyCategoriesUseCase {
    override fun execute(
        balances: Map<YearMonth, Map<Category, Float>>,
        pos: Boolean,
    ): Map<YearMonth, Map<Category, Float>> {
        val classifiedExpenses: MutableMap<YearMonth, Map<Category, Float>> = mutableMapOf()
        for ((yearMonth, categoryMap) in balances) {
            val negativeExpenses =
                categoryMap.filter { (_, value) ->
                    if (pos) {
                        value > 0
                    } else {
                        value < 0
                    }
                }
            if (negativeExpenses.isNotEmpty()) {
                classifiedExpenses[yearMonth] = negativeExpenses
            } else {
                classifiedExpenses[yearMonth] = mapOf()
            }
        }
        return classifiedExpenses
    }
}
