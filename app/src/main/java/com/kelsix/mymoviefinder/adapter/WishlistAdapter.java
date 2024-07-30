package com.kelsix.mymoviefinder.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.RatingBar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.kelsix.mymoviefinder.R;
import com.kelsix.mymoviefinder.model.MovieModel;
import com.kelsix.mymoviefinder.model.TvModel;

import java.text.SimpleDateFormat;
import java.util.List;

public class WishlistAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<Object> wishlistItems;
    private OnItemClickListener onItemClickListener;

    private static final int TYPE_MOVIE = 1;
    private static final int TYPE_TV = 2;

    public interface OnItemClickListener {
        void onItemClick(Object item);
    }

    public WishlistAdapter(Context context, List<Object> wishlistItems, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.wishlistItems = wishlistItems;
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        if (wishlistItems.get(position) instanceof MovieModel) {
            return TYPE_MOVIE;
        } else {
            return TYPE_TV;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_MOVIE) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_movie, parent, false);
            return new MovieViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_tv, parent, false);
            return new TvViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Object item = wishlistItems.get(position);
        if (holder.getItemViewType() == TYPE_MOVIE) {
            MovieModel movie = (MovieModel) item;
            ((MovieViewHolder) holder).bind(movie);
        } else {
            TvModel tv = (TvModel) item;
            ((TvViewHolder) holder).bind(tv);
        }
        holder.itemView.setOnClickListener(v -> onItemClickListener.onItemClick(item));
    }

    @Override
    public int getItemCount() {
        return wishlistItems.size();
    }

    public static class MovieViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, releasedTextView, scoreTextView;
        ImageView imageView;
        RatingBar scoreRatingBar;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            releasedTextView = itemView.findViewById(R.id.releasedTextView);
            scoreTextView = itemView.findViewById(R.id.scoreTextView);
            imageView = itemView.findViewById(R.id.imageView);
            scoreRatingBar = itemView.findViewById(R.id.scoreRatingBar);
        }

        public void bind(MovieModel movie) {
            double rating = movie.getVoteAverage();
            titleTextView.setText(movie.getTitle());
            String releaseDateStr = movie.getReleaseDate();
            String year = "";

            @SuppressLint("SimpleDateFormat") SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
            @SuppressLint("SimpleDateFormat") SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy");

            try {
                year = outputFormat.format(inputFormat.parse(releaseDateStr));
            } catch (java.text.ParseException e) {
                Log.e("ParsingError", "Error parsing release date: " + e.getMessage());
            }

            releasedTextView.setText(year);

            float newValue = (float) rating;
            scoreRatingBar.setNumStars(5);
            scoreRatingBar.setStepSize((float) 0.5);
            scoreRatingBar.setRating(newValue / 2);

            @SuppressLint("DefaultLocale") String formattedValue = String.format("%.2f", newValue);
            scoreTextView.setText(formattedValue);
            Glide.with(itemView.getContext())
                    .load("https://image.tmdb.org/t/p/w500/" + movie.getPosterPath())
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.ic_image)
                            .transform(new RoundedCorners(16)))
                    .into(imageView);
        }
    }

    public static class TvViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, releasedTextView, scoreTextView;
        ImageView imageView;
        RatingBar scoreRatingBar;

        public TvViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            releasedTextView = itemView.findViewById(R.id.releasedTextView);
            scoreTextView = itemView.findViewById(R.id.scoreTextView);
            imageView = itemView.findViewById(R.id.imageView);
            scoreRatingBar = itemView.findViewById(R.id.scoreRatingBar);
        }

        public void bind(TvModel tv) {
            double rating = tv.getVoteAverage();
            titleTextView.setText(tv.getName());
            String releaseDateStr = tv.getFirstAirDate();
            String year = "";

            @SuppressLint("SimpleDateFormat") SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
            @SuppressLint("SimpleDateFormat") SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy");

            try {
                year = outputFormat.format(inputFormat.parse(releaseDateStr));
            } catch (java.text.ParseException e) {
                Log.e("ParsingError", "Error parsing release date: " + e.getMessage());
            }

            releasedTextView.setText(year);

            float newValue = (float) rating;
            scoreRatingBar.setNumStars(5);
            scoreRatingBar.setStepSize((float) 0.5);
            scoreRatingBar.setRating(newValue / 2);

            @SuppressLint("DefaultLocale") String formattedValue = String.format("%.2f", newValue);
            scoreTextView.setText(formattedValue);

            Glide.with(itemView.getContext())
                    .load("https://image.tmdb.org/t/p/w500/" + tv.getPosterPath())
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.ic_image)
                            .transform(new RoundedCorners(16)))
                    .into(imageView);
        }
    }
}
