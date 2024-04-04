package com.example.parkingqr.ui.components.qrcode

import android.os.Bundle
import android.view.View
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.parkingqr.R
import com.example.parkingqr.databinding.FragmentUserQrcodeListBinding
import com.example.parkingqr.domain.model.invoice.ParkingInvoice
import com.example.parkingqr.domain.model.parkinglot.MonthlyTicket
import com.example.parkingqr.domain.model.qrcode.MonthlyTicketQRCode
import com.example.parkingqr.domain.model.qrcode.UserQRCode
import com.example.parkingqr.ui.base.BaseFragment
import com.example.parkingqr.ui.components.dialog.MonthlyTicketQRCodeDialog
import com.example.parkingqr.ui.components.dialog.UserQRCodeDialog
import com.example.parkingqr.utils.AESEncyptionUtil
import com.example.parkingqr.utils.QRcodeUtil
import com.example.parkingqr.utils.TimeUtil
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch


class UserQRCodeListFragment : BaseFragment() {

    private lateinit var binding: FragmentUserQrcodeListBinding
    private lateinit var invoiceList: MutableList<ParkingInvoice>
    private val viewModel: UserQRCodeListViewModel by hiltNavGraphViewModels(R.id.userQRCodeListFragment)
    private lateinit var userQRCodeListAdapter: UserQRCodeListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getIsShowMonthlyTicket()
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
                        }
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
                viewModel.stateUi.collect {
                    if (it.isLoading) showLoading() else hideLoading()
                    if (it.error.isNotEmpty()) {
                        showError(it.error)
                        viewModel.showError()
                    }
                    if (invoiceList.isEmpty()) {
                        if (it.invoiceList.isNotEmpty()) {
                            invoiceList.addAll(it.invoiceList)
                            binding.llDisplayEmptyUserQRCodeList.visibility = View.GONE
                            binding.cir3UserQRCodeList.setViewPager(binding.vpgUserQRcodeList)
                        }
                    } else {
                        invoiceList.clear()
                        invoiceList.addAll(it.invoiceList)
                    }
                    userQRCodeListAdapter.notifyDataSetChanged()
                }
            }
        }
    }

    override fun initViewBinding(): View {
        binding = FragmentUserQrcodeListBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun initListener() {
        showBottomNavigation()
        hideActionBar()
        viewModel.getParkingInvoiceList()
        invoiceList = mutableListOf()
        userQRCodeListAdapter = UserQRCodeListAdapter(invoiceList)
        userQRCodeListAdapter.setOnClickItem {
            handleClickItem(it)
        }
        userQRCodeListAdapter.setOnClickChoosePaymentMethod {
            handleChoosePaymentMethod(it)
        }
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
    }

    private fun handleClickItem(parkingInvoice: ParkingInvoice) {

    }

    private fun handleChoosePaymentMethod(parkingInvoice: ParkingInvoice) {
        viewModel.updatePaymentMethod(parkingInvoice)
    }

    private fun handleShowUserQRCodeDialog(userId: String) {
        val userQRCode = UserQRCode(userId, TimeUtil.getCurrentTime().toString())
        AESEncyptionUtil.encrypt(userQRCode.toString())?.let {
            UserQRCodeDialog(
                requireContext(),
                QRcodeUtil.getQrCodeBitmap(it),
                TimeUtil.getDateCurrentTime()
            ).show()
        }
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