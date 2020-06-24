package com.example.caseplanning.Setting

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import butterknife.ButterKnife
import butterknife.OnClick
import com.example.caseplanning.DataBase.DataBase
import com.example.caseplanning.DataBase.Users
import com.example.caseplanning.GroupTask.GroupTask
import com.example.caseplanning.MainActivity
import com.example.caseplanning.R
import com.example.caseplanning.Sidebar.Access
import com.example.caseplanning.Sidebar.Progress
import com.example.caseplanning.Sidebar.TechSupport
import com.example.caseplanning.mainWindow.WindowTask
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.navigation.NavigationView
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.*

class Account(
    name: String,
    email: String,
    accessUsers: ArrayList<String>
) : Fragment(),
    NavigationView.OnNavigationItemSelectedListener {

    var mDrawerLayout: DrawerLayout? = null
    var mName: String? = name
    var mEmail: String?= email
    var mail : TextView? = null
    var nickname : TextView? = null
    var mAccessUsers : ArrayList<String>? = accessUsers

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.account_setting, container, false)

        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)
        val activity = activity as AppCompatActivity?
        activity!!.setSupportActionBar(toolbar)
        val actionBar = activity.supportActionBar
        actionBar!!.title = "Личная информация"

        ButterKnife.bind(this, view)

        /*боковое меню*/
        mDrawerLayout = view.findViewById<DrawerLayout>(R.id.drawerLayout)
        /*подключение обработчика события кнопок бокового меню*/
        val navigationView = view.findViewById<NavigationView>(R.id.navigationView)
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

        personalInformation(view)
        return view
    }

    private fun personalInformation(view: View) {

        nickname = view.findViewById<TextView>(R.id.nick_text)
        mail = view.findViewById<TextView>(R.id.mail_text)

        nickname!!.text = mName
        mail!!.text = mEmail

        val card_nick = view.findViewById<CardView>(R.id.nick_card)
        val card_mail = view.findViewById<CardView>(R.id.mail_card)

        card_nick.setOnClickListener {
            updateNickname()
        }
        card_mail.setOnClickListener {
            updateMail()
        }
    }

    private fun updateNickname() {
        val view = LayoutInflater.from(context).inflate(R.layout.update_information, null)
        val new_information = view.findViewById<TextInputEditText>(R.id.update_information)
        new_information.setText(mName!!)
        new_information.requestFocus(mName!!.length)
        new_information.hint = "Никнейм"
        MaterialAlertDialogBuilder(context)
            .setTitle("Изменить никнейм")
            .setMessage("Введите новый никнейм")
            .setView(view)
            .setPositiveButton("Сменить") { dialog, which ->
                val user = FirebaseAuth.getInstance().currentUser!!
                val updateProfile = UserProfileChangeRequest.Builder()
                    .setDisplayName(new_information.text.toString()).build()
                user.updateProfile(updateProfile)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            mName = new_information.text.toString()
                            nickname!!.text = mName
                            val dataBase = DataBase()
                            dataBase.updateDataUser(Users(name = mName!!, email = mEmail!!, accessUsers = mAccessUsers!!), user.uid)
                            Toast.makeText(
                                context,
                                "Никнейм успешно изменен",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    .addOnFailureListener {
                        Toast.makeText(
                            context,
                            "Ошибка! Никнейм не изменен",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                dialog.dismiss()
            }
            .setNegativeButton("Отмена") { dialog, which ->
                dialog.dismiss()
            }
            .show()
    }

    private fun updateMail() {

        val view = LayoutInflater.from(context).inflate(R.layout.update_information, null)
        val new_information = view.findViewById<TextInputEditText>(R.id.update_information)
        new_information.setText(mEmail!!)
        new_information.requestFocus(mEmail!!.length)
        MaterialAlertDialogBuilder(context)
            .setTitle("Изменить электронную почту")
            .setMessage("Введите новый элекронный адрес")
            .setView(view)
            .setPositiveButton("Изменить") { dialog, which ->
                val user = FirebaseAuth.getInstance().currentUser!!
                user.updateEmail(new_information.text.toString())
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            user.sendEmailVerification()
                                .addOnCompleteListener {
                                    if(it.isSuccessful){
                                        mEmail = new_information.text.toString()
                                        mail!!.text = mEmail
                                        Toast.makeText(
                                            context,
                                            "Эл.почта успешно изменена. Письмо для подтверждения отправлено ",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                                .addOnFailureListener {
                                    Toast.makeText(
                                        context,
                                        "Ошибка! Письмо для подтверждения не отправлено",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                        }
                    }
                    .addOnFailureListener {
                        Toast.makeText(
                            context,
                            "Ошибка! Эл.почта не изменена",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                dialog.dismiss()
            }
            .setNegativeButton("Отмена") { dialog, which ->
                dialog.dismiss()
            }
            .show()
    }

    @OnClick(R.id.new_password)
    fun newPassword() {
        val view = LayoutInflater.from(context).inflate(R.layout.new_password_account, null)
        val new_password = view.findViewById<TextInputEditText>(R.id.new_password)
        MaterialAlertDialogBuilder(context)
            .setTitle("Смена пароля")
            .setMessage("Введите новый пароль")
            .setView(view)
            .setPositiveButton("Сменить") { dialog, which ->
                val user = FirebaseAuth.getInstance().currentUser!!
                user.updatePassword(new_password.text.toString())
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            Toast.makeText(
                                context,
                                "Пароль успешно изменен",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    .addOnFailureListener {
                        Toast.makeText(
                            context,
                            "Ошибка! Пароль не изменен",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                dialog.dismiss()
            }
            .setNegativeButton("Отмена") { dialog, which ->
                dialog.dismiss()
            }
            .show()
    }

    @OnClick(R.id.deleted_account)
    fun deleted_account() {
        MaterialAlertDialogBuilder(context)
            .setTitle("Удалить аккаунт?")
            .setMessage("Вы действительно хотите удалить аккаунт?\nВосстановить информацию будет невозможно")
            .setPositiveButton("Да") { dialog, which ->
                val user = FirebaseAuth.getInstance().currentUser!!
                credentialUser(user)
                dialog.dismiss()
            }
            .setNegativeButton("Отмена") { dialog, which ->
                dialog.dismiss()
            }
            .show()
    }

    private fun credentialUser(user: FirebaseUser) {
        val view = LayoutInflater.from(context).inflate(R.layout.credential_user, null)
        val credential_password = view.findViewById<TextInputEditText>(R.id.credential_password)
        val credential_email = view.findViewById<TextInputEditText>(R.id.credential_email)

        MaterialAlertDialogBuilder(context)
            .setTitle("Подтвердите подлинность")
            .setMessage("Введите свой логин и пароль")
            .setView(view)
            .setPositiveButton("Ок") { dialog, which ->
                val email = credential_email.text.toString()
                val password = credential_password.text.toString()
                val credential = EmailAuthProvider.getCredential(email, password)

                user.reauthenticate(credential)
                    .addOnCompleteListener {
                        user.delete()
                            .addOnCompleteListener {
                                val dataBase = DataBase()
                                dataBase.deletedUser(user.uid)
                                if (it.isSuccessful) {
                                    Toast.makeText(
                                        context,
                                        "Ваш аккаунт успешно удален",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    val intent = Intent(context, MainActivity::class.java)
                                    startActivity(intent)
                                }
                            }
                            .addOnFailureListener {
                                Toast.makeText(
                                    context,
                                    "${it.stackTrace}",
                                    Toast.LENGTH_SHORT
                                ).show()

                            }
                    }.addOnFailureListener {
                        Toast.makeText(
                            context,
                            "Проверка подлинности пользователя не удачна. Неверн логин или пароль",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                dialog.dismiss()
            }.setNegativeButton("Отмена")
            { dialog, which ->
                dialog.dismiss()
            }
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
    override fun onDestroy() {
        super.onDestroy()
        mDrawerLayout!!.removeAllViews()
        mAccessUsers = null
        nickname = null
        mail = null
        mEmail = null
        mName = null
    }
}