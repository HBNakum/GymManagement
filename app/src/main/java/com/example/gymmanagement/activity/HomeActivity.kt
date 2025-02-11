package com.example.gymmanagement.activity

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.PersistableBundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.gymmanagement.R
import com.example.gymmanagement.databinding.ActivityHomeBinding
import com.example.gymmanagement.fragment.FragmentAddMember
import com.example.gymmanagement.fragment.FragmentAddUpdateFee
import com.example.gymmanagement.fragment.FragmentAllMember
import com.example.gymmanagement.fragment.FragmentChangePassword
import com.example.gymmanagement.fragment.FragmentFeePending
import com.example.gymmanagement.global.DB
import com.example.gymmanagement.manager.SessionManager
import com.google.android.material.navigation.NavigationView

class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val TAG = "HomeActivity"
    var session: SessionManager?=null
    var db: DB?=null
    private lateinit var drawer : DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle

    lateinit var binding: ActivityHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        db = DB(this)
        session = SessionManager(this)
        setSupportActionBar(binding.homeInclude.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.navView.setNavigationItemSelectedListener(this)
        drawer = binding.main

        toggle = ActionBarDrawerToggle(this, drawer,binding.homeInclude.toolbar,
            R.string.navigation_drawer_open,R.string.navigation_drawer_close)

        drawer.addDrawerListener(toggle)
        toggle.syncState()
    }

    override fun onPostCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onPostCreate(savedInstanceState, persistentState)
        toggle.syncState()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        toggle.onConfigurationChanged(newConfig)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return super.onOptionsItemSelected(item)

        if (toggle.onOptionsItemSelected(item)){
            return true
        }

        if (item.itemId == R.id.logOutMenu)
            logout()


        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.nav_home ->{

                val fragment = FragmentAllMember()
                loadFragment(fragment)

                if (drawer.isDrawerOpen(GravityCompat.START)){
                    drawer.closeDrawer(GravityCompat.START)
                }
            }
            R.id.nav_add ->{
                loadFragment()

                if (drawer.isDrawerOpen(GravityCompat.START)){
                    drawer.closeDrawer(GravityCompat.START)
                }
            }
            R.id.nav_nav_fee_panding ->{
                val fragment = FragmentFeePending()
                loadFragment(fragment)

                if (drawer.isDrawerOpen(GravityCompat.START)){
                    drawer.closeDrawer(GravityCompat.START)
                }
            }
            R.id.nav_update_fee ->{
                val fragment = FragmentAddUpdateFee()
                loadFragment(fragment)

                if (drawer.isDrawerOpen(GravityCompat.START)){
                    drawer.closeDrawer(GravityCompat.START)
                }
            }
            R.id.nav_change_password ->{
                val fragment = FragmentChangePassword()
                loadFragment(fragment)

                if (drawer.isDrawerOpen(GravityCompat.START)){
                    drawer.closeDrawer(GravityCompat.START)
                }
            }
            R.id.nav_log_out ->{
               logout()
                if (drawer.isDrawerOpen(GravityCompat.START)){
                    drawer.closeDrawer(GravityCompat.START)
                }
            }
        }
        return true
    }


    private fun logout(){
        session?.setLogin(false)
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START)
        }else{
            super.onBackPressed()
        }
    }

    private fun loadFragment(fragment: Fragment){
        var fragmentManager : FragmentManager?=null
        fragmentManager = supportFragmentManager
        fragmentManager.beginTransaction().replace(R.id.frame_container,fragment).commit()
    }


    private fun loadFragment(){
        val fragment = FragmentAddMember()
        val args = Bundle()
        args.putString("ID","")
        fragment.arguments = args
        val fragmentManager: FragmentManager ?=supportFragmentManager
        fragmentManager!!.beginTransaction().replace(R.id.frame_container,fragment,"FragmentAdd").commit()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        try {
            val inflater = menuInflater
            inflater.inflate(R.menu.menu_main,menu)
        }catch (e: Exception){
            e.printStackTrace()
        }

        return super.onCreateOptionsMenu(menu)
    }
}
