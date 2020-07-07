package com.simplepro.todoandmemooffline.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.simplepro.todoandmemooffline.DB.DoneTodoDB
import com.simplepro.todoandmemooffline.R
import com.simplepro.todoandmemooffline.instance.DoneTodoInstance

class TrophyRecyclerViewAdapter(val trophyList: ArrayList<DoneTodoInstance>, val itemRemoveOnClick : itemRemoveOnClickListener)
    : RecyclerView.Adapter<TrophyRecyclerViewAdapter.CustomViewHolder>() {

    lateinit var trophyListDB : DoneTodoDB

    interface itemRemoveOnClickListener {
        fun itemRemove(view : View, position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        trophyListDB = Room.databaseBuilder(
            parent.context.applicationContext,
            DoneTodoDB::class.java, "doneTodo.db"
        ).allowMainThreadQueries()
            .build()
        val view = LayoutInflater.from(parent.context).inflate(R.layout.memo_todo_list_item, parent, false)
        return CustomViewHolder(view)
            .apply {
                removeButton.setOnClickListener {
                    trophyListDB.doneTodoDB().delete(trophyList[adapterPosition])
                    trophyList.removeAt(adapterPosition)
                    notifyItemRemoved(adapterPosition)
                    notifyItemChanged(adapterPosition, trophyList.size)
                    itemRemoveOnClick.itemRemove(it, adapterPosition)
                }
            }
    }

    override fun getItemCount(): Int {
        return trophyList.size
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        holder.todoTitleText.text = trophyList[position].doneTodo
    }

    class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val todoTitleText = itemView.findViewById<TextView>(R.id.memoTodoListTextView)
        val removeButton = itemView.findViewById<ImageView>(R.id.memoTodoListRemoveButton)
    }
}