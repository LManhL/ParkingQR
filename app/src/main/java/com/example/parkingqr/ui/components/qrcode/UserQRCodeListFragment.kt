package com.example.parkingqr.ui.components.qrcode

import android.os.Bundle
import android.view.View
import androidx.fragment.app.clearFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.parkingqr.R
import com.example.parkingqr.databinding.FragmentUserQrcodeListBinding
import com.example.parkingqr.domain.model.debt.InvoiceDebt
import com.example.parkingqr.domain.model.invoice.ParkingInvoice
import com.example.parkingqr.domain.model.invoice.WaitingRate
import com.example.parkingqr.domain.model.parkinglot.MonthlyTicket
import com.example.parkingqr.domain.model.qrcode.MonthlyTicketQRCode
import com.example.parkingqr.domain.model.qrcode.UserQRCode
import com.example.parkingqr.ui.base.BaseFragment
import com.example.parkingqr.ui.components.dialog.MonthlyTicketQRCodeDialog
import com.example.parkingqr.ui.components.dialog.RateDialog
import com.example.parkingqr.ui.components.dialog.UserQRCodeDialog
import com.example.parkingqr.ui.components.pay.PayOnlineFragment
import com.example.parkingqr.utils.AESEncyptionUtil
import com.example.parkingqr.utils.FormatCurrencyUtil
import com.example.parkingqr.utils.QRcodeUtil
import com.example.parkingqr.utils.TimeUtil
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch


class UserQRCodeListFragment : BaseFragment() {

