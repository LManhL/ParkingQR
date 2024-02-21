package com.example.parkingqr.data.repo.auth

import com.example.parkingqr.data.local.auth.AuthLocalData
import com.example.parkingqr.data.remote.State
import com.example.parkingqr.data.remote.auth.AuthRemoteData
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(private val authRemoteData: AuthRemoteData, private val authLocalData: AuthLocalData): AuthRepository {
    override fun signIn(email: String, password: String): Flow<State<FirebaseUser?>> {
        return authRemoteData.signIn(email, password)
    }

    override fun signUp(email: String, password: String): Flow<State<FirebaseUser?>> {
        return authRemoteData.signUp(email, password)
    }

    override fun signOut(): Flow<State<Boolean>> {
        return authRemoteData.signOut()
    }
}