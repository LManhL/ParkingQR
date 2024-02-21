package com.example.parkingqr.data.remote.user

import com.example.parkingqr.data.remote.State
import com.example.parkingqr.data.remote.dto.user.UserFirebase
import com.example.parkingqr.domain.model.user.UserDetail
import com.example.parkingqr.domain.model.user.UserInvoice
import com.example.parkingqr.domain.model.user.UserLogin
import com.example.parkingqr.domain.model.user.UserProfile
import kotlinx.coroutines.flow.Flow

interface UserRemoteData {
    fun getUserByEmail(email: String): Flow<State<MutableList<UserFirebase>>>
    fun createNewUser(user: UserFirebase): Flow<State<Boolean>>
    fun getUserInformation(): Flow<State<UserFirebase>>
    fun getAllUser(): Flow<State<MutableList<UserFirebase>>>
    fun getUserById(id: String): Flow<State<UserFirebase>>
    fun updateUser(user: UserFirebase): Flow<State<Boolean>>
    fun deleteUser(id: String): Flow<State<Boolean>>
    fun blockUser(id: String): Flow<State<Boolean>>
    fun activeUser(id: String): Flow<State<Boolean>>
    fun searchUserById(userId: String): Flow<State<MutableList<UserFirebase>>>
}