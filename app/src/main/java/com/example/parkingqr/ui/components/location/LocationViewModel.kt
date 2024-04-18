package com.example.parkingqr.ui.components.location

import androidx.lifecycle.viewModelScope
import com.example.parkingqr.data.remote.State
import com.example.parkingqr.data.repo.parkinglot.ParkingLotRepository
import com.example.parkingqr.domain.model.parkinglot.ParkingLot
import com.example.parkingqr.domain.model.parkinglot.Rate
import com.example.parkingqr.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocationViewModel @Inject constructor(private val repository: ParkingLotRepository): BaseViewModel() {

    private val _stateUi = MutableStateFlow(
        LocationUiState()
    )
    val stateUi: StateFlow<LocationUiState> = _stateUi.asStateFlow()

    init {
        getParkingLotList()
    }
    private fun getParkingLotList(){
        viewModelScope.launch {
            repository.getParkingLotList().collect { state ->
                when (state) {
                    is State.Loading -> {
                        _stateUi.update {
                            it.copy(isLoading = true)
                        }
                    }
                    is State.Success -> {
                        _stateUi.update {
                            it.copy(
                                parkingLotList = state.data,
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

    fun getParkingLotRateList(parkingLotId: String){
        viewModelScope.launch {
            repository.getParkingLotRates(parkingLotId).collect { state ->
                when (state) {
                    is State.Loading -> {
                        _stateUi.update {
                            it.copy(isLoading = true)
                        }
                    }
                    is State.Success -> {
                        _stateUi.update {
                            it.copy(
                                rates = state.data,
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

    fun getParkingLotDetail(id: String){
        viewModelScope.launch {
            repository.getParkingLotById(id).collect { state ->
                when (state) {
                    is State.Loading -> {
                        _stateUi.update {
                            it.copy(isLoading = true)
                        }
                    }
                    is State.Success -> {
                        _stateUi.update {
                            it.copy(
                                parkingLotDetail = state.data,
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
    fun showMessage(){
        _stateUi.update {
            it.copy(
                message = ""
            )
        }
    }

    data class LocationUiState(
        val isLoading: Boolean = false,
        val error: String = "",
        val message: String = "",
        val parkingLotList: MutableList<ParkingLot> = mutableListOf(),
        val rates: MutableList<Rate> = mutableListOf(),
        val parkingLotDetail: ParkingLot = ParkingLot()
    )
}