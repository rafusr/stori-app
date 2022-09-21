package com.andikas.storyapp.custom

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Canvas
import android.os.Build
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import com.andikas.storyapp.R
import com.google.android.material.button.MaterialButton

class StoriButton : MaterialButton {

    private lateinit var enabledBackgroundColor: ColorStateList
    private lateinit var disabledBackgroundColor: ColorStateList
    private lateinit var enabledTextColor: ColorStateList
    private lateinit var disabledTextColor: ColorStateList
    private var mShadowColor: Int = -1

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

        letterSpacing = 0F
        includeFontPadding = false
        isAllCaps = false
        stateListAnimator = null
        cornerRadius = 8
        backgroundTintList = if (isEnabled) enabledBackgroundColor else disabledBackgroundColor
        insetTop = 0
        insetBottom = 0
        setTextColor(if (isEnabled) enabledTextColor else disabledTextColor)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            outlineAmbientShadowColor = mShadowColor
            outlineSpotShadowColor = mShadowColor
        }
    }

    private fun init() {
        mShadowColor = ContextCompat.getColor(context, R.color.primary_blue)
        enabledBackgroundColor =
            ContextCompat.getColorStateList(context, R.color.primary_blue) as ColorStateList
        disabledBackgroundColor =
            ContextCompat.getColorStateList(context, R.color.neutral_grey) as ColorStateList
        enabledTextColor = ContextCompat.getColorStateList(context, R.color.white) as ColorStateList
        disabledTextColor =
            ContextCompat.getColorStateList(context, R.color.primary_grey) as ColorStateList
    }

}