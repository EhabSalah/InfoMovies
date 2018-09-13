package com.info.movies.models.generslise_response;

/**
 * Created by EhabSalah on 10/18/2017.
 */

public class Genre {
    int id;
    String name;

    public Genre(int id, String name) {
        this.setId(id);
        this.setName(name);
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
