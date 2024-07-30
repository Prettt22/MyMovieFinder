package com.kelsix.mymoviefinder.api;

import com.kelsix.mymoviefinder.model.ActorModel;
import com.kelsix.mymoviefinder.model.GenreModel;
import com.kelsix.mymoviefinder.model.MovieModel;
import com.kelsix.mymoviefinder.model.RegionModel;
import com.kelsix.mymoviefinder.model.TrailerModel;
import com.kelsix.mymoviefinder.model.TvModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    @GET("trending/movie/day")
    Call<MovieModel> getTrendingMovies(@Query("api_key") String apiKey);

    @GET("movie/popular")
    Call<MovieModel> getPopularMovies(@Query("api_key") String apiKey);

    @GET("movie/now_playing")
    Call<MovieModel> getNowPlayingMovies(@Query("api_key") String apiKey);

    @GET("movie/top_rated")
    Call<MovieModel> getTopRatedMovies(@Query("api_key") String apiKey);

    @GET("movie/upcoming")
    Call<MovieModel> getUpcomingMovies(@Query("api_key") String apiKey);

    @GET("movie/{movie_id}")
    Call<MovieModel> getMovieDetails(@Path("movie_id") int movieId, @Query("api_key") String apiKey);

    @GET("movie/{movie_id}/credits")
    Call<ActorModel> getCast(@Path("movie_id") int movieId, @Query("api_key") String apiKey);

    @GET("search/movie")
    Call<MovieModel> searchMovie(
            @Query("api_key") String apiKey,
            @Query("query") String query
    );

    @GET("person/{actor_id}")
    Call<ActorModel> getActorDetails(@Path("actor_id") int actorId, @Query("api_key") String apiKey);

    @GET("search/person")
    Call<ActorModel> searchActor(
            @Query("api_key") String apiKey,
            @Query("query") String query
    );

    @GET("tv/{tv_id}")
    Call<TvModel> getTvDetails(@Path("tv_id") int tvId, @Query("api_key") String apiKey);

    @GET("search/tv")
    Call<TvModel> searchTv(
            @Query("api_key") String apiKey,
            @Query("query") String query
    );

    @GET("configuration/countries")
    Call<List<RegionModel>> getRegions(@Query("api_key") String apiKey);

    @GET("genre/movie/list")
    Call<GenreModel> getMovieGenres(@Query("api_key") String apiKey);

    @GET("discover/movie")
    Call<MovieModel> discoverMovies(@Query("api_key") String apiKey,
                                    @Query("with_origin_country") String originCountry,
                                    @Query("with_genres") String genres,
                                    @Query("primary_release_date.gte") String startDate,
                                    @Query("primary_release_date.lte") String endDate,
                                    @Query("sort_by") String sortBy);

    @GET("genre/tv/list")
    Call<GenreModel> getTvGenres(@Query("api_key") String apiKey);

    @GET("discover/tv")
    Call<TvModel> discoverTvShows(
            @Query("api_key") String apiKey,
            @Query("with_origin_country") String originCountry,
            @Query("with_genres") String genres,
            @Query("first_air_date.gte") String startDate,
            @Query("first_air_date.lte") String endDate,
            @Query("sort_by") String sortBy
    );

    @GET("movie/{movie_id}/videos")
    Call<TrailerModel> getMovieTrailers(@Path("movie_id") int movieId, @Query("api_key") String apiKey);

    @GET("tv/{tv_id}/videos")
    Call<TrailerModel> getTvTrailers(@Path("tv_id") int tvId, @Query("api_key") String apiKey);
}
