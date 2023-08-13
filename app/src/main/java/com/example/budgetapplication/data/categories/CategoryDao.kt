package com.example.budgetapplication.data.categories

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

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
}