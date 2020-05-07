package com.example.caseplanning.Sidebar

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.ButterKnife
import butterknife.OnClick
import com.example.caseplanning.CreateTask.MyViewModel
import com.example.caseplanning.DataBase.DataBaseTask
import com.example.caseplanning.DataBase.Folder
import com.example.caseplanning.DataBase.Task
import com.example.caseplanning.MainActivity
import com.example.caseplanning.R
import com.example.caseplanning.mainWindow.WindowTask
import com.example.caseplanning.adapter.AdapterRecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.navigation.NavigationView
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.miguelcatalan.materialsearchview.MaterialSearchView
import io.reactivex.disposables.Disposable

class GroupTask : Fragment(), NavigationView.OnNavigationItemSelectedListener,
    AdapterView.OnItemClickListener {

    var pageViewModel: MyViewModel? = null
    var mDrawerLayout: DrawerLayout? = null
    lateinit var disposable: Disposable
    private var actionBar: ActionBar? = null
    private var cancel : MenuItem? = null
    private var edit : MenuItem? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val viewFragment = inflater.inflate(R.layout.folder_tasks, container, false)

        val toolbar = viewFragment.findViewById<Toolbar>(R.id.toolbarGroup)
        val activity = activity as AppCompatActivity?
        activity!!.setSupportActionBar(toolbar)
        actionBar = activity.supportActionBar
        actionBar!!.title = "Группы задач"

        ButterKnife.bind(this, viewFragment)


        /*кнопка поиска*/
        val search = viewFragment.findViewById<MaterialSearchView>(R.id.search)
        search.closeSearch()

        /*боковое меню*/
         mDrawerLayout = viewFragment.findViewById<DrawerLayout>(R.id.drawerLayout)
        /*подключение обработчика события кнопок бокового меню*/
        val navigationView = viewFragment.findViewById<NavigationView>(R.id.navigationView)
        navigationView.setNavigationItemSelectedListener(this)

        val navHeader = navigationView.getHeaderView(0)
        val emailUser = navHeader.findViewById<TextView>(R.id.emailText)
        val nameUser = navHeader.findViewById<TextView>(R.id.nameUser)


        val mToggle = ActionBarDrawerToggle(
            activity, mDrawerLayout, toolbar,
            R.string.Open, R.string.Close
        )

        mDrawerLayout!!.addDrawerListener(mToggle)
        /*проверяем состояние*/
        mToggle.syncState()

        val dataBaseTask = DataBaseTask()
        disposable = dataBaseTask
            .retrieveDataUser()
            .subscribe({ user ->
                nameUser.text = user.name
                emailUser.text = user.email
            },
                { throwable ->
                    throwable.printStackTrace()
                })

        listFolder(viewFragment)

        return viewFragment
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    private fun listFolder(viewFragment: View) {

        val dataBaseTask = DataBaseTask()
        val listFolder = viewFragment.findViewById<RecyclerView>(R.id.listViewFolder)
        listFolder.layoutManager = LinearLayoutManager(context)

        /*подписываемся и выводим данные из бд, при выходе надо удалить подписчиков*/
        disposable = dataBaseTask
            .retrieveDataFolders()
            .subscribe({ folders ->
                val nameFolderList = arrayListOf<String>()
                for (folder in folders)
                    nameFolderList.add(folder.name)
                listFolder.adapter = AdapterRecyclerView(context!!, nameFolderList)
            },
                { throwable ->
                    throwable.printStackTrace()
                })

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.edit, menu)
        cancel= menu.findItem(R.id.cancel_edit)
        edit = menu.findItem(R.id.edit)
        return super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            R.id.edit->{
                if(item.title == "Удалить"){
                    item.title = "Изменить"
                    cancel?.isVisible = false
                }else {
                    item.title = "Удалить"
                    cancel?.isVisible = true
                }
                true
            }
            R.id.cancel_edit->{
                edit?.title = "Изменить"
                cancel?.isVisible = false
                true
            }
            else-> return super.onOptionsItemSelected(item)
        }
    }

    @OnClick(R.id.addFolder)
    fun onClickFolderCreate() {

        val dataBaseTask = DataBaseTask()
        val view = layoutInflater.inflate(R.layout.create_folder, null)
        MaterialAlertDialogBuilder(context)
            .setTitle("Добавить папку")
            .setView(view)
            .setPositiveButton("Добавить") { dialogInterface, id ->
                val outlinedTextField = view.findViewById<TextInputLayout>(R.id.outlinedTextField)
                val nameNewFolder = outlinedTextField.editText!!.text.toString()
                val folder = Folder()

                val listTask = arrayListOf<Task>()
                val task = Task(name = "kzd")
                listTask.add(task)
                folder.name = nameNewFolder
                folder.tasks = listTask

                dataBaseTask.createFolder(folder)

                Toast.makeText(context, "Папка $nameNewFolder успешно создана", Toast.LENGTH_SHORT)
                    .show()

            }
            .setNegativeButton("Отменить", null)
            .show()
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
            R.id.tasks -> {
                val windowTask: Fragment =
                    WindowTask()
                val transaction: FragmentTransaction = fragmentManager!!.beginTransaction()

                transaction.replace(R.id.linerLayout, windowTask)
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
                disposable.dispose()
                val mAuth = FirebaseAuth.getInstance()
                mAuth.signOut()
                val intent = Intent(activity!!.applicationContext, MainActivity::class.java)
                //intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
            }
        }
        mDrawerLayout!!.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onItemClick(
        adapter: AdapterView<*>?,
        view: View?,
        id: Int,
        p3: Long
    ) {

        val idItem = id

    }

    override fun onDestroy() {
        super.onDestroy()
        mDrawerLayout!!.removeAllViews()
        pageViewModel = null
        if(!disposable.isDisposed)
            disposable.dispose()
    }
}