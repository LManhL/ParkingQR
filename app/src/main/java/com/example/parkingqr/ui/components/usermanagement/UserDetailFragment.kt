package com.example.parkingqr.ui.components.usermanagement

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.DatePicker
import android.widget.EditText
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.parkingqr.R
import com.example.parkingqr.databinding.FragmentUserDetailBinding
import com.example.parkingqr.domain.model.user.Account
import com.example.parkingqr.domain.model.user.User
import com.example.parkingqr.ui.base.BaseFragment
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class UserDetailFragment : BaseFragment() {

    private lateinit var binding: FragmentUserDetailBinding
    private val userDetailViewModel: UserDetailViewModel by viewModels()
    private var id: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        id = arguments?.getString(UserManagementFragment.USER_DETAIL_KEY)
        id?.let {
            userDetailViewModel.getUserById(it)
        }
    }

    override fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                userDetailViewModel.stateUi.map { it.isLoading }.distinctUntilChanged().collect {
                    if (it) showLoading() else hideLoading()
                }
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                userDetailViewModel.stateUi.map { it.user }.distinctUntilChanged().collect {
                    it?.let {
                        showUserDetail(it)
                    }
                }
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                userDetailViewModel.stateUi.map { it.needOut }.distinctUntilChanged().collect {
                    if (it) getNavController().popBackStack()
                }
            }
        }
    }

    override fun initViewBinding(): View {
        binding = FragmentUserDetailBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun initListener() {
        showActionBar(getString(R.string.user_management_fragment_name))
        binding.btnSaveUserDetail.setOnClickListener {
            if (binding.btnSaveUserDetail.text.toString().contains("Bỏ")) {
                activeUser()
            } else {
                blockUser()
            }
        }
    }

    private fun activeUser() {
        userDetailViewModel.activeUser()
    }

    private fun blockUser() {
        userDetailViewModel.blockUser()
    }

    private fun showUserDetail(user: User) {
        binding.llContainerUserDetail.visibility = View.VISIBLE
        binding.edtDateOfBirthUserDetail.inputType = InputType.TYPE_NULL
        binding.edtNameUserDetail.setText(user.account.name)
        binding.edtUserNameUserDetail.setText(user.account.username)
        binding.edtAddressUserDetail.setText(user.account.address)
        binding.edtEmailUserDetail.setText(user.account.email)
        binding.edtIdentifierCodeUserDetail.setText(user.account.personalCode)
        binding.edtDateOfBirthUserDetail.setText(user.account.birthday)
        binding.edtPhoneUserDetail.setText(user.account.phoneNumber)

        if (user.account.status == Account.ACTIVE_STATUS) {
            binding.btnSaveUserDetail.text = "Chặn người dùng"
        } else {
            binding.btnSaveUserDetail.text = "Bỏ chặn người dùng"
        }
    }

}