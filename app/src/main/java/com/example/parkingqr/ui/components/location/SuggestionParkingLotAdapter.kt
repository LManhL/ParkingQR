package com.example.parkingqr.ui.components.location

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.parkingqr.R
import com.example.parkingqr.domain.model.parkinglot.ParkingLot

class SuggestionParkingLotAdapter(
    private val items: MutableList<ParkingLot>,
    private val onClickItem: ((ParkingLot) -> Unit)
) :
    Adapter<SuggestionParkingLotAdapter.SuggestionParkingLotViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SuggestionParkingLotViewHolder {
        return LayoutInflater.from(parent.context)
            .inflate(R.layout.item_search_parking_lot_location, parent, false).let {
                SuggestionParkingLotViewHolder(it)
            }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: SuggestionParkingLotViewHolder, position: Int) {
        holder.bind(items[position])
    }

    fun addAll(data: List<ParkingLot>) {
        items.clear()
        items.addAll(data)
        notifyDataSetChanged()
    }

    inner class SuggestionParkingLotViewHolder(private val itemView: View) : ViewHolder(itemView) {
        private val name = itemView.findViewById<TextView>(R.id.tvNameSearchParkingLotLocation)

        private val address =
            itemView.findViewById<TextView>(R.id.tvAddressSearchParkingLotLocation)

        fun bind(parkingLot: ParkingLot) {
            name.text = parkingLot.name
            address.text = parkingLot.address
            itemView.setOnClickListener {
                onClickItem.invoke(parkingLot)
            }
        }
    }
}