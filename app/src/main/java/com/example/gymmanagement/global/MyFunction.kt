package com.example.gymmanagement.global

import android.database.Cursor
import android.util.Log

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

    }
}