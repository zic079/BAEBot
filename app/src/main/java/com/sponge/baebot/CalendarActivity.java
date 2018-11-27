package com.sponge.baebot;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.AsyncQueryHandler;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.sponge.baebot.CalendarQueryHandler;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class CalendarActivity extends AppCompatActivity
        implements OnClickListener {

    /*
    // hardcoded test
    TextView event1 = (TextView)findViewById(R.id.event1);
    TextView event2 = (TextView)findViewById(R.id.event2);
    TextView event3 = (TextView)findViewById(R.id.event3);
    TextView event4 = (TextView)findViewById(R.id.event4);
    TextView event5 = (TextView)findViewById(R.id.event5);
    TextView event6 = (TextView)findViewById(R.id.event6);
    */

    private Button daysBtn;
    private EditText daysEdit;

    private Button newEventBtn;
    private EditText titleEdit;
    private EditText dateEdit;
    private EditText timeEdit;
    private Button dateSelectBtn;
    private Button timeSelectBtn;

    private Calendar mCalendar;
    private int inputYear, inputMonth, inputDay, inputHour, inputMinute;

    private int queryDays = 0;

    CalendarQueryHandler handler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        */

        daysBtn = findViewById(R.id.buttonGetDays);
        daysEdit = findViewById(R.id.editTextDays);
        daysBtn.setOnClickListener(this);

        newEventBtn = findViewById(R.id.buttonNewEvent);
        titleEdit = findViewById(R.id.editTextTitle);
        dateEdit = findViewById(R.id.editTextDate);
        timeEdit = findViewById(R.id.editTextTime);
        dateSelectBtn = findViewById(R.id.buttonSelectDate);
        timeSelectBtn = findViewById(R.id.buttonSelectTime);

        newEventBtn.setOnClickListener(this);
        dateSelectBtn.setOnClickListener(this);
        timeSelectBtn.setOnClickListener(this);

        // calendar to handle dat and time input - set to current time
        mCalendar = Calendar.getInstance();

        // initialize new CalendarQueryHandler to handle calendar CRUD operation
        handler = new CalendarQueryHandler(this, this.getContentResolver()) {};

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonGetDays:
                queryDays = Integer.parseInt(daysEdit.getText().toString());

                // call CalendarQueryHandler to get event
                //handler.readEvent(queryDays);
                break;

            case R.id.buttonSelectDate:
                inputYear = mCalendar.get(Calendar.YEAR);
                inputMonth = mCalendar.get(Calendar.MONTH);
                inputDay = mCalendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int month, int day) {
                                dateEdit.setText( year + "-" + (month + 1) + '-' + day);

                            }
                        }, inputYear, inputMonth, inputDay);
                datePickerDialog.show();
                break;

            case R.id.buttonSelectTime:
                inputHour = mCalendar.get(Calendar.HOUR);
                inputMinute = mCalendar.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hour,
                                                  int minute) {
                                timeEdit.setText(hour + ":" + minute);
                            }
                        }, inputHour, inputMinute, true);
                timePickerDialog.show();
                break;

            case R.id.buttonNewEvent:
                long startMilliseconds = 0;
                long endMilliseconds = 0;

                String newEventTitle = titleEdit.getText().toString();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                String datetimeStr = dateEdit.getText().toString() + " " + timeEdit.getText().toString();

                try {
                    Date startTime = dateFormat.parse(datetimeStr);

                    // hardcode endtime for test - 1 hour
                    mCalendar.setTime(startTime);
                    mCalendar.add(Calendar.HOUR_OF_DAY, 1);
                    Date endTime = mCalendar.getTime();

                    startMilliseconds = startTime.getTime();
                    endMilliseconds = endTime.getTime();

                } catch (ParseException e) {
                    e.printStackTrace();
                }

                //// HERE might need to check start and end time is not zero
                // call CalendarQueryHandler to insert event
                handler.insertEvent(this, startMilliseconds, endMilliseconds, newEventTitle, "");
                break;
        }
    }


    /*
    public void onUpdateCompleted(ArrayList<String> calendarData) {
        // hardcoded test
        TextView eventsCount = (TextView)findViewById(R.id.eventsCount);
        TextView event1 = (TextView)findViewById(R.id.event1);
        TextView event2 = (TextView)findViewById(R.id.event2);
        TextView event3 = (TextView)findViewById(R.id.event3);
        TextView event4 = (TextView)findViewById(R.id.event4);
        TextView event5 = (TextView)findViewById(R.id.event5);
        TextView event6 = (TextView)findViewById(R.id.event6);




        int numsEvent = calendarData.size();
        eventsCount.setText("There are total " + numsEvent + " in the time range");


        // set limit of display
        if(numsEvent > 6) {
            numsEvent = 6;
        }

        // display events info - hardcode, using list view should be better
        switch(numsEvent) {
            case 1:
                event1.setText(calendarData.get(0));
                event2.setText("");
                event3.setText("");
                event4.setText("");
                event5.setText("");
                event6.setText("");
                break;
            case 2:
                event1.setText(calendarData.get(0));
                event2.setText(calendarData.get(1));
                event3.setText("");
                event4.setText("");
                event5.setText("");
                event6.setText("");
                break;
            case 3:
                event1.setText(calendarData.get(0));
                event2.setText(calendarData.get(1));
                event3.setText(calendarData.get(2));
                event4.setText("");
                event5.setText("");
                event6.setText("");
                break;
            case 4:
                event1.setText(calendarData.get(0));
                event2.setText(calendarData.get(1));
                event3.setText(calendarData.get(2));
                event4.setText(calendarData.get(3));
                event5.setText("");
                event6.setText("");
                break;
            case 5:
                event1.setText(calendarData.get(0));
                event2.setText(calendarData.get(1));
                event3.setText(calendarData.get(2));
                event4.setText(calendarData.get(3));
                event5.setText(calendarData.get(4));
                event6.setText("");
                break;
            case 6:
                event1.setText(calendarData.get(0));
                event2.setText(calendarData.get(1));
                event3.setText(calendarData.get(2));
                event4.setText(calendarData.get(3));
                event5.setText(calendarData.get(4));
                event6.setText(calendarData.get(5));
                break;
            default:
                event1.setText("");
                event2.setText("");
                event3.setText("");
                event4.setText("");
                event5.setText("");
                event6.setText("");
                break;
        }
    }

    */

/*
    public void newEvents(ArrayList<String> calendarData) {
        Log.d("In activity", "calendarData size" + calendarData.size());
    }

    public void updateEventList(ArrayList<String> calendarData) {
        if(calendarData.size() > 0) {
            event1.setText(calendarData.get(0));
            event2.setText(calendarData.get(1));
            //event3.setText(calendarData.get(2));
            //event4.setText(calendarData.get(3));
            //event5.setText(calendarData.get(4));
            //event6.setText(calendarData.get(5));
        }
    }
*/
}
