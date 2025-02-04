package com.inventorybox.inventorybox.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.inventorybox.inventorybox.DB.SharedPreferenceController
import com.inventorybox.inventorybox.R
import com.inventorybox.inventorybox.adapter.ExchangeRVAdapter
import com.inventorybox.inventorybox.data.PostInfo
import com.inventorybox.inventorybox.etc.HomeOrderRecyclerViewDecoration
import com.inventorybox.inventorybox.network.RequestToServer
import com.inventorybox.inventorybox.network.customEnqueue
import kotlinx.android.synthetic.main.fragment_exchange_all.*

class ExchangeAllFragment : Fragment() {

    lateinit var exchangeRVAdapter: ExchangeRVAdapter

    // 0-최신순, 1-거리순, 2-가격순
    var sort_idx = 0
    var datas = mutableListOf<PostInfo>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_exchange_all, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        exchangeRVAdapter = ExchangeRVAdapter(view.context)
        rv_exchange_all.adapter = exchangeRVAdapter
        rv_exchange_all.addItemDecoration(HomeOrderRecyclerViewDecoration())

        // 거리순, 최신순, 가격순 설정
        val listener_sort = View.OnClickListener{
            if(it==btn_sort_price){
                btn_sort_distance.categorySetUnClicked(view.context)
                btn_sort_recent.categorySetUnClicked(view.context)
                btn_sort_price.categorySetClicked(view.context)
                sort_idx = 2
            }else if(it==btn_sort_distance){
                btn_sort_distance.categorySetClicked(view.context)
                btn_sort_recent.categorySetUnClicked(view.context)
                btn_sort_price.categorySetUnClicked(view.context)
                sort_idx=1
            }else{
                btn_sort_distance.categorySetUnClicked(view.context)
                btn_sort_recent.categorySetClicked(view.context)
                btn_sort_price.categorySetUnClicked(view.context)
                sort_idx=0
            }
            loadData()
            rv_exchange_all.invalidate()
        }
        btn_sort_distance.setOnClickListener(listener_sort)
        btn_sort_recent.setOnClickListener(listener_sort)
        btn_sort_price.setOnClickListener(listener_sort)


        rv_exchange_all.setOverScrollMode(View.OVER_SCROLL_NEVER)
    }

    override fun onStart() {
        super.onStart()
        loadData()

    }

    fun loadData(){

        datas = arrayListOf()
        RequestToServer.service.requestExchangeHomeData(
            SharedPreferenceController.getUserToken(context!!),
            sort_idx
        ).customEnqueue(
            onSuccess = {
                for(data in it.data.postInfo){
                    datas.add(data)
                }
                exchangeRVAdapter.datas=datas
                exchangeRVAdapter.notifyDataSetChanged()
//                rv_exchange_all.invalidate()

            }
        )
        rv_exchange_all.invalidate()
    }

    fun TextView.categorySetClicked(context: Context){
        this.background = ContextCompat.getDrawable(context, R.drawable.rec18_yellow)
        this.setTextColor(context.getColor(R.color.white))
    }
    fun TextView.categorySetUnClicked(context: Context){
        this.background = null
        this.setTextColor(context.getColor(R.color.grey))
    }
}