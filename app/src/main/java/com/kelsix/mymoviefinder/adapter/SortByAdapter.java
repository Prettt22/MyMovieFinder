package com.kelsix.mymoviefinder.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kelsix.mymoviefinder.R;

import java.util.List;

public class SortByAdapter extends RecyclerView.Adapter<SortByAdapter.SortByViewHolder> {

    private List<String> sortByList;
    private OnItemClickListener listener;
    private String selectedSortBy = "Popular";

    public interface OnItemClickListener {
        void onItemClick(String sortBy);
    }

    public SortByAdapter(List<String> sortByList, OnItemClickListener listener) {
        this.sortByList = sortByList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SortByViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_button, parent, false);
        return new SortByViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SortByViewHolder holder, int position) {
        String sortBy = sortByList.get(position);
        holder.sortByTextView.setText(sortBy);

        holder.itemView.setOnClickListener(v -> {
            selectedSortBy = sortBy;
            listener.onItemClick(sortBy);
            notifyDataSetChanged();
        });

        holder.sortByTextView.setSelected(sortBy.equals(selectedSortBy));
    }

    @Override
    public int getItemCount() {
        return sortByList.size();
    }

    public static class SortByViewHolder extends RecyclerView.ViewHolder {
        TextView sortByTextView;

        public SortByViewHolder(@NonNull View itemView) {
            super(itemView);
            sortByTextView = itemView.findViewById(R.id.button);
        }
    }
}
