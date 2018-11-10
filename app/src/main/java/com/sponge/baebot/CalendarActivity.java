package com.sponge.baebot;

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
import android.widget.EditText;
import android.widget.TextView;

import com.sponge.baebot.CalendarQueryHandler;


import java.util.ArrayList;

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

    private Button daysButton;
    private EditText daysEdit;
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

        daysButton = (Button)findViewById(R.id.buttonGetDays);
        daysEdit = (EditText)findViewById(R.id.editTextDays);

        daysButton.setOnClickListener(this);

        // initialize new CalendarQueryHandler to handle calendar CRUD operation
        handler = new CalendarQueryHandler(this, this.getContentResolver()) {};
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonGetDays:
                queryDays = Integer.parseInt(daysEdit.getText().toString());

                // call CalendarQueryHandler to get event
                handler.readEvent(queryDays);
                break;
        }
    }

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
