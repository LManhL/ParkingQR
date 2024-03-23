package com.example.parkingqr.ui.components.parking

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.parkingqr.data.remote.State
import com.example.parkingqr.data.repo.invoice.InvoiceRepository
import com.example.parkingqr.data.repo.user.UserRepository
import com.example.parkingqr.data.repo.vehicle.VehicleRepository
import com.example.parkingqr.domain.model.invoice.ParkingInvoice
import com.example.parkingqr.domain.model.invoice.UserInvoice
import com.example.parkingqr.domain.model.qrcode.InvoiceQRCode
import com.example.parkingqr.domain.model.qrcode.QRCode
import com.example.parkingqr.domain.model.qrcode.UserQRCode
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
    private val vehicleRepository: VehicleRepository
) : BaseViewModel() {

    companion object {
        const val SEARCH_LICENSE_PLATE = "SEARCH_LICENSE_PLATE"
        const val SEARCH_USER = "SEARCH_USER"
        const val ADD_NEW_PARKING_INVOICE = "ADD_NEW_PARKING_INVOICE"
        const val VALIDATE_VEHICLE = "VALIDATE_VEHICLE"
    }

    private var validateVehicleJob: Job? = null
    private var searchVehicleJob: Job? = null
    private var addParkingInvoiceJob: Job? = null
    private var updateParkingInvoiceJob: Job? = null
    private var searchParkingInvoiceJob: Job? = null
    private val _stateUi = MutableStateFlow(
        ParkingViewModelState()
    )
    val stateUi: StateFlow<ParkingViewModelState> = _stateUi.asStateFlow()
    fun searchUserByIdThenSearchVehicle() {
        val userId = stateUi.value.qrcode?.let { qrcode ->
            (qrcode as UserQRCode).userId
        }
        viewModelScope.launch {
            userRepository.searchUserInvoiceById(
                userId ?: ""
            ).collect { state ->
                when (state) {
                    is State.Loading -> {
                        _stateUi.update {
                            it.copy(state = ParkingState.LOADING)
                        }
                    }
                    is State.Success -> {
                        if (state.data.isNotEmpty()) {
                            _stateUi.update {
                                it.copy(
                                    user = state.data[0]
                                )
                            }
                            _searchVehicleAndUserByUserId()
                        } else {
                            // Ma QR khong hop le
                        }
                    }
                    is State.Failed -> {
                        _stateUi.update {
                            it.copy(
                                state = ParkingState.ERROR, errorMessage = it.errorMessage
                            )
                        }
                    }
                }
            }
        }
    }

    fun _searchVehicleAndUserByUserId() {
        val userId = stateUi.value.qrcode?.let { qrcode ->
            (qrcode as UserQRCode).userId
        }
        viewModelScope.launch {
            invoiceRepository.searchLicensePlateByUserId(
                stateUi.value.licensePlateOfVehicleIn,
                userId ?: ""
            ).collect { state ->
                when (state) {
                    is State.Loading -> {
                        _stateUi.update {
                            it.copy(state = ParkingState.LOADING)
                        }
                    }
                    is State.Success -> {
                        if (state.data.isNotEmpty()) {
                            _stateUi.update {
                                it.copy(
                                    state = ParkingState.SUCCESSFUL_FOUND_VEHICLE,
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
                                    state = ParkingState.FAIL_FOUND_VEHICLE,
                                )
                            }
                        }
                    }
                    is State.Failed -> {
                        _stateUi.update {
                            it.copy(
                                state = ParkingState.ERROR, errorMessage = it.errorMessage
                            )
                        }
                    }
                }
            }
        }
    }

    fun createInvoiceForGuest() {
        _stateUi.update {
            it.copy(
                state = ParkingState.CREATE_INVOICE_FOR_GUEST,
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

    fun createInvoiceForUserNotRegisterVehicle(): ParkingInvoice {
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
                        _stateUi.value.parkingInvoice?.vehicle?.licensePlate!!
                    )
                    else -> {
                        if (!available) {
                            invoiceRepository.addNewParkingInvoice(_stateUi.value.parkingInvoice!!)
                        } else {
                            flowOf()
                        }
                    }
                }
            }.collect { state ->
                when (state) {
                    is State.Loading -> {
                        _stateUi.update {
                            it.copy(state = ParkingState.LOADING)
                        }
                    }
                    is State.Success -> {
                        if (state.data is Boolean) {
                            available = state.data
                            if (available) {
                                _stateUi.update {
                                    it.copy(state = ParkingState.PARKED_VEHICLE)
                                }
                            }
                        } else {
                            _stateUi.update {
                                it.copy(state = ParkingState.SUCCESSFUL_CREATE_PARKING_INVOICE)
                            }
                        }
                    }
                    is State.Failed -> {
                        _stateUi.update {
                            it.copy(
                                state = ParkingState.FAIL_CREATE_PARKING_INVOICE,
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
                state = ParkingState.BLANK,
                errorMessage = "",
                parkingInvoice = null,
                user = null,
                vehicle = null,
                qrcode = null,
                licensePlateOfVehicleIn = "",
                licensePlateOfVehicleOut = "",
                bmVehicleIn = null,
                bmVehicleOut = null
            )
        }
    }

    fun searchParkingInvoiceById(id: String) {
        searchParkingInvoiceJob?.cancel()
        searchParkingInvoiceJob = viewModelScope.launch {
            invoiceRepository.searchParkingInvoiceById(id).collect { state ->
                when (state) {
                    is State.Loading -> {
                        _stateUi.update {
                            it.copy(state = ParkingState.LOADING)
                        }
                    }
                    is State.Success -> {
                        if (state.data.isNotEmpty()) {
                            if (state.data[0].state == "parked") {
                                _stateUi.update {
                                    it.copy(
                                        state = ParkingState.PARKED_PARKING_INVOICE,
                                    )
                                }
                            } else {
                                _stateUi.update {
                                    it.copy(
                                        state = ParkingState.SUCCESSFUL_SEARCH_PARKING_INVOICE,
                                        parkingInvoice = state.data[0],
                                    )
                                }
                            }
                        } else {
                            _stateUi.update {
                                it.copy(
                                    state = ParkingState.FAIL_SEARCH_PARKING_INVOICE,
                                )
                            }
                        }
                    }
                    is State.Failed -> {
                        _stateUi.update {
                            it.copy(
                                state = ParkingState.ERROR, errorMessage = it.errorMessage
                            )
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
                                it.copy(state = ParkingState.LOADING)
                            }
                        }
                        is State.Success -> {
                            _stateUi.update {
                                it.copy(
                                    state = ParkingState.SUCCESSFUL_COMPLETE_PARKING_INVOICE,
                                )
                            }
                        }
                        is State.Failed -> {
                            _stateUi.update {
                                it.copy(
                                    state = ParkingState.FAIL_COMPLETE_PARKING_INVOICE,
                                    errorMessage = it.errorMessage
                                )
                            }
                        }
                    }
                }
        }
    }

    fun updateInvoiceOut(
        _paymentMethod: String, _type: String, _imgOutString: String, _note: String
    ) {
        val newParkingInvoice = stateUi.value.parkingInvoice
        newParkingInvoice?.apply {
            paymentMethod = _paymentMethod
            type = _type
            imageOut = _imgOutString
            note = _note
            timeOut = TimeUtil.getCurrentTime().toString()
        }
        _stateUi.update {
            it.copy(
                parkingInvoice = newParkingInvoice
            )
        }
    }

    fun updateInvoiceIn(_paymentMethod: String, _type: String, _note: String) {
        val newParkingInvoice = _stateUi.value.parkingInvoice
        newParkingInvoice?.apply {
            paymentMethod = _paymentMethod
            type = _type
            note = _note
        }
        _stateUi.update {
            it.copy(
                parkingInvoice = newParkingInvoice
            )
        }
    }

    fun getDataFromQRCode(result: String) {

        val qrcode = AESEncyptionUtil.decrypt(result)?.let {
            QRCode.fromString(it)
        }

        if (qrcode is UserQRCode) {
            _stateUi.update {
                it.copy(
                    state = ParkingState.SUCCESSFUL_GET_USER_QR_CODE,
                    qrcode = qrcode
                )
            }
        } else if (qrcode is InvoiceQRCode) {
            _stateUi.update {
                it.copy(
                    state = ParkingState.SUCCESSFUL_GET_INVOICE_QR_CODE,
                    qrcode = qrcode
                )
            }
        }
        else{
            _stateUi.update {
                it.copy(
                    state = ParkingState.FAIL_GET_QR_CODE
                )
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
        return stateUi.value.state == ParkingState.FAIL_FOUND_VEHICLE || stateUi.value.state == ParkingState.CREATE_INVOICE_FOR_GUEST
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


    data class ParkingViewModelState(
        val errorMessage: String = "",
        val user: UserInvoice? = null,
        val vehicle: VehicleInvoice? = null,
        val parkingInvoice: ParkingInvoice? = null,
        val state: ParkingState = ParkingState.BLANK,
        val errorList: MutableMap<ParkingState, String> = hashMapOf(),
        val messageList: MutableMap<ParkingState, String> = hashMapOf(),
        val qrcode: QRCode? = null,
        val licensePlateOfVehicleIn: String = "",
        val licensePlateOfVehicleOut: String = "",
        val bmVehicleIn: Bitmap? = null,
        val bmVehicleOut: Bitmap? = null,
        val timeLimit: Int = 3
    ) {
        init {
            errorList[ParkingState.FAIL_FOUND_VEHICLE] =
                "Không tìm thấy phương tiện tương ứng có biển số"
            errorList[ParkingState.FAIL_CREATE_PARKING_INVOICE] = "Tạo hóa đơn thất bại"
            errorList[ParkingState.FAIL_SEARCH_PARKING_INVOICE] = "Không tìm thấy hóa đơn tương ứng"
            errorList[ParkingState.FAIL_COMPLETE_PARKING_INVOICE] =
                "Trả hóa đơn xe không thành công"
            errorList[ParkingState.FAIL_GET_QR_CODE] = "Không tìm thấy QRCODE"
            errorList[ParkingState.PARKED_PARKING_INVOICE] = "Hóa đơn không hợp lệ"
            errorList[ParkingState.PARKED_VEHICLE] = "Xe đã được gửi"

            messageList[ParkingState.SUCCESSFUL_FOUND_VEHICLE] =
                "Tìm thấy phương tiện tương ứng có biển số"
            messageList[ParkingState.SUCCESSFUL_CREATE_PARKING_INVOICE] =
                "Tạo hóa đơn gửi xe thành công cho xe có biển số"
            messageList[ParkingState.SUCCESSFUL_SEARCH_PARKING_INVOICE] =
                "Tìm thấy hóa đơn xe có biển số"
            messageList[ParkingState.SUCCESSFUL_COMPLETE_PARKING_INVOICE] =
                "Trả hóa đơn xe thành công cho xe có biển số"

        }
    }

    enum class ParkingState {
        BLANK, LOADING, ERROR, SUCCESSFUL_FOUND_VEHICLE, PARKED_VEHICLE, FAIL_FOUND_VEHICLE,
        SUCCESSFUL_CREATE_PARKING_INVOICE, FAIL_CREATE_PARKING_INVOICE,
        SUCCESSFUL_SEARCH_PARKING_INVOICE, FAIL_SEARCH_PARKING_INVOICE, SUCCESSFUL_COMPLETE_PARKING_INVOICE, PARKED_PARKING_INVOICE,
        FAIL_COMPLETE_PARKING_INVOICE, SUCCESSFUL_GET_INVOICE_QR_CODE, FAIL_GET_QR_CODE, SUCCESSFUL_GET_USER_QR_CODE, CREATE_INVOICE_FOR_GUEST
    }
}