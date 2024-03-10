package com.example.parkingqr.data.mapper

import com.example.parkingqr.data.remote.dto.user.AccountFirebase
import com.example.parkingqr.data.remote.dto.user.UserFirebase
import com.example.parkingqr.domain.model.user.*

fun AccountFirebase.mapToAccount(): Account {
    return Account(
        id = id ?: "",
        username = username ?: "",
        name = name ?: "",
        address = address ?: "",
        birthday = birthday ?: "",
        email = email ?: "",
        personalCode = personalCode ?: "",
        phoneNumber = phoneNumber ?: "",
        role = role ?: "",
        status = status ?: ""
    )
}

fun Account.mapToAccountFirebase(): AccountFirebase {
    return AccountFirebase(
        id = id,
        username = username,
        name = name,
        address = address,
        birthday = birthday,
        email = email,
        personalCode = personalCode,
        phoneNumber = phoneNumber,
        role = role,
        status = status
    )
}

fun User.mapToUserFirebase(): UserFirebase {
    return UserFirebase(userId = userId, account = account.mapToAccountFirebase())
}