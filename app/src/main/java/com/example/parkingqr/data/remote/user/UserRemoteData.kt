package com.example.parkingqr.data.remote.user

import com.example.parkingqr.data.remote.State
import com.example.parkingqr.data.remote.dto.user.AccountFirebase
import com.example.parkingqr.data.remote.dto.user.ParkingAttendantFirebase
import com.example.parkingqr.data.remote.dto.user.ParkingLotManagerFirebase
import com.example.parkingqr.data.remote.dto.user.UserFirebase
import kotlinx.coroutines.flow.Flow

interface UserRemoteData {
    fun getAccountByEmail(email: String): Flow<State<MutableList<AccountFirebase>>>
    fun createNewUser(user: UserFirebase): Flow<State<Boolean>>
    fun createNewParkingLotManager(parkingLotManagerFirebase: ParkingLotManagerFirebase): Flow<State<Boolean>>
    fun createNewParkingAttendant(parkingAttendantFirebase: ParkingAttendantFirebase): Flow<State<Boolean>>
    fun getAccountInformation(): Flow<State<AccountFirebase>>
    fun getAllAccount(): Flow<State<MutableList<AccountFirebase>>>
    fun getAccountById(id: String): Flow<State<AccountFirebase>>
    fun updateAccount(accountFirebase: AccountFirebase): Flow<State<Boolean>>
    fun deleteAccount(id: String): Flow<State<Boolean>>
    fun blockAccount(id: String): Flow<State<Boolean>>
    fun activeAccount(id: String): Flow<State<Boolean>>
    fun searchUserById(userId: String): Flow<State<MutableList<UserFirebase>>>

    fun getUserID(): Flow<State<String>>
    fun getParkingLotManagerById(parkingLotManagerId: String): Flow<State<ParkingLotManagerFirebase>>
}