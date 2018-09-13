package com.info.movies.fargments.movie.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.info.movies.R;
import com.info.movies.constants.GlideApp;
import com.info.movies.fargments.movie.MoviePresenter;
import com.info.movies.models.movie_page.ListItemVideo;

import java.util.List;

/**
 * Created by EhabSalah on 1/14/2018.
 */

public class MovieVideosAdapter extends RecyclerView.Adapter<MovieVideosAdapter.VideosViewHolder>{
    private List<ListItemVideo> videos;
    private Context context;
    private Activity activity;
    private MoviePresenter mMoviePresenter;
    public MovieVideosAdapter(List<ListItemVideo> videos, Context context, MoviePresenter mMoviePresenter){
        this.videos = videos;
        this.context = context;
        this.activity = (Activity) context;
        this.mMoviePresenter =  mMoviePresenter;
    }
    @Override
    public VideosViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v  = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_videos_thumbnail_item_layout,parent,false);

        return new VideosViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final VideosViewHolder holder, final int position) {
        ListItemVideo video = videos.get(position);
        final String key = video.getVideo_key();
        String title = video.getVideo_title();
        GlideApp.
                with(activity).
                load("https://i1.ytimg.com/vi/"+key+"/hqdefault.jpg").
//                placeholder(R.drawable.placeholder_video).
//                error(R.drawable.placeholder_video).
                into(holder.thumbnail);
        holder.title.setText(title);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // presenter --> on video clicked: yfta7 el video.
                mMoviePresenter.onPlayTrailerClick(activity,key);
            }
        });
        if (position==0) {
            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) holder.video_layout.getLayoutParams();
            lp.setMargins(25,10,5,10);
            holder.video_layout.requestLayout();
        }
        else if (position==videos.size()-1)
        {
            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) holder.video_layout.getLayoutParams();
            lp.setMargins(5,10,25,10);
            holder.video_layout.requestLayout();
        }
        else
        {
            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) holder.video_layout.getLayoutParams();
            lp.setMargins(5,10,5,10);
            holder.video_layout.requestLayout();
        }
    }

    @Override
    public int getItemCount() {
        return videos.size();
    }

    public class VideosViewHolder extends RecyclerView.ViewHolder {
        ImageView thumbnail;
        ImageView ic_play;
        TextView title;
        CardView video_layout ;
        public VideosViewHolder(View itemView) {
            super(itemView);
            video_layout = itemView.findViewById(R.id.video_item_layout);
           thumbnail = itemView.findViewById(R.id.video_layout_thumbnail);
           ic_play = itemView.findViewById(R.id.video_layout_ic_play);
           title = itemView.findViewById(R.id.video_layout_title);
        }
    }

}
