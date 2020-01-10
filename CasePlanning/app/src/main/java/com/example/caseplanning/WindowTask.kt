package com.example.caseplanning

import android.content.Intent
import android.content.Intent.getIntent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.size
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import butterknife.ButterKnife
import butterknife.OnClick
import com.example.caseplanning.DataBase.DataBaseTask
import com.example.caseplanning.TypeTask.ToDoTask
import com.google.firebase.auth.FirebaseAuth
import com.miguelcatalan.materialsearchview.MaterialSearchView
import kotlinx.android.synthetic.main.task_window.view.*
import kotlinx.android.synthetic.main.to_do.*


class WindowTask() : Fragment() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var search: MaterialSearchView
    var textTask: String? = " "
    lateinit var listTasks : ListView


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val viewFragment = inflater.inflate(R.layout.window_main_tasks, container, false)

        val toolbar = viewFragment.findViewById<Toolbar>(R.id.toolbarTask)
        val activity = activity as AppCompatActivity?
        activity!!.setSupportActionBar(toolbar)
        val actionBar: ActionBar? = activity.supportActionBar
        actionBar!!.title = "Главное меню"
        toolbar.setTitleTextColor(android.graphics.Color.WHITE)

        ButterKnife.bind(this, viewFragment)

        mAuth = FirebaseAuth.getInstance()

        /*кнопка поиска*/
        search = viewFragment.findViewById<MaterialSearchView>(R.id.search)
        search.closeSearch()

        val intent: Intent = activity.intent
        textTask = intent.getStringExtra("nameTask")

        listTask(viewFragment, textTask)

        return viewFragment
    }

    private fun listTask(viewFragment: View, nameTask: String?) {


        listTasks = viewFragment.findViewById<ListView>(R.id.listViewTask)

            val adapter = ArrayAdapter<String>(
                activity!!.applicationContext,
                android.R.layout.simple_list_item_1)

            val dataBaseTask = DataBaseTask(listTasks, nameTask, adapter)
            dataBaseTask.readDataBase()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    //inflate the menu
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search, menu)

        val searchItem = menu.findItem(R.id.search)
        search.setMenuItem(searchItem)
        search.setOnQueryTextListener(object : MaterialSearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                /*поиск задач*/
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                /*поиск*/
                return false
            }

        })
        search.setOnSearchViewListener(object : MaterialSearchView.SearchViewListener {
            override fun onSearchViewClosed() {
                /*поиск*/
                /* currentText = ""
                 search.setAudioList(mData)*/
            }

            override fun onSearchViewShown() {
                // search.setQuery(currentText,false)
            }
        })
        super.onCreateOptionsMenu(menu, inflater)
    }


    @OnClick(R.id.btn_signOut)
    fun onClickSignOut() {
        //выход пользователя из системы
        mAuth.signOut()

        val intent = Intent(activity!!.applicationContext, MainActivity::class.java)
        //intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)

    }


    @Suppress("DEPRECATION")
    @OnClick(R.id.addTask)
    fun onClickBtnAdd() {
        val createTask: Fragment = CreateTaskWindow(listTasks)
        val transaction: FragmentTransaction = fragmentManager!!.beginTransaction()

        transaction.replace(R.id.linerLayout, createTask)
        transaction.addToBackStack(null)
        transaction.commit()


    }
}


