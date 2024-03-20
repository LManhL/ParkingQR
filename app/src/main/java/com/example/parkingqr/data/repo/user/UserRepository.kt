package com.example.parkingqr.data.repo.user

import com.example.parkingqr.data.remote.State
import com.example.parkingqr.domain.model.invoice.UserInvoice
import com.example.parkingqr.domain.model.user.*
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getAccountByEmail(email: String): Flow<State<MutableList<Account>>>
    fun createNewUser(user: User): Flow<State<Boolean>>
    fun createNewParkingLotManager(parkingLotManager: ParkingLotManager): Flow<State<Boolean>>
    fun createNewParkingAttendant(parkingAttendant: ParkingAttendant): Flow<State<Boolean>>
    fun getAccountInformation(): Flow<State<Account>>
    fun getAllAccount(): Flow<State<MutableList<Account>>>
    fun getAccountById(id: String): Flow<State<Account>>
    fun updateAccount(account: Account): Flow<State<Boolean>>
    fun deleteAccount(id: String): Flow<State<Boolean>>
    fun blockAccount(id: String): Flow<State<Boolean>>
    fun activeAccount(id: String): Flow<State<Boolean>>
    fun searchUserById(userId: String): Flow<State<MutableList<User>>>

    fun searchUserInvoiceById(userId: String): Flow<State<MutableList<UserInvoice>>>
    fun getUserId(): Flow<State<String>>
}