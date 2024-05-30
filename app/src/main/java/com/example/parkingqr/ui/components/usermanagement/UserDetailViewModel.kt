package com.example.parkingqr.ui.components.usermanagement

import androidx.lifecycle.viewModelScope
import com.example.parkingqr.data.remote.State
import com.example.parkingqr.data.repo.user.UserRepository
import com.example.parkingqr.domain.model.user.User
import com.example.parkingqr.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserDetailViewModel @Inject constructor(private val repository: UserRepository) :
    BaseViewModel() {

    private val _stateUi = MutableStateFlow(
        UserDetailState()
    )
    val stateUi: StateFlow<UserDetailState> = _stateUi.asStateFlow()

    fun getUserById(id: String) {
        viewModelScope.launch {
            repository.getUserById(id).collect { state ->
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
                                error = state.message
                            )
                        }
                    }
                }
            }
        }
    }

    fun blockUser() {
        viewModelScope.launch {
            stateUi.value.user?.id?.let { id ->
                repository.blockUser(id).collect { state ->
                    when (state) {
                        is State.Loading -> {
                            _stateUi.update {
                                it.copy(isLoading = true)
                            }
                        }

                        is State.Success -> {
                            _stateUi.update {
                                it.copy(
                                    message = "Chặn tài khoản thành công",
                                    isLoading = false,
                                    needOut = true
                                )
                            }
                        }

                        is State.Failed -> {
                            _stateUi.update {
                                it.copy(
                                    isLoading = false,
                                    error = state.message
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    fun activeUser() {
        viewModelScope.launch {
            stateUi.value.user?.id?.let { id ->
                repository.activeUser(id).collect { state ->
                    when (state) {
                        is State.Loading -> {
                            _stateUi.update {
                                it.copy(isLoading = true)
                            }
                        }

                        is State.Success -> {
                            _stateUi.update {
                                it.copy(
                                    message = "Bỏ chặn tài khoản thành công ",
                                    isLoading = false,
                                    needOut = true
                                )
                            }
                        }

                        is State.Failed -> {
                            _stateUi.update {
                                it.copy(
                                    isLoading = false,
                                    error = state.message
                                )
                            }
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

    data class UserDetailState(
        val isLoading: Boolean = false,
        val error: String = "",
        val message: String = "",
        val user: User? = null,
        val needOut: Boolean = false,
    )
}