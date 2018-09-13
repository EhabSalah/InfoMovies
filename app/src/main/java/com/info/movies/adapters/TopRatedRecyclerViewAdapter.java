package com.info.movies.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.info.movies.R;
import com.info.movies.constants.GlideApp;
import com.info.movies.fargments.toprated.TopRatedPresenterImp;
import com.info.movies.models.posters.TopRatedMovie;


import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by EhabSalah on 11/10/2017.
 */

public class TopRatedRecyclerViewAdapter extends RecyclerView.Adapter<TopRatedRecyclerViewAdapter.ImageViewHolder> {
    private ArrayList<TopRatedMovie> moviesArrayList ;
    private Activity activity;
    private Context context;
    private TopRatedPresenterImp fragmentPresenter; //for handling the onClick

    static  final int FOOTER_TYPE = 0;
    static final int LIST_ITEM_TYPE = 1 ;
    public boolean show_footer ;

    public TopRatedRecyclerViewAdapter(ArrayList<TopRatedMovie> moviesArrayList, Context context, Object fragmentPresenter){
        this.moviesArrayList = moviesArrayList;
        this.context = context;
        this.activity= (Activity) context;
        this.fragmentPresenter = (TopRatedPresenterImp) fragmentPresenter;
    }
    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        ImageViewHolder imageViewHolder;
        if (viewType == FOOTER_TYPE)
        {
            v= LayoutInflater.from(parent.getContext()).inflate(R.layout.loading_item_poster_with_info,parent,false);
            imageViewHolder = new ImageViewHolder(v,context,viewType);
            return imageViewHolder ;
        }
        else if (viewType == LIST_ITEM_TYPE)
        {
            v= LayoutInflater.from(parent.getContext()).inflate(R.layout.top_rated_movie_item_layout,parent,false);
            imageViewHolder = new ImageViewHolder(v,context,viewType);
            return imageViewHolder ;
        }
      return null;
    }

    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {
        if (holder.view_type == LIST_ITEM_TYPE) {

            final TopRatedMovie movie = moviesArrayList.get(position);
            GlideApp.with(activity)
                    .load(movie.getPoster_image_url())
                    .placeholder(R.drawable.poster_placeholder)
                    .error(R.drawable.poster_placeholder)
                //    .transition(DrawableTransitionOptions.withCrossFade(activity.getResources().getInteger(R.integer.load_cross_fade_posters)))
                    .into(holder.movie_poster);
            holder.movie_vote_average.setText(movie.getVote_average());
            holder.release_date.setText(movie.getRelease_date());
            holder.title.setText(movie.getTitle());
            holder.overView.setText(movie.getOverView());
            holder.geners.setText(movie.getGeners());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    fragmentPresenter.onItemClicked(movie.getMovie_id(),movie.getTitle());
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

        @BindView(R.id.top_rated_poster)
        ImageView movie_poster;

        @BindView(R.id.top_rated_movie_title)
        TextView title;

        @BindView(R.id.top_rated_average_vote)
        TextView movie_vote_average;

        @BindView(R.id.top_rated_release_date)
        TextView release_date;

        @BindView(R.id.top_rated_overView)
        TextView overView;

        @BindView(R.id.top_rated_geners)
        TextView geners;


        int view_type;

        View v ;

        public ImageViewHolder(View itemView, Context context, int viewType) {
            super(itemView);
            if (viewType == FOOTER_TYPE)
            {
                view_type = 0;
            }
            else if (viewType == LIST_ITEM_TYPE) {
                ButterKnife.bind(this,itemView); // implement butterknife
                view_type = 1;
                v=itemView;
            }

        }
    }
    public void hideFooter() {
        show_footer = false;
    }
    public void showFooter() {
        show_footer = true;
    }
}