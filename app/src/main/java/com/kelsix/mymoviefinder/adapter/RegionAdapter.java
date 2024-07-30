package com.kelsix.mymoviefinder.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kelsix.mymoviefinder.R;
import com.kelsix.mymoviefinder.model.RegionModel;

import java.util.List;

public class RegionAdapter extends RecyclerView.Adapter<RegionAdapter.RegionViewHolder> {

    private List<String> regionList;
    private OnItemClickListener listener;
    private String selectedRegion = "All Regions";

    public interface OnItemClickListener {
        void onItemClick(String region);
    }

    public RegionAdapter(List<String> regionList, OnItemClickListener listener) {
        this.regionList = regionList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RegionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_button, parent, false);
        return new RegionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RegionViewHolder holder, int position) {
        String region = regionList.get(position);
        holder.regionTextView.setText(region);

        holder.itemView.setOnClickListener(v -> {
            selectedRegion = region;
            listener.onItemClick(region);
            notifyDataSetChanged();
        });

        holder.regionTextView.setSelected(region.equals(selectedRegion));
    }

    @Override
    public int getItemCount() {
        return regionList.size();
    }

    public static class RegionViewHolder extends RecyclerView.ViewHolder {
        TextView regionTextView;

        public RegionViewHolder(@NonNull View itemView) {
            super(itemView);
            regionTextView = itemView.findViewById(R.id.button);
        }
    }
}
