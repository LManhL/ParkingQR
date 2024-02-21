package com.example.parkingqr.data.repo.user

import com.example.parkingqr.data.remote.State
import com.example.parkingqr.domain.model.user.UserDetail
import com.example.parkingqr.domain.model.user.UserInvoice
import com.example.parkingqr.domain.model.user.UserLogin
import com.example.parkingqr.domain.model.user.UserProfile
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getUserByEmail(email: String): Flow<State<MutableList<UserLogin>>>
    fun createNewUser(userLogin: UserLogin): Flow<State<Boolean>>
    fun getUserInformation(): Flow<State<UserProfile>>
    fun getAllUser(): Flow<State<MutableList<UserDetail>>>
    fun getUserById(id: String): Flow<State<UserDetail>>
    fun updateUser(userDetail: UserDetail): Flow<State<Boolean>>
    fun deleteUser(id: String): Flow<State<Boolean>>
    fun blockUser(id: String): Flow<State<Boolean>>
    fun activeUser(id: String): Flow<State<Boolean>>
    fun searchUserById(userId: String): Flow<State<MutableList<UserInvoice>>>
}