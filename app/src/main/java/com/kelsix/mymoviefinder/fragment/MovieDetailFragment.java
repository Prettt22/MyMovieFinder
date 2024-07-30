package com.kelsix.mymoviefinder.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
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
import com.kelsix.mymoviefinder.model.MovieModel;
import com.kelsix.mymoviefinder.model.TrailerModel;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieDetailFragment extends Fragment {
    private NavController navController;
    private ImageView posterImageView, backdropImageView, wishlistButton, favoriteButton;
    private TextView titleTextView, synopsisTextView, originalTitleTextView, releaseDateTextView,
            genresTextView, runtimeTextView, statusTextView, originalLanguageTextView,
            taglineTextView, adultTextView, productionCompanyTextView, productionCountryTextView,
            spokenLanguageTextView, belongsToCollectionTextView, scoreTextView, popularityTextView,
            budgetTextView, revenueTextView, homepageTextView;
    private final ArrayList<String> trailerNames = new ArrayList<>();
    private final ArrayList<String> trailerKeys = new ArrayList<>();

    private SharedPreferences wishlistPrefs, favoritePrefs;
    private static final String WISHLIST_PREFS = "wishlistPrefs";
    private static final String FAVORITE_PREFS = "favoritePrefs";
    private static final String WISHLIST_KEY = "wishlist";
    private static final String FAVORITE_KEY = "favorites";
    private final String apiKey = BuildConfig.API_KEY;

    @SuppressLint("StringFormatMatches")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail_movie, container, false);
        navController = NavHostFragment.findNavController(this);

        view.findViewById(R.id.backButton).setOnClickListener(v -> navController.popBackStack());
        view.findViewById(R.id.trailerButton).setOnClickListener(v -> showTrailerListPopup());

        posterImageView = view.findViewById(R.id.posterImageView);
        backdropImageView = view.findViewById(R.id.backdropImageView);
        wishlistButton = view.findViewById(R.id.wishlistButton);
        favoriteButton = view.findViewById(R.id.favoriteButton);
        titleTextView = view.findViewById(R.id.titleTextView);
        synopsisTextView = view.findViewById(R.id.synopsisTextView);
        originalTitleTextView = view.findViewById(R.id.originalTitleTextView);
        releaseDateTextView = view.findViewById(R.id.releaseDateTextView);
        genresTextView = view.findViewById(R.id.genresTextView);
        runtimeTextView = view.findViewById(R.id.runtimeTextView);
        statusTextView = view.findViewById(R.id.statusTextView);
        originalLanguageTextView = view.findViewById(R.id.originalLanguageTextView);
        taglineTextView = view.findViewById(R.id.taglineTextView);
        adultTextView = view.findViewById(R.id.adultTextView);
        productionCompanyTextView = view.findViewById(R.id.productionCompanyTextView);
        productionCountryTextView = view.findViewById(R.id.productionCountryTextView);
        spokenLanguageTextView = view.findViewById(R.id.spokenLanguageTextView);
        belongsToCollectionTextView = view.findViewById(R.id.belongsToCollectionTextView);
        scoreTextView = view.findViewById(R.id.scoreTextView);
        popularityTextView = view.findViewById(R.id.popularityTextView);
        budgetTextView = view.findViewById(R.id.budgetTextView);
        revenueTextView = view.findViewById(R.id.revenueTextView);
        homepageTextView = view.findViewById(R.id.homepageTextView);

        wishlistPrefs = requireContext().getSharedPreferences(WISHLIST_PREFS, Context.MODE_PRIVATE);
        favoritePrefs = requireContext().getSharedPreferences(FAVORITE_PREFS, Context.MODE_PRIVATE);

        if (getArguments() != null) {
            int movieId = getArguments().getInt("id");
            String itemType = "Movie";
            fetchMovieDetails(movieId);
            fetchMovieTrailers(movieId);
            setupWishlistButton(movieId, itemType);
            setupFavoriteButton(movieId, itemType);
        }

        return view;
    }

    private void setupWishlistButton(int itemId, String itemType) {
        String itemKey = itemType + "_" + itemId;
        Set<String> wishlist = wishlistPrefs.getStringSet(WISHLIST_KEY, new HashSet<>());
        if (wishlist.contains(itemKey)) {
            wishlistButton.setImageResource(R.drawable.baseline_bookmark_24);
        } else {
            wishlistButton.setImageResource(R.drawable.baseline_bookmark_border_24);
        }

        wishlistButton.setOnClickListener(v -> toggleWishlist(itemId, itemType));
    }

    @SuppressLint("MutatingSharedPrefs")
    private void toggleWishlist(int itemId, String itemType) {
        String itemKey = itemType + "_" + itemId;
        Set<String> wishlist = wishlistPrefs.getStringSet(WISHLIST_KEY, new HashSet<>());
        SharedPreferences.Editor editor = wishlistPrefs.edit();
        if (wishlist.contains(itemKey)) {
            wishlist.remove(itemKey);
            wishlistButton.setImageResource(R.drawable.baseline_bookmark_border_24);
            Toast.makeText(getContext(), "Removed from Wishlist", Toast.LENGTH_SHORT).show();
        } else {
            wishlist.add(itemKey);
            wishlistButton.setImageResource(R.drawable.baseline_bookmark_24);
            Toast.makeText(getContext(), "Added to Wishlist", Toast.LENGTH_SHORT).show();
        }
        editor.putStringSet(WISHLIST_KEY, wishlist);
        editor.apply();
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

    private void fetchMovieDetails(int movieId) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        String apiKey = BuildConfig.API_KEY;
        Call<MovieModel> call = apiService.getMovieDetails(movieId, apiKey);

        call.enqueue(new Callback<MovieModel>() {
            @SuppressLint("StringFormatMatches")
            @Override
            public void onResponse(@NonNull Call<MovieModel> call, @NonNull Response<MovieModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    MovieModel movie = response.body();

                    titleTextView.setText(getString(R.string.title_format, movie.getTitle()));
                    synopsisTextView.setText(getString(R.string.overview_format, movie.getOverview()));
                    originalTitleTextView.setText(getString(R.string.original_title_format, movie.getOriginalTitle()));
                    releaseDateTextView.setText(getString(R.string.release_date_format, formatDate(movie.getReleaseDate())));
                    genresTextView.setText(getString(R.string.genres_format, movie.getGenresString()));
                    runtimeTextView.setText(getString(R.string.runtime_format, movie.getRuntime()));
                    statusTextView.setText(getString(R.string.status_format, movie.getStatus()));
                    originalLanguageTextView.setText(getString(R.string.original_language_format, movie.getOriginalLanguage()));
                    taglineTextView.setText(getString(R.string.tagline_format, movie.getTagline()));
                    adultTextView.setText(getString(R.string.adult_format, movie.isAdult() ? "Yes" : "No"));
                    productionCompanyTextView.setText(getString(R.string.production_company_format, movie.getProductionCompaniesString()));
                    productionCountryTextView.setText(getString(R.string.production_country_format, movie.getProductionCountriesString()));
                    spokenLanguageTextView.setText(getString(R.string.spoken_language_format, movie.getSpokenLanguagesString()));
                    belongsToCollectionTextView.setText(getString(R.string.belongs_to_collection_format, movie.getBelongsToCollectionString()));
                    homepageTextView.setText(getString(R.string.homepage_format, movie.getHomepage()));

                    String scoreText = String.format(getString(R.string.score_format), movie.getVoteAverage(), (int) movie.getVoteCount());
                    scoreTextView.setText(scoreText);

                    popularityTextView.setText(getString(R.string.popularity_format, movie.getPopularity()));
                    budgetTextView.setText(getString(R.string.budget_format, formatCurrency(movie.getBudget())));
                    revenueTextView.setText(getString(R.string.revenue_format, formatCurrency(movie.getRevenue())));

                    String posterPath = movie.getPosterPath();
                    if (posterPath != null && !posterPath.isEmpty()) {
                        Glide.with(requireContext())
                                .load("https://image.tmdb.org/t/p/w500/" + posterPath)
                                .apply(new RequestOptions()
                                        .placeholder(R.drawable.ic_image)
                                        .transform(new RoundedCorners(16)))
                                .into(posterImageView);
                    }

                    String backdropPath = movie.getBackdropPath();
                    if (backdropPath != null && !backdropPath.isEmpty()) {
                        Glide.with(requireContext())
                                .load("https://image.tmdb.org/t/p/w500/" + backdropPath)
                                .apply(new RequestOptions()
                                        .placeholder(R.drawable.ic_image)
                                        .transform(new RoundedCorners(16)))
                                .into(backdropImageView);
                    }
                } else {
                    Toast.makeText(getContext(), "Failed to load movie details", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<MovieModel> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Failed to load movie details", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchMovieTrailers(int movieId) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<TrailerModel> call = apiService.getMovieTrailers(movieId, apiKey);

        call.enqueue(new Callback<TrailerModel>() {
            @Override
            public void onResponse(@NonNull Call<TrailerModel> call, @NonNull Response<TrailerModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    TrailerModel trailerModel = response.body();
                    trailerNames.clear();
                    trailerKeys.clear();
                    for (TrailerModel.Trailer trailer : trailerModel.getResults()) {
                        trailerNames.add(trailer.getName());
                        trailerKeys.add(trailer.getKey());
                    }
                } else {
                    Toast.makeText(getContext(), "Failed to load trailers", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<TrailerModel> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Failed to load trailers", Toast.LENGTH_SHORT).show();
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

    private String formatCurrency(long amount) {
        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.US);
        return format.format(amount);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void showTrailerListPopup() {
        if (trailerNames.isEmpty() || trailerKeys.isEmpty()) {
            Toast.makeText(getContext(), "No trailers available", Toast.LENGTH_SHORT).show();
            return;
        }

        @SuppressLint("InflateParams") View popupView = LayoutInflater.from(getContext()).inflate(R.layout.popup_trailer_list, null);
        PopupWindow trailerPopup = new PopupWindow(popupView, 600, 1000, true);

        ListView trailerListView = popupView.findViewById(R.id.trailerListView);
        ImageView closeButton = popupView.findViewById(R.id.closeButton);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, trailerNames);
        trailerListView.setAdapter(adapter);

        trailerListView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedTrailerKey = trailerKeys.get(position);
            showTrailerPopup(selectedTrailerKey);
            trailerPopup.dismiss();
        });

        closeButton.setOnClickListener(v -> trailerPopup.dismiss());

        trailerPopup.setOutsideTouchable(true);
        trailerPopup.setTouchable(true);

        trailerPopup.showAtLocation(getView(), Gravity.CENTER, 0, 0);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void showTrailerPopup(String videoId) {
        @SuppressLint("InflateParams") View popupView = LayoutInflater.from(getContext()).inflate(R.layout.popup_trailer, null);
        PopupWindow trailerPopup = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, 450, true);

        WebView youtubeWebView = popupView.findViewById(R.id.youtube_webview);
        WebSettings webSettings = youtubeWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        youtubeWebView.setWebViewClient(new WebViewClient());

        String iframeHtml = "<html><head><style>*{padding: 0; margin: 0; box-sizing: border-box;}</style></head><body><iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/" + videoId + "\" frameborder=\"0\"></iframe></body></html>";
        youtubeWebView.loadData(iframeHtml, "text/html", "utf-8");

        trailerPopup.setOutsideTouchable(true);
        trailerPopup.setTouchable(true);

        trailerPopup.showAtLocation(getView(), Gravity.BOTTOM | Gravity.END, 0, 0);
    }
}
