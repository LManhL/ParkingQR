package com.example.parkingqr.ui.components.qrcode

import androidx.lifecycle.viewModelScope
import com.example.parkingqr.data.remote.State
import com.example.parkingqr.data.repo.invoice.InvoiceRepository
import com.example.parkingqr.data.repo.monthlyticket.MonthlyTicketRepository
import com.example.parkingqr.data.repo.user.UserRepository
import com.example.parkingqr.domain.model.invoice.ParkingInvoice
import com.example.parkingqr.domain.model.parkinglot.MonthlyTicket
import com.example.parkingqr.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserQRCodeListViewModel @Inject constructor(
    private val invoiceRepository: InvoiceRepository,
    private val userRepository: UserRepository,
    private val monthlyTicketRepository: MonthlyTicketRepository
) : BaseViewModel() {
    private val _stateUi = MutableStateFlow(
        UserQRCodeListUiState()
    )
    val stateUi: StateFlow<UserQRCodeListUiState> = _stateUi.asStateFlow()

    private var getParkingInvoiceListJob: Job? = null

    init {
        getParkingInvoiceList()
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
                                invoiceList = state.data,
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

    fun getUserIdToShowDialog() {
        viewModelScope.launch {
            userRepository.getUserId().collect { state ->
                when (state) {
                    is State.Loading -> {
                        _stateUi.update {
                            it.copy(isLoading = true)
                        }
                    }
                    is State.Success -> {
                        _stateUi.update {
                            it.copy(
                                userId = state.data,
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

    fun getIsShowMonthlyTicket() {
        monthlyTicketRepository.getIsShowMonthlyTicket().let { isShowMonthlyTicket ->
            _stateUi.update {
                it.copy(
                    isShowMonthlyTicket = isShowMonthlyTicket
                )
            }
        }
    }

    fun setIsShowMonthlyTicket(isShow: Boolean) {
        stateUi.value.monthLyTicketList.let { list ->
            if (list.isNotEmpty()) {
                monthlyTicketRepository.setIsShowMonthlyTicket(isShow)
                _stateUi.update {
                    it.copy(
                        isShowMonthlyTicket = isShow
                    )
                }
            } else {
                _stateUi.update {
                    it.copy(
                        message = "Xin vui lòng đăng ký vé tháng trước"
                    )
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
        monthlyTicketRepository.getIsShowMonthlyTicket().let { isShowMonthlyTicket ->
            if (isShowMonthlyTicket) {
                monthlyTicketRepository.getSelectedMonthlyTicketId()?.let {
                    getMonthlyTicketByIdToShowDialog(it)
                }
            } else {
                getUserIdToShowDialog()
            }
        }
    }

    fun getMonthlyTicketByIdToShowDialog(id: String) {
        viewModelScope.launch {
            monthlyTicketRepository.getMonthlyTicketById(id).collect { state ->
                when (state) {
                    is State.Loading -> {
                        _stateUi.update {
                            it.copy(isLoading = true)
                        }
                    }
                    is State.Success -> {
                        _stateUi.update {
                            it.copy(
                                selectedMonthlyTicket = state.data,
                                isLoading = false,
                                isShowMonthlyTicketDialog = true
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


    data class UserQRCodeListUiState(
        val isLoading: Boolean = false,
        val error: String = "",
        val message: String = "",
        val invoiceList: MutableList<ParkingInvoice> = mutableListOf(),
        val userId: String = "",
        val isShowUserDialog: Boolean = false,
        val isShowMonthlyTicketDialog: Boolean = false,
        val monthLyTicketList: MutableList<MonthlyTicket> = mutableListOf(),
        val isShowMonthlyTicket: Boolean = false,
        val selectedMonthlyTicketId: String = "",
        val selectedMonthlyTicket: MonthlyTicket? = null
    )
}