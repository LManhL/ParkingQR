package com.example.parkingqr.ui.components.userprofile

import androidx.lifecycle.viewModelScope
import com.example.parkingqr.data.remote.State
import com.example.parkingqr.data.repo.auth.AuthRepository
import com.example.parkingqr.data.repo.user.UserRepository
import com.example.parkingqr.domain.model.user.Account
import com.example.parkingqr.domain.model.user.User
import com.example.parkingqr.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository
) :
    BaseViewModel() {

    private val _stateUi = MutableStateFlow(
        MyProfileStateViewModel()
    )
    val stateUi: StateFlow<MyProfileStateViewModel> = _stateUi.asStateFlow()
    private var getUserInforJob: Job? = null

    init {
        getUserInfoDetail()
    }

    fun getUserInformation() {
        getUserInforJob?.cancel()
        getUserInforJob = viewModelScope.launch {
            userRepository.getAccountInformation().collect { state ->
                when (state) {
                    is State.Loading -> {
                        _stateUi.update {
                            it.copy(isLoading = true)
                        }
                    }

                    is State.Success -> {
                        _stateUi.update {
                            it.copy(
                                account = state.data,
                                isLoading = false
                            )
                        }
                    }

                    is State.Failed -> {
                        _stateUi.update {
                            it.copy(
                                isLoading = false,
                                error = "Lỗi không xác định"
                            )
                        }
                    }
                }
            }
        }
    }

    fun updateUserInfo(
        name: String,
        username: String,
        address: String,
        email: String,
        identifier: String,
        dateOfBirth: String,
        phone: String
    ) {
        val updatedUser = stateUi.value.user.apply {
            this?.account?.name = name
            this?.account?.username = username
            this?.account?.address = address
            this?.account?.email = email
            this?.account?.personalCode = identifier
            this?.account?.birthday = dateOfBirth
            this?.account?.phoneNumber = phone
        }
        updatedUser?.let {
            viewModelScope.launch {
                userRepository.updateUser(it).collect { state ->
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
                                    message = "Cập nhật thành công"
                                )
                            }
                        }

                        is State.Failed -> {
                            _stateUi.update {
                                it.copy(
                                    isLoading = false,
                                    error = "Lỗi không xác định"
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun getUserInfoDetail() {
        viewModelScope.launch {
            userRepository.getCurrentUserInfo().collect { state ->
                when (state) {
                    is State.Loading -> {
                        _stateUi.update {
                            it.copy(isLoading = true)
                        }
                    }

                    is State.Success -> {
                        _stateUi.update {
                            it.copy(
                                user = state.data,
                                isLoading = false
                            )
                        }
                    }

                    is State.Failed -> {
                        _stateUi.update {
                            it.copy(
                                isLoading = false,
                                error = "Lỗi không xác định"
                            )
                        }
                    }
                }
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut().collect { state ->
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
                                isSignedOut = true
                            )
                        }
                    }

                    is State.Failed -> {
                        _stateUi.update {
                            it.copy(
                                isLoading = false,
                                error = "Lỗi không xác định"
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


    data class MyProfileStateViewModel(
        val isLoading: Boolean = false,
        val error: String = "",
        val message: String = "",
        val account: Account? = null,
        val user: User? = null,
        val isSignedOut: Boolean = false
    )
}