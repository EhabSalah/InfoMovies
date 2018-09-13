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
import com.info.movies.constants.Common;
import com.info.movies.constants.GlideApp;
import com.info.movies.fargments.search.SearchPresenter;
import com.info.movies.models.SearchResultsModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by EhabSalah on 11/25/2017.
 */

public class ResultsRecyclerViewAdapter extends RecyclerView.Adapter<ResultsRecyclerViewAdapter.ResultsViewHolder> {
    static  final int FOOTER_TYPE = 0;
    static final int LIST_ITEM_TYPE = 1 ;
    private ArrayList<SearchResultsModel> searchResultModels;
    private Context context;
    private SearchPresenter mSearchPresenter;
    Activity activity;
    public boolean show_footer ;

    public ResultsRecyclerViewAdapter(ArrayList<SearchResultsModel> searchResultModels, Context context, SearchPresenter mSearchPresenter,boolean show_footer) {
        this.searchResultModels = searchResultModels;
        this.context = context;
        this.mSearchPresenter = mSearchPresenter;
        this.activity = (Activity) context;
        this.show_footer = show_footer;
    }

    @Override
    public ResultsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v ;
        ResultsViewHolder resultsViewHolder;
        if (viewType == FOOTER_TYPE)
        {
            v  = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_item_result_row_loading,parent,false);
            resultsViewHolder = new ResultsViewHolder(v,context,viewType);
            return resultsViewHolder;
        }
        else if (viewType == LIST_ITEM_TYPE)
        {
            v  = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_item_layout_result,parent,false);
            resultsViewHolder = new ResultsViewHolder(v,context,viewType);
            return resultsViewHolder;
        }
        return null;
    }

    @Override
    public void onBindViewHolder(ResultsViewHolder holder, final int position) {
        if (holder.view_type == LIST_ITEM_TYPE)
        {
            final SearchResultsModel item = searchResultModels.get(position);
            String release_date = item.getRelease_year();


            if (release_date!=null && !release_date.trim().isEmpty() && !release_date.trim().equals("")) {
                SimpleDateFormat s = new SimpleDateFormat("yyyy", Locale.ENGLISH);
                try {
                    String year = s.format(Common.sDF.parse(item.getRelease_year()));
                    holder.movie_title.setText(item.getMovie_title().concat(" ("+year+")"));
                } catch (ParseException e) {
                    holder.movie_title.setText(item.getMovie_title());
                    e.printStackTrace();
                }

            }else holder.movie_title.setText(item.getMovie_title());
            /*GlideApp.with(activity)
                    .load(item.getPoster_url())
                    .placeholder(R.drawable.poster_placeholder)
                    .error(R.drawable.poster_placeholder)
                    .transition(DrawableTransitionOptions.withCrossFade(activity.getResources().getInteger(R.integer.load_cross_fade_posters)))
                    .into(holder.movie_poster);*/
            GlideApp.with(activity)
                    .load(item.getPoster_url())
                    .placeholder(R.drawable.poster_placeholder)
                    .error(R.drawable.poster_placeholder)
                  //  .transition(DrawableTransitionOptions.withCrossFade(activity.getResources().getInteger(R.integer.load_cross_fade_posters)))
                    .into(holder.movie_poster);
            holder.v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mSearchPresenter.onItemClicked(item.getId(),item.getMovie_title());
                }
            });
        }
    }
    @Override
    public int getItemViewType(int position) {
        if (show_footer) {
            if (position == searchResultModels.size()) {
                return FOOTER_TYPE;
            }
            return LIST_ITEM_TYPE;
        }
        else
        {
            return LIST_ITEM_TYPE;
        }
/*        if (position == searchResultModels.size())
            return FOOTER_TYPE;
        return LIST_ITEM_TYPE;
        //return position % 2 * 2; // retuurning 0 or 2 according to the position.*/
    }
    @Override
    public int getItemCount()
    {
        if (show_footer)
        {
            return searchResultModels.size()+1;
        }
        else
        {
            return searchResultModels.size();
        }
    }

    public static class ResultsViewHolder extends RecyclerView.ViewHolder{
        int view_type;
        @BindView(R.id.movie_title)
        TextView movie_title;

        @BindView(R.id.movie_poster)
        ImageView movie_poster;

        //@BindView(R.id.movie_result_layout)
        View v;

        public ResultsViewHolder(final View itemView, final Context context, int viewType) {
            super(itemView);
            if (viewType == FOOTER_TYPE)
            {
                view_type = 0;
            }
            else if (viewType == LIST_ITEM_TYPE)
            {
                ButterKnife.bind(this,itemView); // implement butterknife
                view_type = 1;
                v = itemView;
               /* itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(context, "", Toast.LENGTH_SHORT).show();
                    }
                });*/
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
