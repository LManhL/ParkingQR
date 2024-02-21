package com.example.parkingqr.data.remote.auth

import android.content.Context
import com.example.parkingqr.data.remote.BaseRemoteDataSource
import com.example.parkingqr.data.remote.State
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRemoteDataSource @Inject constructor(val context: Context): BaseRemoteDataSource(),
    AuthRemoteData {
    override fun signIn(email: String, password: String): Flow<State<FirebaseUser?>> = flow {
        emit(State.loading())
        val snapshot = auth.signInWithEmailAndPassword(email, password).await()
        emit(State.success(snapshot.user))
    }.catch {
        emit(State.failed(it.message.toString()))
    }.flowOn(Dispatchers.IO)

    override fun signUp(email: String, password: String): Flow<State<FirebaseUser?>> = flow {
        emit(State.loading())
        val snapshot = auth.createUserWithEmailAndPassword(email, password).await()
        emit(State.success(snapshot.user))
    }.catch {
        emit(State.failed(it.message.toString()))
    }.flowOn(Dispatchers.IO)

    override fun signOut(): Flow<State<Boolean>> = flow {
        emit(State.loading())
        auth.signOut()
        emit(State.success(true))
    }.catch {
        emit(State.failed(it.message.toString()))
    }.flowOn(Dispatchers.Main)
}