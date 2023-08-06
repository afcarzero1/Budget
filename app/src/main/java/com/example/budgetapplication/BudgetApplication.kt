package com.example.budgetapplication

import android.app.Application
import com.example.budgetapplication.data.AppContainer
import com.example.budgetapplication.data.AppDataContainer

class BudgetApplication : Application() {

    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}