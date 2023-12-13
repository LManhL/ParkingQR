package com.example.parkingqr.ui.components.invoice

import androidx.lifecycle.viewModelScope
import com.example.parkingqr.data.remote.State
import com.example.parkingqr.domain.invoice.ParkingInvoiceIV
import com.example.parkingqr.ui.base.BaseViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class InvoiceDetailViewModel : BaseViewModel() {
    private val _stateUi = MutableStateFlow(
        InvoiceDetailViewModelState()
    )
    val stateUi: StateFlow<InvoiceDetailViewModelState> = _stateUi.asStateFlow()
    private var getInvoiceJob: Job? = null


    fun getInvoiceById(id: String) {
        getInvoiceJob?.cancel()
        getInvoiceJob = viewModelScope.launch {
            repository.getParkingInvoiceById(id).collect { state ->
                when (state) {
                    is State.Loading -> {
                        _stateUi.update {
                            it.copy(isLoading = true)
                        }
                    }
                    is State.Success -> {
                        _stateUi.update {
                            it.copy(
                                invoice = state.data[0],
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

    fun showError() {
        _stateUi.update {
            it.copy(
                error = ""
            )
        }
    }

    data class InvoiceDetailViewModelState(
        val isLoading: Boolean = false,
        val error: String = "",
        val invoice: ParkingInvoiceIV? = null
    ) {
        init {

        }
    }
}