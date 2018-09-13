package com.info.movies.models.movie_page;

/**
 * Created by EhabSalah on 1/13/2018.
 */

public class ListItemVideo {
    private String video_title;
    private String video_key;

    public ListItemVideo(String video_title, String video_key) {
        this.video_title = video_title;
        this.video_key = video_key;
    }

    public String getVideo_title() {
        return video_title;
    }

    public String getVideo_key() {
        return video_key;
    }
}
