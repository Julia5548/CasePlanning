package com.example.caseplanning.sign_up

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import butterknife.ButterKnife
import com.example.caseplanning.DataBase.DataBase
import com.example.caseplanning.MainActivity
import com.example.caseplanning.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import io.reactivex.disposables.Disposable

class SignUp : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null
    private var mName_user: String = ""
    private var list_users: ArrayList<String>? = arrayListOf()
    private var list_uid: ArrayList<String>? = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        val toolbar = findViewById<Toolbar>(R.id.toolbarSignUp)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Регистрация"

        //кнопка назад
        val actionBar = supportActionBar
        actionBar!!.setDisplayHomeAsUpEnabled(true)
        actionBar.setHomeButtonEnabled(true)

        ButterKnife.bind(this)
        //initialize FireBase Auth
        mAuth = FirebaseAuth.getInstance()

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LOCKED

        val view = findViewById<View>(R.id.view)
        view.setOnClickListener { view_sign ->
            onClickSignUp(view_sign)
        }
    }

    fun errorProgress(progressButton: ProgressButton, delayMills: Long) {
        val handler = Handler()
        handler.postDelayed({
            progressButton.buttonFailed()
            val handlerFinished = Handler()
            handlerFinished.postDelayed({
                progressButton.recoveryState()
            }, 2000)
        }, delayMills)
    }

    fun successfulProgress(
        progressButton: ProgressButton,
        delayMills: Long
    ) {
        val handler = Handler()
        handler.postDelayed({
            progressButton.buttonFinished()
            val handlerFinished = Handler()
            handlerFinished.postDelayed({
                progressButton.recoveryState()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }, 2000)
        }, delayMills)
    }

    fun onClickSignUp(view: View) {

        val progressButton = ProgressButton(applicationContext, view)
        progressButton.buttonActivated()

        val emailEdit: EditText = findViewById(R.id.editEmail)
        val passwordEdit: EditText = findViewById(R.id.editPassword)
        val nameUserEdit: EditText = findViewById(R.id.editUserName)

        val email = emailEdit.text.toString()
        val password = passwordEdit.text.toString()
        val name = nameUserEdit.text.toString()

        /*проверка на пустоту полей*/
        if (email.isEmpty() && password.isEmpty() && name.isEmpty()) {
            emailEdit.error = "Введите свой email"
            passwordEdit.error = "Введите пароль"
            nameUserEdit.error = "Введите свой ник"
            errorProgress(progressButton, 0)
        } else if (email.isEmpty()) {
            emailEdit.error = "Введите свой email"
            emailEdit.requestFocus()
            errorProgress(progressButton, 0)
        } else if (password.isEmpty()) {
            passwordEdit.error = "Введите пароль"
            passwordEdit.requestFocus()
            errorProgress(progressButton, 0)
        } else if (name.isEmpty()) {
            nameUserEdit.error = "Введите свой ник"
            nameUserEdit.requestFocus()
            errorProgress(progressButton, 0)
        } else {
            mName_user = name
            createListUid(nameUserEdit, email, password, progressButton)
        }
    }

    private fun getListUid(): ArrayList<String>? = list_uid

    private fun getUser(): ArrayList<String>? = list_users

    private fun createListUid(
        nameUserEdit: EditText,
        email: String,
        password: String,
        progressButton: ProgressButton
    ) {
        val dataBaseTask = DataBase()
        var disposable : Disposable? = null
        disposable = dataBaseTask
            .retrieveDataUid()
            .subscribe { uids ->
                if (list_uid != null) {
                    if (list_uid!!.size > 0) {
                        list_uid = arrayListOf()
                    }
                    for (uid_user in uids)
                        list_uid!!.add(uid_user.id!!)
                    listUsers(nameUserEdit, email, password, progressButton, disposable)
                }
            }
    }

    private fun listUsers(
        nameUserEdit: EditText,
        email: String,
        password: String,
        progressButton: ProgressButton,
        disposable: Disposable?
    ) {
        if (disposable != null && !disposable.isDisposed)
            disposable.dispose()

        val dataBaseTask = DataBase()
        var mDisposable : Disposable? = null
        val uids = getListUid()
        if (uids != null) {
            for (uid in uids) {
                 mDisposable = dataBaseTask
                    .retrieveDataUser(uid)
                    .subscribe { user_data ->
                        list_users!!.add(user_data.name!!)
                        if (list_users!!.size == uids.size)
                            signUpUser(nameUserEdit, email, password, progressButton, mDisposable)
                    }
            }
        }
    }

    private fun signUpUser(
        nameUserEdit: EditText,
        email: String,
        password: String,
        progressButton: ProgressButton,
        mDisposable: Disposable?
    ) {

        if (mDisposable != null && !mDisposable.isDisposed)
            mDisposable.dispose()

        progressButton.buttonActivated()

        val users = getUser()
        if (users!!.size == list_uid!!.size) {
            if (users.contains(mName_user)) {
                nameUserEdit.error = "Такой ник уже существует, введите другой"
                nameUserEdit.requestFocus()
                errorProgress(progressButton, 800)
            } else {
                mAuth!!.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(
                        this
                    ) { created_user ->
                        if (created_user.isSuccessful) {
                            val user = mAuth!!.currentUser
                            val displayName = UserProfileChangeRequest.Builder()
                                .setDisplayName(mName_user).build()
                            user?.updateProfile(displayName)
                            user?.sendEmailVerification()?.addOnCompleteListener(this)
                            { sented_email ->
                                if (sented_email.isSuccessful) {
                                    Toast.makeText(
                                        applicationContext,
                                        "Регистрация прошла успешно. Потдвердите свою почту!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    val dataBase = DataBase()
                                    dataBase.createUser(mName_user, email)
                                    successfulProgress(progressButton, 2500)
                                } else {
                                    errorProgress(progressButton, 2500)
                                    Toast.makeText(
                                        applicationContext,
                                        sented_email.exception!!.message,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        } else {
                            errorProgress(progressButton, 2500)
                            Log.d("TAG: ", "Регистрация не прошла", created_user.exception)
                            Toast.makeText(
                                applicationContext,
                                "Регистрация не прошла",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            }
        }
    }

    /*возврат на предыдущую страницу*/
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                this.finish()
                true
            }
            else ->
                super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mAuth = null
        list_uid = null
        list_users = null
    }
}
