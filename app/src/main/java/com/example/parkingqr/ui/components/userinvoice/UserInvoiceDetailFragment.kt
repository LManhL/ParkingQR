package com.example.parkingqr.ui.components.userinvoice

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.example.parkingqr.R
import com.example.parkingqr.databinding.FragmentMyInvoiceDetailBinding
import com.example.parkingqr.domain.model.invoice.ParkingInvoice
import com.example.parkingqr.ui.base.BaseFragment
import com.example.parkingqr.utils.FormatCurrencyUtil
import com.example.parkingqr.utils.TimeUtil
import kotlinx.coroutines.launch

class UserInvoiceDetailFragment : BaseFragment() {
    private lateinit var binding: FragmentMyInvoiceDetailBinding
    private lateinit var invoiceId: String
    private val userInvoiceDetailViewModel: UserInvoiceDetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        invoiceId = arguments?.getString(UserInvoiceFragment.INVOICE_ID) ?: "-1"
        if (invoiceId != "-1") {
            userInvoiceDetailViewModel.getInvoiceById(invoiceId)
        }
    }
    override fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                userInvoiceDetailViewModel.stateUi.collect {
                    if (it.isLoading) showLoading() else hideLoading()
                    if (it.error.isNotEmpty()) {
                        showError(it.error)
                        userInvoiceDetailViewModel.showError()
                    }
                    if (it.message.isNotEmpty()) {
                        showMessage(it.message)
                        userInvoiceDetailViewModel.showMessage()
                    }
                    if (it.invoice == null) binding.llWrapAllMyInvoiceDetail.visibility = View.GONE
                    it.invoice?.let { parkingInvoice ->
                        showInvoice(parkingInvoice)
                    }

                }
            }
        }
    }

    override fun initViewBinding(): View {
        binding = FragmentMyInvoiceDetailBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun initListener() {
        showActionBar(getString(R.string.myinvoice_fragment_name))
    }

    private fun showInvoice(parkingInvoice: ParkingInvoice) {
        binding.llWrapAllMyInvoiceDetail.visibility = View.VISIBLE
        binding.edtTimeInMyInvoiceDetail.setText(TimeUtil.convertMilisecondsToDate(parkingInvoice.timeIn))
        binding.edtLicensePlateMyInvoiceDetail.setText(parkingInvoice.vehicle.licensePlate)
        binding.edtPaymentMethodMyInvoiceDetail.setText(parkingInvoice.paymentMethod)
        binding.edtInvoiceTypeMyInvoiceDetail.setText(parkingInvoice.type)
        binding.edtTimeOutMyInvoiceDetail.setText(
            TimeUtil.convertMilisecondsToDate(
                parkingInvoice.timeOut
            )
        )
        val circularProgressDrawable = CircularProgressDrawable(requireContext())
        circularProgressDrawable.strokeWidth = 10f
        circularProgressDrawable.centerRadius = 50f
        circularProgressDrawable.setColorSchemeColors(requireContext().getColor(R.color.main_color))
        circularProgressDrawable.start()

        Glide
            .with(requireContext())
            .load(parkingInvoice.imageIn)
            .placeholder(circularProgressDrawable)
            .fitCenter()
            .into(binding.ivCarInMyInvoiceDetail)

        if (parkingInvoice.state == "parking") {
            binding.llCarOutMyInvoiceDetail.visibility = View.GONE
            binding.edtStateMyInvoiceDetail.setText("Xe đang gửi")
            binding.llTimeOutMyInvoiceDetail.visibility = View.GONE
        } else {
            if (parkingInvoice.imageOut.isNotEmpty()) {
                binding.llCarOutMyInvoiceDetail.visibility = View.VISIBLE
                Glide
                    .with(requireContext())
                    .load(parkingInvoice.imageOut)
                    .placeholder(circularProgressDrawable)
                    .fitCenter()
                    .into(binding.ivCarOutMyInvoiceDetail)
            }
            binding.edtStateMyInvoiceDetail.setText("Đã trả xe")
            binding.llTimeOutMyInvoiceDetail.visibility = View.VISIBLE
        }

        binding.edtNoteMyInvoiceDetail.setText(parkingInvoice.note)

        binding.tvPriceMyInvoiceDetail.text =
            "Giá tiền: ${FormatCurrencyUtil.formatNumberCeil(parkingInvoice.price)} VND"

        if (!parkingInvoice.user.name.isNullOrEmpty()) {
            binding.edtIsRegisterMyInvoiceDetail.setText("Xe đã đăng ký")
        } else {
            binding.edtIsRegisterMyInvoiceDetail.setText("Xe chưa đăng ký")
        }

        if (!parkingInvoice.vehicle.type.isNullOrEmpty()) {
            binding.edtVehicleTypeMyInvoiceDetail.setText(parkingInvoice.vehicle.type)
        } else {
            binding.edtVehicleTypeMyInvoiceDetail.setText("Không có")
        }

        if (!parkingInvoice.user.name.isNullOrEmpty()) {
            binding.edtNameMyInvoiceDetail.setText(parkingInvoice.user.name)
        } else {
            binding.edtNameMyInvoiceDetail.setText("Không có")
        }
    }

}