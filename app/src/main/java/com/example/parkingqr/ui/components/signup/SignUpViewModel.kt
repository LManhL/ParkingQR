package com.example.parkingqr.ui.components.signup

import androidx.lifecycle.viewModelScope
import com.example.parkingqr.data.remote.State
import com.example.parkingqr.data.repo.auth.AuthRepository
import com.example.parkingqr.data.repo.user.UserRepository
import com.example.parkingqr.domain.model.user.Account
import com.example.parkingqr.domain.model.user.User
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
class SignUpViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : BaseViewModel() {

    private val _stateUi = MutableStateFlow(
        SignupStateViewModel()
    )
    val stateUi: StateFlow<SignupStateViewModel> = _stateUi.asStateFlow()
    private var signUpJob: Job? = null

    fun doSignUp(email: String, password: String, account: Account) {
        signUpJob?.cancel()
        signUpJob = viewModelScope.launch {
            authRepository.signUp(email, password).collect { state ->
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
                                    user = state.data,
                                    isLoading = false
                                )
                            }
                            createUser(account)
                        } else {
                            _stateUi.update {
                                it.copy(
                                    isLoading = false,
                                    message = "Đăng ký không thành công"
                                )
                            }
                        }
                    }
                    is State.Failed -> {
                        _stateUi.update {
                            it.copy(
                                isLoading = false,
                                error = "Đăng ký không thành công"
                            )
                        }
                    }
                }
            }
        }
    }

    private fun createUser(account: Account) {
        val user = User(account)
        viewModelScope.launch {
            userRepository.createNewUser(user).collect { state ->
                when (state) {
                    is State.Loading -> {
                        _stateUi.update {
                            it.copy(isLoading = true)
                        }
                    }
                    is State.Success -> {
                        _stateUi.update {
                            it.copy(
                                isLoading = false,
                                isCreatedUser = true
                            )
                        }

                    }
                    is State.Failed -> {
                        _stateUi.update {
                            it.copy(
                                isLoading = false,
                                error = "Đăng ký không thành công"
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


    data class SignupStateViewModel(
        val isLoading: Boolean = false,
        val isCreatedUser: Boolean = false,
        val user: FirebaseUser? = null,
        val error: String = "",
        val message: String = "",
    )
}