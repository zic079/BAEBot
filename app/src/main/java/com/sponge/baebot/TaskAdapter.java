package com.sponge.baebot;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import static android.support.v4.content.ContextCompat.startActivity;


public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {




    public List<Task> getmTask() {
        return mTask;
    }

    public void setmTask(List<Task> mTask) {
        this.mTask = mTask;
    }

    private List<Task> mTask;
    private LayoutInflater mInflater;
    private ItemClickListener  mClickListener;

    public TaskAdapter(Context context, List<Task> data){
        this.mInflater = LayoutInflater.from(context);
        this.mTask = data;
    }

    @NonNull
    @Override
    public TaskAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = mInflater.inflate(R.layout.list_events, parent, false);
        return new ViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull final TaskAdapter.ViewHolder holder, int position) {
        Task t = mTask.get(position);
        holder.myTextView.setText(t.getTitle());
        //holder.checkbox.setChecked(false);
    }

    @Override
    public int getItemCount() {
        return mTask == null ? 0 : mTask.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView myTextView;
        //CheckBox checkbox;

        public ViewHolder(final View itemView) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.event);
            //checkbox = (CheckBox) itemView.findViewById(R.id.taskCompleteCheckBox);
            //checkbox.setClickable(false);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }

//        public void setCheckbox(boolean checked){
//            checkbox.setChecked(checked);
//        }
    }

    public String getItem(int id) {
        return mTask.get(id).getTaskId();
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }


    public interface ItemClickListener{
        void onItemClick(View view, int position);
    }


    public void removeAt(int position) {
        mTask.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mTask.size());
    }
}
