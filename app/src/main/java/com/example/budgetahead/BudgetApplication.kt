package com.example.budgetahead

import android.app.Application
import com.example.budgetahead.data.AppContainer
import com.example.budgetahead.data.AppDataContainer
import com.example.budgetahead.R

class BudgetApplication : Application() {

    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}