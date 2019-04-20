package com.example.taskq;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoggedTask extends Fragment {
    private static LogsAdapter mAdapter;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<DataModel> LoggedTaskList;
    private SharedPreferences shref;
    private TextView BGTV;
    private FirebaseUser user;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootview = inflater.inflate(R.layout.fragment_logged_task, container, false);
        return rootview;
    }


    @Override
    public void onViewCreated(@NonNull final View rootview, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(rootview, savedInstanceState);
        recyclerView = rootview.findViewById(R.id.recycler_logs);
        user = FirebaseAuth.getInstance().getCurrentUser();
        BGTV = rootview.findViewById(R.id.tv_logs);

        Gson gson = new Gson();
        shref = getActivity().getSharedPreferences("LoggedTasks", Context.MODE_PRIVATE);
        String response = shref.getString(user.getUid(), "");
        LoggedTaskList = gson.fromJson(response, new TypeToken<ArrayList<DataModel>>() {
        }.getType());

        if (LoggedTaskList == null || LoggedTaskList.isEmpty()) {
            LoggedTaskList = new ArrayList<DataModel>();
            BGTV.setVisibility(View.VISIBLE);
        } else {
            BGTV.setVisibility(View.GONE);
        }

        layoutManager = new LinearLayoutManager(getActivity());

        ((LinearLayoutManager) layoutManager).setStackFromEnd(true);
        ((LinearLayoutManager) layoutManager).setReverseLayout(true);

        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new LogsAdapter(LoggedTaskList);
        recyclerView.setAdapter(mAdapter);

        ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            DataModel item;
            int position;

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                position = viewHolder.getAdapterPosition();
                item = LoggedTaskList.get(position);
                LoggedTaskList.remove(position);
                mAdapter.notifyItemRemoved(position);

                Snackbar snackbar = Snackbar.make(rootview, "Task removed from Log", Snackbar.LENGTH_SHORT);
                snackbar.setAction("Undo", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        LoggedTaskList.add(position, item);
                        mAdapter.notifyItemInserted(position);
                        if (LoggedTaskList == null || LoggedTaskList.isEmpty()) {
                            BGTV.setVisibility(View.VISIBLE);
                        } else {
                            BGTV.setVisibility(View.GONE);
                        }
                    }
                });

                snackbar.show();
                if (LoggedTaskList == null || LoggedTaskList.isEmpty()) {
                    BGTV.setVisibility(View.VISIBLE);
                } else {
                    BGTV.setVisibility(View.GONE);
                }

                /* else {
                    item = mAdapter.deleteItem(position);
                    Intent intent = new Intent(getActivity(), Notification_receiver.class);
                    intent.setAction(this.item.getTitle() + this.item.getTargetTimestamp());
                    intent.putExtra("title", this.item.getTitle());
                    intent.putExtra("content", this.item.getRemark());
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    alarmManager.cancel(pendingIntent);
                    Snackbar snackbar = Snackbar.make(viewHolder.itemView, "Task Removed.", Snackbar.LENGTH_LONG);
                    snackbar.setAction("Undo", new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {
                            try {
                                mAdapter.addItem(item, position);
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
                }*/
            }
            // You must use @RecyclerViewSwipeDecorator inside the onChildDraw method

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                new RecyclerViewSwipeDecorator.Builder(getContext(), c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addSwipeRightBackgroundColor(ContextCompat.getColor(getContext(), R.color.recycler_view_item_swipe_left_background))
                        .addSwipeRightActionIcon(R.drawable.ic_archive_white_24dp)
                        .addSwipeLeftBackgroundColor(ContextCompat.getColor(getContext(), R.color.recycler_view_item_swipe_right_background))
                        .addSwipeLeftActionIcon(R.drawable.ic_delete_sweep_white_24dp)
                        .addSwipeRightLabel("Done!")
                        .setSwipeRightLabelColor(Color.WHITE)
                        .addSwipeLeftLabel("Delete!")
                        .setSwipeLeftLabelColor(Color.WHITE)
                        .create()
                        .decorate();
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(recyclerView);


        mAdapter.setOnItemClickListener(new LogsAdapter.ClickListener() {
            @Override
            public void onItemLongClick(int position, View v) {

            }

            @Override
            public void onItemClick(int position, View v) {

            }
        });
    }

    protected void addDoneList(DataModel model) {
        LoggedTaskList.add(model);
        mAdapter.notifyDataSetChanged();
        if (LoggedTaskList == null || LoggedTaskList.isEmpty()) {
            LoggedTaskList = new ArrayList<DataModel>();
            BGTV.setVisibility(View.VISIBLE);
        } else {
            BGTV.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Gson gson = new Gson();
        shref = getActivity().getSharedPreferences("LoggedTasks", Context.MODE_PRIVATE);
        String json = gson.toJson(LoggedTaskList);
        SharedPreferences.Editor editor = shref.edit();
        editor.remove(user.getUid()).apply();
        editor.putString(user.getUid(), json);
        editor.commit();
    }

}
