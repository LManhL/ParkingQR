package com.example.parkingqr.ui.components.login

import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.parkingqr.R
import com.example.parkingqr.databinding.FragmentLoginBinding
import com.example.parkingqr.domain.model.user.AccountRole
import com.example.parkingqr.ui.base.BaseFragment
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch

class LoginFragment : BaseFragment() {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var auth: FirebaseAuth
    private val loginViewModel: LoginViewModel by viewModels()

    override fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                loginViewModel.stateUi.collect {
                    if (it.isLoading) showLoading() else hideLoading()
                    if (it.error.isNotEmpty()) {
                        showError(it.error)
                        loginViewModel.showError()
                    }
                    if (it.message.isNotEmpty()) {
                        showMessage(it.message)
                        loginViewModel.showMessage()
                    }

                    it.user?.let { user ->
                        loginViewModel.findUserRole(user.email ?: "")
                    }
                    it.role?.let {
                        loginViewModel.getDetailAccountInfo()
                    }
                    it.person?.let { person ->
                        if (!it.isReady) loginViewModel.saveAccountInfo()
                    }
                    if (it.isReady) {
                        it.role?.let { role ->
                            handleNavigate(role)
                        }
                    }
                }
            }
        }
    }

    override fun initViewBinding(): View {
        binding = FragmentLoginBinding.inflate(layoutInflater)
        auth = Firebase.auth
        return binding.root
    }

    override fun initListener() {
        hideActionBar()
        binding.btnSignInLogin.setOnClickListener {
            handleLogin()
        }
        binding.tvGoToSignUpLogin.setOnClickListener {
            getNavController().navigate(R.id.signUpFragment)
        }
    }

    private fun handleLogin() {
        val email = binding.edtEmailLogin.text.toString()
        val password = binding.edtPasswordLogin.text.toString()

        if (email.isEmpty()) {
            binding.edtEmailLogin.setError("Email không được rỗng")
            return
        }
        if (!validateEmail(email)) {
            binding.edtEmailLogin.setError("Email không đúng định dạng")
            return
        }
        if (password.isEmpty()) {
            showMessage("Mật khẩu không được rỗng")
            return
        }

        loginViewModel.doLogin(email, password)
    }

    private fun validateEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$"
        return email.matches(emailRegex.toRegex())
    }

    private fun handleNavigate(accountRole: AccountRole) {
        when (accountRole) {
            AccountRole.PARKING_LOT_MANAGER -> {
                getNavController().navigate(R.id.homeFragment)
            }
            AccountRole.PARKING_ATTENDANT -> {
                getNavController().navigate(R.id.homeFragment)
            }
            AccountRole.USER -> {
                getNavController().navigate(R.id.userQRCodeListFragment)
            }
            AccountRole.ADMIN -> {
                getNavController().navigate(R.id.userManagementFragment)
            }
        }
    }
}