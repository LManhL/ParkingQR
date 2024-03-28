package com.example.parkingqr.ui.components.invoice

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.parkingqr.data.remote.State
import com.example.parkingqr.data.repo.invoice.InvoiceRepository
import com.example.parkingqr.data.repo.parkinglot.ParkingLotRepository
import com.example.parkingqr.data.repo.user.UserRepository
import com.example.parkingqr.domain.model.invoice.ParkingInvoice
import com.example.parkingqr.domain.model.parkinglot.BillingType
import com.example.parkingqr.ui.base.BaseViewModel
import com.example.parkingqr.utils.TimeUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InvoiceListViewModel @Inject constructor(
    private val invoiceRepository: InvoiceRepository,
    private val parkingLotRepository: ParkingLotRepository,
    private val userRepository: UserRepository
) : BaseViewModel() {
    private val _stateUi = MutableStateFlow(
        InvoiceListViewModelState()
    )
    val stateUi: StateFlow<InvoiceListViewModelState> = _stateUi.asStateFlow()
    private var getInvoiceListJob: Job? = null
    private var searchInvoiceJob: Job? = null

    init {
        getBillingTypeList()
    }

    fun getParkingInvoiceList() {
        getInvoiceListJob?.cancel()
        getInvoiceListJob = viewModelScope.launch {
            userRepository.getLocalParkingLotId()?.let { parkingLotId ->
                invoiceRepository.getParkingLotInvoiceList(parkingLotId).collect { state ->
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
    }

    fun searchParkingInvoice(licensePlate: String) {
        searchInvoiceJob?.cancel()
        searchInvoiceJob = viewModelScope.launch {
            userRepository.getLocalParkingLotId()?.let { parkingLotId ->
                invoiceRepository.searchParkingInvoiceParkingLot(licensePlate, parkingLotId)
                    .collect { state ->
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
                                        invoiceList = state.data,
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
    }

    fun calculateInvoicePrice(parkingInvoice: ParkingInvoice): Double {
        if (parkingInvoice.state == ParkingInvoice.PARKED_STATE_TYPE || parkingInvoice.state == ParkingInvoice.REFUSED_STATE_TYPE) return parkingInvoice.price
        return stateUi.value.billingTypeHashMap[parkingInvoice.vehicle.type]?.calculateInvoicePrice(
            parkingInvoice.timeIn,
            TimeUtil.getCurrentTime().toString()
        ) ?: 0.0
    }

    private fun getBillingTypeList() {
        userRepository.getLocalParkingLotId()?.let { parkingLotId ->
            viewModelScope.launch {
                parkingLotRepository.getBillingTypesByParkingLotId(parkingLotId).collect { state ->
                    when (state) {
                        is State.Loading -> {}
                        is State.Success -> {
                            _stateUi.update { viewModelState ->
                                viewModelState.copy(billingTypeHashMap = state.data.associateBy { it.vehicleType }
                                    .toMutableMap())
                            }
                        }
                        is State.Failed -> {
                            _stateUi.update {
                                it.copy(
                                    error = state.message
                                )
                            }
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


    data class InvoiceListViewModelState(
        val isLoading: Boolean = false,
        val error: String = "",
        val invoiceList: MutableList<ParkingInvoice> = mutableListOf(),
        val billingTypeHashMap: MutableMap<String, BillingType> = mutableMapOf()
    )
}