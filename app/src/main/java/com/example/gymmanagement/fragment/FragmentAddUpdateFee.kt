package com.example.gymmanagement.fragment

import android.R.attr.value
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.gymmanagement.R
import com.example.gymmanagement.databinding.FragmentAddUpdateFeeBinding
import com.example.gymmanagement.global.DB
import com.example.gymmanagement.global.MyFunction


class FragmentAddUpdateFee : Fragment() {

    private lateinit var binding: FragmentAddUpdateFeeBinding
    var db: DB?=null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = FragmentAddUpdateFeeBinding.inflate(inflater,container,false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = activity?.let { DB(it) }

        binding.btnAddMemberShip.setOnClickListener{
            if (validate()){
                saveData()
            }
        }
        fillData()
    }

    private fun  validate(): Boolean{
        if (binding.edtOneMonth.text.toString().trim().isEmpty()){
            showToast("Enter one month fee")
            return false
        }else if (binding.edtThreeMonth.text.toString().trim().isEmpty()){
            showToast("Enter three month fee")
            return false
        }else if (binding.edtSixMonth.text.toString().trim().isEmpty()){
            showToast("Enter six month fee")
            return false
        }else if (binding.edtOneYear.text.toString().trim().isEmpty()){
            showToast("Enter one year fee")
            return false
        }else if (binding.edtThreeYear.text.toString().trim().isEmpty()) {
            showToast("Enter three year fee")
            return false
        }
        return true
    }

    private fun saveData(){
        try {
            val sqlQuery = "INSERT OR REPLACE INTO FEE(ID,ONE_MONTH,THREE_MONTH,SIX_MONTH,ONE_YEAR,THREE_YEAR)VALUES"+
                    "('1','"+binding.edtOneMonth.text.toString().trim()+"','"+binding.edtThreeMonth.text.toString().trim()+"',"+
                    "'"+binding.edtSixMonth.text.toString().trim()+"','"+binding.edtThreeMonth.text.toString().trim()+"',"+
                    "'"+binding.edtThreeYear.text.toString().trim()+"')"

            db?.executeQuery(sqlQuery)
            showToast("Membership data saved successfully")

        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    private fun fillData(){
        try {

            val sqlQuery = "SELECT *FROM FEE WHERE ID = '1' "
            db?.fireQuery(sqlQuery)?.use {
                if (it.count>0) {
                    it.moveToFirst()
                    binding.edtOneMonth.setText(MyFunction.getvalue(it, "ONE_MONTH"))
                    binding.edtThreeMonth.setText(MyFunction.getvalue(it, "THREE_MONTH"))
                    binding.edtSixMonth.setText(MyFunction.getvalue(it, "SIX_MONTH"))
                    binding.edtOneYear.setText(MyFunction.getvalue(it, "ONE_YEAR"))
                    binding.edtThreeYear.setText(MyFunction.getvalue(it, "THREE_YEAR"))
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