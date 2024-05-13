package com.example.parkingqr.ui.components.login

import androidx.lifecycle.viewModelScope
import com.example.parkingqr.data.remote.State
import com.example.parkingqr.data.repo.auth.AuthRepository
import com.example.parkingqr.data.repo.user.UserRepository
import com.example.parkingqr.domain.model.user.*
import com.example.parkingqr.ui.base.BaseViewModel
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : BaseViewModel() {
    private val _stateUi = MutableStateFlow(
        LoginStateViewModel()
    )
    val stateUi: StateFlow<LoginStateViewModel> = _stateUi.asStateFlow()

    private var loginJob: Job? = null

    fun doLogin(email: String, password: String) {
        loginJob?.cancel()
        loginJob = viewModelScope.launch {
            authRepository.signIn(email, password).collect { state ->
                when (state) {
                    is State.Loading -> {
                        _stateUi.update {
                            it.copy(isLoading = true)
                        }
                    }
                    is State.Success -> {

                        if (state.data != null) {
                            _stateUi.update {
                                it.copy(
                                    userAuth = state.data
                                )
                            }
                            findAccountThenLogin(email)
                        } else {
                            _stateUi.update {
                                it.copy(
                                    isLoading = false,
                                    message = "Đăng nhập không thành công"
                                )
                            }
                        }
                    }
                    is State.Failed -> {
                        _stateUi.update {
                            it.copy(
                                isLoading = false,
                                error = "Đăng nhập không thành công"
                            )
                        }
                    }
                }
            }
        }
    }

    private fun findAccountThenLogin(email: String) {
        viewModelScope.launch {
            userRepository.getAccountByEmail(email).collect { state ->
                when (state) {
                    is State.Loading -> {}
                    is State.Success -> {
                        if (state.data.isNotEmpty()) {
                            _stateUi.update {
                                it.copy(
                                    isLoading = false,
                                    account = state.data.first(),
                                    isReady = true
                                )
                            }
                        } else {
                            _stateUi.update {
                                it.copy(
                                    isLoading = false,
                                    message = "Đăng nhập không thành công"
                                )
                            }
                        }
                    }
                    is State.Failed -> {
                        _stateUi.update {
                            it.copy(
                                isLoading = false,
                                error = "Đăng nhập không thành công"
                            )
                        }
                    }
                }
            }
        }
    }

    fun showError() {
        _stateUi.update {
            it.copy(
                error = ""
            )
        }
    }

    fun showMessage() {
        _stateUi.update {
            it.copy(
                message = ""
            )
        }
    }

    data class LoginStateViewModel(
        val isLoading: Boolean = false,
        val userAuth: FirebaseUser? = null,
        val account: Account? = null,
        val isReady: Boolean = false,
        val message: String = "",
        val error: String = ""
    )
}
