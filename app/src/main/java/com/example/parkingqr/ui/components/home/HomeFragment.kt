package com.example.parkingqr.ui.components.home

import android.view.View
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.parkingqr.R
import com.example.parkingqr.databinding.FragmentHomeBinding
import com.example.parkingqr.ui.base.BaseFragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch


class HomeFragment : BaseFragment() {


    private lateinit var binding: FragmentHomeBinding
    private val viewModel: HomeViewModel by hiltNavGraphViewModels(R.id.homeFragment)

    override fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.map { it.isLoading }.collect {
                    if (it) showLoading()
                    else hideLoading()
                }
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.map { it.isNeedToCreateParkingLot }.distinctUntilChanged()
                    .collect {
                        displayUi(it)
                    }
            }
        }
    }

    override fun initViewBinding(): View {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        viewModel.getParkingLotManagerToCheck()
        return binding.root
    }

    override fun initListener() {
        hideActionBar()
        binding.ivParkingHome.setOnClickListener {
            viewModel.uiState.value.isNeedToCreateParkingLot.let {
                if (it) {
                    showMessage("Vui lòng đăng ký bãi gửi xe trước")
                } else {
                    getNavController().navigate(R.id.parkingFragment)
                }
            }
        }
        binding.ivInvoiceListHome.setOnClickListener {
            viewModel.uiState.value.isNeedToCreateParkingLot.let {
                if (it) {
                    showMessage("Vui lòng đăng ký bãi gửi xe trước")
                } else {
                    getNavController().navigate(R.id.invoiceListFragment)
                }
            }
        }
        binding.ivSettingHome.setOnClickListener {
            viewModel.uiState.value.isNeedToCreateParkingLot.let {
                if (it) {
                    showMessage("Vui lòng đăng ký bãi gửi xe trước")
                } else {
                    getNavController().navigate(R.id.parkingLotSettingFragment)
                }
            }
        }
        binding.ivSecurityCameraHome.setOnClickListener {
            getNavController().navigate(R.id.securityCameraFragment)
        }
        binding.tvSignOutHome.setOnClickListener {
            Firebase.auth.signOut()
            getNavController().navigate(R.id.loginFragment)
        }
        binding.ivCreateParkingLotHome.setOnClickListener {
            goToCreateParkingLotScreen()
        }
    }

    private fun goToCreateParkingLotScreen() {
        viewModel.uiState.value.parkingLot.let { parkingLot ->
            if (parkingLot != null) {
                getNavController().navigate(R.id.parkingLotDetailHomeFragment)
            } else {
                getNavController().navigate(R.id.createParkingLotFragment)
            }
        }
    }

    private fun displayUi(isNeedToCreateParkingLot: Boolean) {
        if (!isNeedToCreateParkingLot) {
            binding.ivCreateParkingLotHome.visibility = View.GONE
            binding.crdStatisticHome.visibility = View.VISIBLE
            binding.tvStatisticInvoiceHome.visibility = View.VISIBLE
            binding.tvShowMoreStatisticInvoiceHome.visibility = View.VISIBLE
            binding.tvRegisterHome.visibility = View.GONE
        } else {
            binding.ivCreateParkingLotHome.visibility = View.VISIBLE
            binding.tvStatisticInvoiceHome.visibility = View.GONE
            binding.tvShowMoreStatisticInvoiceHome.visibility = View.GONE
            binding.tvRegisterHome.visibility = View.VISIBLE
            binding.crdStatisticHome.visibility = View.GONE
        }
    }
}