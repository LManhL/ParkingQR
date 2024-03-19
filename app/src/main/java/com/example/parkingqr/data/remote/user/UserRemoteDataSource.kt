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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
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
        val userAuth = auth.currentUser
        val accountRef = db.collection(Params.ACCOUNT_PATH_COLLECTION)
        val userRef = db.collection(Params.USER_PATH_COLLECTION)
        val keyAccount = accountRef.document().id
        val keyUser = userRef.document().id

        user.id = keyUser
        user.userId = userAuth?.uid
        user.account?.id = keyAccount

        accountRef.document(user.account?.id ?: "").set(user.account ?: AccountFirebase()).await()
        userRef.document(user.id ?: "").set(user).await()

        emit(State.success(true))
    }.catch {
        emit(State.failed(it.message.toString()))
    }.flowOn(Dispatchers.IO)

    override fun createNewParkingLotManager(parkingLotManagerFirebase: ParkingLotManagerFirebase): Flow<State<Boolean>> {
        TODO("Not yet implemented")
    }

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

    override fun searchAccountById(userId: String): Flow<State<MutableList<AccountFirebase>>> =
        flow {
            val userRef = db.collection(Params.ACCOUNT_PATH_COLLECTION)
            val query = userRef.whereEqualTo("userId", userId)
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

    override fun getUserID(): Flow<State<String>> = flow {
        emit(State.success(auth.uid ?: ""))
    }.flowOn(Dispatchers.IO)
}