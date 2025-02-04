package com.inventorybox.inventorybox.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.inventorybox.inventorybox.R
import com.inventorybox.inventorybox.adapter.HomeTodayOrderAdapter
import com.inventorybox.inventorybox.data.HomeOrderData
import com.inventorybox.inventorybox.etc.HomeTodayRecyclerViewDecoration
import kotlinx.android.synthetic.main.fragment_home_view_pager.*

class HomeViewPager(var data: MutableList<HomeOrderData>) : Fragment() {

    lateinit var adapter :HomeTodayOrderAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = HomeTodayOrderAdapter(view.context)
        adapter.datas = data
        rv_home_today_order.adapter = adapter
        rv_home_today_order.addItemDecoration(HomeTodayRecyclerViewDecoration())

        rv_home_today_order.overScrollMode = View.OVER_SCROLL_NEVER
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home_view_pager, container, false)
    }

}