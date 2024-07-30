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
import com.kelsix.mymoviefinder.adapter.WishlistAdapter;
import com.kelsix.mymoviefinder.api.ApiClient;
import com.kelsix.mymoviefinder.api.ApiService;
import com.kelsix.mymoviefinder.model.MovieModel;
import com.kelsix.mymoviefinder.model.TvModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WishlistFragment extends Fragment implements WishlistAdapter.OnItemClickListener {
    private NavController navController;
    private RecyclerView wishlistRecyclerView;
    private WishlistAdapter wishlistAdapter;
    private List<Object> wishlistItems;

    private SharedPreferences sharedPreferences;
    private static final String WISHLIST_PREFS = "wishlistPrefs";
    private static final String WISHLIST_KEY = "wishlist";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wishlist, container, false);
        navController = NavHostFragment.findNavController(this);

        wishlistRecyclerView = view.findViewById(R.id.wishlistRecyclerView);
        wishlistRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        wishlistItems = new ArrayList<>();
        wishlistAdapter = new WishlistAdapter(getContext(), wishlistItems, this);
        wishlistRecyclerView.setAdapter(wishlistAdapter);

        sharedPreferences = requireContext().getSharedPreferences(WISHLIST_PREFS, Context.MODE_PRIVATE);

        TextView btn = view.findViewById(R.id.clearButton);
        btn.setOnClickListener(v -> clearWishlist());

        loadWishlist();

        return view;
    }

    private void loadWishlist() {
        Set<String> wishlist = sharedPreferences.getStringSet(WISHLIST_KEY, new HashSet<>());
        wishlistItems.clear();
        for (String item : wishlist) {
            String[] parts = item.split("_");
            String itemType = parts[0];
            int itemId = Integer.parseInt(parts[1]);
            if ("Movie".equals(itemType)) {
                fetchMovie(itemId);
            } else if ("TV".equals(itemType)) {
                fetchTv(itemId);
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
                    wishlistItems.add(response.body());
                    wishlistAdapter.notifyDataSetChanged();
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
                    wishlistItems.add(response.body());
                    wishlistAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(@NonNull Call<TvModel> call, @NonNull Throwable t) {
                // Handle failure
            }
        });
    }

    private void clearWishlist() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(WISHLIST_KEY);
        editor.apply();
        Toast.makeText(getContext(), "Wishlist cleared", Toast.LENGTH_SHORT).show();
        wishlistItems.clear();
        wishlistAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(Object item) {
        if (item instanceof MovieModel) {
            MovieModel movie = (MovieModel) item;
            Bundle bundle = new Bundle();
            bundle.putInt("id", movie.getId());
            navController.navigate(R.id.action_wishlistFragment_to_movieDetailFragment, bundle);

        } else if (item instanceof TvModel) {
            TvModel tv = (TvModel) item;
            Bundle bundle = new Bundle();
            bundle.putInt("id", tv.getId());
            navController.navigate(R.id.action_wishlistFragment_to_tvDetailFragment, bundle);
        }
    }
}
