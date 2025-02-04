package com.example.gymmanagement.activity

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.gymmanagement.R
import com.example.gymmanagement.databinding.ActivityLoginBinding
import com.example.gymmanagement.global.DB
import com.example.gymmanagement.manager.SessionManager

class LoginActivity : AppCompatActivity() {

    var db:DB? = null
    var session: SessionManager?=null
    var edtUserName : EditText?=null
    var edtPassword : EditText?=null

    lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        db = DB(this)
        session = SessionManager(this)
        edtUserName = binding.edUserName
        edtPassword = binding.edPassword

        binding.btnLogin.setOnClickListener{
            if (validateLogin()){
                getLogin()
            }

        }

        binding.txtForgotPassword.setOnClickListener{

        }
    }

    private fun getLogin(){
        try {
            val sqlQuery = "SELECT * FROM ADMIN WHERE USER_NAME = '"+edtUserName?.text.toString().trim()+"' "+"AND PASSWORD = '"+edtPassword?.text.toString().trim()+"' AND ID='1'"
            db?.fireQuery(sqlQuery)?.use {
                if (it.count>0){
                    session?.setLogin(true)
                    Toast.makeText(this,"Successfully Log In",Toast.LENGTH_LONG).show()
                    val intent = Intent( this, HomeActivity::class.java)
                    startActivity(intent)
                    finish()
                }else{
                    session?.setLogin(false)
                    Toast.makeText(this,"Log In Failed", Toast.LENGTH_LONG).show()
                }
            }
        }catch (e: Exception){
            e.printStackTrace()
        }

    }

    private fun validateLogin(): Boolean{
        if (edtUserName?.text.toString().trim().isEmpty()){
            Toast.makeText(this,"Enter User Name", Toast.LENGTH_LONG).show()

        }else if (edtPassword?.text.toString().trim().isEmpty()){
            Toast.makeText(this, "EnterPassword", Toast.LENGTH_LONG).show()
            return false
        }
        return true
    }
}