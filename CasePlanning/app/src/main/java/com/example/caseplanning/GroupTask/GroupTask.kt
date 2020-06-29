package com.example.caseplanning.GroupTask

import android.annotation.SuppressLint
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
import com.example.caseplanning.Setting.Setting
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
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

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

        val user = FirebaseAuth.getInstance().currentUser!!
        user.let {
            nameUser.text = user.displayName
            emailUser.text = user.email
        }
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
                val folder_list = arrayListOf<Folder>()
                val current_task = arrayListOf<Int>()
                val progress = arrayListOf<Float>()
                for (folder in folders) {
                    restore_file(folder)
                    folder_list.add(folder)
                    current_task.add(folder.tasks!!.size)
                    progress.add(folder.progress.toFloat())
                }
                mAdapterFolder =
                    AdapterRecyclerViewFolder(context!!, folder_list, current_task, progress)
                listFolder.adapter = mAdapterFolder
                enableSwipeToDeleteAndUndo(listFolder)

                if(!disposable.isDisposed)
                    disposable.dispose()
            },
                { throwable ->
                    throwable.printStackTrace()
                })
    }

    @SuppressLint("SimpleDateFormat")
    private fun convertFormat(day: String): Date? {
        if(day != "") {
            var date = day
            val arrayDate: List<String> = date.split(".")
            val month: String
            if (arrayDate[1].length == 1) {
                month = "0${arrayDate[1]}"
                date = "${arrayDate[0]}.$month.${arrayDate[2]}"
            }
            val dayTask: String
            if (arrayDate[0].length == 1) {
                dayTask = "0${arrayDate[0]}"
                date = "$dayTask.${arrayDate[1]}.${arrayDate[2]}"
            }
            val format = SimpleDateFormat("dd.MM.yyy")
            val formatedDay = format.parse(date)
            return formatedDay
        }else{
            return null
        }
    }

    private fun restore_file(folder: Folder) {
        val calendar= Calendar.getInstance()
        val format = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        val formatedDate = format.format(calendar.time)
        val date = format.parse(formatedDate)!!
        if(date != convertFormat(folder.date)){
            for ((position,task) in folder.tasks!!.withIndex()){
                task.checked = false
                folder.tasks!![position] = task
            }
            folder.progress = "0"
            folder.date = formatedDate
            val dataBase  = DataBase()
            dataBase.updateDataFolder(folder, folder.id)
        }
    }

    private fun enableSwipeToDeleteAndUndo(
        listFolder: RecyclerView
    ) {
        val dataBaseTask = DataBase()
        val relativeLayout = view!!.findViewById<RelativeLayout>(R.id.relativeLayout)
        var taskList: ArrayList<Task>?
        val swipeToDeleteCallback: SwipeToDeleteCallback =
            object : SwipeToDeleteCallback(context!!) {
                override fun onSwiped(
                    @NonNull viewHolder: RecyclerView.ViewHolder,
                    direction: Int
                ) {
                    val position = viewHolder.adapterPosition
                    val item: String = mAdapterFolder!!.mData[position].name
                    val progress = mAdapterFolder!!.mData[position].progress
                    val date = mAdapterFolder!!.mData[position].date
                    taskList = mAdapterFolder!!.mData[position].tasks
                    dataBaseTask.deletedDataFolder(mAdapterFolder!!.mData[position].id)
                    mAdapterFolder!!.mData.removeAt(position)
                    mAdapterFolder!!.notifyDataSetChanged()

                    val snackbar = Snackbar.make(
                        relativeLayout,
                        "Папка была удалена из списка",
                        Snackbar.LENGTH_LONG
                    )
                    snackbar.setAction("Отменить") { _ ->
                        val folderItem =
                            Folder(
                                id = "",
                                name = item,
                                tasks = taskList,
                                progress = progress,
                                date = date
                            )
                        dataBaseTask.createFolder(folderItem)
                        mAdapterFolder!!.mData.add(folderItem)
                        mAdapterFolder!!.notifyDataSetChanged()
                        listFolder.scrollToPosition(position)
                    }
                    snackbar.setActionTextColor(Color.YELLOW)
                    snackbar.show()
                }
            }
        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchHelper.attachToRecyclerView(listFolder)
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

                val calendar= Calendar.getInstance()
                val format = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                val formatedDate = format.format(calendar.time)

                val folder = Folder(name = nameNewFolder, tasks = listTask, progress = "0", date = formatedDate)

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
                if(fragmentManager!!.findFragmentById(R.id.linerLayout) != null) {
                    fragmentManager!!.beginTransaction().remove(GroupTask()).commit()
                    fragmentManager!!.beginTransaction()
                        .replace(R.id.linerLayout, windowTask)
                        .addToBackStack(null)
                        .commit()
                }
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
        onDestroy()
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