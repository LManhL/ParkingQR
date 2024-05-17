package com.example.parkingqr.data.repo.user

import com.example.parkingqr.data.local.user.UserLocalData
import com.example.parkingqr.data.mapper.*
import com.example.parkingqr.data.remote.State
import com.example.parkingqr.data.remote.user.UserRemoteData
import com.example.parkingqr.domain.model.invoice.UserInvoice
import com.example.parkingqr.domain.model.user.Account
import com.example.parkingqr.domain.model.user.ParkingAttendant
import com.example.parkingqr.domain.model.user.ParkingLotManager
import com.example.parkingqr.domain.model.user.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userRemoteData: UserRemoteData,
    private val userLocalData: UserLocalData
) : UserRepository {
    override fun getAccountByEmail(email: String): Flow<State<MutableList<Account>>> {
        return userRemoteData.getAccountByEmail(email).map { state ->
            when (state) {
                is State.Loading -> State.loading()
                is State.Success -> State.success(state.data.map { it.mapToAccount() }
                    .toMutableList())
                is State.Failed -> State.failed(state.message)
            }
        }
    }

    override fun createNewUser(user: User): Flow<State<Boolean>> {
        return userRemoteData.createNewUser(user.mapToUserFirebase())
    }

    override fun createNewParkingLotManager(parkingLotManager: ParkingLotManager): Flow<State<Boolean>> {
        return userRemoteData.createNewParkingLotManager(parkingLotManager.mapToParkingLotManagerFirebase())
    }

    override fun createNewParkingAttendant(parkingAttendant: ParkingAttendant): Flow<State<Boolean>> {
        TODO("Not yet implemented")
    }

    override fun getAccountInformation(): Flow<State<Account>> {
        return userRemoteData.getAccountInformation().map { state ->
            when (state) {
                is State.Loading -> State.loading()
                is State.Success -> State.success(state.data.mapToAccount())
                is State.Failed -> State.failed(state.message)
            }
        }
    }

    override fun getAllAccount(): Flow<State<MutableList<Account>>> {
        return userRemoteData.getAllAccount().map { state ->
            when (state) {
                is State.Loading -> State.loading()
                is State.Success -> State.success(state.data.map { it.mapToAccount() }
                    .toMutableList())
                is State.Failed -> State.failed(state.message)
            }
        }
    }

    override fun getAccountById(id: String): Flow<State<Account>> {
        return userRemoteData.getAccountById(id).map { state ->
            when (state) {
                is State.Loading -> State.loading()
                is State.Success -> State.success(state.data.mapToAccount())
                is State.Failed -> State.failed(state.message)
            }
        }
    }

    override fun updateAccount(account: Account): Flow<State<Boolean>> {
        return userRemoteData.updateAccount(account.mapToAccountFirebase())
    }

    override fun deleteAccount(id: String): Flow<State<Boolean>> {
        return userRemoteData.deleteAccount(id)
    }

    override fun blockAccount(id: String): Flow<State<Boolean>> {
        return userRemoteData.blockAccount(id)
    }

    override fun activeAccount(id: String): Flow<State<Boolean>> {
        return userRemoteData.activeAccount(id)
    }

    override fun searchUserById(userId: String): Flow<State<MutableList<User>>> {
        return userRemoteData.searchUserById(userId).map { state ->
            when (state) {
                is State.Loading -> State.loading()
                is State.Success -> State.success(mutableListOf(state.data.mapToUser()))
                is State.Failed -> State.failed(state.message)
            }
        }
    }

    override fun searchUserInvoiceById(userId: String): Flow<State<UserInvoice>> {
        return userRemoteData.searchUserById(userId).map { state ->
            when (state) {
                is State.Loading -> State.loading()
                is State.Success -> State.success(state.data.mapToUserInvoice())
                is State.Failed -> State.failed(state.message)
            }
        }
    }

    override fun getUserId(): Flow<State<String>> {
        return userRemoteData.getUserID()
    }

    override fun getParkingLotManagerById(parkingLotManagerId: String): Flow<State<ParkingLotManager>> {
        return userRemoteData.getParkingLotManagerById(parkingLotManagerId).map { state ->
            when (state) {
                is State.Loading -> State.loading()
                is State.Success -> State.success(state.data.mapToParkingLotManager())
                is State.Failed -> State.failed(state.message)
            }
        }
    }

    override fun saveLocalParkingLotId(parkingLotId: String) {
        return userLocalData.saveParkingLotId(parkingLotId)
    }

    override fun getLocalParkingLotId(): String? {
        return userLocalData.getParingLotId()
    }

    override fun getCurrentUserInfo(): Flow<State<User>> {
        return userRemoteData.getCurrentUserInfo().map { state ->
            when (state) {
                is State.Loading -> State.loading()
                is State.Success -> State.success(state.data.mapToUser())
                is State.Failed -> State.failed(state.message)
            }
        }
    }

    override fun getParkingLotManagerByParkingLotId(parkingLotId: String): Flow<State<ParkingLotManager>> {
        return userRemoteData.getParkingLotManagerByParkingLotId(parkingLotId).map { state ->
            when (state) {
                is State.Loading -> State.loading()
                is State.Success -> State.success(state.data.mapToParkingLotManager())
                is State.Failed -> State.failed(state.message)
            }
        }
    }

    override fun getParkingLotManager(): Flow<State<ParkingLotManager>> {
        return userRemoteData.getParkingLotManager().map { state ->
            when (state) {
                is State.Loading -> State.loading()
                is State.Success -> State.success(state.data.mapToParkingLotManager())
                is State.Failed -> State.failed(state.message)
            }
        }
    }

    override fun updateParkingLotIdForParkingLotManager(
        parkingLotManagerId: String,
        parkingLotId: String
    ): Flow<State<Boolean>> {
        return userRemoteData.updateParkingLotIdForParkingLotManager(
            parkingLotManagerId,
            parkingLotId
        )
    }

    override fun getUserById(userId: String): Flow<State<User>> {
        return userRemoteData.getUserById(userId).map { state ->
            when (state) {
                is State.Loading -> State.loading()
                is State.Success -> State.success(state.data.mapToUser())
                is State.Failed -> State.failed(state.message)
            }
        }
    }

    override fun blockUser(id: String): Flow<State<Boolean>> {
        return userRemoteData.blockUser(id)
    }

    override fun activeUser(id: String): Flow<State<Boolean>> {
        return userRemoteData.activeUser(id)
    }
}