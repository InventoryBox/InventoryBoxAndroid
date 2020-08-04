package com.example.inventorybox.activity


    import android.app.Activity
    import android.content.Intent
    import android.os.Bundle
    import android.os.PersistableBundle
    import android.util.Log
    import android.view.View
    import android.widget.EditText
    import androidx.appcompat.app.AppCompatActivity
    import com.example.inventorybox.R
    import com.example.inventorybox.adapter.RecordCategoryAdapter
    import com.example.inventorybox.adapter.RecordModifyAdapter
    import com.example.inventorybox.data.*
    import com.example.inventorybox.fragment.RecordFragment
    import com.example.inventorybox.network.RequestToServer
    import com.example.inventorybox.network.customEnqueue
    import kotlinx.android.synthetic.main.activity_record_modify.*
    import kotlinx.android.synthetic.main.fragment_record.*
    import kotlinx.android.synthetic.main.item_record_record.view.*
    import java.lang.Exception
    import java.text.SimpleDateFormat
    import java.util.*

// 재고기록에서 기록수정 눌렀을 때 뷰
class RecordModifyActivity : AppCompatActivity(){


    lateinit var category_adapter : RecordCategoryAdapter
    lateinit var item_adapter : RecordModifyAdapter
    var datas_cate = mutableListOf<RecordHomeCategoryInfo>()
    var datas_item = mutableListOf<RecordModifyItemInfo>()
    var sorted_item = mutableListOf<RecordModifyItemInfo>()

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_record_modify)

            // adapter 설정
            category_adapter = RecordCategoryAdapter(this)
            item_adapter = RecordModifyAdapter(this)
            rv_cate_record_modify.adapter = category_adapter
            rv_item_record_modify.adapter = item_adapter

            // 카테고리 클릭 이벤트 받아올 리스너
            val category_listener = object : RecordFragment.CategoryClickListener {
                    override fun onClick(category_idx: Int) {
                    if(category_idx>1){
                        sorted_item = datas_item.filter {
                            it.categoryIdx == category_idx
                        }.toMutableList()
                    }else{
                        sorted_item = datas_item
                    }
                    item_adapter.datas = sorted_item
                    item_adapter.notifyDataSetChanged()

                }
            }

            category_adapter.listener = category_listener

//            // 처음 데이터 가져오기
            var date = intent.getStringExtra("date")
            record_modify_tv_date.text = date
            date  = date.replace(".","-",false)
            date = date.subSequence(0,10).toString()
            Log.d("####ResponseModifyActivity", date)
            requestData(date)

            //버튼 눌렀을 때 최상단으로 이동
            ll_up.setOnClickListener {
                scrollview_record_modify.smoothScrollTo(0, 0)
            }

            scrollview_record_modify.setOverScrollMode(View.OVER_SCROLL_NEVER)


            //뒤로 가기
            img_back.setOnClickListener {
                finish()
            }

            //재료 추가하기
            btn_add_ingre.setOnClickListener {
                val intent = Intent(this, RecordAddActivity::class.java)
                startActivity(intent)
            }

            btn_confirm_record_modify.setOnClickListener {
                uploadData(date)
            }
        }

    // 바뀐 데이터를 전달
    private fun uploadData(date : String){

        var datas = arrayListOf<ResponseRecordCntItemInfo>()
        for(i in 0..(item_adapter.itemCount-1)){
            val itemView = rv_item_record_modify.layoutManager?.findViewByPosition(i)
            val count = itemView?.findViewById<EditText>(R.id.tv_rv_input_stock)?.text.toString()
            datas.add(
                ResponseRecordCntItemInfo(
                    datas_item[i].itemIdx,
                    if(count.isNotEmpty()) {
                        Integer.parseInt(count)
                    }else{
                        -1
                    }
                )
            )
        }

        RequestToServer.service.requestRecordModify(
            getString(R.string.test_token),
            RequestRecordItemModify(
                date,
                datas
            )
        ).customEnqueue(
            onSuccess = {
                Log.d("RecordModifyActivity", "success")

                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        )
    }

    private fun requestData(date : String){

        RequestToServer.service.getRecordModifyResponse(
            date, getString(R.string.test_token)
        ).customEnqueue(
            onSuccess = {
                for(data in it.data.categoryInfo){
                    datas_cate.add(data)
                }
                category_adapter.datas = datas_cate
                category_adapter.notifyDataSetChanged()

                for(data in it.data.itemInfo){
                    datas_item.add(data)
                }
                item_adapter.datas = datas_item
                item_adapter.notifyDataSetChanged()
            })
    }

}