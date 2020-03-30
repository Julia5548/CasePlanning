package com.example.caseplanning.Sidebar

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import butterknife.ButterKnife
import com.example.caseplanning.DataBase.DataBaseTask
import com.example.caseplanning.R
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.miguelcatalan.materialsearchview.MaterialSearchView

class Access: Fragment(), NavigationView.OnNavigationItemSelectedListener{


    private lateinit var mAuth: FirebaseAuth
    private lateinit var search: MaterialSearchView


    private lateinit var mDrawerLayout : DrawerLayout
    private lateinit var  mToggle : ActionBarDrawerToggle

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val viewFragment = inflater.inflate(R.layout.provision_access, container, false)

        val toolbar = viewFragment.findViewById<Toolbar>(R.id.toolbar)
        val activity = activity as AppCompatActivity?
        activity!!.setSupportActionBar(toolbar)
        val actionBar: ActionBar? = activity.supportActionBar
        actionBar!!.title = "Предоставление доступа"

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

        listTask(viewFragment)
        return viewFragment
    }


    private fun listTask(viewFragment: View) {

        val listTasks = viewFragment.findViewById<ListView>(R.id.listViewUser)

        val stringList = mutableListOf<Model>()
        stringList.add(Model("Алексей"))
        stringList.add(Model("Юлия" ))
        stringList.add(Model("Ольга"))
        stringList.add(Model("Сергей" ))
        stringList.add(Model("Евгений" ))

        listTasks.adapter = MyListAdapter(context!!,R.layout.provision_access_button,stringList)


    }

    override fun onNavigationItemSelected(p0: MenuItem): Boolean {
        return true
        }

    class MyListAdapter(var mCtx:Context , var resource:Int,var items:List<Model>)
        :ArrayAdapter<Model>( mCtx , resource , items ){

            private val layout = resource
        override fun getView( position: Int, convertView: View?, parent: ViewGroup): View {
            var mConvertView = convertView
            var  holder: ViewHolder = ViewHolder()
            var mViewHolder : ViewHolder? = null
            if (convertView == null){
                val inflater = LayoutInflater.from(context)
                mConvertView = inflater.inflate(layout, parent, false)
                holder.access = mConvertView.findViewById(R.id.btnListView)
                holder.title = mConvertView.findViewById(R.id.textForList)
                val users : Model = items[position]
                holder.title.text = users.title
                holder.access.setOnClickListener {
                  Toast.makeText(context, "Доступ разрешен", Toast.LENGTH_SHORT).show()
                }
                mConvertView.tag = holder
            }else{
                mViewHolder = mConvertView!!.tag as ViewHolder
                mViewHolder.access

            }
            return mConvertView!!
        }
    }
    class ViewHolder{
        lateinit var access: Button
        lateinit var title : TextView
    }
}