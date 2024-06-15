package com.example.budgetahead.data.categories

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.budgetahead.data.transactions.TransactionWithCurrency
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

@Dao
interface CategoryDao {


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(category: Category)

    @Update
    suspend fun update(category: Category)

    @Delete
    suspend fun delete(category: Category)

    @Query("SELECT * from categories WHERE id = :id")
    fun getCategoryStream(id: Int): Flow<Category>

    @Query("SELECT * from categories ORDER BY name ASC")
    fun getAllCategoriesStream(): Flow<List<Category>>

    @Transaction
    @Query("SELECT * from categories WHERE id = :id")
    fun getCategoryWithTransactionsStream(id: Int): Flow<CategoryWithTransactions>

    @Transaction
    @Query("SELECT * from categories ORDER BY name ASC")
    fun getAllCategoriesWithTransactionsStream(): Flow<List<CategoryWithTransactions>>

    @Transaction
    @Query("""
    SELECT * FROM categories 
    LEFT JOIN transactions ON categories.id = transactions.categoryId AND transactions.date > :start AND transactions.date < :end
    """
    )
    fun getAllCategoriesWithTransactionsStream(start: LocalDateTime, end: LocalDateTime): Flow<Map<Category, List<TransactionWithCurrency>>>

}