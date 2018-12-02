package com.sponge.baebot;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity
        implements View.OnClickListener,TaskAdapter.ItemClickListener{
    private ArrayList<com.sponge.baebot.Task> taskList = new ArrayList<>();
    private ArrayList<com.sponge.baebot.Task> tempTaskList = new ArrayList<>();
    private TaskAdapter adapter;
    private RecyclerView recyclerView;
    private TextInputEditText textInputEditText;
    private Button searchButton;
    private String userId;


    private static FirebaseDatabase database = FirebaseDatabase.getInstance(); // Firebase databse
    private static DatabaseReference mDatabase = database.getReference();


    private interface FirebaseCallback{
        void onCallback(ArrayList<com.sponge.baebot.Task> list);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar =  findViewById(R.id.toolbar);
        textInputEditText = findViewById(R.id.search_text);
        searchButton = findViewById(R.id.btn_search);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("search task", "button clicked!!!!");
                tempTaskList.clear();
                recyclerView = findViewById(R.id.search_recyclerView);
                recyclerView.setLayoutManager(new LinearLayoutManager(SearchActivity.this));
                getAllTasks(new FirebaseCallback() {
                    @Override
                    public void onCallback(ArrayList<Task> list) {
                        String keyWord = textInputEditText.getText().toString();
                        if (keyWord != ""){
                            keyWord = keyWord.toLowerCase();
                            for (Task t : list){
                                Log.e("hello!!!!", t.getTitle());
                                String description  = t.getDescription().toLowerCase();
                                String title = t.getTitle().toLowerCase();
                                if ((description.contains(keyWord) || title.contains(keyWord))){
                                    tempTaskList.add(t);
                                }
                            }
                        }
                        Log.e("list length", Integer.toString(tempTaskList.size()));
                        if (tempTaskList.size() > list.size()){
                            tempTaskList.clear();
                            Toast.makeText(SearchActivity.this,
                                    "You submit the search request to often. Please try again later",
                                    Toast.LENGTH_SHORT).show();
                        }
                        adapter = new TaskAdapter(SearchActivity.this, tempTaskList);
                        adapter.setClickListener(SearchActivity.this);
                        recyclerView.setAdapter(adapter);
                    }
                });
            }
        });
        setSupportActionBar(toolbar);

        Log.e("search task", "created");

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            userId = extras.getString("userId");
        }
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.btn_search:

                break;
        }
    }

    @Override
    public void onItemClick(View view, final int position) {

    }

    private void getAllTasks(final SearchActivity.FirebaseCallback firebaseCallback){

        mDatabase.child("task").child(userId).addListenerForSingleValueEvent(
                new ValueEventListener(){
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.d("Search", "onDataChange!");
                        taskList.clear();
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            Task t = ds.getValue(Task.class);
                            Log.d("search-getAllTasks",t.toString());
                            taskList.add(t);
                            Log.d("search-getAllTasks",Integer.toString(taskList.size()));
                        }
                        firebaseCallback.onCallback(taskList);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }



}
