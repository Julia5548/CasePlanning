package com.example.caseplanning.adapter


import android.annotation.SuppressLint
import android.graphics.Canvas
import android.view.MotionEvent
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.ACTION_STATE_SWIPE
import androidx.recyclerview.widget.ItemTouchHelper.LEFT
import androidx.recyclerview.widget.RecyclerView
import java.lang.Math.min
import kotlin.math.max


class SwipeController :
    ItemTouchHelper.Callback() {

    private var currentItemViewHolder: RecyclerView.ViewHolder? = null
    private var buttonWidth: Float = 150f
    private var buttonState = ButtonState.GONE
    private var swipeBack: Boolean = false

    enum class ButtonState{
        GONE,
        LEFT_VISIBLE,
        RIGHT_VISIBLE
    }

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        return makeMovementFlags(0, LEFT)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean = true

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}

}

