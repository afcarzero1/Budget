package com.example.budgetapplication.data.transfers

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.budgetapplication.R
import com.example.budgetapplication.data.accounts.FullAccount
import com.example.budgetapplication.ui.AppViewModelProvider
import com.example.budgetapplication.ui.accounts.AccountTransferForm
import com.example.budgetapplication.ui.components.dialogs.ConfirmationDeletionDialog
import com.example.budgetapplication.ui.navigation.SecondaryScreenTopBar
import kotlinx.coroutines.launch


@Composable
fun TransferDetailsScreen(
    navigateBack: () -> Unit,
    viewModel: TransferDetailsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val transferDBState by viewModel.transferDBState.collectAsState()
    val transferUiState = viewModel.transferUiState
    val availableAccounts by viewModel.accountsListState.collectAsState()

    var deleteConfirmationRequired by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current

    val coroutineScope = rememberCoroutineScope()

    Scaffold(topBar = {
        SecondaryScreenTopBar(navigateBack = navigateBack,
            titleResId = R.string.details_transaction_title,
            actions = {
                IconButton(
                    onClick = { deleteConfirmationRequired = true },
                    modifier = Modifier.padding(horizontal = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = stringResource(R.string.delete),
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
                IconButton(
                    modifier = Modifier.padding(horizontal = 8.dp), onClick = {
                        coroutineScope.launch {
                            viewModel.updateTransfer()
                        }
                        navigateBack()
                    }, enabled = transferUiState.isValid
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.save_24dp_fill0_wght400_grad0_opsz24),
                        contentDescription = stringResource(R.string.save),
                        tint = if (transferUiState.isValid) MaterialTheme.colorScheme.onPrimary else Color.Gray
                    )
                }
            })
    }) { innerPadding ->
        TransferDetailsBody(
            transactionDetailsUiState = viewModel.transferUiState,
            onTransferValueChange = {
                viewModel.updateUiState(it)
            },
            availableAccounts = availableAccounts,
            modifier = Modifier.padding(innerPadding)
        )

        if (deleteConfirmationRequired) {
            ConfirmationDeletionDialog(
                message = stringResource(R.string.delete_account),
                onDeleteConfirm = {
                    deleteConfirmationRequired = false
                    coroutineScope.launch {
                        try {
                            viewModel.deleteTransfer()
                        } catch (e: Exception) {
                            Toast.makeText(
                                context, "Error deleting transfer", Toast.LENGTH_SHORT
                            ).show()
                        }

                    }
                    navigateBack()
                },
                onDeleteCancel = { deleteConfirmationRequired = false },
                modifier = Modifier.padding(dimensionResource(id = R.dimen.medium))
            )
        }
    }


}


@Composable
fun TransferDetailsBody(
    transactionDetailsUiState: TransferDetailsUiState,
    availableAccounts: List<FullAccount>,
    onTransferValueChange: (Transfer) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        AccountTransferForm(
            transfer = transactionDetailsUiState.transfer,
            availableAccounts= availableAccounts,
            onValueChange = {
                onTransferValueChange(it)
            }
        )
    }
}