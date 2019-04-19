package com.example.taskq;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
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

import static com.example.taskq.ActiveTask.cardView;

public class MainActivity extends AppCompatActivity implements ActiveTask.SendMessage {

    InputMethodManager imm;
    TabLayout tabLayout;
    ViewPager viewPager;
    TabsAdapter tabsAdapter;

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
        switch (item.getItemId()){
            case R.id.sign_out:
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
    public void sendData(DataModel item) {
        String tag = "android:switcher:" + R.id.view_pager + ":" + 1;
        LoggedTask loggedTask = (LoggedTask) getSupportFragmentManager().findFragmentByTag(tag);
        loggedTask.addDoneList(item);
    }
}