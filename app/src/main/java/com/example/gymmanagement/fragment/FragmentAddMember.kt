package com.example.gymmanagement.fragment

import android.Manifest
import android.R.attr.path
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.database.DatabaseUtils
import android.graphics.Bitmap
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.gymmanagement.R
import com.example.gymmanagement.databinding.FragmentAddMemberBinding
import com.example.gymmanagement.global.CaptureImage
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
    private var captureImage: CaptureImage?=null
    private var gender = "Male"
    private var ID = ""

    companion object {
        private const val REQUEST_CAMERA = 100
        private const val REQUEST_GALLERY = 101
        private const val CAMERA_PERMISSION_CODE = 102
        private const val STORAGE_PERMISSION_CODE = 103
    }

    private var actualImagePath: String? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = FragmentAddMemberBinding.inflate(inflater,container,false)
        return binding.root
    }

    @SuppressLint("UseRequireInsteadOfGet")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = activity?.let { DB(it) }
        captureImage = CaptureImage(activity)


       //////////////////////////////////////...........................
//        ID = this.arguments!!.getString("ID").toString()
        ID = arguments?.getString("ID").orEmpty()


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

        binding.radioGroup.setOnCheckedChangeListener{radioGroup, id->
            when(id){
                R.id.rdMale ->{
                    gender = "Male"
                }
                R.id.rdFemale ->{
                    gender = "Female"
                }
            }
        }

        binding.btnAddMemberSave.setOnClickListener{
            if (validate()){
                saveData()
            }
        }


        binding.imgPicDate.setOnClickListener{
            activity?.let { it1 -> DatePickerDialog(it1,dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)).show() }
        }

        binding.imgTakeImage.setOnClickListener{
            getImage()

        }

        getFee()

        binding.btnActiveInactive.setOnClickListener{
            try {
                if (getStatus() == "A"){
                    val  sqlQuery = "UPDATE MEMBER SET STATUS = 'D' WHERE ID = '$ID'"
                    db?.fireQuery(sqlQuery)
                    showToast("Member Is Inactive Now")
                }else{
                    val  sqlQuery = "UPDATE MEMBER SET STATUS = 'A' WHERE ID = '$ID'"
                    db?.fireQuery(sqlQuery)
                    showToast("Member Is Active Now")
                }
            }catch (e: Exception){
                e.printStackTrace()
            }
        }


        if (ID.trim().isNotEmpty()){
            if (getStatus() == "A"){
                binding.btnActiveInactive.text = "Inactive"
                binding.btnActiveInactive.visibility = View.VISIBLE
            }else{
                binding.btnActiveInactive.text = "Active"
                binding.btnActiveInactive.visibility = View.VISIBLE
            }
            loadData()
        }else{
            binding.btnActiveInactive.visibility = View.GONE
        }
    }

    private fun getStatus(): String{
        var status = ""
        try {
            val sqlQuery = "SELECT * FROM MEMBER WHERE ID = '$ID'"
            db?.fireQuery(sqlQuery)?.use {
                if (it.count>0){
                    status = MyFunction.getvalue(it,"STATUS")
                }
            }
        }catch (e: Exception){
            e.printStackTrace()
        }
        return status
    }


    private fun getFee(){
        try {
            val sqlQuery = "SELECT * FROM FEE WHERE ID = '1'"
            db?.fireQuery(sqlQuery)?.use {
                if(it.count>0) {
                    oneMonth = MyFunction.getvalue(it, "ONE_MONTH")
                    threeMonth = MyFunction.getvalue(it, "THREE_MONTH")
                    sixMonth = MyFunction.getvalue(it, "SIX_MONTH")
                    oneYear = MyFunction.getvalue(it, "ONE_YEAR")
                    threeYear = MyFunction.getvalue(it, "THREE_YEAR")
                }
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
                edtAmt.setText(String.format("%.2f",total))
            }

        }else if (month == "3 Months"){
            if (discount.trim().isEmpty()){
                discount = "0"
            }

            if (threeMonth !!.trim().isNotEmpty()){
                val discountAmount = ((threeMonth!!.toDouble() * discount.toDouble())/100) // finding out the discount amount
                val total = threeMonth!!.toDouble() - discountAmount // minus discount amount from main amount
                edtAmt.setText(String.format("%.2f",total))
            }
        }else if (month == "6 Months"){
            if (discount.trim().isEmpty()){
                discount = "0"
            }

            if (sixMonth !!.trim().isNotEmpty()){
                val discountAmount = ((sixMonth!!.toDouble() * discount.toDouble())/100) // finding out the discount amount
                val total = sixMonth!!.toDouble() - discountAmount // minus discount amount from main amount
                edtAmt.setText(String.format("%.2f",total))
            }

        }else if (month == "1 Year"){
            if (discount.trim().isEmpty()){
                discount = "0"
            }

            if (oneYear !!.trim().isNotEmpty()){
                val discountAmount = ((oneYear!!.toDouble() * discount.toDouble())/100) // finding out the discount amount
                val total = oneYear!!.toDouble() - discountAmount // minus discount amount from main amount
                edtAmt.setText(String.format("%.2f",total))
            }

        }else if (month == "3 Years"){
            if (discount.trim().isEmpty()){
                discount = "0"
            }

            if (threeYear !!.trim().isNotEmpty()){
                val discountAmount = ((threeYear!!.toDouble() * discount.toDouble())/100) // finding out the discount amount
                val total = threeYear!!.toDouble() - discountAmount // minus discount amount from main amount
                edtAmt.setText(String.format("%.2f",total))
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

    private fun getImage() {
        val items = arrayOf("Take Photo", "Choose Image", "Cancel")
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle("Select Image")
        builder.setItems(items) { dialogInterface, i ->
            when (items[i]) {
                "Take Photo" -> checkCameraPermission()
                "Choose Image" -> checkStoragePermission()
                "Cancel" -> dialogInterface.dismiss()
            }
        }
        builder.show()
    }

    /** Check Camera Permission */
    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED
        ) {
            takePicture()
        } else {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_CODE)
        }
    }

    /** Check Storage Permission (Handles Android 13+) */
    private fun checkStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+ (API 33) → Use READ_MEDIA_IMAGES
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_MEDIA_IMAGES)
                == PackageManager.PERMISSION_GRANTED
            ) {
                takeFromGallery()
            } else {
                ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.READ_MEDIA_IMAGES), STORAGE_PERMISSION_CODE)
            }
        } else {
            // Android 12 and below → Use READ_EXTERNAL_STORAGE
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED
            ) {
                takeFromGallery()
            } else {
                ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), STORAGE_PERMISSION_CODE)
            }
        }
    }

    //Handle Permission Request Results
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            CAMERA_PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    takePicture()
                } else {
                    showPermissionDeniedDialog("Camera permission is required to take pictures.")
                }
            }

            STORAGE_PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    takeFromGallery()
                } else {
                    showPermissionDeniedDialog("Storage permission is required to select images.")
                }
            }
        }
    }

    // Show Permission Denied Alert
    private fun showPermissionDeniedDialog(message: String) {
        AlertDialog.Builder(requireActivity())
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    //     Open Camera
    private fun takePicture() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(takePictureIntent, REQUEST_CAMERA)
    }

    /// Open Gallery
    private fun takeFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_GALLERY)
    }

    //  Handle Image Results
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CAMERA -> {
                    val bitmap = data?.extras?.get("data") as? Bitmap
                    bitmap?.let { getImagePath(it) }
                }
                REQUEST_GALLERY -> {
                    val imageUri: Uri? = data?.data
                    imageUri?.let {
                        actualImagePath = it.toString()
                        Glide.with(requireActivity()).load(actualImagePath).into(binding.imgpic)
                    }
                }
            }
        }
    }

    // Convert Bitmap to File Path
    private fun getImagePath(bitmap: Bitmap) {
        val tempUri: Uri? = captureImage?.getImageUri(requireActivity(), bitmap)
        actualImagePath = captureImage?.getRealPathFromURI(tempUri, requireActivity()).toString()
        Log.d("FragmentAdd", "ActualImagePath: $actualImagePath")

        requireActivity().let {
            Glide.with(it).load(actualImagePath).into(binding.imgpic)
        }
    }


    private  fun validate(): Boolean{
        if(binding.edtFirstName.text.toString().trim().isEmpty()){
            showToast("Enter First Name")
            return false
        }else if (binding.edtLastName.text.toString().trim().isEmpty()){
            showToast("Enter Last Name")
            return false
        }else if (binding.edtAge.text.toString().trim().isEmpty()){
            showToast("Enter Age")
            return false
        }else if (binding.edtMobile.text.toString().trim().isEmpty()) {
            showToast("Enter Mobile Number")
            return false
        }
        return true
    }

    private fun saveData(){
        try {

            var myIncrementID = ""
            if (ID.trim().isEmpty()){
                myIncrementID = getIncrementedId()
            }else{
                myIncrementID = ID
            }


            val sqlQuery = "INSERT OR REPLACE INTO MEMBER(ID,FIRST_NAME,LAST_NAME,GENDER,AGE," +
                    "WEIGHT,MOBILE,ADDRESS,DATE_OF_JOINING,MEMBERSHIP,EXPIRE_ON,DISCOUNT,TOTAL,IMAGE_PATH,STATUS) VALUES" +
                    "('${myIncrementID}', ${DatabaseUtils.sqlEscapeString(binding.edtFirstName.text.toString().trim())}," +
                    "${DatabaseUtils.sqlEscapeString(binding.edtLastName.text.toString().trim())},'$gender'," +
                    "'${binding.edtAge.text.toString().trim()}','${binding.edtWeight.text.toString().trim()}'," +
                    "'${binding.edtMobile.text.toString().trim()}', ${DatabaseUtils.sqlEscapeString(binding.edtAddress.text.toString().trim())}," +
                    "'${MyFunction.returnSQLDateFormat(binding.edtJoiningDate.text.toString().trim())}','${binding.spMembership.selectedItem.toString().trim()}'," +
                    "'${MyFunction.returnSQLDateFormat(binding.edtExpire.text.toString().trim())}','${binding.edtDiscount.text.toString().trim()}'," +
                    "'${binding.edttAmount.text.toString().trim()}', '${actualImagePath}', 'A')"
            Log.d("SQL_INSERT", sqlQuery)  // Debugging line to print the query
            db?.executeQuery(sqlQuery)
            showToast("Data Saved Successfully")



            if (ID.trim().isEmpty()) {
                clearData()
            }
        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    private fun getIncrementedId(): String {
        var incrementId = "1" // Default value if no record exists
        try {
            val sqlQuery = "SELECT IFNULL(MAX(ID)+1, '1') AS ID FROM MEMBER"
            db?.fireQuery(sqlQuery)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    incrementId = cursor.getString(cursor.getColumnIndexOrThrow("ID"))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return incrementId
    }

    private fun clearData(){
        binding.edtFirstName.setText("")
        binding.edtLastName.setText("")
        binding.edtAge.setText("")
        binding.edtWeight.setText("")
        binding.edtMobile.setText("")
        binding.edtJoiningDate.setText("")
        binding.edtAddress.setText("")
        actualImagePath = ""

        Glide.with(this)
            .load(R.drawable.boy)
            .into(binding.imgpic)

    }

    private fun loadData(){
        try {
            val sqlQuery = "SELECT * FROM MEMBER WHERE ID = '$ID'"
            db?.fireQuery(sqlQuery)?.use {
                if (it.count>0){
                    val firstName = MyFunction.getvalue(it,"FIRST_NAME")
                    val lastName = MyFunction.getvalue(it,"LAST_NAME")
                    val age = MyFunction.getvalue(it,"AGE")
                    val gender = MyFunction.getvalue(it,"GENDER")
                    val weight = MyFunction.getvalue(it,"WEIGHT")
                    val mobileNo = MyFunction.getvalue(it,"MOBILE")
                    val address = MyFunction.getvalue(it,"ADDRESS")
                    val dateOfJoin = MyFunction.getvalue(it,"DATE_OF_JOINING")
                    val membership = MyFunction.getvalue(it,"MEMBERSHIP")
                    val expiry = MyFunction.getvalue(it,"EXPIRE_ON")
                    val discount = MyFunction.getvalue(it,"DISCOUNT")
                    val total = MyFunction.getvalue(it,"TOTAL")
                    actualImagePath = MyFunction.getvalue(it,"IMAGE_PATH")

                    binding.edtFirstName.setText(firstName)
                    binding.edtLastName.setText(lastName)
                    binding.edtAge.setText(age)
                    binding.edtWeight.setText(weight)
                    binding.edtMobile.setText(mobileNo)
                    binding.edtAddress.setText(address)
                    binding.edtJoiningDate.setText(MyFunction.returnUserDateFormat(dateOfJoin))

                    if (!actualImagePath.isNullOrEmpty()){
                        Glide.with(this)
                            .load(actualImagePath)
                            .into(binding.imgpic)
                    }else{
                        if (gender == "Male"){
                            Glide.with(this)
                                .load(R.drawable.boy)
                                .into(binding.imgpic)
                        }else{
                            Glide.with(this)
                                .load(actualImagePath)
                                .into(binding.imgpic)
                        }
                    }
                    if (membership.trim().isNotEmpty()){
                        when(membership){
                            "1 Month" ->{
                                binding.spMembership.setSelection(1)
                            }
                            "3 Months" ->{
                                binding.spMembership.setSelection(2)
                            }
                            "6 Months" ->{
                                binding.spMembership.setSelection(3)
                            }
                            "1 Year" ->{
                                binding.spMembership.setSelection(4)
                            }
                            "3 Years" ->{
                                binding.spMembership.setSelection(5)
                            }
                            else -> {
                                binding.spMembership.setSelection(0)
                            }
                        }
                    }

                    if (gender == "Male"){
                        binding.radioGroup.check(R.id.rdMale)
                    }else{
                        binding.radioGroup.check(R.id.rdFemale)
                    }
                    binding.edtExpire.setText(MyFunction.returnUserDateFormat(expiry))
                    binding.edttAmount.setText(total)
                    binding.edtDiscount.setText(discount)
                }
            }

        }catch (e: Exception){
            e.printStackTrace()
        }
    }


}
