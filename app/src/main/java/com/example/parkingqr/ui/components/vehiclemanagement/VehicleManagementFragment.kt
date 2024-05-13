package com.example.parkingqr.ui.components.vehiclemanagement

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.parkingqr.R
import com.example.parkingqr.databinding.FragmentVehicleManagementBinding
import com.example.parkingqr.domain.model.vehicle.VehicleDetail
import com.example.parkingqr.ui.base.BaseFragment
import com.example.parkingqr.ui.components.vehiclemanagement.VehicleDetailAdminFragment.Companion.UPDATE_SUCCESSFULLY
import kotlinx.coroutines.launch

class VehicleManagementFragment : BaseFragment() {

    companion object {
        const val VEHICLE_ID = "VEHICLE_ID"
        const val ACTION_PASS_BACK = "ACTION_PASS_BACK"
    }

    private lateinit var binding: FragmentVehicleManagementBinding
    private val vehicleManagementViewModel: VehicleManagementViewModel by viewModels()
    private lateinit var vehicleManagementAdapter: VehicleManagementAdapter
    private val registrationList = mutableListOf<VehicleDetail>()

    override fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                vehicleManagementViewModel.stateUi.collect {
                    if (it.isLoading) showLoading() else hideLoading()
                    if (it.error.isNotEmpty()) {
                        showError(it.error)
                        vehicleManagementViewModel.showError()
                    }
                    if (registrationList.isEmpty()) registrationList.addAll(it.registrationList)
                    else {
                        registrationList.clear()
                        registrationList.addAll(it.registrationList)
                    }
                    vehicleManagementAdapter.notifyDataSetChanged()
                }
            }
        }
    }

    override fun initViewBinding(): View {
        binding = FragmentVehicleManagementBinding.inflate(layoutInflater)
        vehicleManagementAdapter = VehicleManagementAdapter(registrationList)
        vehicleManagementAdapter.setClickEvent {
            onClickItem(it)
        }
        binding.rlvVehicleListVehicleManagement.apply {
            adapter = vehicleManagementAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }
        return binding.root
    }

    override fun initListener() {
        hideActionBar()

        getNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<String>(
            ACTION_PASS_BACK
        )?.observe(
            viewLifecycleOwner
        ) { result ->
            if (result == UPDATE_SUCCESSFULLY) {
                vehicleManagementViewModel.getVehicleList()
            }
            getNavController().currentBackStackEntry?.savedStateHandle?.remove<String>(
                ACTION_PASS_BACK
            )
        }

    }

    private fun onClickItem(vehicleDetail: VehicleDetail) {
        val bundle = Bundle()
        bundle.putString(VEHICLE_ID, vehicleDetail.id)
        getNavController().navigate(R.id.vehicleDetailAdminFragment, bundle)
    }

}