package com.info.movies.fargments.movie.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.info.movies.R;
import com.info.movies.activities.image_slider.ImageSliderActivity;
import com.info.movies.constants.GlideApp;
import com.info.movies.fargments.movie.MoviePresenter;
import com.info.movies.models.movie_page.ListItemCastCrew;


import java.util.List;

/**
 * Created by EhabSalah on 1/15/2018.
 */

public class MovieCastAdapter extends RecyclerView.Adapter<MovieCastAdapter.CastViewHolder> {
    private final MoviePresenter mPresenter;
    private List<ListItemCastCrew> cast;
    private Context context;
    private Activity activity;

    public MovieCastAdapter(List<ListItemCastCrew> cast, Context context, MoviePresenter mMoviePresenter) {
        this.cast = cast;
        this.context = context;
        this.activity = (Activity) context;
        this. mPresenter = mMoviePresenter;
    }

    @Override
    public CastViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cast_item_layout, parent, false);

        return new CastViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final CastViewHolder holder, final int position) {
        ListItemCastCrew c = cast.get(position);
        final String image_path = c.getImage_path();
        final String name = c.getName();
        String character = c.getCharacter();
        GlideApp.
                with(activity).
                load("https://image.tmdb.org/t/p/w300/" + image_path).
                placeholder(R.drawable.person_profile_placeholder).
                error(R.drawable.person_profile_placeholder).
              //  transition(DrawableTransitionOptions.withCrossFade(activity.getResources().getInteger(R.integer.load_cross_fade_posters))).

                into(holder.image);
        holder.name.setText(name);
        holder.character.setText(character);

        if (position==0) {
            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) holder.layout.getLayoutParams();
            lp.setMargins(25,10,5,10);
            holder.layout.requestLayout();
        }
        else if (position==cast.size()-1)
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

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               context.startActivity(new Intent(context, ImageSliderActivity.class).putExtra("image",image_path).putExtra("title",name));
            }
        });
    }

    @Override
    public int getItemCount() {
        return cast.size();
    }

    public static class CastViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView name;
        TextView character;
        View itemView;
        CardView layout;

        public CastViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            layout = itemView.findViewById(R.id.cast_item_layout_root);
            image = itemView.findViewById(R.id.cast_item_image);
            name = itemView.findViewById(R.id.cast_item_name);
            character = itemView.findViewById(R.id.cast_item_character);
        }

    }
}
