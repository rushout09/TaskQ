package com.example.taskq;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
    public void onViewCreated(@NonNull View rootview, @Nullable Bundle savedInstanceState) {
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
