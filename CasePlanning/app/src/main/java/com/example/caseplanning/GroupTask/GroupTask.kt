package com.example.caseplanning.GroupTask

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.NonNull
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.ButterKnife
import butterknife.OnClick
import com.example.caseplanning.CreateTask.MyViewModel
import com.example.caseplanning.DataBase.DataBase
import com.example.caseplanning.DataBase.Folder
import com.example.caseplanning.DataBase.Task
import com.example.caseplanning.MainActivity
import com.example.caseplanning.R
import com.example.caseplanning.Sidebar.Access
import com.example.caseplanning.Sidebar.Progress
import com.example.caseplanning.Sidebar.Setting
import com.example.caseplanning.Sidebar.TechSupport
import com.example.caseplanning.adapter.AdapterRecyclerViewFolder
import com.example.caseplanning.adapter.SwipeToDeleteCallback
import com.example.caseplanning.mainWindow.WindowTask
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.miguelcatalan.materialsearchview.MaterialSearchView
import io.reactivex.disposables.Disposable

class GroupTask : Fragment(), NavigationView.OnNavigationItemSelectedListener,
    AdapterView.OnItemClickListener {

    var pageViewModel: MyViewModel? = null
    var mDrawerLayout: DrawerLayout? = null
    lateinit var disposable: Disposable
    var mAdapterFolder: AdapterRecyclerViewFolder? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val viewFragment = inflater.inflate(R.layout.folders, container, false)

        val toolbar = viewFragment.findViewById<Toolbar>(R.id.toolbarGroup)
        val activity = activity as AppCompatActivity?
        activity!!.setSupportActionBar(toolbar)
        val actionBar = activity.supportActionBar
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

        val dataBaseTask = DataBase()
        disposable = dataBaseTask
            .retrieveDataUser(FirebaseAuth.getInstance().currentUser!!.uid)
            .subscribe({ user ->
                nameUser.text = user.name
                emailUser.text = user.email
            },
                { throwable ->
                    throwable.printStackTrace()
                }
            )

        listFolder(viewFragment)

        return viewFragment
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    private fun listFolder(viewFragment: View) {

        val dataBaseTask = DataBase()
        val listFolder = viewFragment.findViewById<RecyclerView>(R.id.listViewFolder)
        listFolder.layoutManager = LinearLayoutManager(context)

        /*подписываемся и выводим данные из бд, при выходе надо удалить подписчиков*/
        disposable = dataBaseTask
            .retrieveDataFolders()
            .subscribe({ folders ->
                val nameFolderList = arrayListOf<String>()
                val current_task = arrayListOf<Int>()
                val progress = arrayListOf<Float>()
                for (folder in folders) {
                    nameFolderList.add(folder.name)
                    current_task.add(folder.tasks!!.size)
                    progress.add(folder.progress.toFloat())
                }
                mAdapterFolder = AdapterRecyclerViewFolder(context!!, nameFolderList, current_task, progress)
                listFolder.adapter = mAdapterFolder
                enableSwipeToDeleteAndUndo(listFolder)
            },
                { throwable ->
                    throwable.printStackTrace()
                })
    }

    private fun enableSwipeToDeleteAndUndo(listFolder: RecyclerView) {
        val dataBaseTask = DataBase()
        val relativeLayout = view!!.findViewById<RelativeLayout>(R.id.relativeLayout)
        val disposable = dataBaseTask
            .retrieveDataFolders()
            .subscribe { folders ->
                var taskList: ArrayList<Task>? = null
                val swipeToDeleteCallback: SwipeToDeleteCallback =
                    object : SwipeToDeleteCallback(context!!) {
                        override fun onSwiped(
                            @NonNull viewHolder: RecyclerView.ViewHolder,
                            direction: Int
                        ) {
                            val position = viewHolder.adapterPosition
                            val item: String = mAdapterFolder!!.mData[position]
                            for (folder in folders) {
                                if (folder.name == item) {
                                    taskList = folder.tasks
                                    dataBaseTask.deletedDataFolder(folder.id)
                                    mAdapterFolder!!.mData.removeAt(position)
                                    mAdapterFolder!!.notifyDataSetChanged()

                                    val snackbar = Snackbar.make(
                                        relativeLayout,
                                        "Папка была удалена из списка",
                                        Snackbar.LENGTH_LONG
                                    )
                                    snackbar.setAction("Отменить") { _ ->
                                        val folderItem =
                                            Folder(id = "", name = item, tasks = taskList, progress = folder.progress)
                                        dataBaseTask.createFolder(folderItem)
                                        mAdapterFolder!!.notifyDataSetChanged()
                                        listFolder.scrollToPosition(position)
                                    }
                                    snackbar.setActionTextColor(Color.YELLOW)
                                    snackbar.show()
                                }
                            }
                        }
                    }
                val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
                itemTouchHelper.attachToRecyclerView(listFolder)
            }
        if(disposable != null && disposable.isDisposed)
            disposable.dispose()
    }

    @OnClick(R.id.addFolder)
    fun onClickFolderCreate() {

        val dataBaseTask = DataBase()
        val view = layoutInflater.inflate(R.layout.create_folder, null)
        MaterialAlertDialogBuilder(context)
            .setTitle("Добавить папку")
            .setView(view)
            .setPositiveButton("Добавить") { dialogInterface, id ->
                val outlinedTextField = view.findViewById<TextInputLayout>(R.id.outlinedTextField)
                val nameNewFolder = outlinedTextField.editText!!.text.toString()
                val listTask = arrayListOf<Task>()
                val folder = Folder(name = nameNewFolder, tasks = listTask, progress = "0")

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

                val groupTask: Fragment =
                    GroupTask()
                fragmentManager!!.beginTransaction()
                    .replace(R.id.linerLayout, groupTask)
                    .addToBackStack(null)
                    .commit()
            }
            R.id.tasks -> {
                val windowTask: Fragment =
                    WindowTask()
                fragmentManager!!.beginTransaction()
                    .replace(R.id.linerLayout, windowTask)
                    .addToBackStack(null)
                    .commit()
            }
            /*доступ к задачам другим людям*/
            R.id.access -> {

                val access: Fragment = Access()
                fragmentManager!!.beginTransaction()
                    .replace(R.id.linerLayout, access)
                    .addToBackStack(null)
                    .commit()

            }
            /*прогресс выполнения задач*/
            R.id.progress -> {

                val progress: Fragment =
                    Progress()
                fragmentManager!!.beginTransaction()
                    .replace(R.id.linerLayout, progress)
                    .addToBackStack(null)
                    .commit()

            }
            /*настройки*/
            R.id.setting -> {

                val setting: Fragment =
                    Setting()
                fragmentManager!!.beginTransaction()
                    .replace(R.id.linerLayout, setting)
                    .addToBackStack(null)
                    .commit()

            }
            /*техподдержка*/
            R.id.techSupport -> {

                val techSupport: Fragment =
                    TechSupport()
                fragmentManager!!.beginTransaction()
                    .replace(R.id.linerLayout, techSupport)
                    .addToBackStack(null)
                    .commit()

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
        Log.d("nomer", "${idItem}")
    }

    override fun onDestroy() {
        super.onDestroy()
        mDrawerLayout!!.removeAllViews()
        pageViewModel = null
        if (!disposable.isDisposed)
            disposable.dispose()
    }
}