package com.example.parkingqr.ui.components

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.provider.MediaStore
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.navGraphViewModels
import com.example.parkingqr.R
import com.example.parkingqr.databinding.FragmentParkingBinding
import com.example.parkingqr.domain.parking.ParkingInvoice
import com.example.parkingqr.ui.base.BaseFragment
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
    private val parkingViewModel: ParkingViewModel by navGraphViewModels(R.id.parkingFragment)
    private var carNumberIn = ""
    private var carNumberOut = ""
    private var imageCarIn: Bitmap? = null
    private var imageCarOut: Bitmap? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PIC_CODE_CAR_IN) {
            imageCarIn = data?.extras!!["data"] as Bitmap?
            binding.ivPhotoCarInParking.setImageBitmap(imageCarIn)
            if (imageCarIn != null) {
                TextRecognizer.invoke(imageCarIn!!) {
                    carNumberIn = it
                }
            }
        }
        if (requestCode == PIC_CODE_CAR_OUT) {
            imageCarOut = data?.extras!!["data"] as Bitmap?
            binding.ivPhotoCarOutParking.setImageBitmap(imageCarOut)
            if (imageCarOut != null) {
                TextRecognizer.invoke(imageCarOut!!) {
                    carNumberOut = it
                }
            }
        }
    }

    override fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                parkingViewModel.stateUi.collect {
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
                        }
                        ParkingViewModel.ParkingState.SUCCESSFUL_CREATE_PARKING_INVOICE -> {
                            hideLoading()
                            showMessage("${it.messageList[it.state]}: $carNumberIn")
                            parkingViewModel.refreshData()
                        }
                        ParkingViewModel.ParkingState.SUCCESSFUL_FOUND_VEHICLE -> {
                            hideLoading()
                            binding.tvStateMessageParking.text = "Hóa đơn gửi xe cho xe có đăng ký"
                            binding.ivCarInParking.setBackgroundResource(R.drawable.create)
                            binding.tvCarInParking.text = "Tạo"
                            displayParkingInvoice(it.parkingInvoice!!)
                        }
                        ParkingViewModel.ParkingState.SUCCESSFUL_SEARCH_PARKING_INVOICE -> {
                            hideLoading()
                            if (carNumberOut.isNotEmpty()) {
                                if (carNumberOut == it.parkingInvoice?.vehicle?.licensePlate) {
                                    showMessage("Biển số xe khớp nhau: ${it.parkingInvoice.vehicle.licensePlate}")
                                    binding.tvStateMessageParking.text = "Biển số xe khớp nhau"
                                } else {
                                    showMessage("Biển số xe không khớp nhau: ${it.parkingInvoice?.vehicle?.licensePlate} và $carNumberOut")
                                    binding.tvStateMessageParking.text =
                                        "Biển số xe không khớp nhau: ${it.parkingInvoice?.vehicle?.licensePlate} và $carNumberOut"
                                }
                            } else {
                                showMessage("${it.messageList[it.state]}: ${it.parkingInvoice?.vehicle?.licensePlate}")
                            }
                            displayParkingInvoice(it.parkingInvoice!!)
                            binding.ivCarOutParking.setBackgroundResource(R.drawable.confirm)
                            binding.tvCarOutParking.text = "Trả xe"
                        }
                        ParkingViewModel.ParkingState.FAIL_CREATE_PARKING_INVOICE -> {
                            hideLoading()
                            showMessage("${it.errorList[it.state]}")
                        }
                        ParkingViewModel.ParkingState.FAIL_FOUND_VEHICLE -> {
                            hideLoading()
                            binding.tvStateMessageParking.text =
                                "Hóa đơn gửi xe cho xe chưa đăng ký"
                            binding.ivCarInParking.setBackgroundResource(R.drawable.car_in)
                            binding.tvCarInParking.text = "Tạo"
                            displayParkingInvoice(it.parkingInvoice!!)
                        }
                        ParkingViewModel.ParkingState.FAIL_SEARCH_PARKING_INVOICE -> {
                            hideLoading()
                            showMessage("${it.errorList[it.state]}")
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
                        ParkingViewModel.ParkingState.SUCCESSFUL_GET_QR_CODE ->{
                            parkingViewModel.searchParkingInvoiceById(it.qrcode)
                        }
                        ParkingViewModel.ParkingState.FAIL_GET_QR_CODE ->{
                            showMessage("${it.errorList[it.state]}")
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
        binding.ivPhotoCarInParking.setOnClickListener {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(cameraIntent, PIC_CODE_CAR_IN)
        }
        binding.ivPhotoCarOutParking.setOnClickListener {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(cameraIntent, PIC_CODE_CAR_OUT)
        }
        binding.ivLBlankCarParking.setOnClickListener {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(cameraIntent, PIC_CODE_CAR_IN)
        }
        binding.llButtonCarIn.setOnClickListener {
            handleCarIn()
        }
        binding.llButtonCarOut.setOnClickListener {
            handleCarOut()
        }
        binding.ivQrcodeParking.setOnClickListener {
            QRCodeDialog(context!!, QRcodeService.getQrCodeBitmap(parkingViewModel.stateUi.value.parkingInvoice?.id ?: "")).show()
        }
        binding.llRefreshParking.setOnClickListener {
            parkingViewModel.refreshData()
            handleRefresh()
        }
    }

    private fun handleRefresh() {
        binding.ivCarInParking.setBackgroundResource(R.drawable.image_search)
        binding.tvCarInParking.text = "Tìm kiếm"
        binding.ivCarOutParking.setBackgroundResource(R.drawable.qr_scan)
        binding.tvCarOutParking.text = "Quét"
        binding.llBlankCarParking.visibility = View.VISIBLE
        binding.llContainerParking.visibility = View.INVISIBLE
        binding.llParkingInvoiceCarinParking.visibility = View.GONE
        binding.tvStateMessageParking.text = "Hóa đơn gửi xe hiện đang trống"
        binding.ivPhotoCarOutParking.setImageResource(R.drawable.img_car_out)
        binding.ivPhotoCarInParking.setImageResource(R.drawable.img_car_in)
        carNumberOut = ""
        carNumberIn = ""
        imageCarIn = null
        imageCarOut = null
    }

    private fun handleCarIn() {
        if (imageCarIn == null) {
            showMessage("Chưa có ảnh xe vào")
        } else {
            if (carNumberIn.isEmpty()) {
                showMessage("Không nhận dạng dược ảnh xe vào")
            } else {
                if (parkingViewModel.stateUi.value.state == ParkingViewModel.ParkingState.SUCCESSFUL_FOUND_VEHICLE
                    || parkingViewModel.stateUi.value.state == ParkingViewModel.ParkingState.FAIL_FOUND_VEHICLE
                ) {
                    parkingViewModel.addNewParkingInvoice()
                } else {
                    if (LicensePlateService.checkLicensePlateValid(carNumberIn)) {
                        parkingViewModel.searchVehicleAndUserByLicensePlate(
                            licensePlate = carNumberIn,
                            imageCarIn = imageCarIn!!
                        )
                    } else {
                        showMessage("Biển số xe không hợp lệ: $carNumberIn")
                    }
                }
            }
        }
    }

    private fun handleCarOut() {
        if (parkingViewModel.stateUi.value.state == ParkingViewModel.ParkingState.SUCCESSFUL_SEARCH_PARKING_INVOICE) {
            parkingViewModel.completeParkingInvoice()
        } else {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSION_CODE)
            }
            else{
                getNavController().navigate(R.id.scanFragment)
            }
        }
    }

    private fun displayImageVehicleIn(parkingInvoice: ParkingInvoice) {
        if (parkingViewModel.stateUi.value.state == ParkingViewModel.ParkingState.SUCCESSFUL_SEARCH_PARKING_INVOICE) {
            binding.llParkingInvoiceCarinParking.visibility = View.VISIBLE
            if (parkingInvoice.imageIn.isNotEmpty()) binding.ivParkingInvoiceCarInParking.setImageBitmap(
                ImageService.decodeImage(parkingInvoice.imageIn)
            )
            else binding.ivParkingInvoiceCarInParking.setImageResource(R.drawable.img)
        } else binding.llParkingInvoiceCarinParking.visibility = View.GONE
    }

    private fun displayParkingInvoice(parkingInvoice: ParkingInvoice) {
        binding.llBlankCarParking.visibility = View.GONE
        binding.llContainerParking.visibility = View.VISIBLE

        binding.apply {
            edtTypeParking.setText(parkingInvoice.vehicle.type)
            edtNoteParking.setText(parkingInvoice.note)
            edtNameParking.setText(parkingInvoice.user.name)
            edtLicensePlateParking.setText(parkingInvoice.vehicle.licensePlate)
            edtTimeInParking.setText(parkingInvoice.timeIn)
            edtVehicleTypeParking.setText(parkingInvoice.type)
            edtPaymentMethodParking.setText(parkingInvoice.paymentMethod)
        }
        displayImageVehicleIn(parkingInvoice)
    }
}