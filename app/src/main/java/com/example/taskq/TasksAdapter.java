package com.example.taskq;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;


public class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.MyViewHolder> {
    private ArrayList<DataModel> dataSet;
    private static ClickListener clickListener;

    public TasksAdapter(ArrayList<DataModel> dataSet) {
        this.dataSet = dataSet;
    }

    public interface ClickListener {
        void onItemClick(int position, View v);

        void onItemLongClick(int position, View v, RecyclerView.ViewHolder viewHolder);

    }

    public void setOnItemClickListener(ClickListener clickListener) {
        TasksAdapter.clickListener = clickListener;
    }

    @Override
    public void onBindViewHolder(@NonNull TasksAdapter.MyViewHolder holder, int position) {
        holder.titleView.setText(dataSet.get(position).getTitle());
        if (dataSet.get(position).getRemark().isEmpty())
            holder.remarkView.setVisibility(View.GONE);
        else {
            holder.remarkView.setVisibility(View.VISIBLE);
            holder.remarkView.setText(dataSet.get(position).getRemark());
        }
        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a, E (dd/MM)", Locale.getDefault());
        String datestr = formatter.format(new Date(Long.parseLong(dataSet.get(position).getTargetTimestamp())));
        holder.datetimeView.setText("Complete By " + datestr);
        holder.repeatTV.setText("Repeat: " + dataSet.get(position).getRepeat() + " Type: " + dataSet.get(position).getType());
    }

    @NonNull
    @Override
    public TasksAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item,parent,false);
        MyViewHolder vh = new MyViewHolder(view);
        return vh;
    }

    public void addItem(DataModel item, int position) {
        try {
            dataSet.add(position, item);
            notifyDataSetChanged();
        } catch (Exception e) {
            Log.e("MainActivity", e.getMessage());
        }
    }

    public void addItem(DataModel item) {
        try {
            dataSet.add(item);
            sortData();
            notifyDataSetChanged();
        }
        catch (Exception e){
            Log.e("MainActivity",e.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        if (dataSet == null)
            return 0;
        return dataSet.size();
    }


    public DataModel deleteItem(int position) {
        DataModel item = null;
        try {
            item = dataSet.get(position);
            dataSet.remove(position);
        } catch (Exception e) {
            Log.e("Adapter", e.getMessage());
        }

        return item;
    }

    protected void sortData() {
        Collections.sort(dataSet, new Comparator<DataModel>() {
            @Override
            public int compare(DataModel left, DataModel right) {
                long leftTarTime = Long.parseLong(left.getTargetTimestamp());
                long rightTarTime = Long.parseLong(right.getTargetTimestamp());
                leftTarTime = leftTarTime / (1000 * 60 * 60 * 4);
                rightTarTime = rightTarTime / (1000 * 60 * 60 * 4);
                String leftType = left.getType();
                String rightType = right.getType();
                if (leftTarTime < rightTarTime) return -1;
                else if (leftTarTime > rightTarTime) return 1;
                else {
                    if (leftType.compareToIgnoreCase("Productive") == 0 && rightType.compareToIgnoreCase("Productive") != 0)
                        return -1;
                    if (leftType.compareToIgnoreCase("Productive") != 0 && rightType.compareToIgnoreCase("Productive") == 0)
                        return 1;
                    if (leftType.compareToIgnoreCase("Chores") == 0 && rightType.compareToIgnoreCase("Chores") != 0)
                        return -1;
                    if (leftType.compareToIgnoreCase("Chores") != 0 && rightType.compareToIgnoreCase("Chores") == 0)
                        return 1;
                    return 0;
                }
            }
        });
    }

    protected static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        protected TextView titleView;
        protected TextView remarkView;
        protected CardView cardView;
        protected TextView datetimeView;
        protected TextView repeatTV;

        protected MyViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
            this.titleView = view.findViewById(R.id.title_view);
            this.remarkView = view.findViewById(R.id.remark_view);
            this.cardView = view.findViewById(R.id.cardview);
            this.datetimeView = view.findViewById(R.id.datetime_TV);
            this.repeatTV = view.findViewById(R.id.repeatItem_tv);

        }

        @Override
        public void onClick(View view) {
            clickListener.onItemClick(getAdapterPosition(), view);
        }

        @Override
        public boolean onLongClick(View view) {
            clickListener.onItemLongClick(getAdapterPosition(), view, this);
            return false;
        }

    }

}
