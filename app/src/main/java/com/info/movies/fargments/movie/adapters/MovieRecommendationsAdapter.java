package com.info.movies.fargments.movie.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.info.movies.R;
import com.info.movies.constants.GlideApp;
import com.info.movies.fargments.movie.MoviePresenter;
import com.info.movies.models.posters.MoviePosterAR;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Ehab Salah on 4/17/2018.
 */

public class MovieRecommendationsAdapter extends RecyclerView.Adapter<MovieRecommendationsAdapter.RecommendationsViewHolder> {
    private static boolean show_info;
    private List<MoviePosterAR> recommendations;
    private Context context;
    private Activity activity;
    private MoviePresenter mMoviePresenter;

    public MovieRecommendationsAdapter(List<MoviePosterAR> recommendations, Context context, MoviePresenter mMoviePresenter) {
        this.recommendations = recommendations;
        this.context = context;
        this.activity = (Activity) context;
        this.mMoviePresenter = mMoviePresenter;
    }

    @Override
    public RecommendationsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recommeded_item_layout, parent, false);
        return new RecommendationsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final MovieRecommendationsAdapter.RecommendationsViewHolder holder, int position) {
        GlideApp.with(activity)
                .load(recommendations.get(holder.getAdapterPosition()).getPoster_image_url())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .override(300, 450)
                .placeholder(R.drawable.poster_placeholder)
                .error(R.drawable.poster_placeholder)
                //.transition(DrawableTransitionOptions.withCrossFade(activity.getResources().getInteger(R.integer.load_cross_fade_posters)))
                .into(holder.movie_poster);
        holder.movie_vote_average.setText(recommendations.get(position).getVote_average());
        holder.v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMoviePresenter.onRecommendedMovieClicked(recommendations.get(holder.getAdapterPosition()).getMovie_id(), recommendations.get(holder.getAdapterPosition()).getTitle());
            }
        });
        if (position==0) {
            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) holder.layout.getLayoutParams();
            lp.setMargins(25,10,5,10);
            holder.layout.requestLayout();
        }
        else if (position==recommendations.size()-1)
        {
            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) holder.layout.getLayoutParams();
            lp.setMargins(5,10,25,10);
            holder.layout.requestLayout();
        }
        else
        {
            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) holder.layout.getLayoutParams();
            lp.setMargins(5,10,5,10);
            holder.layout.requestLayout();
        }
    }


    @Override
    public int getItemCount() {
        return recommendations.size();
    }

    public static class RecommendationsViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.recommended_item_image)
        ImageView movie_poster;

        @BindView(R.id.recommended_item_average_vote)
        TextView movie_vote_average;

        @BindView(R.id.recommended_item_poster_info)
        LinearLayout info_layout;

        @BindView(R.id.recommended_item_layout)
        CardView layout;
        View v;

        public RecommendationsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            v = itemView;
            if (show_info)
            {info_layout.setVisibility(View.VISIBLE);
            } else info_layout.setVisibility(View.GONE);
        }
    }

    public void hideInfo() {
        show_info = false;
    }

    public void showInfo() {
        show_info = true;
    }

}
