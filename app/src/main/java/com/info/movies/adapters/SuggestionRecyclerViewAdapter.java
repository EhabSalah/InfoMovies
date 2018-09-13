package com.info.movies.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.info.movies.R;
import com.info.movies.fargments.search.SearchPresenter;
import com.info.movies.models.SearchSuggestionsModel;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by EhabSalah on 11/24/2017.
 */

public class SuggestionRecyclerViewAdapter extends RecyclerView.Adapter<SuggestionRecyclerViewAdapter.SuggestionViewHolder> {
    ArrayList<SearchSuggestionsModel> searchSuggestions;
    Context context;
    SearchPresenter mSearchPresenter;
    public SuggestionRecyclerViewAdapter(ArrayList<SearchSuggestionsModel> newSuggestion, Context context, SearchPresenter mSearchPresenter) {
        this.searchSuggestions = newSuggestion;
        this.context = context;
        this.mSearchPresenter = mSearchPresenter;
    }

    public static class SuggestionViewHolder extends RecyclerView.ViewHolder
    {
        private Context ctx ;

        @BindView(R.id.suggestion_text)
        TextView suggestion_title;
        @BindView(R.id.suggestion_layout)
        LinearLayout suggestion_layout;
        @BindView(R.id.suggestion_left_up_arrow)
        LinearLayout suggestion_left_up_arrow;

        public SuggestionViewHolder(final View itemView, final Context context) {
            super(itemView);
            ButterKnife.bind(this,itemView); // implement butterknife
            ctx = context;
        }

    }

    @Override
    public SuggestionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_item_layout_result_suggestion,parent,false);

        return new SuggestionViewHolder(v,context);
    }

    @Override
    public void onBindViewHolder(SuggestionViewHolder holder, int position) {
        final SearchSuggestionsModel item = searchSuggestions.get(position);
        holder.suggestion_title.setText(item.getSuggested_title());
        holder.suggestion_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSearchPresenter.onSuggestionClick(item.getSuggested_title());
            }
        });
        holder.suggestion_left_up_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSearchPresenter.onSuggestionLeftUpArrowClick(item.getSuggested_title());
                Log.d("TAG", "onClick:arrow ");
            }
        });
    }

    @Override
    public int getItemCount() {
        return searchSuggestions.size();
    }

    private static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
