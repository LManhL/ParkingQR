package com.example.parkingqr.ui.components.location

import com.example.parkingqr.data.repo.parkinglot.ParkingLotRepository
import com.example.parkingqr.domain.model.parkinglot.BillingType
import com.example.parkingqr.domain.model.parkinglot.MonthlyTicket
import com.example.parkingqr.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class RegisterMonthlyInvoiceViewModel @Inject constructor(parkingLotRepository: ParkingLotRepository) :
    BaseViewModel() {

    private val _uiState = MutableStateFlow(BillingTypeDetailUiState())
    val uiState = _uiState.asStateFlow()


    fun getMonthlyTicketList() {
        _uiState.update {
            it.copy(
                monthlyTicketList = it.monthlyTicketList.apply {
                    repeat(5) {
                        add(
                            MonthlyTicket(
                                "0",
                                1.0,
                                10000.0,
                                50000.0,
                                "Thoải mái gửi xe miễn phí 24/7 không giới hạn thời gian"
                            )
                        )
                    }
                }
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

    fun showError() {
        _uiState.update {
            it.copy(
                error = ""
            )
        }
    }


    data class BillingTypeDetailUiState(
        val isLoading: Boolean = false,
        val error: String = "",
        val parkingLotId: String = "",
        val monthlyTicketList: MutableList<MonthlyTicket> = mutableListOf()
    )
}