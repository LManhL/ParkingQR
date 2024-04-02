package com.example.parkingqr.ui.components.location

import android.view.View
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.parkingqr.R
import com.example.parkingqr.databinding.FragmentChooseVehicleMonthlyTicketBinding
import com.example.parkingqr.domain.model.vehicle.VehicleDetail
import com.example.parkingqr.ui.base.BaseFragment
import kotlinx.coroutines.launch

class ChooseVehicleMonthlyTicketFragment : BaseFragment() {

    private lateinit var binding: FragmentChooseVehicleMonthlyTicketBinding
    private val registerMonthlyInvoiceViewModel: RegisterMonthlyInvoiceViewModel by hiltNavGraphViewModels(
        R.id.registerMonthlyInvoiceFragment
    )
    private lateinit var vehicleRegistrationListAdapter: VehicleRegistrationListAdapter
    private val registrationList = mutableListOf<VehicleDetail>()

    override fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                registerMonthlyInvoiceViewModel.uiState.collect { uiState ->
                    if (uiState.isLoading) showLoading() else hideLoading()
                    uiState.error.takeIf { it.isNotEmpty() }?.let { error ->
                        showError(error)
                        registerMonthlyInvoiceViewModel.showError()
                    }
                    registrationList.clear()
                    registrationList.addAll(uiState.registrationList)
                    vehicleRegistrationListAdapter.notifyDataSetChanged()
                }
            }
        }
    }

    override fun initViewBinding(): View {
        binding = FragmentChooseVehicleMonthlyTicketBinding.inflate(layoutInflater)
        vehicleRegistrationListAdapter = VehicleRegistrationListAdapter(registrationList)
        vehicleRegistrationListAdapter.setClickEvent {
            onClickItem(it)
        }
        binding.rlvChooseVehicleMonthlyTicket.apply {
            adapter = vehicleRegistrationListAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }
        return binding.root
    }

    override fun initListener() {
        showActionBar(getString(R.string.choose_vehicle_monthly_ticket_fragment_name))
        registerMonthlyInvoiceViewModel.getRegistrationList()
    }

    private fun onClickItem(vehicleDetail: VehicleDetail) {
        registerMonthlyInvoiceViewModel.selectVehicle(vehicleDetail)
        getNavController().popBackStack()
    }

}