package com.example.budgetapplication.data.categories

import kotlinx.coroutines.flow.Flow


interface CategoriesRepository {
    suspend fun insert(category: Category)

    suspend fun update(category: Category)

    suspend fun delete(category: Category)

    fun getCategoryStream(id: Int): Flow<Category>

    fun getAllCategoriesStream(): Flow<List<Category>>

    fun getCategoryWithTransactionsStream(id: Int): Flow<CategoryWithTransactions>

    fun getAllCategoriesWithTransactionsStream(): Flow<List<CategoryWithTransactions>>
}