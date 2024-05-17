package com.example.parkingqr.ui.components.qrcode

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.parkingqr.data.remote.State
import com.example.parkingqr.data.repo.debt.DebtRepository
import com.example.parkingqr.data.repo.invoice.InvoiceRepository
import com.example.parkingqr.data.repo.monthlyticket.MonthlyTicketRepository
import com.example.parkingqr.data.repo.parkinglot.ParkingLotRepository
import com.example.parkingqr.data.repo.payment.PaymentRepository
import com.example.parkingqr.data.repo.user.UserRepository
import com.example.parkingqr.domain.model.debt.InvoiceDebt
import com.example.parkingqr.domain.model.invoice.ParkingInvoice
import com.example.parkingqr.domain.model.invoice.WaitingRate
import com.example.parkingqr.domain.model.parkinglot.MonthlyTicket
import com.example.parkingqr.domain.model.payment.BankAccount
import com.example.parkingqr.domain.model.user.User
import com.example.parkingqr.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import okhttp3.internal.wait
import javax.inject.Inject

@HiltViewModel
class UserQRCodeListViewModel @Inject constructor(
    private val invoiceRepository: InvoiceRepository,
    private val userRepository: UserRepository,
    private val monthlyTicketRepository: MonthlyTicketRepository,
    private val debtRepository: DebtRepository,
    private val parkingLotRepository: ParkingLotRepository
) : BaseViewModel() {
    private val _stateUi = MutableStateFlow(
        UserQRCodeListUiState()
    )
    val stateUi: StateFlow<UserQRCodeListUiState> = _stateUi.asStateFlow()

    private var getParkingInvoiceListJob: Job? = null


    fun payInvoiceDebt() {
        stateUi.value.invoiceDebt?.let { invoiceDebt ->
            viewModelScope.launch {
                debtRepository.payDebtInvoice(invoiceDebt).collect { state ->
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
                                    invoiceDebt = null
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

    fun getUserInvoiceDebt() {
        viewModelScope.launch {
            debtRepository.getUserUnpaidDebtInvoice().collect { state ->
                when (state) {
                    is State.Loading -> {
                        _stateUi.update {
                            it.copy(
                                invoiceDebt = null
                            )
                        }
                        Log.e("CHECK", "LOADING")
                    }
                    is State.Success -> {
                        _stateUi.update {
                            it.copy(
                                invoiceDebt = state.data
                            )
                        }
                        Log.e("CHECK", "SUCCESS")
                    }
                    is State.Failed -> {
                        _stateUi.update {
                            it.copy(
                                invoiceDebt = null,
                            )
                        }
                        Log.e("CHECK", "FAIL")
                    }
                }

            }
        }
    }

    fun getParkingInvoiceList() {
        getParkingInvoiceListJob?.cancel()
        getParkingInvoiceListJob = viewModelScope.launch {
            invoiceRepository.getUserInvoiceListHaveParkingState().collect { state ->
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
                                invoiceList = state.data,
                                isHideUserDialog = true,
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

    fun updatePaymentMethod(parkingInvoice: ParkingInvoice) {
        viewModelScope.launch {
            invoiceRepository.updateInvoicePaymentMethod(parkingInvoice).collect { state ->
                when (state) {
                    is State.Loading -> {
                        _stateUi.update {
                            it.copy(isLoading = true)
                        }
                    }
                    is State.Success -> {
                        _stateUi.update {
                            it.copy(
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

    fun getMonthlyTicketListAndSelectedId() {
        viewModelScope.launch {
            monthlyTicketRepository.getCurrentUserMonthlyTicketList().collect { state ->
                when (state) {
                    is State.Loading -> {
                        _stateUi.update {
                            it.copy(isLoading = true)
                        }
                    }
                    is State.Success -> {
                        _stateUi.update {
                            it.copy(
                                monthLyTicketList = state.data,
                                isLoading = false
                            )
                        }
                        getSelectedMonthlyTicketId()
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

    fun getSelectedMonthlyTicketId() {
        monthlyTicketRepository.getSelectedMonthlyTicketId().let { value ->
            if (value == null) {
                stateUi.value.monthLyTicketList.takeIf { it.isNotEmpty() }?.let { list ->
                    setSelectedMonthlyTicketId(list.first().id)
                }
            } else {
                _stateUi.update {
                    it.copy(
                        selectedMonthlyTicketId = value
                    )
                }
            }
        }
    }

    fun setSelectedMonthlyTicketId(monthlyTicketId: String) {
        monthlyTicketRepository.setSelectedMonthlyTicketId(monthlyTicketId)
        _stateUi.update {
            it.copy(
                selectedMonthlyTicketId = monthlyTicketId
            )
        }
    }

    fun handleToShowQRCode() {
        viewModelScope.launch {
            userRepository.getCurrentUserInfo().collect { state ->
                when (state) {
                    is State.Loading -> {
                        _stateUi.update {
                            it.copy(isLoading = true)
                        }
                    }
                    is State.Success -> {
                        _stateUi.update {
                            it.copy(
                                user = state.data,
                                isShowUserDialog = true,
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

    fun getWaitingRatesToShow() {
        viewModelScope.launch {
            invoiceRepository.getUnShowedWaitingRates().collect { state ->
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
                                waitingRateList = state.data
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

    fun sendWaitingRate(rate: Int, comment: String, waitingRate: WaitingRate) {
        waitingRate.apply {
            this.rate = rate.toDouble()
            this.comment = comment
        }.let {
            viewModelScope.launch {
                delay(100)
                invoiceRepository.deleteWaitingRateById(waitingRate.id).collect()
                parkingLotRepository.createRate(waitingRate).collect { state ->
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
                                    message = "Cảm ơn bạn đã hoàn thành đánh giá"
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

    fun createWaitingRate() {
        stateUi.value.invoiceDebt?.parkingInvoice?.let { parkingInvoice ->
            viewModelScope.launch {
                invoiceRepository.createWaitingRate(parkingInvoice).collect { state ->
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
                                    isLoading = false
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
    }

    fun activeUser() {
        viewModelScope.launch {
            stateUi.value.user?.id?.let {
                userRepository.activeUser(it).collect()
            }
        }
    }


    fun showDialog() {
        _stateUi.update {
            it.copy(
                isShowUserDialog = false,
                isShowMonthlyTicketDialog = false
            )
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

    fun hideDialog() {
        _stateUi.update {
            it.copy(
                isHideUserDialog = false
            )
        }
    }

    data class UserQRCodeListUiState(
        val isLoading: Boolean = false,
        val error: String = "",
        val message: String = "",
        val invoiceList: MutableList<ParkingInvoice> = mutableListOf(),
        val user: User? = null,
        val isShowUserDialog: Boolean = false,
        val isShowMonthlyTicketDialog: Boolean = false,
        val isHideUserDialog: Boolean = false,
        val monthLyTicketList: MutableList<MonthlyTicket> = mutableListOf(),
        val selectedMonthlyTicketId: String = "",
        val selectedMonthlyTicket: MonthlyTicket? = null,
        val invoiceDebt: InvoiceDebt? = null,
        val bankAccountList: MutableList<BankAccount> = mutableListOf(),
        val selectedBankAccount: BankAccount? = null,
        val waitingRateList: List<WaitingRate> = listOf()
    )
}