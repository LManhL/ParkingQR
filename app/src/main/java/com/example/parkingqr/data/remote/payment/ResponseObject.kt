package com.example.parkingqr.data.remote.payment

import com.google.gson.annotations.SerializedName

data class ResponseObject<T>(
    @SerializedName(value = "status")
    val status: String,
    @SerializedName(value = "message")
    val message: String,
    @SerializedName(value = "data")
    val data: T
)