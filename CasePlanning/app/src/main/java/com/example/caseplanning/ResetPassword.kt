package com.example.caseplanning

import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import butterknife.ButterKnife
import butterknife.OnClick
import com.google.firebase.auth.FirebaseAuth

class ResetPassword : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        ButterKnife.bind(this)

        val toolbar = findViewById<Toolbar>(R.id.toolbarResetPassword)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Reset Password"
        val actionBar = supportActionBar
        actionBar!!.setDisplayHomeAsUpEnabled(true)
        actionBar.setHomeButtonEnabled(true)


        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LOCKED

        //initialize FireBase Auth
        mAuth = FirebaseAuth.getInstance()

    }

    @OnClick(R.id.btn_link_resetPassword)
    fun onClickResetPassword() {

        val emailResetPassword = findViewById<EditText>(R.id.editResetPassowrdEmail)
        val email = emailResetPassword.text.toString()

        if (email.isEmpty()) {
            emailResetPassword.error = "Введите свой email"
            emailResetPassword.requestFocus()
        } else {
            //отправка сосбщения на почту для сбрроса пароля
            mAuth!!.sendPasswordResetEmail(email)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            applicationContext, "Сообщение для сброса пароля " +
                                    "отправлено на почту", Toast.LENGTH_LONG
                        ).show()
                    } else {
                        Log.d("Error", task.exception.toString())
                        Toast.makeText(applicationContext, "${task.exception}", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
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
