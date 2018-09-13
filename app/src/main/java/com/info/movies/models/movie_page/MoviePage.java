package com.info.movies.models.movie_page;

import com.info.movies.models.posters.MoviePosterAR;

import java.util.List;

/**
 * Created by EhabSalah on 1/13/2018.
 */

public class MoviePage {


    private String trailer_key;
    private String movie_id;
    private String title;
    private String original_title;
    private String poster_path;
    private String genres;
    private String release_date;
    private String runtime;
    private String vote_average;
    private String vote_count;
    private String languages;
    private String overview;
    private List<ListItemVideo> videos;
    private List<String> images;
    private List<ListItemCastCrew> cast_crew; //
    private String director_name;
    private String  home_page;
    private String imdp_id;
    private String revenue;
    private String belongs_to;
    private List<String> companies;
    private List<String> countries;
    private String tagline;
    private List<MoviePosterAR> recommendations;

    public MoviePage() {
    }

    public String getMovie_id() {
        return movie_id;
    }

    public void setMovie_id(String movie_id) {
        this.movie_id = movie_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<MoviePosterAR> getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(List<MoviePosterAR> recommendations) {
        this.recommendations = recommendations;
    }

    public String getTagline() {
        return tagline;
    }

    public void setTagline(String tagline) {
        this.tagline = tagline;
    }

    public List<String> getCompanies() {
        return companies;
    }

    public void setCompanies(List<String> companies) {
        this.companies = companies;
    }

    public List<String> getCountries() {
        return countries;
    }

    public void setCountries(List<String> countries) {
        this.countries = countries;
    }

    public String getBelongs_to() {
        return belongs_to;
    }

    public void setBelongs_to(String belongs_to) {
        this.belongs_to = belongs_to;
    }

    public String getRevenue() {
        return revenue;
    }

    public void setRevenue(String revenue) {
        this.revenue = revenue;
    }

    public String getHome_page() {
        return home_page;
    }

    public void setHome_page(String home_page) {
        this.home_page = home_page;
    }

    public String getImdp_id() {
        return imdp_id;
    }

    public void setImdp_id(String imdp_id) {
        this.imdp_id = imdp_id;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }

    public String getTrailer_key() {
        return trailer_key;
    }

    public void setTrailer_key(String trailer_key) {
        this.trailer_key = trailer_key;
    }

    public String getOriginal_title() {
        return original_title;
    }

    public void setOriginal_title(String original_title) {
        this.original_title = original_title;
    }

    public String getGenres() {
        return genres;
    }

    public void setGenres(String genres) {
        this.genres = genres;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public String getRuntime() {
        return runtime;
    }

    public void setRuntime(String runtime) {
        this.runtime = runtime;
    }

    public String getVote_average() {
        return vote_average;
    }

    public void setVote_average(String vote_average) {
        this.vote_average = vote_average;
    }

    public String getVote_count() {
        return vote_count;
    }

    public void setVote_count(String vote_count) {
        this.vote_count = vote_count;
    }

    public String getLanguages() {
        return languages;
    }

    public void setLanguages(String languages) {
        this.languages = languages;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public List<ListItemVideo> getVideos() {
        return videos;
    }

    public void setVideos(List<ListItemVideo> videos) {
        this.videos = videos;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public List<ListItemCastCrew> getCast_crew() {
        return cast_crew;
    }

    public void setCast_crew(List<ListItemCastCrew> cast_crew) {
        this.cast_crew = cast_crew;
    }

    public String getDirector_name() {
        return director_name;
    }

    public void setDirector_name(String director_name) {
        this.director_name = director_name;
    }
}
