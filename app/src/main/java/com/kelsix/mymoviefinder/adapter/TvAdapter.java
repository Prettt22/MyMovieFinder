package com.kelsix.mymoviefinder.adapter;

import android.annotation.SuppressLint;
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
import com.kelsix.mymoviefinder.model.TvModel;

import java.text.SimpleDateFormat;
import java.util.List;

public class TvAdapter extends RecyclerView.Adapter<TvAdapter.TvViewHolder> {
    private final List<TvModel> tvList;
    private final TvAdapter.OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(TvModel tv);
    }

    public TvAdapter(List<TvModel> tvList, TvAdapter.OnItemClickListener listener) {
        this.tvList = tvList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TvAdapter.TvViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tv, parent, false);
        return new TvAdapter.TvViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TvAdapter.TvViewHolder holder, int position) {
        TvModel tv = tvList.get(position);
        holder.bind(tv, listener);
    }

    @Override
    public int getItemCount() {
        return tvList.size();
    }

    public static class TvViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView titleTextView;
        TextView releasedTextView;
        RatingBar scoreRatingBar;
        TextView scoreTextView;

        public TvViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            releasedTextView = itemView.findViewById(R.id.releasedTextView);
            scoreRatingBar = itemView.findViewById(R.id.scoreRatingBar);
            scoreTextView = itemView.findViewById(R.id.scoreTextView);
        }

        public void bind(final TvModel tv, final TvAdapter.OnItemClickListener listener) {
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

            itemView.setOnClickListener(v -> listener.onItemClick(tv));
        }
    }
}
