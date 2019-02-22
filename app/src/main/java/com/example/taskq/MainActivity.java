package com.example.taskq;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;



public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private CardView cardView;
    protected TextView textView;
    private MyAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<DataModel> myDataset;
    private Button SubmitButton;
    private EditText Title;
    private EditText Remark;
    private RadioGroup Type;
    private SeekBar Urgency;
    private SeekBar Importance;
    private SeekBar Easiness;
    private FloatingActionButton AddTask;
    private FirebaseUser user;
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
            recyclerView = (RecyclerView)findViewById(R.id.recycler_view);
            cardView = (CardView)findViewById(R.id.cardview_main);
            SubmitButton = (Button)findViewById(R.id.task_submit);
            Title = (EditText)findViewById(R.id.title_submit);
            Remark = (EditText)findViewById(R.id.remark_submit);
            Type = (RadioGroup)findViewById(R.id.typeRG_submit);
            Urgency = (SeekBar)findViewById(R.id.urgent_submit);
            Importance = (SeekBar)findViewById(R.id.imp_submit);
            Easiness = (SeekBar)findViewById(R.id.easy_submit);
            AddTask = (FloatingActionButton) findViewById(R.id.add_task);
            textView = (TextView)findViewById(R.id.tv_main);
            user = FirebaseAuth.getInstance().getCurrentUser();
            //recyclerView.setHasFixedSize(true)

            shref = getApplicationContext().getSharedPreferences("tasks",Context.MODE_PRIVATE);
            Gson gson = new Gson();
            String response=shref.getString(user.getUid(),"");
            myDataset = gson.fromJson(response, new TypeToken<ArrayList<DataModel> >(){}.getType());
            if(myDataset==null){
                myDataset = new ArrayList<>();
                textView.setVisibility(View.VISIBLE);
            }
            else{
                textView.setVisibility(View.GONE);
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




            AddTask.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cardVisible();
                }
            });
            SubmitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    submitTask();
                    buttonVisible();
                    textView.setVisibility(View.GONE);

                }
            });

        }

    }
    protected void submitTask(){
        String titleStr = Title.getText().toString();
        if(titleStr.isEmpty()){
            Toast.makeText(this,"Empty Title!",Toast.LENGTH_SHORT).show();
        }
        else{
            String remarkStr = Remark.getText().toString();
            int id = Type.getCheckedRadioButtonId();
            RadioButton rb = (RadioButton)findViewById(id);
            String typeStr = rb.getText().toString();
            final String urgStr = String.valueOf(Urgency.getProgress());
            String impStr = String.valueOf(Importance.getProgress());
            String easeStr = String.valueOf(Easiness.getProgress());
            DataModel e = new DataModel(titleStr,remarkStr,typeStr,urgStr,impStr,easeStr);
            myDataset.add(e);
            Collections.sort(myDataset, new Comparator<DataModel>() {
                @Override
                public int compare(DataModel left, DataModel right) {
                    int leftUrg = Integer.parseInt(left.getUrgency());
                    int leftImp = Integer.parseInt(left.getImportance());
                    int leftEase = Integer.parseInt(left.getEasiness());
                    int rightUrg = Integer.parseInt(right.getUrgency());
                    int rightImp = Integer.parseInt(right.getImportance());
                    int rightEase = Integer.parseInt(right.getEasiness());
                    if(leftUrg>rightUrg) return -1;
                    else if(leftUrg<rightUrg) return 1;
                    else{
                        if(leftImp>rightImp) return -1;
                        else if(leftImp<rightImp) return 1;
                        else{
                            if(leftEase>rightEase) return -1;
                            else if(leftEase<rightEase) return 1;
                            return 0;
                        }
                    }
                }
            });
            mAdapter.notifyDataSetChanged();
            Title.setText("");
            Remark.setText("");
            Toast.makeText(this,"Task Added!",Toast.LENGTH_SHORT).show();
        }
    }
    protected void cardVisible(){
        AddTask.hide();
        imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        try {
            Title.requestFocus();
            imm.showSoftInput(Title,0);
        }
        catch (Exception e){
            Log.e("MainActivity",e.getMessage());
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
        if(cardView.getVisibility()==View.VISIBLE)
            buttonVisible();
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
}
