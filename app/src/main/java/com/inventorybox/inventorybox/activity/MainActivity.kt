package com.inventorybox.inventorybox.activity

import android.content.Intent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.content.pm.PackageInfo
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentTransaction
import com.bumptech.glide.Glide
import com.inventorybox.inventorybox.DB.SharedPreferenceController
import com.inventorybox.inventorybox.ExchangeMyPost
import com.inventorybox.inventorybox.R
import com.inventorybox.inventorybox.fragment.ExchangeFragment
import com.inventorybox.inventorybox.fragment.GraphFragment
import com.inventorybox.inventorybox.fragment.HomeFragment
import com.inventorybox.inventorybox.fragment.RecordFragment
import com.inventorybox.inventorybox.network.RequestToServer
import com.inventorybox.inventorybox.network.customEnqueue
import kotlinx.android.synthetic.main.activity_drawer.*
import kotlinx.android.synthetic.main.activity_main.*
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


class MainActivity : AppCompatActivity() {

    val requestToServer = RequestToServer

    /*companion object{
        var dl : DrawerLayout? = null
    }*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //dl = home_drawer
//        main_bottom_navigation.setItemIconSize(50)  //하단바 아이콘 사이즈

        //유저 개인 정보 가져오기
//        getPersonal()

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

        //로그아웃시 메인액티비티 종료 및 로그인 화면으로 이동
        val login_intent = Intent(this, LoginActivity::class.java)
        val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(arg0: Context?, intent: Intent) {
                val action = intent.action
                if (action == "finish_activity") {
                    finish()
                    startActivity(login_intent)
                }
            }
        }
        registerReceiver(broadcastReceiver, IntentFilter("finish_activity"))
    }

    private fun drawerSelected() {
        drawer_profile.setOnClickListener {
            startActivity(Intent(this, HomeProfileActivity::class.java))
        }
        drawer_personal.setOnClickListener {
            startActivity(Intent(this, HomePersonalActivity::class.java))
        }
        drawer_mypost.setOnClickListener {
            startActivity(Intent(this, ExchangeMyPost::class.java))
        }
        drawer_emailpassword.setOnClickListener {
            startActivity(Intent(this, HomeEmailActivity::class.java))
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

    fun getPersonal() {
        requestToServer.service.getHomePersonal(
            SharedPreferenceController.getUserToken(this)
        ).customEnqueue(
            onSuccess = {
                Log.d("home personal", "유저 개인 정보 조회 성공")

                val repName = findViewById<TextView>(R.id.drawer_rep_name)
                val coName = findViewById<TextView>(R.id.drawer_co_name)
                val img = findViewById<ImageView>(R.id.iv_drawer_profile)

                repName.text = it.data.nickname
                coName.text = it.data.coName
                Glide.with(this).load(it.data.img).into(iv_drawer_profile)

            }
        )
    }

    override fun onStart() {
        super.onStart()
        getPersonal()
    }




}
