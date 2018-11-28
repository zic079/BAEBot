package com.sponge.baebot;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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


public class EventActivity extends AppCompatActivity
        implements View.OnClickListener {
    private Button selectDate, selectTime_start, selectTime_end;
    private EditText title, description, eventIdInput;
    private int year, month, dayOfMonth, hour_start, minute_start, hour_end, minute_end;
    private Calendar calendar = Calendar.getInstance();
    private static FirebaseDatabase database = FirebaseDatabase.getInstance(); // Firebase databse
    private static DatabaseReference mDatabase = database.getReference();
    private String userId;
    private TableLayout tl;
    private ArrayList<Event> eventList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            userId = extras.getString("userId");
            //The key argument here must match that used in the other activity
        }
        Intent intent = getIntent();
        User myUser = intent.getParcelableExtra("user");

//        final Button deleteEvent = findViewById(R.id.btnDelete);
//        eventIdInput = findViewById(R.id.eventId_input);
//
//        deleteEvent.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v){
//                deleteEvent();
//            }
//        });

//        tl= findViewById(R.id.tastTable);
//        Button getEvent = findViewById(R.id.btnGetEvent);
//        getEvent.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v){
//                Log.w("button", "get Event button clicked!");
//                searchEvent();
//                tl.removeAllViews();
//
//
//                Log.d("list size", ""+Integer.toString(eventList.size()));
//                Handler handler = new Handler();
//                handler.postDelayed(new Runnable() {
//                    public void run() {
//                        printEvents();
//
//                    }
//                }, 100);
//            }
//        });

        Button createEvent = findViewById(R.id.btnEvent);
        createEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addEvent();
            }
        });

        title = findViewById(R.id.title_input_event);
        description = findViewById(R.id.description_event);

        selectDate = findViewById(R.id.btnDate_event);
        //date = findViewById(R.id.tvSelectedDate);
        selectDate.setOnClickListener(this);

        selectTime_start = findViewById(R.id.btnTime_start);
        //time = findViewById(R.id.tvSelectedTime);
        selectTime_start.setOnClickListener(this);

        selectTime_end = findViewById(R.id.btnTime_end);
        //time = findViewById(R.id.tvSelectedTime);
        selectTime_end.setOnClickListener(this);
    }

    @Override
    public void onClick(View v){
        switch (v.getId()) {
            case R.id.btnDate_event:
                Log.w("button", "select date button clicked!");
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(EventActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                selectDate.setText(year + "-" + (month + 1) + "-" + day);
                            }
                        }, year, month, dayOfMonth);
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
                datePickerDialog.show();
                break;
            case R.id.btnTime_start:
                Log.w("button", "select time button clicked!");
                hour_start = calendar.get(Calendar.HOUR);
                minute_start = calendar.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog_start = new TimePickerDialog(EventActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                selectTime_start.setText(hourOfDay + ":" + minute);
                            }
                        }, hour_start, minute_start, true);
                timePickerDialog_start.show();
                break;

            case R.id.btnTime_end:
                Log.w("button", "select time button clicked!");
                hour_end = calendar.get(Calendar.HOUR);
                minute_end = calendar.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog_end = new TimePickerDialog(EventActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                selectTime_end.setText(hourOfDay + ":" + minute);
                            }
                        }, hour_end, minute_end, true);
                timePickerDialog_end.show();
                break;
        }
    }

    private void addEvent(){
        Log.w("button", "create event button clicked!");
        String strTitle = title.getText().toString();
        String strDescription = description.getText().toString();
        String strDate = "";
        String strStartTime = "";
        String strEndTime = "";

        if (selectDate != null && selectTime_start != null && selectTime_end != null) {
            strDate = selectDate.getText().toString();
            strStartTime = selectTime_start.getText().toString();
            strEndTime = selectTime_end.getText().toString();
        }

        if (strDate.length() != 0 && strStartTime.length() != 0 && strEndTime.length() != 0 && strTitle.length() != 0) {


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
            String strHour_start = "";
            for (idx = 0; idx < strStartTime.length(); ++idx) {
                if (strStartTime.charAt(idx) != ':') {
                    strHour_start += strStartTime.charAt(idx);
                } else {
                    break;
                }
            }
            hour_start = Integer.parseInt(strHour_start);
            minute_start = Integer.parseInt(strStartTime.substring(idx+1));

            String strHour_end = "";
            for (idx = 0; idx < strEndTime.length(); ++idx) {
                if (strEndTime.charAt(idx) != ':') {
                    strHour_end += strEndTime.charAt(idx);
                } else {
                    break;
                }

            }
            hour_end = Integer.parseInt(strHour_end);
            minute_end = Integer.parseInt(strEndTime.substring(idx+1));


            if (userId != null) {
                long currentTime = calendar.getTimeInMillis();
                String eventId = Long.toString(currentTime);
                Event event = new Event(eventId, strTitle, strDescription, year, month,
                        dayOfMonth, hour_start, minute_start, hour_end, minute_end);
                Log.d("event id", "" + eventId);
                mDatabase.child("event").child(userId).child(eventId).setValue(event);
                Log.w("add to db", "success");
            } else {
                Log.w("dataBase error", "No such User");
            }
        }
    }

    private void searchEvent(){
        mDatabase.child("event").child(userId).addListenerForSingleValueEvent(
                new ValueEventListener(){
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.d("Events", "onDataChange!");
                        eventList.clear();
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            Log.d("Events", "" + ds.getKey());
                            Event t = ds.getValue(Event.class);
                            eventList.add(t);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void printEvents(){
        for (Event t : eventList) {
            TableRow tr1 = new TableRow(EventActivity.this);
            tr1.setLayoutParams(new TableRow.LayoutParams(
                    TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT));
            TextView textview = new TextView(EventActivity.this);
            textview.setText(t.getEventId()+ " " + t.getTitle() + " " + t.getDescription() + " " +
                    t.getYear() + "-" + t.getMonth() + "-" +
                    t.getDayOfMonth() + " " + t.getHourStart() + ":" + t.getMinuteStart() + " to " +
                    t.getHourEnd() + " " + t.getMinuteEnd()) ;
            textview.setTextColor(Color.BLACK);
            tr1.addView(textview);
            tl.addView(tr1, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT));
        }
    }

    private void deleteEvent(){
        mDatabase.child("event").child(userId).addListenerForSingleValueEvent(
                new ValueEventListener(){
                    String eventId = eventIdInput.getText().toString();
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child(eventId).exists()) {
                            mDatabase.child("task").child(userId).child(eventId).removeValue(
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
}
