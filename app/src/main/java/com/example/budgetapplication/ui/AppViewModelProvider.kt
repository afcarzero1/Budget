package com.example.budgetapplication.ui

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.budgetapplication.BudgetApplication
import com.example.budgetapplication.data.transfers.TransferDetailsViewModel
import com.example.budgetapplication.ui.accounts.AccountDetailsViewModel
import com.example.budgetapplication.ui.accounts.AccountSummaryViewModel
import com.example.budgetapplication.ui.accounts.AccountTransferEntryViewModel
import com.example.budgetapplication.ui.accounts.AccountsEntryViewModel
import com.example.budgetapplication.ui.accounts.AccountsViewModel
import com.example.budgetapplication.ui.categories.CategoriesSummaryViewModel
import com.example.budgetapplication.ui.categories.CategoryDetailsViewModel
import com.example.budgetapplication.ui.categories.CategoryEntryViewModel
import com.example.budgetapplication.ui.currencies.CurrenciesViewModel
import com.example.budgetapplication.ui.overall.OverallViewModel
import com.example.budgetapplication.ui.transactions.FutureTransactionDetailsViewModel
import com.example.budgetapplication.ui.transactions.FutureTransactionEntryViewModel
import com.example.budgetapplication.ui.transactions.FutureTransactionsSummaryViewModel
import com.example.budgetapplication.ui.transactions.TransactionDetailsViewModel
import com.example.budgetapplication.ui.transactions.TransactionEntryViewModel
import com.example.budgetapplication.ui.transactions.TransactionsSummaryViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {

        initializer {
            CurrenciesViewModel(budgetApplication().container.currenciesRepository)
        }

        initializer {
            AccountsViewModel(budgetApplication().container.accountsRepository)
        }

        initializer {
            AccountSummaryViewModel(
                this.createSavedStateHandle(),
                budgetApplication().container.accountsRepository
            )
        }

        initializer {
            AccountsEntryViewModel(
                budgetApplication().container.accountsRepository,
                budgetApplication().container.currenciesRepository
            )
        }

        initializer {
            AccountDetailsViewModel(
                this.createSavedStateHandle(),
                budgetApplication().container.accountsRepository
            )
        }

        initializer {
            CategoriesSummaryViewModel(
                budgetApplication().container.categoriesRepository,
                budgetApplication().container.currenciesRepository
            )
        }

        initializer {
            CategoryEntryViewModel(budgetApplication().container.categoriesRepository)
        }

        initializer {
            CategoryDetailsViewModel(
                this.createSavedStateHandle(),
                budgetApplication().container.categoriesRepository
            )
        }

        initializer {
            TransactionsSummaryViewModel(
                budgetApplication().container.transactionsRepository,
                budgetApplication().container.currenciesRepository
            )
        }

        initializer {
            TransactionEntryViewModel(
                budgetApplication().container.transactionsRepository,
                budgetApplication().container.accountsRepository,
                budgetApplication().container.categoriesRepository
            )
        }

        initializer {
            TransferDetailsViewModel(
                this.createSavedStateHandle(),
                budgetApplication().container.transactionsRepository,
                budgetApplication().container.accountsRepository
            )
        }

        initializer {
            TransactionDetailsViewModel(
                this.createSavedStateHandle(),
                budgetApplication().container.transactionsRepository
            )
        }

        initializer {
            FutureTransactionsSummaryViewModel(
                budgetApplication().container.futureTransactionsRepository
            )
        }

        initializer {
            FutureTransactionEntryViewModel(
                budgetApplication().container.futureTransactionsRepository,
                budgetApplication().container.categoriesRepository,
                budgetApplication().container.currenciesRepository
            )
        }

        initializer {
            FutureTransactionDetailsViewModel(
                this.createSavedStateHandle(),
                budgetApplication().container.futureTransactionsRepository
            )
        }

        initializer {
            OverallViewModel(
                budgetApplication().container.accountsRepository,
                budgetApplication().container.balancesRepository,
                budgetApplication().container.currenciesRepository
            )
        }

        initializer {
            AccountTransferEntryViewModel(
                budgetApplication().container.accountsRepository
            )
        }

    }

}


fun CreationExtras.budgetApplication(): BudgetApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as BudgetApplication)