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
import com.kelsix.mymoviefinder.adapter.ActorAdapter;
import com.kelsix.mymoviefinder.api.ApiClient;
import com.kelsix.mymoviefinder.api.ApiService;
import com.kelsix.mymoviefinder.model.ActorModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActorSearchFragment extends Fragment {
    private ActorAdapter actorAdapter;
    private RecyclerView actorRecyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_actor, container, false);

        actorRecyclerView = view.findViewById(R.id.actorRecyclerView);
        actorRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        if (getArguments() != null) {
            String query = getArguments().getString("query");
            fetchSearch(query);
        }

        return view;
    }

    private void fetchSearch(String query) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        String apiKey = BuildConfig.API_KEY;
        Call<ActorModel> call = apiService.searchActor(apiKey, query);

        call.enqueue(new Callback<ActorModel>() {
            @Override
            public void onResponse(@NonNull Call<ActorModel> call, @NonNull Response<ActorModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ActorModel> searchResults = response.body().getResults();

                    actorAdapter = new ActorAdapter(searchResults, actor -> {
                        Bundle bundle = new Bundle();
                        bundle.putInt("id", actor.getId());
                        Navigation.findNavController(requireView()).navigate(R.id.action_searchFragment_to_actorDetailFragment, bundle);
                    });
                    actorRecyclerView.setAdapter(actorAdapter);
                } else {
                    Toast.makeText(getContext(), "Failed to retrieve data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ActorModel> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Error fetching data", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
