package com.example.parkingqr.ui.components.parkinglotmanagement

import android.view.View
import com.example.parkingqr.R
import com.example.parkingqr.databinding.FragmentAdminHomeBinding
import com.example.parkingqr.ui.base.BaseFragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class AdminHomeFragment : BaseFragment() {

    private lateinit var binding: FragmentAdminHomeBinding

    override fun observeViewModel() {

    }

    override fun initViewBinding(): View {
        binding = FragmentAdminHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun initListener() {
        binding.tvSignOutAdminHome.setOnClickListener {
            Firebase.auth.signOut().let {
                getNavController().navigate(R.id.loginFragment)
            }
        }
        binding.crdVehicleMangeAdminHome.setOnClickListener {
            getNavController().navigate(R.id.vehicleManagementFragment)
        }

        binding.crdParkingLotMangeAdminHome.setOnClickListener {
            getNavController().navigate(R.id.parkingLotListFragment)
        }
        binding.crdUserMangeAdminHome.setOnClickListener {
            getNavController().navigate(R.id.userManagementFragment)
        }

    }

}