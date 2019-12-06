

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


class MainActivity() : AppCompatActivity(){


    lateinit var dialog : Dialog

    private lateinit var mAuth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ButterKnife.bind(this)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Sign Up"

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

        val email = emailEdit.text.toString()
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

            checkIfEmailVerified()

            /*авторизация пользователя в системе*/
            mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {

                        val user = mAuth.currentUser
                        if (user!!.isEmailVerified) {
                            Log.d("TAG: ", "signInWithEmail: success")
                            updateUI(user)
                        }else{
                            Log.d("TAG: ", "почта не потдверждена")
                            Toast.makeText(
                                applicationContext,
                                "почта не потдверждена",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
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
    private fun checkIfEmailVerified() {

        val mAuthListener = FirebaseAuth.AuthStateListener {

            val user = mAuth.currentUser
            if (user != null) {
                Log.e(
                    "TAG ", if (user.isEmailVerified) "User is signed in and " +
                            "email is verified" else "Email is not verified"
                )
            }else{
                Log.e("TAG", "onAuthStateChanged:signed_out")
            }
        }
        //return mAuthListener
    }

    /*обновляем состояние пользователя*/
    fun updateUI(user: FirebaseUser?) {
        if (user != null) {

            val intentMainWindowCasePlanning = Intent(this, MainWindowCasePlanning::class.java)
            startActivity(intentMainWindowCasePlanning)
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