    private lateinit var binding: FragmentUserQrcodeListBinding
    private lateinit var invoiceList: MutableList<ParkingInvoice>
    private val viewModel: UserQRCodeListViewModel by hiltNavGraphViewModels(R.id.userQRCodeListFragment)
    private lateinit var userQRCodeListAdapter: UserQRCodeListAdapter
    private var userQRCodeDialog: UserQRCodeDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        invoiceList = mutableListOf()
        userQRCodeListAdapter = UserQRCodeListAdapter(invoiceList)
        userQRCodeListAdapter.setOnClickItem {
            handleClickItem(it)
        }
        userQRCodeListAdapter.setOnClickChoosePaymentMethod {
            handleChoosePaymentMethod(it)
        }
    }

    override fun observeViewModel() {

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.stateUi.map {
                    it.isShowMonthlyTicketDialog to it.selectedMonthlyTicket
                }.distinctUntilChanged()
                    .collect { (isShowMonthlyTicketDialog, selectedMonthlyTicket) ->
                        if (isShowMonthlyTicketDialog) {
                            selectedMonthlyTicket?.let {
                                handleShowMonthlyTicketQRCodeDialog(it)
                                viewModel.showDialog()
                            }
                        } else dismissUserQRCode()
                    }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.stateUi.map { it.isShowUserDialog to it.userId }
                    .distinctUntilChanged()
                    .collect { (isShowUserDialog, userId) ->
                        if (isShowUserDialog) {
                            handleShowUserQRCodeDialog(userId)
                            viewModel.showDialog()
                        }
                    }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.stateUi.map { it.isHideUserDialog }
                    .distinctUntilChanged()
                    .collect { isHideUserDialog ->
                        if (isHideUserDialog) {
                            dismissUserQRCode()
                            viewModel.hideDialog()
                        }
                    }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.stateUi.map { it.isLoading to it.error }.distinctUntilChanged()
                    .collect { (isLoading, isError) ->
                        if (isLoading) showLoading() else hideLoading()
                        if (isError.isNotEmpty()) {
                            showError(isError)
                            viewModel.showError()
                        }
                    }
            }
        }


        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.stateUi.map { it.invoiceList }
                    .distinctUntilChanged()
                    .collect { data ->
                        invoiceList.clear()
                        invoiceList.addAll(data)
                        if (data.isNotEmpty()) {
                            binding.llDisplayEmptyUserQRCodeList.visibility = View.GONE
                            binding.cir3UserQRCodeList.visibility = View.VISIBLE
                            binding.cir3UserQRCodeList.setViewPager(binding.vpgUserQRcodeList)
                        } else {
                            binding.llDisplayEmptyUserQRCodeList.visibility = View.VISIBLE
                            binding.cir3UserQRCodeList.visibility = View.GONE
                        }
                        userQRCodeListAdapter.notifyDataSetChanged()
                    }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.stateUi.map { it.invoiceDebt }
                    .distinctUntilChanged()
                    .collect {
                        showInvoiceDebt(it)
                    }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.stateUi.map { it.waitingRateList }.distinctUntilChanged().collect {
                    showRates(it)
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.stateUi.map { it.message }.distinctUntilChanged().collect {
                    if (it.isNotEmpty()) {
                        showMessage(it)
                        viewModel.showMessage()
                    }
                }
            }
        }

    }

    override fun initViewBinding(): View {
        binding = FragmentUserQrcodeListBinding.inflate(layoutInflater)
        viewModel.getUserInvoiceDebt()
        viewModel.getWaitingRatesToShow()
        viewModel.getParkingInvoiceList()
        return binding.root
    }

    override fun initListener() {
        showBottomNavigation()
        hideActionBar()
        binding.vpgUserQRcodeList.apply {
            adapter = userQRCodeListAdapter
        }
        binding.btnHistoryUserQRCodeList.setOnClickListener {
            getNavController().navigate(R.id.myinvoiceFragment)
            hideBottomNavigation()
        }
        binding.ivHeaderUserQrcodeList.setOnClickListener {
            viewModel.handleToShowQRCode()
        }
        binding.ivSelectQRCodeTypeUserQRCodeList.setOnClickListener {
            getNavController().navigate(R.id.selectQRCodeTypeFragment)
        }
        binding.crdInvoiceDebtUserQRCodeList.setOnClickListener {
            handlePayOnline()
        }
    }

    private fun showRates(rates: List<WaitingRate>) {
        rates.forEach { waitingRate ->
            RateDialog(requireContext(), waitingRate) { rate, comment ->
                handleSendRate(rate, comment, waitingRate)
            }.show()
        }
    }

    private fun handleSendRate(rate: Int, comment: String, waitingRate: WaitingRate) {
        viewModel.sendWaitingRate(rate, comment, waitingRate)
    }

    private fun handlePayOnline() {
        viewModel.stateUi.value.invoiceDebt?.parkingInvoice?.price?.let { price ->
            getNavController().navigate(R.id.payOnlineFragment, Bundle().apply {
                putDouble(PayOnlineFragment.AMOUNT_KEY, price)
            })
            setFragmentResultListener(PayOnlineFragment.RESULT_KEY) { _, bundle ->
                bundle.getString(PayOnlineFragment.BUNDLE_RESULT_KEY)?.let {
                    when (it) {
                        PayOnlineFragment.SUCCESS_RESULT -> {
                            viewModel.createWaitingRate()
                            viewModel.payInvoiceDebt()
                        }
                        PayOnlineFragment.FAIL_RESULT -> {

                        }
                    }
                    clearFragmentResult(PayOnlineFragment.RESULT_KEY)
                }
            }
        }
    }

    private fun showInvoiceDebt(invoiceDebt: InvoiceDebt?) {
        if (invoiceDebt == null) binding.crdInvoiceDebtUserQRCodeList.visibility = View.GONE
        else {
            binding.crdInvoiceDebtUserQRCodeList.visibility = View.VISIBLE
            binding.tvVehicleDesUserQRCodeList.text =
                "${invoiceDebt.parkingInvoice?.vehicle?.getVehicleType()} - ${invoiceDebt.parkingInvoice?.vehicle?.licensePlate?.uppercase()} - ${invoiceDebt.parkingInvoice?.vehicle?.brand?.uppercase()}"
            binding.tvAmountUserQRCodeList.text =
                "${FormatCurrencyUtil.formatNumberCeil(invoiceDebt.parkingInvoice?.price ?: 0.0)} VND"
        }
    }

    private fun handleClickItem(parkingInvoice: ParkingInvoice) {

    }

    private fun handleChoosePaymentMethod(parkingInvoice: ParkingInvoice) {
        viewModel.updatePaymentMethod(parkingInvoice)
    }

    private fun handleShowUserQRCodeDialog(userId: String) {
        val userQRCode = UserQRCode(userId, TimeUtil.getCurrentTime().toString())
        AESEncyptionUtil.encrypt(userQRCode.toString())?.let {
            userQRCodeDialog = UserQRCodeDialog(
                requireContext(),
                QRcodeUtil.getQrCodeBitmap(it),
                TimeUtil.getDateCurrentTime()
            )
            userQRCodeDialog?.show()
        }
    }

    private fun dismissUserQRCode() {
        userQRCodeDialog?.dismiss()
    }

    private fun handleShowMonthlyTicketQRCodeDialog(monthlyTicket: MonthlyTicket) {
        val monthlyTicketQRCode =
            MonthlyTicketQRCode(monthlyTicket.id, TimeUtil.getCurrentTime().toString())
        AESEncyptionUtil.encrypt(monthlyTicketQRCode.toString())?.let {
            MonthlyTicketQRCodeDialog(
                requireContext(),
                QRcodeUtil.getQrCodeBitmap(it),
                monthlyTicket
            ).show()
        }
    }

}