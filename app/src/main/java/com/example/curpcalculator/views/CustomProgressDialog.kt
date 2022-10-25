package com.example.curpcalculator.views

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.widget.TextView
import com.example.curpcalculator.R

class CustomProgressDialog(context: Context) {

    private val progressDialog: Dialog = Dialog(context)

    val isShowing: Boolean
        get() = this.progressDialog.isShowing

    init {
        this.progressDialog.setCancelable(false)
    }

    fun showCustomProgressDialog(text: String) {
        if (!isShowing) {
            this.progressDialog.show()
            this.progressDialog.setContentView(R.layout.custom_progress_dialog)
            this.progressDialog.findViewById<TextView>(R.id.textView1).text = text
            this.progressDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }

    fun dismissCustomProgressDialog() {
        this.progressDialog.dismiss()
    }
}
