package com.example.parkingqr.ui.components.admin

import android.graphics.Bitmap
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.example.parkingqr.R

class ImageParkingLotDetailAdapter(
    private val list: MutableList<String>
) :
    Adapter<ImageParkingLotDetailAdapter.ImageParkingLotViewHolder>() {

    override fun getItemCount() = list.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageParkingLotViewHolder {
        return LayoutInflater.from(parent.context)
            .inflate(R.layout.item_image_parking_lot, parent, false).let {
                ImageParkingLotViewHolder(it)
            }
    }

    override fun onBindViewHolder(holder: ImageParkingLotViewHolder, position: Int) {
        holder.bind(list[position])
    }

    fun addData(data: List<String>) {
        list.clear()
        list.addAll(data.toMutableList())
        notifyDataSetChanged()
    }

    inner class ImageParkingLotViewHolder(itemView: View) : ViewHolder(itemView) {
        private val image = itemView.findViewById<ImageView>(R.id.ivImageParkingLot)

        fun bind(url: String) {
            val circularProgressDrawable = CircularProgressDrawable(itemView.context)
            circularProgressDrawable.strokeWidth = 5f
            circularProgressDrawable.centerRadius = 10f
            circularProgressDrawable.setColorSchemeColors(itemView.context.getColor(R.color.main_color))
            circularProgressDrawable.start()
            Glide
                .with(itemView.context)
                .load(url)
                .placeholder(circularProgressDrawable)
                .fitCenter()
                .into(image)
        }
    }
}