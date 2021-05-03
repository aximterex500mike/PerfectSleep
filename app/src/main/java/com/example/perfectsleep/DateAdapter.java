package com.example.perfectsleep;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class DateAdapter extends RecyclerView.Adapter<DateAdapter.DateViewHolder> {
    private ArrayList<DateText> dateTexts;

    public static class DateViewHolder extends RecyclerView.ViewHolder{
        public TextView date;

        public DateViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.txtName);
        }
    }

    public DateAdapter(ArrayList<DateText> dateTexts){
        this.dateTexts = dateTexts;
    }

    @NonNull
    @Override
    public DateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.date_list,parent, false);
        DateViewHolder dateViewHolder = new DateViewHolder(v);
        return dateViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull DateViewHolder holder, int position) {
        DateText currentDate = dateTexts.get(position);

        holder.date.setText(currentDate.getDate());
    }

    @Override
    public int getItemCount() {
        return dateTexts.size();
    }
}
