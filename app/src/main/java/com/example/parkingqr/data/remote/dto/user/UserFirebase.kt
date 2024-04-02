package com.example.parkingqr.data.remote.dto.user

data class UserFirebase(
    var userId: String? = null,
) : PersonFirebase() {
    constructor(id: String, userId: String, account: AccountFirebase) : this() {
        this.id = id
        this.userId = userId
        this.account = account
    }
}