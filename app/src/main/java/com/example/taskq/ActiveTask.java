package com.example.taskq;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static android.content.Context.ALARM_SERVICE;


/**
 * A simple {@link Fragment} subclass.
 */
public class ActiveTask extends Fragment implements AdapterView.OnItemSelectedListener {
    protected static CardView cardView;
    protected static FloatingActionButton AddTask;
    protected static int mPos;
    protected TextView MainHintTV;
    SharedPreferences shref;
    InputMethodManager imm;
    private RecyclerView recyclerView;
    SendMessage SM;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<DataModel> mDataset;
    private ArrayList<DataModel> mInvisible;
    private Button SubmitButton;
    private EditText Title;
    private EditText Remark;
    private RadioGroup Type;
    private RadioGroup Repeat_RG;
    private LinearLayout DatetimeLL;
    private EditText DateET;
    private EditText TimeET;
    private Calendar calendar;
    private AlarmManager alarmManager;
    private FirebaseUser user;
    private TasksAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootview = inflater.inflate(R.layout.fragment_active_task, container, false);
        return rootview;
    }

    @Override
    public void onViewCreated(@NonNull final View rootview, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(rootview, savedInstanceState);
        recyclerView = rootview.findViewById(R.id.recycler_view);
        cardView = rootview.findViewById(R.id.cardview_main);
        SubmitButton = rootview.findViewById(R.id.task_submit);
        Title = rootview.findViewById(R.id.title_submit);
        Remark = rootview.findViewById(R.id.remark_submit);
        Type = rootview.findViewById(R.id.typeRG_submit);
        Repeat_RG = rootview.findViewById(R.id.repeat_RG);
        DateET = rootview.findViewById(R.id.date_ET);
        TimeET = rootview.findViewById(R.id.time_ET);
        DatetimeLL = rootview.findViewById(R.id.datetime_LL);
        AddTask = rootview.findViewById(R.id.add_task);
        MainHintTV = rootview.findViewById(R.id.tv_main);
        user = FirebaseAuth.getInstance().getCurrentUser();
        alarmManager = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);

        Gson gson = new Gson();
        shref = getActivity().getSharedPreferences("tasks", Context.MODE_PRIVATE);
        String response = shref.getString(user.getUid(), "");
        mDataset = gson.fromJson(response, new TypeToken<ArrayList<DataModel>>() {
        }.getType());

        shref = getActivity().getSharedPreferences("invisible", Context.MODE_PRIVATE);
        response = shref.getString(user.getUid(), "");
        mInvisible = gson.fromJson(response, new TypeToken<ArrayList<DataModel>>() {
        }.getType());
        ArrayList<DataModel> arrayList = new ArrayList<>();
        if (mInvisible != null && !mInvisible.isEmpty()) {
            long currentTimeMillis = System.currentTimeMillis();
            for (DataModel d : mInvisible) {
                if (Long.parseLong(d.getTargetTimestamp()) - currentTimeMillis < 1000 * 60 * 60 * 12 && d.getRepeat().compareTo("Daily") == 0) {
                    mDataset.add(d);
                    arrayList.add(d);
                }
                if (Long.parseLong(d.getTargetTimestamp()) - currentTimeMillis < 1000 * 60 * 60 * 24 * 3 && d.getRepeat().compareTo("Weekly") == 0) {
                    mDataset.add(d);
                    arrayList.add(d);
                }
            }
            for (DataModel d : arrayList) {
                mInvisible.remove(d);
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

        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new TasksAdapter(mDataset);
        recyclerView.setAdapter(mAdapter);
        ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                markAsDone(viewHolder);
                toggleBackgroundHint();
            }

            // You must use @RecyclerViewSwipeDecorator inside the onChildDraw method

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                new RecyclerViewSwipeDecorator.Builder(getContext(), c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addSwipeLeftBackgroundColor(ContextCompat.getColor(getContext(), R.color.recycler_view_item_swipe_left_background))
                        .addSwipeLeftActionIcon(R.drawable.ic_archive_white_24dp)
                        .addSwipeRightBackgroundColor(ContextCompat.getColor(getContext(), R.color.recycler_view_item_swipe_left_background))
                        .addSwipeRightActionIcon(R.drawable.ic_archive_white_24dp)
                        .addSwipeRightLabel("Done!")
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
                calendar.set(Calendar.HOUR_OF_DAY, 22);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                updateDate();
            }
        };

        DateET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog dpd = new DatePickerDialog(getContext(), date,
                        calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                dpd.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                dpd.show();

            }
        });

        final TimePickerDialog.OnTimeSetListener time = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                calendar.set(Calendar.HOUR_OF_DAY, i);
                calendar.set(Calendar.MINUTE, i1);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);

                updateTime();
            }

        };

        TimeET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog tpd = new TimePickerDialog(getContext(), time, calendar.get(Calendar.HOUR_OF_DAY) + 1, 0, false);
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

        mAdapter.setOnItemClickListener(new TasksAdapter.ClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                mPos = position;
                cardVisible(mPos);
            }

            private int pos;
            private RecyclerView.ViewHolder vh;
            @Override
            public void onItemLongClick(int position, View v, RecyclerView.ViewHolder viewHolder) {
                pos = position;
                vh = viewHolder;
                PopupMenu popupMenu = new PopupMenu(getContext(), v);
                popupMenu.inflate(R.menu.options_active_task);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        int itemId = menuItem.getItemId();
                        if (itemId == R.id.deleteTaskOption) {
                            deleteTask(pos);
                            toggleBackgroundHint();
                            return true;
                        } else if (itemId == R.id.markTaskDoneOption) {
                            markAsDone(vh);
                            toggleBackgroundHint();
                        } else if (itemId == R.id.editTaskOption) {
                            mPos = pos;
                            cardVisible(mPos);
                        }
                        return false;
                    }
                });
                popupMenu.show();
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
                submitTask(mPos, rootview);
                mPos = -1;
            }
        });
    }

    protected void submitTask(int position, View rootview) {
        if (position == -1) {
            String titleStr = Title.getText().toString();
            String remarkStr = Remark.getText().toString();
            String TypeInt = String.valueOf(Type.getCheckedRadioButtonId());
            String RepeatInt = String.valueOf(Repeat_RG.getCheckedRadioButtonId());
            RadioButton rb = rootview.findViewById(Type.getCheckedRadioButtonId());
            String TypeStr = rb.getText().toString();
            rb = rootview.findViewById(Repeat_RG.getCheckedRadioButtonId());
            String RepeatStr = rb.getText().toString();
            String dateStr = DateET.getText().toString();
            String timestampStr = "0";
            if (RepeatStr.compareToIgnoreCase("Daily") == 0) {
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
                calendar.set(Calendar.HOUR, calendar.get(Calendar.HOUR_OF_DAY));
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
            }
            if (titleStr.isEmpty()) {
                Toast.makeText(getContext(), "Empty Title!", Toast.LENGTH_SHORT).show();
            } else if (RepeatStr.compareToIgnoreCase("Once") == 0 && (dateStr.compareToIgnoreCase("Date") == 0)) {
                Toast.makeText(getContext(), "Give proper Date!", Toast.LENGTH_SHORT).show();
            } else {
                timestampStr = String.valueOf(calendar.getTimeInMillis());
                DataModel newTask = new DataModel(titleStr, remarkStr, TypeStr, RepeatStr, TypeInt, RepeatInt, timestampStr);
                Intent intent = new Intent(getActivity(), Notification_receiver.class);
                intent.setAction(newTask.getTitle() + newTask.getTargetTimestamp());
                intent.putExtra("title", titleStr);
                intent.putExtra("content", remarkStr);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                alarmManager.set(AlarmManager.RTC_WAKEUP, Long.parseLong(newTask.getTargetTimestamp()) - 1000 * 60 * 45, pendingIntent);
                mAdapter.addItem(newTask);
                mAdapter.sortData();
                mAdapter.notifyDataSetChanged();
                Toast.makeText(getContext(), "Task Added!", Toast.LENGTH_SHORT).show();

                buttonVisible();
                toggleBackgroundHint();
            }
        } else {
            String titleStr = Title.getText().toString();
            DataModel currentTask = mDataset.get(position);
            String remarkStr = Remark.getText().toString();
            String TypeInt = String.valueOf(Type.getCheckedRadioButtonId());
            String repeatInt = String.valueOf(Repeat_RG.getCheckedRadioButtonId());
            RadioButton rb = rootview.findViewById(Type.getCheckedRadioButtonId());
            String TypeStr = rb.getText().toString();
            rb = rootview.findViewById(Repeat_RG.getCheckedRadioButtonId());
            String repeatStr = rb.getText().toString();
            String dateStr = DateET.getText().toString();
            if (titleStr.isEmpty()) {
                Toast.makeText(getContext(), "Empty Title!", Toast.LENGTH_SHORT).show();
            } else if (repeatStr.compareToIgnoreCase("Once") == 0 && (dateStr.compareToIgnoreCase("Date") == 0)) {
                Toast.makeText(getContext(), "Give proper Date!", Toast.LENGTH_SHORT).show();
            } else {
                String timestampStr = String.valueOf(calendar.getTimeInMillis());
                Intent intent = new Intent(getContext(), Notification_receiver.class);
                intent.setAction(currentTask.getTitle() + currentTask.getTargetTimestamp());
                intent.putExtra("title", currentTask.getTitle());
                intent.putExtra("content", currentTask.getRemark());
                PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                alarmManager.cancel(pendingIntent);

                int f = 0;
                if (currentTask.getTargetTimestamp().compareToIgnoreCase(timestampStr) != 0) {
                    f = 1;
                }
                currentTask.setTargetTimestamp(timestampStr);
                currentTask.setType(TypeStr);
                currentTask.setRemark(remarkStr);
                currentTask.setRepeatid(repeatInt);
                currentTask.setTaskid(TypeInt);
                currentTask.setRepeat(repeatStr);
                currentTask.setTitle(titleStr);

                intent = new Intent(getContext(), Notification_receiver.class);
                intent.setAction(currentTask.getTitle() + currentTask.getTargetTimestamp());
                intent.putExtra("title", titleStr);
                intent.putExtra("content", remarkStr);
                pendingIntent = PendingIntent.getBroadcast(getContext(), 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                alarmManager.set(AlarmManager.RTC_WAKEUP, Long.parseLong(currentTask.getTargetTimestamp()) - 1000 * 60 * 45, pendingIntent);

                if (f == 1) {
                    mAdapter.sortData();
                }
                mAdapter.notifyDataSetChanged();
                Toast.makeText(getContext(), "Task Updated!", Toast.LENGTH_SHORT).show();

                buttonVisible();
                toggleBackgroundHint();
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
            imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(Title, 0);
        }
        cardView.setVisibility(View.VISIBLE);
    }

    public void buttonVisible() {
        cardView.setVisibility(View.GONE);
        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        try {
            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            Log.e("MainActivity", e.getMessage());
        }
        AddTask.show();
    }


    @Override
    public void onPause() {
        super.onPause();
        Gson gson = new Gson();
        shref = getActivity().getSharedPreferences("tasks", Context.MODE_PRIVATE);
        String json = gson.toJson(mDataset);
        SharedPreferences.Editor editor = shref.edit();
        editor.remove(user.getUid()).apply();
        editor.putString(user.getUid(), json);
        editor.commit();
        shref = getActivity().getSharedPreferences("invisible", Context.MODE_PRIVATE);
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
        String myFormat = "hh:mm a";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
        TimeET.setText(sdf.format(calendar.getTime()));
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            SM = (SendMessage) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException("Error in retrieving data.Please try again.");
        }
    }

    interface SendMessage {
        void sendData(DataModel item);
    }

    public void toggleBackgroundHint() {
        if (mDataset == null || mDataset.isEmpty()) {
            MainHintTV.setVisibility(View.VISIBLE);
        } else {
            MainHintTV.setVisibility(View.GONE);
        }
    }

    public void markAsDone(RecyclerView.ViewHolder viewHolder) {
        int position = viewHolder.getAdapterPosition();
        DataModel item = mAdapter.deleteItem(position);
        mAdapter.notifyItemRemoved(position);

        DataModel delItem = new DataModel();
        delItem.setTitle(item.getTitle());
        delItem.setRepeat(item.getRepeat());
        delItem.setTaskid(item.getTaskid());
        delItem.setRepeatid(item.getRepeatid());
        delItem.setRemark(item.getRemark());
        delItem.setType(item.getType());
        delItem.setTargetTimestamp(String.valueOf(System.currentTimeMillis()));
        SM.sendData(delItem);

        if (item.getRepeat().compareToIgnoreCase("Once") != 0) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR));
            calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH));
            if (item.getRepeat().compareToIgnoreCase("Daily") == 0)
                calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 1);
            else
                calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 6);
            calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY));
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            item.setTargetTimestamp(String.valueOf(calendar.getTimeInMillis()));
            mInvisible.add(item);

            Intent intent = new Intent(getActivity(), Notification_receiver.class);
            intent.setAction(item.getTitle() + item.getTargetTimestamp());
            intent.putExtra("title", item.getTitle());
            intent.putExtra("content", item.getRemark());
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.set(AlarmManager.RTC_WAKEUP, Long.parseLong(item.getTargetTimestamp()) - 1000 * 60 * 45, pendingIntent);
        } else {
            Intent intent = new Intent(getActivity(), Notification_receiver.class);
            intent.setAction(item.getTitle() + item.getTargetTimestamp());
            intent.putExtra("title", item.getTitle());
            intent.putExtra("content", item.getRemark());
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.cancel(pendingIntent);
        }
        Toast.makeText(getContext(), "Task done!", Toast.LENGTH_SHORT).show();
    }

    public void deleteTask(int pos) {
        DataModel item = mAdapter.deleteItem(pos);
        mAdapter.notifyItemRemoved(pos);
        Intent intent = new Intent(getActivity(), Notification_receiver.class);
        intent.setAction(item.getTitle() + item.getTargetTimestamp());
        intent.putExtra("title", item.getTitle());
        intent.putExtra("content", item.getRemark());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pendingIntent);

        Toast.makeText(getContext(), "Task Permanently Deleted!", Toast.LENGTH_SHORT).show();
    }
}
