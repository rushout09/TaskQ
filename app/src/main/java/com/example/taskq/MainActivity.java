package com.example.taskq;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private RecyclerView recyclerView;
    private CardView cardView;
    protected TextView MainHintTV;
    private MyAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<DataModel> mDataset;
    private ArrayList<DataModel> mInvisible;
    private Button SubmitButton;
    private EditText Title;
    private EditText Remark;
    private RadioGroup Type;
    private FloatingActionButton AddTask;
    private FirebaseUser user;
    private RadioGroup Repeat_RG;
    private LinearLayout DatetimeLL;
    private EditText DateET;
    private EditText TimeET;
    private Calendar calendar;
    private int mPos;
    SharedPreferences shref;
    InputMethodManager imm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(FirebaseAuth.getInstance().getCurrentUser()==null){
            Intent intent = new Intent(MainActivity.this,SignInActivity.class);
            startActivity(intent);
            finish();
        }


        recyclerView = findViewById(R.id.recycler_view);
        cardView = findViewById(R.id.cardview_main);
        SubmitButton = findViewById(R.id.task_submit);
        Title = findViewById(R.id.title_submit);
        Remark = findViewById(R.id.remark_submit);
        Type = findViewById(R.id.typeRG_submit);
        Repeat_RG = findViewById(R.id.repeat_RG);
        DateET = findViewById(R.id.date_ET);
        TimeET = findViewById(R.id.time_ET);
        DatetimeLL = findViewById(R.id.datetime_LL);
        AddTask = findViewById(R.id.add_task);
        MainHintTV = findViewById(R.id.tv_main);
        user = FirebaseAuth.getInstance().getCurrentUser();

        Gson gson = new Gson();
        shref = getApplicationContext().getSharedPreferences("tasks", Context.MODE_PRIVATE);
        String response = shref.getString(user.getUid(), "");
        mDataset = gson.fromJson(response, new TypeToken<ArrayList<DataModel>>() {
        }.getType());

        shref = getApplicationContext().getSharedPreferences("invisible", Context.MODE_PRIVATE);
        response = shref.getString(user.getUid(), "");
        mInvisible = gson.fromJson(response, new TypeToken<ArrayList<DataModel>>() {
        }.getType());
        if (mInvisible != null && !mInvisible.isEmpty()) {
            long currtime = System.currentTimeMillis();
            for (DataModel d : mInvisible) {
                if (Long.parseLong(d.getTargetTimestamp()) - currtime < 1000 * 60 * 60 * 12 && d.getRepeat().compareTo("Daily") == 0) {
                    mDataset.add(d);
                    mInvisible.remove(d);
                }
                if (Long.parseLong(d.getTargetTimestamp()) - currtime < 1000 * 60 * 60 * 24 * 3 && d.getRepeat().compareTo("Weekly") == 0) {
                    mDataset.add(d);
                    mInvisible.remove(d);
                }
            }
        } else {
            mInvisible = new ArrayList<DataModel>();
        }
        if (mDataset == null || mDataset.isEmpty()) {
            mDataset = new ArrayList<DataModel>();
            MainHintTV.setVisibility(View.VISIBLE);
        } else {
            MainHintTV.setVisibility(View.GONE);
        }

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new MyAdapter(mDataset);
        recyclerView.setAdapter(mAdapter);
        mAdapter.sortData();
        ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAdapterPosition();
                final DataModel item;
                if (direction == ItemTouchHelper.LEFT) {
                    item = mAdapter.deleteItem(position);
                    if (item.getRepeat().compareToIgnoreCase("Once") != 0) {
                        Calendar calendar = Calendar.getInstance();
                        if (item.getRepeat().compareToIgnoreCase("Daily") == 0) {
                            calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR));
                            calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH));
                            calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 1);
                            calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) + 1);
                            calendar.set(Calendar.MINUTE, 0);
                            calendar.set(Calendar.SECOND, 0);
                            calendar.set(Calendar.MILLISECOND, 0);
                            item.setTargetTimestamp(String.valueOf(calendar.getTimeInMillis()));
                            mInvisible.add(item);
                        } else if (item.getRepeat().compareToIgnoreCase("Weekly") == 0) {
                            calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR));
                            calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH));
                            calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 7);
                            calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) + 1);
                            calendar.set(Calendar.MINUTE, 0);
                            calendar.set(Calendar.SECOND, 0);
                            calendar.set(Calendar.MILLISECOND, 0);
                            item.setTargetTimestamp(String.valueOf(calendar.getTimeInMillis()));
                            mInvisible.add(item);
                        }
                    }
                    Snackbar snackbar = Snackbar.make(viewHolder.itemView, "Task Done.", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                    if (mDataset == null || mDataset.isEmpty()) {
                        MainHintTV.setVisibility(View.VISIBLE);
                    } else {
                        MainHintTV.setVisibility(View.GONE);
                    }
                } else {
                    item = mAdapter.deleteItem(position);
                    Snackbar snackbar = Snackbar.make(viewHolder.itemView, "Task Removed.", Snackbar.LENGTH_LONG);
                    snackbar.setAction("Undo", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                mAdapter.addItem(item);
                            } catch (Exception e) {
                                Log.e("MainActivity", e.getMessage());
                            }
                        }
                    });
                    snackbar.show();
                    if (mDataset == null || mDataset.isEmpty()) {
                        MainHintTV.setVisibility(View.VISIBLE);
                    } else {
                        MainHintTV.setVisibility(View.GONE);
                    }
                }
            }
            // You must use @RecyclerViewSwipeDecorator inside the onChildDraw method

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                new RecyclerViewSwipeDecorator.Builder(MainActivity.this, c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addSwipeLeftBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.recycler_view_item_swipe_left_background))
                        .addSwipeLeftActionIcon(R.drawable.ic_archive_white_24dp)
                        .addSwipeRightBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.recycler_view_item_swipe_right_background))
                        .addSwipeRightActionIcon(R.drawable.ic_delete_sweep_white_24dp)
                        .addSwipeRightLabel("Delete")
                        .setSwipeRightLabelColor(Color.WHITE)
                        .addSwipeLeftLabel("Done!")
                        .setSwipeLeftLabelColor(Color.WHITE)
                        .create()
                        .decorate();
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(recyclerView);


        mPos = -1;


        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, day);
                calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) + 1);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                updateDate();
            }
        };

        DateET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog dpd = new DatePickerDialog(MainActivity.this, date,
                        calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                dpd.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                dpd.show();

            }
        });

        final TimePickerDialog.OnTimeSetListener time = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                calendar.set(Calendar.HOUR_OF_DAY, i);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);

                updateTime();
            }

        };

        TimeET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog tpd = new TimePickerDialog(MainActivity.this, time, calendar.get(Calendar.HOUR_OF_DAY) + 1, 0, false);
                tpd.show();
            }
        });

        Repeat_RG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.once_RB) {
                    DatetimeLL.setVisibility(View.VISIBLE);
                } else if (i == R.id.daily_RB) {
                    DatetimeLL.setVisibility(View.GONE);
                } else if (i == R.id.weekly_RB) {
                    DatetimeLL.setVisibility(View.GONE);
                }
            }
        });

        mAdapter.setOnItemClickListener(new MyAdapter.ClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                mPos = position;
                cardVisible(mPos);
            }

            @Override
            public void onItemLongClick(int position, View v) {
            }
        });

        AddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cardVisible(mPos);
            }
        });
        SubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitTask(mPos);
                MainHintTV.setVisibility(View.GONE);

            }
        });
    }

    protected void submitTask(int position) {
        String titleStr = Title.getText().toString();
        if(titleStr.isEmpty()){
            Toast.makeText(this,"Empty Title!",Toast.LENGTH_SHORT).show();
        }
        else{
            String remarkStr = Remark.getText().toString();
            String TypeInt = String.valueOf(Type.getCheckedRadioButtonId());
            String RepeatInt = String.valueOf(Repeat_RG.getCheckedRadioButtonId());
            RadioButton rb = findViewById(Type.getCheckedRadioButtonId());
            String TypeStr = rb.getText().toString();
            rb = findViewById(Repeat_RG.getCheckedRadioButtonId());
            String RepeatStr = rb.getText().toString();
            String dateStr = DateET.getText().toString();
            if (RepeatStr.compareToIgnoreCase("Once") == 0 && (dateStr.compareToIgnoreCase("Date") == 0)) {
                Toast.makeText(this, "Give proper Date!", Toast.LENGTH_SHORT).show();
            } else {
                String timestampStr = "0";
                if (RepeatStr.compareToIgnoreCase("Once") == 0) {

                } else if (RepeatStr.compareToIgnoreCase("Daily") == 0) {
                    calendar = Calendar.getInstance();
                    calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR));
                    calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH));
                    calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH));
                    calendar.set(Calendar.HOUR, calendar.get(Calendar.HOUR_OF_DAY) + 1);
                    calendar.set(Calendar.MINUTE, 0);
                    calendar.set(Calendar.SECOND, 0);
                    calendar.set(Calendar.MILLISECOND, 0);
                } else if (RepeatStr.compareToIgnoreCase("Weekly") == 0) {
                    calendar = Calendar.getInstance();
                    calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR));
                    calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH));
                    calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 6);
                    calendar.set(Calendar.HOUR, calendar.get(Calendar.HOUR_OF_DAY) + 1);
                    calendar.set(Calendar.MINUTE, 0);
                    calendar.set(Calendar.SECOND, 0);
                    calendar.set(Calendar.MILLISECOND, 0);
                }
                timestampStr = String.valueOf(calendar.getTimeInMillis());

                String initialTime = String.valueOf(Calendar.getInstance().getTimeInMillis());
                DataModel e = new DataModel(titleStr, remarkStr, TypeStr, RepeatStr, TypeInt, RepeatInt, timestampStr);


                if (position != -1) {
                    mAdapter.deleteItem(position);
                    mPos = -1;
                }


                mDataset.add(e);
                mAdapter.sortData();
                mAdapter.notifyDataSetChanged();
                buttonVisible();
                if (mDataset == null || mDataset.isEmpty()) {
                    MainHintTV.setVisibility(View.VISIBLE);
                } else {
                    MainHintTV.setVisibility(View.GONE);
                }
                Toast.makeText(this, "Task Added!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    protected void cardVisible(int position) {
        AddTask.hide();
        calendar = Calendar.getInstance();
        if (position != -1) {
            calendar.setTimeInMillis(Long.parseLong(mDataset.get(position).getTargetTimestamp()));
            Title.setText(mDataset.get(position).getTitle());
            Remark.setText(mDataset.get(position).getRemark());
            Type.check(Integer.parseInt(mDataset.get(position).getTaskid()));
            Repeat_RG.check(Integer.parseInt(mDataset.get(position).getRepeatid()));
            if (mDataset.get(position).getRepeatid() != null)
                Repeat_RG.check(Integer.parseInt(mDataset.get(position).getRepeatid()));
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/YY", Locale.getDefault());
            String datestr = dateFormat.format(new Date(Long.parseLong(mDataset.get(position).getTargetTimestamp())));
            SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
            String timestr = timeFormat.format(new Date(Long.parseLong(mDataset.get(position).getTargetTimestamp())));
            DateET.setText(datestr);
            TimeET.setText(timestr);


        } else {
            Title.setText("");
            Remark.setText("");
            Type.check(R.id.prod_submit);
            Repeat_RG.check(R.id.once_RB);
            DateET.setText("Date");
            TimeET.setText("Time (Optional)");
            Title.requestFocus();
            imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(Title, 0);
        }
        cardView.setVisibility(View.VISIBLE);
    }
    protected void buttonVisible(){
        cardView.setVisibility(View.GONE);
        imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        try {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);
        }
        catch (Exception e){
            Log.e("MainActivity",e.getMessage());
        }
        AddTask.show();
    }
    @Override
    public void onBackPressed(){
        if (cardView.getVisibility() == View.VISIBLE) {
            buttonVisible();
            mPos = -1;
        }
        else
            super.onBackPressed();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=new MenuInflater(this);
        inflater.inflate(R.menu.main,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.sign_out:
            AuthUI.getInstance()
                    .signOut(this)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Intent intent = new Intent(MainActivity.this,SignInActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });
        }
        return true;
    }
    @Override
    protected void onPause() {
        super.onPause();
        Gson gson = new Gson();
        shref = getApplicationContext().getSharedPreferences("tasks",Context.MODE_PRIVATE);
        String json = gson.toJson(mDataset);
        SharedPreferences.Editor editor = shref.edit();
        editor.remove(user.getUid()).apply();
        editor.putString(user.getUid(),json);
        editor.commit();
        shref = getApplicationContext().getSharedPreferences("invisible", Context.MODE_PRIVATE);
        json = gson.toJson(mInvisible);
        editor = shref.edit();
        editor.remove(user.getUid()).apply();
        editor.putString(user.getUid(), json);
        editor.commit();
    }

    protected void updateDate() {
        String myFormat = "dd/MM/yy (EEEE)";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
        DateET.setText(sdf.format(calendar.getTime()));
    }
    protected void updateTime() {
        String myFormat = "hh a";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
        TimeET.setText(sdf.format(calendar.getTime()));
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }
}


