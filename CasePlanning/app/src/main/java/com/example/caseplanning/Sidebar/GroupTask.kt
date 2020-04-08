package com.example.caseplanning.Sidebar

import android.content.DialogInterface
import android.content.Intent
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
import com.example.caseplanning.MainActivity
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

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            /*группа задач*/
            R.id.groupTask -> {

                val groupTask: Fragment = GroupTask()
                val transaction: FragmentTransaction = fragmentManager!!.beginTransaction()

                transaction.replace(R.id.linerLayout, groupTask)
                transaction.addToBackStack(null)
                transaction.commit()
            }
            /*доступ к задачам другим людям*/
            R.id.access -> {

                val access: Fragment = Access()
                val transaction: FragmentTransaction = fragmentManager!!.beginTransaction()

                transaction.replace(R.id.linerLayout, access)
                transaction.addToBackStack(null)
                transaction.commit()

            }
            /*прогресс выполнения задач*/
            R.id.progress -> {

                val progress: Fragment = Progress()
                val transaction: FragmentTransaction = fragmentManager!!.beginTransaction()

                transaction.replace(R.id.linerLayout, progress)
                transaction.addToBackStack(null)
                transaction.commit()

            }
            /*настройки*/
            R.id.setting -> {

                val setting: Fragment = Setting()
                val transaction: FragmentTransaction = fragmentManager!!.beginTransaction()

                transaction.replace(R.id.linerLayout, setting)
                transaction.addToBackStack(null)
                transaction.commit()

            }
            /*техподдержка*/
            R.id.techSupport -> {

                val techSupport: Fragment = TechSupport()
                val transaction: FragmentTransaction = fragmentManager!!.beginTransaction()

                transaction.replace(R.id.linerLayout, techSupport)
                transaction.addToBackStack(null)
                transaction.commit()

            }
            /*выход пользователя из системы*/
            R.id.signOut -> {
                mAuth.signOut()
                val intent = Intent(activity!!.applicationContext, MainActivity::class.java)
                //intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
            }
        }
        return true
    }

    override fun onItemClick(adapter: AdapterView<*>?,
                             view: View?,
                             id: Int,
                             p3: Long) {

        this.id = id

    }
}