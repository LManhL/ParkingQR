package com.example.parkingqr.ui.components.dialog

import android.app.Dialog
import android.content.Context
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import com.example.parkingqr.R

class AddCameraNormalDialog(
    private val context: Context,
    private val saveCallBack: ((String) -> Unit)
) {

    private var dialog = Dialog(context)
    private lateinit var uriCamera: EditText
    private lateinit var save: Button

    fun show() {
        dialog.setContentView(R.layout.dialog_add_camera_normal)
        uriCamera = dialog.findViewById(R.id.edtCamDialogAddCameraNormal)
        save = dialog.findViewById(R.id.btnSaveDialogAddCameraNormal)

        val displayMetrics = context.resources.displayMetrics
        val width = (displayMetrics.widthPixels * 0.95).toInt()
        val height = (displayMetrics.heightPixels * 0.6).toInt()


        dialog.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.window?.setBackgroundDrawableResource(R.drawable.bg_user_qr_code_dialog)

        dialog.setCancelable(true)
        dialog.create()
        dialog.show()

        save.setOnClickListener {
            saveCallBack.invoke(uriCamera.text.toString())
            dismiss()
        }
    }

    fun dismiss() {
        dialog.dismiss()
    }

}