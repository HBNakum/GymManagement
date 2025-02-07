package com.example.gymmanagement.global

import android.database.Cursor
import android.util.Log
import java.text.SimpleDateFormat

class MyFunction {

    companion object{
        fun getvalue(cursor: Cursor,columnName: String): String{

            var value: String = ""

            try {
                val col = cursor.getColumnIndex(columnName)
                value = cursor.getString(col)
            }catch (e: Exception){
                e.printStackTrace()
                Log.d("MyFunction", "getValue ${e.printStackTrace()}")
            }
            return value

        }

        fun returnSQLDateFormat(date: String): String {
            try {
                if (date.trim().isNotEmpty()){
                    val  dateFormat1 = SimpleDateFormat("dd/MM/yyyy")
                    val firstDate = dateFormat1.parse(date)
                    val  dateFormat2 = SimpleDateFormat("yyyy-MM-dd")
                    return dateFormat2.format(firstDate)
                }
            }catch (e: Exception){
                e.printStackTrace()
            }
            return  ""
        }

    }
}