package com.example.caseplanning.Setting

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import butterknife.ButterKnife
import butterknife.OnClick
import com.example.caseplanning.DataBase.DataBase
import com.example.caseplanning.MainActivity
import com.example.caseplanning.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.*
import io.reactivex.disposables.Disposable

class Account(
    name: String,
    email: String
) : Fragment() {

    var mName: String? = name
    var mEmail: String? = email
    var mail: TextView? = null
    var nickname: TextView? = null

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
        personalInformation(view)
        return view
    }

    private fun personalInformation(view: View) {

        nickname = view.findViewById(R.id.nick_text)
        mail = view.findViewById(R.id.mail_text)

        nickname!!.text = mName
        mail!!.text = mEmail

        val card_nick = view.findViewById<CardView>(R.id.nick_card)
        val card_mail = view.findViewById<CardView>(R.id.mail_card)

        card_nick.setOnClickListener {
            getListUsers("update")
        }
        card_mail.setOnClickListener {
            updateMail()
        }
    }

    private fun getListUsers(tagger : String) {
        val dataBase = DataBase()
        var disposable: Disposable? = null
        disposable = dataBase
            .readUser()
            .subscribe {
                if (tagger == "update") {
                    updateNickname(it, disposable)
                }else{
                    updateAccessUsers(it, "deleted")
                }
            }

    }

    private fun updateNickname(
        users: HashMap<String, String>,
        disposable: Disposable?
    ) {
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

                            for ((key, value) in users)
                                if (key == user.uid) {
                                    users[key] = mName!!
                                }

                            val dataBase = DataBase()
                            dataBase.updateDataUser(users)
                            if (disposable != null && !disposable.isDisposed)
                                disposable.dispose()
                            updateAccessUsers(users, "update")

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

    private fun updateAccessUsers(
        users: HashMap<String, String>,
        tagger: String
    ) {
        val database = DataBase()
        val currentUid = FirebaseAuth.getInstance().currentUser!!.uid
        var disposabl: Disposable? = null
        for ((uid, _) in users) {
            disposabl = database
                .retrieveAccess(uid)
                .subscribe {
                    if (uid != currentUid) {
                        if (tagger == "update") {
                            if (it.containsKey(currentUid))
                                it[currentUid] = mName!!
                            database.updateAccessUsers(it, uid)
                        } else {
                            if (it.containsKey(currentUid))
                                it.remove(currentUid)
                            database.updateAccessUsers(it, uid)
                        }
                    } else {
                        if (tagger == "deleted") {
                            users.remove(currentUid)
                            database.updateDataUser(users)
                        }
                    }
                    if (disposabl != null && !disposabl!!.isDisposed)
                        disposabl!!.dispose()
                }
        }

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
                                    if (it.isSuccessful) {
                                        mEmail = new_information.text.toString()
                                        mail!!.text = mEmail
                                        Toast.makeText(
                                            context,
                                            "Эл.почта успешно изменена. Письмо для подтверждения отправлено ",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                    val intent = Intent(context, MainActivity::class.java)
                                    startActivity(intent)
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
                        val intent = Intent(context, MainActivity::class.java)
                        startActivity(intent)
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
                                getListUsers("deleted")
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

    override fun onDestroy() {
        super.onDestroy()
        nickname = null
        mail = null
        mEmail = null
        mName = null
    }
}