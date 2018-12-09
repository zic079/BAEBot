package com.sponge.baebot;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.sponge.baebot.CalendarQueryHandler;

import com.sponge.baebot.R;
import com.sponge.baebot.RecyclerViewAdapter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class ShowCalendarActivity extends AppCompatActivity{


    private static final String TAG = "CalendarActivity";
    private CalendarView mCalendarView;
    private TextView myDate;
    private CalendarQueryHandler handler;
    private Calendar calendar = Calendar.getInstance();
    private ImageButton button;
    private LayoutInflater layoutInflater;
    private PopupWindow popupWindow;
    private RelativeLayout relativeLayout;
    private Button newEventBtn;
    private EditText titleEdit;
    private Button dateSelectBtn;
    private Button timeSelectBtn;

    private Calendar mCalendar;
    private int inputYear, inputMonth, inputDay, inputHour, inputMinute;

    private int queryDays = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_calendar);

        handler = new CalendarQueryHandler(this, this.getContentResolver()) {};
        button = (ImageButton) findViewById(R.id.event_link);
        relativeLayout = (RelativeLayout)findViewById(R.id.relative_view);

        // calendar to handle dat and time input - set to current time
        mCalendar = Calendar.getInstance();

        // set default - today
        inputYear = mCalendar.get(Calendar.YEAR);
        inputMonth = mCalendar.get(Calendar.MONTH) + 1;
        inputDay = mCalendar.get(Calendar.DAY_OF_MONTH);

        mCalendarView = (CalendarView) findViewById(R.id.calendarView);
        myDate = (TextView) findViewById(R.id.myDate);
        String dateDisplay = inputYear + "/" + inputMonth + "/" + inputDay;
        myDate.setText(dateDisplay);

        Calendar startDate_offset = new GregorianCalendar(inputYear,inputMonth - 1,inputDay-1);
        Calendar startDate = new GregorianCalendar(inputYear,inputMonth - 1,inputDay);
        Calendar endDate = new GregorianCalendar(inputYear,inputMonth - 1,inputDay+1);
        handler.readEvent(startDate_offset, startDate, endDate);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(ShowCalendarActivity.this,android.R.style.Theme_Translucent_NoTitleBar);
                final View mView = getLayoutInflater().inflate(R.layout.activity_event,null);
                newEventBtn = mView.findViewById(R.id.buttonNewEvent);
                titleEdit = mView.findViewById(R.id.editTextTitle);
                final String temp = inputYear + "-" + inputMonth + '-' + inputDay;
                dateSelectBtn = mView.findViewById(R.id.buttonSelectDate);
                dateSelectBtn.setText(temp);
                timeSelectBtn = mView.findViewById(R.id.buttonSelectTime);

                timeSelectBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        inputHour = mCalendar.get(Calendar.HOUR);
                        inputMinute = mCalendar.get(Calendar.MINUTE);

                        TimePickerDialog timePickerDialog = new TimePickerDialog(mView.getContext(),
                                new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePicker view, int hour,
                                                          int minute) {
                                        timeSelectBtn.setText(hour + ":" + minute);
                                    }
                                }, inputHour, inputMinute, true);
                        timePickerDialog.show();
                    }
                });
                newEventBtn.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        long startMilliseconds = 0;
                        long endMilliseconds = 0;

                        String newEventTitle = titleEdit.getText().toString();
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                        String datetimeStr = temp + " " + timeSelectBtn.getText().toString();
                        try {
                            Date startTime = dateFormat.parse(datetimeStr);
                            // endtime - 1 hour later
                            mCalendar.setTime(startTime);
                            mCalendar.add(Calendar.HOUR_OF_DAY, 1);
                            Date endTime = mCalendar.getTime();

                            startMilliseconds = startTime.getTime();
                            endMilliseconds = endTime.getTime();

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        handler.insertEvent(v.getContext(), startMilliseconds, endMilliseconds, newEventTitle, "");
                        dialog.dismiss();
                    }
                });
                Window window = dialog.getWindow();
                window.setGravity(Gravity.CENTER);

                window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.setContentView(mView);
                dialog.setCanceledOnTouchOutside(true);
                dialog.show();
            }
        });

        mCalendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                inputYear = year;
                inputMonth = month + 1;
                inputDay = dayOfMonth;
                String dateDisplay = year + "/" + (month+1) + "/" + dayOfMonth;
                myDate.setText(dateDisplay);

                Calendar startDate_offset = new GregorianCalendar(year,month,dayOfMonth-1);
                Calendar startDate = new GregorianCalendar(year,month,dayOfMonth);
                Calendar endDate = new GregorianCalendar(year,month,dayOfMonth+1);

                handler.readEvent(startDate_offset, startDate, endDate);
            }
        });


    }

