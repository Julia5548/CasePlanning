package com.example.caseplanning

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import butterknife.ButterKnife
import butterknife.OnClick
import com.example.caseplanning.Photo.Photo

class CreateTaskWindow  : Fragment(){

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val viewFragment = inflater.inflate(R.layout.task_window, container, false)

        val toolbar = viewFragment.findViewById<Toolbar>(R.id.toolbarCreateTask)
        val activity = activity as AppCompatActivity?
        activity!!.setSupportActionBar(toolbar)
        val actionBar: ActionBar? = activity.supportActionBar
        actionBar!!.title = "Создать"
        toolbar.setTitleTextColor(android.graphics.Color.WHITE)

        ButterKnife.bind(this, viewFragment)

        return viewFragment
    }
    @OnClick(R.id.photo)
    fun onClickAddPhoto(){

        val photo: Fragment = Photo()
        val transaction: FragmentTransaction = fragmentManager!!.beginTransaction()

        transaction.add(R.id.typeTask, photo)
        transaction.commit()

    }
}