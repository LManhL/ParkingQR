package com.example.parkingqr.ui.components.home

import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.example.parkingqr.data.remote.State
import com.example.parkingqr.data.repo.parkinglot.ParkingLotRepository
import com.example.parkingqr.data.repo.user.UserRepository
import com.example.parkingqr.domain.model.parkinglot.ParkingLot
import com.example.parkingqr.domain.model.user.ParkingLotManager
import com.example.parkingqr.ui.base.BaseViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val parkingLotRepository: ParkingLotRepository
) :
    BaseViewModel() {

    private val _uiState = MutableStateFlow(
        HomeUiState()
    )
    val uiState = _uiState.asStateFlow()

    fun deleteParkingLotByIdThenUpdateParkingLotManager() {
        viewModelScope.launch {
            uiState.value.parkingLot?.id?.let { parkingLotId ->
                parkingLotRepository.deleteParkingLotById(parkingLotId).collect { state ->
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
                                    parkingLot = null,
                                    isDeletedParkingLot = true
                                )
                            }
                            updateParkingLotIdForParkingLotManager("")
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


    fun createParkingLotThenUpdateParkingLotManager(
        name: String,
        address: String,
        phone: String,
        area: String,
        carCapacity: String,
        motorCapacity: String,
        description: String,
        images: List<Uri>,
        latLng: LatLng
    ) {
        viewModelScope.launch {
            ParkingLot(
                name = name,
                phoneNumber = phone,
                address = address,
                area = area.toDoubleOrNull() ?: 0.0,
                carCapacity = carCapacity.toDoubleOrNull() ?: 0.0,
                motorCapacity = motorCapacity.toDoubleOrNull() ?: 0.0,
                description = description,
                images = images.map { it.toString() }.toMutableList(),
                location = latLng,
                status = ParkingLot.ParkingLotStatus.PENDING_STATUS
            ).let { parkingLot ->
                parkingLotRepository.createParkingLot(parkingLot).collect { state ->
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
                                )
                            }
                            updateParkingLotIdForParkingLotManager(state.data)
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

    fun getParkingLotManagerToCheck() {
        viewModelScope.launch {
            userRepository.getParkingLotManager().collect { state ->
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
                        checkIfHaveValidParkingLotThenSaveParkingLotId(state.data)
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

    fun resetIsDeletedPara() {
        _uiState.update {
            it.copy(
                isDeletedParkingLot = false
            )
        }
    }
    fun resetIsCreatedPara() {
        _uiState.update {
            it.copy(
                isCreatedParkingLot = false
            )
        }
    }
    fun showMessage(){
        _uiState.update {
            it.copy(
                message = ""
            )
        }
    }

    fun setCurMarker(marker: Marker){
        _uiState.update {
            it.copy(
                curMarker = marker
            )
        }
    }

    private fun checkIfHaveValidParkingLotThenSaveParkingLotId(parkingLotManager: ParkingLotManager) {
        // Chưa tạo bãi gửi xe
        if (parkingLotManager.parkingLotId.isEmpty()) {
            _uiState.update {
                it.copy(
                    isNeedToCreateParkingLot = true
                )
            }
        }
        // Đã tạo bãi gửi xe
        else {
            // Kiểm tra bãi gửi xe được phê duyệt hay bị từ chối hay đang chờ phê duyệt
            getParkingLotByIdToCheckIfValidateThenStoreId(parkingLotManager.parkingLotId)
        }
    }

    private fun getParkingLotByIdToCheckIfValidateThenStoreId(parkingLotId: String) {
        viewModelScope.launch {
            parkingLotRepository.getParkingLotById(parkingLotId).collect { state ->
                when (state) {
                    is State.Loading -> {
                        _uiState.update {
                            it.copy(
                                isLoading = true
                            )
                        }
                    }
                    is State.Success -> {
                        val parkingLot = state.data

                        if (parkingLot.status == ParkingLot.ParkingLotStatus.ACCEPTED_STATUS) {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    parkingLot = parkingLot
                                )
                            }
                            // Lưu Id bãi gửi xe
                            storeParkingLotId(parkingLot)
                        } else {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    parkingLot = parkingLot,
                                    isNeedToCreateParkingLot = true
                                )
                            }
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

    private fun updateParkingLotIdForParkingLotManager(parkingLotId: String) {
        viewModelScope.launch {
            uiState.value.parkingLotManager?.id?.let { parkingLotManagerId ->
                userRepository.updateParkingLotIdForParkingLotManager(
                    parkingLotManagerId,
                    parkingLotId
                ).collect { state ->
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
                                    isCreatedParkingLot = true
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

    private fun storeParkingLotId(parkingLot: ParkingLot) {
        viewModelScope.launch {
            userRepository.saveLocalParkingLotId(parkingLot.id)
        }
    }


    data class HomeUiState(
        val isLoading: Boolean = false,
        val message: String = "",
        val error: String = "",
        val parkingLotManager: ParkingLotManager? = null,
        val isNeedToCreateParkingLot: Boolean = false,
        val parkingLot: ParkingLot? = null,
        val isCreatedParkingLot: Boolean = false,
        val isDeletedParkingLot: Boolean = false,
        val curMarker: Marker? = null
    )
}