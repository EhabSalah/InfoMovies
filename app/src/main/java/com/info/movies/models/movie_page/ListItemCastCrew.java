package com.info.movies.models.movie_page;

/**
 * Created by EhabSalah on 1/13/2018.
 */

public class ListItemCastCrew {
    private String name;
    private String image_path;
    private String character;

    public ListItemCastCrew(String name, String image_path, String character) {
        this.name = name;
        this.image_path = image_path;
        this.character = character;
    }

    public String getName() {
        return name;
    }

    public String getImage_path() {
        return image_path;
    }

    public String getCharacter() {
        return character;
    }
}
