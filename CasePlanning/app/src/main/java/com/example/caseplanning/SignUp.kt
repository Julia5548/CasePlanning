package com.example.caseplanning

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.util.Log
import android.view.MenuItem


class SignUp : AppCompatActivity() {



    private lateinit var mAuth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        val toolbar = findViewById<Toolbar>(R.id.toolbarSignUp)
        setSupportActionBar(toolbar)
        toolbar!!.title = "Sign Up"

        ButterKnife.bind(this)
        //initialize FireBase Auth
        mAuth = FirebaseAuth.getInstance()

        //кнопка назад
        val actionBar = supportActionBar
        actionBar!!.setDisplayHomeAsUpEnabled(true)
        actionBar.setHomeButtonEnabled(true)

        toolbar.setTitleTextColor(android.graphics.Color.WHITE)
    }

    @OnClick(R.id.btn_link_signUp)
    fun onClickSignUp(){

        val emailEdit : EditText = findViewById(R.id.editEmail)
        val passwordEdit : EditText = findViewById(R.id.editPassword)

        val email = emailEdit.text.toString()
        val password = passwordEdit.text.toString()

        mAuth.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener(this
            ) { task ->
                if (task.isSuccessful){


                    val user = mAuth.currentUser
                    user?.sendEmailVerification()?.addOnCompleteListener(this)
                    { taskTwo ->
                        if (taskTwo.isSuccessful) {
                            Toast.makeText(applicationContext, "Signup successful. Verification email sent",
                                Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(
                                applicationContext,
                                taskTwo.exception!!.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    updateId(user)
                }else{
                    Log.d("TAG: ","createUserWithEmail:failure", task.exception)
                    Toast.makeText(applicationContext, "Authentication failed", Toast.LENGTH_SHORT).show()
                    updateId(user = null)
                }
            }


    }

    fun updateId(user : FirebaseUser?){


        if(user != null) {
            /*потверждение почты*/

            if (user.isEmailVerified) {
                val intent = Intent(this, MainWindowCasePlanning::class.java)
                startActivity(intent)
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
    /*возврат на предыдущую страницу*/
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            android.R.id.home -> {
                this.finish()
                return true
            }
            else ->
                return super.onOptionsItemSelected(item)
        }
    }
}
