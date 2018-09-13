package com.info.movies.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.info.movies.R;
import com.info.movies.constants.GlideApp;
import com.info.movies.fargments.popularmovies.PopularPresenterImp;
import com.info.movies.models.posters.PopularMovie;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by EhabSalah on 11/10/2017.
 */

public class PopularPosterRecyclerViewAdapter extends RecyclerView.Adapter<PopularPosterRecyclerViewAdapter.ImageViewHolder> {
    private ArrayList<PopularMovie> moviesArrayList ;
    private Activity activity;
    private Context context;
    private PopularPresenterImp fragmentPresenter; //for handling the onClick
    private static boolean show_info;
    static  final int FOOTER_TYPE = 0;
    static final int LIST_ITEM_TYPE = 1 ;
    public boolean show_footer ;
    public PopularPosterRecyclerViewAdapter(ArrayList<PopularMovie> moviesArrayList, Context context, Object fragmentPresenter){
        this.moviesArrayList = moviesArrayList;
        this.context = context;
        this.activity= (Activity) context;
        this.fragmentPresenter = (PopularPresenterImp) fragmentPresenter;
    }
    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v ;
        ImageViewHolder imageViewHolder;
        if (viewType == FOOTER_TYPE)
        {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.loading_item_poster,parent,false);
            imageViewHolder = new ImageViewHolder(v,context, viewType);
            return imageViewHolder ;
        }
        else if (viewType == LIST_ITEM_TYPE)
        {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.popular_movie_item_layout,parent,false);
            imageViewHolder = new ImageViewHolder(v,context, viewType);
            return imageViewHolder ;
        }
        return null;
    }

    @Override
    public void onBindViewHolder(ImageViewHolder holder, final int position) {
        if (holder.view_type == LIST_ITEM_TYPE) {
            GlideApp.with(activity)
                    .load(moviesArrayList.get(position).getPoster_image_url())
                    .placeholder(R.drawable.poster_placeholder)
                    .error(R.drawable.poster_placeholder)
                  //  .transition(DrawableTransitionOptions.withCrossFade(activity.getResources().getInteger(R.integer.load_cross_fade_posters)))
                    .into(holder.movie_poster);
            holder.movie_vote_average.setText(moviesArrayList.get(position).getVote_average());
            holder.movie_rank.setText(moviesArrayList.get(position).getRank());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    fragmentPresenter.onItemClicked(moviesArrayList.get(position).getMovie_id(),moviesArrayList.get(position).getTitle());
                }
            });
        }
    }
    @Override
    public int getItemViewType(int position) {
        if (show_footer) {
            if (position == moviesArrayList.size()) {
                return FOOTER_TYPE;
            }
            return LIST_ITEM_TYPE;
        }
        else
        {
            return LIST_ITEM_TYPE;
        }
    }
    @Override
    public int getItemCount() {
        if (show_footer) {
            return moviesArrayList.size()+1;
        }
        else
        {
            return moviesArrayList.size();
        }
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder{
        int view_type;

        @BindView(R.id.popular_poster_image)
        ImageView movie_poster;

        @BindView(R.id.popular_rank)
        TextView movie_rank;

        @BindView(R.id.popular_vote_average)
        TextView movie_vote_average;

        @BindView(R.id.popular_poster_average_vote_layout)
        LinearLayout average_vote_layout;

        View v;


        public ImageViewHolder(View itemView, Context context, int viewType) {
            super(itemView);
            if (viewType == FOOTER_TYPE)
            {
                view_type = 0;
            }
            else if (viewType == LIST_ITEM_TYPE) {
                ButterKnife.bind(this, itemView); // implement butterknife
                view_type = 1;
                v = itemView;
                if (show_info) {
                    average_vote_layout.setVisibility(View.VISIBLE);
                } else {
                    average_vote_layout.setVisibility(View.GONE);
                }
            }
        }
    }
    public void hideInfo() {
        show_info = false;
    }
    public void showInfo() {
        show_info = true;
    }

    public void hideFooter() {
        show_footer = false;
    }
    public void showFooter() {
        show_footer = true;
    }
}