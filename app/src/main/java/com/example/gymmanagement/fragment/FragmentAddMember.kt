package com.example.gymmanagement.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.gymmanagement.databinding.FragmentAddMemberBinding
import com.example.gymmanagement.global.DB
import com.example.gymmanagement.global.MyFunction
import java.util.Locale

class FragmentAddMember : Fragment() {

    private lateinit var binding: FragmentAddMemberBinding
    private var db: DB? = null
    private val REQUEST_CAMERA = 1234
    private val REQUEST_GALLERY = 5464
    private var membershipFees = mutableMapOf<String, Double>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentAddMemberBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = activity?.let { DB(it) }

        setupDateSelection()
        setupMembershipSelection()
        setupDiscountWatcher()
        setupImageSelection()

        getMembershipFees()
    }

    private fun setupDateSelection() {
        val cal = Calendar.getInstance()
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, month)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.US)
            binding.edtJoiningDate.setText(sdf.format(cal.time))
        }

        binding.imgPicDate.setOnClickListener {
            activity?.let {
                DatePickerDialog(it, dateSetListener, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
            }
        }
    }

    private fun setupMembershipSelection() {
        binding.spMembership.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val membershipType = binding.spMembership.selectedItem.toString().trim()
                if (membershipType == "Select") {
                    binding.edtExpire.setText("")
                    calculateTotal()
                } else {
                    if (binding.edtJoiningDate.text.toString().trim().isNotEmpty()) {
                        val months = when (membershipType) {
                            "1 Month" -> 1
                            "3 Months" -> 3
                            "6 Months" -> 6
                            "1 Year" -> 12
                            "3 Years" -> 36
                            else -> 0
                        }
                        calculateExpireDate(months)
                        calculateTotal()
                    } else {
                        showToast("Select Joining Date First")
                        binding.spMembership.setSelection(0)
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupDiscountWatcher() {
        binding.edtDiscount.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                calculateTotal()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun calculateTotal() {
        val membershipType = binding.spMembership.selectedItem.toString().trim()
        val discount = binding.edtDiscount.text.toString().toDoubleOrNull() ?: 0.0

        val fee = membershipFees[membershipType] ?: 0.0
        val discountAmount = (fee * discount) / 100
        val total = fee - discountAmount

        binding.edttAmount.setText(total.toString())
    }

    @SuppressLint("SimpleDateFormat")
    private fun calculateExpireDate(months: Int) {
        val joiningDate = binding.edtJoiningDate.text.toString().trim()
        if (joiningDate.isNotEmpty()) {
            val format = SimpleDateFormat("dd/MM/yyyy")
            val date = format.parse(joiningDate)
            val cal = Calendar.getInstance()
            cal.time = date
            cal.add(Calendar.MONTH, months)
            binding.edtExpire.setText(format.format(cal.time))
        }
    }

    private fun getMembershipFees() {
        try {
            val sqlQuery = "SELECT * FROM FEE WHERE ID = '1'"
            db?.fireQuery(sqlQuery)?.use {
                membershipFees["1 Month"] = MyFunction.getvalue(it, "ONE_MONTH").toDouble()
                membershipFees["3 Months"] = MyFunction.getvalue(it, "THREE_MONTH").toDouble()
                membershipFees["6 Months"] = MyFunction.getvalue(it, "SIX_MONTH").toDouble()
                membershipFees["1 Year"] = MyFunction.getvalue(it, "ONE_YEAR").toDouble()
                membershipFees["3 Years"] = MyFunction.getvalue(it, "THREE_YEAR").toDouble()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
    }

    private fun setupImageSelection() {
        binding.imgTakeImage.setOnClickListener { showImagePickerDialog() }
    }

    private fun showImagePickerDialog() {
        val options = arrayOf("Camera", "Gallery")
        val builder = android.app.AlertDialog.Builder(requireContext())
        builder.setTitle("Select Image From")
        builder.setItems(options) { _, which ->
            when (which) {
                0 -> {
                    // Open Camera
                    val takePicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    startActivityForResult(takePicture, REQUEST_CAMERA)
                }
                1 -> {
                    // Open Gallery
                    val pickPhoto = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    startActivityForResult(pickPhoto, REQUEST_GALLERY)
                }
            }
        }
        builder.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CAMERA -> {
                    val bitmap = data?.extras?.get("data") as Bitmap
                    binding.imgpic.setImageBitmap(bitmap)
                }
                REQUEST_GALLERY -> {
                    val selectedImageUri: Uri? = data?.data
                    if (selectedImageUri != null) {
                        binding.imgpic.setImageURI(selectedImageUri)
                    }
                }
            }
        }
    }
}
