package com.inventorybox.inventorybox.activity

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.inventorybox.inventorybox.DB.SharedPreferenceController
import com.inventorybox.inventorybox.R
import com.inventorybox.inventorybox.adapter.RecordCategoryAdapter
import com.inventorybox.inventorybox.adapter.RecordCategoryEditAdapter
import com.inventorybox.inventorybox.data.*
import com.inventorybox.inventorybox.etc.CategoryEditDialog
import com.inventorybox.inventorybox.etc.CustomDialog
import com.inventorybox.inventorybox.etc.showCustomToast
import com.inventorybox.inventorybox.fragment.RecordFragment
import com.inventorybox.inventorybox.network.RequestToServer
import com.inventorybox.inventorybox.network.customEnqueue
import kotlinx.android.synthetic.main.activity_category_edit.*
import kotlinx.android.synthetic.main.activity_category_edit.img_back


class RecordCateogyActivity : AppCompatActivity() {

    var item_adapter = RecordCategoryEditAdapter(this)
    //deleted pos에 onClick에 추가한 itemindex를 배열로 보내주기
    lateinit var category_adapter : RecordCategoryAdapter


    var categoryIdx_cur = 1

    var datas = mutableListOf<RecordHomeCategoryInfo>()
    var datas_item = mutableListOf<RecordHomeItemInfo>()
    var sorted_item = mutableListOf<RecordHomeItemInfo>()
    var datas_cate = mutableListOf<RecordHomeCategoryInfo>()

    // 클릭된 아이템의 position
//    var clicked_pos = mutableListOf<Int>()

    //    var item_index = mutableListOf<Int>()
    // 클릭된 아이템의 idx
//    var clicked_idx = mutableListOf<Int>()

    val requestToServer = RequestToServer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_edit)

        //맨 위로 가기
        val listener = View.OnClickListener{
            scrollview_category_edit.smoothScrollTo(0,0)
        }
        tv_up.setOnClickListener(listener)
        img_up.setOnClickListener(listener)

        //체크박스
        tv_all.setOnClickListener {
            checkBox_all.performClick()
        }
//        val date = intent.getStringExtra("date")
        val date = "0"
        //상단 카테고리
        category_adapter = RecordCategoryAdapter(this)
        // 카테고리 클릭 이벤트 리스너
        val category_listener = object : RecordFragment.CategoryClickListener {
            override fun onClick(category_idx: Int) {
                categoryIdx_cur = category_idx

                if(category_idx>1){
                    sorted_item = datas_item.filter {
                        it.categoryIdx == category_idx
                    }.toMutableList()
                }else{
                    sorted_item = datas_item
                }
                item_adapter.isAllSelected=false
                item_adapter.datas = sorted_item
                item_adapter.notifyDataSetChanged()
                checkBox_all.isChecked=false

                // 아이템 5개 미만이면 맨위로 가기 버튼 안보이게
                if(sorted_item.count()>=5){
                    tv_up.visibility = View.VISIBLE
                    img_up.visibility = View.VISIBLE
                }else{
                    tv_up.visibility = View.INVISIBLE
                    img_up.visibility = View.INVISIBLE
                }

            }

        }
        category_adapter.listener = category_listener
        category_adapter.datas = datas_cate
        rv_category_record_cate.adapter = category_adapter




        rv_item_record_cate.adapter = item_adapter

        item_adapter.datas = datas_item
        item_adapter.notifyDataSetChanged()


        requestData(date)
//        RecordCategoryResponse(date)

        // recyclerview 설정


        // (전체선택)checkbox 가 눌리면,
        val checkbox_click_listener = object : CheckboxClickListener{
            override fun onClick(idx: Int, pos: Int, isClicked : Boolean) { //deleted pos에 onClick에 추가한 itemindex를 배열로 보내주기
//                checkBox_all.isChecked = false
//                Log.d("exchange cateogory activity",clicked_pos.toString())
                datas_item.find{
                    it.itemIdx==idx
                }!!.isSelected = isClicked

                var isAllSelected = true
                for(item in sorted_item){
                    if(!item.isSelected){
                        isAllSelected=false
                        break
                    }
                }
                checkBox_all.isChecked = isAllSelected
            }
        }


        btn_delete.setOnClickListener {


            val items = datas_item.filter {
                it.isSelected
            }.map{
                it.itemIdx
            }
            if(items.count()==0){
                this.showCustomToast("선택된 재료가 없습니다")
            }else{

                val dialog = CustomDialog(this)
                dialog.setTitle("재료삭제")
                dialog.setContent("총 ${items.count()}개의 재료가 삭제됩니다")
                dialog.setPositiveBtn("확인"){
                    deleteRecordItem(items)
                    dialog.dismissDialog()
                }
                dialog.setNegativeBtn("취소") {dialog.dismissDialog()}

                dialog.showDialog()
            }
//            Collections.sort(clicked_pos)
//            Collections.reverse(clicked_pos)

//            for(i in clicked_pos){
//                datas_item.removeAt(i)
//            }
//            Log.d("RecordCategoryActivity",clicked_idx.toString())

//            clicked_idx = mutableListOf()
//            clicked_pos = mutableListOf()
////            recordCategoryAdapter = RecordCategoryEditAdapter(this)
//            requestData(date)

        }

        item_adapter.setListener(checkbox_click_listener)
