package com.example.parkingqr.domain.model.user

class User() : Person() {
    var userId: String = ""

    constructor(id: String, userId: String, account: Account): this(){
        this.id = id
        this.userId = userId
        this.account = account
    }
    constructor(account: Account) : this() {
        this.account = account
        account.setActive()
    }

    override fun getRole(): AccountRole {
        return AccountRole.USER
    }
}