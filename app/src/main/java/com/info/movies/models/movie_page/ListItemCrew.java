package com.info.movies.models.movie_page;

/**
 * Created by EhabSalah on 1/13/2018.
 */

public class ListItemCrew {
    private String name ;
    private String image_path ;
    private String job_title ;

    public ListItemCrew(String name, String image_path, String job_title) {
        this.name = name;
        this.image_path = image_path;
        this.job_title = job_title;
    }

    public String getName() {
        return name;
    }

    public String getImage_path() {
        return image_path;
    }

    public String getJob_title() {
        return job_title;
    }
}
