package com.example.parkingqr.ui.components.parking

import android.graphics.Bitmap
import androidx.lifecycle.viewModelScope
import com.example.parkingqr.data.remote.State
import com.example.parkingqr.data.repo.debt.DebtRepository
import com.example.parkingqr.data.repo.invoice.InvoiceRepository
import com.example.parkingqr.data.repo.monthlyticket.MonthlyTicketRepository
import com.example.parkingqr.data.repo.parkinglot.ParkingLotRepository
import com.example.parkingqr.data.repo.user.UserRepository
import com.example.parkingqr.data.repo.vehicle.VehicleRepository
import com.example.parkingqr.domain.model.invoice.ParkingInvoice
import com.example.parkingqr.domain.model.invoice.UserInvoice
import com.example.parkingqr.domain.model.parkinglot.BillingType
import com.example.parkingqr.domain.model.qrcode.*
import com.example.parkingqr.domain.model.user.Account
import com.example.parkingqr.domain.model.user.User
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
    private val monthlyTicketRepository: MonthlyTicketRepository,
    private val debtRepository: DebtRepository
) : BaseViewModel() {

    companion object {
        const val ADD_NEW_PARKING_INVOICE = "ADD_NEW_PARKING_INVOICE"
        const val VALIDATE_VEHICLE = "VALIDATE_VEHICLE"
    }

    private var addParkingInvoiceJob: Job? = null
    private var searchParkingInvoiceJob: Job? = null
    private val _stateUi = MutableStateFlow(
        ParkingViewModelState()
    )
    val stateUi: StateFlow<ParkingViewModelState> = _stateUi.asStateFlow()

    init {
        getBillingTypeList()
    }

    fun getDataFromQRCodeToCreateParkingInvoice() {
        viewModelScope.launch {
            stateUi.value.qrcode?.let { qrcode ->
                (qrcode as UserQRCode).userId
            }?.let { userIdFromQRCode ->
                userRepository.getUserById(
                    userIdFromQRCode
                ).collect { state ->
                    when (state) {
                        is State.Loading -> {
                            _stateUi.update {
                                it.copy(isLoading = true)
                            }
                        }
                        is State.Success -> {
                            val foundUser = state.data

                            if (foundUser.account.status == Account.BLOCKED_STATUS) {
                                _stateUi.update {
                                    it.copy(
                                        isLoading = false,
                                        message = "Người dùng bị chặn",
                                    )
                                }
                            } else {
                                _stateUi.update {
                                    it.copy(
                                        userInvoice = UserInvoice().apply {
                                            id = foundUser.id
                                            userId = foundUser.userId
                                            name = foundUser.account.name
                                            phoneNumber = foundUser.account.phoneNumber
                                        },
                                        userDetail = foundUser
                                    )
                                }
                                findVehicleToCreateParkingInvoice(foundUser)
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
    }

    private suspend fun findVehicleToCreateParkingInvoice(user: User) {
        vehicleRepository.getVerifiedVehiclesOfUser(user.userId).collect { state ->
            when (state) {
                is State.Loading -> {
                    _stateUi.update {
                        it.copy(isLoading = true)
                    }
                }
                is State.Success -> {

                    val licensePlateOfVehicleIn = stateUi.value.licensePlateOfVehicleIn
                    val foundVehicle =
                        state.data.firstOrNull { it.licensePlate == licensePlateOfVehicleIn }

                    // Tìm thấy xe có biển số giống xe vào

                    if (foundVehicle != null) {

                        // Tìm kiếm xem xe có đăng ký vé tháng không
                        findValidMonthlyTickerByVehicleIdToCreateInvoice(foundVehicle)

                        // Nếu xe không đăng ký vé tháng
                        if (stateUi.value.action != ParkingAction.SHOW_INVOICE_CREATE_FROM_MONTHLY_TICKET) {
                            // Gửi xe cho phương tiện có đăng ký
                            _stateUi.update {
                                it.copy(
                                    isLoading = false,
                                    action = ParkingAction.SHOW_INVOICE_FOR_USER_REGISTER_VEHICLE,
                                    parkingInvoice = ParkingInvoice(
                                        id = invoiceRepository.getNewParkingInvoiceKey(),
                                        user = it.userInvoice ?: UserInvoice(),
                                        vehicle = foundVehicle,
                                        imageIn = ImageUtil.encodeImage(
                                            (stateUi.value.bmVehicleIn ?: "0") as Bitmap
                                        ),
                                        timeIn = TimeUtil.getCurrentTime().toString()
                                    )
                                )
                            }
                        }
                    }
                    // Không tìm thấy xe nào có biển số giống xe vào -> Gửi xe cho phương tiện chưa đăng ký
                    else {
                        _stateUi.update {
                            it.copy(
                                isLoading = false,
                                parkingInvoice = createInvoiceForUserNotRegisterVehicle(),
                                action = ParkingAction.SHOW_INVOICE_FOR_USER_NOT_REGISTER_VEHICLE,
                                vehicleList = state.data
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

    fun createInvoiceForGuest() {
        _stateUi.update {
            it.copy(
                action = ParkingAction.SHOW_INVOICE_FOR_GUEST,
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
            user = stateUi.value.userInvoice ?: UserInvoice(),
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
                userInvoice = null,
                vehicle = null,
                parkingInvoice = null,
                action = null,
                qrcode = null,
                licensePlateOfVehicleIn = "",
                licensePlateOfVehicleOut = "",
                bmVehicleIn = null,
                bmVehicleOut = null,
                vehicleList = listOf()
            )
        }
    }

    fun getParkingInvoiceFromQRCode() {
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

                            val foundInvoice = state.data

                            if (foundInvoice.state == ParkingInvoice.PARKED_STATE_TYPE) {
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
                                        parkingInvoice = foundInvoice,
                                    )
                                }
                            }
                        }
                        is State.Failed -> {
                            _stateUi.update {
                                it.copy(
                                    isLoading = false,
                                    errorMessage = it.message
                                )
                            }
                        }
                    }
                }
            }
        }
    }


    fun completeParkingInvoice() {
        stateUi.value.parkingInvoice?.let { parkingInvoice ->
            if (parkingInvoice.isOnlinePayment()) {
                completeParkingInvoiceForOnlinePayment(parkingInvoice)
            } else {
                completeParkingInvoiceForOtherPayment(parkingInvoice)
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
        return stateUi.value.action == ParkingAction.SHOW_INVOICE_FOR_USER_NOT_REGISTER_VEHICLE || stateUi.value.action == ParkingAction.SHOW_INVOICE_FOR_GUEST
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

    fun handleSelectVehicle(vehicle: VehicleInvoice) {
        viewModelScope.launch {

            findValidMonthlyTickerByVehicleIdToCreateInvoice(vehicle)

            // Nếu xe không đăng ký vé tháng
            if (stateUi.value.action != ParkingAction.SHOW_INVOICE_CREATE_FROM_MONTHLY_TICKET) {
                // Gửi xe cho phương tiện có đăng ký
                _stateUi.update {
                    it.copy(
                        isLoading = false,
                        action = ParkingAction.SHOW_INVOICE_FOR_USER_REGISTER_VEHICLE,
                        parkingInvoice = ParkingInvoice(
                            id = invoiceRepository.getNewParkingInvoiceKey(),
                            user = it.userInvoice ?: UserInvoice(),
                            vehicle = vehicle,
                            imageIn = ImageUtil.encodeImage(
                                (stateUi.value.bmVehicleIn ?: "0") as Bitmap
                            ),
                            timeIn = TimeUtil.getCurrentTime().toString()
                        )
                    )
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

    private suspend fun findValidMonthlyTickerByVehicleIdToCreateInvoice(vehicle: VehicleInvoice) {
        monthlyTicketRepository.getValidMonthlyTicketByVehicleId(vehicle.id).collect { state ->
            if (state is State.Success) {
                state.data.apply {
                    _stateUi.update {
                        it.copy(
                            isLoading = false,
                            action = ParkingAction.SHOW_INVOICE_CREATE_FROM_MONTHLY_TICKET,
                            parkingInvoice = ParkingInvoice(
                                id = invoiceRepository.getNewParkingInvoiceKey(),
                                user = UserInvoice(
                                    id = user.id,
                                    userId = user.userId,
                                    name = user.account.name,
                                    phoneNumber = user.account.phoneNumber
                                ),
                                vehicle = vehicle,
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
        }
    }

    private fun completeParkingInvoiceForOtherPayment(parkingInvoice: ParkingInvoice) {
        viewModelScope.launch {
            invoiceRepository.completeParkingInvoice(parkingInvoice).zip(
                invoiceRepository.createWaitingRate(parkingInvoice)
            ) { invoiceState, waitingRateState ->
                if (invoiceState is State.Success && waitingRateState is State.Success) {
                    State.success(true)
                } else if (invoiceState is State.Failed || waitingRateState is State.Failed) {
                    State.failed("Lỗi không xác định")
                } else {
                    State.loading()
                }
            }.collect { state ->
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

    private fun completeParkingInvoiceForOnlinePayment(parkingInvoice: ParkingInvoice) {
        viewModelScope.launch {
            invoiceRepository.completeParkingInvoice(parkingInvoice).zip(
                debtRepository.createDebtInvoice(parkingInvoice.apply {
                    imageOut = ""
                    state = ParkingInvoice.PARKED_STATE_TYPE
                })
            ) { invoiceState, debState ->
                if (invoiceState is State.Success && debState is State.Success) {
                    userRepository.blockUser(parkingInvoice.user.id).collect()
                    State.success(true)
                } else if (invoiceState is State.Failed || debState is State.Failed) {
                    State.failed<String>("Vui lòng thử lại")
                } else {
                    State.loading()
                }
            }.collect { state ->
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
                                action = ParkingAction.REFRESH,
                                message = "Trả hóa đơn xe thành công cho xe có biển số: ${it.parkingInvoice?.vehicle?.licensePlate}"
                            )
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

    private fun getBillingTypeList() {
        userRepository.getLocalParkingLotId()?.let { parkingLotId ->
            viewModelScope.launch {
                parkingLotRepository.getBillingTypesByParkingLotId(parkingLotId).collect { state ->
                    when (state) {
                        is State.Loading -> {
                            _stateUi.update {
                                it.copy(
                                    isLoading = true
                                )
                            }
                        }
                        is State.Success -> {
                            _stateUi.update { viewModelState ->
                                viewModelState.copy(
                                    billingTypeHashMap = state.data.associateBy { it.vehicleType }
                                        .toMutableMap(),
                                    isLoading = false
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
    }


    data class ParkingViewModelState(
        val isLoading: Boolean = false,
        val errorMessage: String = "",
        val message: String = "",
        val userInvoice: UserInvoice? = null,
        val userDetail: User? = null,
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
        val timeLimit: Int = 3,
        val billingTypeHashMap: MutableMap<String, BillingType> = mutableMapOf(),
        val vehicleList: List<VehicleInvoice> = listOf()
    ) {
        init {
            errorList[ParkingAction.SHOW_INVOICE_FOR_USER_NOT_REGISTER_VEHICLE] =
                "Không tìm thấy phương tiện tương ứng có biển số"
            messageList[ParkingAction.SHOW_INVOICE_FOR_USER_REGISTER_VEHICLE] =
                "Tìm thấy phương tiện tương ứng có biển số"
            messageList[ParkingAction.PROCESS_TO_COMPLETE_PARKING_INVOICE] =
                "Tìm thấy hóa đơn xe có biển số"

        }
    }

    enum class ParkingAction {
        REFRESH,
        SHOW_INVOICE_FOR_USER_REGISTER_VEHICLE, SHOW_INVOICE_FOR_USER_NOT_REGISTER_VEHICLE, SHOW_INVOICE_FOR_GUEST,
        PROCESS_TO_COMPLETE_PARKING_INVOICE, GET_INVOICE_FROM_QR_CODE, CREATE_INVOICE_FROM_USER_QR_CODE, SHOW_INVOICE_CREATE_FROM_MONTHLY_TICKET
    }
}