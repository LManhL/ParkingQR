package com.example.parkingqr.ui.components.dialog

import android.app.Dialog
import android.content.Context
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import com.example.parkingqr.R

class AddCameraInOutDialog(
    private val context: Context,
    private val uriCameraInStr: String,
    private val uriCameraOutStr: String,
    private val saveCallBack: ((String, String) -> Unit)
) {

    private var dialog = Dialog(context)
    private lateinit var uriCameraIn: EditText
    private lateinit var uriCameraOut: EditText
    private lateinit var save: Button

    fun show() {
        dialog.setContentView(R.layout.dialog_add_camera)
        uriCameraIn = dialog.findViewById(R.id.edtCamInDialogAddCamera)
        uriCameraOut = dialog.findViewById(R.id.edtCamOutDialogAddCamera)
        save = dialog.findViewById(R.id.btnSaveDialogAddCamera)
        uriCameraIn.setText(uriCameraInStr)
        uriCameraOut.setText(uriCameraOutStr)

        val displayMetrics = context.resources.displayMetrics
        val width = (displayMetrics.widthPixels * 0.95).toInt()
        val height = (displayMetrics.heightPixels * 0.6).toInt()


        dialog.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.window?.setBackgroundDrawableResource(R.drawable.bg_user_qr_code_dialog)

        dialog.setCancelable(true)
        dialog.create()
        dialog.show()

        save.setOnClickListener {
            saveCallBack.invoke(uriCameraIn.text.toString(), uriCameraOut.text.toString())
            dismiss()
        }
    }

    fun dismiss() {
        dialog.dismiss()
    }

}