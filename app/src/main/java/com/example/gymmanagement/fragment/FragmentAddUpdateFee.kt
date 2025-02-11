package com.example.gymmanagement.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.gymmanagement.databinding.FragmentAddUpdateFeeBinding
import com.example.gymmanagement.global.DB
import com.example.gymmanagement.global.MyFunction

class FragmentAddUpdateFee : Fragment() {

    private lateinit var binding: FragmentAddUpdateFeeBinding
    private var db: DB? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentAddUpdateFeeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.title = "Fee"
        db = activity?.let { DB(it) }

        binding.btnAddMemberShip.setOnClickListener {
            if (validate()) {
                saveData()
            }
        }
        fillData()
    }

    private fun validate(): Boolean {
        return when {
            binding.edtOneMonth.text.toString().trim().isEmpty() -> {
                showToast("Enter one month fee")
                false
            }
            binding.edtThreeMonth.text.toString().trim().isEmpty() -> {
                showToast("Enter three month fee")
                false
            }
            binding.edtSixMonth.text.toString().trim().isEmpty() -> {
                showToast("Enter six month fee")
                false
            }
            binding.edtOneYear.text.toString().trim().isEmpty() -> {
                showToast("Enter one year fee")
                false
            }
            binding.edtThreeYear.text.toString().trim().isEmpty() -> {
                showToast("Enter three year fee")
                false
            }
            else -> true
        }
    }

    private fun saveData() {
        try {
            val checkQuery = "SELECT COUNT(*) FROM FEE WHERE ID = '1'"
            val cursor = db?.fireQuery(checkQuery)

            var dataExists = false
            cursor?.use {
                if (it.moveToFirst()) {
                    dataExists = it.getInt(0) > 0
                }
            }

            val sqlQuery = if (dataExists) {
                """UPDATE FEE SET 
                    ONE_MONTH = '${binding.edtOneMonth.text.toString().trim()}', 
                    THREE_MONTH = '${binding.edtThreeMonth.text.toString().trim()}', 
                    SIX_MONTH = '${binding.edtSixMonth.text.toString().trim()}', 
                    ONE_YEAR = '${binding.edtOneYear.text.toString().trim()}', 
                    THREE_YEAR = '${binding.edtThreeYear.text.toString().trim()}' 
                    WHERE ID = '1'""".trimIndent()
            } else {
                """INSERT INTO FEE (ID, ONE_MONTH, THREE_MONTH, SIX_MONTH, ONE_YEAR, THREE_YEAR) VALUES 
                    ('1', '${binding.edtOneMonth.text.toString().trim()}', 
                    '${binding.edtThreeMonth.text.toString().trim()}', 
                    '${binding.edtSixMonth.text.toString().trim()}', 
                    '${binding.edtOneYear.text.toString().trim()}', 
                    '${binding.edtThreeYear.text.toString().trim()}')""".trimIndent()
            }

            db?.executeQuery(sqlQuery)
            showToast("Membership data saved successfully")
        } catch (e: Exception) {
            e.printStackTrace()
            showToast("Error saving data: ${e.message}")
        }
    }

    private fun fillData() {
        try {
            val sqlQuery = "SELECT * FROM FEE WHERE ID = '1'"
            db?.fireQuery(sqlQuery)?.use {
                if (it.count > 0) {
                    it.moveToFirst()
                    binding.edtOneMonth.setText(MyFunction.getvalue(it, "ONE_MONTH"))
                    binding.edtThreeMonth.setText(MyFunction.getvalue(it, "THREE_MONTH"))
                    binding.edtSixMonth.setText(MyFunction.getvalue(it, "SIX_MONTH"))
                    binding.edtOneYear.setText(MyFunction.getvalue(it, "ONE_YEAR"))
                    binding.edtThreeYear.setText(MyFunction.getvalue(it, "THREE_YEAR"))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    private fun showToast(value: String) {
        Toast.makeText(activity, value, Toast.LENGTH_LONG).show()
    }
}
