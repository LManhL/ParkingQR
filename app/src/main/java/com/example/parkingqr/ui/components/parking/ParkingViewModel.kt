package com.example.parkingqr.ui.components.parking

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.parkingqr.data.remote.State
import com.example.parkingqr.data.repo.invoice.InvoiceRepository
import com.example.parkingqr.data.repo.monthlyticket.MonthlyTicketRepository
import com.example.parkingqr.data.repo.parkinglot.ParkingLotRepository
import com.example.parkingqr.data.repo.user.UserRepository
import com.example.parkingqr.data.repo.vehicle.VehicleRepository
import com.example.parkingqr.domain.model.invoice.ParkingInvoice
import com.example.parkingqr.domain.model.invoice.UserInvoice
import com.example.parkingqr.domain.model.parkinglot.BillingType
import com.example.parkingqr.domain.model.parkinglot.MonthlyTicket
import com.example.parkingqr.domain.model.qrcode.*
import com.example.parkingqr.domain.model.vehicle.VehicleInvoice
import com.example.parkingqr.ui.base.BaseViewModel
import com.example.parkingqr.utils.AESEncyptionUtil
import com.example.parkingqr.utils.ImageUtil
import com.example.parkingqr.utils.TextRecognizerUtil
import com.example.parkingqr.utils.TimeUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class ParkingViewModel @Inject constructor(
    private val invoiceRepository: InvoiceRepository,
    private val userRepository: UserRepository,
    private val vehicleRepository: VehicleRepository,
    private val parkingLotRepository: ParkingLotRepository,
    private val monthlyTicketRepository: MonthlyTicketRepository
) : BaseViewModel() {

    companion object {
        const val SEARCH_LICENSE_PLATE = "SEARCH_LICENSE_PLATE"
        const val SEARCH_USER = "SEARCH_USER"
        const val ADD_NEW_PARKING_INVOICE = "ADD_NEW_PARKING_INVOICE"
        const val VALIDATE_VEHICLE = "VALIDATE_VEHICLE"
    }

    private var addParkingInvoiceJob: Job? = null
    private var updateParkingInvoiceJob: Job? = null
    private var searchParkingInvoiceJob: Job? = null
    private val _stateUi = MutableStateFlow(
        ParkingViewModelState()
    )
    val stateUi: StateFlow<ParkingViewModelState> = _stateUi.asStateFlow()

    init {
        getBillingTypeList()
    }

    fun searchUserThenSearchTheVehicleFromQRCodeThenCreateParkingInvoice() {
        viewModelScope.launch {
            stateUi.value.qrcode?.let { qrcode ->
                (qrcode as UserQRCode).userId
            }?.let { id ->
                userRepository.searchUserInvoiceById(
                    id
                ).collect { state ->
                    when (state) {
                        is State.Loading -> {
                            _stateUi.update {
                                it.copy(isLoading = true)
                            }
                        }
                        is State.Success -> {
                            if (state.data.isNotEmpty()) {
                                _stateUi.update {
                                    it.copy(
                                        user = state.data[0]
                                    )
                                }
                                searchUserVehicleFromQRCodeThenCreateParkingInvoice()
                            } else {
                                // Khong tim thay user tuong ung -> Ma QR co van de
                                _stateUi.update {
                                    it.copy(
                                        isLoading = false,
                                        message = "Mã QR không hợp lệ"
                                    )
                                }
                            }
                        }
                        is State.Failed -> {
                            _stateUi.update {
                                it.copy(
                                    isLoading = false, errorMessage = it.errorMessage
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun searchUserVehicleFromQRCodeThenCreateParkingInvoice() {
        viewModelScope.launch {
            stateUi.value.qrcode?.let { qrcode ->
                (qrcode as UserQRCode).userId
            }?.let { id ->
                invoiceRepository.searchLicensePlateByUserId(
                    stateUi.value.licensePlateOfVehicleIn,
                    id
                ).collect { state ->
                    when (state) {
                        is State.Loading -> {
                            _stateUi.update {
                                it.copy(isLoading = true)
                            }
                        }
                        is State.Success -> {
                            if (state.data.isNotEmpty()) {
                                _stateUi.update {
                                    it.copy(
                                        isLoading = false,
                                        action = ParkingAction.CREATE_INVOICE_FOR_USER_REGISTER_VEHICLE,
                                        parkingInvoice = ParkingInvoice(
                                            id = invoiceRepository.getNewParkingInvoiceKey(),
                                            user = it.user ?: UserInvoice(),
                                            vehicle = state.data[0],
                                            imageIn = ImageUtil.encodeImage(
                                                (stateUi.value.bmVehicleIn ?: "0") as Bitmap
                                            ),
                                            timeIn = TimeUtil.getCurrentTime().toString()
                                        ),
                                    )
                                }
                            } else {
                                _stateUi.update {
                                    it.copy(
                                        parkingInvoice = createInvoiceForUserNotRegisterVehicle(),
                                        action = ParkingAction.CREATE_INVOICE_FOR_USER_NOT_REGISTER_VEHICLE,
                                    )
                                }
                            }
                        }
                        is State.Failed -> {
                            _stateUi.update {
                                it.copy(
                                    isLoading = false, errorMessage = it.errorMessage
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    fun createInvoiceForGuest() {
        _stateUi.update {
            it.copy(
                action = ParkingAction.CREATE_INVOICE_FOR_GUEST,
                parkingInvoice = ParkingInvoice(
                    id = invoiceRepository.getNewParkingInvoiceKey(),
                    user = UserInvoice(),
                    vehicle = VehicleInvoice(stateUi.value.licensePlateOfVehicleIn),
                    imageIn = ImageUtil.encodeImage(stateUi.value.bmVehicleIn!!),
                    timeIn = TimeUtil.getCurrentTime().toString()
                ),
            )
        }
    }

    private fun createInvoiceForUserNotRegisterVehicle(): ParkingInvoice {
        return ParkingInvoice(
            id = invoiceRepository.getNewParkingInvoiceKey(),
            user = stateUi.value.user ?: UserInvoice(),
            vehicle = VehicleInvoice(stateUi.value.licensePlateOfVehicleIn),
            imageIn = ImageUtil.encodeImage(stateUi.value.bmVehicleIn!!),
            timeIn = TimeUtil.getCurrentTime().toString()
        )
    }

    fun addNewParkingInvoice() {
        var available = false
        addParkingInvoiceJob?.cancel()
        addParkingInvoiceJob = viewModelScope.launch {
            flowOf(VALIDATE_VEHICLE, ADD_NEW_PARKING_INVOICE).flatMapConcat {
                when (it) {
                    VALIDATE_VEHICLE -> invoiceRepository.searchParkingInvoiceByLicensePlateAndStateParking(
                        stateUi.value.parkingInvoice?.vehicle?.licensePlate!!
                    )
                    else -> {
                        if (!available) {
                            userRepository.getLocalParkingLotId()?.let { parkingLotId ->
                                _stateUi.update { state ->
                                    state.copy(
                                        parkingInvoice = state.parkingInvoice?.apply {
                                            this.parkingLotId = parkingLotId
                                        }
                                    )
                                }
                            }
                            invoiceRepository.addNewParkingInvoice(stateUi.value.parkingInvoice!!)
                        } else {
                            flowOf()
                        }
                    }
                }
            }.collect { state ->
                when (state) {
                    is State.Loading -> {
                        _stateUi.update {
                            it.copy(isLoading = true)
                        }
                    }
                    is State.Success -> {
                        if (state.data is Boolean) {
                            available = state.data
                            if (available) {
                                _stateUi.update {
                                    it.copy(
                                        isLoading = false,
                                        message = "Xe đã được gửi"
                                    )
                                }
                            }
                        } else {
                            _stateUi.update {
                                it.copy(
                                    action = ParkingAction.REFRESH,
                                    message = "Tạo hóa đơn gửi xe thành công cho xe có biển số: ${it.parkingInvoice?.vehicle?.licensePlate}"
                                )
                            }
                        }
                    }
                    is State.Failed -> {
                        _stateUi.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = it.errorMessage
                            )
                        }
                    }
                }
            }
        }
    }

    fun refreshData() {
        _stateUi.update {
            it.copy(
                isLoading = false,
                errorMessage = "",
                message = "",
                user = null,
                vehicle = null,
                parkingInvoice = null,
                action = null,
                qrcode = null,
                licensePlateOfVehicleIn = "",
                licensePlateOfVehicleOut = "",
                bmVehicleIn = null,
                bmVehicleOut = null,
                billingTypeHashMap = mutableMapOf(),
                monthlyTicket = null
            )
        }
    }

    fun searchParkingInvoiceFromQRCode() {
        searchParkingInvoiceJob?.cancel()
        searchParkingInvoiceJob = viewModelScope.launch {
            stateUi.value.qrcode.let { qrCode ->
                (qrCode as InvoiceQRCode).invoiceId
            }?.let { id ->
                invoiceRepository.searchParkingInvoiceById(id).collect { state ->
                    when (state) {
                        is State.Loading -> {
                            _stateUi.update {
                                it.copy(isLoading = true)
                            }
                        }
                        is State.Success -> {
                            if (state.data.isNotEmpty()) {
                                if (state.data[0].state == "parked") {
                                    _stateUi.update {
                                        it.copy(
                                            isLoading = false,
                                            message = "Mã QR không hợp lệ"
                                        )
                                    }
                                } else {
                                    _stateUi.update {
                                        it.copy(
                                            isLoading = false,
                                            action = ParkingAction.PROCESS_TO_COMPLETE_PARKING_INVOICE,
                                            parkingInvoice = state.data[0],
                                        )
                                    }
                                }
                            } else {
                                _stateUi.update {
                                    it.copy(
                                        isLoading = false,
                                        message = "Mã QR không hợp lệ"
                                    )
                                }
                            }
                        }
                        is State.Failed -> {
                            _stateUi.update {
                                it.copy(
                                    isLoading = false, errorMessage = it.message
                                )
                            }
                        }
                    }
                }
            }
        }
    }


    fun completeParkingInvoice() {
        updateParkingInvoiceJob?.cancel()
        updateParkingInvoiceJob = viewModelScope.launch {
            invoiceRepository.completeParkingInvoice(stateUi.value.parkingInvoice!!)
                .collect { state ->
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
                                    action = ParkingAction.REFRESH,
                                    message = "Trả hóa đơn xe thành công cho xe có biển số: ${it.parkingInvoice?.vehicle?.licensePlate}"
                                )
                            }
                        }
                        is State.Failed -> {
                            _stateUi.update {
                                it.copy(
                                    isLoading = false,
                                    errorMessage = state.message
                                )
                            }
                        }
                    }
                }
        }
    }

    fun updateInvoiceOut(
        _note: String
    ) {
        _stateUi.update {
            it.copy(
                parkingInvoice = it.parkingInvoice?.apply {
                    imageOut = stateUi.value.bmVehicleOut?.let { bm ->
                        ImageUtil.encodeImage(bm)
                    }.toString()
                    note = _note
                    timeOut = TimeUtil.getCurrentTime().toString()
                    price = calculateInvoicePrice(this)
                }
            )
        }
    }

    fun updateInvoiceIn(_note: String) {
        _stateUi.update {
            it.copy(
                parkingInvoice = it.parkingInvoice?.apply {
                    note = _note
                }
            )
        }
    }

    fun getDataFromQRCode(result: String) {
        AESEncyptionUtil.decrypt(result)?.let {
            Log.e("bugggggg", it)
            QRCodeFactory.fromString(it)
        }?.let { qrcode ->
            when (qrcode) {
                is UserQRCode -> {
                    _stateUi.update {
                        it.copy(
                            action = ParkingAction.CREATE_INVOICE_FROM_USER_QR_CODE,
                            qrcode = qrcode
                        )
                    }
                }
                is InvoiceQRCode -> {
                    _stateUi.update {
                        it.copy(
                            action = ParkingAction.GET_INVOICE_FROM_QR_CODE,
                            qrcode = qrcode
                        )
                    }
                }
                is MonthlyTicketQRCode -> {
                    _stateUi.update {
                        it.copy(
                            action = ParkingAction.GET_MONTHLY_TICKET_FROM_QR_CODE,
                            qrcode = qrcode
                        )
                    }
                }
                else -> {
                    _stateUi.update {
                        it.copy(
                            message = "Mã QR không hợp lệ"
                        )
                    }
                }
            }
        }
    }

    fun processImageVehicleIn(bm: Bitmap) {
        TextRecognizerUtil.invoke(bm) { licensePlate ->
            _stateUi.update {
                it.copy(
                    licensePlateOfVehicleIn = licensePlate,
                    bmVehicleIn = bm
                )
            }
        }
    }

    fun processImageVehicleOut(bm: Bitmap) {
        TextRecognizerUtil.invoke(bm) { licensePlate ->
            _stateUi.update {
                it.copy(
                    licensePlateOfVehicleOut = licensePlate,
                    bmVehicleOut = bm
                )
            }
        }
    }

    fun isUnregisterVehicle(): Boolean {
        return stateUi.value.action == ParkingAction.CREATE_INVOICE_FOR_USER_NOT_REGISTER_VEHICLE || stateUi.value.action == ParkingAction.CREATE_INVOICE_FOR_GUEST
    }

    fun updateVehicleType(vehicleType: String) {
        _stateUi.update {
            it.copy(
                parkingInvoice = it.parkingInvoice?.apply {
                    vehicle.type = vehicleType
                }
            )
        }
    }

    fun calculateInvoicePrice(parkingInvoice: ParkingInvoice): Double {
        if (parkingInvoice.isMonthlyTicketType()) return 0.0
        return stateUi.value.billingTypeHashMap[parkingInvoice.vehicle.type]?.calculateInvoicePrice(
            parkingInvoice.timeIn,
            TimeUtil.getCurrentTime().toString()
        ) ?: 0.0
    }

    fun getMonthlyTicketFromQRCodeThenCheck() {
        viewModelScope.launch {
            stateUi.value.qrcode?.let { qrcode ->
                (qrcode as MonthlyTicketQRCode).monthlyTicketId.let { id ->
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
                                        isLoading = false,
                                        monthlyTicket = state.data,
                                    )
                                }
                                checkMonthlyTicketThenCreateParkingInvoice()
                            }
                            is State.Failed -> {
                                _stateUi.update {
                                    it.copy(
                                        isLoading = false,
                                        errorMessage = it.errorMessage
                                    )
                                }
                            }
                        }

                    }
                }
            }
        }
    }

    fun showError() {
        _stateUi.update {
            it.copy(
                errorMessage = ""
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

    private fun checkMonthlyTicketThenCreateParkingInvoice() {
        stateUi.value.monthlyTicket?.apply {
            // Xem còn hạn hay không?
            if (expiredAt.toLong() < TimeUtil.getCurrentTime()) {
                _stateUi.update {
                    it.copy(
                        errorMessage = "Vé đã hết hạn"
                    )
                }
                return
            }
            // Xem có đúng là của bãi gửi xe này hay không?
            if (userRepository.getLocalParkingLotId() != parkingLot.id) {
                _stateUi.update {
                    it.copy(
                        errorMessage = "Vé không hợp lệ"
                    )
                }
                return
            }
            _stateUi.update {
                it.copy(
                    action = ParkingAction.CREATE_INVOICE_FROM_MONTHLY_TICKET,
                    parkingInvoice = ParkingInvoice(
                        id = invoiceRepository.getNewParkingInvoiceKey(),
                        user = UserInvoice(
                            id = user.id,
                            userId = user.userId,
                            name = user.account.name,
                            phoneNumber = user.account.phoneNumber
                        ),
                        vehicle = VehicleInvoice(
                            id = vehicle.id ?: "",
                            userId = vehicle.userId ?: "",
                            licensePlate = vehicle.licensePlate ?: "",
                            state = vehicle.state ?: "",
                            brand = vehicle.brand ?: "",
                            type = vehicle.type ?: "",
                            color = vehicle.color ?: "",
                            ownerFullName = vehicle.ownerFullName ?: "",
                        ),
                        imageIn = ImageUtil.encodeImage(
                            (stateUi.value.bmVehicleIn ?: "") as Bitmap
                        ),
                        timeIn = TimeUtil.getCurrentTime().toString()
                    ).apply {
                        type = ParkingInvoice.MONTH_INVOICE_TYPE
                        paymentMethod = ParkingInvoice.VNPAY_PAYMENT_METHOD
                    },
                )
            }
        }
    }

    private fun getBillingTypeList() {
        userRepository.getLocalParkingLotId()?.let { parkingLotId ->
            viewModelScope.launch {
                parkingLotRepository.getBillingTypesByParkingLotId(parkingLotId).collect { state ->
                    when (state) {
                        is State.Loading -> {}
                        is State.Success -> {
                            _stateUi.update { viewModelState ->
                                viewModelState.copy(billingTypeHashMap = state.data.associateBy { it.vehicleType }
                                    .toMutableMap())
                            }
                        }
                        is State.Failed -> {
                            _stateUi.update {
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


    data class ParkingViewModelState(
        val isLoading: Boolean = false,
        val errorMessage: String = "",
        val message: String = "",
        val user: UserInvoice? = null,
        val vehicle: VehicleInvoice? = null,
        val parkingInvoice: ParkingInvoice? = null,
        val action: ParkingAction? = null,
        val errorList: MutableMap<ParkingAction, String> = hashMapOf(),
        val messageList: MutableMap<ParkingAction, String> = hashMapOf(),
        val qrcode: QRCode? = null,
        val licensePlateOfVehicleIn: String = "",
        val licensePlateOfVehicleOut: String = "",
        val bmVehicleIn: Bitmap? = null,
        val bmVehicleOut: Bitmap? = null,
        val timeLimit: Int = 5,
        val billingTypeHashMap: MutableMap<String, BillingType> = mutableMapOf(),
        val monthlyTicket: MonthlyTicket? = null
    ) {
        init {
            errorList[ParkingAction.CREATE_INVOICE_FOR_USER_NOT_REGISTER_VEHICLE] =
                "Không tìm thấy phương tiện tương ứng có biển số"

            messageList[ParkingAction.CREATE_INVOICE_FOR_USER_REGISTER_VEHICLE] =
                "Tìm thấy phương tiện tương ứng có biển số"
            messageList[ParkingAction.PROCESS_TO_COMPLETE_PARKING_INVOICE] =
                "Tìm thấy hóa đơn xe có biển số"

        }
    }

    enum class ParkingAction {
        REFRESH,
        CREATE_INVOICE_FOR_USER_REGISTER_VEHICLE, CREATE_INVOICE_FOR_USER_NOT_REGISTER_VEHICLE, CREATE_INVOICE_FOR_GUEST,
        PROCESS_TO_COMPLETE_PARKING_INVOICE, GET_INVOICE_FROM_QR_CODE, CREATE_INVOICE_FROM_USER_QR_CODE, GET_MONTHLY_TICKET_FROM_QR_CODE, CREATE_INVOICE_FROM_MONTHLY_TICKET
    }
}