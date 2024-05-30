package com.example.parkingqr.ui.components.usermanagement

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.parkingqr.R
import com.example.parkingqr.domain.model.user.Account
import com.example.parkingqr.domain.model.user.User

class UserManagementAdapter(private val userList: MutableList<User>) :
    Adapter<UserManagementAdapter.UserManagementViewHolder>() {

    private var onClickItem: ((User) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserManagementViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user_management, parent, false)
        return UserManagementViewHolder(view)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: UserManagementViewHolder, position: Int) {
        holder.bind(userList[position])
    }

    fun addAll(data: List<User>){
        userList.clear()
        userList.addAll(data)
        notifyDataSetChanged()
    }

    fun setEventClick(onClickItem: ((User) -> Unit)) {
        this.onClickItem = onClickItem
    }


    inner class UserManagementViewHolder(itemView: View) : ViewHolder(itemView) {
        private val status: TextView = itemView.findViewById(R.id.tvStatusUserManagement)
        private val email: TextView = itemView.findViewById(R.id.tvEmailUserManagement)
        private val phone: TextView = itemView.findViewById(R.id.tvPhoneNumberUserManagement)
        private val role: TextView = itemView.findViewById(R.id.tvRoleUserManagement)
        private var curUser: User? = null

        init {
            itemView.setOnClickListener {
                onClickItem?.invoke(curUser!!)
            }
        }

        fun bind(user: User) {
            curUser = user

            email.text = user.account.email
            phone.text = user.account.phoneNumber
            role.text = "Người dùng"
            if (user.account.status == Account.ACTIVE_STATUS) {
                status.text = "Đang hoạt động"
                status.setTextColor(itemView.resources.getColor(R.color.light_green))
            } else {
                status.text = "Bị chặn"
                status.setTextColor(itemView.resources.getColor(R.color.light_red))
            }
        }

    }
}