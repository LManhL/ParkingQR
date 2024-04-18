package com.example.parkingqr.ui.components.location

import android.os.Bundle
import android.view.View
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.parkingqr.R
import com.example.parkingqr.databinding.FragmentRegisterMonthlyInvoiceBinding
import com.example.parkingqr.domain.model.parkinglot.MonthlyTicketType
import com.example.parkingqr.domain.model.payment.BankAccount
import com.example.parkingqr.ui.base.BaseFragment
import com.example.parkingqr.ui.components.MainActivity
import com.example.parkingqr.ui.components.dialog.PaymentSuccessDialog
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

class RegisterMonthlyInvoiceFragment : BaseFragment() {
    private lateinit var binding: FragmentRegisterMonthlyInvoiceBinding
    private val registerMonthlyInvoiceViewModel: RegisterMonthlyInvoiceViewModel by hiltNavGraphViewModels(
        R.id.registerMonthlyInvoiceFragment
    )
    private lateinit var monthlyTicketAdapter: MonthlyTicketAdapter
    private lateinit var bankAccountAdapter: BankAccountAdapter
    private val monthTicketList: MutableList<MonthlyTicketType> = mutableListOf()
    private val bankAccountList: MutableList<BankAccount> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.getString(BottomSheetPlaceDetailFragment.PARKING_LOT_ID)?.let {
            registerMonthlyInvoiceViewModel.setParkingLotId(it)
        }
        monthlyTicketAdapter = MonthlyTicketAdapter(monthTicketList)
        bankAccountAdapter = BankAccountAdapter(bankAccountList)
    }

    override fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                registerMonthlyInvoiceViewModel.uiState.map { it.monthlyTicketTypeList }
                    .distinctUntilChanged().collect { list ->
                        monthTicketList.clear()
                        monthTicketList.addAll(list)
                        monthlyTicketAdapter.notifyDataSetChanged()
                    }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                registerMonthlyInvoiceViewModel.uiState.map { it.selectedVehicle }
                    .distinctUntilChanged().collect { vehicle ->
                        vehicle?.let {
                            binding.tvLabelServicesRegisterMonthlyInvoice.visibility = View.VISIBLE
                            binding.tvLabelServicesRegisterMonthlyInvoice.text =
                                "Các gói dịch vụ dành cho ${it.getVehicleType()}"
                            binding.edtLicensePlateRegisterMonthlyInvoice.setText(it.licensePlate?.uppercase())
                            registerMonthlyInvoiceViewModel.getMonthlyTicketListByVehicleType()
                        }
                    }
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                registerMonthlyInvoiceViewModel.uiState.collect { uiState ->
                    uiState.apply {
                        if (uiState.isLoading) showLoading() else hideLoading()
                        uiState.error.takeIf { it.isNotEmpty() }?.let { error -> showError(error) }
                        isPaymentSuccessful.takeIf { it }?.let {
                            registerMonthlyInvoiceViewModel.createMonthlyTicket()
                        }
                        isCreated.takeIf { it }?.let {
                            handleCreateSuccessfully()
                        }
                    }

                }
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                registerMonthlyInvoiceViewModel.uiState.map { it.payByTokenResponse }
                    .distinctUntilChanged().collect {
                        it?.let {
                            payByTokenWithVNPay(it.url)
                            registerMonthlyInvoiceViewModel.showPaymentPage()
                        }
                    }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                registerMonthlyInvoiceViewModel.uiState.map { it.selectedBankAccount }
                    .distinctUntilChanged().collect {
                        it?.let {
                            bankAccountList.clear()
                            bankAccountList.add(it)
                            bankAccountAdapter.notifyDataSetChanged()
                        }
                    }
            }
        }
    }

    override fun initViewBinding(): View {
        binding = FragmentRegisterMonthlyInvoiceBinding.inflate(layoutInflater)
        binding.rlvListRegisterMonthlyInvoice.apply {
            adapter = monthlyTicketAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
        binding.rlvBankAccountRegisterMonthlyInvoice.apply {
            adapter = bankAccountAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }
        return binding.root
    }

    override fun initListener() {
        showActionBar(getString(R.string.register_monthly_invoice_fragment_name))
        hideBottomNavigation()
        binding.tvChooseVehicleMonthlyInvoice.setOnClickListener {
            getNavController().navigate(R.id.chooseVehicleMonthlyTicket)
        }
        binding.btnPaymentRegisterMonthlyInvoice.setOnClickListener {
            registerMonthlyInvoiceViewModel.selectMonthlyTicket(monthlyTicketAdapter.getSelectedMonthlyTicket())
            registerMonthlyInvoiceViewModel.getInfoToCreateMonthlyTicket()
            registerMonthlyInvoiceViewModel.payByToken()
        }
        binding.llChooseBankRegisterMonthlyInvoice.setOnClickListener {
            getNavController().navigate(R.id.chooseBankAccountFragment)
        }
    }

    private fun payByTokenWithVNPay(url: String) {
        (requireActivity() as MainActivity).openSdk(
            url,
            successAction = { handlePaymentSuccessful() },
            failBackAction = { handlePaymentFail() }
        )
    }

    private fun handlePaymentSuccessful() {
        registerMonthlyInvoiceViewModel.handlePaymentSuccessful()
    }

    private fun handleCreateSuccessfully() {
        PaymentSuccessDialog(requireContext(), 3) {
            getNavController().popBackStack()
        }.show()
    }

    private fun handlePaymentFail() {
        showMessage("Thanh toán không thành công vui lòng thử lại sau ít phút")
    }
}