package com.kelsix.mymoviefinder.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.kelsix.mymoviefinder.R;
import com.kelsix.mymoviefinder.model.MovieModel;
import com.kelsix.mymoviefinder.model.TvModel;
import com.kelsix.mymoviefinder.model.ActorModel;

import java.text.SimpleDateFormat;
import java.util.List;

public class FavoriteAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<Object> favoriteItems;
    private OnItemClickListener listener;

    public FavoriteAdapter(Context context, List<Object> favoriteItems, OnItemClickListener listener) {
        this.context = context;
        this.favoriteItems = favoriteItems;
        this.listener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        if (favoriteItems.get(position) instanceof MovieModel) {
            return 0;
        } else if (favoriteItems.get(position) instanceof TvModel) {
            return 1;
        } else if (favoriteItems.get(position) instanceof ActorModel) {
            return 2;
        } else {
            return -1; // Handle unknown type
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 0) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_movie, parent, false);
            return new MovieViewHolder(view);
        } else if (viewType == 1) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_tv, parent, false);
            return new TvViewHolder(view);
        } else if (viewType == 2) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_actor, parent, false); // Create item_actor layout
            return new ActorViewHolder(view);
        } else {
            // Handle unknown type if necessary
            return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == 0) {
            MovieModel movie = (MovieModel) favoriteItems.get(position);
            ((MovieViewHolder) holder).bind(movie);
        } else if (holder.getItemViewType() == 1) {
            TvModel tv = (TvModel) favoriteItems.get(position);
            ((TvViewHolder) holder).bind(tv);
        } else if (holder.getItemViewType() == 2) {
            ActorModel actor = (ActorModel) favoriteItems.get(position);
            ((ActorViewHolder) holder).bind(actor);
        }
    }

    @Override
    public int getItemCount() {
        return favoriteItems.size();
    }

    public interface OnItemClickListener {
        void onItemClick(Object item);
    }

    class MovieViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, releasedTextView, scoreTextView;
        ImageView imageView;
        RatingBar scoreRatingBar;

        MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            releasedTextView = itemView.findViewById(R.id.releasedTextView);
            scoreTextView = itemView.findViewById(R.id.scoreTextView);
            imageView = itemView.findViewById(R.id.imageView);
            scoreRatingBar = itemView.findViewById(R.id.scoreRatingBar);

            itemView.setOnClickListener(v -> listener.onItemClick(favoriteItems.get(getAdapterPosition())));
        }

        void bind(MovieModel movie) {
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

    class TvViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, releasedTextView, scoreTextView;
        ImageView imageView;
        RatingBar scoreRatingBar;

        TvViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            releasedTextView = itemView.findViewById(R.id.releasedTextView);
            scoreTextView = itemView.findViewById(R.id.scoreTextView);
            imageView = itemView.findViewById(R.id.imageView);
            scoreRatingBar = itemView.findViewById(R.id.scoreRatingBar);

            itemView.setOnClickListener(v -> listener.onItemClick(favoriteItems.get(getAdapterPosition())));
        }

        void bind(TvModel tv) {
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

    class ActorViewHolder extends RecyclerView.ViewHolder {
        ImageView profileImageView;
        TextView nameTextView;

        ActorViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImageView = itemView.findViewById(R.id.profileImageView);
            nameTextView = itemView.findViewById(R.id.nameTextView);

            itemView.setOnClickListener(v -> listener.onItemClick(favoriteItems.get(getAdapterPosition())));
        }

        void bind(ActorModel actor) {
            nameTextView.setText(actor.getName());

            Glide.with(itemView.getContext())
                    .load("https://image.tmdb.org/t/p/w500/" + actor.getProfilePath())
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.ic_image)
                            .transform(new RoundedCorners(16)))
                    .into(profileImageView);
        }
    }
}
