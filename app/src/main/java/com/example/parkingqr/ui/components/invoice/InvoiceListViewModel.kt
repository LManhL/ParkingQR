package com.example.parkingqr.ui.components.invoice

import androidx.lifecycle.viewModelScope
import com.example.parkingqr.data.remote.State
import com.example.parkingqr.domain.invoice.ParkingInvoiceIV
import com.example.parkingqr.ui.base.BaseFragment
import com.example.parkingqr.ui.base.BaseViewModel
import com.example.parkingqr.ui.components.parking.ParkingViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class InvoiceListViewModel: BaseViewModel() {
    private val _stateUi = MutableStateFlow(
        InvoiceListViewModelState()
    )
    val stateUi: StateFlow<InvoiceListViewModelState> = _stateUi.asStateFlow()
    private var getInvoiceListJob: Job? = null

    init {
        getParkingInvoiceList("")
    }

    fun getParkingInvoiceList(id: String) {
        getInvoiceListJob?.cancel()
        getInvoiceListJob = viewModelScope.launch {
            repository.getParkingInvoiceList(id).collect { state ->
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


    data class InvoiceListViewModelState(
        val isLoading: Boolean = false,
        val error: String = "",
        val invoiceList: MutableList<ParkingInvoiceIV> = mutableListOf()
    ){
        init {

        }
    }
}