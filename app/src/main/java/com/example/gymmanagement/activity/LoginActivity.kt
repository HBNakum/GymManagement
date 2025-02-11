package com.example.gymmanagement.activity

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.gymmanagement.R
import com.example.gymmanagement.databinding.ActivityLoginBinding
import com.example.gymmanagement.databinding.ForgetPasswordDialogBinding
import com.example.gymmanagement.global.DB
import com.example.gymmanagement.global.MyFunction
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
            showDialog()
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

    private fun showDialog(){
        val binding2 = ForgetPasswordDialogBinding.inflate(LayoutInflater.from(this))
        val dialog = Dialog(this, R.style.AlertDialogCustom)
        dialog.setContentView(binding2.root)
        dialog.setCancelable(false)
        dialog.show()

        binding2.btnForgetSubmit.setOnClickListener{

            if (binding2.edtForgetMobile.text.toString().trim().isNotEmpty()){
                checkData(binding2.edtForgetMobile.text.toString().trim(),binding2.txtYourPassword)
            }else{
                Toast.makeText(this,"Enter Mobile No.", Toast.LENGTH_LONG).show()
            }

        }

        binding2.imgBackButton.setOnClickListener{
            dialog.dismiss()
        }
    }

    private fun checkData(mobile: String, txtShowPassword: TextView){
        try {
            val sqlQuery = "SELECT * FROM ADMIN WHERE MOBILE = '$mobile'"
            db?.fireQuery(sqlQuery)?.use {
                if (it.count>0){
                    val password = MyFunction.getvalue(it,"PASSWORD")
                    txtShowPassword.visibility = View.VISIBLE
                    txtShowPassword.text = "Your Password is : $password"
                }else{
                    Toast.makeText(this,"Enter InCorrect Mobile No. ", Toast.LENGTH_LONG).show()
                    txtShowPassword.visibility = View.GONE
                }
            }
        }catch (e: Exception){
            e.printStackTrace()
        }
    }
}