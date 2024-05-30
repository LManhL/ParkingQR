package com.example.parkingqr.ui.components.usermanagement

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.parkingqr.R
import com.example.parkingqr.databinding.FragmentUserManagementBinding
import com.example.parkingqr.domain.model.user.User
import com.example.parkingqr.ui.base.BaseFragment
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class UserManagementFragment : BaseFragment() {

    companion object {
        const val USER_DETAIL_KEY = "USER_DETAIL_KEY"
    }

    private lateinit var binding: FragmentUserManagementBinding
    private val userAdapter = UserManagementAdapter(mutableListOf())
    private val userManagementViewModel: UserManagementViewModel by viewModels()

    override fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                userManagementViewModel.stateUi.map { it.isLoading }.distinctUntilChanged()
                    .collect {
                        if (it) showLoading() else hideLoading()
                    }
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                userManagementViewModel.stateUi.map { it.userList }.distinctUntilChanged()
                    .collect {
                        userAdapter.addAll(it)
                    }
            }
        }
    }

    override fun initViewBinding(): View {
        binding = FragmentUserManagementBinding.inflate(layoutInflater)
        userAdapter.setEventClick {
            handleClickItem(it)
        }
        binding.rlvUserListUserManagement.apply {
            adapter = userAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }
        return binding.root
    }

    override fun initListener() {
        showActionBar("Quản lý người dùng")
    }

    override fun onDestroy() {
        super.onDestroy()
        hideActionBar()
    }

    private fun handleClickItem(user: User) {
        val bundle = Bundle()
        bundle.putString(USER_DETAIL_KEY, user.userId)
        getNavController().navigate(R.id.userDetailFragment, bundle)
    }

}