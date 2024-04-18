package com.example.parkingqr.ui.components.dialog

import android.app.Dialog
import android.content.Context
import android.view.ViewGroup
import com.example.parkingqr.R

class LoadingDialog(context: Context) {

    var dialog = Dialog(context)

    fun show() {
        dialog.setContentView(R.layout.loading_dialog)
        dialog.window?.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.window?.setBackgroundDrawableResource(R.drawable.bg_loading_dialog)
        dialog.setCancelable(true)
        dialog.create()
        dialog.show()
    }

    fun dismiss() {
        dialog.dismiss()
    }
}