package com.example.parkingqr.ui.components.location

import android.content.Context
import androidx.core.content.ContextCompat
import com.example.parkingqr.R
import com.example.parkingqr.domain.model.parkinglot.ParkingLotClusterItem
import com.example.parkingqr.utils.BitmapUtil
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer

class ParkingLotRenderer(
    private val context: Context,
    private val map: GoogleMap,
    private val clusterManager: ClusterManager<ParkingLotClusterItem>
) : DefaultClusterRenderer<ParkingLotClusterItem>(context, map, clusterManager) {

    private val icon: BitmapDescriptor by lazy {
        BitmapUtil.vectorToBitmap(context, R.drawable.parking_location)
    }

    override fun onBeforeClusterItemRendered(
        item: ParkingLotClusterItem,
        markerOptions: MarkerOptions
    ) {
        markerOptions.title(item.data.name)
            .position(item.position)
            .icon(icon)
    }

    override fun onClusterItemRendered(clusterItem: ParkingLotClusterItem, marker: Marker) {
        marker.tag = clusterItem
    }
}