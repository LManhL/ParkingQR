package com.example.parkingqr.data.repo.auth

import com.example.parkingqr.data.remote.State
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun signIn(email: String, password: String): Flow<State<FirebaseUser?>>

    fun signUp(email: String, password: String): Flow<State<FirebaseUser?>>

    fun signOut(): Flow<State<Boolean>>
}