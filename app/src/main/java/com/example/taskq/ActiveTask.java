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
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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

import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


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
    private RecyclerView recyclerView2;
    private SendMessageToLog SM;
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
    private FirebaseUser user;
    private TasksAdapter mAdapter;
    private OneTimeWorkRequest notificationRequest;
    private SnoozedAdapter snoozedAdapter;
    private RecyclerView.LayoutManager layoutManager2;
    private TextView snoozedTv;

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
        recyclerView2 = rootview.findViewById(R.id.snoozedList);
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
        snoozedTv = rootview.findViewById(R.id.snoozedTv);

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
        }
        toggleBackgroundHint();

        layoutManager = new LinearLayoutManager(getActivity());
        layoutManager2 = new LinearLayoutManager(getActivity());
        recyclerView2.setLayoutManager(layoutManager2);
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new TasksAdapter(mDataset);
        snoozedAdapter = new SnoozedAdapter(mInvisible);
        recyclerView2.setAdapter(snoozedAdapter);
        recyclerView.setAdapter(mAdapter);

        recyclerView2.setNestedScrollingEnabled(false);
        recyclerView.setNestedScrollingEnabled(false);


        ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {

            DataModel item, item2;

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
                item = mDataset.get(viewHolder.getAdapterPosition());
                item2 = new DataModel(item);
                markAsDone(viewHolder);
                Snackbar snackbar = Snackbar.make(recyclerView, "Task Done and added to snooze!", Snackbar.LENGTH_SHORT);
                snackbar.setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        addTask(item2);
                        if (item.getRepeat().compareToIgnoreCase("Once") != 0) {
                            cancelNotificationRequest(item.getUuid());
                            mInvisible.remove(item);
                            snoozedAdapter.notifyItemRemoved(mInvisible.size());
                        }
                        SM.popFromLog(item.getType());
                    }
                });
                snackbar.show();
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
                TimePickerDialog tpd = new TimePickerDialog(getContext(), time, calendar.get(Calendar.HOUR_OF_DAY), 0, false);
                tpd.show();
            }
        });

        Repeat_RG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.once_RB) {
                    DatetimeLL.setVisibility(View.VISIBLE);
                    DateET.setVisibility(View.VISIBLE);
                } else if (i == R.id.daily_RB) {
                    DatetimeLL.setVisibility(View.VISIBLE);
                    DateET.setVisibility(View.GONE);
                } else if (i == R.id.weekly_RB) {
                    DatetimeLL.setVisibility(View.GONE);
                }
            }
        });

        snoozedAdapter.setOnItemClickListener(new SnoozedAdapter.ClickListener() {
            @Override
            public void onItemClick(int position, View v) {

            }

            DataModel item;
            int pos;
            @Override
            public void onItemLongClick(int position, View v, RecyclerView.ViewHolder viewHolder) {
                pos = position;
                PopupMenu popupMenu = new PopupMenu(getContext(), v);
                popupMenu.inflate(R.menu.options_snoozed_task);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        int itemId = menuItem.getItemId();
                        if (itemId == R.id.viewSnoozedGraphOption) {
                            if (mInvisible.get(pos).getRepeat().compareTo("Daily") == 0) {
                                DataModel item = mInvisible.get(pos);
                                Intent intent = new Intent(getContext(), GraphActivity.class);
                                intent.putExtra("currentStreak", item.getCurrentStreak());
                                intent.putExtra("maxStreak", item.getMaxStreak());
                                intent.putExtra("title", item.getTitle());
                                startActivity(intent);
                            } else {
                                Toast.makeText(getContext(), "Streak is only available for daily tasks.", Toast.LENGTH_SHORT).show();
                            }
                            return true;
                        } else if (itemId == R.id.deleteSnoozedTaskOption) {
                            cancelNotificationRequest(mInvisible.get(pos).getUuid());
                            item = mInvisible.get(pos);
                            mInvisible.remove(pos);
                            snoozedAdapter.notifyItemRemoved(pos);
                            toggleBackgroundHint();
                            Snackbar snackbar = Snackbar.make(rootview, "Task Deleted Permanently.", Snackbar.LENGTH_SHORT);
                            snackbar.setAction("Undo", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    UUID uuid = setNotificationRequest(item.getTitle(), item.getRemark(), item.getTargetTimestamp());
                                    item.setUuid(uuid);
                                    mInvisible.add(pos, item);
                                    snoozedAdapter.notifyItemInserted(pos);
                                    toggleBackgroundHint();
                                }
                            });
                            snackbar.show();
                            return true;
                        }
                        return true;
                    }
                });
                popupMenu.show();
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
            private DataModel item, item2;
            @Override
            public void onItemLongClick(final int position, View v, RecyclerView.ViewHolder viewHolder) {
                pos = position;
                vh = viewHolder;
                PopupMenu popupMenu = new PopupMenu(getContext(), v);
                popupMenu.inflate(R.menu.options_active_task);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        buttonVisible();
                        int itemId = menuItem.getItemId();
                        if (itemId == R.id.deleteTaskOption) {
                            item = mDataset.get(pos);
                            deleteTask(pos);
                            Snackbar snackbar = Snackbar.make(rootview, "Task Deleted Permanently.", Snackbar.LENGTH_SHORT);
                            snackbar.setAction("Undo", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    addTask(item);
                                }
                            });
                            snackbar.show();
                            mPos = -1;
                            return true;
                        } else if (itemId == R.id.markTaskDoneOption) {
                            item = mDataset.get(vh.getAdapterPosition());
                            item2 = new DataModel(item);
                            markAsDone(vh);
                            Snackbar snackbar = Snackbar.make(recyclerView, "Task Done and added to snooze!", Snackbar.LENGTH_SHORT);
                            snackbar.setAction("UNDO", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    addTask(item2);
                                    if (item.getRepeat().compareToIgnoreCase("Once") != 0) {
                                        cancelNotificationRequest(item.getUuid());
                                        mInvisible.remove(item);
                                        snoozedAdapter.notifyItemRemoved(mInvisible.size());
                                    }
                                    SM.popFromLog(item.getType());
                                }
                            });
                            snackbar.show();
                            toggleBackgroundHint();
                            return true;
                        } else if (itemId == R.id.editTaskOption) {
                            mPos = pos;
                            cardVisible(mPos);
                            return true;
                        } else if (itemId == R.id.viewGraphOption) {
                            if (mDataset.get(pos).getRepeat().compareTo("Daily") == 0) {
                                DataModel item = mDataset.get(pos);
                                Intent intent = new Intent(getContext(), GraphActivity.class);
                                intent.putExtra("currentStreak", item.getCurrentStreak());
                                intent.putExtra("maxStreak", item.getMaxStreak());
                                intent.putExtra("title", item.getTitle());
                                startActivity(intent);
                            } else {
                                Toast.makeText(getContext(), "Streak is only available for daily tasks.", Toast.LENGTH_SHORT).show();
                            }
                            return true;
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
        String titleStr = Title.getText().toString();
        String remarkStr = Remark.getText().toString();
        String TypeInt = String.valueOf(Type.getCheckedRadioButtonId());
        String repeatInt = String.valueOf(Repeat_RG.getCheckedRadioButtonId());
        RadioButton rb = rootview.findViewById(Type.getCheckedRadioButtonId());
        String TypeStr = rb.getText().toString();
        rb = rootview.findViewById(Repeat_RG.getCheckedRadioButtonId());
        String repeatStr = rb.getText().toString();
        String dateStr = DateET.getText().toString();
        if (position == -1) {
            String timestampStr = "0";
            if (repeatStr.compareToIgnoreCase("Daily") == 0) {
                calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR));
                calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH));
                calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH));
                calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY));
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
            } else if (repeatStr.compareToIgnoreCase("Weekly") == 0) {
                calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR));
                calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH));
                calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH));
                calendar.set(Calendar.HOUR_OF_DAY, 6);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
            }
            if (titleStr.isEmpty()) {
                Toast.makeText(getContext(), "Empty Title!", Toast.LENGTH_SHORT).show();
            } else if ((repeatStr.compareToIgnoreCase("Once") == 0) && (dateStr.compareToIgnoreCase("Date") == 0)) {
                Toast.makeText(getContext(), "Give proper Date!", Toast.LENGTH_SHORT).show();
            } else {
                timestampStr = String.valueOf(calendar.getTimeInMillis());
                DataModel newTask = new DataModel(titleStr, remarkStr, TypeStr, repeatStr, TypeInt, repeatInt, timestampStr);
                addTask(newTask);
                Toast.makeText(getContext(), "Task Added!", Toast.LENGTH_SHORT).show();
                buttonVisible();
            }
        } else {
            DataModel currentTask = mDataset.get(position);
            if (titleStr.isEmpty()) {
                Toast.makeText(getContext(), "Empty Title!", Toast.LENGTH_SHORT).show();
            } else if (repeatStr.compareToIgnoreCase("Once") == 0 && (dateStr.compareToIgnoreCase("Date") == 0)) {
                Toast.makeText(getContext(), "Give proper Date!", Toast.LENGTH_SHORT).show();
            } else {
                String timestampStr = String.valueOf(calendar.getTimeInMillis());
                currentTask.setTargetTimestamp(timestampStr);
                currentTask.setType(TypeStr);
                currentTask.setRemark(remarkStr);
                currentTask.setRepeatId(repeatInt);
                currentTask.setTypeId(TypeInt);
                currentTask.setRepeat(repeatStr);
                currentTask.setTitle(titleStr);
                deleteTask(position);
                addTask(currentTask);
                Toast.makeText(getContext(), "Task Updated!", Toast.LENGTH_SHORT).show();
                buttonVisible();
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
            Type.check(Integer.parseInt(mDataset.get(position).getTaskId()));
            Repeat_RG.check(Integer.parseInt(mDataset.get(position).getRepeatId()));
            if (mDataset.get(position).getRepeatId() != null)
                Repeat_RG.check(Integer.parseInt(mDataset.get(position).getRepeatId()));
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
            imm.hideSoftInputFromWindow(getActivity().getWindow().getDecorView().getWindowToken(), 0);
        } catch (Exception e) {
            Log.e("MainActivity", e.getMessage());
        }
        AddTask.show();
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

    public void markAsDone(RecyclerView.ViewHolder viewHolder) {
        int position = viewHolder.getAdapterPosition();
        DataModel item = mDataset.get(position);
        long currentTime = System.currentTimeMillis();

        if (item.getDoneTimestamp().compareTo("0") == 0 || (currentTime - Long.parseLong(item.getDoneTimestamp()) <= 1000 * 60 * 60 * 24)) {
            int currentStreak = item.getCurrentStreak();
            currentStreak++;
            item.setCurrentStreak(currentStreak);
        } else {
            int maxStreak = item.getMaxStreakValue();
            int currentStreak = item.getCurrentStreak();
            if (maxStreak < currentStreak) {
                maxStreak = currentStreak;
            }
            currentStreak = 1;
            item.setCurrentStreak(currentStreak);
            item.insertMaxStreak(maxStreak);
        }

        DataModel delItem = new DataModel(item.getTitle(), item.getRemark(), item.getType(), item.getRepeat(), item.getTaskId(), item.getRepeatId(), item.getTargetTimestamp());
        delItem.setDoneTimestamp(String.valueOf(currentTime));
        item.setDoneTimestamp(String.valueOf(currentTime));
        SM.sendDataToLog(delItem);

        deleteTask(position);

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
            UUID id = setNotificationRequest(item.getTitle(), item.getRemark(), item.getTargetTimestamp());
            item.setUuid(id);
            mInvisible.add(item);
            snoozedAdapter.notifyItemInserted(mInvisible.size() - 1);
        }
        toggleBackgroundHint();
    }

    public void toggleBackgroundHint() {
        if (mDataset == null || mDataset.isEmpty() && (mInvisible == null || mInvisible.isEmpty())) {
            MainHintTV.setVisibility(View.VISIBLE);
        } else {
            MainHintTV.setVisibility(View.GONE);
        }
        if (mInvisible == null || mInvisible.isEmpty()) {
            snoozedTv.setVisibility(View.GONE);
        } else {
            snoozedTv.setVisibility(View.VISIBLE);
        }
    }

    public void addTask(DataModel task) {
        UUID id = setNotificationRequest(task.getTitle(), task.getRemark(), task.getTargetTimestamp());
        task.setUuid(id);
        mAdapter.addItem(task);
        toggleBackgroundHint();
    }

    public void deleteTask(int pos) {
        cancelNotificationRequest(mDataset.get(pos).getUuid());
        mAdapter.deleteItem(pos);
        toggleBackgroundHint();
    }

    private UUID setNotificationRequest(String Title, String Content, String Target) {
        SimpleDateFormat format = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        String time = format.format(new Date(Long.parseLong(Target)));
        Data data = new Data.Builder()
                .putString("title", Title + " By " + time)
                .putString("content", Content)
                .build();
        long delay = Long.parseLong(Target) - System.currentTimeMillis() - 1000 * 60 * 45;
        notificationRequest = new OneTimeWorkRequest.Builder(NotificationWorker.class)
                .setInputData(data)
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .build();
        WorkManager.getInstance().enqueue(notificationRequest);
        return notificationRequest.getId();
    }

    private void cancelNotificationRequest(UUID noificationId) {
        WorkManager.getInstance().cancelWorkById(noificationId);
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
            SM = (SendMessageToLog) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException("Error in retrieving data.Please try again.");
        }
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

    interface SendMessageToLog {
        void sendDataToLog(DataModel item);

        void popFromLog(String type);
    }

}
