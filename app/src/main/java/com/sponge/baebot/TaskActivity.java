package com.sponge.baebot;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;


public class TaskActivity extends AppCompatActivity
        implements View.OnClickListener {
    private EditText date, time, title, description, taskIdInput;
    private int year, month, dayOfMonth, hour, minute;
    private Calendar calendar = Calendar.getInstance();
    private static FirebaseDatabase database = FirebaseDatabase.getInstance(); // Firebase databse
    private static DatabaseReference mDatabase = database.getReference();
    private String userId;
    private TableLayout tl;
    private ArrayList<Task> taskList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            userId = extras.getString("userId");
            //The key argument here must match that used in the other activity
        }
        Intent intent = getIntent();
        User myUser = intent.getParcelableExtra("user");

        final Button deleteTask = findViewById(R.id.btnDelete);
        taskIdInput = findViewById(R.id.taskId_input);

        deleteTask.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                deleteTask();
            }
        });

        tl= findViewById(R.id.tastTable);
        Button getTask = findViewById(R.id.btnGetTask);
        getTask.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Log.w("button", "get Task button clicked!");
//                searchTask();
                reschedule();
                tl.removeAllViews();


                Log.d("list size", ""+Integer.toString(taskList.size()));
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
//                        printTasks();


                    }
                }, 100);
            }
        });

        Button createTask = findViewById(R.id.btnTask);
        createTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTask();
            }
        });

        title = findViewById(R.id.title_input);
        description = findViewById(R.id.description);

        Button selectDate = findViewById(R.id.btnDate);
        date = findViewById(R.id.tvSelectedDate);
        selectDate.setOnClickListener(this);

        Button selectTime = findViewById(R.id.btnTime);
        time = findViewById(R.id.tvSelectedTime);
        selectTime.setOnClickListener(this);
    }


    @Override
    public void onClick(View v){
        switch (v.getId()) {
            case R.id.btnDate:
                Log.w("button", "select date button clicked!");
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(TaskActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                date.setText(year + "-" + (month + 1) + "-" + day);
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
                                time.setText(hourOfDay + ":" + minute);
                            }
                        }, hour, minute, true);
                timePickerDialog.show();
                break;
        }
    }

    private void addTask(){
        Log.w("button", "create task button clicked!");
        String strTitle = title.getText().toString();
        String strDescription = description.getText().toString();

        String strDate = date.getText().toString();
        String strTime = time.getText().toString();
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


            if (userId != null) {
                String taskId = UUID.randomUUID().toString();
                Task task = null;
                try {
                    Log.d("date",year + "/" + month + "/" + dayOfMonth + "/" + hour + "/" + minute);
                    Date d = new SimpleDateFormat("yyyy/MM/dd/hh/mm").parse(year + "/" + month + "/" + dayOfMonth + "/" + hour + "/" + minute);
                    Timestamp ts = new Timestamp(d.getTime());
                    task = new Task(taskId,strTitle,strDescription, ts.getTime());
                }catch (Exception e){
                    Log.d("task", "date parsing error");
                }

                Log.d("task id", taskId);
                if(task == null){
                    Log.d("task","task null");
                }else{
                    Log.d("task","task not null");
                }

                mDatabase.child("task").child(userId).child(taskId).setValue(task);
                Log.d("add to db", "success");
            } else {
                Log.d("dataBase error", "No such User");
            }
        }
    }

    private void searchTask(){
        mDatabase.child("task").child(userId).addListenerForSingleValueEvent(
                new ValueEventListener(){
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.d("Tasks", "onDataChange!");
                        taskList.clear();
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            Log.d("Tasks", "" + ds.getKey());
                            Task t = ds.getValue(Task.class);
                            taskList.add(t);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

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

    private void deleteTask(){
        mDatabase.child("task").child(userId).addListenerForSingleValueEvent(
                new ValueEventListener(){
                    String taskId = taskIdInput.getText().toString();
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child(taskId).exists()) {
                            mDatabase.child("task").child(userId).child(taskId).removeValue(
                                    new DatabaseReference.CompletionListener() {
                                        @Override
                                        public void onComplete(
                                                @Nullable DatabaseError databaseError,
                                                @NonNull DatabaseReference databaseReference) {
                                                if (databaseError == null){
                                                    Log.d("delete task", "success");
                                                } else {
                                                    Log.d("delete task", "failure");
                                                }
                                        }
                                    }
                            );
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    public void reschedule(){
        Log.d("rechedule","in reschedule");
        final String taskId = null;
        mDatabase.child("task").child(userId).addListenerForSingleValueEvent(
                new ValueEventListener(){
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        if (dataSnapshot.child(taskId).exists()) {
//                            Task temp = dataSnapshot.child(taskId).getValue(Task.class);
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
                        Log.d("reschedule","in ondatechange");
                        for(DataSnapshot ds : dataSnapshot.getChildren()){
                            System.out.println(ds.getValue(Task.class).getTitle());
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }
}
