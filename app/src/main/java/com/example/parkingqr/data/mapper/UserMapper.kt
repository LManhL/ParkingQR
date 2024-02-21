package com.example.parkingqr.data.mapper

import com.example.parkingqr.data.remote.dto.user.UserFirebase
import com.example.parkingqr.domain.model.user.UserDetail
import com.example.parkingqr.domain.model.user.UserInvoice
import com.example.parkingqr.domain.model.user.UserLogin
import com.example.parkingqr.domain.model.user.UserProfile
import com.google.firebase.firestore.auth.User

fun UserFirebase.mapToUserLogin(): UserLogin {
    return UserLogin(
        id = id ?: "",
        userId = userId ?: "",
        name = name ?: "",
        phoneNumber = phoneNumber ?: "",
        role = role ?: "",
        email = email ?: "",
    )
}

fun UserFirebase.mapToUserProfile(): UserProfile {
    return UserProfile(
        id = id ?: "",
        userId = userId ?: "",
        name = name ?: "",
        phoneNumber = phoneNumber ?: "",
    )
}

fun UserFirebase.mapToUserDetail(): UserDetail {
    return UserDetail(
        id = id ?: "",
        role = role ?: "",
        userId = userId ?: "",
        personalCode = personalCode ?: "",
        name = name ?: "",
        phoneNumber = phoneNumber ?: "",
        address = address ?: "",
        birthday = birthday ?: "",
        email = email ?: "",
        username = username ?: "",
        status = status ?: ""
    )
}

fun UserFirebase.mapToUserInvoice(): UserInvoice {
    return UserInvoice(
        id = id ?: "",
        userId = userId ?: "",
        name = name ?: "",
        phoneNumber = phoneNumber ?: "",
    )
}

fun UserLogin.mapToUserFirebase(): UserFirebase {
    return UserFirebase(
        id = id ?: "",
        role = role ?: "",
        userId = userId ?: "",
        name = name ?: "",
        email = email ?: "",
        phoneNumber = phoneNumber ?: "",
    )
}

fun UserDetail.mapToUserFirebase(): UserFirebase {
    return UserFirebase(
        id = id,
        role = role,
        status = status,
        userId = userId,
        personalCode = personalCode,
        name = name,
        phoneNumber = phoneNumber,
        address = address,
        birthday = birthday,
        email = email,
        username = username
    )
}