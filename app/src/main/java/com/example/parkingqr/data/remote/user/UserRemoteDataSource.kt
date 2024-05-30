package com.example.parkingqr.data.remote.user

import android.content.Context
import com.example.parkingqr.data.remote.BaseRemoteDataSource
import com.example.parkingqr.data.remote.Params
import com.example.parkingqr.data.remote.State
import com.example.parkingqr.data.remote.dto.user.AccountFirebase
import com.example.parkingqr.data.remote.dto.user.ParkingAttendantFirebase
import com.example.parkingqr.data.remote.dto.user.ParkingLotManagerFirebase
import com.example.parkingqr.data.remote.dto.user.UserFirebase
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserRemoteDataSource @Inject constructor(val context: Context) : BaseRemoteDataSource(),
    UserRemoteData {
    override fun getAccountByEmail(email: String): Flow<State<MutableList<AccountFirebase>>> =
        flow {
            val userRef = db.collection(Params.ACCOUNT_PATH_COLLECTION)
            val query = userRef.whereEqualTo("email", email)
            emit(State.loading())
            val querySnapshot = query.get().await()
            val userList: MutableList<AccountFirebase> = mutableListOf()
            for (document in querySnapshot.documents) {
                document.toObject(AccountFirebase::class.java)?.let { userList.add(it) }
            }
            emit(State.success(userList))
        }.catch {
            emit(State.failed(it.message.toString()))
        }.flowOn(Dispatchers.IO)

    override fun createNewUser(user: UserFirebase): Flow<State<Boolean>> = flow {
        emit(State.loading())
        val accountRef = db.collection(Params.ACCOUNT_PATH_COLLECTION)
        val userRef = db.collection(Params.USER_PATH_COLLECTION)
        val keyAccount = accountRef.document().id
        val keyUser = userRef.document().id

        user.id = keyUser
        user.account?.id = keyAccount

        accountRef.document(user.account?.id ?: "").set(user.account ?: AccountFirebase()).await()
        userRef.document(user.id ?: "").set(user).await()

        emit(State.success(true))
    }.catch {
        emit(State.failed(it.message.toString()))
    }.flowOn(Dispatchers.IO)

    override fun createNewParkingLotManager(parkingLotManagerFirebase: ParkingLotManagerFirebase): Flow<State<Boolean>> =
        flow {
            emit(State.loading())
            val accountRef = db.collection(Params.ACCOUNT_PATH_COLLECTION).document()
            val parkingLotManagerRef =
                db.collection(Params.PARKING_LOT_MANAGER_PATH_COLLECTION).document()
            val keyAccount = accountRef.id
            val keyParkingLotManager = parkingLotManagerRef.id

            parkingLotManagerFirebase.id = keyParkingLotManager
            parkingLotManagerFirebase.account?.id = keyAccount

            accountRef.set(parkingLotManagerFirebase.account!!).await()
            parkingLotManagerRef.set(parkingLotManagerFirebase).await()
            emit(State.success(true))
        }.catch {
            emit(State.failed(it.message.toString()))
        }.flowOn(Dispatchers.IO)

    override fun createNewParkingAttendant(parkingAttendantFirebase: ParkingAttendantFirebase): Flow<State<Boolean>> {
        TODO("Not yet implemented")
    }

    override fun getAccountInformation(): Flow<State<AccountFirebase>> = flow {
        val user = auth.currentUser
        val userRef = db.collection(Params.ACCOUNT_PATH_COLLECTION)
        val query = userRef.whereEqualTo("email", user?.email)
        emit(State.loading())
        val querySnapshot = query.get().await()
        val userList: MutableList<AccountFirebase> = mutableListOf()
        for (document in querySnapshot.documents) {
            document.toObject(AccountFirebase::class.java)?.let { userList.add(it) }
        }
        if (userList.isNotEmpty()) {
            emit(State.success(userList[0]))
        } else {
            emit(State.failed("Không tìm thấy người dùng"))
        }
    }.catch {
        emit(State.failed(it.message.toString()))
    }.flowOn(Dispatchers.IO)

    override fun getAllAccount(): Flow<State<MutableList<AccountFirebase>>> = flow {
        emit(State.loading())
        val ref = db.collection(Params.ACCOUNT_PATH_COLLECTION)
        val query: Query = ref
        val querySnapshot = query.get().await()
        val userList = mutableListOf<AccountFirebase>()
        for (document in querySnapshot.documents) {
            document.toObject(AccountFirebase::class.java)?.let {
                userList.add(it)
            }
        }
        emit(State.success(userList))
    }.catch { emit(State.failed(it.message.toString())) }.flowOn(Dispatchers.IO)

    override fun getAccountById(id: String): Flow<State<AccountFirebase>> = flow {
        val userRef = db.collection(Params.ACCOUNT_PATH_COLLECTION).document(id)
        emit(State.loading())
        val querySnapshot = userRef.get().await()
        querySnapshot.toObject(AccountFirebase::class.java)?.let {
            emit(State.success(it))
        }
    }.catch { emit(State.failed(it.message.toString())) }.flowOn(Dispatchers.IO)

    override fun updateAccount(account: AccountFirebase): Flow<State<Boolean>> = flow {
        emit(State.loading())
        val userRef = db.collection(Params.ACCOUNT_PATH_COLLECTION)
        val snapshot = userRef.document(account.id!!).set(account).await()
        emit(State.success(true))
    }.catch { emit(State.failed(it.message.toString())) }.flowOn(Dispatchers.IO)

    override fun deleteAccount(id: String): Flow<State<Boolean>> = flow {
        emit(State.loading())
        val userRef = db.collection(Params.ACCOUNT_PATH_COLLECTION)
        val snapshot = userRef.document(id).delete().await()
        emit(State.success(true))
    }.catch { emit(State.failed(it.message.toString())) }.flowOn(Dispatchers.IO)

    override fun blockAccount(id: String): Flow<State<Boolean>> = flow {
        emit(State.loading())
        val userRef = db.collection(Params.ACCOUNT_PATH_COLLECTION)
        val snapshot = userRef.document(id).update("status", "blocked").await()
        emit(State.success(true))
    }.catch { emit(State.failed(it.message.toString())) }.flowOn(Dispatchers.IO)

    override fun activeAccount(id: String): Flow<State<Boolean>> = flow {
        emit(State.loading())
        val userRef = db.collection(Params.ACCOUNT_PATH_COLLECTION)
        val snapshot = userRef.document(id).update("status", "active").await()
        emit(State.success(true))
    }.catch { emit(State.failed(it.message.toString())) }.flowOn(Dispatchers.IO)

    override fun searchUserById(userId: String): Flow<State<UserFirebase>> =
        flow {
            val userRef = db.collection(Params.USER_PATH_COLLECTION)
            val query = userRef.whereEqualTo("userId", userId)
            emit(State.loading())
            val querySnapshot = query.get().await()
            val userList: MutableList<UserFirebase> = mutableListOf()
            for (document in querySnapshot.documents) {
                document.toObject(UserFirebase::class.java)?.let {
                    userList.add(it)
                }
            }
            val foundUser = userList.firstOrNull()
            if (foundUser != null) {
                emit(State.success(foundUser))
            } else {
                emit(State.failed("Không tìm thấy người dùng tương ứng"))
            }
        }.catch {
            emit(State.failed(it.message.toString()))
        }.flowOn(Dispatchers.IO)

    override fun getParkingLotManager(): Flow<State<ParkingLotManagerFirebase>> =
        flow<State<ParkingLotManagerFirebase>> {
            val ref = db.collection(Params.PARKING_LOT_MANAGER_PATH_COLLECTION)
            val query = ref.whereEqualTo("parkingLotManagerId", auth.uid)
            emit(State.loading())
            query.get().await().let { querySnapshots ->
                val accountList: MutableList<ParkingLotManagerFirebase> = mutableListOf()
                for (document in querySnapshots) {
                    document.toObject(ParkingLotManagerFirebase::class.java).let {
                        accountList.add(it)
                    }
                }
                if (accountList.isNotEmpty()) emit(State.success(accountList.first()))
                else emit(State.failed("Không tìm thấy"))
            }
        }.catch {
            emit(State.failed(it.message.toString()))
        }.flowOn(Dispatchers.IO)

    override fun getUserID(): Flow<State<String>> = flow {
        emit(State.success(auth.uid ?: ""))
    }.flowOn(Dispatchers.IO)

    override fun getParkingLotManagerById(parkingLotManagerId: String): Flow<State<ParkingLotManagerFirebase>> =
        flow {
            val userRef = db.collection(Params.PARKING_LOT_MANAGER_PATH_COLLECTION)
            val query = userRef.whereEqualTo("parkingLotManagerId", parkingLotManagerId)
            emit(State.loading())
            val querySnapshot = query.get().await()
            val accountList: MutableList<ParkingLotManagerFirebase> = mutableListOf()
            for (document in querySnapshot.documents) {
                document.toObject(ParkingLotManagerFirebase::class.java)?.let {
                    accountList.add(it)
                }
            }
            if (accountList.isNotEmpty()) emit(State.success(accountList[0]))
            else emit(State.failed("Không tìm thấy"))
        }.catch {
            emit(State.failed(it.message.toString()))
        }.flowOn(Dispatchers.IO)

    override fun getCurrentUserInfo(): Flow<State<UserFirebase>> = flow {
        val userRef = db.collection(Params.USER_PATH_COLLECTION)
        val query = userRef.whereEqualTo("userId", auth.uid)
        emit(State.loading())
        val querySnapshot = query.get().await()
        val userList: MutableList<UserFirebase> = mutableListOf()
        for (document in querySnapshot.documents) {
            document.toObject(UserFirebase::class.java)?.let {
                userList.add(it)
            }
        }
        if (userList.isNotEmpty()) {
            emit(State.success(userList.first()))
        } else {
            emit(State.failed("Không tìm thấy"))
        }
    }.catch {
        emit(State.failed(it.message.toString()))
    }.flowOn(Dispatchers.IO)

    override fun getParkingLotManagerByParkingLotId(parkingLotId: String): Flow<State<ParkingLotManagerFirebase>> =
        flow<State<ParkingLotManagerFirebase>> {
            emit(State.loading())
            val userRef = db.collection(Params.PARKING_LOT_MANAGER_PATH_COLLECTION)
            val query = userRef.whereEqualTo("parkingLotId", parkingLotId)
            query.get().await().let { snapshots ->
                val list = mutableListOf<ParkingLotManagerFirebase>()
                for (snapshot in snapshots) {
                    list.add(
                        snapshot.toObject(ParkingLotManagerFirebase::class.java)
                    )
                }
                val res = list.firstOrNull()
                if (res != null) {
                    emit(State.success(res))
                } else {
                    emit(State.failed("Không tìm thấy"))
                }
            }
        }.catch {
            emit(State.failed(it.message.toString()))
        }.flowOn(Dispatchers.IO)

    override fun updateParkingLotIdForParkingLotManager(
        parkingLotManagerId: String,
        parkingLotId: String
    ): Flow<State<Boolean>> = flow {
        emit(State.loading())
        val ref = db.collection(Params.PARKING_LOT_MANAGER_PATH_COLLECTION)
        ref.document(parkingLotManagerId).update("parkingLotId", parkingLotId).await()
        emit(State.success(true))
    }.catch {
        emit(State.failed(it.message.toString()))
    }.flowOn(Dispatchers.IO)

    override fun blockUser(id: String): Flow<State<Boolean>> = flow {
        emit(State.loading())
        val ref = db.collection(Params.USER_PATH_COLLECTION)
        ref.document(id).update("account.status", "blocked").await()
        emit(State.success(true))
    }.catch {
        emit(State.failed(it.message.toString()))
    }.flowOn(Dispatchers.IO)

    override fun activeUser(id: String): Flow<State<Boolean>> = flow {
        emit(State.loading())
        val ref = db.collection(Params.USER_PATH_COLLECTION)
        ref.document(id).update("account.status", "active").await()
        emit(State.success(true))
    }.catch {
        emit(State.failed(it.message.toString()))
    }.flowOn(Dispatchers.IO)

    override fun getUserById(userId: String): Flow<State<UserFirebase>> =
        flow<State<UserFirebase>> {
            emit(State.loading())
            db.collection(Params.USER_PATH_COLLECTION).whereEqualTo("userId", userId).get().await()
                .let { querySnapshots ->
                    val res = mutableListOf<UserFirebase>()
                    querySnapshots.forEach {
                        res.add(it.toObject(UserFirebase::class.java))
                    }
                    val foundUser = res.firstOrNull()
                    if (foundUser != null) {
                        emit(State.success(foundUser))
                    } else {
                        emit(State.failed("Không tìm thấy"))
                    }
                }
        }.catch {
            emit(State.failed(it.message.toString()))
        }.flowOn(Dispatchers.IO)

    override fun getAllUser(): Flow<State<List<UserFirebase>>> = flow {
        emit(State.loading())
        db.collection(Params.USER_PATH_COLLECTION).snapshots().map { querySnapshots ->
            val res = mutableListOf<UserFirebase>()
            querySnapshots.forEach {
                res.add(it.toObject(UserFirebase::class.java))
            }
            emit(State.success(res.toList()))
        }.collect()
    }.catch {
        emit(State.failed(it.message.toString()))
    }.flowOn(Dispatchers.IO)

    override fun updateUser(user: UserFirebase): Flow<State<Boolean>> = flow {
        emit(State.loading())
        db.collection(Params.USER_PATH_COLLECTION).document(user.id.toString()).set(user).await()
        emit(State.success(true))
    }.catch {
        emit(State.failed(it.message.toString()))
    }.flowOn(Dispatchers.IO)
}