package com.example.parkingqr.ui.components.location

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.parkingqr.R
import com.example.parkingqr.domain.model.parkinglot.Rate
import com.example.parkingqr.utils.TimeUtil

class RateListAdapter(private val rateList: MutableList<Rate>) :
    Adapter<RateListAdapter.RateViewHolder>() {

    private var onClickItem: ((Rate) -> Unit)? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RateViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_rate_content, parent, false)
        return RateViewHolder(view)
    }

    override fun getItemCount(): Int {
        return rateList.size
    }

    override fun onBindViewHolder(holder: RateViewHolder, position: Int) {
        holder.bind(rateList[position])
    }

    fun setEventClick(callback: ((Rate) -> Unit)) {
        onClickItem = callback
    }

    inner class RateViewHolder(itemView: View) : ViewHolder(itemView) {

        private val avatar: ImageView = itemView.findViewById(R.id.ivAvatarRateContent)
        private val name: TextView = itemView.findViewById(R.id.tvNameRateContent)
        private val createAt: TextView = itemView.findViewById(R.id.tvCreateAtRateContent)
        private val stars: LinearLayout = itemView.findViewById(R.id.llStarRateContent)
        private val comment: TextView = itemView.findViewById(R.id.tvCommentRateContent)
        private val vehicleType: TextView = itemView.findViewById(R.id.tvVehicleTypeRateContent)


        private lateinit var curRate: Rate

        init {
            itemView.setOnClickListener {
                onClickItem?.invoke(curRate)
            }
        }

        fun bind(rate: Rate) {
            curRate = rate
            name.text = rate.userRate.name
            createAt.text = TimeUtil.getTimeAgoString(curRate.createAt.toLongOrNull() ?: 0)
            comment.text = curRate.comment
            vehicleType.text = "${rate.getReadableVehicleType()} - ${rate.brand.uppercase()}"
            bindAvatar()
            bindStars()
        }

        private fun bindAvatar() {
            Glide
                .with(itemView)
                .load(curRate.userRate.avatar)
                .error(R.drawable.my_user)
                .placeholder(R.drawable.my_user)
                .fitCenter()
                .into(avatar)
        }

        private fun bindStars() {
            for (i in 0 until 5) {
                val starImageView = stars.getChildAt(i) as ImageView
                if (i < curRate.rate) {
                    starImageView.setImageResource(R.drawable.star_full)
                } else {
                    starImageView.setImageResource(R.drawable.star_empty)
                }
            }
        }
    }
}