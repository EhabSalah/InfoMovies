package com.info.movies.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by EhabSalah on 11/24/2017.
 */

public class SearchSuggestionsModel implements Parcelable {
    private String suggested_title;

    public SearchSuggestionsModel(String suggested_title) {
        this.suggested_title = suggested_title;
    }

    public String getSuggested_title() {
        return suggested_title;
    }

    public void setSuggested_title(String suggested_title) {
        this.suggested_title = suggested_title;
    }

/*************************************************************************************************/
    private SearchSuggestionsModel(Parcel in) {
        suggested_title = in.readString();
    }

    public static final Creator<SearchSuggestionsModel> CREATOR = new Creator<SearchSuggestionsModel>() {
        @Override
        public SearchSuggestionsModel createFromParcel(Parcel in) {
            return new SearchSuggestionsModel(in);
        }

        @Override
        public SearchSuggestionsModel[] newArray(int size) {
            return new SearchSuggestionsModel[size];
        }
    };


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(suggested_title);
    }
}
