package com.info.movies.fargments.movie.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.info.movies.R;
import com.info.movies.constants.GlideApp;
import com.info.movies.fargments.movie.MoviePresenter;


import java.util.List;

/**
 * Created by EhabSalah on 1/15/2018.
 */

public class MovieImagesAdapter extends RecyclerView.Adapter<MovieImagesAdapter.ImageViewHolder>{
    private MoviePresenter mMoviePresenter;
    private List<String> images;
    private Context context;
    private Activity activity;
    public MovieImagesAdapter(List<String> images, Context context, MoviePresenter mMoviePresenter){
        this.images = images;
        this.context = context;
        this.activity = (Activity) context;
        this.mMoviePresenter=mMoviePresenter;
    }
    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v  = LayoutInflater.from(parent.getContext()).inflate(R.layout.images_item_layout,parent,false);

        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ImageViewHolder holder, final int position) {
        String image_path = images.get(position);
        GlideApp.
                with(activity).
                load("https://image.tmdb.org/t/p/w500/"+image_path).
                placeholder(R.drawable.movie_images_placeholder).
                error(R.drawable.movie_images_placeholder).
                into(holder.image);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // presenter --> on video clicked: yfta7 el video.
                mMoviePresenter.onImageSelected(position,images);
            }
        });
        if (position==0) {
            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) holder.layout.getLayoutParams();
            lp.setMargins(25,10,6,10);
            holder.layout.requestLayout();
        }
        else if (position==images.size()-1)
        {

            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) holder.layout.getLayoutParams();
            lp.setMargins(6,10,25,10);
            holder.layout.requestLayout();
        }
        else
        {

            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) holder.layout.getLayoutParams();
            lp.setMargins(6,10,6,10);
            holder.layout.requestLayout();
        }
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        private CardView layout;
        ImageView image;
        View itemView ;
        public ImageViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            layout = itemView.findViewById(R.id.image_item_layout);
            image = itemView.findViewById(R.id.image_item_layout_image);
        }
    }

}
