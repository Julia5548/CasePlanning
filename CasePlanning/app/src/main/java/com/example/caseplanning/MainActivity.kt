

package com.example.caseplanning


import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import butterknife.ButterKnife
import butterknife.OnClick
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.miguelcatalan.materialsearchview.MaterialSearchView
import kotlinx.android.synthetic.main.header.*


class MainActivity : AppCompatActivity(){


    lateinit var dialog : Dialog
    var email : String = " "

    private lateinit var mAuth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ButterKnife.bind(this)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Sign In"

        toolbar.setTitleTextColor(android.graphics.Color.WHITE)

        //initialize FireBase Auth
        mAuth = FirebaseAuth.getInstance()

    }

    override fun onStart() {
        super.onStart()
        //вошел ли пользователь
        val currentUser = mAuth.currentUser
        updateUI(currentUser)

    }

    //вход в систему
    @Suppress("DEPRECATION")
    @OnClick(R.id.btn_link_signIn)
    fun onSignInClick(){


        val emailEdit : EditText = findViewById(R.id.editEmail)
        val passwordEdit : EditText = findViewById(R.id.editPassword)

        email = emailEdit.text.toString()
        val password = passwordEdit.text.toString()

        /*проверка на пустоту полей*/
        if (email.isEmpty() && password.isEmpty()){
            /*вызываем диалоговое окно*/
          //  showDialog(1)
            emailEdit.error = "Введите свой email"
            passwordEdit.error = "Введите пароль"
          /*  val btn = dialog.findViewById<Button>(R.id.btn_dialog_ok)
            btn.setOnClickListener {/*закрываем диалоговое окно*/dismissDialog(1)}*/
        }else if (email.isEmpty()){
            emailEdit.error = "Введите свой email"
            emailEdit.requestFocus()
        }else if (password.isEmpty()){
            passwordEdit.error = "Введите пароль"
            passwordEdit.requestFocus()
        }else{

            /*авторизация пользователя в системе*/
            mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {

                        val user = mAuth.currentUser
                            updateUI(user)
                    } else {
                        Log.w("TAG: ", "signInWithEmail: failure", task.exception)
                        //СДЕЛАТЬ КАК ОТДЕЛЬНОЕ ОКОШКО
                        Toast.makeText(
                            applicationContext,
                            "Authentication failed",
                            Toast.LENGTH_SHORT
                        ).show()
                        updateUI(user = null)
                    }
                }
        }

    }

    /*обновляем состояние пользователя*/
    fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            /*потверждение почты*/

            if (user.isEmailVerified) {
                val intentMainWindowCasePlanning = Intent(this,
                    MainWindowCasePlanning::class.java)
                startActivity(intentMainWindowCasePlanning)
                Toast.makeText(
                    applicationContext,
                    "$user добро пожаловать в систему по планировке дел",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }else{
            Toast.makeText(applicationContext, "регистрация не прошла", Toast.LENGTH_SHORT)
                .show()
        }
    }

 /*создаем диалоговое окно для вывода ошибки*/
    @SuppressLint("NewApi")
    override fun onCreateDialog(id: Int): Dialog {
        val buider = AlertDialog.Builder(this)
        dialog = buider.setView(R.layout.dialog_frame).create()
        return dialog
    }
    //reset password
    @OnClick(R.id.btn_resetPassword)
    fun onClickBtnResetPassword(){
        val intentResetPassword = Intent(this, ResetPassword::class.java)
        startActivity(intentResetPassword)
    }
    /*кнопка регистрации*/
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.sign_up, menu)
        return super.onCreateOptionsMenu(menu)
    }

    /*обработчик нажатия на кнопки меню*/
    override fun onOptionsItemSelected(item: MenuItem): Boolean {


        return when (item.itemId){
            R.id.signUp -> {
                val intentSignUp = Intent(this, SignUp::class.java)
                startActivity(intentSignUp)
                true
            }

            else ->
                super.onOptionsItemSelected(item)
        }
    }


}
