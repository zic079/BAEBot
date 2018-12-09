package com.sponge.baebot;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

public class FragmentTask extends Fragment {
    private View v;
    private RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;
    private ArrayList<com.sponge.baebot.Task> tasks = new ArrayList<>();
    private ArrayList<com.sponge.baebot.Task> taskList = new ArrayList<>();
    private ArrayList<String> strTasks = new ArrayList<>();
    private String userId;
    private static DatabaseReference mDatabase;


    private interface FirebaseCallback{
        void onCallback(ArrayList<com.sponge.baebot.Task> list);
    }

    public FragmentTask() {
    }

    public ArrayList<com.sponge.baebot.Task> returnTasks(){
        return taskList;
    }

    @Override
    public void onResume(){
        super.onResume();
        getTask(new FirebaseCallback() {
            @Override
            public void onCallback(ArrayList<com.sponge.baebot.Task> list) {
                tasks = list;
                for (Task t : tasks) {
                    strTasks.add(t.getTitle());
                }
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        v=inflater.inflate(R.layout.task_fragment,container,false);

        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity().getClass() == MainActivity.class) {
            userId = ((MainActivity) getActivity()).getUserId();
            mDatabase = ((MainActivity) getActivity()).getmDatabaseRef();
            getTask(new FirebaseCallback() {
                @Override
                public void onCallback(ArrayList<com.sponge.baebot.Task> list) {
                    tasks = list;
                    for (Task t : tasks) {

                        Timestamp time  = new Timestamp(t.getTimestamp());
                        Date date = new Date((time.getTime()+28800)*1000);
                        strTasks.add(t.getTitle() + "\n"+ date.toString());
                    }
                    recyclerView =  v.findViewById(R.id.task_recyclerView);
                    if (recyclerViewAdapter == null){
                        recyclerViewAdapter = new RecyclerViewAdapter(strTasks, getContext());
                    } else {
                        recyclerViewAdapter.notifyDataSetChanged();
                    }

                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    recyclerView.setAdapter(recyclerViewAdapter);
                }
            });
        }

    }

    public void getTask(final FirebaseCallback firebaseCallback){
        mDatabase.child("task").child(userId).addListenerForSingleValueEvent(
                new ValueEventListener(){
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        taskList.clear();
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            com.sponge.baebot.Task t = ds.getValue(com.sponge.baebot.Task.class);
                            taskList.add(t);
                        }
                        tasks.clear();
                        firebaseCallback.onCallback(taskList);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    public int getCount() {
        return strTasks.size();
    }

}