//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
////            case R.id.buttonGetDays:
////                queryDays = Integer.parseInt(.getText().toString());
////
////                // call CalendarQueryHandler to get event
////                //handler.readEvent(queryDays);
////                break;
//
//            case R.id.buttonSelectDate:
//                inputYear = mCalendar.get(Calendar.YEAR);
//                inputMonth = mCalendar.get(Calendar.MONTH);
//                inputDay = mCalendar.get(Calendar.DAY_OF_MONTH);
//
//                DatePickerDialog datePickerDialog = new DatePickerDialog(this,
//                        new DatePickerDialog.OnDateSetListener() {
//                            @Override
//                            public void onDateSet(DatePicker view, int year,
//                                                  int month, int day) {
//                                dateSelectBtn.setText( year + "-" + (month + 1) + '-' + day);
//
//                            }
//                        }, inputYear, inputMonth, inputDay);
//                datePickerDialog.show();
//                break;
//
//            case R.id.buttonSelectTime:
//                inputHour = mCalendar.get(Calendar.HOUR);
//                inputMinute = mCalendar.get(Calendar.MINUTE);
//
//                TimePickerDialog timePickerDialog = new TimePickerDialog(this,
//                        new TimePickerDialog.OnTimeSetListener() {
//                            @Override
//                            public void onTimeSet(TimePicker view, int hour,
//                                                  int minute) {
//                                timeSelectBtn.setText(hour + ":" + minute);
//                            }
//                        }, inputHour, inputMinute, true);
//                timePickerDialog.show();
//                break;
//
//            case R.id.buttonNewEvent:
//                long startMilliseconds = 0;
//                long endMilliseconds = 0;
//
//                String newEventTitle = titleEdit.getText().toString();
//                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
//                String datetimeStr = dateSelectBtn.getText().toString() + " " + timeSelectBtn.getText().toString();
//
//                try {
//                    Date startTime = dateFormat.parse(datetimeStr);
//
//                    // hardcode endtime for test - 1 hour
//                    mCalendar.setTime(startTime);
//                    mCalendar.add(Calendar.HOUR_OF_DAY, 1);
//                    Date endTime = mCalendar.getTime();
//
//                    startMilliseconds = startTime.getTime();
//                    endMilliseconds = endTime.getTime();
//
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }
//
//                //// HERE might need to check start and end time is not zero
//                // call CalendarQueryHandler to insert event
//                handler.insertEvent(this, startMilliseconds, endMilliseconds, newEventTitle, "");
//                break;
//        }
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

//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.imageButton:
//                Log.d(TAG, "onClick: CLICKED");
//                handler.deleteEvent();
//        }
//    }

//    private boolean checkDate (int year, int month, int dayOfMonth) {
//        String date = year + "/" + (month) + "/" + dayOfMonth;
//        System.out.println(date);
//        if (year == inputYear && month == inputMonth && dayOfMonth == inputDay)
//            return true;
//        return false;
//    }

//    private void initRecyclerView(ArrayList<String> events) {
//        RecyclerView recyclerView = findViewById(R.id.event_recyclerView);
//        RecyclerViewAdapter adapter = new RecyclerViewAdapter(events,this);
//        recyclerView.setAdapter(adapter);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//    }

}
