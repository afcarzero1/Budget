package com.example.budgetahead.ui

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.budgetahead.BudgetApplication
import com.example.budgetahead.data.transfers.TransferDetailsViewModel
import com.example.budgetahead.ui.accounts.AccountDetailsViewModel
import com.example.budgetahead.ui.accounts.AccountSummaryViewModel
import com.example.budgetahead.ui.accounts.AccountTransferEntryViewModel
import com.example.budgetahead.ui.accounts.AccountsEntryViewModel
import com.example.budgetahead.ui.accounts.AccountsViewModel
import com.example.budgetahead.ui.cashflow.CashFlowOverviewViewModel
import com.example.budgetahead.ui.categories.CategoriesSummaryViewModel
import com.example.budgetahead.ui.categories.CategoryDetailsViewModel
import com.example.budgetahead.ui.categories.CategoryEntryViewModel
import com.example.budgetahead.ui.categories.CategoryOverviewViewModel
import com.example.budgetahead.ui.currencies.CurrenciesViewModel
import com.example.budgetahead.ui.onboarding.OnBoardingViewModel
import com.example.budgetahead.ui.overall.OverallViewModel
import com.example.budgetahead.ui.transactions.FutureTransactionDetailsViewModel
import com.example.budgetahead.ui.transactions.FutureTransactionEntryViewModel
import com.example.budgetahead.ui.transactions.FutureTransactionsSummaryViewModel
import com.example.budgetahead.ui.transactions.TransactionDetailsViewModel
import com.example.budgetahead.ui.transactions.TransactionEntryViewModel
import com.example.budgetahead.ui.transactions.TransactionsSummaryViewModel

object AppViewModelProvider {
    val Factory =
        viewModelFactory {

            initializer {
                MainViewModel(
                    budgetApplication().container.localUserManager,
                )
            }

            initializer {
                OnBoardingViewModel(
                    budgetApplication().container.localUserManager,
                )
            }

            initializer {
                CurrenciesViewModel(budgetApplication().container.currenciesRepository)
            }

            initializer {
                AccountsViewModel(budgetApplication().container.accountsRepository)
            }

            initializer {
                AccountSummaryViewModel(
                    this.createSavedStateHandle(),
                    budgetApplication().container.accountsRepository,
                    budgetApplication().container.currenciesRepository,
                )
            }

            initializer {
                AccountsEntryViewModel(
                    budgetApplication().container.accountsRepository,
                    budgetApplication().container.currenciesRepository,
                )
            }

            initializer {
                AccountDetailsViewModel(
                    this.createSavedStateHandle(),
                    budgetApplication().container.accountsRepository,
                )
            }

            initializer {
                CategoriesSummaryViewModel(
                    budgetApplication().container.categoriesRepository,
                    budgetApplication().container.currenciesRepository,
                )
            }

            initializer {
                CategoryEntryViewModel(budgetApplication().container.categoriesRepository)
            }

            initializer {
                CategoryOverviewViewModel(
                    this.createSavedStateHandle(),
                    budgetApplication().container.categoriesRepository,
                    budgetApplication().container.currenciesRepository,
                )
            }

            initializer {
                CategoryDetailsViewModel(
                    this.createSavedStateHandle(),
                    budgetApplication().container.categoriesRepository,
                )
            }

            initializer {
                TransactionsSummaryViewModel(
                    budgetApplication().container.transactionsRepository,
                    budgetApplication().container.currenciesRepository,
                )
            }

            initializer {
                TransactionEntryViewModel(
                    budgetApplication().container.transactionsRepository,
                    budgetApplication().container.accountsRepository,
                    budgetApplication().container.categoriesRepository,
                )
            }

            initializer {
                TransferDetailsViewModel(
                    this.createSavedStateHandle(),
                    budgetApplication().container.transactionsRepository,
                    budgetApplication().container.accountsRepository,
                )
            }

            initializer {
                TransactionDetailsViewModel(
                    this.createSavedStateHandle(),
                    budgetApplication().container.transactionsRepository,
                )
            }

            initializer {
                FutureTransactionsSummaryViewModel(
                    budgetApplication().container.futureTransactionsRepository,
                )
            }

            initializer {
                FutureTransactionEntryViewModel(
                    budgetApplication().container.futureTransactionsRepository,
                    budgetApplication().container.categoriesRepository,
                    budgetApplication().container.currenciesRepository,
                )
            }

            initializer {
                FutureTransactionDetailsViewModel(
                    this.createSavedStateHandle(),
                    budgetApplication().container.futureTransactionsRepository,
                )
            }

            initializer {
                OverallViewModel(
                    budgetApplication().container.accountsRepository,
                    budgetApplication().container.balancesRepository,
                    budgetApplication().container.currenciesRepository,
                )
            }

            initializer {
                CashFlowOverviewViewModel(
                    this.createSavedStateHandle(),
                    budgetApplication().container.currenciesRepository,
                    budgetApplication().container.balancesRepository,
                )
            }

            initializer {
                AccountTransferEntryViewModel(
                    budgetApplication().container.accountsRepository,
                )
            }
        }
}

fun CreationExtras.budgetApplication(): BudgetApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as BudgetApplication)
