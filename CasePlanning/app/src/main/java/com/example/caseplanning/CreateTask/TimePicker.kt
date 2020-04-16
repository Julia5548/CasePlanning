package com.example.caseplanning.CreateTask

import android.annotation.SuppressLint
import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import android.text.format.DateFormat
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import java.util.*

class TimePicker(var time : TextView):DialogFragment(), TimePickerDialog.OnTimeSetListener {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val calendar = Calendar.getInstance()
        val hour = calendar[Calendar.HOUR_OF_DAY]
        val minute = calendar[Calendar.MINUTE]

        return TimePickerDialog(activity, this ,hour, minute, DateFormat.is24HourFormat(activity))
    }


    @SuppressLint("SetTextI18n")

    override fun onTimeSet(timePicker: TimePicker?, hour: Int, minute: Int) {
        time.text = "$hour:$minute"
    }
}