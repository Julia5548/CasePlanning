package com.example.caseplanning.sign_up

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import com.example.caseplanning.R
import com.google.android.material.card.MaterialCardView
import kotlinx.android.synthetic.main.activity_sign_up.view.*

class ProgressButton() {

    private lateinit var imageView : ImageView
    private lateinit var progress : TextView
    private lateinit var progressBar : ProgressBar
    private lateinit var relativeLayout: RelativeLayout
    private lateinit var cardView: MaterialCardView

    lateinit var fade_in : Animation

    constructor(context: Context, view : View) : this() {

        fade_in = AnimationUtils.loadAnimation(context, R.anim.fade_in)

        imageView = view.findViewById(R.id.image_done_error)
        progress = view.findViewById(R.id.btn_sign_up)
        progressBar = view.findViewById(R.id.progress_bar)
        relativeLayout = view.findViewById(R.id.relativeLayout_progress)
        cardView = view.findViewById(R.id.cardViewProgress)
    }

    fun buttonActivated(){
        progressBar.animation = fade_in
        progressBar.visibility = View.VISIBLE
        progress.animation = fade_in
        progress.text = "Подождите"
    }

    fun recoveryState(){

        progressBar.visibility = View.GONE
        progress.text = "Регистрация"
        relativeLayout.setBackgroundColor(cardView.resources.getColor(R.color.colorPrimary))
        imageView.visibility = View.GONE
    }

    fun buttonFinished(){

        progressBar.visibility = View.GONE
        progress.text = "Успешно"
        relativeLayout.setBackgroundColor(cardView.resources.getColor(R.color.green))
        imageView.visibility = View.VISIBLE
        imageView.setImageResource(R.drawable.ic_baseline_check_24)
    }

    fun buttonFailed(){

        progressBar.visibility = View.GONE
        progress.text = "Ошибка"
        relativeLayout.setBackgroundColor(cardView.resources.getColor(R.color.red))
        imageView.visibility = View.VISIBLE
        imageView.setImageResource(R.drawable.ic_baseline_clear_24)
    }
}