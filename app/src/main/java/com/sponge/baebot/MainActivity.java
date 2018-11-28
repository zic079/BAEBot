package com.sponge.baebot;

import android.Manifest;
import android.app.ActivityOptions;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener,PopupMenu.OnMenuItemClickListener {

    // navigation drawer switch
    SwitchCompat voice_switcher;
    SwitchCompat weather_switcher;
    SwitchCompat alarm_switcher;
    SwitchCompat sleep_switcher;
    SwitchCompat quote_switcher;

    private Button eventBtn;
    private Button calendarBtn;
    private Button weatherBtn;
    private TextView sentence;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;

    // Projection array. Creating indices for this array instead of doing
    // dynamic lookups improves performance.
    // https://developer.android.com/reference/android/provider/CalendarContract.Events
    public static final String[] EVENT_PROJECTION = new String[] {
            CalendarContract.Events.CALENDAR_ID,                  // 0
            CalendarContract.Events.TITLE,                        // 1
            CalendarContract.Events.DESCRIPTION,                  // 2
            CalendarContract.Events.DTSTART,                      // 3
            CalendarContract.Events.DTEND,                        // 4
    };

    // The indices for the projection array above.
    private static final int PROJECTION_ID_INDEX = 0;
    private static final int PROJECTION_TITLE_INDEX = 1;
    private static final int PROJECTION_DESCRIPTION_INDEX = 2;
    private static final int PROJECTION_TIMESTART_INDEX = 3;
    private static final int PROJECTION_TIMEEND_INDEX = 4;

    // Initialize client for authorization
    private GoogleSignInClient mGoogleSignInClient;         // Google sign in client
    private FirebaseAuth mAuth;                             // Firebase authorization

    private static final int PERMISSION_REQUEST_CODE = 100;

    //private CalendarQueryHandler handler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Configure google login in to access token
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // record firebase and google client instance
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();

        // checking / request user permission for calendar provider
        requestPermission();

        // call setupNavView to initialized navigation tab
        setupNavView();

        // button on main content
        findViewById(R.id.eventBtn).setOnClickListener(this);
        findViewById(R.id.showCalendarBtn).setOnClickListener(this);
       // findViewById(R.id.taskBtn).setOnClickListener(this);
        findViewById(R.id.weatherBtn).setOnClickListener(this);

        // read calendar data with AsyncQueryHandler
        //ArrayList<String> calendarData = readEvent();
        //initRecyclerView(calendarData);

/*
        // show today's event
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);

        Calendar startDate_offset = new GregorianCalendar(year,month,dayOfMonth-1);
        Calendar startDate = new GregorianCalendar(year,month,dayOfMonth);
        Calendar endDate = new GregorianCalendar(year,month,dayOfMonth+1);

        handler = new CalendarQueryHandler(this, this.getContentResolver()) {};
        handler.readEvent(startDate_offset, startDate, endDate);
*/

        tabLayout = (TabLayout)findViewById(R.id.tablayout);
        viewPager = (ViewPager)findViewById(R.id.viewpager);
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(new FragmentEvent(),"Event");
        viewPagerAdapter.addFragment(new FragmentTask(),"Task");
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

/*
    private void initRecyclerView(ArrayList<String> events) {
        RecyclerView recyclerView = findViewById(R.id.main_recycler);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(events,this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
*/

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    /* This is the option menu on the top right, we are not using it for now
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    */

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    // Handle navigation drawer switches here.
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.voice_switch) {
            voice_switcher.setChecked(!voice_switcher.isChecked());
            Snackbar.make(item.getActionView(), (voice_switcher.isChecked()) ? "Voice On" : "Voice Off", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
        }
        if (id == R.id.weather_switch) {
            weather_switcher.setChecked(!weather_switcher.isChecked());
            Snackbar.make(item.getActionView(), (weather_switcher.isChecked()) ? "Weather On" : "Weather Off", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
        }
        if (id == R.id.alarm_switch) {
            alarm_switcher.setChecked(!alarm_switcher.isChecked());
            Snackbar.make(item.getActionView(), (alarm_switcher.isChecked()) ? "Alarm On" : "Alarm Off", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
        }
        if (id == R.id.sleep_switch) {
            sleep_switcher.setChecked(!sleep_switcher.isChecked());
            Snackbar.make(item.getActionView(), (sleep_switcher.isChecked()) ? "Sleep Time On" : "Sleep Time Off", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
        }
        if (id == R.id.quote_switch) {
            quote_switcher.setChecked(!quote_switcher.isChecked());
            Snackbar.make(item.getActionView(), (quote_switcher.isChecked()) ? "Daily Quote On" : "Daily Quote Off", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        //drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View v) {
        eventBtn = (Button)findViewById(R.id.eventBtn);
        calendarBtn = (Button)findViewById(R.id.showCalendarBtn);
        weatherBtn = (Button)findViewById(R.id.weatherBtn);
        sentence = (TextView)findViewById(R.id.sentence);
        switch (v.getId()) {
            case R.id.signOutButton:
                signOut();
                break;

            case R.id.eventBtn:
                if ((eventBtn.getText()).equals("events/tasks")) {
                    eventBtn.setText("Add events");
                    calendarBtn.setText("Add tasks");
                    weatherBtn.setText("Return");
                    sentence.setText("Would you like to add a new event or task?");
                } else {
                    switchActivity(CalendarActivity.class);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
                break;

            case R.id.showCalendarBtn:
                if (calendarBtn.getText().equals("Add tasks")){
                    FirebaseUser currentUser = mAuth.getCurrentUser();
                    String userId = currentUser.getUid();
                    User myUser = new User(currentUser.getDisplayName(),currentUser.getEmail() );
                    Intent i = new Intent(MainActivity.this, TaskActivity.class);
                    i.putExtra("userId",userId);
                    i.putExtra("user", myUser);
                    startActivity(i);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }else {
                    switchActivity(ShowCalendarActivity.class);
                }
                break;

            case R.id.weatherBtn:
                if (weatherBtn.getText().equals("Return")) {
                    eventBtn.setText("events/tasks");
                    calendarBtn.setText("Show Calendar");
                    weatherBtn.setText("Weather");
                    sentence.setText("What would you like assistance on?");
                }
                else
                    switchActivity(WeatherActivity.class);
                break;
        }
    }

    private void updateUserInfo(FirebaseUser user, NavigationView navView) {
        View headerView = navView.getHeaderView(0);

        TextView userNameText = (TextView)headerView.findViewById(R.id.userName);
        TextView userEmailText = (TextView)headerView.findViewById(R.id.userEmail);
        userNameText.setText(user.getDisplayName());
        userEmailText.setText(user.getEmail());
    }

    // signOut function - sign out from current account and return to sign in page
    private void signOut() {
        // Firebase sign out
        mAuth.signOut();

        // Google sign out
        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    // switch to sign in activity while complete signout from google
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        switchActivity(SignInActivity.class);
                    }
                });
    }


    // switch Activity utility, switching to new activity given by param
    private void switchActivity(final Class<? extends Activity> targetActivity) {
        Intent intent = new Intent(this, targetActivity);
        startActivity(intent);
    }

    // helper function to ensure app has user permission to read/write calendar
    private void requestPermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            // checking permission before reading and writing calendar
            if (checkSelfPermission(Manifest.permission.READ_CALENDAR)
                    == PackageManager.PERMISSION_DENIED) {
                Log.d("permission", "permission denied to read calendar");
                String[] permissions = {Manifest.permission.READ_CALENDAR};
                Log.d("permission", "requesting permission");
                requestPermissions(permissions, PERMISSION_REQUEST_CODE);
            }

            if (checkSelfPermission(Manifest.permission.WRITE_CALENDAR)
                    == PackageManager.PERMISSION_DENIED) {
                Log.d("permission", "permission denied to write calendar");
                String[] permissions = {Manifest.permission.WRITE_CALENDAR};
                Log.d("permission", "requesting permission");
                requestPermissions(permissions, PERMISSION_REQUEST_CODE);
            }
        }
    }

    // setUp navigation tab view
    private void setupNavView() {
        // get navigation view component
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //get the menu item from the navigation view
        Menu menu = navigationView.getMenu();

        // update user info on navigation tab
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUserInfo(currentUser, navigationView);

        // set up button on click listener
        View headerView = navigationView.getHeaderView(0);
        headerView.findViewById(R.id.signOutButton).setOnClickListener(this);

        //toolbar for navigation drawer
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);

        // What is this pieces of code for? Doesn't seen like we have a fab.
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

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();



        //Voice
        MenuItem menuItem_voice = menu.findItem(R.id.voice_switch);
        View actionView_voice = menuItem_voice.getActionView();

        voice_switcher = actionView_voice.findViewById(R.id.switcher_drawer);
        voice_switcher.setChecked(true);
        voice_switcher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //voice_switcher.setChecked(!voice_switcher.isChecked());
                Snackbar.make(v, (voice_switcher.isChecked()) ? "Voice On" : "Voice Off", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
            }
        });

        //Weather
        MenuItem menuItem_weather = menu.findItem(R.id.weather_switch);
        View actionView_weather = menuItem_weather.getActionView();

        weather_switcher = actionView_weather.findViewById(R.id.switcher_drawer);
        weather_switcher.setChecked(true);
        weather_switcher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, (weather_switcher.isChecked()) ? "Weather On" : "Weather Off", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
            }
        });

        //Alarm
        MenuItem menuItem_alarm = menu.findItem(R.id.alarm_switch);
        View actionView_alarm = menuItem_alarm.getActionView();

        alarm_switcher = actionView_alarm.findViewById(R.id.switcher_drawer);
        alarm_switcher.setChecked(true);
        alarm_switcher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, (alarm_switcher.isChecked()) ? "Alarm On" : "Alarm Off", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
            }
        });

        //Sleep
        MenuItem menuItem_sleep = menu.findItem(R.id.sleep_switch);
        View actionView_sleep = menuItem_sleep.getActionView();

        sleep_switcher = actionView_sleep.findViewById(R.id.switcher_drawer);
        sleep_switcher.setChecked(true);
        sleep_switcher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, (sleep_switcher.isChecked()) ? "Sleep Time On" : "Sleep Time Off", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
            }
        });

        //Quote
        MenuItem menuItem_quote = menu.findItem(R.id.quote_switch);
        View actionView_quote = menuItem_quote.getActionView();

        quote_switcher = actionView_quote.findViewById(R.id.switcher_drawer);
        quote_switcher.setChecked(true);
        quote_switcher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, (quote_switcher.isChecked()) ? "Daily Quote On" : "Daily Quote Off", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
            }
        });
    }

    public void showPopup(View v) {
        PopupMenu popupMenu = new PopupMenu(this,v);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.inflate(R.menu.popup_menu);
        popupMenu.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit:
                Toast.makeText(this, "Hello Edit!", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.delete:
                Toast.makeText(this, "Hello Delete!", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return false;
        }
    }

    /*
    ////// TESTING ONLY - NOT AsyncQueryHandler
    private ArrayList<String> readEvent() {

        ContentResolver cr = getContentResolver();

        // use CalendarContract.Instances for read data on calendar (rather than owner info)
        Uri CALENDAR_URI = Uri.parse("content://com.android.calendar/events");
        Cursor cur = null;

        // building selection - the start and end time range for calendar provider
        Calendar calendarStart= Calendar.getInstance();
        calendarStart.set(2018,10,10,0,0); //Note that months start from 0 (January)
        Calendar calendarEnd= Calendar.getInstance();
        calendarEnd.set(2019,2,1,0,0); //Note that months start from 0 (January)


        // set selection and selectionArgs as null
        String selection = "((dtstart >= " + calendarStart.getTimeInMillis() + ") AND (dtend <= " + calendarEnd.getTimeInMillis()+"))";
        String[] selectionArgs = null;

        //cur = cr.query(CALENDAR_URI, new String[] { "calendar_id", "title", "description",
        //        "dtstart", "dtend", "eventLocation" }, selection, selectionArgs, null);

        cur = cr.query(CALENDAR_URI, EVENT_PROJECTION, selection, selectionArgs, CalendarContract.Events.DTSTART + " ASC");
        ArrayList<String> calendarData = new ArrayList<>();

        if(cur.getCount() > 0) {
            Log.d("readEvent", "events found");

            cur.moveToFirst();
            while (cur.moveToNext()) {
                // information of event
                String eventTitle;
                String eventBeginMill;
                String eventBeginDate;

                // Get the field values
                eventTitle = cur.getString(PROJECTION_TITLE_INDEX);
                eventBeginMill = cur.getString(PROJECTION_TIMESTART_INDEX);
                eventBeginDate = milliToDate(eventBeginMill);

                // Building string of current cursor data
                // String currentData = String.format("Calendar ID: %s\nDisplay Name: %s\nAccount Name: %s\nOwner Name: %s", calID, displayName, accountName, ownerName);
                String currentData = String.format("Event Name: %s\nBegin Time: %s", eventTitle, eventBeginDate);
                Log.d("readEvent", currentData);
                calendarData.add(currentData);
            }
        }

        //ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1, calendarData);
        //listView.setAdapter(stringArrayAdapter);

        return calendarData;
    }


    // helper function - convert millisecond to readable date
    private String milliToDate(String milliSec) {
        String date;            // date convert from millisecond

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendarTemp = Calendar.getInstance();
        calendarTemp.setTimeInMillis(Long.parseLong(milliSec));
        date = formatter.format(calendarTemp.getTime());

        return date;
    }
    */
}


