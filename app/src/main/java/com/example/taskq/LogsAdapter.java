package com.example.taskq;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class LogsAdapter extends RecyclerView.Adapter<LogsAdapter.MyViewHolder> {
    private static LogsAdapter.ClickListener clickListener;
    private ArrayList<DataModel> dataSet;

    public LogsAdapter(ArrayList<DataModel> dataSet) {
        this.dataSet = dataSet;
    }

    public void setOnItemClickListener(LogsAdapter.ClickListener clickListener) {
        LogsAdapter.clickListener = clickListener;
    }

    @NonNull
    @Override
    public LogsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item, viewGroup, false);
        LogsAdapter.MyViewHolder vh = new LogsAdapter.MyViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull LogsAdapter.MyViewHolder holder, int position) {
        holder.titleView.setText(dataSet.get(position).getTitle());
        if (dataSet.get(position).getRemark().isEmpty())
            holder.remarkView.setVisibility(View.GONE);
        else {
            holder.remarkView.setVisibility(View.VISIBLE);
            holder.remarkView.setText(dataSet.get(position).getRemark());
        }
        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a \nE (dd/MM)", Locale.getDefault());
        String datestr = formatter.format(new Date(Long.parseLong(dataSet.get(position).getDoneTimestamp())));
        holder.datetimeView.setText("Completed On " + datestr);
        holder.repeatTv.setText("Repeat: " + dataSet.get(position).getRepeat() + "\nType: " + dataSet.get(position).getType());
    }

    @Override
    public int getItemCount() {
        if (dataSet == null)
            return 0;
        return dataSet.size();
    }

    public interface ClickListener {
        void onItemClick(int position, View v);

        void onItemLongClick(int position, View v, RecyclerView.ViewHolder viewHolder);
    }

    protected static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        protected TextView titleView;
        protected TextView remarkView;
        protected CardView cardView;
        protected TextView datetimeView;
        protected TextView repeatTv;

        protected MyViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
            this.titleView = view.findViewById(R.id.title_view);
            this.remarkView = view.findViewById(R.id.remark_view);
            this.cardView = view.findViewById(R.id.cardview);
            this.datetimeView = view.findViewById(R.id.datetime_TV);
            this.repeatTv = view.findViewById(R.id.repeatItem_tv);
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
