package com.kelsix.mymoviefinder.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kelsix.mymoviefinder.R;
import com.kelsix.mymoviefinder.model.GenreModel;

import java.util.List;

public class GenreAdapter extends RecyclerView.Adapter<GenreAdapter.GenreViewHolder> {

    private List<GenreModel.Genre> genreList;
    private OnItemClickListener listener;
    private int selectedGenreId = 0; // Assuming 0 represents "All Genres"

    public interface OnItemClickListener {
        void onItemClick(GenreModel.Genre genre);
    }

    public GenreAdapter(List<GenreModel.Genre> genreList, OnItemClickListener listener) {
        this.genreList = genreList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public GenreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_button, parent, false);
        return new GenreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GenreViewHolder holder, int position) {
        GenreModel.Genre genre = genreList.get(position);
        holder.genreTextView.setText(genre.getName());

        holder.itemView.setOnClickListener(v -> {
            selectedGenreId = genre.getId();
            listener.onItemClick(genre);
            notifyDataSetChanged();
        });

        holder.genreTextView.setSelected(genre.getId() == selectedGenreId);
    }

    @Override
    public int getItemCount() {
        return genreList.size();
    }

    public static class GenreViewHolder extends RecyclerView.ViewHolder {
        TextView genreTextView;

        public GenreViewHolder(@NonNull View itemView) {
            super(itemView);
            genreTextView = itemView.findViewById(R.id.button);
        }
    }
}
