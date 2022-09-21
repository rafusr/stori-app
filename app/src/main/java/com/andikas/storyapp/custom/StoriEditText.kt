package com.andikas.storyapp.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.andikas.storyapp.R
import com.andikas.storyapp.utils.Extension.drawable
import com.andikas.storyapp.utils.Extension.string
import com.andikas.storyapp.utils.Extension.validateChar
import com.andikas.storyapp.utils.Extension.validateEmail
import com.andikas.storyapp.utils.Extension.validatePassword

class StoriEditText : AppCompatEditText {

    private lateinit var mBackground: Drawable

    @DrawableRes
    private var mIntBackground: Int = -1
    private lateinit var mErrorBackground: Drawable

    @DrawableRes
    private var mErrorIntBackground: Int = -1
    private var mShadowColor: Int = -1
    private var mTextColor: Int = -1
    private var mHintTextColor: Int = -1

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
        stateListAnimator = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            outlineAmbientShadowColor = mShadowColor
            outlineSpotShadowColor = mShadowColor
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) textCursorDrawable = null
        background = mBackground
        setBackgroundResource(mIntBackground)
        setTextColor(mTextColor)
        setHintTextColor(mHintTextColor)
    }

    private fun init() {
        mShadowColor = ContextCompat.getColor(context, R.color.primary_blue)
        mBackground = ContextCompat.getDrawable(context, R.drawable.bg_field) as Drawable
        mIntBackground = R.drawable.bg_field
        mErrorBackground = ContextCompat.getDrawable(context, R.drawable.bg_field_error) as Drawable
        mErrorIntBackground = R.drawable.bg_field_error
        mTextColor = ContextCompat.getColor(context, R.color.primary_key)
        mHintTextColor = ContextCompat.getColor(context, R.color.primary_grey)

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                val mText = p0.toString()
                when (inputType - 1) {
                    InputType.TYPE_TEXT_VARIATION_PASSWORD -> {
                        if (mText.isNotEmpty()) {
                            if (mText.length >= 6) {
                                if (mText.validatePassword()) setFieldError(false)
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
                    }
                    InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS -> {
                        if (mText.isNotEmpty()) {
                            if (mText.validateEmail()) setFieldError(false)
                            else setFieldError(
                                true,
                                context.string(R.string.error_email_wrong_format)
                            )
                        } else {
                            setFieldError(true, context.string(R.string.error_email_empty))
                        }
                    }
                    InputType.TYPE_TEXT_FLAG_CAP_WORDS -> {
                        if (mText.isNotEmpty()) {
                            if (mText.validateChar()) setFieldError(false)
                            else setFieldError(
                                true,
                                context.string(R.string.error_name_wrong_format)
                            )
                        } else {
                            setFieldError(true, context.string(R.string.error_name_empty))
                        }
                    }
                }
            }

        })
    }

    private fun setFieldError(state: Boolean, message: String? = null) {
        if (state) {
            background = mErrorBackground
            setBackgroundResource(mErrorIntBackground)
            this@StoriEditText.error = message
        } else {
            background = context.drawable(R.drawable.bg_field)
        }
    }

}