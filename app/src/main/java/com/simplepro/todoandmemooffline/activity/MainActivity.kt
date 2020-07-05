package com.simplepro.todoandmemooffline.activity

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.simplepro.secondtodoandmemo.adapter.MemoRecyclerViewAdapter
import com.simplepro.secondtodoandmemo.adapter.TodoRecyclerViewAdapter
import com.simplepro.secondtodoandmemo.instance.MemoInstance
import com.simplepro.todoandmemooffline.DB.DoneTodoDB
import com.simplepro.todoandmemooffline.DB.MemoDB
import com.simplepro.todoandmemooffline.DB.TodoDB
import com.simplepro.todoandmemooffline.R
import com.simplepro.todoandmemooffline.adapter.MemoTodoRecyclerViewAdapter
import com.simplepro.todoandmemooffline.instance.DoneTodoInstance
import com.simplepro.todoandmemooffline.instance.TodoInstance
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ThreadLocalRandom
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity(), TodoRecyclerViewAdapter.todoItemClickListener,
    MemoRecyclerViewAdapter.memoItemClickListener,
    MemoTodoRecyclerViewAdapter.memoItemViewOnClickListener {

    //변수 선언
    var todoList: ArrayList<TodoInstance> = arrayListOf()
    var memoList: ArrayList<MemoInstance> = arrayListOf()
    var DoneTodoList: ArrayList<DoneTodoInstance> = arrayListOf()
    //LottieAnimation의 VISIBLE을 정해주기 위해서 선언하는 변수 (false면 VISIBLE true 면 GONE)
    var todoLottieAnimationVisibleForm = false
    var memoLottieAnimationVisibleForm = false
    //중복 LottieAnimation을 방지하기 위함. 또는 다른 여러가지 클릭 이벤트에서 사용됨.
    var tabMenuBoolean = "TODO"

    //메모 리스트에 들어갈 날짜 항목
    var currentTime: Date = Calendar.getInstance().getTime()
    var date_text: String = "null"

    var lottieAnimationAlphaAnimation : Animation? = null
    var startLottieAnimationAlphaAnimation: Animation? = null

    lateinit var memoDialog: AlertDialog.Builder
    lateinit var memoEdialog: LayoutInflater
    lateinit var memoMView: View
    lateinit var memoBuilder: AlertDialog

    lateinit var memoTitleTextDialog: EditText
    lateinit var memoContentTextDialog: EditText
    lateinit var memoListLayoutDialog: ConstraintLayout
    lateinit var memoPlanConstraintLayoutDialog : ConstraintLayout
    lateinit var memoPlanRecyclerViewLayoutDialog : RecyclerView
    lateinit var memoPlanCancelButtonDialog : ImageView
    lateinit var memoPlanResetTextViewDialog : TextView
    lateinit var memoPlanTextDialog : TextView
    lateinit var memoSaveButtonDialog : Button
    lateinit var memoCancelButtonDialog : Button

    var memoTitleText : String = ""
    var memoContentText : String = ""
    var memoPlanText : String = ""

    var todoTitleText : String = ""
    var todoContentText : String = ""

    lateinit var OutRightSlideAnimation: Animation
    lateinit var InRightSlideAnimation: Animation
    lateinit var OutLeftSlideAnimation: Animation
    lateinit var InLeftSlideAnimation: Animation

    lateinit var memoAdapter : MemoRecyclerViewAdapter
    lateinit var todoAdapter : TodoRecyclerViewAdapter
    lateinit var memoTodoAdapter : MemoTodoRecyclerViewAdapter

    var memoSearchList : ArrayList<MemoInstance> = arrayListOf()
    var todoSearchList : ArrayList<TodoInstance> = arrayListOf()

    lateinit var todoId: String
    var todoIdBoolean : Boolean = false
    lateinit var memoId: String
    var memoIdBoolean : Boolean = false

    lateinit var todoDB : TodoDB
    lateinit var memoDB : MemoDB
    lateinit var doneTodoDB : DoneTodoDB


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //변수 정의
        lottieAnimationAlphaAnimation = AnimationUtils.loadAnimation(this, R.anim.lottie_animation_alpha_animation)
        startLottieAnimationAlphaAnimation = AnimationUtils.loadAnimation(this, R.anim.lottie_animation_alpha_animation2)

        OutRightSlideAnimation = AnimationUtils.loadAnimation(this, R.anim.out_right_slide_animation)
        InRightSlideAnimation = AnimationUtils.loadAnimation(this,  R.anim.in_right_slide_animation)

        OutLeftSlideAnimation = AnimationUtils.loadAnimation(this, R.anim.out_left_slide_animation)
        InLeftSlideAnimation = AnimationUtils.loadAnimation(this,  R.anim.in_left_slide_animation)

        memoTodoAdapter = MemoTodoRecyclerViewAdapter(DoneTodoList, this, this)
        memoAdapter = MemoRecyclerViewAdapter(memoList, memoSearchList,this)
        todoAdapter = TodoRecyclerViewAdapter(todoList, DoneTodoList,this, todoSearchList)

        todoDB = Room.databaseBuilder(
            applicationContext,
            TodoDB::class.java, "todo.db"
        ).allowMainThreadQueries()
            .build()

        memoDB = Room.databaseBuilder(
            applicationContext,
            MemoDB::class.java, "memo.db"
        ).allowMainThreadQueries()
            .build()

        doneTodoDB = Room.databaseBuilder(
            applicationContext,
            DoneTodoDB::class.java, "doneTodo.db"
        ).allowMainThreadQueries()
            .build()

        DoneTodoList = doneTodoDB.doneTodoDB().getAll() as ArrayList<DoneTodoInstance>

        //리사이클러뷰와 어답터를 연결해주는 메소드를 호출함.
        bridgeRecyclerViewAndAdapter()

        //투두 데이터, 메모 데이터, 던투두 데이터를 파이어베이스에서 가져오는 메소드를 호출함.
//        bringTodoAndMemoAndDoneTodoDataToFirebase()

        //로티 애니메이션의 visible 을 조정해주는 메소드를 호출함.
        controlLottieAnimationVisible()

        //추가 버튼이 클릭되었을 때.
        addButton.setOnClickListener {

            //만일 이용자가 TODO를 클릭한 상태라면
            if(tabMenuBoolean == "TODO") {
                //Dialog 띄어줌
                todoDialogDeclaration()
            }

            //만일 사용자가 MEMO 버튼을 누른 상태라면
            else if(tabMenuBoolean == "MEMO") {
                //Dialog 띄어줌.
                memoDialogDeclaration()
            }
        }

        //투두나 메모 탭이 클릭되었을 때 행동하는 메소드를 호출함.
        selectTab()

        //검색에 관하여 처리하는 메소드를 호출함.
        searchSomebody()

        //개발자 소개를 눌렀을 때.
        introduceDeveloperTextView.setOnClickListener {
            val intent = Intent(this, IntroduceDeveloperActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    //todoItem 이 remove 되었을 때 todoLottieAnimation 의 visibility 를 조정하는 콜백 메소드
    override fun todoOnItemClick(view: View, position: Int) {
        Log.d("TAG", "MainActivity.todoOnItemClick - todoOnItemClick")
        loadTodoIdData()
        loop@ for(i in 0 .. todoList.size - 1)
        {
            if(todoList[i].todoId == todoId)
            {
                todoDB.todoDao().delete(todoList[i])
                todoList.removeAt(i)
                todoSearchView.setQuery("", false)
                todoSearchView.clearFocus()
                todoAdapter.notifyItemRemoved(i)
                todoAdapter.notifyItemChanged(i, todoList.size)
                break@loop
            }
        }
        if(todoList.size == 0)
        {
            Log.d("TAG", "todoList size is 0")
            todoLottieAnimationLayout.visibility = View.VISIBLE
            todoLottieAnimationLayout.startAnimation(startLottieAnimationAlphaAnimation)
        }
    }

    //todoItem 들의 정보를 수정해주는 콜백 메소드.
    override fun todoOnItemReplaceClick(view: View, position: Int) {
        val dialog = AlertDialog.Builder(this)
        val edialog: LayoutInflater = LayoutInflater.from(this)
        val mView: View = edialog.inflate(R.layout.todo_add_dialog, null)
        val builder: AlertDialog = dialog.create()

        val todoText = mView.findViewById<EditText>(R.id.todoEditTextDialog)
        val contentText = mView.findViewById<EditText>(R.id.contentEditTextDialog)
        val todoButton = mView.findViewById<Button>(R.id.todoButtonDialog)
        val cancelTodoButton = mView.findViewById<Button>(R.id.CancelTodoButtonDialog)

        loadTodoTitleAndContentTextData()
        loadTodoIdData()

        todoText.setText(todoTitleText)
        contentText.setText(todoContentText)

        todoTitleText = ""
        todoContentText = ""

        builder.setView(mView)
        builder.show()

        //수정 버튼이 클릭되었을 때
        todoButton.setOnClickListener {
            for(i in 0 .. todoList.size - 1)
            {
                if(todoList[i].todoId == todoId)
                {
                    todoList.set(i,
                        TodoInstance(
                            todoText.text.toString(),
                            contentText.text.toString(),
                            todoList[i].todoId
                        )
                    )
                    todoDB.todoDao().update(TodoInstance(todoText.text.toString(), contentText.text.toString(), todoList[i].todoId))
                    todoSearchView.setQuery("", false)
                    todoSearchView.clearFocus()
                }
            }
            todoAdapter.notifyDataSetChanged()
            builder.dismiss()
        }

        //닫기 버튼이 클릭되었을 때
        cancelTodoButton.setOnClickListener {
            builder.dismiss()
        }
    }

    //memoItem 이 remove 되었을 때 memoLottieAnimation 의 visibility 를 조정하는 콜백 메소드
    override fun memoOnItemClick(view: View, position: Int) {
        Log.d("TAG", "MainActivity.memoOnItemClick - memoOnItemClick")
        loadMemoIdData()
        loop@ for(i in 0 .. memoList.size - 1)
        {
            if(memoList[i].memoId == memoId)
            {
                memoDB.memoDao().delete(memoList[i])
                memoList.removeAt(i)
                memoSearchView.setQuery("", false)
                memoSearchView.clearFocus()
                memoAdapter.notifyItemRemoved(i)
                memoAdapter.notifyItemChanged(i, memoList.size)
                break@loop
            }
        }
        if(memoList.size == 0)
        {
            Log.d("TAG", "MainActivity.memoOnItemClick - memoList size is 0")
            memoLottieAnimationLayout.visibility = View.VISIBLE
            memoLottieAnimationLayout.startAnimation(startLottieAnimationAlphaAnimation)
        }
    }

    //memoItem 들의 정보를 수정해주는 콜백 메소드
    override fun memoItemReplaceClick(view: View, position: Int) {
        //변수 선언
        memoDialog = AlertDialog.Builder(this)
        memoEdialog = LayoutInflater.from(this)
        memoMView = memoEdialog.inflate(R.layout.memo_add_dialog, null)
        memoBuilder = memoDialog.create()

        memoTitleTextDialog = memoMView.findViewById<EditText>(R.id.memoTitleEditTextDialog)
        memoContentTextDialog = memoMView.findViewById<EditText>(R.id.memoContentEditTextDialog)
        memoListLayoutDialog = memoMView.findViewById<ConstraintLayout>(R.id.memoListLayoutDialog)
        memoPlanConstraintLayoutDialog = memoMView.findViewById<ConstraintLayout>(R.id.memoPlanLayoutDialog)
        memoPlanRecyclerViewLayoutDialog = memoMView.findViewById<RecyclerView>(R.id.memoPlanRecyclerViewDialog)
        memoPlanCancelButtonDialog = memoMView.findViewById<ImageView>(R.id.memoPlanCancelImageViewDialog)
        memoPlanTextDialog = memoMView.findViewById<TextView>(R.id.memoListPlanTextViewDialog)
        memoPlanResetTextViewDialog = memoMView.findViewById(R.id.memoPlanResetTextViewDialog)
        memoSaveButtonDialog = memoMView.findViewById<Button>(R.id.memoSaveButtonDialog)
        memoCancelButtonDialog = memoMView.findViewById<Button>(R.id.memoCancelButtonDialog)

        var currentTime: Date = Calendar.getInstance().getTime()
        val date_text: String = SimpleDateFormat("yyyy년 MM월 dd일 EE요일", Locale.getDefault()).format(currentTime)
        loadMemoTitleAndContentTextData()
        loadMemoIdData()

        memoTitleTextDialog.setText("${memoTitleText}")
        memoContentTextDialog.setText("${memoContentText}")

        memoTitleText = ""
        memoContentText = ""


        loop@ for(i in 0 .. memoList.size - 1) {
            if(memoList[i].memoId == memoId)
            {
                if(memoList[i].memoPlan != "" || memoList[i].memoPlan != "무슨 계획을 한 후에 쓰는 메모인가요? (선택)")
                {
                    memoPlanText = memoList[i].memoPlan
                    break@loop
                }
                else {
                    memoPlanText = ""
                }
            }
        }

        if(memoPlanText != "")
        {
            memoPlanTextDialog.setText("${memoPlanText}")
        }
        else if(memoPlanText == "" || memoPlanText == "무슨 계획을 한 후에 쓰는 메모인가요? (선택)")
        {
            memoPlanTextDialog.setText("무슨 계획을 한 후에 쓰는 메모인가요? (선택)")
        }

        memoBuilder.setView(memoMView)
        memoBuilder.show()

        //memoList 저장 버튼
        memoSaveButtonDialog.setOnClickListener {
            Log.d("TAG", "MainActivity.memoItemReplaceClick - memoButton is pressed")
            for(i in 0 .. memoList.size - 1)
            {
                if(memoList[i].memoId == memoId)
                {
                    memoList.set(i,
                        MemoInstance(
                            memoTitleTextDialog.text.toString(),
                            memoContentTextDialog.text.toString(),
                            date_text,
                            "${memoPlanText}",
                            memoList[i].memoId
                        )
                    )
                    memoDB.memoDao().update(MemoInstance(memoTitleTextDialog.text.toString(), memoContentTextDialog.text.toString(), date_text, memoPlanText, memoList[i].memoId))
                    memoSearchView.setQuery("", false)
                    memoSearchView.clearFocus()
                }
            }
            memoAdapter.notifyDataSetChanged()
            Log.d("TAG", "MainActivity.memoItemReplaceClick - memoList of size : ${memoList.size}")
            memoBuilder.dismiss()
        }

        //Dialog 닫기 버튼
        memoCancelButtonDialog.setOnClickListener {
            Log.d("TAG", "MainActivity.memoItemReplaceClick - memoCancelButton is pressed")
            memoBuilder.dismiss()
        }

        //RecyclerView와 같이 나타나는 닫기 버튼 (X 버튼)
        memoPlanCancelButtonDialog.setOnClickListener {
            memoListLayoutDialog.visibility = View.VISIBLE
            memoPlanConstraintLayoutDialog.visibility = View.GONE
        }

        //memoList 의 planText 초기화 텍스트가 눌렸을 때
        memoPlanResetTextViewDialog.setOnClickListener {
            memoPlanText = ""
            memoPlanTextDialog.setText("무슨 계획을 한 후에 쓰는 메모인가요? (선택)")
            memoListLayoutDialog.visibility = View.VISIBLE
            memoPlanConstraintLayoutDialog.visibility = View.GONE
        }

        // 무슨 계획을 한 후에 쓰는 메모인가요? (선택)
        memoPlanTextDialog.setOnClickListener {
            memoListLayoutDialog.visibility = View.INVISIBLE
            memoPlanConstraintLayoutDialog.visibility = View.VISIBLE
            memoPlanRecyclerViewLayoutDialog.adapter =
                MemoTodoRecyclerViewAdapter(DoneTodoList,this,this)
            memoPlanRecyclerViewLayoutDialog.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            memoPlanRecyclerViewLayoutDialog.setHasFixedSize(true)
        }

        //Dialog 안에 있는 RecyclerView
        memoPlanRecyclerViewLayoutDialog.setOnClickListener {
            //RecyclerView 에서 아이템을 클릭했을 때 이벤트를 어떻게 구현할지 생각해야함.
            memoListLayoutDialog.visibility = View.VISIBLE
            memoPlanConstraintLayoutDialog.visibility = View.GONE
        }
    }

    //memoPlanText 를 조정해주는 콜백 메소드
    override fun memoItemViewOnClick(view: View, position: Int) {
        memoListLayoutDialog.visibility = View.VISIBLE
        memoPlanConstraintLayoutDialog.visibility = View.GONE
        loadMemoPlanTextData()
        if(memoPlanText != "")
        {
            memoPlanTextDialog.setText("${memoPlanText}")
        }
    }

    //memoPlanText 를 쉐어드로 저장했었는데 그 값을 받아와서 조정하는 메소드
    private fun loadMemoPlanTextData(){
        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        val memoPlanTextShared = pref.getString("memoPlanText", "")

        if(memoPlanTextShared != "" && memoPlanTextShared != "무슨 계획을 한 후에 쓰는 메모인가요? (선택)")
        {
            memoPlanText = memoPlanTextShared.toString()
        }
    }

    //memo 의 타이틀과 내용을 가져오기 위한 메소드.
    private fun loadMemoTitleAndContentTextData(){
        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        val memoTitleTextShared = pref.getString("memoTitleText", "")
        val memoContentTextShared = pref.getString("memoContentText", "")

        if(memoTitleTextShared != "")
        {
            memoTitleText = memoTitleTextShared.toString()
//            Log.d("TAG", "memoTitleTextShared is $memoTitleTextShared")
//            Log.d("TAG", "memoTitleText is $memoTitleText")
        }
        if(memoContentTextShared != "")
        {
            memoContentText = memoContentTextShared.toString()
//            Log.d("TAG", "memoContentTextShared is $memoContentTextShared")
//            Log.d("TAG", "memoContextText is $memoContentText")
        }
    }

    //memo 의 아이디를 가져오기 위한 메소드.
    private fun loadMemoIdData(){
        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        val memoIdShared = pref.getString("memoId", "")


        if(memoIdShared != "")
        {
            memoId = memoIdShared.toString()
        }

    }

    //to do 의 타이틀과 상세내용을 가져오기 위한 메소드.
    private fun loadTodoTitleAndContentTextData(){
        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        val todoTitleTextShared = pref.getString("todoTitleText", "")
        val todoContentTextShared = pref.getString("todoContentText", "")

        if(todoTitleTextShared != "")
        {
            todoTitleText = todoTitleTextShared.toString()
        }
        if(todoContentTextShared != "")
        {
            todoContentText = todoContentTextShared.toString()
        }
    }

    //to do 의 아이디를 가져오기 위한 메소드.
    private fun loadTodoIdData() {
        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        val todoIdShared = pref.getString("todoId", "")

        if(todoIdShared != "")
        {
            todoId = todoIdShared.toString()
        }
    }

    //todoDialog 메소드
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun todoDialogDeclaration() {
        val todoDialog = AlertDialog.Builder(this)
        val todoEdialog: LayoutInflater = LayoutInflater.from(this)
        val todoMView: View = todoEdialog.inflate(R.layout.todo_add_dialog, null)
        val todoBuilder: AlertDialog = todoDialog.create()

        val todoText = todoMView.findViewById<EditText>(R.id.todoEditTextDialog)
        val contentText = todoMView.findViewById<EditText>(R.id.contentEditTextDialog)
        val todoButton = todoMView.findViewById<Button>(R.id.todoButtonDialog)
        val cancelTodoButton = todoMView.findViewById<Button>(R.id.CancelTodoButtonDialog)

        todoBuilder.setView(todoMView)
        todoBuilder.show()

        todoButton.setOnClickListener {
            Log.d("TAG", "MainActivity.todoDialogDeclaration - todoButton is pressed")

            makeTodoIdAndSaveTodoData(todoText.text.toString(), contentText.text.toString(), todoBuilder)
            //만일 todoList의 아이템을 추가했을 때 todoList 의 사이즈가 1이면 todoLottieAnimationVisibleForm 을 true 로 바꾸어 주어 LottieAnimation 의 Visible 을 조정해주어야 함.
            if (todoList.size == 1) {
                todoLottieAnimationVisibleForm = true
                if(todoRecyclerView.visibility == View.GONE)
                {
                    todoRecyclerView.visibility = View.VISIBLE
                }
            }
            //만일 todoLottieAnimationVisibleForm 이 true 이면 todoLottieAnimationView를 애니메이션고 함께 자연스럽게 GONE 으로 바꾸어 줌.
            if (todoLottieAnimationVisibleForm == true) {
                todoLottieAnimationLayout.startAnimation(lottieAnimationAlphaAnimation)
                Handler().postDelayed({
                    todoLottieAnimationLayout.visibility = View.GONE
                    todoLottieAnimationVisibleForm = false
                }, 500)
            }
        }
        //닫기 버튼이 클릭되었을 때
        cancelTodoButton.setOnClickListener {
            Log.d("TAG", "MainActivity.todoDialogDeclaration - todoCancelButton is pressed")
            todoBuilder.dismiss()
        }
    }

    //memoDialog 메소드
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun memoDialogDeclaration() {
        //필요한 변수 선언
        memoDialog = AlertDialog.Builder(this)
        memoEdialog = LayoutInflater.from(this)
        memoMView = memoEdialog.inflate(R.layout.memo_add_dialog, null)
        memoBuilder = memoDialog.create()

        memoTitleTextDialog = memoMView.findViewById<EditText>(R.id.memoTitleEditTextDialog)
        memoContentTextDialog = memoMView.findViewById<EditText>(R.id.memoContentEditTextDialog)
        memoListLayoutDialog = memoMView.findViewById<ConstraintLayout>(R.id.memoListLayoutDialog)
        memoPlanConstraintLayoutDialog = memoMView.findViewById<ConstraintLayout>(R.id.memoPlanLayoutDialog)
        memoPlanRecyclerViewLayoutDialog = memoMView.findViewById<RecyclerView>(R.id.memoPlanRecyclerViewDialog)
        memoPlanResetTextViewDialog = memoMView.findViewById(R.id.memoPlanResetTextViewDialog)
        memoPlanCancelButtonDialog = memoMView.findViewById<ImageView>(R.id.memoPlanCancelImageViewDialog)
        memoPlanTextDialog = memoMView.findViewById<TextView>(R.id.memoListPlanTextViewDialog)
        memoSaveButtonDialog = memoMView.findViewById<Button>(R.id.memoSaveButtonDialog)
        memoCancelButtonDialog = memoMView.findViewById<Button>(R.id.memoCancelButtonDialog)

        memoBuilder.setView(memoMView)
        memoBuilder.show()

        memoPlanTextDialog.setText("무슨 계획을 한 후에 쓰는 메모인가요? (선택)")

        memoPlanText = ""

        //저장하기 버튼을 눌렀을 때
        memoSaveButtonDialog.setOnClickListener {
            Log.d("TAG", "MainActivity.memoDialogDeclaration - memoButton is pressed")
            date_text = SimpleDateFormat("yyyy년 MM월 dd일 EE요일", Locale.getDefault()).format(currentTime)
            makeMemoIdAndSaveMemoData(memoTitleTextDialog.text.toString(), memoContentTextDialog.text.toString(), date_text, memoPlanText, memoBuilder)
            //만일 memoList 의 사이즈가 1이라면 memoLottieAnimationVisibleForm 을 true 로 바꾸어 주어 memoLottieAnimationView 를 GONE 으로 바꾸어 주어야 함.
            if (memoList.size == 1) {
                memoLottieAnimationVisibleForm = true
                if(memoRecyclerView.visibility == View.GONE)
                {
                    memoRecyclerView.visibility = View.VISIBLE
                }
            }
            //만일 memoLottieAnimationVisibleForm 이 true 이면 애니메이션과 함께 memoLottieAnimationView 를 GONE 으로 바꾸어 줌.
            if (memoLottieAnimationVisibleForm == true) {
                memoLottieAnimationLayout.startAnimation(lottieAnimationAlphaAnimation)
                Handler().postDelayed({
                    memoLottieAnimationLayout.visibility = View.GONE
                    memoLottieAnimationVisibleForm = false
                }, 500)
            }
        }

        //닫기 버튼이 눌렸을 때
        memoCancelButtonDialog.setOnClickListener {
            Log.d("TAG", "MainActivity.memoDialogDeclaration - memoCancelButton is pressed")
            memoBuilder.dismiss()
        }

        //RecyclerView 와 같이 나오는 닫기 버튼 (X 버튼)
        memoPlanCancelButtonDialog.setOnClickListener {
            memoListLayoutDialog.visibility = View.VISIBLE
            memoPlanConstraintLayoutDialog.visibility = View.GONE
        }

        //memoList 의 planText 초기화 텍스트가 눌렸을 때
        memoPlanResetTextViewDialog.setOnClickListener {
            memoPlanText = ""
            memoPlanTextDialog.setText("무슨 계획을 한 후에 쓰는 메모인가요? (선택)")
            memoListLayoutDialog.visibility = View.VISIBLE
            memoPlanConstraintLayoutDialog.visibility = View.GONE
        }

        //무슨 계획을 한 후에 쓰는 메모인가요? (선택) 이 눌렸을 때
        memoPlanTextDialog.setOnClickListener {
            memoListLayoutDialog.visibility = View.INVISIBLE
            memoPlanConstraintLayoutDialog.visibility = View.VISIBLE
            memoPlanRecyclerViewLayoutDialog.adapter =
                MemoTodoRecyclerViewAdapter(
                    DoneTodoList,
                    this,
                    this
                )
            memoPlanRecyclerViewLayoutDialog.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            memoPlanRecyclerViewLayoutDialog.setHasFixedSize(true)
        }

        //메모 Dialog 안에 있는 RecylerView 가 눌렸을 때.
        memoPlanRecyclerViewLayoutDialog.setOnClickListener {
            memoListLayoutDialog.visibility = View.VISIBLE
            memoPlanConstraintLayoutDialog.visibility = View.GONE
        }
    }

    //투두아이디를 생성하고 FireStore 에 투두 데이터를 저장하는 메소드.
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun makeTodoIdAndSaveTodoData(todoText: String, contentText : String, todoBuilder: AlertDialog) {
        //투두 아이디 주는 것.
        todoId = ThreadLocalRandom.current().nextInt(1000000, 9999999).toString()
        Log.d("TAG", "todoId is ${todoId}")

        //만일 투두리스트가 비었다면 그냥 바로 추가하기
        if(todoList.isEmpty())
        {
            todoList.add(
                TodoInstance(
                    todoText,
                    contentText,
                    todoId
                )
            )

            todoDB.todoDao().insert(TodoInstance(todoText, contentText, todoId))
            Log.d("TAG", "todoDB is ${todoDB.todoDao().getAll()}")
            Log.d("TAG", "MainActivity.todoDialogDeclaration - todoList of size : ${todoList.size}")
            todoAdapter.notifyDataSetChanged()
            todoBuilder.dismiss()
        }
        //만일 투두리스트가 비지 않았다면.
        else if(todoList.isNotEmpty())
        {
            for(i in 0 .. todoList.size - 1)
            {
                if (todoList[i].todoId == todoId) {
                    todoIdBoolean = true
                }
            }
            if (todoIdBoolean == false)
            {
                todoList.add(
                    TodoInstance(
                        todoText,
                        contentText,
                        todoId
                    )
                )

                todoDB.todoDao().insert(TodoInstance(todoText, contentText, todoId))
                Log.d("TAG", "MainActivity.todoDialogDeclaration - todoList of size : ${todoList.size}")
                todoAdapter.notifyDataSetChanged()
                todoBuilder.dismiss()
            }
            else if(todoIdBoolean == true)
            {
                todoIdBoolean = false
                todoId = ThreadLocalRandom.current().nextInt(1000000, 9999999).toString()
                Log.d("TAG", "todoId is ${todoId}")
                for(i in 0 .. todoList.size - 1)
                {
                    if (todoList[i].todoId == todoId) {
                        todoIdBoolean = true
                    }
                }
                if(todoIdBoolean == false)
                {
                    todoList.add(
                        TodoInstance(
                            todoText,
                            contentText,
                            todoId
                        )
                    )

                    todoDB.todoDao().insert(TodoInstance(todoText, contentText, todoId))
                    Log.d("TAG", "MainActivity.todoDialogDeclaration - todoList of size : ${todoList.size}")
                    todoAdapter.notifyDataSetChanged()
                    todoBuilder.dismiss()
                    todoIdBoolean = false
                }
                else if(todoIdBoolean == true)
                {
                    todoIdBoolean = false
                    todoId = ThreadLocalRandom.current().nextInt(1000000, 9999999).toString()
                    Log.d("TAG", "todoId is ${todoId}")
                    for(i in 0 .. todoList.size - 1)
                    {
                        if (todoList[i].todoId == todoId) {
                            todoIdBoolean = true
                        }
                    }
                    if(todoIdBoolean == false)
                    {
                        todoList.add(
                            TodoInstance(
                                todoText,
                                contentText,
                                todoId
                            )
                        )

                        todoDB.todoDao().insert(TodoInstance(todoText, contentText, todoId))
                        Log.d("TAG", "MainActivity.todoDialogDeclaration - todoList of size : ${todoList.size}")
                        todoAdapter.notifyDataSetChanged()
                        todoBuilder.dismiss()
                        todoIdBoolean = false
                    }
                    else if(todoIdBoolean == true)
                    {
                        todoIdBoolean = false
                        Toast.makeText(applicationContext, "다시 한번 시도해주세요.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    //메모아이디를 생성하는 FireStore 에 메모 데이터를 저장하는 메소드.
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun makeMemoIdAndSaveMemoData(memoTitle: String, memoContent: String, date: String, memoPlan: String, memoBuilder: AlertDialog) {

        //메모 아이디 주는 것.
        memoId = ThreadLocalRandom.current().nextInt(1000000, 9999999).toString()
        Log.d("TAG", "memoId is ${memoId}")
        //만일 메모리스트가 비었다면 그냥 바로 추가하기
        if(memoList.isEmpty())
        {
            memoList.add(MemoInstance(memoTitle, memoContent, date, "${memoPlan}", memoId)
            )
            memoDB.memoDao().insert(MemoInstance(memoTitle, memoContent, date, memoPlan, memoId))
            Log.d("TAG", "MainActivity.memoDialogDeclaration - memoList of size : ${memoList.size}")
            memoAdapter.notifyDataSetChanged()
            memoBuilder.dismiss()
        }
        //만일 메모리스트가 비지 않았다면.
        else if(memoList.isNotEmpty())
        {
            for(i in 0 .. memoList.size - 1)
            {
                if (memoList[i].memoId == memoId) {
                    memoIdBoolean = true
                }
            }
            if (memoIdBoolean == false)
            {
                memoList.add(MemoInstance(memoTitle, memoContent, date,"${memoPlan}", memoId)
                )
                memoDB.memoDao().insert(MemoInstance(memoTitle, memoContent, date, memoPlan, memoId))
                Log.d("TAG", "MainActivity.memoDialogDeclaration - memoList of size : ${memoList.size}")
                memoAdapter.notifyDataSetChanged()
                memoBuilder.dismiss()
            }
            else if(memoIdBoolean == true)
            {
                memoIdBoolean = false
                memoId = ThreadLocalRandom.current().nextInt(1000000, 9999999).toString()
                Log.d("TAG", "memoId is ${memoId}")
                for(i in 0 .. memoList.size - 1)
                {
                    if (memoList[i].memoId == memoId) {
                        memoIdBoolean = true
                    }
                }
                if(memoIdBoolean == false)
                {
                    memoList.add(MemoInstance(
                            memoTitle,
                            memoContent,
                            date,
                            "${memoPlan}",
                            memoId
                        )
                    )
                    memoDB.memoDao().insert(MemoInstance(memoTitle, memoContent, date, memoPlan, memoId))
                    Log.d("TAG", "MainActivity.memoDialogDeclaration - memoList of size : ${memoList.size}")
                    memoAdapter.notifyDataSetChanged()
                    memoBuilder.dismiss()
                }
                else if(memoIdBoolean == true)
                {
                    memoIdBoolean = false
                    memoId = ThreadLocalRandom.current().nextInt(1000000, 9999999).toString()
                    Log.d("TAG", "memoId is ${memoId}")
                    for(i in 0 .. memoList.size - 1)
                    {
                        if (memoList[i].memoId == memoId) {
                            memoIdBoolean = true
                        }
                    }
                    if(memoIdBoolean == false)
                    {
                        memoList.add(MemoInstance(
                                memoTitle,
                                memoContent,
                                date,
                                "${memoPlan}",
                                memoId
                            )
                        )
                        memoDB.memoDao().insert(MemoInstance(memoTitle, memoContent, date, memoPlan, memoId))
                        Log.d("TAG", "MainActivity.memoDialogDeclaration - memoList of size : ${memoList.size}")
                        memoAdapter.notifyDataSetChanged()
                        memoBuilder.dismiss()
                    }
                    else if(memoIdBoolean == true)
                    {
                        memoIdBoolean = false
                        Toast.makeText(applicationContext, "다시 한번 시도해주세요.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    //검색에 관련된 것들을 담아놓은 메소드.
    private fun searchSomebody() {

        //검색에 대하여 view 쪽에서 작동하는 메소드를 호출함.
        searchViewAboutView()

        //검색의 setOnQueryText 를 처리하는 메소드를 호출함.
        searchViewSetOnQueryText()

    }

    //투두, 메모 둘 중에 하나의 탭이 선택되었을 때
    private fun selectTab() {
        //Menu 에 있는 todo버튼이 눌렸을 때
        tabMenuTodoLayout.setOnClickListener {

            //이미 TODO버튼이 눌린 상태라면
            if (tabMenuBoolean == "TODO") {
                Log.d("TAG", "MainActivity.onCreate - tabMenuBoolean is TODO")
            }

            //tabMenuBoolean 이 TODO가 아니고, todoList의 사이즈가 0이라면
            else if (todoList.size == 0) {
                //todoSearchView 를 비운다.
                todoSearchView.setQuery("", false)
                todoSearchView.clearFocus()
                //todoRecyclerView는 보여주고 memoRecyclerView는 안 보여준다.
                if (todoRecyclerView.visibility != View.GONE)
                    todoRecyclerView.visibility = View.GONE
                if (memoRecyclerView.visibility != View.GONE)
                    memoRecyclerView.visibility = View.GONE
                //tabMenuBoolean 의 값을 TODO로 만들어주어 TODO가 클릭 됬음을 표시한다.
                tabMenuBoolean = "TODO"
                //TODO가 선택됬음을 사용자에게 알리기 위해 보기 좋게 Background 를 변경한다.
                tabMenuTodoLayout.setBackgroundResource(R.drawable.selected_tab_menu_background)
                tabMenuMemoLayout.setBackgroundResource(R.drawable.rectangle_tab_menu_background)
                stateTextView.text = "TODO"
                //todoLottieAnimationLayout 을 애니메이션과 함께 자연스럽게 보여준다.
                todoLottieAnimationLayout.visibility = View.VISIBLE
                todoLottieAnimationLayout.startAnimation(startLottieAnimationAlphaAnimation)
                memoSearchViewLayout.visibility = View.GONE
                searchImageView.visibility = View.VISIBLE
                titleTextViewBottomLinearLayout.visibility = View.VISIBLE


                //만일 memoList 의 사이즈가 0이라면
                if (memoList.size == 0) {
                    //memoLottieAnimationView 를 애니메이션과 함께 자연스럽게 GONE 으로 바꿈.
                    memoLottieAnimationLayout.startAnimation(lottieAnimationAlphaAnimation)
                    Handler().postDelayed({
                        memoLottieAnimationLayout.visibility = View.GONE
                    }, 500)
                }
            }
            //만일 todoList 의 사이즈가 0보다 크다면
            else {
                todoSearchView.setQuery("", false)
                todoSearchView.clearFocus()
                //todoRecyclerView는 보여주고 memoRecyclerView는 안 보여준다.
                if (todoRecyclerView.visibility == View.GONE)
                    todoRecyclerView.visibility = View.VISIBLE
                if (memoRecyclerView.visibility != View.GONE)
                    memoRecyclerView.visibility = View.GONE
                tabMenuBoolean = "TODO"
                tabMenuTodoLayout.setBackgroundResource(R.drawable.selected_tab_menu_background)
                tabMenuMemoLayout.setBackgroundResource(R.drawable.rectangle_tab_menu_background)
                stateTextView.text = "TODO"
                memoSearchViewLayout.visibility = View.GONE
                searchImageView.visibility = View.VISIBLE
                titleTextViewBottomLinearLayout.visibility = View.VISIBLE

                if (memoList.size == 0) {
                    memoLottieAnimationLayout.startAnimation(lottieAnimationAlphaAnimation)
                    Handler().postDelayed({
                        memoLottieAnimationLayout.visibility = View.GONE
                    }, 500)
                }
            }
        }

        //Menu 에 있는 MEMO 가 눌렸다면
        tabMenuMemoLayout.setOnClickListener {
            //만일 이미 MEMO 가 눌린 상태라면
            if(tabMenuBoolean == "MEMO") {
                Log.d("TAG", "MainActivity.onCreate - tabMenuBoolean is MEMO")
            }

            //memoList 의 사이즈가 0이라면
            else if(memoList.size == 0) {
                memoSearchView.setQuery("", false)
                memoSearchView.clearFocus()
                //todoRecyclerView 는 안보여주고, memoRecyclerView 는 보여준다.
                if (todoRecyclerView.visibility != View.GONE)
                    todoRecyclerView.visibility = View.GONE
                if (memoRecyclerView.visibility != View.GONE)
                    memoRecyclerView.visibility = View.GONE
                tabMenuBoolean = "MEMO"
                tabMenuMemoLayout.setBackgroundResource(R.drawable.selected_tab_menu_background)
                tabMenuTodoLayout.setBackgroundResource(R.drawable.rectangle_tab_menu_background)
                stateTextView.text = "MEMO"
                //memoLottieAnimationView 를 애니메이션과 함께 보여준다.
                memoLottieAnimationLayout.visibility = View.VISIBLE
                memoLottieAnimationLayout.startAnimation(startLottieAnimationAlphaAnimation)
                todoSearchViewLayout.visibility = View.GONE
                searchImageView.visibility = View.VISIBLE
                titleTextViewBottomLinearLayout.visibility = View.VISIBLE
                //todoList 의 사이즈가 0이라면
                if(todoList.size == 0)
                {
                    //todoLottieAnimationView 를 애니메이션과 함께 안 보여준다.
                    todoLottieAnimationLayout.startAnimation(lottieAnimationAlphaAnimation)
                    Handler().postDelayed({
                        todoLottieAnimationLayout.visibility = View.GONE
                    }, 500)
                }
            }

            //memoList 의 사이즈가 0보다 크다면
            else {
                memoSearchView.setQuery("", false)
                memoSearchView.clearFocus()
                //todoRecyclerView 는 안보여주고, memoRecyclerView 는 보여준다.
                if (todoRecyclerView.visibility != View.GONE)
                    todoRecyclerView.visibility = View.GONE
                if (memoRecyclerView.visibility == View.GONE)
                    memoRecyclerView.visibility = View.VISIBLE
                tabMenuBoolean = "MEMO"
                tabMenuTodoLayout.setBackgroundResource(R.drawable.rectangle_tab_menu_background)
                tabMenuMemoLayout.setBackgroundResource(R.drawable.selected_tab_menu_background)
                stateTextView.text = "MEMO"
                todoSearchViewLayout.visibility = View.GONE
                searchImageView.visibility = View.VISIBLE
                titleTextViewBottomLinearLayout.visibility = View.VISIBLE

                //만일 todoList 의 사이즈가 0이라면
                if(todoList.size == 0)
                {
                    //todoLottieAnimationView 를 애니메이션과 함께 안 보여준다.
                    todoLottieAnimationLayout.startAnimation(lottieAnimationAlphaAnimation)
                    Handler().postDelayed({
                        todoLottieAnimationLayout.visibility = View.GONE
                    }, 500)
                }
            }
        }
    }

    //searchView 관련해서 View 쪽에서 작동하는 메소드.
    private fun searchViewAboutView() {
        //검색하기 버튼이 눌렸을 때.
        searchImageView.setOnClickListener {
            if(tabMenuBoolean == "TODO")
            {
                titleTextViewBottomLinearLayout.startAnimation(OutRightSlideAnimation)
                searchImageView.startAnimation(OutRightSlideAnimation)
                todoSearchViewLayout.visibility = View.VISIBLE
                todoSearchViewLayout.startAnimation(InRightSlideAnimation)
                Handler().postDelayed({
                    titleTextViewBottomLinearLayout.visibility = View.INVISIBLE
                    searchImageView.visibility = View.INVISIBLE
                }, 500)
            }
            else if(tabMenuBoolean == "MEMO")
            {
                titleTextViewBottomLinearLayout.startAnimation(OutRightSlideAnimation)
                searchImageView.startAnimation(OutRightSlideAnimation)
                memoSearchViewLayout.visibility = View.VISIBLE
                memoSearchViewLayout.startAnimation(InRightSlideAnimation)
                Handler().postDelayed({
                    titleTextViewBottomLinearLayout.visibility = View.INVISIBLE
                    searchImageView.visibility = View.INVISIBLE
                }, 500)
            }
        }

        //t odo 검색을 취소했을 때.
        todoRightArrow.setOnClickListener {
            //만일 tabMenuBoolean 이 T ODO 이면서 todoSearchView 의 visibility 가 VISIBLE 이면 실행한다.
//            if(tabMenuBoolean == "T ODO" && todoSearchView.visibility == View.VISIBLE) {
            todoSearchViewLayout.startAnimation(OutLeftSlideAnimation)
            titleTextViewBottomLinearLayout.visibility = View.VISIBLE
            searchImageView.visibility = View.VISIBLE
            searchImageView.startAnimation(InLeftSlideAnimation)
            titleTextViewBottomLinearLayout.startAnimation(InLeftSlideAnimation)
            Handler().postDelayed({
                todoSearchViewLayout.visibility = View.GONE
            }, 500)
//            }
        }

        //memo 검색을 취소했을 때.
        memoRightArrow.setOnClickListener {
            //만일 tabMenuBoolean 이 MEMO 이면서 memoSearchView 의 visibility 가 VISIBLE 이면 실행한다.
//            else if(tabMenuBoolean == "MEMO" && memoSearchView.visibility == View.VISIBLE) {
            memoSearchViewLayout.startAnimation(OutLeftSlideAnimation)
            titleTextViewBottomLinearLayout.visibility = View.VISIBLE
            searchImageView.visibility = View.VISIBLE
            searchImageView.startAnimation(InLeftSlideAnimation)
            titleTextViewBottomLinearLayout.startAnimation(InLeftSlideAnimation)
            Handler().postDelayed({
                memoSearchViewLayout.visibility = View.GONE
            }, 500)
//            }
        }
    }

    //searchView 의 setOnQueryTextListener
    private fun searchViewSetOnQueryText() {
        //todoSearchView 에 입력이 되었을 때
        todoSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener, androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                todoAdapter.filter.filter(newText)
                return false
            }


        })

        //memoSearchView 에 입력이 되었을 때
        memoSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener, androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                memoAdapter.filter.filter(newText)
                return false
            }

        })
    }

    //리사이클러뷰와 어답터를 연결
    private fun bridgeRecyclerViewAndAdapter() {
        //todoList 에 todoDB 에 있는 값을 대입한 다음에 RecyclerView 에 연결한다.
        todoList.clear()
        todoList.addAll(todoDB.todoDao().getAll())
        todoAdapter = TodoRecyclerViewAdapter(todoList, DoneTodoList,this@MainActivity, todoSearchList)
        todoAdapter.notifyDataSetChanged()
        if(tabMenuBoolean == "TODO")
        {
            todoRecyclerView.visibility = View.VISIBLE
            todoRecyclerView.startAnimation(startLottieAnimationAlphaAnimation)
        }

        //memoList 에 memoDB 에 있는 값을 대입한 다음에 RecyclerView 에 연결한다.
        memoList.clear()
        memoList.addAll(memoDB.memoDao().getAll())
        memoAdapter = MemoRecyclerViewAdapter(memoList, memoSearchList,this)
        memoAdapter.notifyDataSetChanged()

        //todoRecyclerView adapter 연결 & RecyclerView 세팅
        todoRecyclerView.apply{
            adapter = todoAdapter
            layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.VERTICAL, false)
            setHasFixedSize(true)
        }

        //memoRecyclerView adapter 연결 & RecyclerView 세팅
        memoRecyclerView.apply {
            adapter = memoAdapter
            layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.VERTICAL, false)
            setHasFixedSize(true)
        }
    }

    //로티 애니메이션 Visible 조정해주는 메소드.
    private fun controlLottieAnimationVisible() {
        //만일 todoList 의 사이즈가 1이면 GONE 으로 되는 todoLottieAnimationVisibleForm 을 true 로 바꾸어 LottieAnimationView 를 GONE 형태로 바꾸어 줘야함.
        if(todoList.size >= 1) {
            todoLottieAnimationVisibleForm = true
        }
        if(todoLottieAnimationVisibleForm == true) {
            todoLottieAnimationLayout.startAnimation(lottieAnimationAlphaAnimation)
            Handler().postDelayed({
                todoLottieAnimationLayout.visibility = View.GONE
                todoLottieAnimationVisibleForm = false
            }, 500)
        }

        //여기는 위에 부분과 똑같음. 위에는 todoLottieAnimationVisibleForm 이였지만 여기는 memoLottieAnimationVisibleForm 이다.
        if(memoList.size >= 1) {
            memoLottieAnimationVisibleForm = true
        }
        if(memoLottieAnimationVisibleForm == true) {
            memoLottieAnimationLayout.startAnimation(lottieAnimationAlphaAnimation)
            Handler().postDelayed({
                memoLottieAnimationLayout.visibility = View.GONE
                memoLottieAnimationVisibleForm = false
            }, 500)
        }
    }


}