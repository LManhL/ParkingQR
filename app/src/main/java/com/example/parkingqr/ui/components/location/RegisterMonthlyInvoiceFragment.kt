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
import com.example.parkingqr.ui.base.BaseFragment
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class RegisterMonthlyInvoiceFragment : BaseFragment() {
    private lateinit var binding: FragmentRegisterMonthlyInvoiceBinding
    private val registerMonthlyInvoiceViewModel: RegisterMonthlyInvoiceViewModel by hiltNavGraphViewModels(
        R.id.registerMonthlyInvoiceFragment
    )
    private lateinit var monthlyTicketAdapter: MonthlyTicketAdapter
    private val monthTicketList: MutableList<MonthlyTicketType> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.getString(BottomSheetPlaceDetailFragment.PARKING_LOT_ID)?.let {
            registerMonthlyInvoiceViewModel.setParkingLotId(it)
        }
        monthlyTicketAdapter = MonthlyTicketAdapter(monthTicketList)
    }

    override fun observeViewModel() {

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                registerMonthlyInvoiceViewModel.uiState.collect { uiState ->
                    if (uiState.isLoading) showLoading() else hideLoading()
                    uiState.error.takeIf { it.isNotEmpty() }?.let { error -> showError(error) }
                }
            }
        }

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
                        isReadyToCreate.takeIf { it }?.let {
                            registerMonthlyInvoiceViewModel.createMonthlyTicket()
                        }
                        isCreated.takeIf { it }?.let {
                            showMessage("Mua vé thành công")
                            getNavController().popBackStack()
                        }
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
        return binding.root
    }

    override fun initListener() {
        showActionBar(getString(R.string.register_monthly_invoice_fragment_name))
        binding.tvChooseVehicleMonthlyInvoice.setOnClickListener {
            getNavController().navigate(R.id.chooseVehicleMonthlyTicket)
        }
        binding.btnPaymentRegisterMonthlyInvoice.setOnClickListener {
            registerMonthlyInvoiceViewModel.selectMonthlyTicket(monthlyTicketAdapter.getSelectedMonthlyTicket())
            registerMonthlyInvoiceViewModel.getInfoToCreateMonthlyTicket()
        }
    }
}