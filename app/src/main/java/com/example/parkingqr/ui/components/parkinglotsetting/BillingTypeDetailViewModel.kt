package com.example.parkingqr.ui.components.parkinglotsetting

import androidx.lifecycle.viewModelScope
import com.example.parkingqr.data.remote.State
import com.example.parkingqr.data.repo.parkinglot.ParkingLotRepository
import com.example.parkingqr.data.repo.user.UserRepository
import com.example.parkingqr.domain.model.parkinglot.BillingType
import com.example.parkingqr.ui.base.BaseViewModel
import com.example.parkingqr.utils.FormatCurrencyUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BillingTypeDetailViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val parkingLotRepository: ParkingLotRepository
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(
        BillingTypeDetailUiState()
    )
    val uiState = _uiState.asStateFlow()

    init {
        getBillingTypeList()
    }

    private fun getBillingTypeList() {
        userRepository.getLocalParkingLotId()?.let { parkingLotId ->
            viewModelScope.launch {
                parkingLotRepository.getBillingTypesByParkingLotId(parkingLotId).collect { state ->
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
                                    billingTypeList = state.data,
                                    chooseItem = state.data.takeIf { data -> data.isNotEmpty() }
                                        ?.let { billingTypes -> billingTypes[0] }
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

    fun getBillingTypeFromTypeReadable(vehicleTypeReadable: String) {
        uiState.value.billingTypeList.firstOrNull {
            it.getVehicleTypeReadable() == vehicleTypeReadable
        }?.let { item ->
            _uiState.update {
                it.copy(
                    chooseItem = item,
                    isLoading = true
                )
            }
        }
        viewModelScope.launch {
            delay(200)
            _uiState.update {
                it.copy(
                    isLoading = false
                )
            }
        }
    }

    fun getUpdateInfoFromView(
        firstBlockPrice: String,
        afterFirstBlockPrice: String,
        firstBlock: String,
        roundedMinutesToOneHour: String,
        nightSurcharge: String,
        startDaylightTime: String,
        endDaylightTime: String,
        startNightTime: String,
        endNightTime: String,
        surcharge: String
    ) {
        _uiState.update {
            it.copy(
                chooseItem = it.chooseItem?.apply {
                    this.firstBlockPrice = FormatCurrencyUtil.convertFormatToNumber(firstBlockPrice)
                    this.afterFirstBlockPrice =
                        FormatCurrencyUtil.convertFormatToNumber(afterFirstBlockPrice)
                    this.firstBlock = firstBlock.toDouble()
                    this.roundedMinutesToOneHour = roundedMinutesToOneHour.toDouble()
                    this.nightSurcharge = FormatCurrencyUtil.convertFormatToNumber(nightSurcharge)
                    this.startDaylightTime = startDaylightTime
                    this.endDaylightTime = endDaylightTime
                    this.startNightTime = startNightTime
                    this.endNightTime = endNightTime
                    this.surcharge = FormatCurrencyUtil.convertFormatToNumber(surcharge)
                }
            )
        }
    }

    fun updateBillingType() {
        viewModelScope.launch {
            uiState.value.chooseItem?.let { billingType ->
                parkingLotRepository.updateBillingType(
                    userRepository.getLocalParkingLotId().toString(), billingType
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
                                    isUpdated = state.data
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

    fun createBillingType() {
        viewModelScope.launch {
            val billingTypeList = mutableListOf(
                BillingType.createBillingTypeForMotor(),
                BillingType.createBillingTypeForCar()
            )
            parkingLotRepository.createBillingType(
                userRepository.getLocalParkingLotId().toString(),
                billingTypeList[0]
            ).zip(
                parkingLotRepository.createBillingType(
                    userRepository.getLocalParkingLotId().toString(),
                    billingTypeList[1]
                )
            ) { state1, state2 ->
                if (state1 is State.Success && state2 is State.Success) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                        )
                    }
                    getBillingTypeList()
                } else if (state1 is State.Failed && state2 is State.Failed) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = state1.message + state2.message
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                        )
                    }
                }
            }.collect()
        }
    }

    data class BillingTypeDetailUiState(
        val isLoading: Boolean = false,
        val error: String = "",
        val message: String = "",
        val billingTypeList: MutableList<BillingType> = mutableListOf(),
        val chooseItem: BillingType? = null,
        val isUpdated: Boolean = false,
    )
}