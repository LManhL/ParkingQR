package com.example.parkingqr.domain.model.user

class User() : Person() {
    var userId: String = ""

    constructor(account: Account) : this() {
        this.account = account
        account.setActive()
    }

    override fun getRole(): AccountRole {
        return AccountRole.USER
    }
}