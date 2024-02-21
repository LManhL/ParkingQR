package com.example.parkingqr.data.remote

import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

open class BaseRemoteDataSource {
    val db = Firebase.firestore
    val auth = Firebase.auth
    val storage = Firebase.storage
}