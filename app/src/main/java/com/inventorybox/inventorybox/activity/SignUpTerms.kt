package com.inventorybox.inventorybox.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.inventorybox.inventorybox.R
import com.inventorybox.inventorybox.network.ApplicationController
import com.inventorybox.inventorybox.network.RequestToServer
import com.inventorybox.inventorybox.network.customEnqueue
import kotlinx.android.synthetic.main.activity_sign_up_terms.*
import okhttp3.MediaType
import okhttp3.RequestBody

class SignUpTerms : AppCompatActivity() {

    // multipart form으로 보내기 위해
    var map = HashMap<String, RequestBody>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up_terms)

        //전체 체크박스 선택 시 모든 체크박스 선택
        signup_cb_all.setOnClickListener {
            if(signup_cb_all.isChecked){
                signup_cb_1.isChecked = true
                signup_cb_2.isChecked = true
                signup_cb_3.isChecked = true
            }else{
                signup_cb_1.isChecked = false
                signup_cb_2.isChecked = false
                signup_cb_3.isChecked = false
            }
            btnSignupTermsActivation()
        }

        // 글씨나 주변만 눌러도 체크박스 클릭되도록
        agree_1.setOnClickListener {
            signup_cb_all.performClick()
        }
        agree_2.setOnClickListener {
            signup_cb_1.performClick()
        }
        agree_3.setOnClickListener {
            signup_cb_2.performClick()
        }
        agree_4.setOnClickListener {
            signup_cb_3.performClick()
        }

        signup_cb_1.setOnClickListener {
            signUpTermsAllCheckBox()
            btnSignupTermsActivation()
        }

        signup_cb_2.setOnClickListener {
            signUpTermsAllCheckBox()
            btnSignupTermsActivation()
        }

        signup_cb_3.setOnClickListener {
            signUpTermsAllCheckBox()
            btnSignupTermsActivation()
        }

        //재고창고 약관 확인 버튼 클릭 시
        cl_terms_check.setOnClickListener{
            val intent = Intent(this, TermsConditionsActivity::class.java)
            startActivity(intent)
        }

        //뒤로가기 버튼 클릭 시 프로필설정 화면으로 이동
        btn_back_signup_terms.setOnClickListener{
            finish()
        }

        //다음 버튼 클릭 시 로그인 화면으로 이동
        btn_signup_terms.setOnClickListener{

            sendToServer()
            val intent = Intent(this, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            startActivity(intent)
            finish()
        }
    }

    private fun sendToServer() {
        val global = ApplicationController


        map.put("nickname", getRq(global.nickname))
        map.put("email", getRq(global.email))
        map.put("password", getRq(global.password))
        map.put("repName", getRq(global.rep_name))
        map.put("coName",getRq(global.co_name))
        map.put("phoneNumber",getRq(global.phone_num))
        map.put("pushAlarm",getRq("0"))
//        map.put("img",getRq("0"))
//
        Log.d("signup profile", "${global.email}      ${global.img}      ${global.nickname}      ${global.rep_name}      ${global.phone_num}      ${global.co_name}")
        val img = global.img
        RequestToServer.service.requestSignUp(
            img,
            map
        ).customEnqueue(
            onSuccess = {
                Log.d("signup profile", "success")
                Log.d("signup profile", "${global.email}      ${global.img}")
            }
        )

    }


    //젠체 체크박스 상태
    fun signUpTermsAllCheckBox(){
        signup_cb_all.isChecked = signup_cb_1.isChecked&&signup_cb_2.isChecked&&signup_cb_3.isChecked
    }

    //필수 체크박스 선택 시 버튼 활성화
    fun btnSignupTermsActivation(){
        if(signup_cb_1.isChecked&&signup_cb_2.isChecked&&signup_cb_3.isChecked){
            btn_signup_terms.background = getDrawable(R.drawable.rec30_yellow_gradient)
            btn_signup_terms.isEnabled=true
        }else{
            btn_signup_terms.background = getDrawable(R.drawable.graph_rec30_middlegrey)
            btn_signup_terms.isEnabled=false
        }
    }
    fun getRq(s : String): RequestBody {
        return RequestBody.create(MediaType.parse("text/plain"), s)
    }

}