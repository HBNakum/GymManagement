package com.example.gymmanagement.global

object SqlTable {
    const val admin = "CREATE TABLE ADMIN(ID INTEGER PRIMARY KEY AUTOINCREMENT, USER_NAME TEXT DEFAULT '',"+
            "PASSWORD TEXT DEFAULT '' , MOBILE TEXT DEFAULT '')"


    const val member = "CREATE TABLE MEMBER(ID INTEGER PRIMARY KEY AUTOINCREMENT, FIRST_NAME TEXT DEFAULT '',"+
            "LAST_NAME TEXT DEFAULT '' , GENDER TEXT DEFAULT '' , AGE INTEGER DEFAULT 0 , WEIGHT REAL DEFAULT 0, MOBILE TEXT DEFAULT '',"+
            "ADDRESS TEXT DEFAULT '' , DATE_OF_JOINING TEXT DEFAULT '' , MEMBERSHIP TEXT DEFAULT '', EXPIRE_ON TEXT DEFAULT '',"+
            "DISCOUNT REAL DEFAULT 0 , TOTAL REAL DEFAULT 0, IMAGE_PATH TEXT DEFAULT '', STATUS TEXT DEFAULT 'A')"


    const val fee = "CREATE TABLE FEE(ID INTEGER PRIMARY KEY AUTOINCREMENT,"+
            "ONE_MONTH REAL DEFAULT 0, THREE_MONTH REAL DEFAULT 0, "+
            "SIX_MONTH REAL DEFAULT 0, ONE_YEAR REAL DEFAULT 0, THREE_YEAR REAL DEFAULT 0)"

}