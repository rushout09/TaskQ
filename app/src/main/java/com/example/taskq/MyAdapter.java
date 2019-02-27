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
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;


public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private ArrayList<DataModel> dataSet;
    private static ClickListener clickListener;

    public void setOnItemClickListener(ClickListener clickListener) {
        MyAdapter.clickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClick(int position, View v);

        void onItemLongClick(int position, View v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdapter.MyViewHolder holder, int position) {
        holder.titleView.setText(dataSet.get(position).getTitle());
        holder.remarkView.setText(dataSet.get(position).getRemark());
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/YY hh:mm", Locale.getDefault());
        String datestr = formatter.format(new Date(Long.parseLong(dataSet.get(position).getTargetTimestamp())));
        holder.datetimeView.setText(datestr);
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

    public void addItem(DataModel item, int position){
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
        return dataSet.size();
    }

    public DataModel removeItem(int position){
        DataModel item = null;
        try {
            item = dataSet.get(position);
            dataSet.remove(position);
            notifyItemRemoved(position);
            if (item.getRepeat().compareToIgnoreCase("Once") != 0) {
                Calendar calendar = new GregorianCalendar();
                item.setInitialTimestamp(String.valueOf(calendar.getTimeInMillis()));
                if (item.getRepeat().compareToIgnoreCase("Daily") == 0) {
                    calendar.add(Calendar.DAY_OF_MONTH, 1);
                } else if (item.getRepeat().compareToIgnoreCase("Weekly") == 0) {
                    calendar.add(Calendar.DAY_OF_MONTH, 7);
                }
                item.setTargetTimestamp(String.valueOf(calendar.getTimeInMillis()));
                dataSet.add(item);
                sortData();
                notifyDataSetChanged();
            }
        }
        catch (Exception e){
            Log.e("Adapter",e.getMessage());
        }
        return item;
    }

    public DataModel deleteItem(int position) {
        DataModel item = null;
        try {
            item = dataSet.get(position);
            dataSet.remove(position);
            notifyItemRemoved(position);
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
                long rightTarTime = Long.parseLong(left.getTargetTimestamp());
                long leftIniTime = Long.parseLong(left.getInitialTimestamp());
                long rightIniTime = Long.parseLong(left.getInitialTimestamp());
                long currTime = Calendar.getInstance().getTimeInMillis();
                float leftUrgency;
                if (leftTarTime < leftIniTime || leftTarTime < currTime)
                    leftUrgency = 100;
                else
                    leftUrgency = 100 - ((float) ((leftTarTime - currTime) / (leftTarTime - leftIniTime)) * 100);
                leftUrgency = Math.round(leftUrgency / 5);
                float rightUrgency;
                if (rightTarTime < leftIniTime || rightTarTime < currTime)
                    rightUrgency = 100;
                else
                    rightUrgency = 100 - ((float) ((rightIniTime - currTime) / (rightTarTime - rightIniTime)) * 100);
                rightUrgency = Math.round(rightUrgency / 5);
                int leftImp = Integer.parseInt(left.getImportance());
                leftImp = Math.round((float) leftImp / 5);
                int rightImp = Integer.parseInt(right.getImportance());
                rightImp = Math.round((float) rightImp / 5);
                String leftType = left.getType();
                String rightType = right.getType();
                if (leftUrgency > rightUrgency) return 1;
                else if (leftUrgency < rightUrgency) return -1;
                else {
                    if (leftImp > rightImp) return 1;
                    else if (leftImp < rightImp) return -1;
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
            }
        });
    }

    protected static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        protected TextView titleView;
        protected TextView remarkView;
        protected CardView cardView;
        protected TextView datetimeView;

        protected MyViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
            this.titleView = view.findViewById(R.id.title_view);
            this.remarkView = view.findViewById(R.id.remark_view);
            this.cardView = view.findViewById(R.id.cardview);
            this.datetimeView = view.findViewById(R.id.datetime_TV);

        }

        @Override
        public void onClick(View view) {
            clickListener.onItemClick(getAdapterPosition(), view);
        }

        @Override
        public boolean onLongClick(View view) {
            clickListener.onItemLongClick(getAdapterPosition(), view);
            return false;
        }

    }

}
