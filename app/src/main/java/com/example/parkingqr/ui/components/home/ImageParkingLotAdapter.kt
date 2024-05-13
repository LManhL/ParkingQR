package com.example.parkingqr.ui.components.home

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

class ImageParkingLotAdapter(
    private val list: MutableList<Uri?>,
    private val addImageCallBack: (() -> Unit)
) :
    Adapter<ViewHolder>() {

    companion object {
        const val ITEM_IMAGE = 0
        const val ITEM_ADD = 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (list[position] == null) ITEM_ADD
        else ITEM_IMAGE
    }

    override fun getItemCount() = list.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return if (viewType == ITEM_IMAGE) {
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_image_parking_lot, parent, false).let {
                    ImageParkingLotViewHolder(it)
                }
        } else {
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_add_image_parking_lot, parent, false).let {
                    AddImageParkingLotViewHolder(it)
                }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (holder is ImageParkingLotViewHolder) {
            list[position]?.let {
                holder.bind(it)
            }
        }
    }

    fun addToList(uri: Uri) {
        list.removeLastOrNull()
        list.add(uri)
        list.add(null)
        notifyDataSetChanged()
    }

    fun getData(): List<Uri> {
        return list.filterNotNull().toList()
    }

    inner class ImageParkingLotViewHolder(itemView: View) : ViewHolder(itemView) {
        private val image = itemView.findViewById<ImageView>(R.id.ivImageParkingLot)
        fun bind(uri: Uri) {
            val circularProgressDrawable = CircularProgressDrawable(itemView.context)
            circularProgressDrawable.strokeWidth = 5f
            circularProgressDrawable.centerRadius = 10f
            circularProgressDrawable.setColorSchemeColors(itemView.context.getColor(R.color.main_color))
            circularProgressDrawable.start()
            Glide
                .with(itemView.context)
                .load(uri)
                .placeholder(circularProgressDrawable)
                .fitCenter()
                .into(image)
        }
    }

    inner class AddImageParkingLotViewHolder(itemView: View) : ViewHolder(itemView) {
        private val image = itemView.findViewById<ImageView>(R.id.ivAddImageParkingLot)

        init {
            itemView.setOnClickListener {
                addImageCallBack.invoke()
            }
        }
    }
}