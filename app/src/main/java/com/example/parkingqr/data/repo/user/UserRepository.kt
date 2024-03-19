package com.example.parkingqr.data.repo.user

import com.example.parkingqr.data.remote.State
import com.example.parkingqr.data.remote.dto.user.AccountFirebase
import com.example.parkingqr.data.remote.dto.user.ParkingAttendantFirebase
import com.example.parkingqr.data.remote.dto.user.ParkingLotManagerFirebase
import com.example.parkingqr.data.remote.dto.user.UserFirebase
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
    fun searchAccountById(userId: String): Flow<State<MutableList<Account>>>
    fun getUserId(): Flow<State<String>>
}