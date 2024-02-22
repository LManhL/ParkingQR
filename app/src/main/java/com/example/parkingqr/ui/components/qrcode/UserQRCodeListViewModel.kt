package com.example.parkingqr.ui.components.qrcode

import androidx.lifecycle.viewModelScope
import com.example.parkingqr.data.remote.State
import com.example.parkingqr.data.repo.invoice.InvoiceRepository
import com.example.parkingqr.domain.model.invoice.ParkingInvoice
import com.example.parkingqr.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserQRCodeListViewModel @Inject constructor(private val repository: InvoiceRepository): BaseViewModel() {
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
            repository.getUserInvoiceListHaveParkingState().collect { state ->
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

    fun showError(){
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
        val invoiceList: MutableList<ParkingInvoice> = mutableListOf()
    )
}