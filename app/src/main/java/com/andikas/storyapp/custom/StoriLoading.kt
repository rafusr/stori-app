package com.andikas.storyapp.custom

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import android.view.WindowManager
import com.andikas.storyapp.databinding.DialogLoadingBinding

class StoriLoading(context: Context) : Dialog(context) {

    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        DialogLoadingBinding.inflate(layoutInflater)
    }

    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        setContentView(binding.root)
        setCancelable(false)
    }

    fun showLoading(state: Boolean) {
        if (state) show() else dismiss()
    }

}