package com.aleksagn.playlistmaker.ui.player

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import com.aleksagn.playlistmaker.R

class PlaybackButtonView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
    @StyleRes defStyleRes: Int = 0,
) : View(context, attrs, defStyleAttr, defStyleRes) {

    private var playIcon: Drawable? = null
    private var pauseIcon: Drawable? = null
    private var imageRect = RectF(0f, 0f, 0f, 0f)

    var isPlaying = false
        set(value) {
            if (field == value) return
            field = value
            invalidate()
        }

    init {
        isClickable = true
        isFocusable = true

        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.PlaybackButtonView,
            defStyleAttr,
            defStyleRes
        ).apply {
            try {
                playIcon = getDrawable(R.styleable.PlaybackButtonView_playIcon)
                pauseIcon = getDrawable(R.styleable.PlaybackButtonView_pauseIcon)
            } finally {
                recycle()
            }
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val size = minOf(w, h) * 1f
        val left = (w - size) / 2f
        val top = (h - size) / 2f
        val right = left + size
        val bottom = top + size

        imageRect.set(left, top, right, bottom)

        playIcon?.bounds = Rect(imageRect.left.toInt(), imageRect.top.toInt(),
            imageRect.right.toInt(), imageRect.bottom.toInt())

        pauseIcon?.bounds = Rect(imageRect.left.toInt(), imageRect.top.toInt(),
            imageRect.right.toInt(), imageRect.bottom.toInt())
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val icon = if (isPlaying) pauseIcon else playIcon
        icon?.draw(canvas)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!isEnabled) return super.onTouchEvent(event)
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                return true
            }
            MotionEvent.ACTION_UP -> {
                performClick()
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }
}
