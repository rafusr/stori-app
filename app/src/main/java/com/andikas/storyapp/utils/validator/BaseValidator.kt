package com.andikas.storyapp.utils.validator

import android.widget.TextView
import com.andikas.storyapp.utils.Extension.visible

abstract class BaseValidator(private val errorTextView: TextView) {

    fun setFieldError(state: Boolean, message: String? = null) {
        if (state) {
            errorTextView.apply {
                visible(true)
                text = message
            }
        } else {
            errorTextView.visible(false)
        }
    }

}