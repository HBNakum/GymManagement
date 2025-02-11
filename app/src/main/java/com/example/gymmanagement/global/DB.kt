package com.example.gymmanagement.global

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DB(val context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(SqlTable.admin)
        db?.execSQL(SqlTable.member)
        db?.execSQL(SqlTable.fee)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS ADMIN")
        db?.execSQL("DROP TABLE IF EXISTS MEMBER")
        db?.execSQL("DROP TABLE IF EXISTS FEE")

    }

    fun executeQuery(sql: String): Boolean {
        return try {
            val database = this.writableDatabase
            database.execSQL(sql)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun fireQuery(sql: String): Cursor? {
        var tempCursor: Cursor? = null
        try {
            val database = this.readableDatabase
            tempCursor = database.rawQuery(sql, null)
            if (tempCursor != null && tempCursor.moveToFirst()) {
                return tempCursor
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return tempCursor
    }
    companion object {
        private const val DB_VERSION = 1  // Fixed typo
        private const val DB_NAME = "Gym.DB"
    }
}
