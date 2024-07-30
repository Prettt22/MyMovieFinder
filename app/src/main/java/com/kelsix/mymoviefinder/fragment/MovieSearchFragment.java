package com.kelsix.mymoviefinder.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kelsix.mymoviefinder.BuildConfig;
import com.kelsix.mymoviefinder.R;
import com.kelsix.mymoviefinder.adapter.MovieAdapter;
import com.kelsix.mymoviefinder.api.ApiClient;
import com.kelsix.mymoviefinder.api.ApiService;
import com.kelsix.mymoviefinder.model.MovieModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieSearchFragment extends Fragment {
    private final String apiKey = BuildConfig.API_KEY;
    private MovieAdapter movieAdapter;
    private RecyclerView movieRecyclerView;
    private String query;

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_movie, container, false);

        movieRecyclerView = view.findViewById(R.id.movieRecyclerView);
        movieRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        if (getArguments() != null) {
            query = getArguments().getString("query");
            fetchSearch(query);
        }

        return view;
    }

    private void fetchSearch(String query) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<MovieModel> call = apiService.searchMovie(apiKey, query);

        call.enqueue(new Callback<MovieModel>() {
            @Override
            public void onResponse(@NonNull Call<MovieModel> call, @NonNull Response<MovieModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<MovieModel> searchResults = response.body().getResults();

                    movieAdapter = new MovieAdapter(searchResults, movie -> {
                        Bundle bundle = new Bundle();
                        bundle.putInt("id", movie.getId());
                        Navigation.findNavController(requireView()).navigate(R.id.action_searchFragment_to_movieDetailFragment, bundle);
                    });
                    movieRecyclerView.setAdapter(movieAdapter);
                } else {
                    Toast.makeText(getContext(), "Failed to retrieve data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<MovieModel> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Error fetching data", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
