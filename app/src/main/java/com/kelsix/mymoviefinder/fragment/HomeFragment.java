package com.kelsix.mymoviefinder.fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import com.kelsix.mymoviefinder.adapter.MovieSliderAdapter;
import com.kelsix.mymoviefinder.adapter.MovieAdapter;

import com.kelsix.mymoviefinder.R;
import com.kelsix.mymoviefinder.BuildConfig;
import com.kelsix.mymoviefinder.api.ApiClient;
import com.kelsix.mymoviefinder.api.ApiService;
import com.kelsix.mymoviefinder.model.MovieModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {
    private NavController navController;
    private ViewPager2 trendingViewPager;
    private RecyclerView popularRecyclerView, nowPlayingRecyclerView, topRatedRecyclerView, upcomingRecyclerView;
    private MovieSliderAdapter movieTrendingAdapter;
    private final String apiKey = BuildConfig.API_KEY;
    private final Handler handler = new Handler();

    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            int currentItem = trendingViewPager.getCurrentItem();
            int nextItem = currentItem + 1;
            if (nextItem >= movieTrendingAdapter.getItemCount()) {
                nextItem = 0;
            }
            trendingViewPager.setCurrentItem(nextItem);
            startAutoScroll();
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        navController = NavHostFragment.findNavController(this);

        trendingViewPager = view.findViewById(R.id.trendingViewPager);
        popularRecyclerView = view.findViewById(R.id.popularRecyclerView);
        nowPlayingRecyclerView = view.findViewById(R.id.nowPlayingRecyclerView);
        topRatedRecyclerView = view.findViewById(R.id.topRatedRecyclerView);
        upcomingRecyclerView = view.findViewById(R.id.upcomingRecyclerView);

        popularRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        nowPlayingRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        topRatedRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        upcomingRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));

        fetchTrendingMovies();
        fetchMovies(ApiService::getPopularMovies, popularRecyclerView);
        fetchMovies(ApiService::getNowPlayingMovies, nowPlayingRecyclerView);
        fetchMovies(ApiService::getTopRatedMovies, topRatedRecyclerView);
        fetchMovies(ApiService::getUpcomingMovies, upcomingRecyclerView);

        SearchView searchView = view.findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Bundle bundle = new Bundle();
                bundle.putString("query", query);
                navController.navigate(R.id.action_homeFragment_to_searchFragment, bundle);
                searchView.setQuery("", false);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopAutoScroll();
    }

    private void startAutoScroll() {
        handler.postDelayed(runnable, 10000);
    }

    private void stopAutoScroll() {
        handler.removeCallbacks(runnable);
    }

    private void fetchTrendingMovies() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<MovieModel> call = apiService.getTrendingMovies(apiKey);

        call.enqueue(new Callback<MovieModel>() {
            @Override
            public void onResponse(@NonNull Call<MovieModel> call, @NonNull Response<MovieModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<MovieModel> movieList = response.body().getResults();
                    movieTrendingAdapter = new MovieSliderAdapter(movieList, new MovieSliderAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(MovieModel movie) {
                            Bundle bundle = new Bundle();
                            bundle.putInt("id", movie.getId());
                            Navigation.findNavController(requireView()).navigate(R.id.action_homeFragment_to_movieDetailFragment, bundle);
                        }
                    });
                    trendingViewPager.setAdapter(movieTrendingAdapter);

                    trendingViewPager.setClipToPadding(false);
                    trendingViewPager.setClipChildren(false);
                    trendingViewPager.setOffscreenPageLimit(3);

                    CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
                    compositePageTransformer.addTransformer(new MarginPageTransformer(40));
                    compositePageTransformer.addTransformer(new ViewPager2.PageTransformer() {
                        @Override
                        public void transformPage(@NonNull View page, float position) {
                            float r = 1 - Math.abs(position);
                            page.setScaleY(0.95f + r * 0.06f);
                        }
                    });

                    trendingViewPager.setPageTransformer(compositePageTransformer);

                    startAutoScroll();
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

    private void fetchMovies(FetchMoviesFunction fetchMoviesFunction, RecyclerView recyclerView) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<MovieModel> call = fetchMoviesFunction.fetchMovies(apiService, apiKey);

        call.enqueue(new Callback<MovieModel>() {
            @Override
            public void onResponse(@NonNull Call<MovieModel> call, @NonNull Response<MovieModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<MovieModel> movieList = response.body().getResults();
                    MovieAdapter movieAdapter = new MovieAdapter(movieList, new MovieAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(MovieModel movie) {
                            Bundle bundle = new Bundle();
                            bundle.putInt("id", movie.getId());
                            navController.navigate(R.id.action_homeFragment_to_movieDetailFragment, bundle);
                        }
                    });
                    recyclerView.setAdapter(movieAdapter);
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

    @FunctionalInterface
    private interface FetchMoviesFunction {
        Call<MovieModel> fetchMovies(ApiService apiService, String apiKey);
    }
}
