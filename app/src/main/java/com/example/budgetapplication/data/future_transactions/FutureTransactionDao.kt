package com.example.budgetapplication.data.future_transactions

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy

@Dao
interface FutureTransactionDao{
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(futureTransaction: FutureTransaction)

    
}