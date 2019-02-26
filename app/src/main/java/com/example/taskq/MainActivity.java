package com.example.taskq;

import android.app.DatePickerDialog;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private RecyclerView recyclerView;
    private CardView cardView;
    protected TextView MainHintTV;
    private MyAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<DataModel> myDataset;
    private Button SubmitButton;
    private EditText Title;
    private EditText Remark;
    private RadioGroup Type;
    private SeekBar Importance;
    private FloatingActionButton AddTask;
    private FirebaseUser user;
    private RadioGroup Repeat_RG;
    private RadioGroup OnByRG;
    private LinearLayout DatetimeLL;
    private EditText DateET;
    private Spinner TimeSpinner;
    private Spinner WeekSpinner;
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
        else{
            recyclerView = findViewById(R.id.recycler_view);
            cardView = findViewById(R.id.cardview_main);
            SubmitButton = findViewById(R.id.task_submit);
            Title = findViewById(R.id.title_submit);
            Remark = findViewById(R.id.remark_submit);
            Type = findViewById(R.id.typeRG_submit);
            Repeat_RG = findViewById(R.id.repeat_RG);
            OnByRG = findViewById(R.id.onby_RG);
            DateET = findViewById(R.id.date_ET);
            TimeSpinner = findViewById(R.id.time_Spinner);
            WeekSpinner = findViewById(R.id.weekday_Spinner);
            DatetimeLL = findViewById(R.id.datetime_LL);
            Importance = findViewById(R.id.imp_submit);
            AddTask = findViewById(R.id.add_task);
            MainHintTV = findViewById(R.id.tv_main);
            user = FirebaseAuth.getInstance().getCurrentUser();

            mPos = -1;

            final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, day);
                    updateDate(year, month, day);
                }
            };

            DateET.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    calendar = Calendar.getInstance();
                    DatePickerDialog dpd = new DatePickerDialog(MainActivity.this, date,
                            calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                    dpd.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                    dpd.show();

                }
            });

            ArrayAdapter<CharSequence> timeAdapter = ArrayAdapter.createFromResource(this, R.array.time_array, android.R.layout.simple_spinner_item);
            timeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            TimeSpinner.setAdapter(timeAdapter);
            TimeSpinner.setOnItemSelectedListener(this);

            ArrayAdapter<CharSequence> weekAdapter = ArrayAdapter.createFromResource(this, R.array.week_array, android.R.layout.simple_spinner_item);
            weekAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            WeekSpinner.setAdapter(weekAdapter);
            WeekSpinner.setOnItemSelectedListener(this);


            Repeat_RG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int i) {
                    if (i == R.id.once_RB) {
                        OnByRG.setVisibility(View.VISIBLE);
                        DateET.setVisibility(View.VISIBLE);
                        WeekSpinner.setVisibility(View.GONE);
                        TimeSpinner.setVisibility(View.VISIBLE);
                    } else if (i == R.id.daily_RB) {
                        OnByRG.setVisibility(View.GONE);
                        DateET.setVisibility(View.GONE);
                        WeekSpinner.setVisibility(View.GONE);
                        TimeSpinner.setVisibility(View.VISIBLE);
                    } else if (i == R.id.weekly_RB) {
                        DateET.setVisibility(View.GONE);
                        OnByRG.setVisibility(View.VISIBLE);
                        WeekSpinner.setVisibility(View.VISIBLE);
                        TimeSpinner.setVisibility(View.VISIBLE);
                    }
                }
            });
            //recyclerView.setHasFixedSize(true)

            shref = getApplicationContext().getSharedPreferences("tasks",Context.MODE_PRIVATE);
            Gson gson = new Gson();
            String response=shref.getString(user.getUid(),"");
            myDataset = gson.fromJson(response, new TypeToken<ArrayList<DataModel> >(){}.getType());
            if(myDataset==null){
                myDataset = new ArrayList<>();
                MainHintTV.setVisibility(View.VISIBLE);
            }
            else{
                MainHintTV.setVisibility(View.GONE);
            }

            layoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(layoutManager);
            mAdapter = new MyAdapter(myDataset);
            recyclerView.setAdapter(mAdapter);
            ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

                @Override
                public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                    return false;
                }

                @Override
                public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                    final int position = viewHolder.getAdapterPosition();
                    final DataModel item = mAdapter.removeItem(position);
                    Snackbar snackbar = Snackbar.make(viewHolder.itemView, "Task Completed.",Snackbar.LENGTH_LONG );
                    snackbar.setAction("Undo", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                mAdapter.addItem(item, position);
                            }
                            catch (Exception e){
                                Log.e("MainActivity",e.getMessage());
                            }
                        }
                    });
                    snackbar.show();
                }
                // You must use @RecyclerViewSwipeDecorator inside the onChildDraw method

                @Override
                public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive){

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

            mAdapter.setOnItemClickListener(new MyAdapter.ClickListener() {
                @Override
                public void onItemClick(int position, View v) {
                    mPos = position;
                    cardVisible(position);
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
                    buttonVisible();
                    MainHintTV.setVisibility(View.GONE);

                }
            });

        }
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
            String OnbyInt = String.valueOf(OnByRG.getCheckedRadioButtonId());
            RadioButton rb = findViewById(Type.getCheckedRadioButtonId());
            String TypeStr = rb.getText().toString();
            rb = findViewById(Repeat_RG.getCheckedRadioButtonId());
            String RepeatStr = rb.getText().toString();
            rb = findViewById(OnByRG.getCheckedRadioButtonId());
            String OnbyStr = rb.getText().toString();
            String dateStr = DateET.getText().toString();
            String dayStr = String.valueOf(WeekSpinner.getSelectedItemPosition());
            String timeStr = String.valueOf(TimeSpinner.getSelectedItemPosition());

            String impStr = String.valueOf(Importance.getProgress());
            DataModel e = new DataModel(titleStr, remarkStr, TypeStr, RepeatStr, OnbyStr, dateStr, dayStr, timeStr, impStr, TypeInt, RepeatInt, OnbyInt);
            if (position != -1) {
                mAdapter.removeItem(position);
                mPos = -1;
            }
            myDataset.add(e);
            Collections.sort(myDataset, new Comparator<DataModel>() {
                @Override
                public int compare(DataModel left, DataModel right) {
                    int leftImp = Integer.parseInt(left.getImportance());

                    int rightImp = Integer.parseInt(right.getImportance());


                    if (leftImp > rightImp) return -1;
                    else if (leftImp < rightImp) return 1;
                    else {

                        return 0;
                    }
                }
            });

            mAdapter.notifyDataSetChanged();
            Title.setText("");
            Remark.setText("");
            Toast.makeText(this,"Task Added!",Toast.LENGTH_SHORT).show();
        }
    }

    protected void cardVisible(int position) {
        AddTask.hide();
        calendar = Calendar.getInstance();
        if (position != -1) {
            Title.setText(myDataset.get(position).getTitle());
            Remark.setText(myDataset.get(position).getRemark());
            Type.check(Integer.parseInt(myDataset.get(position).getTaskid()));
            if (myDataset.get(position).getRepeatid() != null)
                Repeat_RG.check(Integer.parseInt(myDataset.get(position).getRepeatid()));
            if (myDataset.get(position).getOnbyid() != null)
                OnByRG.check(Integer.parseInt(myDataset.get(position).getOnbyid()));
            DateET.setText(myDataset.get(position).getTargetdate());
            WeekSpinner.setSelection(Integer.parseInt(myDataset.get(position).getTargetday()), true);
            TimeSpinner.setSelection(Integer.parseInt(myDataset.get(position).getTargettime()), true);
            Importance.setProgress(Integer.parseInt(myDataset.get(position).getImportance()));

        } else {
            Title.setText("");
            Remark.setText("");
            Type.check(R.id.prod_submit);
            Importance.setProgress(0);
            Repeat_RG.check(R.id.once_RB);
            OnByRG.check(R.id.on_submit);
            DateET.setText("Date");
            WeekSpinner.setSelection(0, true);
            TimeSpinner.setSelection(0, true);
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
        String json = gson.toJson(myDataset);
        SharedPreferences.Editor editor = shref.edit();
        editor.remove(user.getUid()).apply();
        editor.putString(user.getUid(),json);
        editor.commit();
    }
    protected void updateDate(int year, int month, int day) {
        String myFormat = "dd/MM/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        DateET.setText(sdf.format(calendar.getTime()));
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }
}


