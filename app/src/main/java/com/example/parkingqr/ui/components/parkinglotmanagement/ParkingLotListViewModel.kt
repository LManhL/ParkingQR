package com.example.parkingqr.ui.components.parkinglotmanagement

import androidx.lifecycle.viewModelScope
import com.example.parkingqr.data.remote.State
import com.example.parkingqr.data.repo.parkinglot.ParkingLotRepository
import com.example.parkingqr.data.repo.user.UserRepository
import com.example.parkingqr.domain.model.parkinglot.ParkingLot
import com.example.parkingqr.domain.model.user.ParkingLotManager
import com.example.parkingqr.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ParkingLotListViewModel @Inject constructor(
    private val parkingLotRepository: ParkingLotRepository,
    private val userRepository: UserRepository
) :
    BaseViewModel() {

    private val _uiState = MutableStateFlow(
        ParkingLotListUiState()
    )
    val uiState = _uiState.asStateFlow()

    init {
        getParkingLotList()
    }

    private fun getParkingLotList() {
        viewModelScope.launch {
            parkingLotRepository.getParkingLotList().collect { state ->
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
                                parkingLotList = state.data.toList()
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

    fun selectItem(parkingLot: ParkingLot) {
        _uiState.update {
            it.copy(
                selectedParkingLot = parkingLot
            )
        }
    }

    fun getParkingLotManager() {
        uiState.value.selectedParkingLot?.let { parkingLot ->
            viewModelScope.launch {
                userRepository.getParkingLotManagerByParkingLotId(parkingLot.id).collect { state ->
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
                                    parkingLotManager = state.data
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

    fun acceptParkingLot() {
        viewModelScope.launch {
            uiState.value.selectedParkingLot?.id?.let { id ->
                parkingLotRepository.acceptParkingLotById(id).collect { state ->
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
                                    isUpdated = true
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

    fun declineParkingLot() {
        viewModelScope.launch {
            uiState.value.selectedParkingLot?.id?.let { id ->
                parkingLotRepository.declineParkingLotById(id).collect { state ->
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
                                    isUpdated = true
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

    fun showUpdate() {
        _uiState.update {
            it.copy(
                isUpdated = false
            )
        }
    }


    data class ParkingLotListUiState(
        val isLoading: Boolean = false,
        val error: String = "",
        val message: String = "",
        val parkingLotList: List<ParkingLot> = listOf(),
        val selectedParkingLot: ParkingLot? = null,
        val parkingLotManager: ParkingLotManager? = null,
        val isUpdated: Boolean = false
    )
}
