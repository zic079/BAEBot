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

import java.util.ArrayList;

public class FragmentTask extends Fragment {
    private View v;
    private RecyclerView recyclerView;
    private ArrayList<String> tasks;

    public FragmentTask() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v=inflater.inflate(R.layout.task_fragment,container,false);
        recyclerView = (RecyclerView)v.findViewById(R.id.task_recyclerView);
        RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(tasks,getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(recyclerViewAdapter);
        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tasks = new ArrayList<>();
        tasks.add("bbbb1");
        tasks.add("bbbb2");
        tasks.add("bbbb3");
        tasks.add("bbbb4");

    }
}
