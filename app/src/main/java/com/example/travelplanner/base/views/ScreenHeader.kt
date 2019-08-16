package com.example.travelplanner.base.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.travelplanner.R
import kotlinx.android.synthetic.main.view_screen_header.view.*

typealias Listener = (() -> Unit)

class ScreenHeader @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : ConstraintLayout(context, attrs, defStyleAttr) {

    var leftButtonListener: Listener? = null
    var rightButtonListener: Listener? = null
    var primaryText: String? = null
        set(value) {
            field = value
            updatePrimaryText(value)
        }
    var secondaryText: String? = null
        set(value) {
            field = value
            updateSecondaryText(value)
        }

    init {
        View.inflate(context, R.layout.view_screen_header, this)
        val verticalPadding = resources.getDimensionPixelSize(R.dimen.screen_header_vertical_padding)
        setPadding(paddingLeft, verticalPadding + paddingTop, paddingRight, verticalPadding + paddingBottom)
        applyAttrs(attrs)
        leftButton.setOnClickListener { leftButtonListener?.invoke() }
        rightButton.setOnClickListener { rightButtonListener?.invoke() }
    }

    private fun applyAttrs(attrs: AttributeSet?) {
        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.ScreenHeader)
            primaryText = a.getString(R.styleable.ScreenHeader_primaryText) ?: ""
            secondaryText = a.getString(R.styleable.ScreenHeader_secondaryText) ?: ""
            val leftButtonImage = a.getResourceId(R.styleable.ScreenHeader_leftButtonImage, 0)
            val rightButtonImage = a.getResourceId(R.styleable.ScreenHeader_rightButtonImage, 0)
            a.recycle()

            if (leftButtonImage != 0) leftButton.setImageResource(leftButtonImage)
            leftButton.visibility = if (leftButtonImage != 0) View.VISIBLE else View.INVISIBLE

            if (rightButtonImage != 0) rightButton.setImageResource(rightButtonImage)
            rightButton.visibility = if (rightButtonImage != 0) View.VISIBLE else View.INVISIBLE
        }
    }

    private fun updatePrimaryText(text: String?) {
        primaryTitleView.text = text
        primaryTitleView.visibility = if (!text.isNullOrEmpty()) View.VISIBLE else View.GONE
    }

    private fun updateSecondaryText(text: String?) {
        secondaryTitleView.text = text
        secondaryTitleView.visibility = if (!text.isNullOrEmpty()) View.VISIBLE else View.GONE
    }
}
