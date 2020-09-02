package com.example.inventorybox.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import com.example.inventorybox.R
import com.example.inventorybox.fragment.ExchangeFragment
import com.example.inventorybox.fragment.GraphFragment
import com.example.inventorybox.fragment.HomeFragment
import com.example.inventorybox.fragment.RecordFragment
import kotlinx.android.synthetic.main.activity_drawer.*
import kotlinx.android.synthetic.main.activity_main.*
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


class MainActivity : AppCompatActivity() {

    /*companion object{
        var dl : DrawerLayout? = null
    }*/

    lateinit var main_activity:Activity //로그아웃

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        main_activity = this@MainActivity //로그아웃

        //dl = home_drawer
        main_bottom_navigation.setItemIconSize(90)  //하단바 아이콘 사이즈

        //드로워 선택
        drawerSelected()
        val drawerEvent = {
            home_drawer.openDrawer(drawer)
        }
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.replace(
            R.id.frame_layout,
            HomeFragment(drawerEvent), "home").commitAllowingStateLoss()

        main_bottom_navigation.setOnNavigationItemSelectedListener {
            val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()

            when(it.itemId) {
                R.id.menu_home -> {
                    val fragment = HomeFragment(drawerEvent)
                    transaction.replace(R.id.frame_layout, fragment, "home")
                    home_drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)  //drawer가 나오게 하기
                }
                R.id.menu_record -> {
                    val fragment = RecordFragment()
                    transaction.replace(R.id.frame_layout, fragment, "record")
                    home_drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED) //drawer가 안나오게 막기
                }
                R.id.menu_graph -> {
                    val fragment = GraphFragment()
                    transaction.replace(R.id.frame_layout, fragment, "graph")
                    home_drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
                }
                R.id.menu_exchange -> {
                    val fragment = ExchangeFragment()
                    transaction.replace(R.id.frame_layout, fragment, "exchange")
                    home_drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
                }
            }
            transaction.commit()
            true
        }
    }

    private fun drawerSelected() {
        drawer_notice.setOnClickListener {
            startActivity(Intent(this, HomeNoticeActivity::class.java))
        }
        drawer_profile.setOnClickListener {
            startActivity(Intent(this, HomeProfileActivity::class.java))
        }
        drawer_personal.setOnClickListener {
            startActivity(Intent(this, HomePersonalActivity::class.java))
        }
        drawer_mypost.setOnClickListener {
            startActivity(Intent(this, HomeMypostActivity::class.java))
        }
        drawer_emailpassword.setOnClickListener {
            startActivity(Intent(this, HomeEmailActivity::class.java))
        }
        drawer_customer.setOnClickListener {
            startActivity(Intent(this, HomeCustomerActivity::class.java))
        }
        drawer_settings.setOnClickListener {
            startActivity(Intent(this, HomeSettingsActivity::class.java))
        }
    }

    fun getKeyHash(context: Context?): String? {
        val packageInfo: PackageInfo =
            PackageInfo()
                ?: return null
        for (signature in packageInfo.signatures) {
            try {
                val md: MessageDigest = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                return Base64.encodeToString(md.digest(), Base64.NO_WRAP)
            } catch (e: NoSuchAlgorithmException) {

            }
        }
        return null
    }
}
