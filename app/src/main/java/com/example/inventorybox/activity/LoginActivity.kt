package com.example.inventorybox.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.inventorybox.R
import com.example.inventorybox.SignUp
import com.example.inventorybox.network.POST.RequestLogin
import com.example.inventorybox.network.POST.ResponseLogin
import com.example.inventorybox.network.RequestToServer
import kotlinx.android.synthetic.main.activity_login.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    val requestToServer = RequestToServer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        btn_signup.setOnClickListener {
            startActivity(Intent(this, SignUp::class.java))
        }

        btn_signin.setOnClickListener {
            val login_email = et_login_email.text.toString()
            val login_pw : String = et_login_password.text.toString()

            if(isValid(login_email, login_pw)) postLoginResponse(login_email, login_pw)
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

    //서버에 로그인 요청
    fun postLoginResponse(u_email: String, u_pw: String){

        requestToServer.service.requestLogin(
            RequestLogin(
                email = u_email,
                password = u_pw
            )
        ).enqueue(object : Callback<ResponseLogin>{
            override fun onFailure(call: Call<ResponseLogin>, t: Throwable){
                Log.e("login failed", t.toString())
                et_login_email.setBackgroundResource(R.drawable.underline_red)
                et_login_password.setBackgroundResource(R.drawable.underline_red)
            }

            override fun onResponse(
                call: Call<ResponseLogin>,
                response: Response<ResponseLogin>
            ) {
                if (response.isSuccessful){
                    if (response.body()!!.status == 200){
                        Toast.makeText(this@LoginActivity, "로그인이 되었습니다", Toast.LENGTH_SHORT).show()
                        Log.d("login", "로그인이 되었습니다." + response.body()!!.data!!)
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        finish()
                    }
                    else{
                        et_login_email.setBackgroundResource(R.drawable.underline_red)
                        et_login_password.setBackgroundResource(R.drawable.underline_red)
                        Toast.makeText(this@LoginActivity, "이메일/비밀번호를 확인하세요!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        )
    }

}
