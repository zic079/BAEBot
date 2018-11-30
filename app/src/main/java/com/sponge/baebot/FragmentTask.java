package com.sponge.baebot;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;

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
                Log.e("On resume!!!!", list.toString());
                tasks = list;
                for (Task t : tasks) {
                    strTasks.add(t.getTitle());
                }
                Log.e("On resume!!!!", strTasks.toString());
//                recyclerViewAdapter.notifyDataSetChanged();
//                //recyclerView = (RecyclerView) v.findViewById(R.id.task_recyclerView);
////                RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(strTasks, getContext());
//                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
//                recyclerView.setAdapter(recyclerViewAdapter);
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
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
                    Log.e("get task main", list.toString());
                    tasks = list;
                    for (Task t : tasks) {
                        strTasks.add(t.getTitle());
                    }
                    Log.e("here!!!!!", strTasks.toString());
                    recyclerView = (RecyclerView) v.findViewById(R.id.task_recyclerView);
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
                        Log.e("Tasks", "on frag!");
                        taskList.clear();
                        int i = 0;
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            com.sponge.baebot.Task t = ds.getValue(com.sponge.baebot.Task.class);
                            taskList.add(t);
                            i++;
                            Log.e("frag-getAllTasks a",t.toString());
                            Log.e("frag-getAllTasks b",Integer.toString(i));
                            Log.e("frag-getAllTasks c",Integer.toString(taskList.size()));
                        }
                        tasks.clear();
                        firebaseCallback.onCallback(taskList);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }
//    private void getAllTasks(){
//        mDatabase.child("task").child(userId).addListenerForSingleValueEvent(
//                new ValueEventListener(){
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        Log.e("FragmentTask", "!!!!!!!!!!!!!!!!!!!!!");
//                        //taskList.clear();
//                        int i = 0;
//                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
////                            Log.d("Tasks", "" + ds.getKey());
//                            Task t = ds.getValue(Task.class);
////                            taskList.add(t);
////                            Task tt = (Task)t;
//                            i++;
////                            Log.d("task-getAllTasks",tt.toString());
//                            Log.e("task-getAllTasks",t.toString());
//                            Log.e("task-getAllTasks",Integer.toString(i));
//                            //taskList.add(t);
//                            //Log.d("task-getAllTasks",Integer.toString(taskList.size()));
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });
//    }
}
