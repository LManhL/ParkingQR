package com.example.parkingqr.data.mapper

import com.example.parkingqr.data.remote.dto.user.AccountFirebase
import com.example.parkingqr.data.remote.dto.user.ParkingLotManagerFirebase
import com.example.parkingqr.data.remote.dto.user.UserFirebase
import com.example.parkingqr.domain.model.invoice.UserInvoice
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
    return UserFirebase(id = id, userId = userId, account = account.mapToAccountFirebase())
}

fun UserFirebase.mapToUser(): User {
    return User(
        id = id ?: "",
        userId = userId ?: "",
        account = account?.mapToAccount() ?: Account()
    )
}

fun UserFirebase.mapToUserInvoice(): UserInvoice {
    return UserInvoice(
        id = id ?: "",
        userId = userId ?: "",
        name = account?.name ?: "",
        phoneNumber = account?.phoneNumber ?: ""
    )
}

fun ParkingLotManagerFirebase.mapToParkingLotManager(): ParkingLotManager {
    return ParkingLotManager(
        id = id ?: "",
        parkingLotManagerId = parkingLotManagerId ?: "",
        parkingLotId = parkingLotId ?: "",
        account = account?.mapToAccount() ?: Account()

    )
}