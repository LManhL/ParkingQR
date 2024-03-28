package com.example.parkingqr.ui.components.location

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.parkingqr.R
import com.example.parkingqr.databinding.FragmentRegisterMonthlyInvoiceBinding
import com.example.parkingqr.domain.model.parkinglot.MonthlyTicket
import com.example.parkingqr.ui.base.BaseFragment
import kotlinx.coroutines.launch

class RegisterMonthlyInvoiceFragment : BaseFragment() {
    private lateinit var binding: FragmentRegisterMonthlyInvoiceBinding
    private val registerMonthlyInvoiceViewModel: RegisterMonthlyInvoiceViewModel by viewModels()
    private lateinit var monthlyTicketAdapter: MonthlyTicketAdapter
    private lateinit var monthTicketList: MutableList<MonthlyTicket>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.getString(BottomSheetPlaceDetailFragment.PARKING_LOT_ID)?.let {
            registerMonthlyInvoiceViewModel.setParkingLotId(it)
        }
    }

    override fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                registerMonthlyInvoiceViewModel.uiState.collect { uiState ->
                    monthTicketList.clear()
                    monthTicketList.addAll(uiState.monthlyTicketList)
                    monthlyTicketAdapter.notifyDataSetChanged()
                }
            }
        }
    }

    override fun initViewBinding(): View {
        binding = FragmentRegisterMonthlyInvoiceBinding.inflate(layoutInflater)
        monthTicketList = mutableListOf()
        monthlyTicketAdapter = MonthlyTicketAdapter(monthTicketList)
        binding.rlvListRegisterMonthlyInvoice.apply {
            adapter = monthlyTicketAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
        return binding.root
    }

    override fun initListener() {
        showActionBar(getString(R.string.register_monthly_invoice_fragment_name))
        registerMonthlyInvoiceViewModel.getMonthlyTicketList()
    }
}