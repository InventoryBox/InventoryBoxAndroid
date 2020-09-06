package com.example.inventorybox.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.inventorybox.DB.SharedPreferenceController
import com.example.inventorybox.R
import com.example.inventorybox.SignUp
import com.example.inventorybox.etc.showCustomToast
import com.example.inventorybox.network.POST.RequestLogin
import com.example.inventorybox.network.POST.ResponseLogin
import com.example.inventorybox.network.RequestToServer
import com.example.inventorybox.network.customEnqueue
import kotlinx.android.synthetic.main.activity_login.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    val requestToServer = RequestToServer

    private val REQUEST_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        btn_signup.setOnClickListener {
            startActivity(Intent(this, SignUp::class.java))
        }

        tv_find_email.setOnClickListener {
            startActivity(Intent(this, FindEmailActivity::class.java))
        }

        tv_find_pw.setOnClickListener {
            startActivity(Intent(this, FindEmailActivity::class.java))
        }



        btn_signin.setOnClickListener {
            val login_email = et_login_email.text.toString()
            val login_pw : String = et_login_password.text.toString()

            if(login_email.isNullOrBlank() || login_pw.isNullOrBlank()||!isValid(login_email, login_pw)){
                Toast.makeText(this, "이메일과 비밀번호를 확인하세요", Toast.LENGTH_SHORT).show()
            }
            else {
                 postLoginResponse(login_email, login_pw)
            }
        }
    }

    //이메일과 비밀번호가 채워져 있지 않으면 포커스
    fun isValid(u_email: String, u_pw: String): Boolean {
        if (u_email == ""){
            et_login_email.requestFocus()
        }else if (u_pw == ""){
            et_login_password.requestFocus()
        }
        else return true
        return false
    }

    fun postLoginResponse(u_email: String, u_pw: String){

        requestToServer.service.requestLogin(
            RequestLogin(
                email = u_email,
                password = u_pw
            )
        ).customEnqueue(
            onFail = {
                Log.e("login failed", "fail")
                et_login_email.setBackgroundResource(R.drawable.underline_red)
                et_login_password.setBackgroundResource(R.drawable.underline_red)
                this.showCustomToast("이메일/비밀번호를 확인하세요!")
            },
            onSuccess = {
                this.showCustomToast("로그인이 되었습니다")
                startActivity(Intent(this@LoginActivity, MainActivity::class.java))

                val login_u_email = et_login_email.text.toString()
                val login_u_pw = et_login_password.text.toString()

                //Log.d("token", it.data!!.jwt.toString())

                SharedPreferenceController.setUserInfo(applicationContext, login_u_email)
                //SharedPreferenceController.setUserInfo(applicationContext, it.data.jwt)
                finish()
            },
            onError = {
                this.showCustomToast("아이디/비밀번호를 확인하세요!")
            }

        )


//            .enqueue(object: Callback<ResponseLogin>{
//            override fun onFailure(call: Call<ResponseLogin>, t: Throwable) {
//
//            }
//
//            override fun onResponse(call: Call<ResponseLogin>, response: Response<ResponseLogin>) {
//                if (response.isSuccessful){
//                    if (response.body()!!.status == 200){
//                        Toast.makeText(this@LoginActivity, "로그인이 되었습니다", Toast.LENGTH_SHORT).show()
//                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
//
//                        val login_u_email = et_login_email.text.toString()
//                        val login_u_pw = et_login_password.text.toString()
//
//                        Login(login_u_email, login_u_pw)
//                        finish()
//                    }else{
//                        Toast.makeText(this@LoginActivity, "아이디/비밀번호를 확인하세요!", Toast.LENGTH_SHORT).show()
//                    }
//                }
//            }
//        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                val id = data!!.getStringExtra("id")
                val pw = data!!.getStringExtra("password")
                et_login_email.setText(id)
                et_login_password.setText(pw)

                if(SharedPreferenceController.getUserEmail(this)!!.isEmpty()){
                    Toast.makeText(this, "로그인을 해주세요", Toast.LENGTH_SHORT).show()
                }
                else{
                    Toast.makeText(this, "자동 로그인 합니다", Toast.LENGTH_SHORT).show()
                    val delayHandler = Handler()
                    delayHandler.postDelayed(Runnable {
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }, 2000)
                }
            }
        }
    }

//    fun Login(u_email: String, u_: String) {
//        SharedPreferenceController.setUserInfo(applicationContext, u_email)
//        finish()
//    }

}

