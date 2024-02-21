package com.example.parkingqr.data.remote.user

import android.content.Context
import com.example.parkingqr.data.remote.BaseRemoteDataSource
import com.example.parkingqr.data.remote.Params
import com.example.parkingqr.data.remote.State
import com.example.parkingqr.data.remote.dto.user.UserFirebase
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserRemoteDataSource @Inject constructor(val context: Context): BaseRemoteDataSource(),
    UserRemoteData {
    override fun getUserByEmail(email: String): Flow<State<MutableList<UserFirebase>>> = flow {
        val userRef = db.collection(Params.USER_PATH_COLLECTION)
        val query = userRef.whereEqualTo("email", email)
        emit(State.loading())
        val querySnapshot = query.get().await()
        val userList: MutableList<UserFirebase> = mutableListOf()
        for (document in querySnapshot.documents) {
            document.toObject(UserFirebase::class.java)?.let { userList.add(it) }
        }
        emit(State.success(userList))
    }.catch {
        emit(State.failed(it.message.toString()))
    }.flowOn(Dispatchers.IO)

    override fun createNewUser(user: UserFirebase): Flow<State<Boolean>> = flow {
        emit(State.loading())
        val userRef = db.collection(Params.USER_PATH_COLLECTION)
        val key = userRef.document().id
        user.id = key
        val snapshot = userRef.document(user.id!!).set(user).await()
        emit(State.success(true))
    }.catch {
        emit(State.failed(it.message.toString()))
    }.flowOn(Dispatchers.IO)

    override fun getUserInformation(): Flow<State<UserFirebase>> = flow {
        val user = auth.currentUser
        val userRef = db.collection(Params.USER_PATH_COLLECTION)
        val query = userRef.whereEqualTo("userId", user?.uid)
        emit(State.loading())
        val querySnapshot = query.get().await()
        val userList: MutableList<UserFirebase> = mutableListOf()
        for (document in querySnapshot.documents) {
            document.toObject(UserFirebase::class.java)?.let { userList.add(it) }
        }
        if (userList.isNotEmpty()) {
            emit(State.success(userList[0]))
        } else {
            emit(State.failed("Không tìm thấy người dùng"))
        }
    }.catch {
        emit(State.failed(it.message.toString()))
    }.flowOn(Dispatchers.IO)

    override fun getAllUser(): Flow<State<MutableList<UserFirebase>>> = flow {
        emit(State.loading())
        val ref = db.collection(Params.USER_PATH_COLLECTION)
        val query: Query = ref
        val querySnapshot = query.get().await()
        val userList = mutableListOf<UserFirebase>()
        for (document in querySnapshot.documents) {
            document.toObject(UserFirebase::class.java)?.let {
                userList.add(it)
            }
        }
        emit(State.success(userList))
    }.catch { emit(State.failed(it.message.toString())) }.flowOn(Dispatchers.IO)

    override fun getUserById(id: String): Flow<State<UserFirebase>> = flow {
        val userRef = db.collection(Params.USER_PATH_COLLECTION).document(id)
        emit(State.loading())
        val querySnapshot = userRef.get().await()
        querySnapshot.toObject(UserFirebase::class.java)?.let {
            emit(State.success(it))
        }
    }.catch { emit(State.failed(it.message.toString())) }.flowOn(Dispatchers.IO)

    override fun updateUser(user: UserFirebase): Flow<State<Boolean>> = flow {
        emit(State.loading())
        val userRef = db.collection(Params.USER_PATH_COLLECTION)
        val snapshot = userRef.document(user.id!!).set(user).await()
        emit(State.success(true))
    }.catch { emit(State.failed(it.message.toString())) }.flowOn(Dispatchers.IO)

    override fun deleteUser(id: String): Flow<State<Boolean>> = flow {
        emit(State.loading())
        val userRef = db.collection(Params.USER_PATH_COLLECTION)
        val snapshot = userRef.document(id).delete().await()
        emit(State.success(true))
    }.catch { emit(State.failed(it.message.toString())) }.flowOn(Dispatchers.IO)

    override fun blockUser(id: String): Flow<State<Boolean>> = flow {
        emit(State.loading())
        val userRef = db.collection(Params.USER_PATH_COLLECTION)
        val snapshot = userRef.document(id).update("status", "blocked").await()
        emit(State.success(true))
    }.catch { emit(State.failed(it.message.toString())) }.flowOn(Dispatchers.IO)

    override fun activeUser(id: String): Flow<State<Boolean>> = flow {
        emit(State.loading())
        val userRef = db.collection(Params.USER_PATH_COLLECTION)
        val snapshot = userRef.document(id).update("status", "active").await()
        emit(State.success(true))
    }.catch { emit(State.failed(it.message.toString())) }.flowOn(Dispatchers.IO)

    override fun searchUserById(userId: String): Flow<State<MutableList<UserFirebase>>> = flow {
        val userRef = db.collection(Params.USER_PATH_COLLECTION)
        val query = userRef.whereEqualTo("userId", userId)
        emit(State.loading())
        val querySnapshot = query.get().await()
        val userList: MutableList<UserFirebase> = mutableListOf()
        for (document in querySnapshot.documents) {
            document.toObject(UserFirebase::class.java)?.let { userList.add(it) }
        }
        emit(State.success(userList))
    }.catch {
        emit(State.failed(it.message.toString()))
    }.flowOn(Dispatchers.IO)
}