package com.example.budgetahead

import android.app.Application
import com.example.budgetahead.data.AppContainer
import com.example.budgetahead.data.AppDataContainer

class BudgetApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}
