package com.info.movies.models.movie_details;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Ehab Salah on 4/17/2018.
 */

public class Recommendations
{
    @SerializedName("page")

    private int page;
    @SerializedName("results")
    private ArrayList<Result2> results;

    @SerializedName("total_pages")

    private int total_pages;

    @SerializedName("total_results")
    private int total_results;

    public int getPage() { return this.page; }

    public void setPage(int page) { this.page = page; }


    public ArrayList<Result2> getResults() { return this.results; }

    public void setResults(ArrayList<Result2> results) { this.results = results; }


    public int getTotalPages() { return this.total_pages; }

    public void setTotalPages(int total_pages) { this.total_pages = total_pages; }


    public int getTotalResults() { return this.total_results; }

    public void setTotalResults(int total_results) { this.total_results = total_results; }
}