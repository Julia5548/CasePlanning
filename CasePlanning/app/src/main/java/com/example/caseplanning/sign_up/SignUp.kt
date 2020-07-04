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
            listUsers(nameUserEdit, email, password, progressButton)
        }
    }


    private fun listUsers(
        nameUserEdit: EditText,
        email: String,
        password: String,
        progressButton: ProgressButton
    ) {

        val dataBaseTask = DataBase()
        var mDisposable: Disposable? = null
        mDisposable = dataBaseTask
            .retrieveDataUser()
            .subscribe { user_data ->
                if (!user_data.containsValue(mName_user)) {
                    signUpUser(user_data, email, password, progressButton, mDisposable)
                } else {
                    errorProgress(progressButton, 800)
                    nameUserEdit.error = "Такой ник уже существует, введите другой"
                    nameUserEdit.requestFocus()
                }
            }
    }

    private fun signUpUser(
        users: HashMap<String, String>,
        email: String,
        password: String,
        progressButton: ProgressButton,
        mDisposable: Disposable?
    ) {

        if (mDisposable != null && !mDisposable.isDisposed)
            mDisposable.dispose()

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
                            users[user.uid] = mName_user
                            dataBase.createUser(users)
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
    }
}
