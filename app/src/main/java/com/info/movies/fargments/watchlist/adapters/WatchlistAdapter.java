package com.info.movies.fargments.watchlist.adapters;

import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.info.movies.R;
import com.info.movies.constants.Common;
import com.info.movies.constants.GlideApp;
import com.info.movies.db.DBHelper;
import com.info.movies.fargments.watchlist.WatchlistPresenter;
import com.info.movies.models.WatchlistItem;

import java.util.List;

/**
 * Created by EhabSalah on 1/18/2018.
 */

public class WatchlistAdapter extends RecyclerView.Adapter<WatchlistAdapter.MovieViewHolder>{
    private List<WatchlistItem> items;
    private Context context;
    private Activity activity;
    WatchlistPresenter mWatchlistPresenter;
    public WatchlistAdapter(List<WatchlistItem> items, Context context, WatchlistPresenter mMoviePresenter){
        this.items = items;
        this.context = context;
        this.activity = (Activity) context;
        this.mWatchlistPresenter=mMoviePresenter;
    }
    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v  = LayoutInflater.from(parent.getContext()).inflate(R.layout.watchlist_item,parent,false);

        return new MovieViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final MovieViewHolder holder, int position) {
        final WatchlistItem c = items.get(position);
        final String poster = c.getPoster_path();
        final String title = c.getTitle();
        final String genres = c.getGenres();
        final String ID = c.getId();
        GlideApp.
                with(activity).
                load("https://image.tmdb.org/t/p/w185/"+poster).
                placeholder(R.drawable.poster_placeholder).
                error(R.drawable.poster_placeholder).
                transition(DrawableTransitionOptions.withCrossFade(activity.getResources().getInteger(R.integer.load_cross_fade_posters))).

                into(holder.poster);
        holder.title.setText(title);
        holder.genres.setText(genres);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mWatchlistPresenter.onItemCLicked(ID,title);
            }
        });
        holder.unsave_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Common.isMovieAddedToWatchlist(ID,context))
                {
                    DBHelper watchlistDBHelper = new DBHelper(context); // db declaration
                    SQLiteDatabase sqLiteDatabase = watchlistDBHelper.getWritableDatabase();
                    if (watchlistDBHelper.daleteFromWatchlist(sqLiteDatabase,ID)) {
                        holder.watchlist_item_iv.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_bookmark_gray));
                        holder.watchlist_item_tv.setText("+");
                    }else
                    {
                        Log.d("TAG_M", "onClick: NOT DELETED");
                    }
                    watchlistDBHelper.close();
                }
                else
                {
                    DBHelper watchlistDBHelper = new DBHelper(context); // db declaration
                    SQLiteDatabase sqLiteDatabase = watchlistDBHelper.getWritableDatabase();
                    if (watchlistDBHelper.insertMovie(ID, title, poster, genres, sqLiteDatabase)) {
                        holder.watchlist_item_iv.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_bookmark_green));
                        holder.watchlist_item_tv.setText("✓");
                        Toast toast = Toast.makeText(context, "Added to Watchlist!", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                    watchlistDBHelper.close();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class MovieViewHolder extends RecyclerView.ViewHolder {
        ImageView poster;
        TextView title;
        TextView genres;
        View itemView ;
        TextView watchlist_item_tv;
        ImageView watchlist_item_iv;
        CoordinatorLayout unsave_layout;
        public MovieViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            poster = itemView.findViewById(R.id.wl_poster);
            title = itemView.findViewById(R.id.wl_title);
            genres = itemView.findViewById(R.id.wl_genres);
            unsave_layout = itemView.findViewById(R.id.watchlist_item_unsave_layout);
            watchlist_item_tv = itemView.findViewById(R.id.watchlist_item_tv);
            watchlist_item_iv = itemView.findViewById(R.id.watchlist_item_iv);
        }
    }
}
