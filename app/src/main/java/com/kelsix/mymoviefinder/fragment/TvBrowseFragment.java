package com.kelsix.mymoviefinder.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kelsix.mymoviefinder.BuildConfig;
import com.kelsix.mymoviefinder.R;
import com.kelsix.mymoviefinder.adapter.GenreAdapter;
import com.kelsix.mymoviefinder.adapter.PeriodAdapter;
import com.kelsix.mymoviefinder.adapter.RegionAdapter;
import com.kelsix.mymoviefinder.adapter.SortByAdapter;
import com.kelsix.mymoviefinder.adapter.TvAdapter;
import com.kelsix.mymoviefinder.api.ApiClient;
import com.kelsix.mymoviefinder.api.ApiService;
import com.kelsix.mymoviefinder.model.GenreModel;
import com.kelsix.mymoviefinder.model.RegionModel;
import com.kelsix.mymoviefinder.model.TvModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TvBrowseFragment extends Fragment {
    private RecyclerView regionRecyclerView, genreRecyclerView, periodRecyclerView, sortByRecyclerView, tvRecyclerView;
    private RegionAdapter regionAdapter;
    private String selectedCountryCode = "";
    private String selectedGenre = "";
    private String selectedPeriod = "";
    private String selectedSortBy = "popularity.desc";

    private String apiKey = BuildConfig.API_KEY;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_browse_tv, container, false);

        regionRecyclerView = view.findViewById(R.id.regionRecyclerView);
        genreRecyclerView = view.findViewById(R.id.genreRecyclerView);
        periodRecyclerView = view.findViewById(R.id.periodRecyclerView);
        sortByRecyclerView = view.findViewById(R.id.sortByRecyclerView);
        tvRecyclerView = view.findViewById(R.id.tvRecyclerView);

        // Fetch regions and genres
        fetchRegions();
        fetchGenres();

        // Set up RecyclerViews for period and sortBy
        setupPeriodRecyclerView();
        setupSortByRecyclerView();

        // Fetch TV shows based on selected filters
        fetchTvShows();

        return view;
    }

    private void fetchRegions() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<List<RegionModel>> call = apiService.getRegions(apiKey);

        call.enqueue(new Callback<List<RegionModel>>() {
            @Override
            public void onResponse(Call<List<RegionModel>> call, Response<List<RegionModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<RegionModel> regions = response.body();
                    List<String> regionNames = new ArrayList<>();
                    regionNames.add("All Regions");
                    for (RegionModel region : regions) {
                        regionNames.add(region.getEnglish_name());
                    }
                    setupRegionRecyclerView(regionNames, regions);
                }
            }

            @Override
            public void onFailure(Call<List<RegionModel>> call, Throwable t) {
                // Handle failure
            }
        });
    }

    private void fetchGenres() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<GenreModel> call = apiService.getTvGenres(apiKey);

        call.enqueue(new Callback<GenreModel>() {
            @Override
            public void onResponse(Call<GenreModel> call, Response<GenreModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<GenreModel.Genre> genres = response.body().getGenres();
                    GenreModel.Genre allGenres = new GenreModel.Genre();
                    allGenres.setId(0);
                    allGenres.setName("All Genres");
                    genres.add(0, allGenres);
                    setupGenreRecyclerView(genres);
                }
            }

            @Override
            public void onFailure(Call<GenreModel> call, Throwable t) {
                // Handle failure
            }
        });
    }

    private void setupRegionRecyclerView(List<String> regions, List<RegionModel> regionModels) {
        regionRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        regionAdapter = new RegionAdapter(regions, region -> {
            selectedCountryCode = region.equals("All Regions") ? "" : getIsoCodeFromRegion(region, regionModels);
            fetchTvShows();
        });
        regionRecyclerView.setAdapter(regionAdapter);
    }

    private String getIsoCodeFromRegion(String regionName, List<RegionModel> regionModels) {
        for (RegionModel region : regionModels) {
            if (region.getEnglish_name().equals(regionName)) {
                return region.getIso_3166_1();
            }
        }
        return "";
    }

    private void setupGenreRecyclerView(List<GenreModel.Genre> genres) {
        genreRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        GenreAdapter genreAdapter = new GenreAdapter(genres, genre -> {
            selectedGenre = genre.getId() == 0 ? "" : String.valueOf(genre.getId());
            fetchTvShows();
        });
        genreRecyclerView.setAdapter(genreAdapter);
    }

    private void setupPeriodRecyclerView() {
        List<String> periods = Arrays.asList("All Periods", "2024", "2023", "2022", "2021", "2020", "2019", "2018", "2017", "2016", "2015", "2014", "2013", "2012", "2011", "2001-2010", "1991-2000", "1881-1990", "1871-1880");
        periodRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        PeriodAdapter periodAdapter = new PeriodAdapter(periods, period -> {
            selectedPeriod = period.equals("All Periods") ? "" : period;
            fetchTvShows();
        });
        periodRecyclerView.setAdapter(periodAdapter);
    }

    private void setupSortByRecyclerView() {
        List<String> sortByOptions = Arrays.asList("Popular", "Title", "Latest", "Score");
        sortByRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        SortByAdapter sortByAdapter = new SortByAdapter(sortByOptions, sortBy -> {
            switch (sortBy) {
                case "Popular":
                    selectedSortBy = "popularity.desc";
                    break;
                case "Title":
                    selectedSortBy = "original_name.asc";
                    break;
                case "Latest":
                    selectedSortBy = "first_air_date.desc";
                    break;
                case "Score":
                    selectedSortBy = "vote_average.desc";
                    break;
            }
            fetchTvShows();
        });
        sortByRecyclerView.setAdapter(sortByAdapter);
    }

    private void fetchTvShows() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        String startDate = "";
        String endDate = "";

        // Handle period range
        if (!selectedPeriod.equals("") && selectedPeriod.contains("-")) {
            String[] years = selectedPeriod.split("-");
            startDate = years[0] + "-01-01";
            endDate = years[1] + "-12-31";
        } else if (!selectedPeriod.equals("") && !selectedPeriod.equals("All Periods")) {
            startDate = selectedPeriod + "-01-01";
            endDate = selectedPeriod + "-12-31";
        }

        Call<TvModel> call = apiService.discoverTvShows(apiKey, selectedCountryCode, selectedGenre, startDate, endDate, selectedSortBy);

        call.enqueue(new Callback<TvModel>() {
            @Override
            public void onResponse(Call<TvModel> call, Response<TvModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<TvModel> tvShows = response.body().getResults();
                    setupTvRecyclerView(tvShows);
                }
            }

            @Override
            public void onFailure(Call<TvModel> call, Throwable t) {
                // Handle failure
            }
        });
    }

    private void setupTvRecyclerView(List<TvModel> tvShows) {
        tvRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        TvAdapter tvAdapter = new TvAdapter(tvShows, tvShow -> {
            Bundle bundle = new Bundle();
            bundle.putInt("id", tvShow.getId());
            Navigation.findNavController(requireView()).navigate(R.id.action_browseFragment_to_tvDetailFragment, bundle);
        });
        tvRecyclerView.setAdapter(tvAdapter);
    }
}
