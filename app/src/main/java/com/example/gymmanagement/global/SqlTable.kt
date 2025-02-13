package com.example.gymmanagement.global

object SqlTable {
    const val admin = "CREATE TABLE ADMIN(ID INTEGER PRIMARY KEY AUTOINCREMENT, USER_NAME TEXT DEFAULT '',"+
            "PASSWORD TEXT DEFAULT '' , MOBILE TEXT DEFAULT '')"


    const val member = "CREATE TABLE MEMBER(ID INTEGER PRIMARY KEY AUTOINCREMENT,FIRST_NAME TEXT DEFAULT '',"+
            "LAST_NAME TEXT DEFAULT '',GENDER TEXT DEFAULT '',AGE TEXT DEFAULT '', WEIGHT TEXT DEFAULT '',MOBILE TEXT DEFAULT '',"+
            "ADDRESS TEXT DEFAULT '', DATE_OF_JOINING TEXT DEFAULT '',MEMBERSHIP TEXT DEFAULT '',EXPIRE_ON TEXT DEFAULT '',"+
            "DISCOUNT TEXT DEFAULT '',TOTAL TEXT DEFAULT '',IMAGE_PATH DEFAULT '',STATUS TEXT DEFAULT 'A')"

    const val fee = "CREATE TABLE FEE(ID INTEGER PRIMARY KEY AUTOINCREMENT,"+
            "ONE_MONTH REAL DEFAULT 0, THREE_MONTH REAL DEFAULT 0, "+
            "SIX_MONTH REAL DEFAULT 0, ONE_YEAR REAL DEFAULT 0, THREE_YEAR REAL DEFAULT 0)"

}