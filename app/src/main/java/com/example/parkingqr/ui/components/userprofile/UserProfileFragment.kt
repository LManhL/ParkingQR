package com.example.parkingqr.ui.components.userprofile

import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.parkingqr.R
import com.example.parkingqr.databinding.FragmentMyProfileBinding
import com.example.parkingqr.domain.model.user.Account
import com.example.parkingqr.ui.base.BaseFragment
import kotlinx.coroutines.launch

class UserProfileFragment : BaseFragment() {
    private lateinit var binding: FragmentMyProfileBinding
    private val userProfileViewModel: UserProfileViewModel by viewModels()

    override fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                userProfileViewModel.stateUi.collect {
                    if (it.isLoading) showLoading() else hideLoading()
                    if (it.error.isNotEmpty()) {
                        showError(it.error)
                        userProfileViewModel.showError()
                    }
                    if (it.message.isNotEmpty()) {
                        showMessage(it.message)
                        userProfileViewModel.showMessage()
                    }
                    updateUI(it.account)
                    if (it.isSignedOut) {
                        getNavController().navigate(R.id.loginFragment)
                    }
                }
            }
        }
    }


    override fun initViewBinding(): View {
        binding = FragmentMyProfileBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun initListener() {
        hideActionBar()
        showBottomNavigation()
        binding.tvRegisterMyProfile.setOnClickListener {
            getNavController().navigate(R.id.vehicleRegistrationListFragment)
            hideBottomNavigation()
        }
        binding.tvSignOutMyProfile.setOnClickListener {
            userProfileViewModel.signOut()
            hideBottomNavigation()
        }
        binding.tvHistoryMyProfile.setOnClickListener {
            getNavController().navigate(R.id.myinvoiceFragment)
            hideBottomNavigation()
        }
        binding.tvConnectBankAccountMyProfile.setOnClickListener {
            getNavController().navigate(R.id.connectBankAccountFragment)
            hideBottomNavigation()
        }
        userProfileViewModel.getUserInformation()
    }

    private fun updateUI(account: Account?) {
        binding.clContainerMyProfile.visibility = View.INVISIBLE
        account?.let {
            binding.clContainerMyProfile.visibility = View.VISIBLE
            binding.tvNameMyProfile.text = it.name
        }
    }
}