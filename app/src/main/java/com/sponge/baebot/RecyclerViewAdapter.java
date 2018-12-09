package com.sponge.baebot;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{

    private static final String TAG = "RecyclerViewAdapter";

    private ArrayList<String> events;

    private Context context;



    public RecyclerViewAdapter(ArrayList<String> events, Context context) {
        this.events = events;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_events, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
        Log.d(TAG, "onBindViewHolder: called.");
        viewHolder.textView.setText(events.get(i));
        //viewHolder.content.setText(events.get(i));
        viewHolder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,events.get(i),Toast.LENGTH_SHORT).show();
//                View popupView = LayoutInflater.from(context).inflate(R.layout.popup_layout, null);
//                final PopupWindow popupWindow = new PopupWindow(popupView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
//                Button btnDismiss = (Button) popupView.findViewById(R.id.ib_close);
//                btnDismiss.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        popupWindow.dismiss();
//                    }
//                });
//                popupWindow.showAsDropDown(popupView, 0, 0);
            }
        });
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView textView;
        private RelativeLayout parentLayout;


        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.event);
            parentLayout = itemView.findViewById(R.id.relativeLayout);
//            btnExpand.setOnClickListener(new View.OnClickListener() {
//                @RequiresApi(api = Build.VERSION_CODES.M)
//                @Override
//                public void onClick(View v) {
//                    PopupMenu popup = new PopupMenu(btnExpand.getContext(), itemView);
//                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//                        @Override
//                        public boolean onMenuItemClick(MenuItem item) {
//                            switch (item.getItemId()) {
//                                case R.id.edit:
//                                    Toast.makeText(btnExpand.getContext(), "Hello Edit!", Toast.LENGTH_SHORT).show();
//                                    return true;
//                                case R.id.delete:
//                                    Toast.makeText(btnExpand.getContext(), "Hello Delete!", Toast.LENGTH_SHORT).show();
//                                    return true;
//                                default:
//                                    return false;
//                            }
//                        }
//                    });
//                    // here you can inflate your menu
//                    popup.inflate(R.menu.popup_menu);
//                    popup.setGravity(Gravity.RIGHT);
//
//                    popup.show();
//                }
//            });

        }

    }
}
