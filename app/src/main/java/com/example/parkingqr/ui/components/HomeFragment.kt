package com.example.parkingqr.ui.components

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.parkingqr.R
import com.example.parkingqr.databinding.FragmentHomeBinding
import com.example.parkingqr.ui.base.BaseFragment


class HomeFragment: BaseFragment() {

    private lateinit var binding: FragmentHomeBinding

    override fun observeViewModel() {

    }

    override fun initViewBinding(): View {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun initListener() {
        binding.ivParkingHome.setOnClickListener{
            getNavController().navigate(R.id.parkingFragment)
        }
    }
    override fun onResume() {
        super.onResume()
//        (requireActivity() as AppCompatActivity).supportActionBar?.hide()
    }

    override fun onStop() {
        super.onStop()
//        (requireActivity() as AppCompatActivity).supportActionBar?.show()
    }
}