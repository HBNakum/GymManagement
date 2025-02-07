package com.example.gymmanagement.fragment

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import com.example.gymmanagement.R
import com.example.gymmanagement.databinding.FragmentAddMemberBinding
import com.example.gymmanagement.global.DB
import com.example.gymmanagement.global.MyFunction
import java.time.Month
import java.util.Locale


class FragmentAddMember : Fragment() {

    var db: DB?= null
    var oneMonth: String?= ""
    var threeMonth: String?= ""
    var sixMonth: String?= ""
    var oneYear: String?= ""
    var threeYear: String?= ""

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

        }
        binding.spMembership.onItemSelectedListener = object  : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val value = binding.spMembership.selectedItem.toString().trim()

                if (value == "Select"){
                    binding.edtExpire.setText("")
                    calculateTotal(binding.spMembership,binding.edtDiscount,binding.edttAmount)
                }else {
                    if (binding.edtJoiningDate.text.toString().trim().isNotEmpty()){
                        if (value == "1 Month"){
                            calculateExpireDate(1,binding.edtExpire)
                            calculateTotal(binding.spMembership,binding.edtDiscount,binding.edttAmount)
                        }else if (value == "3 Months"){
                            calculateExpireDate(3,binding.edtExpire)
                            calculateTotal(binding.spMembership,binding.edtDiscount,binding.edttAmount)
                        }else if (value == "6 Months"){
                            calculateExpireDate(6,binding.edtExpire)
                            calculateTotal(binding.spMembership,binding.edtDiscount,binding.edttAmount)
                        }else if (value == "1 Year"){
                            calculateExpireDate(12,binding.edtExpire)
                            calculateTotal(binding.spMembership,binding.edtDiscount,binding.edttAmount)
                        }else if (value == "3 Years"){
                            calculateExpireDate(36,binding.edtExpire)
                            calculateTotal(binding.spMembership,binding.edtDiscount,binding.edttAmount)
                        }

                    }else{
                        showToast("Select Joining Date First")
                        binding.spMembership.setSelection(0)
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }


        binding.edtDiscount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                if (p0!=null){
                    calculateTotal(binding.spMembership,binding.edtDiscount,binding.edttAmount)
                }
            }

        })


        binding.imgPicDate.setOnClickListener{
            activity?.let { it1 -> DatePickerDialog(it1,dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)).show() }
        }

        getFee()
    }


    private fun getFee(){
        try {
            val sqlQuery = "SELECT * FROM FEE WHERE ID = '1'"
            db?.fireQuery(sqlQuery)?.use {
                oneMonth = MyFunction.getvalue(it,"ONE_MONTH")
                threeMonth = MyFunction.getvalue(it,"THREE_MONTH")
                sixMonth = MyFunction.getvalue(it,"SIX_MONTH")
                oneYear = MyFunction.getvalue(it,"ONE_YEAR")
                threeYear = MyFunction.getvalue(it,"THREE_YEAR")
            }
        }catch (e: Exception){
            e.printStackTrace()
        }
    }


    private  fun calculateTotal(spMember: Spinner,edtDis: EditText,edtAmt: EditText){
        val month = spMember.selectedItem.toString().trim()
        var discount = edtDis.text.toString().trim()
        if (edtDis.text.toString().toString().isEmpty()){
            discount = "0"
        }

        if(month == "Select"){
            edtAmt.setText("")
        }else if (month == "1 Month"){
            if (discount.trim().isEmpty()){
                discount = "0"
            }

            if (oneMonth !!.trim().isNotEmpty()){
                val discountAmount = ((oneMonth!!.toDouble() * discount.toDouble())/100) // finding out the discount amount
                val total = oneMonth!!.toDouble() - discountAmount // minus discount amount from main amount
                edtAmt.setText(total.toString())
            }

        }else if (month == "3 Months"){
            if (discount.trim().isEmpty()){
                discount = "0"
            }

            if (threeMonth !!.trim().isNotEmpty()){
                val discountAmount = ((threeMonth!!.toDouble() * discount.toDouble())/100) // finding out the discount amount
                val total = threeMonth!!.toDouble() - discountAmount // minus discount amount from main amount
                edtAmt.setText(total.toString())
            }
        }else if (month == "6 Months"){
            if (discount.trim().isEmpty()){
                discount = "0"
            }

            if (sixMonth !!.trim().isNotEmpty()){
                val discountAmount = ((sixMonth!!.toDouble() * discount.toDouble())/100) // finding out the discount amount
                val total = sixMonth!!.toDouble() - discountAmount // minus discount amount from main amount
                edtAmt.setText(total.toString())
            }

        }else if (month == "1 Year"){
            if (discount.trim().isEmpty()){
                discount = "0"
            }

            if (oneYear !!.trim().isNotEmpty()){
                val discountAmount = ((oneYear!!.toDouble() * discount.toDouble())/100) // finding out the discount amount
                val total = oneYear!!.toDouble() - discountAmount // minus discount amount from main amount
                edtAmt.setText(total.toString())
            }

        }else if (month == "3 Years"){
            if (discount.trim().isEmpty()){
                discount = "0"
            }

            if (threeYear !!.trim().isNotEmpty()){
                val discountAmount = ((threeYear!!.toDouble() * discount.toDouble())/100) // finding out the discount amount
                val total = threeYear!!.toDouble() - discountAmount // minus discount amount from main amount
                edtAmt.setText(total.toString())
            }
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