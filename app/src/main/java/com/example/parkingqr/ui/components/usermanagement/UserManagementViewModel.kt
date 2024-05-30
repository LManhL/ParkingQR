package com.example.parkingqr.ui.components.usermanagement

import androidx.lifecycle.viewModelScope
import com.example.parkingqr.data.remote.State
import com.example.parkingqr.data.repo.auth.AuthRepository
import com.example.parkingqr.data.repo.user.UserRepository
import com.example.parkingqr.domain.model.user.User
import com.example.parkingqr.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserManagementViewModel @Inject constructor(private val userRepository: UserRepository, private val authRepository: AuthRepository): BaseViewModel() {

    private val _stateUi = MutableStateFlow(
        UserManagementState()
    )
    val stateUi: StateFlow<UserManagementState> = _stateUi.asStateFlow()
    private var getUserJob: Job? = null
    private var searchUserJob: Job? = null

    init {
        getUserList()
    }

    private fun getUserList() {
        getUserJob?.cancel()
        getUserJob = viewModelScope.launch {
            userRepository.getAllUser().collect { state ->
                when (state) {
                    is State.Loading -> {
                        _stateUi.update {
                            it.copy(isLoading = true)
                        }
                    }

                    is State.Success -> {
                        _stateUi.update {
                            it.copy(
                                userList = state.data,
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


    data class UserManagementState(
        val isLoading: Boolean = false,
        val error: String = "",
        val message: String = "",
        val isDeleted: Boolean = false,
        val userList: List<User> = mutableListOf()
    )
}
