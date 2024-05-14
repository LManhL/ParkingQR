package com.example.parkingqr.ui.components.location

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.example.parkingqr.R
import com.example.parkingqr.domain.model.parkinglot.ParkingLotClusterItem
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker

class MarkerInfoWindowCustomAdapter(private val context: Context) : GoogleMap.InfoWindowAdapter {
    override fun getInfoContents(marker: Marker): View? {
        val place = marker.tag as? ParkingLotClusterItem ?: return null
        val view = LayoutInflater.from(context).inflate(
            R.layout.item_marker_info_content, null
        )
        view.findViewById<TextView>(
            R.id.tvTitleMarkerInfoContent
        ).text = place.data.name

        return view
    }

    override fun getInfoWindow(marker: Marker): View? {
        return null
    }
}