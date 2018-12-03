package com.sponge.baebot;

import android.app.ActivityOptions;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.UUID;


public class TaskActivity extends AppCompatActivity
        implements View.OnClickListener, TaskAdapter.ItemClickListener {
    private Button selectDate, selectTime, saveTask,btnReschedule;
    private EditText title, description;
    private int year, month, dayOfMonth, hour, minute;
    private Calendar calendar = Calendar.getInstance();
    private static FirebaseDatabase database = FirebaseDatabase.getInstance(); // Firebase databse
    private static DatabaseReference mDatabase = database.getReference();
    private String userId;
    private popWindow popupWindow;
    //private LayoutInflater layoutInflater;
    //private TableLayout tl;
    private ArrayList<Task> taskList = new ArrayList<>();
    private ArrayList<Task> tasks = new ArrayList<>();
    private ArrayList<String> strTasks = new ArrayList<>();
    private RecyclerView recyclerView;
    //private RecyclerViewAdapter recyclerViewAdapter;
    private TaskAdapter adapter;
    private Task editedTask;
    private LinearLayout linearLayout;

    private interface FirebaseCallback{
        void onCallback(ArrayList<com.sponge.baebot.Task> list);
    }
    
    private interface FirebaseCallbackEdit{
        void onCallback(Task taskToEdit);
    }

    private interface RescheduleHandler{
        void onCallback(ArrayList<Task> list);
    }

    public void getSingleTask(String id, final FirebaseCallbackEdit firebaseCallbackEdit){
        mDatabase.child("task").child(userId).child(id).addListenerForSingleValueEvent(
                new ValueEventListener(){
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        Task t = dataSnapshot.getValue(Task.class);
                        Log.e("Task Activity edit",t.toString());
                        firebaseCallbackEdit.onCallback(t);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onItemClick(View view, final int position){
        PopupMenu popup = new PopupMenu(this, view);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.view:

                        getSingleTask(adapter.getItem(position),new FirebaseCallbackEdit() {
                            @Override
                            public void onCallback(Task taskToEdit) {
                                LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                                ViewGroup container = (ViewGroup) layoutInflater.inflate(R.layout.popup_layout,null);
                                if (popupWindow != null){
                                    popupWindow.dismiss();
                                }
                                popupWindow = new popWindow(TaskActivity.this, taskToEdit);
                                popupWindow.show(findViewById(R.id.linearLayout_task), 0,0);
                            }
                        });

                        return true;
                    case R.id.edit:
                        //Toast.makeText(selectDate.getContext(), "Hello Edit!", Toast.LENGTH_SHORT).show();
                        getSingleTask(adapter.getItem(position),new FirebaseCallbackEdit() {
                            @Override
                            public void onCallback(Task taskToEdit) {
                                editedTask = taskToEdit;
                                title.setText(editedTask.getTitle());
                                description.setText(editedTask.getDescription());

                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");
                                String dateAndTime = sdf.format(new Date((editedTask.getTimestamp()+28800)*1000));
                                selectDate.setText(dateAndTime.substring(0,10));
                                selectTime.setText(dateAndTime.substring(11));
                            }
                        });
                        return true;

                        case R.id.delete:
                            mDatabase.child("task").child(userId).child(adapter.getItem(position)).removeValue();
                            Toast.makeText(selectDate.getContext(), "Delete Successfully!", Toast.LENGTH_SHORT).show();
                            adapter.removeAt(position);
                            return true;
                    case R.id.complete:
                        mDatabase.child("task").child(userId).child(adapter.getItem(position)).removeValue();
                        Toast.makeText(selectDate.getContext(), "Completed!", Toast.LENGTH_SHORT).show();
                        adapter.removeAt(position);
                        return true;


                        default:
                            return false;
                }
            }
        });
        // here you can inflate your menu
        popup.inflate(R.menu.popup_menu);
        popup.setGravity(Gravity.RIGHT);
        popup.show();

    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        this.startActivity(new Intent(TaskActivity.this,MainActivity.class));
        Log.w("Task Activity", "on back pressed");
        return;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        if (popupWindow != null){
            popupWindow.dismiss();
        }

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            userId = extras.getString("userId");
            //The key argument here must match that used in the other activity
        }
        Intent intent = getIntent();
        User myUser = intent.getParcelableExtra("user");
        linearLayout = (LinearLayout)findViewById(R.id.linearLayout_task);

//        final Button deleteTask = findViewById(R.id.btnDelete);
//        taskIdInput = findViewById(R.id.taskId_input);
//
//        deleteTask.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v){
//                deleteTask();
//            }
//        });

