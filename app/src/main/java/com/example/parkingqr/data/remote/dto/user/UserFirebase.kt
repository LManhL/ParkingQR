package com.example.parkingqr.data.remote.dto.user

data class UserFirebase(
    var userId: String? = null,
): PersonFirebase() {
    constructor(userId: String, account: AccountFirebase): this(){
        this.userId = userId
        this.account = account
    }
}