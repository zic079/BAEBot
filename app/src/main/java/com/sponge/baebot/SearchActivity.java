package com.sponge.baebot;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity
        implements View.OnClickListener,TaskAdapter.ItemClickListener{
    private ArrayList<com.sponge.baebot.Task> taskList = new ArrayList<>();
    private TaskAdapter adapter;
    private RecyclerView recyclerView;
    private TextInputEditText textInputEditText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
//        textInputEditText = findViewById(R.id.search_text);
//        recyclerView = findViewById(R.id.search_recycleView);
//        recyclerView.setLayoutManager(new LinearLayoutManager(SearchActivity.this));
//        adapter = new TaskAdapter(SearchActivity.this, taskList);
//        adapter.setClickListener(SearchActivity.this);
//////                recyclerViewAdapter = new RecyclerViewAdapter(strTasks, TaskActivity.this);
//////                recyclerView.setAdapter(recyclerViewAdapter);
//        recyclerView.setAdapter(adapter);
//
//        FragmentTask myTaskFrag = new FragmentTask();
//        this.taskList = myTaskFrag.returnTasks();
    }

    @Override
    public void onClick(View v){

    }

    @Override
    public void onItemClick(View view, final int position) {

    }

    protected void showSearchedTask(){

    }



}