//        tl= findViewById(R.id.tastTable);
//        Button getTask = findViewById(R.id.btnGetTask);
//        getTask.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v){
//                Log.w("button", "get Task button clicked!");
//                getAllTasks();
//                tl.removeAllViews();
//
//
//                Log.d("list size", ""+Integer.toString(taskList.size()));
//                Handler handler = new Handler();
//                handler.postDelayed(new Runnable() {
//                    public void run() {
//                        printTasks();
//
//                    }
//                }, 100);
//            }
//        });

        findViewById(R.id.search_bar).setOnClickListener(this);


        title = findViewById(R.id.title_input);
        description = findViewById(R.id.description);

        selectDate = findViewById(R.id.btnDate);
        //date = findViewById(R.id.tvSelectedDate);
        selectDate.setOnClickListener(this);

        selectTime = findViewById(R.id.btnTime);
        //time = findViewById(R.id.tvSelectedTime);
        selectTime.setOnClickListener(this);

        saveTask = findViewById(R.id.btnTask);

        btnReschedule = findViewById(R.id.reschedule);

        saveTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (addTask()) {
                    getAllTasks(new FirebaseCallback() {
                        @Override
                        public void onCallback(ArrayList<Task> list) {
                            Intent intent = getIntent();
                            startActivity(intent);
                        }
                    });
                    Toast.makeText(selectDate.getContext(), "Save Successfully", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnReschedule.setOnClickListener(new View.OnClickListener(){




            @Override
            public void onClick(View v) {
//                getAllTasks(new FirebaseCallback() {
//                    @Override
//                    public void onCallback(ArrayList<Task> list) {
//                        Log.e("aaaaaaaaaaaaaaaaaaaaa","aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaab");
//                        Log.e("aaareshcedule","aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaab"+Integer.toString(list.size()));
//                    }
//                });

                List<Task> task = adapter.getmTask();
                Log.e("reschedule*********",Integer.toString(task.size()));

                class SortByPriority implements Comparator<Task>
                {

                    // Used for sorting in ascending order of
                    // roll number
                    public int compare(Task a, Task b)
                    {
                        if(b.getPriority() - a.getPriority() > 0){
                            return 1;
                        }else if(b.getPriority() - a.getPriority() < 0){
                            return -1;
                        }else{
                            return (int)(a.getTimestamp() - b.getTimestamp());
                        }
                    }
                }

                Date d = new Date();
                long a = d.getTime();
                Timestamp curTime = new Timestamp(a);
                long cur = curTime.getTime()/1000-28800;
                cur = cur/86400 * 86400;
                Log.e("currentTime",Long.toString(cur));

                ArrayList<Task> unfinished = new ArrayList<>();
                int[] weeklyTaskCount = {0,0,0,0,0,0,0};

                for(Task t : task){
                    Log.e("reschedule","today's timestamp" +Long.toString(cur));
                    Log.e("reschedule",t.getTaskId() +" "+ Long.toString(t.getTimestamp()));
                    if(!t.isCompleted() && t.getTimestamp() < cur){
                        unfinished.add(t);
                    }else if(!t.isCompleted()){
                        int day = (int) ((t.getTimestamp() - cur)/86400);
                        if(day > 6){
                            day = 6;
                        }
                        weeklyTaskCount[day] += 1;
                    }
                }


                Collections.sort(unfinished, new SortByPriority());

                int totalFinished = 0;
                int totalUnfinished = unfinished.size();
                for(int i = 0; i < 7; i++){
                    totalFinished += weeklyTaskCount[i];
                }
                int total = totalUnfinished + totalFinished;
                int avg = (int)total/7+1;
                int curDay = 0;

                for(Task t: unfinished){
                    while (weeklyTaskCount[curDay] >= avg) {
                        curDay++;
                    }
                    t.setTimestamp(curDay*86400+cur+28800);
                    mDatabase.child("task").child(userId).child(t.getTaskId()).setValue(t);
                }
            }
        });





        getAllTasks(new FirebaseCallback() {
            @Override
            public void onCallback(ArrayList<Task> list) {
                Log.e("aaaaaaaaaaaaaaaaaaaaa","aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaab");
                Log.e("aaaaaaaaaaaaaaaaaaaaa","aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaab"+Integer.toString(list.size()));
                tasks = list;
                for (Task t : tasks) {
                    strTasks.add(t.toString());
                }
                recyclerView = findViewById(R.id.task_recyclerView);
                recyclerView.setLayoutManager(new LinearLayoutManager(TaskActivity.this));
                adapter = new TaskAdapter(TaskActivity.this, tasks);
                adapter.setClickListener(TaskActivity.this);
                recyclerView.setAdapter(adapter);
            }
        });




//
//        rescheduleAllUnfinishedTasks(new RescheduleHandler() {
//            @Override
//            public void onCallback(ArrayList<Task> taskList) {

//                class SortByPriority implements Comparator<Task>
//                {
//
//                    // Used for sorting in ascending order of
//                    // roll number
//                    public int compare(Task a, Task b)
//                    {
//                        if(b.getPriority() - a.getPriority() > 0){
//                            return 1;
//                        }else if(b.getPriority() - a.getPriority() < 0){
//                            return -1;
//                        }else{
//                            return (int)(a.getTimestamp() - b.getTimestamp());
//                        }
//                    }
//                }

//                Date d = new Date();
//                long a = d.getTime()/86400 * 86400;
//                Timestamp curTime = new Timestamp(a);
//                long cur = curTime.getTime()/1000-28800;
//
//
//
//                ArrayList<Task> unfinished = new ArrayList<>();
//                int[] weeklyTaskCount = {0,0,0,0,0,0,0};
//
//                for(Task t : taskList){
//                    if(!t.isCompleted() && t.getTimestamp() < cur){
//                        unfinished.add(t);
//                    }else if(!t.isCompleted()){
//                        int day = (int) ((t.getTimestamp() - cur)/86400);
//                        if(day > 6){
//                            day = 6;
//                        }
//                        weeklyTaskCount[day] += 1;
//                    }
//                }
//                Collections.sort(unfinished, new SortByPriority());
//
//                int totalUnfinished = unfinished.size();
//                int totalFinished = 0;
//                for(int i = 0; i < 7; i++){
//                    totalFinished += weeklyTaskCount[i];
//                }
//                int total = totalUnfinished + totalFinished;
//                int avg = (int)total/7+1;
//                int curDay = 0;
//
//                for(Task t: unfinished){
//                    while (weeklyTaskCount[curDay] >= avg) {
//                        curDay++;
//                    }
//                    t.setTimestamp(curDay*86400+cur);
//                    mDatabase.child("task").child(userId).child(t.getTaskId()).setValue(t);
//                }
//            }
//        });
    }

    @Override
    public void onClick(View v){
        final View search = findViewById(R.id.search);

        switch (v.getId()) {
            case R.id.search_bar:
                Intent intent = new Intent(TaskActivity.this, SearchActivity.class);
                ActivityOptions options1 = ActivityOptions
                        .makeSceneTransitionAnimation(this, search, "search");
                intent.putExtra("userId", userId );
                startActivity(intent, options1.toBundle());
                break;

            case R.id.btnDate:
                Log.w("button", "select date button clicked!");
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(TaskActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                selectDate.setText(year + "-" + (month + 1) + "-" + day);
                            }
                        }, year, month, dayOfMonth);
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
                datePickerDialog.show();
                break;
            case R.id.btnTime:
                Log.w("button", "select time button clicked!");
                hour = calendar.get(Calendar.HOUR);
                minute = calendar.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(TaskActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                selectTime.setText(hourOfDay + ":" + minute);
                            }
                        }, hour, minute, true);
                timePickerDialog.show();
                break;

        }
    }

    private boolean addTask(){
        Log.w("button", "create task button clicked!");
        String strTitle = "";
        String strDescription = description.getText().toString();
        String strDate = "";
        String strTime = "";

        if (selectDate != null && selectTime != null) {
            strDate = selectDate.getText().toString();
            strTime = selectTime.getText().toString();
            strTitle = title.getText().toString().replaceAll("\n", "");
        }

        if (strDate.length() != 0 && strTime.length() != 0 && strTitle.length() != 0) {

            // xxxx-xx-xx or xxxx-x-xx or xxxx-xx-x or xxxx-x-x
            year = Integer.parseInt(strDate.substring(0, 4));
            String strMonth = "";
            int i;
            for (i = 5; i < strDate.length(); ++i) {
                if (strDate.charAt(i) != '-') {
                    strMonth += strDate.charAt(i);
                } else {
                    break;
                }
            }
            month = Integer.parseInt(strMonth);
            String strDay = "";
            for (int j = i + 1; j < strDate.length(); ++j) {
                if (strDate.charAt(j) != '-') {
                    strDay += strDate.charAt(j);
                } else {
                    break;
                }
            }
            dayOfMonth = Integer.parseInt(strDay);

            // xx:xx or x:xx or x:x or xx:x
            int idx;
            String strHour = "";
            for (idx = 0; idx < strTime.length(); ++idx) {
                if (strTime.charAt(idx) != ':') {
                    strHour += strTime.charAt(idx);
                } else {
                    break;
                }
            }
            hour = Integer.parseInt(strHour);
            minute = Integer.parseInt(strTime.substring(idx+1));
            Timestamp ts;

            if (userId != null) {
                String taskId = UUID.randomUUID().toString();
                Task task = null;
                try {
                    Log.d("date",year + "/" + month + "/" + dayOfMonth + "/" + hour + "/" + minute);
                    Date d = new SimpleDateFormat("yyyy/MM/dd/hh/mm").parse(year + "/" + month + "/" + dayOfMonth + "/" + hour + "/" + minute);
                    ts = new Timestamp(d.getTime());
                    task = new Task(taskId,strTitle,strDescription, ts.getTime()/1000 - 28800);

                    if (editedTask == null) {
                        mDatabase.child("task").child(userId).child(taskId).setValue(task);

                    } else {
                        task = new Task(editedTask.getTaskId(), strTitle, strDescription, ts.getTime()/1000 - 28800);
                        mDatabase.child("task").child(userId).child(editedTask.getTaskId()).setValue(task);
                        editedTask = null;
                    }

                    Log.d("add to db", "success");
                }catch (Exception e){
                    Log.d("task", "date parsing error");
                }

                Log.d("task id", taskId);
                if(task == null){
                    Log.d("task","task null");
                }else{
                    Log.d("task","task not null");
                }

            } else {
                Log.d("dataBase error", "No such User");
            }
            return true;
        }
        else {
            Toast.makeText(this, "Missing information", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private void getAllTasks(final FirebaseCallback firebaseCallback){
        mDatabase.child("task").child(userId).addListenerForSingleValueEvent(
                new ValueEventListener(){
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.d("Tasks", "onDataChange!");
                        taskList.clear();
                        int i = 0;
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
//                            Log.d("Tasks", "" + ds.getKey());
                            Task t = ds.getValue(Task.class);
//                            taskList.add(t);
//                            Task tt = (Task)t;
                            i++;
//                            Log.d("task-getAllTasks",tt.toString());
                            Log.d("task-getAllTasks",t.toString());
                            Log.d("task-getAllTasks",Integer.toString(i));
                            taskList.add(t);
                            Log.d("task-getAllTasks",Integer.toString(taskList.size()));
                        }
                        tasks.clear();
                        firebaseCallback.onCallback(taskList);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }


//    private void showTasks(){
//        RecyclerView recyclerView = findViewById(R.id.task_recyclerView);
//        ArrayList<String> tasks = new ArrayList<>();
//        for (Task t : taskList){
//            tasks.add(t.toString());
//        }
//        RecyclerViewAdapter adapter = new RecyclerViewAdapter(tasks, this);
//        recyclerView.setAdapter(adapter);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//    }

//    public void showPopup(View v) {
//        PopupMenu popupMenu = new PopupMenu(this,v);
//        popupMenu.setOnMenuItemClickListener(this);
//        popupMenu.inflate(R.menu.popup_menu);
//        popupMenu.show();
//    }
//
//    @Override
//    public boolean onMenuItemClick(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.edit:
//                Toast.makeText(this, "Hello Edit!", Toast.LENGTH_SHORT).show();
//                return true;
//            case R.id.delete:
//                Toast.makeText(this, "Hello Delete!", Toast.LENGTH_SHORT).show();
//                return true;
//            default:
//                return false;
//        }
//    }

//    private void printTasks(){
//        for (Task t : taskList) {
//            TableRow tr1 = new TableRow(TaskActivity.this);
//            tr1.setLayoutParams(new TableRow.LayoutParams(
//                    TableLayout.LayoutParams.MATCH_PARENT,
//                    TableLayout.LayoutParams.WRAP_CONTENT));
//            TextView textview = new TextView(TaskActivity.this);
//            textview.setText(t.getTaskId()+ " " + t.getTitle() + " " + t.getDescription() + " " +
//                    t.getYear() + "-" + t.getMonth() + "-" +
//                    t.getDayOfMonth() + " " + t.getHour() + ":" + t.getMinute());
//            textview.setTextColor(Color.BLACK);
//            tr1.addView(textview);
//            tl.addView(tr1, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
//                    TableLayout.LayoutParams.WRAP_CONTENT));
//        }
//    }
//
//    private void deleteTask(){
//        mDatabase.child("task").child(userId).addListenerForSingleValueEvent(
//                new ValueEventListener(){
//                    String taskId = taskIdInput.getText().toString();
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        if (dataSnapshot.child(taskId).exists()) {
//                            mDatabase.child("task").child(userId).child(taskId).removeValue(
//                                    new DatabaseReference.CompletionListener() {
//                                        @Override
//                                        public void onComplete(
//                                                @Nullable DatabaseError databaseError,
//                                                @NonNull DatabaseReference databaseReference) {
//                                            if (databaseError == null){
//                                                Log.d("delete task", "success");
//                                            } else {
//                                                Log.d("delete task", "failure");
//                                            }
//                                        }
//                                    }
//                            );
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });
//    }
}
