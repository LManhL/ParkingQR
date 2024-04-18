package com.example.parkingqr.ui.components.userprofile

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.parkingqr.data.remote.State
import com.example.parkingqr.data.repo.payment.PaymentRepository
import com.example.parkingqr.domain.model.payment.BankAccount
import com.example.parkingqr.domain.model.payment.CreateTokenResponse
import com.example.parkingqr.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConnectBankAccountViewModel @Inject constructor(private val paymentRepository: PaymentRepository) :
    BaseViewModel() {

    private val _stateUi = MutableStateFlow(ConnectBankAccountState())
    val stateUi = _stateUi.asStateFlow()

    init {
        getBankAccountList()
    }

    fun createToken() {
        viewModelScope.launch {
            paymentRepository.connectBankAccount().collect { state ->
                when (state) {
                    is State.Loading -> {
                        _stateUi.update {
                            it.copy(
                                isLoading = true
                            )
                        }
                    }
                    is State.Success -> {
                        _stateUi.update {
                            it.copy(
                                isLoading = false,
                                createTokenResponse = state.data
                            )
                        }
                    }
                    is State.Failed -> {
                        _stateUi.update {
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

    fun getBankAccountList() {
        viewModelScope.launch {
            paymentRepository.getBankAccountList().collect { state ->
                when (state) {
                    is State.Loading -> {
                        _stateUi.update {
                            it.copy(
                                isLoading = true
                            )
                        }
                    }
                    is State.Success -> {
                        _stateUi.update {
                            it.copy(
                                isLoading = false,
                                bankAccountList = state.data.toMutableList(),
                            )
                        }
                    }
                    is State.Failed -> {
                        _stateUi.update {
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

    fun showSdk() {
        _stateUi.update {
            it.copy(
                createTokenResponse = null
            )
        }
    }

    fun showError() {
        _stateUi.update {
            it.copy(
                error = ""
            )
        }
    }

    fun showMessage() {
        _stateUi.update {
            it.copy(
                message = ""
            )
        }
    }

    data class ConnectBankAccountState(
        val isLoading: Boolean = false,
        val error: String = "",
        val message: String = "",
        val createTokenResponse: CreateTokenResponse? = null,
        val bankAccountList: MutableList<BankAccount> = mutableListOf()
    )
}