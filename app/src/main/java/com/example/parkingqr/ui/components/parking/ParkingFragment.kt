package com.example.parkingqr.ui.components.parking

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.provider.MediaStore
import android.text.InputType
import android.util.Log
import android.view.PixelCopy
import android.view.SurfaceView
import android.view.View
import androidx.annotation.OptIn
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.rtsp.RtspMediaSource
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.example.parkingqr.R
import com.example.parkingqr.databinding.FragmentParkingBinding
import com.example.parkingqr.domain.model.qrcode.InvoiceQRCode
import com.example.parkingqr.ui.base.BaseFragment
import com.example.parkingqr.ui.components.dialog.*
import com.example.parkingqr.utils.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch


class ParkingFragment : BaseFragment() {

    companion object {
        private const val PIC_CODE_CAR_IN = 123
        private const val PIC_CODE_CAR_OUT = 456
        private const val REQUEST_CAMERA_PERMISSION_CODE = 789
    }

    private lateinit var binding: FragmentParkingBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var playerForCameraIn: ExoPlayer
    private lateinit var playerForCameraOut: ExoPlayer
    private var timer: Timer? = null
    private val viewModel: ParkingViewModel by hiltNavGraphViewModels(R.id.parkingFragment)

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PIC_CODE_CAR_IN && resultCode == RESULT_OK) {
            val bm = data?.extras!!["data"] as Bitmap?
            bm?.let {
                viewModel.processImageVehicleIn(it) {

                }
            }
        }
        if (requestCode == PIC_CODE_CAR_OUT && resultCode == RESULT_OK) {
            val bm = data?.extras!!["data"] as Bitmap?
            bm?.let {
                viewModel.processImageVehicleOut(it) {

                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        playerForCameraIn = ExoPlayer.Builder(requireContext()).build()
        playerForCameraOut = ExoPlayer.Builder(requireContext()).build()
    }

    override fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.stateUi.map { it.isLoading }.distinctUntilChanged()
                    .collect { isLoading ->
                        if (isLoading) showLoading()
                        else hideLoading()
                    }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.stateUi.map { it.message to it.errorMessage }.distinctUntilChanged()
                    .collect { (message, errorMessage) ->
                        message.takeIf { it.isNotEmpty() }?.let {
                            showMessage(message)
                            viewModel.showMessage()
                        }
                        errorMessage.takeIf { it.isNotEmpty() }?.let {
                            showError(errorMessage)
                            viewModel.showError()
                        }
                    }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.stateUi.map { it.bmVehicleIn to it.bmVehicleOut }.distinctUntilChanged()
                    .collect { (bmVehicleIn, bmVehicleOut) ->
                        if (bmVehicleIn != null) {
                            binding.ivPhotoCarInParking.setImageBitmap(bmVehicleIn)
                        } else {
                            binding.ivPhotoCarInParking.setImageResource(R.drawable.img_car_in)
                        }
                        if (bmVehicleOut != null) {
                            binding.ivPhotoCarOutParking.setImageBitmap(bmVehicleOut)
                        } else {
                            binding.ivPhotoCarOutParking.setImageResource(R.drawable.img_car_out)
                        }
                    }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.stateUi.map { it.cameraIn }.distinctUntilChanged()
                    .collect {
                        if (it != null && it.uri.isNotEmpty()) {
                            binding.ivPhotoCarInParking.visibility = View.GONE
                            binding.plvCarInParking.visibility = View.VISIBLE
                            binding.plvCarInParking.player = playerForCameraIn
                            if (!playerForCameraIn.isPlaying) {
                                prepareToShowCamera(playerForCameraIn, it.uri)
                            }
                        } else {
                            binding.ivPhotoCarInParking.visibility = View.VISIBLE
                            binding.plvCarInParking.visibility = View.GONE
                        }
                    }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.stateUi.map { it.cameraOut }.distinctUntilChanged()
                    .collect {
                        if (it != null && it.uri.isNotEmpty()) {
                            binding.ivPhotoCarOutParking.visibility = View.GONE
                            binding.plvCarOutParking.visibility = View.VISIBLE
                            binding.plvCarOutParking.player = playerForCameraOut
                            if (!playerForCameraOut.isPlaying) {
                                prepareToShowCamera(playerForCameraOut, it.uri)
                            }
                        } else {
                            binding.ivPhotoCarOutParking.visibility = View.VISIBLE
                            binding.plvCarOutParking.visibility = View.GONE
                        }
                    }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.stateUi.map { it.action }.distinctUntilChanged().collect { action ->
                    when (action) {
                        ParkingViewModel.ParkingAction.REFRESH -> {
                            handleRefresh()
                        }
                        ParkingViewModel.ParkingAction.SHOW_INVOICE_FOR_USER_REGISTER_VEHICLE -> {
                            showInvoiceForUserRegisterVehicle()
                        }
                        ParkingViewModel.ParkingAction.SHOW_INVOICE_FOR_USER_NOT_REGISTER_VEHICLE -> {
                            showInvoiceForUserNotRegisterVehicle()
                        }
                        ParkingViewModel.ParkingAction.SHOW_INVOICE_FOR_GUEST -> {
                            showInvoiceForGuest()
                        }
                        ParkingViewModel.ParkingAction.PROCESS_TO_COMPLETE_PARKING_INVOICE -> {
                            processToCompleteParkingInvoice()
                        }
                        ParkingViewModel.ParkingAction.SHOW_INVOICE_CREATE_FROM_MONTHLY_TICKET -> {
                            showInvoiceCreateFromMonthlyTicket()
                        }
                        ParkingViewModel.ParkingAction.GET_INVOICE_FROM_QR_CODE -> {
                            getInvoiceFromQRCode()
                        }
                        ParkingViewModel.ParkingAction.CREATE_INVOICE_FROM_USER_QR_CODE -> {
                            CoroutineScope(Dispatchers.Main).launch {
                                delay(100)
                                createInvoiceForUser()
                            }
                        }
                        else -> {}
                    }
                }
            }
        }
    }

    override fun initViewBinding(): View {
        auth = Firebase.auth
        binding = FragmentParkingBinding.inflate(layoutInflater)
        viewModel.getCameras()
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
            handleCreateInvoiceForGuest()
        }
        binding.llButtonQRCodeParking.setOnClickListener {
            handleOpenQRScan()
        }
        binding.ivQrcodeParking.setOnClickListener {
            showInvoiceQRCode()
        }
        binding.tvRefreshParking.setOnClickListener {
            viewModel.refreshData()
            handleRefresh()
        }
        binding.tvActionQrcodeParking.setOnClickListener {
            val action = binding.tvActionQrcodeParking.text.toString()
            if (action.contains("tạo")) {
                if (viewModel.isUnregisterVehicle()) {
                    ChooseVehicleTypeDialog(requireContext()) {
                        viewModel.updateVehicleType(it)
                        createNewInvoice()
                    }.show()
                } else {
                    createNewInvoice()
                }
            } else {
                completeInvoice()
            }
        }
        binding.tvStopTimerParking.setOnClickListener {
            stopTimer()
        }
        binding.llWrongLicensePlateParking.setOnClickListener {
            showDialogChooseVehicle()
        }
        binding.ivSettingCameraParking.setOnClickListener {
            handleUpdateCamera()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        playerForCameraIn.release()
        playerForCameraOut.release()
    }

    private fun handleUpdateCamera() {
        val cameraIn = viewModel.stateUi.value.cameraIn
        val cameraOut = viewModel.stateUi.value.cameraOut

        AddCameraInOutDialog(
            requireContext(),
            cameraIn?.uri ?: "",
            cameraOut?.uri ?: ""
        ) { camInUri, camOutUri ->
            viewModel.updateCameraIn(camInUri)
            viewModel.updateCameraOut(camOutUri)
        }.show()
    }

    @OptIn(UnstableApi::class)
    private fun prepareToShowCamera(exoPlayer: ExoPlayer, uri: String) {
        try {
            CoroutineScope(Dispatchers.Main).launch {
                if (uri.contains("rtsp")) {
                    val mediaSource =
                        RtspMediaSource.Factory()
                            .createMediaSource(MediaItem.fromUri(uri))
                    exoPlayer.setMediaSource(mediaSource)
                } else {
                    exoPlayer.setMediaItem(MediaItem.fromUri(uri))
                }
                exoPlayer.prepare()
                exoPlayer.play()
            }
        } catch (e: Exception) {
            Log.e("TAG", "Error : $e");
        }
    }


    private fun takeScreenshotWithPixelCopy(videoView: SurfaceView, callback: (Bitmap?) -> Unit) {
        val bitmap: Bitmap = Bitmap.createBitmap(
            512,
            512,
            Bitmap.Config.ARGB_8888
        )
        try {
            val handlerThread = HandlerThread("PixelCopier")
            handlerThread.start()
            PixelCopy.request(
                videoView, bitmap,
                { copyResult ->
                    if (copyResult == PixelCopy.SUCCESS) {
                        CoroutineScope(Dispatchers.Main).launch {
                            callback(bitmap)
                        }
                    }
                    handlerThread.quitSafely()
                },
                Handler(handlerThread.looper)
            )
        } catch (e: IllegalArgumentException) {
            callback(null)
            e.printStackTrace()
        }
    }

    private fun showDialogChooseVehicle() {
        viewModel.stateUi.value.apply {
            ChooseUserVehicleDialog(requireContext(), vehicleList, licensePlateOfVehicleIn) {
                viewModel.handleSelectVehicle(it)
            }.show()
        }
    }

    private fun getInvoiceFromQRCode() {
        viewModel.getParkingInvoiceFromQRCode()
    }

    private fun showInvoiceForGuest() {
        binding.llWrongLicensePlateParking.visibility = View.GONE
        binding.tvStateMessageParking.text = "Hóa đơn gửi xe cho khách"
        displayParkingInvoice()
    }

    private fun showInvoiceForUserRegisterVehicle() {
        binding.llWrongLicensePlateParking.visibility = View.GONE
        binding.tvStateMessageParking.text =
            "Hóa đơn xe có đăng ký"
        displayParkingInvoice()
        showTimerVehicleIn()
    }

    private fun showInvoiceForUserNotRegisterVehicle() {
        binding.llWrongLicensePlateParking.visibility = View.VISIBLE
        binding.tvStateMessageParking.text =
            "Người dùng chưa đăng ký xe"
        displayParkingInvoice()
    }

    private fun stopTimer() {
        timer?.stop()
    }


    private fun showTimerVehicleIn() {
        binding.llTimeLeftParking.visibility = View.VISIBLE
        viewModel.stateUi.value.timeLimit.let { limit ->
            timer = object : Timer(SECOND_MILLISECONDS, limit) {
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
            }
            timer?.start()
        }
    }

    private fun showTimerVehicleOut() {
        binding.llTimeLeftParking.visibility = View.VISIBLE
        viewModel.stateUi.value.timeLimit.let { limit ->
            timer = object : Timer(SECOND_MILLISECONDS, limit) {
                override fun onFinish() {
                    completeInvoice()
                }

                override fun onWorking(count: Int) {
                    if (limit - count >= 10) {
                        binding.tvTimeleftParking.text = "00:${(limit - count)}"
                    } else {
                        binding.tvTimeleftParking.text = "00:0${(limit - count)}"
                    }
                }
            }
            timer?.start()
        }
    }

    @OptIn(UnstableApi::class)
    private fun processToCompleteParkingInvoice() {
        if (viewModel.stateUi.value.cameraOut != null) {
            processToCompleteInvoiceIfHaveCamera()
        } else {
            processToCompleteInvoiceIfNotHaveCamera()
        }
    }

    @OptIn(UnstableApi::class)
    private fun processToCompleteInvoiceIfHaveCamera() {
        binding.plvCarOutParking.videoSurfaceView?.let {
            takeScreenshotWithPixelCopy(it as SurfaceView) { bitmap ->
                bitmap?.let { data ->
                    viewModel.processImageVehicleOut(data) {
                        viewModel.stateUi.value.apply {
                            if (licensePlateOfVehicleOut.isNotEmpty()) {
                                if (licensePlateOfVehicleOut == parkingInvoice?.vehicle?.licensePlate) {
                                    showMessage("Biển số xe khớp nhau: ${parkingInvoice.vehicle.licensePlate}")
                                    binding.tvStateMessageParking.text = "Biển số xe khớp nhau"
                                    if (parkingInvoice.isOnlinePayment() || parkingInvoice.isMonthlyTicketType()) {
                                        showTimerVehicleOut()
                                    }
                                } else {
                                    showMessage("Biển số xe không khớp nhau: ${parkingInvoice?.vehicle?.licensePlate} và ${licensePlateOfVehicleOut}")
                                    binding.tvStateMessageParking.text =
                                        "Biển số xe không khớp nhau: ${parkingInvoice?.vehicle?.licensePlate} và ${licensePlateOfVehicleOut}"
                                    binding.llTimeLeftParking.visibility = View.GONE
                                }
                            } else {
                                showMessage("${messageList[action]}: ${parkingInvoice?.vehicle?.licensePlate}")
                                binding.llTimeLeftParking.visibility = View.GONE
                            }
                            displayParkingInvoice()
                            binding.tvActionQrcodeParking.text = "Nhấn vào để xác nhận trả xe"
                        }
                    }
                }
            }
        }
    }

    private fun processToCompleteInvoiceIfNotHaveCamera() {
        viewModel.stateUi.value.apply {
            if (licensePlateOfVehicleOut.isNotEmpty()) {
                if (licensePlateOfVehicleOut == parkingInvoice?.vehicle?.licensePlate) {
                    showMessage("Biển số xe khớp nhau: ${parkingInvoice.vehicle.licensePlate}")
                    binding.tvStateMessageParking.text = "Biển số xe khớp nhau"
                    if (parkingInvoice.isOnlinePayment() || parkingInvoice.isMonthlyTicketType()) {
                        showTimerVehicleOut()
                    }
                } else {
                    showMessage("Biển số xe không khớp nhau: ${parkingInvoice?.vehicle?.licensePlate} và ${licensePlateOfVehicleOut}")
                    binding.tvStateMessageParking.text =
                        "Biển số xe không khớp nhau: ${parkingInvoice?.vehicle?.licensePlate} và ${licensePlateOfVehicleOut}"
                    binding.llTimeLeftParking.visibility = View.GONE
                }
            } else {
                showMessage("${messageList[action]}: ${parkingInvoice?.vehicle?.licensePlate}")
                binding.llTimeLeftParking.visibility = View.GONE
            }
            displayParkingInvoice()
            binding.tvActionQrcodeParking.text = "Nhấn vào để xác nhận trả xe"
        }
    }

    private fun handlePickImageCarIn() {
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
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(cameraIntent, PIC_CODE_CAR_IN)
        }
    }

    private fun handlePickImageCarOut() {
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
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(cameraIntent, PIC_CODE_CAR_OUT)
        }
    }

    private fun showInvoiceQRCode() {
        InvoiceQRCode(
            viewModel.stateUi.value.parkingInvoice?.id ?: "0",
            TimeUtil.getCurrentTime().toString()
        ).let { invoiceQRCode ->
            AESEncyptionUtil.encrypt(invoiceQRCode.toString())?.apply {
                InvoiceQRCodeDialog(
                    requireContext(),
                    QRcodeUtil.getQrCodeBitmap(this),
                ).show()
            }
        }
    }

    private fun handleRefresh() {
        viewModel.refreshData()
        binding.llBlankCarParking.visibility = View.VISIBLE
        binding.llContainerParking.visibility = View.INVISIBLE
        binding.llParkingInvoiceCarinParking.visibility = View.GONE
        binding.llTimeLeftParking.visibility = View.GONE
        binding.llWrongLicensePlateParking.visibility = View.GONE
        binding.tvStateMessageParking.text = "Hóa đơn gửi xe hiện đang trống"
        timer?.apply {
            stop()
        }
    }

    @OptIn(UnstableApi::class)
    private fun createInvoiceForUser() {
        if (viewModel.stateUi.value.cameraIn != null) {
            createInvoiceForUserIfHaveCamera()
        } else {
            createInvoiceForUserIfNotHaveCamera()
        }
    }

    @OptIn(UnstableApi::class)
    private fun createInvoiceForUserIfHaveCamera() {
        binding.plvCarInParking.videoSurfaceView?.let {
            takeScreenshotWithPixelCopy(it as SurfaceView) { bitmap ->
                bitmap?.let { data ->
                    viewModel.processImageVehicleIn(data) {
                        viewModel.stateUi.value.apply {
                            if (licensePlateOfVehicleIn.isEmpty()) {
                                showMessage("Không nhận dạng dược ảnh xe vào")
                                return@apply
                            }
                            if (LicensePlateUtil.checkLicensePlateValid(
                                    licensePlateOfVehicleIn
                                )
                            ) {
                                viewModel.getDataFromQRCodeToCreateParkingInvoice()
                            } else {
                                showMessage("Biển số xe không hợp lệ: $licensePlateOfVehicleIn")
                            }
                        }
                    }
                }
            }
        }
    }

    private fun createInvoiceForUserIfNotHaveCamera() {
        viewModel.stateUi.value.apply {
            if (bmVehicleIn == null) {
                showMessage("Chưa có ảnh xe vào")
                return
            }
            if (licensePlateOfVehicleIn.isEmpty()) {
                showMessage("Không nhận dạng dược ảnh xe vào")
                return
            }
            if (LicensePlateUtil.checkLicensePlateValid(licensePlateOfVehicleIn)) {
                viewModel.getDataFromQRCodeToCreateParkingInvoice()
            } else {
                showMessage("Biển số xe không hợp lệ: $licensePlateOfVehicleIn")
            }
        }
    }

    @OptIn(UnstableApi::class)
    private fun handleCreateInvoiceForGuest() {
        if (viewModel.stateUi.value.cameraIn != null) {
            createInvoiceForGuestIfHaveCamera()
        } else {
            createInvoiceForGuestIfNotHaveCamera()
        }
    }

    private fun showInvoiceCreateFromMonthlyTicket() {
        binding.llWrongLicensePlateParking.visibility = View.GONE
        binding.tvStateMessageParking.text =
            "Hóa đơn xe gửi vé tháng"
        displayParkingInvoice()
        showTimerVehicleIn()
    }

    @OptIn(UnstableApi::class)
    private fun createInvoiceForGuestIfHaveCamera() {
        binding.plvCarInParking.videoSurfaceView?.let {
            takeScreenshotWithPixelCopy(it as SurfaceView) { bitmap ->
                bitmap?.let { data ->
                    viewModel.processImageVehicleIn(data) {
                        viewModel.stateUi.value.apply {
                            if (licensePlateOfVehicleIn.isEmpty()) {
                                showMessage("Không nhận dạng dược ảnh xe vào")
                                return@apply
                            }
                            if (LicensePlateUtil.checkLicensePlateValid(
                                    licensePlateOfVehicleIn
                                )
                            ) {
                                viewModel.createInvoiceForGuest()
                            } else {
                                showMessage("Biển số xe không hợp lệ: $licensePlateOfVehicleIn")
                            }
                        }
                    }
                }
            }
        }
    }

    private fun createInvoiceForGuestIfNotHaveCamera() {
        viewModel.stateUi.value.apply {
            if (bmVehicleIn == null) {
                showMessage("Chưa có ảnh xe vào")
                return
            }
            if (licensePlateOfVehicleIn.isEmpty()) {
                showMessage("Không nhận dạng dược ảnh xe vào")
                return
            }
            if (LicensePlateUtil.checkLicensePlateValid(licensePlateOfVehicleIn)) {
                viewModel.createInvoiceForGuest()
            } else {
                showMessage("Biển số xe không hợp lệ: $licensePlateOfVehicleIn")
            }
        }
    }

    private fun createNewInvoice() {
        val note = binding.edtNoteParking.text.toString()
        viewModel.updateInvoiceIn(note)
        viewModel.addNewParkingInvoice()
    }

    private fun completeInvoice() {
        val note = binding.edtNoteParking.text.toString()
        viewModel.updateInvoiceOut(note)
        viewModel.completeParkingInvoice()
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


    private fun displayImageVehicleInOfParkingInvoice() {
        viewModel.stateUi.value.apply {
            if (action == ParkingViewModel.ParkingAction.PROCESS_TO_COMPLETE_PARKING_INVOICE) {
                binding.llParkingInvoiceCarinParking.visibility = View.VISIBLE
                parkingInvoice?.let {
                    if (it.imageIn.isNotEmpty()) {
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
                    }
                }
            } else binding.llParkingInvoiceCarinParking.visibility = View.GONE
        }
    }

    private fun displayParkingInvoice() {
        viewModel.stateUi.value.parkingInvoice?.apply {
            val parkingInvoice = this
            binding.llBlankCarParking.visibility = View.GONE
            binding.llContainerParking.visibility = View.VISIBLE
            displayImageVehicleInOfParkingInvoice()
            binding.apply {
                edtNoteParking.setText(note)
                edtNameParking.setText(user.name)
                edtLicensePlateParking.setText(vehicle.licensePlate)
                edtTimeInParking.setText(TimeUtil.convertMilisecondsToDate(timeIn))
                edtVehicleTypeParking.setText(vehicle.getVehicleType())
                edtPaymentMethodParking.setText(getPaymentMethodReadable())
                edtPriceParking.setText(
                    "${
                        FormatCurrencyUtil.formatNumberCeil(
                            viewModel.calculateInvoicePrice(
                                parkingInvoice
                            )
                        )
                    } VND"
                )
                edtInvoiceTypeParking.setText(getInvoiceTypeReadable())
                if (user.name.isNotEmpty()) {
                    edtNameParking.setText(user.name)
                } else {
                    edtNameParking.setText("Không có tên người dùng")
                }
                if (vehicle.type.isNotEmpty()) {
                    edtVehicleTypeParking.setText(vehicle.getVehicleType())
                } else {
                    edtVehicleTypeParking.setText("Không có loại xe")
                }
            }
        }
    }
}