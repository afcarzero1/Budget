package com.example.budgetahead.data.categories

import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

interface CategoriesRepository {
    suspend fun insert(category: Category)

    suspend fun update(category: Category)

    suspend fun delete(category: Category)

    fun getCategoryStream(id: Int): Flow<Category>

    fun getAllCategoriesStream(): Flow<List<Category>>

    fun getCategoryWithTransactionsStream(id: Int): Flow<CategoryWithTransactions>

    fun getCategoryWithPlannedTransactionsStream(id: Int): Flow<CategoryWithPlannedTransactions>

    fun getAllCategoriesWithTransactionsStream(): Flow<List<CategoryWithTransactions>>

    fun getAllCategoriesWithTransactionsStream(
        start: LocalDateTime,
        end: LocalDateTime,
    ): Flow<List<CategoryWithTransactions>>
}
