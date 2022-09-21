package com.andikas.storyapp.utils.validator

import android.content.Context
import android.widget.EditText
import android.widget.TextView
import androidx.core.widget.doAfterTextChanged
import com.andikas.storyapp.R
import com.andikas.storyapp.utils.Extension.string
import com.andikas.storyapp.utils.Extension.validatePassword

class PasswordValidator(
    private val context: Context,
    private val editText: EditText,
    errorTextView: TextView
) : BaseValidator(errorTextView), Validator {

    override fun validate(validatorResult: ValidatorResult) {
        editText.doAfterTextChanged {
            val text = it.toString()
            if (text.isNotEmpty()) {
                if (text.length >= 6) {
                    if (text.validatePassword()) setFieldError(false)
                    else setFieldError(
                        true,
                        context.string(R.string.error_password_wrong_typing_format)
                    )
                } else setFieldError(
                    true,
                    context.string(R.string.error_password_wrong_length_format)
                )
            } else {
                setFieldError(true, context.string(R.string.error_password_empty))
            }
            validatorResult.validateResult()
        }
    }

    override fun validationResult(): Boolean =
        (editText.text.toString().isNotEmpty() && editText.text.toString()
            .validatePassword() && editText.text.toString().length >= 6)

}