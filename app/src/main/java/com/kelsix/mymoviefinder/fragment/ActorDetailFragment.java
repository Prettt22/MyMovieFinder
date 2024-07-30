package com.kelsix.mymoviefinder.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.kelsix.mymoviefinder.BuildConfig;
import com.kelsix.mymoviefinder.R;
import com.kelsix.mymoviefinder.api.ApiClient;
import com.kelsix.mymoviefinder.api.ApiService;
import com.kelsix.mymoviefinder.model.ActorModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActorDetailFragment extends Fragment {
    private NavController navController;
    private ImageView profileImageView, profileBackdropImageView, favoriteButton;
    private TextView biographyTextView, birthdayTextView, deathdayTextView,
            genderTextView, knownForDepartmentTextView, nameTextView,
            name2TextView, placeOfBirthTextView, popularityTextView;

    private SharedPreferences favoritePrefs;
    private static final String FAVORITE_PREFS = "favoritePrefs";
    private static final String FAVORITE_KEY = "favorites";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail_actor, container, false);
        navController = NavHostFragment.findNavController(this);

        view.findViewById(R.id.backButton).setOnClickListener(v -> navController.popBackStack());

        favoriteButton = view.findViewById(R.id.favoriteButton);
        biographyTextView = view.findViewById(R.id.biographyTextView);
        birthdayTextView = view.findViewById(R.id.birthdayTextView);
        deathdayTextView = view.findViewById(R.id.deathdayTextView);
        genderTextView = view.findViewById(R.id.genderTextView);
        knownForDepartmentTextView = view.findViewById(R.id.knownForDepartmentTextView);
        nameTextView = view.findViewById(R.id.nameTextView);
        name2TextView = view.findViewById(R.id.name2TextView);
        placeOfBirthTextView = view.findViewById(R.id.placeOfBirthTextView);
        popularityTextView = view.findViewById(R.id.popularityTextView);
        profileImageView = view.findViewById(R.id.profileImageView);
        profileBackdropImageView = view.findViewById(R.id.profileBackdropImageView);

        favoritePrefs = requireContext().getSharedPreferences(FAVORITE_PREFS, Context.MODE_PRIVATE);

        if (getArguments() != null) {
            int actorId = getArguments().getInt("id");
            String itemType = "Actor";
            fetchActorDetails(actorId);
            setupFavoriteButton(actorId, itemType);
        }
        return view;
    }

    private void setupFavoriteButton(int itemId, String itemType) {
        String itemKey = itemType + "_" + itemId;
        Set<String> favorites = favoritePrefs.getStringSet(FAVORITE_KEY, new HashSet<>());
        if (favorites.contains(itemKey)) {
            favoriteButton.setImageResource(R.drawable.baseline_favorite_24);
        } else {
            favoriteButton.setImageResource(R.drawable.baseline_favorite_border_24);
        }

        favoriteButton.setOnClickListener(v -> toggleFavorite(itemId, itemType));
    }

    @SuppressLint("MutatingSharedPrefs")
    private void toggleFavorite(int itemId, String itemType) {
        String itemKey = itemType + "_" + itemId;
        Set<String> favorites = favoritePrefs.getStringSet(FAVORITE_KEY, new HashSet<>());
        SharedPreferences.Editor editor = favoritePrefs.edit();
        if (favorites.contains(itemKey)) {
            favorites.remove(itemKey);
            favoriteButton.setImageResource(R.drawable.baseline_favorite_border_24);
            Toast.makeText(getContext(), "Removed from Favorites", Toast.LENGTH_SHORT).show();
        } else {
            favorites.add(itemKey);
            favoriteButton.setImageResource(R.drawable.baseline_favorite_24);
            Toast.makeText(getContext(), "Added to Favorites", Toast.LENGTH_SHORT).show();
        }
        editor.putStringSet(FAVORITE_KEY, favorites);
        editor.apply();
    }

    private void fetchActorDetails(int actorId) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        String apiKey = BuildConfig.API_KEY;
        Call<ActorModel> call = apiService.getActorDetails(actorId, apiKey);

        call.enqueue(new Callback<ActorModel>() {
            @SuppressLint("StringFormatMatches")
            @Override
            public void onResponse(@NonNull Call<ActorModel> call, @NonNull Response<ActorModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ActorModel actor = response.body();

                    biographyTextView.setText(getString(R.string.biography_format, actor.getBiography()));
                    birthdayTextView.setText(getString(R.string.birthday_format, formatDate(actor.getBirthday())));
                    deathdayTextView.setText(getString(R.string.deathday_format, formatDate(actor.getDeathday())));
                    genderTextView.setText(getString(R.string.gender_format, actor.getGenderString()));
                    knownForDepartmentTextView.setText(getString(R.string.known_for_department_format, actor.getKnownForDepartment()));
                    nameTextView.setText(getString(R.string.name_format, actor.getName()));
                    name2TextView.setText(getString(R.string.name2_format, actor.getName()));
                    placeOfBirthTextView.setText(getString(R.string.place_of_birth_format, actor.getPlaceOfBirth()));
                    popularityTextView.setText(getString(R.string.popularity_format, actor.getPopularity()));

                    String profilePath = actor.getProfilePath();
                    String imageUrl = "https://image.tmdb.org/t/p/w500" + profilePath;

                    if (profilePath != null && !profilePath.isEmpty()) {
                        Glide.with(requireContext())
                                .load(imageUrl)
                                .apply(new RequestOptions()
                                        .placeholder(R.drawable.ic_image)
                                        .transform(new RoundedCorners(16)))
                                .into(profileImageView);

                        Glide.with(requireContext())
                                .load(imageUrl)
                                .apply(new RequestOptions()
                                        .placeholder(R.drawable.ic_image)
                                        .transform(new RoundedCorners(16)))
                                .into(profileBackdropImageView);
                    }

                } else {
                    Toast.makeText(getContext(), "Failed to load actor details", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ActorModel> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Failed to load actor details", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private String formatDate(String date) {
        if (date == null || date.isEmpty()) return "";

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            Date parsedDate = dateFormat.parse(date);
            SimpleDateFormat newDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            return newDateFormat.format(parsedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

}
