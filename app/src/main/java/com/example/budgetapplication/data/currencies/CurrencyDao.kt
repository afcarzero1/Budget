package com.example.budgetapplication.data.currencies

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow


@Dao
interface CurrencyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(currency: Currency)

    @Update
    suspend fun update(currency: Currency)

    @Delete
    suspend fun delete(currency: Currency)

    @Query("SELECT * from currencies WHERE name = :name")
    fun getItem(name: String): Flow<Currency>

    @Query("SELECT * from currencies ORDER BY name ASC")
    fun getAllCurrencies(): Flow<List<Currency>>
}