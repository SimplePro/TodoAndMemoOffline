package com.simplepro.todoandmemooffline.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.simplepro.todoandmemooffline.DB.DoneTodoDB
import com.simplepro.todoandmemooffline.R
import com.simplepro.todoandmemooffline.adapter.TrophyRecyclerViewAdapter
import com.simplepro.todoandmemooffline.instance.DoneTodoInstance
import kotlinx.android.synthetic.main.activity_trophy.*

class TrophyActivity : AppCompatActivity() {

    lateinit var trophyListDB: DoneTodoDB
    lateinit var trophyAdapter : TrophyRecyclerViewAdapter
    var trophyList = arrayListOf<DoneTodoInstance>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        trophyListDB = Room.databaseBuilder(
            applicationContext,
            DoneTodoDB::class.java, "doneTodo.db"
        ).allowMainThreadQueries()
            .build()

        trophyList = trophyListDB.doneTodoDB().getAll() as ArrayList<DoneTodoInstance>

        trophyAdapter = TrophyRecyclerViewAdapter(trophyList)

        setContentView(R.layout.activity_trophy)

        trophyLeftButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        trophyRecyclerView.apply {
            adapter = trophyAdapter
            layoutManager = LinearLayoutManager(this@TrophyActivity, LinearLayoutManager.VERTICAL, false)
            setHasFixedSize(true)
        }

    }
}