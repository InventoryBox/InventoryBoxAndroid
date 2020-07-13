package com.example.inventorybox.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.inventorybox.R
import com.example.inventorybox.ViewHolder.RecordCategoryVH
import com.example.inventorybox.data.RecordCategoryData
import kotlinx.android.synthetic.main.activity_category_edit.*
import kotlinx.android.synthetic.main.item_record_category.view.*
import kotlinx.android.synthetic.main.item_record_edit.*

class RecordCategoryEditAdapter(private val context: Context) : RecyclerView.Adapter<RecordCategoryVH>() {
    var datas = mutableListOf<RecordCategoryData>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordCategoryVH {
        val view = LayoutInflater.from(context).inflate(R.layout.item_record_edit, parent, false)
        return RecordCategoryVH(
            view
        )

    }

    override fun getItemCount(): Int {
        return datas.size
    }

    override fun onBindViewHolder(holder: RecordCategoryVH, position: Int) {
        holder.bind(datas[position])

    }

}

class  RecordCategoryVH(itemView: View) : RecyclerView.ViewHolder(itemView){

    fun bind(data : String){
        itemView.tv_category_name.text=data
    }
    //selected일 때 변화
    fun set_selected(){
        itemView.setBackgroundResource(R.drawable.graph_rec20_dark_grey)
        itemView.tv_category_name.setTextColor(ContextCompat.getColor(itemView.context, R.color.white))
    }
    //unselect일 때의 변화
    fun set_unselected(){
        itemView.setBackgroundResource(R.drawable.graph_rec20_grey_blank)
        itemView.tv_category_name.setTextColor(ContextCompat.getColor(itemView.context, R.color.darkgrey))
    }

}