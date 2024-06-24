package com.example.budgetahead.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.budgetahead.data.manager.LocalUserManager
import kotlinx.coroutines.launch

class OnBoardingViewModel(
    private val localUserManager: LocalUserManager
) : ViewModel() {

    fun onEvent(event: OnBoardingEvent) {
        when (event) {
            is OnBoardingEvent.SaveAppEntry -> {
                saveAppEntry()
            }
        }
    }

    private fun saveAppEntry() {
        viewModelScope.launch {
            localUserManager.saveAppEntry()
        }
    }

}


sealed class OnBoardingEvent {
    object SaveAppEntry : OnBoardingEvent()
}