package com.example.parkingqr.ui.components.parkinglotsetting

import androidx.lifecycle.viewModelScope
import com.example.parkingqr.data.remote.State
import com.example.parkingqr.data.repo.parkinglot.ParkingLotRepository
import com.example.parkingqr.data.repo.user.UserRepository
import com.example.parkingqr.domain.model.parkinglot.MonthlyTicketType
import com.example.parkingqr.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MonthlyTicketTypeViewModel @Inject constructor(
    private val parkingLotRepository: ParkingLotRepository,
    private val userRepository: UserRepository
) :
    BaseViewModel() {

    private val _uiState = MutableStateFlow(
        MonthlyTicketTypeUiState()
    )
    val uiState = _uiState.asStateFlow()

    fun getMonthlyTicketTypeList() {
        viewModelScope.launch {
            userRepository.getLocalParkingLotId()?.let { parkingLotId ->
                parkingLotRepository.getAllMonthlyTicketTypeList(parkingLotId).collect { state ->
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
                                    monthlyTicketList = state.data
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

    fun updateMonthlyTicketType(monthlyTicketType: MonthlyTicketType) {
        viewModelScope.launch {
            userRepository.getLocalParkingLotId()?.let { parkingLotId ->
                parkingLotRepository.updateMonthlyTicketType(parkingLotId, monthlyTicketType)
                    .collect { state ->
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
                                        message = "Cập nhật thành công"
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

    fun createMonthlyTicketType(monthlyTicketType: MonthlyTicketType) {
        viewModelScope.launch {
            userRepository.getLocalParkingLotId()?.let { parkingLotId ->
                parkingLotRepository.createMonthlyTicketType(parkingLotId, monthlyTicketType)
                    .collect { state ->
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
                                        message = "Thêm thành công"
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

    fun deleteMonthlyTicketType(monthlyTicketType: MonthlyTicketType) {
        viewModelScope.launch {
            userRepository.getLocalParkingLotId()?.let { parkingLotId ->
                parkingLotRepository.deleteMonthlyTicketType(parkingLotId, monthlyTicketType)
                    .collect { state ->
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
                                        message = "Xoá thành công"
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

    fun showMessage() {
        _uiState.update {
            it.copy(
                message = ""
            )
        }
    }

    data class MonthlyTicketTypeUiState(
        val isLoading: Boolean = false,
        val error: String = "",
        val message: String = "",
        val monthlyTicketList: List<MonthlyTicketType> = listOf()
    )
}