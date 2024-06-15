package com.example.budgetahead.data.categories

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime

class OfflineCategoriesRepository(
    private val categoryDao: CategoryDao
) : CategoriesRepository {
    override suspend fun insert(category: Category) = categoryDao.insert(category)

    override suspend fun update(category: Category) = categoryDao.update(category)

    override suspend fun delete(category: Category) = categoryDao.delete(category)

    override fun getCategoryStream(id: Int): Flow<Category> = categoryDao.getCategoryStream(id)

    override fun getAllCategoriesStream(): Flow<List<Category>> =
        categoryDao.getAllCategoriesStream()

    override fun getCategoryWithTransactionsStream(id: Int): Flow<CategoryWithTransactions> =
        categoryDao.getCategoryWithTransactionsStream(id)

    override fun getAllCategoriesWithTransactionsStream(): Flow<List<CategoryWithTransactions>> =
        categoryDao.getAllCategoriesWithTransactionsStream()

    override fun getAllCategoriesWithTransactionsStream(
        start: LocalDateTime,
        end: LocalDateTime
    ): Flow<List<CategoryWithTransactions>> =
        categoryDao.getAllCategoriesWithTransactionsStream(start, end).map { categoriesWithTransactions ->
            categoriesWithTransactions.map { categoryWithTransactions ->
                CategoryWithTransactions(
                    category = categoryWithTransactions.key,
                    transactions = categoryWithTransactions.value.map { transaction ->
                        transaction
                    }
                )
            }
        }
}