package com.example.parkingqr.ui.components.pay

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.parkingqr.data.remote.State
import com.example.parkingqr.data.repo.payment.PaymentRepository
import com.example.parkingqr.domain.model.payment.BankAccount
import com.example.parkingqr.domain.model.payment.PayByTokenResponse
import com.example.parkingqr.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PayOnlineViewModel @Inject constructor(private val paymentRepository: PaymentRepository) :
    BaseViewModel() {

    private val _uiState = MutableStateFlow(
        PayOnlineUiState()
    )
    val uiState = _uiState.asStateFlow()

    init {
        getBankAccountList()
    }

    fun setAmount(amount: Double) {
        _uiState.update {
            it.copy(
                amount = amount
            )
        }
    }

    fun selectBankAccount(bankAccount: BankAccount) {
        _uiState.update {
            it.copy(
                selectedBankAccount = bankAccount
            )
        }
    }

    fun payByToken() {
        uiState.value.apply {
            val price = amount
            val token = selectedBankAccount?.token
            if (price == null || token == null) return
            viewModelScope.launch {
                paymentRepository.payByToken(price, token)
                    .collect { state ->
                        when (state) {
                            is State.Loading -> {
                                _uiState.update {
                                    it.copy(isLoading = true)
                                }
                            }
                            is State.Success -> {
                                _uiState.update {
                                    it.copy(
                                        isLoading = false,
                                        payByTokenResponse = state.data
                                    )
                                }
                            }
                            is State.Failed -> {
                                _uiState.update {
                                    it.copy(
                                        isLoading = false,
                                        error = state.message
                                    )
                                }
                            }
                        }

                    }
            }
        }
    }

    fun showPaymentPage() {
        _uiState.update {
            it.copy(
                payByTokenResponse = null
            )
        }
    }

    fun showError() {
        _uiState.update {
            it.copy(
                error = ""
            )
        }
    }

    fun showMessage() {
        _uiState.update {
            it.copy(
                message = ""
            )
        }
    }

    private fun getBankAccountList() {
        viewModelScope.launch {
            paymentRepository.getBankAccountList().collect { state ->
                when (state) {
                    is State.Loading -> {
                        _uiState.update {
                            it.copy(
                                isLoading = true
                            )
                        }
                    }
                    is State.Success -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                bankAccountList = state.data.toMutableList(),
                                selectedBankAccount = if (state.data.isNotEmpty()) state.data[0] else null
                            )
                        }
                    }
                    is State.Failed -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = state.message
                            )
                        }
                        Log.e("error", state.message)
                    }
                }
            }
        }
    }

    data class PayOnlineUiState(
        val isLoading: Boolean = false,
        val error: String = "",
        val message: String = "",
        val bankAccountList: List<BankAccount> = listOf(),
        val selectedBankAccount: BankAccount? = null,
        val amount: Double? = null,
        val payByTokenResponse: PayByTokenResponse? = null
    )

}