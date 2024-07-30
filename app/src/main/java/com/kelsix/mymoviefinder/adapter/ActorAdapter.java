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
import com.kelsix.mymoviefinder.model.ActorModel;

import java.text.SimpleDateFormat;
import java.util.List;

public class ActorAdapter extends RecyclerView.Adapter<ActorAdapter.ActorViewHolder> {
    private final List<ActorModel> actorList;
    private final ActorAdapter.OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(ActorModel actor);
    }

    public ActorAdapter(List<ActorModel> actorList, ActorAdapter.OnItemClickListener listener) {
        this.actorList = actorList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ActorAdapter.ActorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_actor, parent, false);
        return new ActorAdapter.ActorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActorAdapter.ActorViewHolder holder, int position) {
        ActorModel actor = actorList.get(position);
        holder.bind(actor, listener);
    }

    @Override
    public int getItemCount() {
        return actorList.size();
    }

    public static class ActorViewHolder extends RecyclerView.ViewHolder {
        ImageView profileImageView;
        TextView nameTextView;

        public ActorViewHolder(View itemView) {
            super(itemView);
            profileImageView = itemView.findViewById(R.id.profileImageView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
        }

        public void bind(final ActorModel actor, final ActorAdapter.OnItemClickListener listener) {
            nameTextView.setText(actor.getName());

            Glide.with(itemView.getContext())
                    .load("https://image.tmdb.org/t/p/w500/" + actor.getProfilePath())
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.ic_image)
                            .transform(new RoundedCorners(16)))
                    .into(profileImageView);

            itemView.setOnClickListener(v -> listener.onItemClick(actor));
        }
    }
}
