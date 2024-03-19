package com.example.parkingqr.ui.components.qrcode

import androidx.lifecycle.viewModelScope
import com.example.parkingqr.data.remote.State
import com.example.parkingqr.data.repo.invoice.InvoiceRepository
import com.example.parkingqr.data.repo.user.UserRepository
import com.example.parkingqr.domain.model.invoice.ParkingInvoice
import com.example.parkingqr.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserQRCodeListViewModel @Inject constructor(
    private val invoiceRepository: InvoiceRepository,
    private val userRepository: UserRepository
) : BaseViewModel() {
    private val _stateUi = MutableStateFlow(
        UserQRCodeListUiState()
    )
    val stateUi: StateFlow<UserQRCodeListUiState> = _stateUi.asStateFlow()

    private var getParkingInvoiceListJob: Job? = null

    init {
        getParkingInvoiceList()
    }

    fun getParkingInvoiceList() {
        getParkingInvoiceListJob?.cancel()
        getParkingInvoiceListJob = viewModelScope.launch {
            invoiceRepository.getUserInvoiceListHaveParkingState().collect { state ->
                when (state) {
                    is State.Loading -> {
                        _stateUi.update {
                            it.copy(isLoading = true)
                        }
                    }
                    is State.Success -> {
                        _stateUi.update {
                            it.copy(
                                invoiceList = state.data,
                                isLoading = false
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
                    }
                }
            }
        }
    }

    fun getUserID() {
        viewModelScope.launch {
            userRepository.getUserId().collect { state ->
                when (state) {
                    is State.Loading -> {
                        _stateUi.update {
                            it.copy(isLoading = true)
                        }
                    }
                    is State.Success -> {
                        _stateUi.update {
                            it.copy(
                                userId = state.data,
                                isShowDialog = true,
                                isLoading = false
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
                    }
                }

            }
        }
    }

    fun showDialog(){
        _stateUi.update {
            it.copy(
                isShowDialog = false
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


    data class UserQRCodeListUiState(
        val isLoading: Boolean = false,
        val error: String = "",
        val message: String = "",
        val invoiceList: MutableList<ParkingInvoice> = mutableListOf(),
        val userId: String = "",
        val isShowDialog: Boolean = false
    )
}