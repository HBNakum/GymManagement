package com.example.gymmanagement.fragment

import android.database.DatabaseUtils
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.gymmanagement.R
import com.example.gymmanagement.databinding.FragmentChangePasswordBinding
import com.example.gymmanagement.global.DB
import com.example.gymmanagement.global.MyFunction


class FragmentChangePassword : Fragment() {


    private lateinit var binding: FragmentChangePasswordBinding
    private var db: DB?=null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = FragmentChangePasswordBinding.inflate(inflater, container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.title = "Change Password"
        db = activity?.let { DB(it) }

        filOldMobile()

        binding.btnChangePassword.setOnClickListener{
            try {
                if (binding.edtNewPassword.text.toString().trim().isNotEmpty()){
                    if (binding.edtNewPassword.text.toString().trim()== binding.edtConfirmPassword.text.toString().trim()){
                        val sqlQuery = "UPDATE ADMIN SET PASSWORD = "+ DatabaseUtils.sqlEscapeString(
                            binding.edtNewPassword.text.toString().trim()
                        )+""
                        db?.executeQuery(sqlQuery)
                        showToast("Password Changed Successfully")
                    }else{
                        showToast("Password Not Matched")
                    }
                }else{
                    showToast("Enter New Password")
                }
            }catch (e: Exception){
                e.printStackTrace()
            }
        }

        binding.btnChangeMobile.setOnClickListener{
            try {
                if (binding.edtNewNumber.text.toString().trim().isNotEmpty()){
                    val sqlQuery = "UPDATE ADMIN SET MOBILE = '" + binding.edtNewNumber.text.toString().trim() +"'"
                    db?.executeQuery(sqlQuery)
                    showToast("Mobile Number Changed Successfully")
                }else{
                    showToast("Enter New Mobile Number")
                }

            }catch (e: Exception){
                e.printStackTrace()
            }
        }
    }

    private fun filOldMobile(){
        try {
            val sqlQuery = "SELECT MOBILE FROM ADMIN"
            db?.fireQuery(sqlQuery)?.use {
                val mobile = MyFunction.getvalue(it,"MOBILE")
                if (mobile.trim().isNotEmpty()){
                    binding.edtOldNumber.setText(mobile)
                }
            }

        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    private fun showToast(value: String){
        Toast.makeText(activity,value, Toast.LENGTH_LONG).show()
    }


}