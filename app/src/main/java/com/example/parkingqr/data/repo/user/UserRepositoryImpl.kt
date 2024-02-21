package com.example.parkingqr.data.repo.user

import com.example.parkingqr.data.local.user.UserLocalData
import com.example.parkingqr.data.mapper.*
import com.example.parkingqr.data.remote.State
import com.example.parkingqr.data.remote.user.UserRemoteData
import com.example.parkingqr.domain.model.user.UserDetail
import com.example.parkingqr.domain.model.user.UserInvoice
import com.example.parkingqr.domain.model.user.UserLogin
import com.example.parkingqr.domain.model.user.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userRemoteData: UserRemoteData,
    private val userLocalData: UserLocalData
) : UserRepository {
    override fun getUserByEmail(email: String): Flow<State<MutableList<UserLogin>>> {
        return userRemoteData.getUserByEmail(email).map { state ->
            when (state) {
                is State.Loading -> State.loading()
                is State.Success -> State.success(state.data.map { it.mapToUserLogin() }
                    .toMutableList())
                is State.Failed -> State.failed(state.message)
            }
        }
    }

    override fun createNewUser(userLogin: UserLogin): Flow<State<Boolean>> {
        return userRemoteData.createNewUser(userLogin.mapToUserFirebase())
    }

    override fun getUserInformation(): Flow<State<UserProfile>> {
        return userRemoteData.getUserInformation().map { state ->
            when (state) {
                is State.Loading -> State.loading()
                is State.Success -> State.success(state.data.mapToUserProfile())
                is State.Failed -> State.failed(state.message)
            }
        }
    }

    override fun getAllUser(): Flow<State<MutableList<UserDetail>>> {
        return userRemoteData.getAllUser().map { state ->
            when (state) {
                is State.Loading -> State.loading()
                is State.Success -> State.success(state.data.map { it.mapToUserDetail() }
                    .toMutableList())
                is State.Failed -> State.failed(state.message)
            }

        }
    }

    override fun getUserById(id: String): Flow<State<UserDetail>> {
        return userRemoteData.getUserById(id).map { state ->
            when (state) {
                is State.Loading -> State.loading()
                is State.Success -> State.success(state.data.mapToUserDetail())
                is State.Failed -> State.failed(state.message)
            }

        }
    }

    override fun updateUser(userDetail: UserDetail): Flow<State<Boolean>> {
        return userRemoteData.updateUser(userDetail.mapToUserFirebase())
    }

    override fun deleteUser(id: String): Flow<State<Boolean>> {
        return userRemoteData.deleteUser(id)
    }

    override fun blockUser(id: String): Flow<State<Boolean>> {
        return userRemoteData.blockUser(id)
    }

    override fun activeUser(id: String): Flow<State<Boolean>> {
        return userRemoteData.activeUser(id)
    }

    override fun searchUserById(userId: String): Flow<State<MutableList<UserInvoice>>> {
        return userRemoteData.searchUserById(userId).map { state ->
            when (state) {
                is State.Loading -> State.loading()
                is State.Success -> State.success(state.data.map { it.mapToUserInvoice() }
                    .toMutableList())
                is State.Failed -> State.failed(state.message)
            }

        }
    }
}