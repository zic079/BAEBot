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

public class FragmentEvent extends Fragment {

    private View v;
    private RecyclerView recyclerView;
    private ArrayList<String> events;



    public FragmentEvent() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v=inflater.inflate(R.layout.event_fragment,container,false);
        recyclerView = (RecyclerView)v.findViewById(R.id.event_recyclerView);
        RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(events,getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(recyclerViewAdapter);
        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        events = new ArrayList<>();
        events.add("aaaaa1");
        events.add("aaaaa2");
        events.add("aaaaa3");
        events.add("aaaaa4");


    }
}
