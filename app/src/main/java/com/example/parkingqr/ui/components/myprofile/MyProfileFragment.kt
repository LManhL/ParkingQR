package com.example.parkingqr.ui.components.myprofile

import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.parkingqr.R
import com.example.parkingqr.databinding.FragmentMyProfileBinding
import com.example.parkingqr.domain.model.user.UserProfile
import com.example.parkingqr.ui.base.BaseFragment
import kotlinx.coroutines.launch

class MyProfileFragment: BaseFragment() {
    private lateinit var binding: FragmentMyProfileBinding
    private val myProfileViewModel: MyProfileViewModel by viewModels()

    override fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                myProfileViewModel.stateUi.collect {
                    if (it.isLoading) showLoading() else hideLoading()
                    if (it.error.isNotEmpty()) {
                        showError(it.error)
                        myProfileViewModel.showError()
                    }
                    if (it.message.isNotEmpty()) {
                        showMessage(it.message)
                        myProfileViewModel.showMessage()
                    }
                    updateUI(it.userProfile)
                    if(it.isSignedOut){
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
        binding.tvSignOutMyProfile.setOnClickListener{
            myProfileViewModel.signOut()
            hideBottomNavigation()
        }
    }
    private fun updateUI(userProfile: UserProfile?){
        binding.clContainerMyProfile.visibility = View.INVISIBLE
        userProfile?.let {
            binding.clContainerMyProfile.visibility = View.VISIBLE
            binding.tvNameMyProfile.text = userProfile.name
        }
    }
}