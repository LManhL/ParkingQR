package com.example.parkingqr.ui.components.location

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.parkingqr.data.remote.State
import com.example.parkingqr.data.repo.monthlyticket.MonthlyTicketRepository
import com.example.parkingqr.data.repo.parkinglot.ParkingLotRepository
import com.example.parkingqr.data.repo.payment.PaymentRepository
import com.example.parkingqr.data.repo.user.UserRepository
import com.example.parkingqr.data.repo.vehicle.VehicleRepository
import com.example.parkingqr.domain.model.parkinglot.MonthlyTicket
import com.example.parkingqr.domain.model.parkinglot.MonthlyTicketType
import com.example.parkingqr.domain.model.parkinglot.ParkingLot
import com.example.parkingqr.domain.model.payment.BankAccount
import com.example.parkingqr.domain.model.payment.PayByTokenResponse
import com.example.parkingqr.domain.model.user.User
import com.example.parkingqr.domain.model.vehicle.VehicleDetail
import com.example.parkingqr.ui.base.BaseViewModel
import com.example.parkingqr.utils.TimeUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterMonthlyInvoiceViewModel @Inject constructor(
    private val parkingLotRepository: ParkingLotRepository,
    private val vehicleRepository: VehicleRepository,
    private val userRepository: UserRepository,
    private val monthlyTicketRepository: MonthlyTicketRepository,
    private val paymentRepository: PaymentRepository
) :
    BaseViewModel() {

    private val _uiState = MutableStateFlow(BillingTypeDetailUiState())
    val uiState = _uiState.asStateFlow()

    init {
        getBankAccountList()
    }

    fun getMonthlyTicketListByVehicleType() {
        uiState.value.selectedVehicle?.let { vehicle ->
            viewModelScope.launch {
                parkingLotRepository.getMonthlyTicketByVehicleType(
                    uiState.value.parkingLotId,
                    vehicle.type ?: ""
                ).collect { state ->
                    when (state) {
                        is State.Loading -> {
                            _uiState.update {
                                it.copy(isLoading = true)
                            }
                        }
                        is State.Success -> {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    monthlyTicketTypeList = state.data
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

    fun getRegistrationList() {
        viewModelScope.launch {
            vehicleRepository.getVehicleRegistrationList().collect { state ->
                when (state) {
                    is State.Loading -> {
                        _uiState.update {
                            it.copy(isLoading = true)
                        }
                    }
                    is State.Success -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                registrationList = state.data
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

    fun getInfoToCreateMonthlyTicket() {
        _uiState.update {
            it.copy(
                isLoading = true
            )
        }
        viewModelScope.launch {
            userRepository.getCurrentUserInfo()
                .zip(parkingLotRepository.getParkingLotById(uiState.value.parkingLotId)) { stateUser, stateParkingLot ->
                    if (stateUser is State.Success && stateParkingLot is State.Success) {
                        _uiState.update {
                            it.copy(
                                user = stateUser.data,
                                parkingLot = stateParkingLot.data,
                            )
                        }
                    } else if (stateUser is State.Failed || stateParkingLot is State.Failed) {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = "Lỗi không xác định"
                            )
                        }
                    }
                }.collect()
        }
    }

    fun createMonthlyTicket() {
        viewModelScope.launch {
            uiState.value.let { state ->
                MonthlyTicket(
                    parkingLot = state.parkingLot ?: ParkingLot(),
                    vehicle = state.selectedVehicle ?: VehicleDetail(),
                    user = state.user ?: User(),
                    monthTicketType = state.selectedMonthlyTicketType ?: MonthlyTicketType(),
                    createAt = TimeUtil.getCurrentTime().toString(),
                    expiredAt = TimeUtil.getTimeAfterNumberOfMonth(
                        state.selectedMonthlyTicketType?.numberOfMonth ?: 0.0
                    ).toString()
                ).let { monthlyTicket ->
                    monthlyTicketRepository.createMonthlyTicket(monthlyTicket).collect { state ->
                        when (state) {
                            is State.Loading -> {}
                            is State.Success -> {
                                _uiState.update {
                                    it.copy(
                                        isLoading = false,
                                        isCreated = state.data
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
    }


    fun selectVehicle(vehicleDetail: VehicleDetail) {
        _uiState.update {
            it.copy(
                selectedVehicle = vehicleDetail
            )
        }
    }

    fun selectMonthlyTicket(monthlyTicketType: MonthlyTicketType) {
        _uiState.update {
            it.copy(
                selectedMonthlyTicketType = monthlyTicketType
            )
        }
    }

    fun setParkingLotId(parkingLotId: String) {
        _uiState.update {
            it.copy(
                parkingLotId = parkingLotId
            )
        }
    }

    fun payByToken() {
        uiState.value.apply {
            val price = selectedMonthlyTicketType?.promotionalPrice
            val token = selectedBankAccount?.token
            if (price == null || token == null) return
            viewModelScope.launch {
                paymentRepository.payByToken(price, token)
                    .collect { state ->
                        when (state) {
                            is State.Loading -> {
                                _uiState.update {
                                    it.copy(isLoading = true)
                                }
                            }
                            is State.Success -> {
                                _uiState.update {
                                    it.copy(
                                        isLoading = false,
                                        payByTokenResponse = state.data
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

    fun handlePaymentSuccessful() {
        _uiState.update {
            it.copy(
                isPaymentSuccessful = true
            )
        }
    }

    private fun getBankAccountList() {
        viewModelScope.launch {
            paymentRepository.getBankAccountList().collect { state ->
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
                                bankAccountList = state.data.toMutableList()
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
                        Log.e("error", state.message)
                    }
                }
            }
        }
    }

    fun showError() {
        _uiState.update {
            it.copy(
                error = ""
            )
        }
    }

    fun showPaymentPage() {
        _uiState.update {
            it.copy(
                payByTokenResponse = null
            )
        }
    }

    fun selectBankAccount(bankAccount: BankAccount) {
        _uiState.update {
            it.copy(
                selectedBankAccount = bankAccount
            )
        }
    }


    data class BillingTypeDetailUiState(
        val isLoading: Boolean = false,
        val error: String = "",
        val parkingLotId: String = "",
        val monthlyTicketTypeList: MutableList<MonthlyTicketType> = mutableListOf(),
        val registrationList: MutableList<VehicleDetail> = mutableListOf(),
        val selectedVehicle: VehicleDetail? = null,
        val selectedMonthlyTicketType: MonthlyTicketType? = null,
        val parkingLot: ParkingLot? = null,
        val user: User? = null,
        val isPaymentSuccessful: Boolean = false,
        val isCreated: Boolean = false,
        val payByTokenResponse: PayByTokenResponse? = null,
        val bankAccountList: List<BankAccount> = listOf(),
        val selectedBankAccount: BankAccount? = null,
    )
}