//        recordCategoryAdapter.setListener(checkbox_click_listener2)


        rv_item_record_cate.adapter = item_adapter
        //loadRecordCategoryDatas()

        //뒤로가기 버튼 누르면 화면 나가기
        img_back.setOnClickListener {
            finish()
       }

        //체크박스 선택시 전체 체크박스 선택되도록
        checkBox_all.setOnClickListener {
            if(checkBox_all.isChecked){
//                item_adapter.isAllSelected = true
//                item_adapter.notifyDataSetChanged()
//                clicked_idx.addAll(sorted_item.map { it.itemIdx })
//                Log.d("####RecordCategoryActivity",clicked_idx.toString())
//                clicked_idx.distinct()
                for(item in sorted_item){
                    datas_item.find{
                        it.itemIdx==item.itemIdx
                    }!!.isSelected=true
                    sorted_item.forEach {
                        it.isSelected=true
                    }
                    item_adapter.datas=sorted_item
                    item_adapter.notifyDataSetChanged()
                }
            }else{
                for(item in sorted_item){
                    datas_item.find{
                        it.itemIdx==item.itemIdx
                    }!!.isSelected=false
                    sorted_item.forEach {
                        it.isSelected=false
                    }
                    item_adapter.datas=sorted_item
                    item_adapter.notifyDataSetChanged()
                }
            }
        }

        /*// 클릭 시 아직 준비중입니다 토스트
        btn_move.setOnClickListener {
            showToast(this, "아직 준비중입니다")
        }*/
        //카테고리 추가 버튼 클릭 시 다이얼로그
        btn_add.setOnClickListener {
            val builder : AlertDialog.Builder = AlertDialog.Builder(this)
            val dialogView = layoutInflater.inflate(R.layout.layout_add_custom_dialog, null)

            builder.setView(dialogView)
            val dialog = builder.create()
            dialog.show()
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            val btn_positive = dialogView.findViewById<Button>(R.id.btn_positive)
            btn_positive.setOnClickListener {
                val category_name = dialogView.findViewById<EditText>(R.id.et_category_name)

                if(category_name.text.toString().isNotEmpty()){
                    RequestToServer.service.requestCategoryAdd(
                        SharedPreferenceController.getUserToken(this),
                        RequestCategoryAdd(
                            category_name.text.toString()
                        )
                    ).customEnqueue(
                        onSuccess = {
                            Log.d("#######","category add success")
                            datas_cate.add(RecordHomeCategoryInfo(
                                datas_cate.size, category_name.text.toString()
                            ))
                            category_adapter.datas = datas_cate
                            category_adapter.notifyDataSetChanged()

                        }
                    )
                }

                dialog.dismiss()
            }

            val btn_negative = dialogView.findViewById<Button>(R.id.btn_negative)
            btn_negative.setOnClickListener{
                dialog.dismiss()
            }

        }

        // 카테고리 이동 버튼 클릭 시
        btn_move.setOnClickListener {

            val items = datas_item.filter {
                it.isSelected
            }

            if(items.count()<=0){
                this.showCustomToast("선택된 재료가 없습니다")
            }else{

                val listener = object : RecordAddActivity.CategorySetListener {
                    override fun onSet(item: CategorySetInfo) {
                        val category_idx = item.categoryIdx
                        requestCategoryMove(category_idx)
                    }
                }

                val dialog = CategoryEditDialog()
                dialog.confirm_listener=listener
                dialog.title = "카테고리 이동"
                dialog.show(supportFragmentManager, null)
//            dialog.dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))


            }
        }

        // 카테고리 삭제 버튼 클릭 시
        btn_folder_delete.setOnClickListener {
            
            val listener = object : RecordAddActivity.CategorySetListener {
                override fun onSet(item: CategorySetInfo) {
                    val dialog = CustomDialog(this@RecordCateogyActivity)
                    dialog.setTitle("카테고리 삭제")
                    dialog.setContent("카테고리에 속해있는 내부 재료도 모두 삭제됩니다.\n" + "삭제하시겠습니까?")
                    dialog.setNegativeBtn("취소") { v -> dialog.dismissDialog() }
                    dialog.setPositiveBtn("확인"
                    ) {
                        var category_idx = item.categoryIdx
                        requestCategoryDelete(category_idx)
                        dialog.dismissDialog()
                    }
                    dialog.showDialog()
                }
            }

            val dialog = CategoryEditDialog()
            dialog.confirm_listener=listener
            dialog.title = "카테고리 삭제"
            dialog.show(supportFragmentManager, null)
        }

    }

    private fun requestCategoryDelete(categoryIdx: Int) {

        requestToServer.service.deleteCategory(
            SharedPreferenceController.getUserToken(this),
            categoryIdx
        ).customEnqueue(
            onSuccess = {
                datas_cate.filter{
                    it.categoryIdx!=categoryIdx
                }
//                recreate()
                requestData("")

            }
        )
    }

    private fun requestCategoryMove(categoryIdx: Int) {
        var list = mutableListOf<CategoryMove>()
//        for(i in clicked_idx){
//            list.add(CategoryMove(i, categoryIdx))
//
//        }
        val items = datas_item.filter {
            it.isSelected
        }
        for(item in items){
            list.add(CategoryMove(item.itemIdx, categoryIdx))
        }

        requestToServer.service.moveCategory(
            SharedPreferenceController.getUserToken(this),
            RequestRecordDelete(list)
        ).customEnqueue(
            onSuccess ={
//                Log.d("recordcategory move","${clicked_idx.toString()} move to ${categoryIdx}")
//                recreate()
                requestData("")
            }
        )
    }


    // data 가져옴
    // default 값으로?
    fun requestData(date: String){

        Log.d("#############","0")
        datas_cate = mutableListOf()
        datas_item = mutableListOf()
        requestToServer.service.getRecordHomeResponse(
            "0", SharedPreferenceController.getUserToken(this)
        ).customEnqueue(
            onSuccess = {

                //데이터 없을 경우 dialog


                if(it.data.itemInfo.isNullOrEmpty()){
                    this.showCustomToast("오늘 재고 기록하기를 완료한 이후에\n 재료 및 카테고리 편집을 이용하실 수 있습니다.")
                }


                for(data in it.data.categoryInfo){
                    datas_cate.add(data)
                }
                category_adapter.datas = datas_cate
//                category_adapter.selected_pos=0
                category_adapter.notifyDataSetChanged()
//                rv_category_record_cate.scrollToPosition(0)



                for(data in it.data.itemInfo){
                    data.isSelected=false
                    datas_item.add(data)
                }
                if(categoryIdx_cur>1){
                    sorted_item = datas_item.filter {item->
                        item.categoryIdx == categoryIdx_cur
                    }.toMutableList()
                }else{
                    sorted_item = datas_item
                }
                item_adapter.datas = sorted_item
                item_adapter.notifyDataSetChanged()

                // 아이템 개수 5개 미만이면 맨위로 가기 안보이게
                if(sorted_item.count()>=5){
                    tv_up.visibility = View.VISIBLE
                    img_up.visibility = View.VISIBLE
                }else{
                    tv_up.visibility = View.INVISIBLE
                    img_up.visibility = View.INVISIBLE
                }


            }
        )
    }

    private fun deleteRecordItem(items: List<Int>){


        requestToServer.service.deleteRecord(
            SharedPreferenceController.getUserToken(this),
            items.toString()
//        RequestRecordDelete(
//            clicked_idx
//        )
        ).customEnqueue(
            onSuccess = {
//                Log.d("recordcategory delete","${clicked_idx.toString()} deleted")
//                clicked_idx = mutableListOf()
//              clicked_pos = mutableListOf()
//            recordCategoryAdapter = RecordCategoryEditAdapter(this)
            requestData("")
            }
        )
    }

    interface CheckboxClickListener{
        fun onClick(idx : Int, pos : Int, isClicked : Boolean)
    }

}