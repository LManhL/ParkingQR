package com.example.parkingqr.ui.components.securitycamera

import androidx.lifecycle.viewModelScope
import com.example.parkingqr.data.remote.State
import com.example.parkingqr.data.repo.parkinglot.ParkingLotRepository
import com.example.parkingqr.data.repo.user.UserRepository
import com.example.parkingqr.domain.model.parkinglot.SecurityCamera
import com.example.parkingqr.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SecurityCameraViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val parkingLotRepository: ParkingLotRepository
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(SecurityCameraUiState())
    val uiState = _uiState.asStateFlow()

    init {
        getCameras()
    }

    fun addCamera(uri: String) {
        viewModelScope.launch {
            userRepository.getLocalParkingLotId()?.let { parkingLotId ->
                parkingLotRepository.addCamera(
                    parkingLotId,
                    SecurityCamera(uri = uri, type = SecurityCamera.TYPE.TYPE_CAME_NORMAL)
                ).collect { state ->
                    if (state is State.Success) {
                        _uiState.update {
                            it.copy(
                                message = "Thêm thành công"
                            )
                        }
                    }
                }
            }
        }
    }

    fun deleteCamera(securityCamera: SecurityCamera) {
        viewModelScope.launch {
            userRepository.getLocalParkingLotId()?.let { parkingLotId ->
                parkingLotRepository.deleteCameraById(parkingLotId, securityCamera.id)
                    .collect { state ->
                        _uiState.update {
                            it.copy(
                                message = "Xóa thành công"
                            )
                        }
                    }
            }
        }
    }

    private fun getCameras() {
        viewModelScope.launch {
            userRepository.getLocalParkingLotId()?.let { parkingLotId ->
                parkingLotRepository.getAllCameras(parkingLotId).collect { state ->
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
                                    cameras = state.data.filter { item -> item.uri.isNotEmpty() }
                                )
                            }
                        }

                        is State.Failed -> {
                            _uiState.update {
                                it.copy(
                                    errorMessage = state.message
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
}

data class SecurityCameraUiState(
    val isLoading: Boolean = false,
    val message: String = "",
    val errorMessage: String = "",
    val cameras: List<SecurityCamera> = listOf()
)