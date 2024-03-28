package com.example.parkingqr.ui.components.parkinglotsetting

import android.view.View
import com.example.parkingqr.R
import com.example.parkingqr.databinding.FragmentBillingTypeListBinding
import com.example.parkingqr.ui.base.BaseFragment

class BillingTypeListFragment : BaseFragment() {
    private lateinit var binding: FragmentBillingTypeListBinding

    override fun initViewBinding(): View {
        binding = FragmentBillingTypeListBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun observeViewModel() {

    }

    override fun initListener() {
        showActionBar(getString(R.string.billing_type_list_fragment_name))
        binding.crdMonthTypeBillingTypeList.setOnClickListener {

        }
        binding.crdHourTypeBillingTypeList.setOnClickListener {
            getNavController().navigate(R.id.billingTypeDetailFragment)
        }
    }
}