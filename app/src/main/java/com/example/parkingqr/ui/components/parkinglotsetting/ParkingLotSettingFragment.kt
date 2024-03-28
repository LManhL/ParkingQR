package com.example.parkingqr.ui.components.parkinglotsetting

import android.view.View
import com.example.parkingqr.R
import com.example.parkingqr.databinding.FragmentParkinglotSettingBinding
import com.example.parkingqr.ui.base.BaseFragment

class ParkingLotSettingFragment: BaseFragment() {
    private lateinit var binding: FragmentParkinglotSettingBinding
    override fun observeViewModel() {

    }

    override fun initViewBinding(): View {
        binding = FragmentParkinglotSettingBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun initListener() {
        showActionBar(getString(R.string.parking_lot_setting_fragment_name))
        binding.crdBillingTypeParkingLotSetting.setOnClickListener{
            getNavController().navigate(R.id.billingTypeListFragment)
        }
    }
}