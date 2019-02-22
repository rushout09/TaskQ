package com.example.taskq;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import static java.security.AccessController.getContext;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private ArrayList<DataModel> dataSet;

    protected static class MyViewHolder extends RecyclerView.ViewHolder{
        protected TextView titleView;
        protected TextView remarkView;
        protected CardView cardView;

        protected MyViewHolder(View view){
            super(view);
            this.titleView = (TextView)view.findViewById(R.id.title_view);
            this.remarkView = (TextView)view.findViewById(R.id.remark_view);
            this.cardView = (CardView)view.findViewById(R.id.cardview);
        }
    }
    public MyAdapter(ArrayList<DataModel> dataSet){
        this.dataSet = dataSet;
    }

    @NonNull
    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item,parent,false);
        MyViewHolder vh = new MyViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdapter.MyViewHolder holder, int position) {
        holder.titleView.setText(dataSet.get(position).getTitle());
        holder.remarkView.setText(dataSet.get(position).getRemark());
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public void addItem(DataModel item, int position){
        try {
            dataSet.add(item);
            notifyItemRemoved(position);
        }
        catch (Exception e){
            Log.e("MainActivity",e.getMessage());
        }
    }

    public DataModel removeItem(int position){
        DataModel item = null;
        try {
            item = dataSet.get(position);
            dataSet.remove(position);
            notifyItemRemoved(position);

        }
        catch (Exception e){
            Log.e("Adapter",e.getMessage());
        }
        return item;
    }
}
