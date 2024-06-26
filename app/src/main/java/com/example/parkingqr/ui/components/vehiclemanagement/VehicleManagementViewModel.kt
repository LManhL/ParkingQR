package com.example.parkingqr.ui.components.vehiclemanagement

import androidx.lifecycle.viewModelScope
import com.example.parkingqr.data.remote.State
import com.example.parkingqr.data.repo.auth.AuthRepository
import com.example.parkingqr.data.repo.vehicle.VehicleRepository
import com.example.parkingqr.domain.model.vehicle.VehicleDetail
import com.example.parkingqr.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VehicleManagementViewModel @Inject constructor(private val vehicleRepository: VehicleRepository, private val authRepository: AuthRepository) :
    BaseViewModel() {
    private val _stateUi = MutableStateFlow(VehicleRegistrationListState())
    val stateUi = _stateUi.asStateFlow()
    private var getListJob: Job? = null

    init {
        getVehicleList()
    }

    fun getVehicleList() {
        getListJob?.cancel()
        getListJob = viewModelScope.launch {
            vehicleRepository.getAllVehicle().collect { state ->
                when (state) {
                    is State.Loading -> {
                        _stateUi.update {
                            it.copy(isLoading = true)
                        }
                    }
                    is State.Success -> {
                        _stateUi.update {
                            it.copy(
                                isLoading = false,
                                registrationList = state.data
                            )
                        }
                    }
                    is State.Failed -> {
                        _stateUi.update {
                            it.copy(
                                isLoading = false,
                                error = it.message
                            )
                        }
                    }
                }
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut().collect { state ->
                when (state) {
                    is State.Loading -> {
                        _stateUi.update {
                            it.copy(isLoading = true)
                        }
                    }
                    is State.Success -> {
                        _stateUi.update {
                            it.copy(
                                isLoading = false,
                            )
                        }
                    }
                    is State.Failed -> {
                        _stateUi.update {
                            it.copy(
                                isLoading = false,
                                error = "Lỗi không xác định"
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

    fun showMessage() {
        _stateUi.update {
            it.copy(
                message = ""
            )
        }
    }

    data class VehicleRegistrationListState(
        val isLoading: Boolean = false,
        val error: String = "",
        val message: String = "",
        val registrationList: MutableList<VehicleDetail> = mutableListOf()
    )
}