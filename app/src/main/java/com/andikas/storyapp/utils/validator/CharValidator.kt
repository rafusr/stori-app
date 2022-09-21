package com.andikas.storyapp.utils.validator

import android.content.Context
import android.widget.EditText
import android.widget.TextView
import androidx.core.widget.doAfterTextChanged
import com.andikas.storyapp.R
import com.andikas.storyapp.utils.Extension.string
import com.andikas.storyapp.utils.Extension.validateChar

class CharValidator(
    private val context: Context,
    private val editText: EditText,
    errorTextView: TextView
) : BaseValidator(errorTextView), Validator {
    override fun validate(validatorResult: ValidatorResult) {
        editText.doAfterTextChanged {
            val text = it.toString()
            if (text.isNotEmpty()) {
                if (text.validateChar()) setFieldError(false)
                else setFieldError(
                    true,
                    context.string(R.string.error_name_wrong_format)
                )
            } else {
                setFieldError(true, context.string(R.string.error_name_empty))
            }
            validatorResult.validateResult()
        }
    }

    override fun validationResult(): Boolean =
        (editText.text.toString().isNotEmpty() && editText.text.toString().validateChar())
}