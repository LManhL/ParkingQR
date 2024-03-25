package com.example.parkingqr.ui.components.login

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.parkingqr.data.remote.State
import com.example.parkingqr.data.repo.auth.AuthRepository
import com.example.parkingqr.data.repo.user.UserRepository
import com.example.parkingqr.domain.model.user.AccountRole
import com.example.parkingqr.domain.model.user.ParkingAttendant
import com.example.parkingqr.domain.model.user.ParkingLotManager
import com.example.parkingqr.domain.model.user.Person
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
                                    user = state.data,
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

    fun findUserRole(email: String) {
        viewModelScope.launch {
            userRepository.getAccountByEmail(email).collect { state ->
                when (state) {
                    is State.Loading -> {}
                    is State.Success -> {

                        if (state.data.isNotEmpty()) {
                            val account = state.data[0]
                            _stateUi.update {
                                it.copy(
                                    role = account.getAccountRole(),
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

    private fun getParkingLotManagerById() {
        viewModelScope.launch {
            userRepository.getParkingLotManagerById(stateUi.value.user?.uid ?: "0")
                .collect { state ->
                    when (state) {
                        is State.Loading -> {}
                        is State.Success -> {
                            _stateUi.update {
                                it.copy(
                                    isLoading = false,
                                    person = state.data
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

    fun saveAccountInfo() {
        stateUi.value.person?.let { person ->
            if (person is ParkingLotManager) {
                userRepository.saveLocalParkingLotId(person.parkingLotId)
            }
            if (person is ParkingAttendant) {
                userRepository.saveLocalParkingLotId(person.parkingLotId)
            }
        }
        _stateUi.update {
            it.copy(
                isReady = true,
            )
        }
    }

    fun getDetailAccountInfo() {
        stateUi.value.role?.let { accountRole ->
            if (accountRole == AccountRole.PARKING_LOT_MANAGER) {
                getParkingLotManagerById()
            } else if (accountRole == AccountRole.PARKING_ATTENDANT) {

            } else {

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
}

data class LoginStateViewModel(
    val isLoading: Boolean = false,
    val user: FirebaseUser? = null,
    val role: AccountRole? = null,
    val person: Person? = null,
    val error: String = "",
    val message: String = "",
    val isReady: Boolean = false
)