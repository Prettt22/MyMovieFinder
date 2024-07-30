package com.kelsix.mymoviefinder.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kelsix.mymoviefinder.R;

import java.util.List;

public class PeriodAdapter extends RecyclerView.Adapter<PeriodAdapter.PeriodViewHolder> {

    private List<String> periodList;
    private OnItemClickListener listener;
    private String selectedPeriod = "All Periods";

    public interface OnItemClickListener {
        void onItemClick(String period);
    }

    public PeriodAdapter(List<String> periodList, OnItemClickListener listener) {
        this.periodList = periodList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PeriodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_button, parent, false);
        return new PeriodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PeriodViewHolder holder, int position) {
        String period = periodList.get(position);
        holder.periodTextView.setText(period);

        holder.itemView.setOnClickListener(v -> {
            selectedPeriod = period;
            listener.onItemClick(period);
            notifyDataSetChanged();
        });

        holder.periodTextView.setSelected(period.equals(selectedPeriod));
    }

    @Override
    public int getItemCount() {
        return periodList.size();
    }

    public static class PeriodViewHolder extends RecyclerView.ViewHolder {
        TextView periodTextView;

        public PeriodViewHolder(@NonNull View itemView) {
            super(itemView);
            periodTextView = itemView.findViewById(R.id.button);
        }
    }
}
