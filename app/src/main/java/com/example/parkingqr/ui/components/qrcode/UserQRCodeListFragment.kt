package com.example.parkingqr.ui.components.qrcode

import android.util.Log
import android.view.View
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.parkingqr.R
import com.example.parkingqr.databinding.FragmentUserQrcodeListBinding
import com.example.parkingqr.domain.model.invoice.ParkingInvoice
import com.example.parkingqr.domain.model.qrcode.UserQRCode
import com.example.parkingqr.ui.base.BaseFragment
import com.example.parkingqr.ui.components.dialog.UserQRCodeDialog
import com.example.parkingqr.utils.AESEncyptionUtil
import com.example.parkingqr.utils.QRcodeUtil
import com.example.parkingqr.utils.TimeUtil
import com.google.gson.JsonObject
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch


class UserQRCodeListFragment : BaseFragment() {

    private lateinit var binding: FragmentUserQrcodeListBinding
    private lateinit var invoiceList: MutableList<ParkingInvoice>
    private val userQRCodeListViewModel: UserQRCodeListViewModel by hiltNavGraphViewModels(R.id.userQRCodeListFragment)
    private lateinit var userQRCodeListAdapter: UserQRCodeListAdapter

    override fun initViewBinding(): View {
        binding = FragmentUserQrcodeListBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun observeViewModel() {

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                userQRCodeListViewModel.stateUi.map { it.isShowDialog }.distinctUntilChanged()
                    .collect {
                        if (it) {
                            handleShowUserQRCodeDialog(userQRCodeListViewModel.stateUi.value.userId)
                            userQRCodeListViewModel.showDialog()
                        }
                    }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                userQRCodeListViewModel.stateUi.collect {
                    if (it.isLoading) showLoading() else hideLoading()
                    if (it.error.isNotEmpty()) {
                        showError(it.error)
                        userQRCodeListViewModel.showError()
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

    override fun initListener() {
        showBottomNavigation()
        userQRCodeListViewModel.getParkingInvoiceList()
        invoiceList = mutableListOf()
        userQRCodeListAdapter = UserQRCodeListAdapter(invoiceList)
        userQRCodeListAdapter.setEventClick {
            handleClickItem(it)
        }
        binding.vpgUserQRcodeList.apply {
            adapter = userQRCodeListAdapter
        }
        binding.btnHistoryUserQRCodeList.setOnClickListener {
            getNavController().navigate(R.id.myinvoiceFragment)
            hideBottomNavigation()
        }
        binding.ivHeaderUserQrcodeList.setOnClickListener {
            userQRCodeListViewModel.getUserID()
        }
    }

    private fun handleClickItem(parkingInvoice: ParkingInvoice) {

    }

    private fun handleShowUserQRCodeDialog(userId: String) {
        val userQRCode = UserQRCode(userId, TimeUtil.getCurrentTime().toString())
        AESEncyptionUtil.encrypt(userQRCode.toString())?.apply {
            UserQRCodeDialog(
                requireContext(),
                QRcodeUtil.getQrCodeBitmap(this),
                TimeUtil.getDateCurrentTime()
            ).show()
        }
    }

}