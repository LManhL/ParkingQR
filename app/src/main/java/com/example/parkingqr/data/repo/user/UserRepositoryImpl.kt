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
        TODO("Not yet implemented")
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
                is State.Success -> State.success(state.data.map { it.mapToUser() }
                    .toMutableList())
                is State.Failed -> State.failed(state.message)
            }
        }
    }

    override fun searchUserInvoiceById(userId: String): Flow<State<MutableList<UserInvoice>>> {
        return userRemoteData.searchUserById(userId).map { state ->
            when (state) {
                is State.Loading -> State.loading()
                is State.Success -> State.success(state.data.map { it.mapToUserInvoice() }
                    .toMutableList())
                is State.Failed -> State.failed(state.message)
            }
        }
    }

    override fun getUserId(): Flow<State<String>> {
        return userRemoteData.getUserID()
    }

}