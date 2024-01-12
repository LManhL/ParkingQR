package com.example.parkingqr.ui.components.mydebt

import android.view.View
import com.example.parkingqr.databinding.FragmentMyDebtBinding
import com.example.parkingqr.ui.base.BaseFragment

class MyDebtFragment : BaseFragment(){
    private lateinit var binding: FragmentMyDebtBinding
    override fun observeViewModel() {

    }

    override fun initViewBinding(): View {
        binding = FragmentMyDebtBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun initListener() {
        hideActionBar()
        showBottomNavigation()
    }
}