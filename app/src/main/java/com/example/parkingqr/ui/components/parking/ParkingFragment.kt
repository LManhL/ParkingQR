package com.example.parkingqr.ui.components.parking

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.provider.MediaStore
import android.text.InputType
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.example.parkingqr.R
import com.example.parkingqr.databinding.FragmentParkingBinding
import com.example.parkingqr.domain.model.invoice.ParkingInvoice
import com.example.parkingqr.domain.model.qrcode.InvoiceQRCode
import com.example.parkingqr.ui.base.BaseFragment
import com.example.parkingqr.ui.components.dialog.ChooseVehicleTypeDialog
import com.example.parkingqr.ui.components.dialog.InvoiceQRCodeDialog
import com.example.parkingqr.ui.components.qrcode.Timer
import com.example.parkingqr.utils.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch


class ParkingFragment : BaseFragment() {

    companion object {
        private const val PIC_CODE_CAR_IN = 123
        private const val PIC_CODE_CAR_OUT = 456
        private const val REQUEST_CAMERA_PERMISSION_CODE = 789
    }

    private lateinit var binding: FragmentParkingBinding
    private lateinit var auth: FirebaseAuth
    private val parkingViewModel: ParkingViewModel by hiltNavGraphViewModels(R.id.parkingFragment)

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PIC_CODE_CAR_IN && resultCode == RESULT_OK) {
            val bm = data?.extras!!["data"] as Bitmap?
            bm?.let {
                parkingViewModel.processImageVehicleIn(it)
            }
        }
        if (requestCode == PIC_CODE_CAR_OUT && resultCode == RESULT_OK) {
            val bm = data?.extras!!["data"] as Bitmap?
            bm?.let {
                parkingViewModel.processImageVehicleOut(it)
            }
        }
    }

    override fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                parkingViewModel.stateUi.collect {

                    if (it.bmVehicleIn != null) {
                        binding.ivPhotoCarInParking.setImageBitmap(it.bmVehicleIn)
                    } else {
                        binding.ivPhotoCarInParking.setImageResource(R.drawable.img_car_in)
                    }
                    if (it.bmVehicleOut != null) {
                        binding.ivPhotoCarOutParking.setImageBitmap(it.bmVehicleOut)
                    } else {
                        binding.ivPhotoCarOutParking.setImageResource(R.drawable.img_car_out)
                    }

                    when (it.state) {
                        ParkingViewModel.ParkingState.BLANK -> {
                            hideLoading()
                            handleRefresh()
                        }
                        ParkingViewModel.ParkingState.LOADING -> {
                            showLoading()
                        }
                        ParkingViewModel.ParkingState.ERROR -> {
                            hideLoading()
                            showError(it.errorMessage)
                            parkingViewModel.refreshData()
                        }
                        ParkingViewModel.ParkingState.SUCCESSFUL_CREATE_PARKING_INVOICE -> {
                            hideLoading()
                            showMessage("${it.messageList[it.state]} ${it.licensePlateOfVehicleIn.uppercase()}")
                            parkingViewModel.refreshData()
                        }
                        ParkingViewModel.ParkingState.SUCCESSFUL_FOUND_VEHICLE -> {
                            hideLoading()
                            binding.tvStateMessageParking.text =
                                "Hóa đơn xe có đăng ký của người dùng"
                            displayParkingInvoice(it.parkingInvoice!!)
                            showTimerVehicleIn()
                        }
                        ParkingViewModel.ParkingState.SUCCESSFUL_SEARCH_PARKING_INVOICE -> {
                            hideLoading()
                            handleSuccessfullyGetInvoiceQRCode()
                        }
                        ParkingViewModel.ParkingState.FAIL_CREATE_PARKING_INVOICE -> {
                            hideLoading()
                            showMessage("${it.errorList[it.state]}")
                        }
                        ParkingViewModel.ParkingState.PARKED_VEHICLE -> {
                            hideLoading()
                            showMessage("${it.errorList[it.state]}")
                        }
                        ParkingViewModel.ParkingState.FAIL_FOUND_VEHICLE -> {
                            hideLoading()
                            binding.tvStateMessageParking.text =
                                "Hóa đơn xe chưa đăng ký của người dùng"
                            displayParkingInvoice(it.parkingInvoice!!)
                        }
                        ParkingViewModel.ParkingState.FAIL_SEARCH_PARKING_INVOICE -> {
                            hideLoading()
                            showMessage("${it.errorList[it.state]}")
                            parkingViewModel.refreshData()
                        }
                        ParkingViewModel.ParkingState.PARKED_PARKING_INVOICE -> {
                            hideLoading()
                            showMessage("${it.errorList[it.state]}")
                            parkingViewModel.refreshData()
                        }
                        ParkingViewModel.ParkingState.SUCCESSFUL_COMPLETE_PARKING_INVOICE -> {
                            hideLoading()
                            showMessage("${it.messageList[it.state]}: ${it.parkingInvoice?.vehicle?.licensePlate}")
                            parkingViewModel.refreshData()
                        }
                        ParkingViewModel.ParkingState.FAIL_COMPLETE_PARKING_INVOICE -> {
                            hideLoading()
                            showMessage("${it.errorList[it.state]}")
                        }
                        ParkingViewModel.ParkingState.SUCCESSFUL_GET_INVOICE_QR_CODE -> {
                            parkingViewModel.searchParkingInvoiceById(
                                (it.qrcode as InvoiceQRCode).invoiceId ?: ""
                            )
                        }
                        ParkingViewModel.ParkingState.FAIL_GET_QR_CODE -> {
                            showMessage("${it.errorList[it.state]}")
                            parkingViewModel.refreshData()
                        }
                        ParkingViewModel.ParkingState.SUCCESSFUL_GET_USER_QR_CODE -> {
                            parkingViewModel.searchUserByIdThenSearchVehicle()
                        }
                        ParkingViewModel.ParkingState.CREATE_INVOICE_FOR_GUEST -> {
                            displayParkingInvoice(it.parkingInvoice!!)
                            binding.tvStateMessageParking.text = "Hóa đơn gửi xe cho khách"
                        }
                    }
                }

            }
        }
    }

    override fun initViewBinding(): View {
        auth = Firebase.auth
        binding = FragmentParkingBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun initListener() {
        showActionBar(getString(R.string.parking_fragment_name))
        binding.edtPaymentMethodParking.inputType = InputType.TYPE_NULL
        binding.edtInvoiceTypeParking.inputType = InputType.TYPE_NULL

        binding.ivPhotoCarInParking.setOnClickListener {
            handlePickImageCarIn()
        }
        binding.ivPhotoCarOutParking.setOnClickListener {
            handlePickImageCarOut()
        }
        binding.ivLBlankCarParking.setOnClickListener {
            handlePickImageCarIn()
        }
        binding.llButtonCarIn.setOnClickListener {
            searchVehicleIn()
        }
        binding.llButtonCarOut.setOnClickListener {
            handleCarOut()
        }
        binding.ivQrcodeParking.setOnClickListener {
            showInvoiceQRCode()
        }
        binding.tvRefreshParking.setOnClickListener {
            parkingViewModel.refreshData()
            handleRefresh()
        }
        binding.tvActionQrcodeParking.setOnClickListener {
            val action = binding.tvActionQrcodeParking.text.toString()
            if (action.contains("tạo")) {
                if (parkingViewModel.isUnregisterVehicle()) {
                    ChooseVehicleTypeDialog(requireContext()) {
                        parkingViewModel.updateVehicleType(it)
                        createNewInvoice()
                    }.show()
                } else {
                    createNewInvoice()
                }
            } else {
                completeInvoice()
            }
        }
    }

    private fun showTimerVehicleIn() {
        parkingViewModel.stateUi.value.timeLimit.let { limit ->
            val timer = object : Timer(Timer.SECOND_MILLISECONDS, limit) {
                override fun onFinish() {
                    createNewInvoice()
                }

                override fun onWorking(count: Int) {
                    if (limit - count >= 10) {
                        binding.tvTimeleftParking.text = "00:${(limit - count)}"
                    } else {
                        binding.tvTimeleftParking.text = "00:0${(limit - count)}"
                    }
                }
            }.start()
        }
    }

    fun showTimerVehicleOut() {

    }

    private fun handleSuccessfullyGetInvoiceQRCode() {
        parkingViewModel.stateUi.value.let {
            if (it.licensePlateOfVehicleOut.isNotEmpty()) {
                if (it.licensePlateOfVehicleOut == it.parkingInvoice?.vehicle?.licensePlate) {
                    showMessage("Biển số xe khớp nhau: ${it.parkingInvoice.vehicle.licensePlate}")
                    binding.tvStateMessageParking.text = "Biển số xe khớp nhau"
                } else {
                    showMessage("Biển số xe không khớp nhau: ${it.parkingInvoice?.vehicle?.licensePlate} và ${it.licensePlateOfVehicleOut}")
                    binding.tvStateMessageParking.text =
                        "Biển số xe không khớp nhau: ${it.parkingInvoice?.vehicle?.licensePlate} và ${it.licensePlateOfVehicleOut}"
                }
            } else {
                showMessage("${it.messageList[it.state]}: ${it.parkingInvoice?.vehicle?.licensePlate}")
            }
            displayParkingInvoice(it.parkingInvoice!!)
            binding.tvActionQrcodeParking.text = "Nhấn vào để xác nhận trả xe"
        }
    }

    private fun handlePickImageCarIn() {
        if (parkingViewModel.stateUi.value.state == ParkingViewModel.ParkingState.BLANK) {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(cameraIntent, PIC_CODE_CAR_IN)
        } else {
            showMessage("Vui lòng xử lí giao dịch hiện tại trước")
        }
    }

    private fun handlePickImageCarOut() {
        if (parkingViewModel.stateUi.value.state == ParkingViewModel.ParkingState.BLANK) {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(cameraIntent, PIC_CODE_CAR_OUT)
        } else {
            showMessage("Vui lòng xử lí giao dịch hiện tại trước")
        }
    }

    private fun showInvoiceQRCode() {
        val invoiceQRCode = InvoiceQRCode(
            parkingViewModel.stateUi.value.parkingInvoice?.id ?: "0",
            TimeUtil.getCurrentTime().toString()
        )
        AESEncyptionUtil.encrypt(invoiceQRCode.toString())?.apply {
            InvoiceQRCodeDialog(
                requireContext(),
                QRcodeUtil.getQrCodeBitmap(this),
            ).show()
        }
    }

    private fun handleRefresh() {
        binding.llBlankCarParking.visibility = View.VISIBLE
        binding.llContainerParking.visibility = View.INVISIBLE
        binding.llParkingInvoiceCarinParking.visibility = View.GONE
        binding.llTimeLeftParking.visibility = View.GONE
        binding.tvStateMessageParking.text = "Hóa đơn gửi xe hiện đang trống"
    }

    private fun searchVehicleIn() {
        if (parkingViewModel.stateUi.value.licensePlateOfVehicleIn.isEmpty()) {
            showMessage("Chưa có ảnh xe vào")
        } else {
            if (parkingViewModel.stateUi.value.licensePlateOfVehicleIn.isEmpty()) {
                showMessage("Không nhận dạng dược ảnh xe vào")
            } else {
                searchVehicle()
            }
        }
    }

    private fun createNewInvoice() {
        val note = binding.edtNoteParking.text.toString()
        val paymentMethod = binding.edtPaymentMethodParking.text.toString()
        val type = binding.edtInvoiceTypeParking.text.toString()

        parkingViewModel.updateInvoiceIn(paymentMethod, type, note)
        parkingViewModel.addNewParkingInvoice()
    }

    private fun searchVehicle() {
        if (parkingViewModel.stateUi.value.bmVehicleIn == null) {
            showMessage("Chưa có ảnh xe vào")
            return
        }
        if (LicensePlateUtil.checkLicensePlateValid(parkingViewModel.stateUi.value.licensePlateOfVehicleIn)) {
            parkingViewModel.createInvoiceForGuest()
        } else {
            showMessage("Biển số xe không hợp lệ: ${parkingViewModel.stateUi.value.licensePlateOfVehicleIn}")
        }
    }


    private fun handleCarOut() {
        if (parkingViewModel.stateUi.value.state == ParkingViewModel.ParkingState.BLANK) {
            handleOpenQRScan()
        } else {
            showMessage("Vui lòng xử lí giao dịch hiện tại trước")
        }
    }

    private fun completeInvoice() {
        val note = binding.edtNoteParking.text.toString()
        val paymentMethod = binding.edtPaymentMethodParking.text.toString()
        val type = binding.edtInvoiceTypeParking.text.toString()
        var imgOutString = ""

        parkingViewModel.stateUi.value.bmVehicleOut?.let {
            imgOutString = ImageUtil.encodeImage(it)
        }
        parkingViewModel.updateInvoiceOut(paymentMethod, type, imgOutString, note)
        parkingViewModel.completeParkingInvoice()
    }

    private fun handleOpenQRScan() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_DENIED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.CAMERA),
                REQUEST_CAMERA_PERMISSION_CODE
            )
        } else {
            getNavController().navigate(R.id.scanFragment)
        }
    }

    private fun displayImageVehicleIn(parkingInvoice: ParkingInvoice) {
        if (parkingViewModel.stateUi.value.state == ParkingViewModel.ParkingState.SUCCESSFUL_SEARCH_PARKING_INVOICE) {
            binding.llParkingInvoiceCarinParking.visibility = View.VISIBLE
            if (parkingInvoice.imageIn.isNotEmpty()) {
                val circularProgressDrawable = CircularProgressDrawable(requireContext())
                circularProgressDrawable.strokeWidth = 5f
                circularProgressDrawable.centerRadius = 10f
                circularProgressDrawable.setColorSchemeColors(requireContext().getColor(R.color.main_color))
                circularProgressDrawable.start()

                Glide
                    .with(requireContext())
                    .load(parkingInvoice.imageIn)
                    .placeholder(circularProgressDrawable)
                    .fitCenter()
                    .into(binding.ivParkingInvoiceCarInParking)
            } else binding.ivParkingInvoiceCarInParking.setImageResource(R.drawable.img)
        } else binding.llParkingInvoiceCarinParking.visibility = View.GONE
    }

    private fun displayParkingInvoice(parkingInvoice: ParkingInvoice) {
        binding.llBlankCarParking.visibility = View.GONE
        binding.llContainerParking.visibility = View.VISIBLE
        displayImageVehicleIn(parkingInvoice)

        binding.apply {
            edtNoteParking.setText(parkingInvoice.note)
            edtNameParking.setText(parkingInvoice.user.name)
            edtLicensePlateParking.setText(parkingInvoice.vehicle.licensePlate)
            edtTimeInParking.setText(TimeUtil.convertMilisecondsToDate(parkingInvoice.timeIn))
            edtVehicleTypeParking.setText(parkingInvoice.getInvoiceType())
            edtPaymentMethodParking.setText(parkingInvoice.getPaymentMethod())
            edtPriceParking.setText("${FormatCurrencyUtil.formatNumberCeil(parkingInvoice.calTotalPrice())} VND")
            edtInvoiceTypeParking.setText(parkingInvoice.type)
            if (!parkingInvoice.user.name.isNullOrEmpty()) {
                edtNameParking.setText(parkingInvoice.user.name)
            } else {
                edtNameParking.setText("Không có tên người dùng")
            }
            if (!parkingInvoice.vehicle.type.isNullOrEmpty()) {
                edtVehicleTypeParking.setText(parkingInvoice.vehicle.getVehicleType())
            } else {
                edtVehicleTypeParking.setText("Không có loại xe")
            }
        }
    }
}