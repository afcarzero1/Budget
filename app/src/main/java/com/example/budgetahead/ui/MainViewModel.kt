package com.example.budgetahead.ui

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.budgetahead.data.manager.LocalUserManager
import com.example.budgetahead.ui.navigation.BudgetDestination
import com.example.budgetahead.ui.navigation.OnBoarding
import com.example.budgetahead.ui.navigation.Overview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class MainViewModel(
    private val localUserManager: LocalUserManager,
) : ViewModel() {
    private val _splashCondition = mutableStateOf(true)
    val splashCondition: State<Boolean> = _splashCondition

    private val _startDestination = mutableStateOf<BudgetDestination>(Overview)
    val startDestination: State<BudgetDestination> = _startDestination

    init {
        localUserManager
            .readAppEntry()
            .onEach { shouldStartFromHomeScreen ->
                if (shouldStartFromHomeScreen) {
                    _startDestination.value = Overview
                } else {
                    _startDestination.value = OnBoarding
                }
                delay(300)
                _splashCondition.value = false
            }.launchIn(viewModelScope)
    }
}
