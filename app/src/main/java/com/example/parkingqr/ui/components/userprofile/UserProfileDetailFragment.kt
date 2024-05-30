package com.example.parkingqr.ui.components.userprofile

import android.app.DatePickerDialog
import android.text.InputType
import android.view.View
import android.widget.DatePicker
import android.widget.EditText
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.parkingqr.R
import com.example.parkingqr.databinding.FragmentUserProfileDetailBinding
import com.example.parkingqr.domain.model.user.Account
import com.example.parkingqr.domain.model.user.User
import com.example.parkingqr.ui.base.BaseFragment
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class UserProfileDetailFragment : BaseFragment() {

    private lateinit var binding: FragmentUserProfileDetailBinding
    private val viewModel: UserProfileViewModel by hiltNavGraphViewModels(R.id.myprofileFragment)
    private val calendar = Calendar.getInstance()
    override fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.stateUi.map { it.user }.distinctUntilChanged().collect {
                    it?.let {
                        showUser(it)
                    }
                }
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.stateUi.map { it.message }.distinctUntilChanged().collect {
                    if (it.isNotEmpty()) {
                        showMessage(it)
                        viewModel.showMessage()
                    }
                }
            }
        }
    }

    override fun initViewBinding(): View {
        binding = FragmentUserProfileDetailBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun initListener() {
        showActionBar("Thông tin cá nhân")
        binding.btnSaveUserProfileDetail.setOnClickListener {
            updateUser()
        }
        binding.edtDateOfBirthUserProfileDetail.setOnClickListener {
            showDatePickerDialog(binding.edtDateOfBirthUserProfileDetail)
        }
    }

    private fun showUser(user: User) {
        binding.llContainerUserProfileDetail.visibility = View.VISIBLE
        binding.edtDateOfBirthUserProfileDetail.inputType = InputType.TYPE_NULL
        binding.edtNameUserProfileDetail.setText(user.account.name)
        binding.edtUserNameUserProfileDetail.setText(user.account.username)
        binding.edtAddressUserProfileDetail.setText(user.account.address)
        binding.edtEmailUserProfileDetail.setText(user.account.email)
        binding.edtIdentifierCodeUserProfileDetail.setText(user.account.personalCode)
        binding.edtDateOfBirthUserProfileDetail.setText(user.account.birthday)
        binding.edtPhoneUserProfileDetail.setText(user.account.phoneNumber)
        if (user.account.status == Account.ACTIVE_STATUS) {
            binding.tvStatusProfileDetail.text = "Hoạt động"
        } else {
            binding.tvStatusProfileDetail.text = "Bị chặn"
        }
    }

    private fun updateUser() {
        val name = binding.edtNameUserProfileDetail.text.toString()
        val username = binding.edtUserNameUserProfileDetail.text.toString()
        val address = binding.edtAddressUserProfileDetail.text.toString()
        val email = binding.edtEmailUserProfileDetail.text.toString()
        val identifier = binding.edtIdentifierCodeUserProfileDetail.text.toString()
        val dateOfBirth = binding.edtDateOfBirthUserProfileDetail.text.toString()
        val phone = binding.edtPhoneUserProfileDetail.text.toString()
        viewModel.updateUserInfo(
            name,
            username,
            address,
            email,
            identifier,
            dateOfBirth,
            phone
        )
    }

    private fun showDatePickerDialog(editText: EditText) {
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { view: DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                // Xử lý khi ngày được chọn
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, monthOfYear)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateEditText(editText)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun updateEditText(editText: EditText) {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        editText.setText(dateFormat.format(calendar.time))
    }
}