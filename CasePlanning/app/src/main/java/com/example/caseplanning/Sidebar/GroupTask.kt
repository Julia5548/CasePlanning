package com.example.caseplanning.Sidebar

import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.get
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import butterknife.ButterKnife
import butterknife.OnClick
import butterknife.OnItemLongClick
import com.example.caseplanning.CreateTask.MyViewModel
import com.example.caseplanning.DataBase.DataBaseTask
import com.example.caseplanning.EditElements.EditFolder
import com.example.caseplanning.R
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.miguelcatalan.materialsearchview.MaterialSearchView
import java.lang.Exception

class GroupTask: Fragment(), NavigationView.OnNavigationItemSelectedListener, AdapterView.OnItemClickListener {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var search: MaterialSearchView
    private val dataBaseTask = DataBaseTask()
    var nameFolder: String? = null
    lateinit var listFolder : ListView
    var list: ArrayList<String>? = null
    var adapter : ArrayAdapter<String>? = null
    private lateinit var mDrawerLayout : DrawerLayout
    private lateinit var  mToggle : ActionBarDrawerToggle
var pageViewModel : MyViewModel = MyViewModel()
    var id : Int? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val viewFragment = inflater.inflate(R.layout.folder_tasks, container, false)

        val toolbar = viewFragment.findViewById<Toolbar>(R.id.toolbar)
        val activity = activity as AppCompatActivity?
        activity!!.setSupportActionBar(toolbar)
        val actionBar: ActionBar? = activity.supportActionBar
        actionBar!!.title = "Группы задач"

        ButterKnife.bind(this, viewFragment)

        mAuth = FirebaseAuth.getInstance()

        /*кнопка поиска*/
        search = viewFragment.findViewById<MaterialSearchView>(R.id.search)
        search.closeSearch()

        /*боковое меню*/
        mDrawerLayout = viewFragment.findViewById(R.id.drawerLayout)
        mToggle = ActionBarDrawerToggle(activity, mDrawerLayout,
            R.string.Open, R.string.Close)
        mDrawerLayout.addDrawerListener(mToggle)
        /*проверяем состояние*/
        mToggle.syncState()
        /*добавление стрелки для закрытия бокового меню, делает ее кликабельной*/
        actionBar.setDisplayHomeAsUpEnabled(true)

        /*подключение обработчика события кнопок бокового меню*/
        val navigationView = viewFragment.findViewById<NavigationView>(R.id.navigationView)
        navigationView.setNavigationItemSelectedListener(this)

        list = arrayListOf()
        val pageViewModel = ViewModelProviders.of(requireActivity()).get(MyViewModel::class.java)

        pageViewModel.getNameFolder().observe(requireActivity(), Observer<String> {
            nameFolder = it
        })
        if (nameFolder != null) {

            list!!.add(nameFolder!!)
        }
        listTask(viewFragment, list)

        return viewFragment
    }

    private fun listTask(viewFragment: View, stringList: ArrayList<String>?) {


        listFolder = viewFragment.findViewById<ListView>(R.id.listViewFolder)

        /*подписываемся и выводим данные из бд, при выходе надо удалить подписчиков*/
        /*    val disposable = dataBaseTask.retrieveData()
                .subscribe {
                    val stringList = arrayListOf<String>()

                    for(task in it) {
                        stringList.add(task.name)
                    }*/
        adapter = ArrayAdapter<String>(
            activity!!.applicationContext,
            android.R.layout.simple_list_item_1,
            stringList!!
        )

        listFolder.adapter = adapter
        registerForContextMenu(listFolder)
        listFolder.onItemClickListener = this

    }

    @OnClick(R.id.addFolder)
    fun onClickFolderCreate(){
              val view = layoutInflater.inflate(R.layout.create_folder, null)
        val mBuilder = AlertDialog.Builder(activity!!)
        mBuilder.setView(view)

        val nameFolder = view.findViewById<EditText>(R.id.editNameFolder)
        mBuilder.setPositiveButton("Добавить"
        ) { dialog, id ->

            try{
                val nameNewFolder = nameFolder.text.toString()
                pageViewModel.setnameFolder(nameNewFolder)
                list!!.add(nameNewFolder)
                adapter!!.notifyDataSetChanged()
                Toast.makeText(context, "Папка $nameNewFolder успешно создана" , Toast.LENGTH_SHORT).show()
            }catch (e : Exception){
                Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()

            }
        }
        mBuilder.show()

    }

    /*появление кнопок при нажатие на элемент из листа*/
    override fun onCreateContextMenu(
        menu: ContextMenu,
        v: View,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        val inflater: MenuInflater = activity!!.menuInflater
        inflater.inflate(R.menu.menu_task, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val info : AdapterView.AdapterContextMenuInfo =  item.menuInfo as AdapterView.AdapterContextMenuInfo
       this.id = info.position
        val name = listFolder.getItemAtPosition(this.id!!).toString()
        when (item.itemId) {
            R.id.edit -> {
                //действия при изменении
                val editFolder  = EditFolder(context, requireActivity(), adapter, list!!, id!!)
                editFolder.createDialog(name)
                val pageViewModel = ViewModelProviders.of(requireActivity()).get(MyViewModel::class.java)

                pageViewModel.getNameFolder().observe(requireActivity(), Observer<String> {
                    nameFolder = it
                })


                return true
            }
            R.id.deleted -> {
                list!!.removeAt(0)
                adapter!!.notifyDataSetChanged()
                return true
            }

        }
        return super.onContextItemSelected(item)
    }

    override fun onNavigationItemSelected(p0: MenuItem): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onItemClick(adapter: AdapterView<*>?,
                             view: View?,
                             id: Int,
                             p3: Long) {

        this.id = id

    }
}