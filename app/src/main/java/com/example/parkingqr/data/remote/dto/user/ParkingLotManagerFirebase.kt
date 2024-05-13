package com.example.parkingqr.data.remote.dto.user

class ParkingLotManagerFirebase(
    id: String? = null,
    account: AccountFirebase? = null,
    var parkingLotId: String? = null,
    var parkingLotManagerId: String? = null
) : PersonFirebase(id, account)