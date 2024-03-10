package com.example.parkingqr.ui.components.location

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.TextView
import com.example.parkingqr.R
import com.example.parkingqr.domain.model.parkinglot.ParkingLot

class ParkingLotSearchAdapter(
    private val context: Context,
    private val resource: Int,
    private val items: List<ParkingLot>
) : ArrayAdapter<ParkingLot>(context, resource, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_search_parking_lot_location, parent, false)
        val parkingLot = items[position]
        val name: TextView = view.findViewById(R.id.tvNameSearchParkingLotLocation)
        val address: TextView = view.findViewById(R.id.tvAddressSearchParkingLotLocation)
        address.text = parkingLot.address
        name.text = parkingLot.name
        return view
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filterResults = FilterResults()
                if (constraint != null) {
                    val suggestions = mutableListOf<ParkingLot>()
                    for (item in items) {
                        if (item.name.lowercase().contains(constraint.toString().lowercase())) {
                            suggestions.add(item)
                        }
                    }
                    filterResults.values = suggestions
                    filterResults.count = suggestions.size
                }
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                if (results != null && results.count > 0) {
                    clear()
                    addAll(results.values as ArrayList<ParkingLot>)
                    notifyDataSetChanged()
                }
            }

            override fun convertResultToString(resultValue: Any?): CharSequence {
                return (resultValue as ParkingLot).name
            }
        }
    }

}