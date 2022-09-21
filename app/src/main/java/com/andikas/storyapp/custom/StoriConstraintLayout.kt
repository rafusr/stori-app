package com.andikas.storyapp.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet
import androidx.annotation.DrawableRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.andikas.storyapp.R

class StoriConstraintLayout : ConstraintLayout {

    private lateinit var mBackground: Drawable
    @DrawableRes
    private var mIntBackground: Int = -1
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            outlineAmbientShadowColor = mShadowColor
            outlineSpotShadowColor = mShadowColor
        }
        background = mBackground
        setBackgroundResource(mIntBackground)
    }

    private fun init() {
        mShadowColor = ContextCompat.getColor(context, R.color.primary_blue)
        mBackground = ContextCompat.getDrawable(context, R.drawable.bg_view_group) as Drawable
        mIntBackground = R.drawable.bg_view_group
    }

}