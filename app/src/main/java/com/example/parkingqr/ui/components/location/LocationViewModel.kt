package com.example.parkingqr.ui.components.location

import androidx.lifecycle.viewModelScope
import com.example.parkingqr.data.remote.State
import com.example.parkingqr.data.repo.invoice.InvoiceRepository
import com.example.parkingqr.data.repo.parkinglot.ParkingLotRepository
import com.example.parkingqr.domain.model.invoice.ParkingInvoice
import com.example.parkingqr.domain.model.parkinglot.ParkingLot
import com.example.parkingqr.domain.model.parkinglot.Rate
import com.example.parkingqr.domain.model.vehicle.VehicleInvoice
import com.example.parkingqr.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocationViewModel @Inject constructor(
    private val parkingLotRepository: ParkingLotRepository,
    private val invoiceRepository: InvoiceRepository
) :
    BaseViewModel() {

    private val _uiState = MutableStateFlow(
        LocationUiState()
    )
    val uiState: StateFlow<LocationUiState> = _uiState.asStateFlow()

    init {
        getParkingLotList()
    }

    private fun getParkingLotList() {
        viewModelScope.launch {
            parkingLotRepository.getParkingLotList().collect { state ->
                when (state) {
                    is State.Loading -> {
                        _uiState.update {
                            it.copy(isLoading = true)
                        }
                    }
                    is State.Success -> {
                        _uiState.update {
                            it.copy(
                                parkingLotList = state.data,
                                isLoading = false
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

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getParkingLotInfo(parkingLotId: String) {
        viewModelScope.launch {
            // Loading
            _uiState.update {
                it.copy(
                    isLoading = true
                )
            }
            // Get data
            parkingLotRepository.getParkingLotRates(parkingLotId).flatMapMerge { stateRate ->
                parkingLotRepository.getParkingLotById(parkingLotId).flatMapMerge { stateDetail ->
                    invoiceRepository.getAllPendingInvoiceByParkingLotId(parkingLotId)
                        .flatMapMerge { stateInvoiceList ->
                            flow {
                                emit(Triple(stateRate, stateDetail, stateInvoiceList))
                            }
                        }
                }
            }.collect { (stateRate, stateDetail, stateInvoiceList) ->
                if (stateRate is State.Success && stateDetail is State.Success && stateInvoiceList is State.Success) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            rates = stateRate.data,
                            parkingLotDetail = stateDetail.data,
                            pendingInvoiceList = stateInvoiceList.data.toMutableList(),
                            isShowParkingLotDetail = true
                        )
                    }
                }
                if (stateRate is State.Failed || stateDetail is State.Failed || stateInvoiceList is State.Failed) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = "Lỗi không xác định"
                        )
                    }
                }
            }
        }
    }

    fun searchParkingLotByName(name: String) {
        viewModelScope.launch {
            parkingLotRepository.searchParkingLotByName(name).collect { state ->
                when (state) {
                    is State.Loading -> {}
                    is State.Success -> {
                        _uiState.update {
                            it.copy(
                                suggestParkingLotList = state.data.toMutableList(),
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

    fun clearSuggestList(){
        _uiState.update {
            it.copy(
                suggestParkingLotList = mutableListOf()
            )
        }
    }
    fun getPendingMotor(): Int {
        return uiState.value.pendingInvoiceList.filter { item ->
            item.vehicle.type == VehicleInvoice.MOTORBIKE_TYPE
        }.size
    }


    fun getPendingCar(): Int {
        return uiState.value.pendingInvoiceList.filter { item ->
            item.vehicle.type == VehicleInvoice.CAR_TYPE
        }.size
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

    fun showParkingLotDetail() {
        _uiState.update {
            it.copy(
                isShowParkingLotDetail = false
            )
        }
    }

    data class LocationUiState(
        val isLoading: Boolean = false,
        val error: String = "",
        val message: String = "",
        val parkingLotList: MutableList<ParkingLot> = mutableListOf(),
        val rates: MutableList<Rate> = mutableListOf(),
        val parkingLotDetail: ParkingLot = ParkingLot(),
        val pendingInvoiceList: MutableList<ParkingInvoice> = mutableListOf(),
        val isShowParkingLotDetail: Boolean = false,
        val suggestParkingLotList: MutableList<ParkingLot> = mutableListOf()
    )
}