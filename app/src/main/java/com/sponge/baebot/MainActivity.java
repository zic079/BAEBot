
package com.sponge.baebot;

import android.Manifest;
import android.app.ActivityOptions;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

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

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    // navigation drawer switch
    SwitchCompat voice_switcher;
    SwitchCompat weather_switcher;
    //SwitchCompat alarm_switcher;
    SwitchCompat sleep_switcher;
    //SwitchCompat quote_switcher;

    private ImageButton Waifu;
    private TextView sentence;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;

    // Preprocess for voice
    private static MediaPlayer rv;
    int rId = R.raw.how_are_u_doing_today;

    private static FirebaseDatabase database = FirebaseDatabase.getInstance(); // Firebase databse
    private static DatabaseReference mDatabase = database.getReference();

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
    private FirebaseUser mUser;

    private static final int PERMISSION_REQUEST_CODE = 100;

    //private CalendarQueryHandler handler;

    private ArrayList<com.sponge.baebot.Task> taskList = new ArrayList<>();
    private ArrayList<com.sponge.baebot.Task> myList = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("Main activity", "onCreate called");

        // Configure google login in to access token
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // record firebase and google client instance
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        // checking / request user permission for calendar provider
        requestPermission();

        // call setupNavView to initialized navigation tab
        setupNavView();

        // button on main content
        findViewById(R.id.taskBtn).setOnClickListener(this);
        findViewById(R.id.showCalendarBtn).setOnClickListener(this);
        findViewById(R.id.tutorial).setOnClickListener(this);
        // findViewById(R.id.taskBtn).setOnClickListener(this);
        findViewById(R.id.weatherBtn).setOnClickListener(this);
        findViewById(R.id.Waifu).setOnClickListener(this);
        findViewById(R.id.search_bar).setOnClickListener(this);


        // Get current System time to play different greetings.
        sentence = (TextView)findViewById(R.id.sentence);
        Calendar vu = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH");
        String time = sdf.format(vu.getTime());
        int resultTime = Integer.parseInt(time);

        Random rand = new Random();
        int n = rand.nextInt(2) + 1;

            switch (n) {
                case 1:
                    rId = R.raw.motivation;
                    sentence.setText("Today is your day.");
                    break;
                case 2:
                    rId = R.raw.motivation2;
                    sentence.setText("Optimism is the faith that leads to achievement");
                    break;
            }

        rv = MediaPlayer.create(MainActivity.this, rId);

        if(voice_switcher.isChecked()) {
            rv.start();
        }

        rv.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                rv.release();
                rv = null;

            }
        });



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
        Waifu = findViewById(R.id.Waifu);
        tabLayout = (TabLayout)findViewById(R.id.tablayout);
        viewPager = (ViewPager)findViewById(R.id.viewpager);
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        FragmentEvent eventFrag = new FragmentEvent();
        FragmentTask taskFrag = new FragmentTask();
        viewPagerAdapter.addFragment(eventFrag,"Event");
        viewPagerAdapter.addFragment(taskFrag,"Task");
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);


        Log.d("MAIN ACT", "### Event Count: " + eventFrag.getCount());
        Log.d("MAIN ACT", "### Task Count: " + taskFrag.getCount());

        NotificationScheduler.setReminder(MainActivity.this, AlarmReceiver.class);

    }

    @Override
    protected void onStart(){
        super.onStart();

        Log.d("MAIN ACTIVITY", "$$$onStart: ");
        NotificationScheduler.setReminder(MainActivity.this, AlarmReceiver.class);

    }
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
        /*
        if (id == R.id.alarm_switch) {
            alarm_switcher.setChecked(!alarm_switcher.isChecked());
            Snackbar.make(item.getActionView(), (alarm_switcher.isChecked()) ? "Alarm On" : "Alarm Off", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
        }
        */
        if (id == R.id.sleep_switch) {
            sleep_switcher.setChecked(!sleep_switcher.isChecked());
            Snackbar.make(item.getActionView(), (sleep_switcher.isChecked()) ? "Sleep Time On" : "Sleep Time Off", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
        }
        /*
        if (id == R.id.quote_switch) {
            quote_switcher.setChecked(!quote_switcher.isChecked());
            Snackbar.make(item.getActionView(), (quote_switcher.isChecked()) ? "Daily Quote On" : "Daily Quote Off", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
        }
        */

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        //drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View v) {
        sentence = (TextView)findViewById(R.id.sentence);
        final View search = findViewById(R.id.search);
        final View waifu = findViewById(R.id.Waifu);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        String userId = currentUser.getUid();
        switch (v.getId()) {
            case R.id.tutorial:
                switchActivity(TutorialActivity.class);
                break;
            case R.id.search_bar:
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                intent.putExtra("userId", userId);
                ActivityOptions options1 = ActivityOptions
                        .makeSceneTransitionAnimation(this, search, "search");
                startActivity(intent, options1.toBundle());
                break;

            case R.id.signOutButton:
                signOut();
                break;

            case R.id.taskBtn:
                if (mAuth == null)
                    throw new NullPointerException();
                User myUser = new User(currentUser.getDisplayName(),currentUser.getEmail() );
                Intent i = new Intent(MainActivity.this, TaskActivity.class);
                i.putExtra("userId",userId);
                i.putExtra("user", myUser);
                ActivityOptions options = ActivityOptions
                        .makeSceneTransitionAnimation(this, waifu, "waifu");
                startActivity(i, options.toBundle());
                break;

            case R.id.showCalendarBtn:
                switchActivity(ShowCalendarActivity.class);
                break;

            case R.id.weatherBtn:
//                Intent iii = new Intent(MainActivity.this, TutorialActivity.class);
//                startActivity(iii);
                switchActivity(WeatherActivity.class);
                break;

            case R.id.Waifu:
                // Prevent multiple media being played simultaneously.
                if(voice_switcher.isChecked() && rv != null) {
                    rv.reset();
                }

                Calendar vu = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("HH");
                String time = sdf.format(vu.getTime());
                int resultTime = Integer.parseInt(time);
                //final MediaPlayer gt;
                //int greet;
                Random rand = new Random();
                int n = rand.nextInt(2) + 1;

                switch(n) {
                    case 1:
                        if (resultTime < 12 && resultTime > 6) {
                            sentence.setText("Good Morning");
                            rId = R.raw.good_morning;
                        } else if (resultTime < 18 && resultTime > 12) {
                            sentence.setText("Good Afternoon");
                            rId = R.raw.good_afternoon;
                        } else {
                            sentence.setText("Good Evening");
                            rId = R.raw.good_evening;
                        }
                        break;

                    case 2:
                        sentence.setText("How are u doing today");
                        rId = R.raw.how_are_u_doing_today;

                    case 3:
                        sentence.setText("Start early, start often");
                        rId = R.raw.quote;
                }

                rv = MediaPlayer.create(MainActivity.this, rId);
                if(voice_switcher.isChecked()) {
                    rv.start();
                }
                rv.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        rv.release();
                        rv = null;
                    }
                });

                break;
        }
    }

    private void updateUserInfo(FirebaseUser user, NavigationView navView) {
        View headerView = navView.getHeaderView(0);
        ImageView userImage = (ImageView)headerView.findViewById(R.id.userImage);
        TextView userNameText = (TextView)headerView.findViewById(R.id.userName);
        TextView userEmailText = (TextView)headerView.findViewById(R.id.userEmail);
        Picasso.get().load(user.getPhotoUrl()).resize(170, 170).into(userImage);
        userNameText.setText(mUser.getDisplayName());
        userEmailText.setText(mUser.getEmail());
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

        /*
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
        */

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

        /*
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
        */
    }
//
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

    public String getUserId(){
        if (mAuth == null)
            throw new NullPointerException();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String userId = currentUser.getUid();
        return userId;
    }

    public DatabaseReference getmDatabaseRef(){
        return mDatabase;
    }

    public ArrayList<com.sponge.baebot.Task> getTaskList(){
        return myList;
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
