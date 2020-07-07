package com.simplepro.todoandmemooffline.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.simplepro.todoandmemooffline.DB.DoneTodoDB
import com.simplepro.todoandmemooffline.R
import com.simplepro.todoandmemooffline.adapter.TrophyRecyclerViewAdapter
import com.simplepro.todoandmemooffline.instance.DoneTodoInstance
import kotlinx.android.synthetic.main.activity_trophy.*

class TrophyActivity : AppCompatActivity(), TrophyRecyclerViewAdapter.itemRemoveOnClickListener {

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

        if(trophyList.size >= 1)
        {
            Handler().postDelayed({
                trophyLottieAnimationLayout.visibility = View.GONE
            }, 100)
        }

        trophyAdapter = TrophyRecyclerViewAdapter(trophyList, this)

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

    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun itemRemove(view: View, position: Int) {
        if(trophyList.size == 0)
        {
            trophyLottieAnimationLayout.visibility = View.VISIBLE
        }
    }
}