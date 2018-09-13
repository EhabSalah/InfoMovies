package com.info.movies.db;

/**
 * Created by EhabSalah on 1/17/2018.
 */

public class DatabaseCntract {
    public DatabaseCntract() {
    }
    public static class WatchlistEntry{
        public static final String TABLE_NAME = "movies_watchlist";
        public static final String COL_ID = "movie_id";
        public static final String COL_POSTER_PATH = "movie_post_path";
        public static final String COL_TITLE = "movie_title";
        public static final String COL_GENRES = "movie_genres";

        public static final String COL_NO = "movie_no";
    }
    public static class RatedEntry{
        public static final String TABLE_NAME = "rated_movies";
        public static final String COL_ID = "movie_id";
        public static final String COL_RATE = "user_rate";
        public static final String COL_NO = "movie_no";
    }
}
