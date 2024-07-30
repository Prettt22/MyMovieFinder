package com.kelsix.mymoviefinder.adapter;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.kelsix.mymoviefinder.R;
import com.kelsix.mymoviefinder.model.MovieModel;

import java.text.SimpleDateFormat;
import java.util.List;

public class MovieSliderAdapter extends RecyclerView.Adapter<MovieSliderAdapter.MovieViewHolder> {

    private final List<MovieModel> movieList;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(MovieModel movie);
    }

    public MovieSliderAdapter(List<MovieModel> movieList, OnItemClickListener listener) {
        this.movieList = movieList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movie_slider, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        MovieModel movie = movieList.get(position);
        holder.bind(movie, listener);
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    public static class MovieViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView titleTextView;
        TextView releasedTextView;
        TextView scoreTextView;

        public MovieViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            releasedTextView = itemView.findViewById(R.id.releasedTextView);
            scoreTextView = itemView.findViewById(R.id.scoreTextView);
        }

        public void bind(final MovieModel movie, final OnItemClickListener listener) {
            double rating = movie.getVoteAverage();
            titleTextView.setText(movie.getTitle());
            String releaseDateStr = movie.getReleaseDate();
            String year = "";

            @SuppressLint("SimpleDateFormat") SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
            @SuppressLint("SimpleDateFormat") SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy");

            try {
                if (releaseDateStr != null && !releaseDateStr.isEmpty()) {
                    year = outputFormat.format(inputFormat.parse(releaseDateStr));
                }
            } catch (java.text.ParseException e) {
                Log.e("ParsingError", "Error parsing release date: " + e.getMessage());
            }

            releasedTextView.setText(year);

            @SuppressLint("DefaultLocale") String formattedValue = String.format("%.2f", rating);
            scoreTextView.setText(formattedValue);

            Glide.with(itemView.getContext())
                    .load("https://image.tmdb.org/t/p/w500/" + movie.getBackdropPath())
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.ic_image)
                            .transform(new RoundedCorners(16)))
                    .into(imageView);

            itemView.setOnClickListener(v -> listener.onItemClick(movie));
        }
    }
}
