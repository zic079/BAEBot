package com.sponge.baebot;

import android.app.ActivityOptions;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.UUID;


public class TaskActivity extends AppCompatActivity
        implements View.OnClickListener, TaskAdapter.ItemClickListener {
    private Button selectDate;
    private Button selectTime;
    private CheckBox checkBoxA, checkBoxB, checkBoxC;
    private EditText title, description;
    private int year, month, dayOfMonth, hour, minute;
    private Calendar calendar = Calendar.getInstance();
    private static FirebaseDatabase database = FirebaseDatabase.getInstance(); // Firebase databse
    private static DatabaseReference mDatabase = database.getReference();
    private String userId;
    private PopWindow popupWindow;
    private ArrayList<Task> taskList = new ArrayList<>();
    private ArrayList<Task> tasks = new ArrayList<>();
    private ArrayList<String> strTasks = new ArrayList<>();
    private RecyclerView recyclerView;
    private TaskAdapter adapter;
    private Task editedTask;

    private interface FirebaseCallback {
        void onCallback(ArrayList<com.sponge.baebot.Task> list);
    }

    private interface FirebaseCallbackEdit {
        void onCallback(Task taskToEdit);
    }

    public void getSingleTask(String id, final FirebaseCallbackEdit firebaseCallbackEdit) {
        mDatabase.child("task").child(userId).child(id).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        Task t = dataSnapshot.getValue(Task.class);
                        firebaseCallbackEdit.onCallback(t);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onItemClick(View view, final int position) {
        PopupMenu popup = new PopupMenu(this, view);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.view:

                        getSingleTask(adapter.getItem(position), new FirebaseCallbackEdit() {
                            @Override
                            public void onCallback(Task taskToEdit) {
                                LayoutInflater layoutInflater =
                                        (LayoutInflater) getApplicationContext()
                                                .getSystemService(LAYOUT_INFLATER_SERVICE);
                                ViewGroup container = (ViewGroup) layoutInflater
                                        .inflate(R.layout.popup_layout, null);
                                if (popupWindow != null) {
                                    popupWindow.dismiss();
                                }
                                popupWindow = new PopWindow(TaskActivity.this, taskToEdit);
                                popupWindow.show(findViewById(R.id.linearLayout_task), 0, 0);
                            }
                        });

                        return true;
                    case R.id.edit:
                        getSingleTask(adapter.getItem(position), new FirebaseCallbackEdit() {
                            @Override
                            public void onCallback(Task taskToEdit) {
                                editedTask = taskToEdit;
                                title.setText(editedTask.getTitle());
                                description.setText(editedTask.getDescription());

                                SimpleDateFormat sdf =
                                        new SimpleDateFormat("yyyy-MM-dd hh:mm");
                                Date dateAndTime = new Date((editedTask.getTimestamp() + 28800) * 1000);
                                String strDateAndTime = sdf.
                                        format(new Date((editedTask.getTimestamp() + 28800) * 1000));
                                selectDate.setText(strDateAndTime.substring(0, 10));
                                selectTime.setText(dateAndTime.toString().substring(11, 20));

                                int priority = editedTask.getPriority();
                                if (priority == 2) {
                                    checkBoxA.setChecked(true);
                                    checkBoxB.setChecked(false);
                                    checkBoxC.setChecked(false);
                                } else if (priority == 1) {
                                    checkBoxA.setChecked(false);
                                    checkBoxB.setChecked(true);
                                    checkBoxC.setChecked(false);
                                } else {
                                    checkBoxA.setChecked(false);
                                    checkBoxB.setChecked(false);
                                    checkBoxC.setChecked(true);
                                }
                            }
                        });
                        return true;

                    case R.id.delete:
                        mDatabase.child("task").child(userId).child(adapter.getItem(position))
                                .removeValue();
                        Toast.makeText(selectDate.getContext(), "Delete Successfully!",
                                Toast.LENGTH_SHORT).show();
                        adapter.removeAt(position);
                        return true;
                    case R.id.complete:
                        mDatabase.child("task").child(userId).child(adapter.getItem(position))
                                .removeValue();
                        Toast.makeText(selectDate.getContext(), "Completed!",
                                Toast.LENGTH_SHORT).show();
                        adapter.removeAt(position);
                        return true;


                    default:
                        return false;
                }
            }
        });
        // here you can inflate your menu
        popup.inflate(R.menu.popup_menu);
        popup.setGravity(Gravity.RIGHT);
        popup.show();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.startActivity(new Intent(TaskActivity.this, MainActivity.class));
        return;
    }

    public void onCheckboxClicked(View view) {

        switch (view.getId()) {

            case R.id.priority1:

                checkBoxB.setChecked(false);
                checkBoxC.setChecked(false);
                break;

            case R.id.priority2:

                checkBoxC.setChecked(false);
                checkBoxA.setChecked(false);

                break;

            case R.id.priority3:

                checkBoxA.setChecked(false);
                checkBoxB.setChecked(false);

                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        if (popupWindow != null) {
            popupWindow.dismiss();
        }

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            userId = extras.getString("userId");
            //The key argument here must match that used in the other activity
        }
        Intent intent = getIntent();

        findViewById(R.id.search_bar).setOnClickListener(this);

        checkBoxA = (CheckBox) findViewById(R.id.priority1);
        checkBoxA.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    onCheckboxClicked(buttonView);
                }
            }
        });
        checkBoxB = (CheckBox) findViewById(R.id.priority2);
        checkBoxB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    onCheckboxClicked(buttonView);
                }
            }
        });
        checkBoxC = (CheckBox) findViewById(R.id.priority3);
        checkBoxC.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    onCheckboxClicked(buttonView);
                }
            }
        });


        title = findViewById(R.id.title_input);
        description = findViewById(R.id.description);

        selectDate = findViewById(R.id.btnDate);
        //date = findViewById(R.id.tvSelectedDate);
        selectDate.setOnClickListener(this);

        selectTime = findViewById(R.id.btnTime);
        //time = findViewById(R.id.tvSelectedTime);
        selectTime.setOnClickListener(this);

        Button saveTask = findViewById(R.id.btnTask);

        Button btnReschedule = findViewById(R.id.reschedule);

        saveTask.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (addTask()) {
                    getAllTasks(new FirebaseCallback() {
                        @Override
                        public void onCallback(ArrayList<Task> list) {
                            Intent intent = getIntent();
                            startActivity(intent);
                        }
                    });
                    Toast.makeText(selectDate.getContext(), "Save Successfully",
                            Toast.LENGTH_SHORT).show();
                }

            }
        });

        getAllTasks(new FirebaseCallback() {
            @Override
            public void onCallback(ArrayList<Task> list) {
                tasks = list;
                for (Task t : tasks) {
                    strTasks.add(t.toString());
                }
                recyclerView = findViewById(R.id.task_recyclerView);
                recyclerView.setLayoutManager(new LinearLayoutManager(TaskActivity.this));
                adapter = new TaskAdapter(TaskActivity.this, tasks);
                adapter.setClickListener(TaskActivity.this);
                recyclerView.setAdapter(adapter);
            }
        });

        btnReschedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                List<Task> localTask;

                if (adapter != null) {
                    localTask = adapter.getmTask();
                    class SortByPriority implements Comparator<Task> {

                        // Used for sorting in ascending order of
                        // roll number
                        public int compare(Task a, Task b) {
                            if (b.getPriority() - a.getPriority() > 0) {
                                return 1;
                            } else if (b.getPriority() - a.getPriority() < 0) {
                                return -1;
                            } else {
                                return (int) (a.getTimestamp() - b.getTimestamp());
                            }
                        }
                    }

                    Date d = new Date();
                    long a = d.getTime();
                    Timestamp curTime = new Timestamp(a);
                    long cur = curTime.getTime() / 1000 - 28800;
                    cur = cur / 86400 * 86400;
                    ArrayList<Task> unfinished = new ArrayList<>();
                    int[] weeklyTaskCount = {0, 0, 0, 0, 0, 0, 0};

                    for (Task t : localTask) {
                        if (!t.isCompleted() && t.getTimestamp() < cur) {
                            unfinished.add(t);
                        } else if (!t.isCompleted()) {
                            int day = (int) ((t.getTimestamp() - cur) / 86400);
                            if (day > 6) {
                                day = 6;
                            }
                            weeklyTaskCount[day] += 1;
                        }
                    }


                    Collections.sort(unfinished, new SortByPriority());

                    int totalFinished = 0;
                    int totalUnfinished = unfinished.size();
                    for (int i = 0; i < 7; i++) {
                        totalFinished += weeklyTaskCount[i];
                    }
                    int total = totalUnfinished + totalFinished;
                    int avg = (int) total / 7 + 1;
                    int curDay = 0;

                    for (Task t : unfinished) {
                        while (weeklyTaskCount[curDay] >= avg) {
                            curDay++;
                        }
                        t.setTimestamp(curDay * 86400 + cur + 28800);
                        mDatabase.child("task").child(userId).child(t.getTaskId()).setValue(t);
                    }

                    ArrayList<Task> toshowList = unfinished;
                    for (Task t : localTask) {
                        boolean exist = false;
                        for (Task t2 : toshowList) {
                            if (t2.getTaskId() == t.getTaskId()) {
                                exist = true;
                                break;
                            }
                        }
                        if (!exist) {
                            toshowList.add(t);
                        }
                    }

                    recyclerView = findViewById(R.id.task_recyclerView);
                    recyclerView.setLayoutManager(
                            new LinearLayoutManager(TaskActivity.this));
                    adapter = new TaskAdapter(TaskActivity.this, toshowList);
                    adapter.setClickListener(TaskActivity.this);
                    recyclerView.setAdapter(adapter);

                }
            }


        });
    } // end of onCreate

    @Override
    public void onClick(View v) {
        final View search = findViewById(R.id.search);

        switch (v.getId()) {
            case R.id.search_bar:
                Intent intent = new Intent(TaskActivity.this, SearchActivity.class);
                ActivityOptions options1 = ActivityOptions
                        .makeSceneTransitionAnimation(this, search,
                                "search");
                intent.putExtra("userId", userId);
                startActivity(intent, options1.toBundle());
                break;

            case R.id.btnDate:
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(TaskActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month,
                                                  int day) {
                                selectDate.setText(year + "-" + (month + 1) + "-" + day);
                            }
                        }, year, month, dayOfMonth);
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
                datePickerDialog.show();
                break;
            case R.id.btnTime:
                hour = calendar.get(Calendar.HOUR);
                minute = calendar.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(TaskActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                String hourStr = Integer.toString(hourOfDay);
                                String minuteStr = Integer.toString(minute);
                                if(hourOfDay < 10) hourStr = "0" + hourStr;
                                if(minute < 10) minuteStr = "0" + minuteStr;
                                selectTime.setText(hourStr + ":" + minuteStr);
                            }
                        }, hour, minute, true);
                timePickerDialog.show();
                break;

        }
    }

    private boolean addTask() {
        String strTitle = "";
        String strDescription = description.getText().toString();
        String strDate = "";
        String strTime = "";

        if (selectDate != null && selectTime != null) {
            strDate = selectDate.getText().toString();
            strTime = selectTime.getText().toString();
            strTitle = title.getText().toString().replaceAll("\n", "");
        }

        if (strDate.length() != 0 && strTime.length() != 0 && strTitle.length() != 0) {

            // xxxx-xx-xx or xxxx-x-xx or xxxx-xx-x or xxxx-x-x
            year = Integer.parseInt(strDate.substring(0, 4));
            String strMonth = "";
            int i;
            for (i = 5; i < strDate.length(); ++i) {
                if (strDate.charAt(i) != '-') {
                    strMonth += strDate.charAt(i);
                } else {
                    break;
                }
            }
            month = Integer.parseInt(strMonth);
            String strDay = "";
            for (int j = i + 1; j < strDate.length(); ++j) {
                if (strDate.charAt(j) != '-') {
                    strDay += strDate.charAt(j);
                } else {
                    break;
                }
            }
            dayOfMonth = Integer.parseInt(strDay);

            // xx:xx or x:xx or x:x or xx:x
            int idx;
            String strHour = "";
            for (idx = 0; idx < strTime.length(); ++idx) {
                if (strTime.charAt(idx) != ':') {
                    strHour += strTime.charAt(idx);
                } else {
                    break;
                }
            }
            hour = Integer.parseInt(strHour);
            minute = Integer.parseInt(strTime.substring(idx + 1));
            Timestamp ts;

            try {
                if (userId != null) {
                    String taskId = UUID.randomUUID().toString();
                    Task task = null;
                    try {
                        Date d = new SimpleDateFormat("yyyy/MM/dd/hh/mm")
                                .parse(year + "/" + month + "/" + dayOfMonth +
                                        "/" + hour + "/" + minute);
                        ts = new Timestamp(d.getTime());
                        int priority = 0;
                        if (checkBoxA.isChecked()) {
                            priority = 2;
                        } else if (checkBoxB.isChecked()) {
                            priority = 1;
                        } else {
                            priority = 0;
                        }
                        task = new Task(taskId, strTitle, strDescription,
                                ts.getTime() / 1000 - 28800, priority);

                        if (editedTask == null) {
                            mDatabase.child("task").child(userId).child(taskId).setValue(task);

                        } else {
                            task = new Task(editedTask.getTaskId(), strTitle, strDescription,
                                    ts.getTime() / 1000 - 28800, priority);
                            mDatabase.child("task").child(userId).child(editedTask.getTaskId())
                                    .setValue(task);
                            editedTask = null;
                        }

                    } catch (Exception e) {

                    }

                }
            } catch (Exception e) {

            }

            return true;
        } else {
            Toast.makeText(this, "Missing information", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private void getAllTasks(final FirebaseCallback firebaseCallback) {
        mDatabase.child("task").child(userId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        taskList.clear();
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            Task t = ds.getValue(Task.class);
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

}