package com.kelsix.mymoviefinder.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kelsix.mymoviefinder.BuildConfig;
import com.kelsix.mymoviefinder.R;
import com.kelsix.mymoviefinder.adapter.FavoriteAdapter;
import com.kelsix.mymoviefinder.api.ApiClient;
import com.kelsix.mymoviefinder.api.ApiService;
import com.kelsix.mymoviefinder.model.ActorModel;
import com.kelsix.mymoviefinder.model.MovieModel;
import com.kelsix.mymoviefinder.model.TvModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavoriteFragment extends Fragment implements FavoriteAdapter.OnItemClickListener {
    private NavController navController;
    private RecyclerView favoriteRecyclerView;
    private FavoriteAdapter favoriteAdapter;
    private List<Object> favoriteItems;

    private SharedPreferences sharedPreferences;
    private static final String FAVORITE_PREFS = "favoritePrefs";
    private static final String FAVORITE_KEY = "favorites";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorite, container, false);
        navController = NavHostFragment.findNavController(this);

        favoriteRecyclerView = view.findViewById(R.id.favoriteRecyclerView);
        favoriteRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        favoriteItems = new ArrayList<>();
        favoriteAdapter = new FavoriteAdapter(getContext(), favoriteItems, this);
        favoriteRecyclerView.setAdapter(favoriteAdapter);

        sharedPreferences = requireContext().getSharedPreferences(FAVORITE_PREFS, Context.MODE_PRIVATE);

        TextView btn = view.findViewById(R.id.clearAllFavoritesButton);
        btn.setOnClickListener(v -> clearFavorites());

        loadFavorites();

        return view;
    }

    private void loadFavorites() {
        Set<String> favorites = sharedPreferences.getStringSet(FAVORITE_KEY, new HashSet<>());
        favoriteItems.clear();
        for (String item : favorites) {
            String[] parts = item.split("_");
            String itemType = parts[0];
            int itemId = Integer.parseInt(parts[1]);
            if ("Movie".equals(itemType)) {
                fetchMovie(itemId);
            } else if ("TV".equals(itemType)) {
                fetchTv(itemId);
            } else if ("Actor".equals(itemType)) {
                fetchActor(itemId);
            }
        }
    }

    private void fetchMovie(int movieId) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        String apiKey = BuildConfig.API_KEY;
        Call<MovieModel> call = apiService.getMovieDetails(movieId, apiKey);

        call.enqueue(new Callback<MovieModel>() {
            @Override
            public void onResponse(@NonNull Call<MovieModel> call, @NonNull Response<MovieModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    favoriteItems.add(response.body());
                    favoriteAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(@NonNull Call<MovieModel> call, @NonNull Throwable t) {
                // Handle failure
            }
        });
    }

    private void fetchTv(int tvId) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        String apiKey = BuildConfig.API_KEY;
        Call<TvModel> call = apiService.getTvDetails(tvId, apiKey);

        call.enqueue(new Callback<TvModel>() {
            @Override
            public void onResponse(@NonNull Call<TvModel> call, @NonNull Response<TvModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    favoriteItems.add(response.body());
                    favoriteAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(@NonNull Call<TvModel> call, @NonNull Throwable t) {
                // Handle failure
            }
        });
    }

    private void fetchActor(int actorId) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        String apiKey = BuildConfig.API_KEY;
        Call<ActorModel> call = apiService.getActorDetails(actorId, apiKey);

        call.enqueue(new Callback<ActorModel>() {
            @Override
            public void onResponse(@NonNull Call<ActorModel> call, @NonNull Response<ActorModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    favoriteItems.add(response.body());
                    favoriteAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ActorModel> call, @NonNull Throwable t) {
                // Handle failure
            }
        });
    }

    private void clearFavorites() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(FAVORITE_KEY);
        editor.apply();
        Toast.makeText(getContext(), "Favorites cleared", Toast.LENGTH_SHORT).show();
        favoriteItems.clear();
        favoriteAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(Object item) {
        if (item instanceof MovieModel) {
            MovieModel movie = (MovieModel) item;
            Bundle bundle = new Bundle();
            bundle.putInt("id", movie.getId());
            navController.navigate(R.id.action_favoriteFragment_to_movieDetailFragment, bundle);
        } else if (item instanceof TvModel) {
            TvModel tv = (TvModel) item;
            Bundle bundle = new Bundle();
            bundle.putInt("id", tv.getId());
            navController.navigate(R.id.action_favoriteFragment_to_tvDetailFragment, bundle);
        } else if (item instanceof ActorModel) {
            ActorModel tv = (ActorModel) item;
            Bundle bundle = new Bundle();
            bundle.putInt("id", tv.getId());
            navController.navigate(R.id.action_favoriteFragment_to_actorDetailFragment, bundle);
        }
    }
}
