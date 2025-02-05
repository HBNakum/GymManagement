package com.example.gymmanagement.fragment

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.EditText
import android.widget.Toast
import com.example.gymmanagement.R
import com.example.gymmanagement.databinding.FragmentAddMemberBinding
import com.example.gymmanagement.global.DB
import java.time.Month
import java.util.Locale


class FragmentAddMember : Fragment() {

    var db: DB?= null

    private  lateinit var binding: FragmentAddMemberBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = FragmentAddMemberBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = activity?.let { DB(it) }

        val  cal = Calendar.getInstance()
        val dateSetListener = DatePickerDialog.OnDateSetListener{view1, year, monthOfYear, dayOfMonth ->

            cal.set(Calendar.YEAR,year)
            cal.set(Calendar.MONTH,monthOfYear)
            cal.set(Calendar.DAY_OF_MONTH,dayOfMonth)

            val myFormat = "dd/MM/yyyy"
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            binding.edtJoiningDate.setText(sdf.format(cal.time))

            binding.spMembership.onItemSelectedListener = object  : AdapterView.OnItemSelectedListener{
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    val value = binding.spMembership.selectedItem.toString().trim()

                    if (value == "Select"){
                        binding.edtExpire.setText("")
                    }else {
                        if (binding.edtJoiningDate.text.toString().trim().isNotEmpty()){
                            if (value == "1 Month"){
                                calculateExpireDate(1,binding.edtExpire)
                            }else if (value == "3 Months"){
                                calculateExpireDate(3,binding.edtExpire)
                            }else if (value == "6 Months"){
                                calculateExpireDate(6,binding.edtExpire)
                            }else if (value == "1 Year"){
                                calculateExpireDate(12,binding.edtExpire)
                            }else if (value == "3 Years"){
                                calculateExpireDate(36,binding.edtExpire)
                            }

                        }else{
                            showToast("Select Joining Date First")
                        }
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

            }
        }

        binding.imgPicDate.setOnClickListener{
            activity?.let { it1 -> DatePickerDialog(it1,dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)).show() }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private  fun calculateExpireDate(month: Int, edtExpiry: EditText){
        val dtStart = binding.edtJoiningDate.text.toString().trim()
        if (dtStart.isNotEmpty()){
            val format = SimpleDateFormat("dd/MM/yyyy")
            val date1 = format.parse(dtStart)
            val cal = java.util.Calendar.getInstance()
            cal.time = date1
            cal.add(Calendar.MONTH,month)

            val myFormat = "dd/MM/yyyy"
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            edtExpiry.setText(sdf.format(cal.time))

        }
    }


    private fun showToast(value: String){
        Toast.makeText(activity,value, Toast.LENGTH_LONG).show()
    }


}