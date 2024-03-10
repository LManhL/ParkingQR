package com.example.parkingqr.domain.model.user

abstract class Person {
    var id: String = ""
    var account: Account = Account()

    abstract fun getRole(): AccountRole
}