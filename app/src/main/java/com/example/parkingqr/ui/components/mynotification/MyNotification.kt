package com.example.parkingqr.ui.components.mynotification

import android.view.View
import com.example.parkingqr.databinding.FragmentMyNotificationBinding
import com.example.parkingqr.databinding.FragmentMyProfileBinding
import com.example.parkingqr.ui.base.BaseFragment

class MyNotification: BaseFragment() {
    private lateinit var binding: FragmentMyNotificationBinding
    override fun observeViewModel() {

    }

    override fun initViewBinding(): View {
        binding = FragmentMyNotificationBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun initListener() {
        hideActionBar()
        showBottomNavigation()
    }
}