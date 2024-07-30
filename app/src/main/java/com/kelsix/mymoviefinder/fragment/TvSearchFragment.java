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
import com.kelsix.mymoviefinder.adapter.TvAdapter;
import com.kelsix.mymoviefinder.api.ApiClient;
import com.kelsix.mymoviefinder.api.ApiService;
import com.kelsix.mymoviefinder.model.TvModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TvSearchFragment extends Fragment {
    private final String apiKey = BuildConfig.API_KEY;
    private TvAdapter tvAdapter;
    private RecyclerView tvRecyclerView;
    private String query;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_tv, container, false);

        tvRecyclerView = view.findViewById(R.id.tvRecyclerView);
        tvRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        if (getArguments() != null) {
            query = getArguments().getString("query");
            fetchSearch(query);
        }

        return view;
    }

    private void fetchSearch(String query) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<TvModel> call = apiService.searchTv(apiKey, query);

        call.enqueue(new Callback<TvModel>() {
            @Override
            public void onResponse(@NonNull Call<TvModel> call, @NonNull Response<TvModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<TvModel> searchResults = response.body().getResults();

                    tvAdapter = new TvAdapter(searchResults, tv -> {
                        Bundle bundle = new Bundle();
                        bundle.putInt("id", tv.getId());
                        Navigation.findNavController(requireView()).navigate(R.id.action_searchFragment_to_tvDetailFragment, bundle);
                    });
                    tvRecyclerView.setAdapter(tvAdapter);
                } else {
                    Toast.makeText(getContext(), "Failed to retrieve data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<TvModel> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Error fetching data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
