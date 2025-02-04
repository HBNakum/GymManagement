package com.example.gymmanagement

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.gymmanagement.activity.HomeActivity
import com.example.gymmanagement.activity.LoginActivity
import com.example.gymmanagement.databinding.ActivitySplashScreenBinding
import com.example.gymmanagement.global.DB
import com.example.gymmanagement.manager.SessionManager

class SplashScreenActivity : AppCompatActivity() {

    private var mDelayHandler: Handler? = null
    private val splash_delay: Long = 3000 //3 seconds
    var db: DB? = null
    var session: SessionManager? = null
    private lateinit var binding: ActivitySplashScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash_screen)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        db = DB(this)
        session = SessionManager(this)

        insertAdminData()
        mDelayHandler = Handler()
        mDelayHandler?.postDelayed(mRunnable, splash_delay)

    }

    private val mRunnable: Runnable = Runnable {

        if (session?.isLoggedIn == true){
            val intent = Intent(this , HomeActivity::class.java)
            startActivity(intent)
            finish()
        }else {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun insertAdminData() {
        try {
            val sqlCheck = "SELECT * FROM ADMIN"
            db?.fireQuery(sqlCheck)?.use {
                if (it.count > 0) {
                    Log.d("SplashActivity", "data available")
                } else {
                    val sqlQuery =
                        "INSERT OR REPLACE INTO ADMIN(ID,USER_NAME,PASSWORD,MOBILE)VALUES('1','Admin','123456','8888888888')"
                    db?.executeQuery(sqlQuery)
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mDelayHandler?.removeCallbacks(mRunnable)
    }
}