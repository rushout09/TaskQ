package com.example.taskq;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

import static com.example.taskq.ActiveTask.cardView;

public class MainActivity extends AppCompatActivity implements ActiveTask.SendMessageToLog, LoggedTask.SendMessageToActive {

    InputMethodManager imm;
    TabLayout tabLayout;
    ViewPager viewPager;
    TabsAdapter tabsAdapter;
    private static final int PRODUCTIVE = 0;
    private static final int CHORES = 1;
    private static final int RECREATION = 2;
    private ArrayList<Integer> stats;
    private SharedPreferences shref;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createNotificationChannel();
        if(FirebaseAuth.getInstance().getCurrentUser()==null){
            Intent intent = new Intent(MainActivity.this,SignInActivity.class);
            startActivity(intent);
            finish();
        }

        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        user = FirebaseAuth.getInstance().getCurrentUser();

        Gson gson = new Gson();
        shref = getSharedPreferences("stats", Context.MODE_PRIVATE);
        String response = shref.getString(user.getUid(), "");
        stats = gson.fromJson(response, new TypeToken<ArrayList<Integer>>() {
        }.getType());

        if (stats == null || stats.isEmpty()) {
            stats = new ArrayList<>();
            stats.add(0);
            stats.add(0);
            stats.add(0);
        }

        viewPager = findViewById(R.id.view_pager);
        tabsAdapter = new TabsAdapter(getSupportFragmentManager());
        viewPager.setAdapter(tabsAdapter);
        tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setupWithViewPager(viewPager);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=new MenuInflater(this);
        inflater.inflate(R.menu.main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.sign_out) {
                AuthUI.getInstance()
                        .signOut(this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Intent intent = new Intent(MainActivity.this, SignInActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });
        } else if (itemId == R.id.pie_chart) {
            Intent intent = new Intent(MainActivity.this, PieActivity.class);
            intent.putExtra("productive", stats.get(PRODUCTIVE));
            intent.putExtra("chores", stats.get(CHORES));
            intent.putExtra("recreation", stats.get(RECREATION));
            startActivity(intent);
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (cardView.getVisibility() == View.VISIBLE) {
            buttonVisible();
            ActiveTask.mPos = -1;
        } else {
            super.onBackPressed();
        }
    }

    public void buttonVisible() {
        cardView.setVisibility(View.GONE);
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        try {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            Log.e("MainActivity", e.getMessage());
        }
        ActiveTask.AddTask.show();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence sequence = "general";
            String description = "All notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("123", sequence, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Gson gson = new Gson();
        shref = getSharedPreferences("stats", Context.MODE_PRIVATE);
        String json = gson.toJson(stats);
        SharedPreferences.Editor editor = shref.edit();
        editor.remove(user.getUid()).apply();
        editor.putString(user.getUid(), json);
        editor.commit();
    }

    @Override
    public void sendDataToLog(DataModel item) {
        String tag = "android:switcher:" + R.id.view_pager + ":" + 1;
        LoggedTask loggedTask = (LoggedTask) getSupportFragmentManager().findFragmentByTag(tag);
        if (item.getType().compareToIgnoreCase("Productive") == 0) {
            stats.set(PRODUCTIVE, stats.get(PRODUCTIVE) + 1);
        } else if (item.getType().compareToIgnoreCase("Chores") == 0) {
            stats.set(CHORES, stats.get(CHORES) + 1);
        } else if (item.getType().compareToIgnoreCase("Recreation") == 0) {
            stats.set(RECREATION, stats.get(RECREATION) + 1);
        }
        if (loggedTask != null)
            loggedTask.addDoneList(item);
    }

    @Override
    public void sendDataToActive(DataModel item, Boolean isUndo) {
        String tag = "android:switcher:" + R.id.view_pager + ":" + 0;
        ActiveTask activeTask = (ActiveTask) getSupportFragmentManager().findFragmentByTag(tag);
        if (isUndo) {
            if (item.getType().compareToIgnoreCase("Productive") == 0) {

                stats.set(PRODUCTIVE, stats.get(PRODUCTIVE) - 1);
            } else if (item.getType().compareToIgnoreCase("Chores") == 0) {
                stats.set(CHORES, stats.get(CHORES) - 1);
            } else if (item.getType().compareToIgnoreCase("Recreation") == 0) {
                stats.set(RECREATION, stats.get(RECREATION) - 1);
            }
        }
        if (activeTask != null)
            activeTask.addTaskList(item);
    }
}