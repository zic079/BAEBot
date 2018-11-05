package com.sponge.baebot;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private GoogleSignInClient mGoogleSignInClient;         // Google sign in client
    private FirebaseAuth mAuth;                             // Firebase authorization

    // Projection array. Creating indices for this array instead of doing
    // dynamic lookups improves performance.
    public static final String[] EVENT_PROJECTION = new String[] {
        CalendarContract.Calendars._ID,                           // 0
        CalendarContract.Calendars.ACCOUNT_NAME,                  // 1
        CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,         // 2
        CalendarContract.Calendars.OWNER_ACCOUNT                  // 3
    };


    final String[] INSTANCE_PROJECTION = new String[] {
        CalendarContract.Instances.EVENT_ID,      // 0
        CalendarContract.Instances.BEGIN,         // 1
        CalendarContract.Instances.TITLE          // 2
    };




    // The indices for the projection array above.
    private static final int PROJECTION_ID_INDEX = 0;
    private static final int PROJECTION_ACCOUNT_NAME_INDEX = 1;
    private static final int PROJECTION_DISPLAY_NAME_INDEX = 2;
    private static final int PROJECTION_OWNER_ACCOUNT_INDEX = 3;

    private static final int INSTANCE_ID_INDEX = 0;
    private static final int INSTANCE_BEGIN_INDEX = 1;
    private static final int INSTANCE_TITLE_INDEX = 2;


    private static final int PERMISSION_REQUEST_CODE = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Configure google login in to access token
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();

        // update user info on navigation tab
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUserInfo(currentUser, navigationView);

        // set up button on click listener
        View headerView = navigationView.getHeaderView(0);
        headerView.findViewById(R.id.signOutButton).setOnClickListener(this);

        // check permission
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

        // read calendar data with AsyncQueryHandler
        ArrayList<String> calendarData = new ArrayList<>();
        calendarData = readEvent();

        if(calendarData.size() > 0) {
            TextView successText = (TextView)findViewById(R.id.event1);
            successText.setText(calendarData.get(0));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.signOutButton:
                signOut();
                break;
        }
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

    /*
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
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_day) {
            // Handle the day action
        }
        else if (id == R.id.nav_week) {

        }
        else if (id == R.id.nav_month) {

        }
        else if (id == R.id.nav_webreg) {

        }
        else if (id == R.id.nav_settings) {

        }
        else if (id == R.id.nav_send) {

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void updateUserInfo(FirebaseUser user, NavigationView navView) {
        View headerView = navView.getHeaderView(0);

        TextView userNameText = (TextView)headerView.findViewById(R.id.userName);
        TextView userEmailText = (TextView)headerView.findViewById(R.id.userEmail);
        userNameText.setText(user.getDisplayName());
        userEmailText.setText(user.getEmail());
    }

    private void signOut() {
        // Firebase sign out
        mAuth.signOut();

        // Google sign out
        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        switchActivity(SignInActivity.class);
                    }
                });

        Intent intent = new Intent(this, SignInActivity.class);
        startActivity(intent);
    }

    private void switchActivity(final Class<? extends Activity> targetActivity) {
        Intent intent = new Intent(this, targetActivity);
        startActivity(intent);
    }

    ////// TESTING ONLY - NOT AsyncQueryHandler
    private ArrayList<String> readEvent() {

        // still making error debug message, move to onCreate seems to fix, don't know why
        // but whatever..... it worked as for now
        /*
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            // checking permission before reading and writing calendar
            if (checkSelfPermission(Manifest.permission.READ_CALENDAR)
                    == PackageManager.PERMISSION_DENIED) {
                Log.d("permission", "permission denied to read calendar");
                String[] permissions = {Manifest.permission.READ_CALENDAR};
                Log.d("permission", "requesting permission");
                requestPermissions(permissions, PERMISSION_REQUEST_CODE);
            }
        }
        */


        // make up a time range for searching event
        long startMillis = 0;
        long endMillis = 0;
        Calendar beginTime = Calendar.getInstance();
        beginTime.set(2018, 11, 02, 6, 00);
        startMillis = beginTime.getTimeInMillis();
        Calendar endTime = Calendar.getInstance();
        endTime.set(2018, 11, 15, 6, 00);
        endMillis = endTime.getTimeInMillis();


        ContentResolver cr = getContentResolver();

        // use CalendarContract.Instances for read data on calendar (rather than owner info)
        //Uri uri = CalendarContract.Calendars.CONTENT_URI;
        //Uri uri = CalendarContract.Instances.CONTENT_URI;
        Uri CALENDAR_URI = Uri.parse("content://com.android.calendar/events");
        /*
        Uri.Builder builder = CalendarContract.Instances.CONTENT_URI.buildUpon();
        ContentUris.appendId(builder, startMillis);
        ContentUris.appendId(builder, endMillis);
        */

        Cursor cur = null;

        // ---- current design return all calendar event (no filtering)
        // could add the feature in the future - pass in from the parameter
        /*
        String selection = "((" + Calendars.ACCOUNT_NAME + " = ?) AND ("
                + Calendars.ACCOUNT_TYPE + " = ?) AND ("
                + Calendars.OWNER_ACCOUNT + " = ?))";
        String[] selectionArgs = new String[] {"hera@example.com", "com.example",
                "hera@example.com"};
        */

        // set selection and selectionArgs as null
        String selection = null;
        String[] selectionArgs = null;

        cur = cr.query(CALENDAR_URI, new String[] { "calendar_id", "title", "description",
                "dtstart", "dtend", "eventLocation" }, selection, selectionArgs, null);
        ArrayList<String> calendarData = new ArrayList<>();

        if(cur.getCount() > 0) {
            Log.d("readEvent", "events found");

            cur.moveToFirst();
            while (cur.moveToNext()) {
                /*
                long calID = 0;
                String displayName = null;
                String accountName = null;
                String ownerName = null;

                // Get the field values
                calID = cur.getLong(PROJECTION_ID_INDEX);
                displayName = cur.getString(PROJECTION_DISPLAY_NAME_INDEX);
                accountName = cur.getString(PROJECTION_ACCOUNT_NAME_INDEX);
                ownerName = cur.getString(PROJECTION_OWNER_ACCOUNT_INDEX);
                */

                /*
                long calID = 0;
                String eventBegin = null;
                String eventTitle = null;

                // Get the field values
                calID = cur.getLong(INSTANCE_ID_INDEX);
                eventBegin = cur.getString(INSTANCE_BEGIN_INDEX);
                eventTitle = cur.getString(INSTANCE_TITLE_INDEX);
                */

                String eventName = null;
                String eventBeginMill = null;
                String eventBegin = null;
                String eventDescription = null;

                // Get the field values
                eventName = cur.getString(1);

                eventBeginMill = cur.getString(3);
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                Calendar calendarTemp = Calendar.getInstance();
                calendarTemp.setTimeInMillis(Long.parseLong(eventBeginMill));
                eventBegin = formatter.format(calendarTemp.getTime());

                eventDescription = cur.getString(2);


                // building string of current cursor data
                //String currentData = String.format("Calendar ID: %s\nDisplay Name: %s\nAccount Name: %s\nOwner Name: %s", calID, displayName, accountName, ownerName);
                String currentData = String.format("Event Name: %s\nBegin Time: %s\nEvent Description: %s", eventName, eventBegin, eventDescription);
                Log.d("readEvent", currentData);
                calendarData.add(currentData);
            }
        }

        //ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1, calendarData);
        //listView.setAdapter(stringArrayAdapter);

        return calendarData;
    }
}

