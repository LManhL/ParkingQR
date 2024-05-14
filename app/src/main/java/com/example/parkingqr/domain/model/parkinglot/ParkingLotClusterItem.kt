package com.example.parkingqr.domain.model.parkinglot

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

data class ParkingLotClusterItem(
    val data: ParkingLot
) : ClusterItem {
    override fun getPosition(): LatLng {
        return data.location
    }

    override fun getTitle(): String {
        return data.name
    }

    override fun getSnippet(): String {
        return data.address
    }

    override fun getZIndex(): Float{
        return 0f
    }
}