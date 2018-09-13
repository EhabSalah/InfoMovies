package com.info.movies.fargments.watchlist;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.info.movies.R;
import com.info.movies.db.DBHelper;
import com.info.movies.models.WatchlistItem;
import com.info.movies.MainActivity;
import com.info.movies.fargments.movie.MovieFragment;
import com.info.movies.fargments.watchlist.adapters.WatchlistAdapter;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class WatchlistFragment extends Fragment implements WatchlistView {


    private Toolbar toolbar;
    private TextView title;
    private OnWatchlistFragmentInteractionListener mListener;

    public WatchlistFragment() {
        // Required empty public constructor
    }

    RelativeLayout empty_watchlist_layout;
    RecyclerView watchlist_recyclerview;
    WatchlistPresenter mWatchlistPresenter;
    private RecyclerView.LayoutManager layoutManager;
    private WatchlistAdapter mAdapter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnWatchlistFragmentInteractionListener) {
            mListener = (OnWatchlistFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnAboutFragmentInteractionListener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mWatchlistPresenter = new WatchlistPresenterIMP(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_watchlist, container, false);

        initViews(v);
        DBHelper mDb = new DBHelper(getContext());
        initToolbar(toolbar, title);
        mWatchlistPresenter.onCreate(mDb);
        return v;
    }

    void initToolbar(Toolbar toolbar, TextView title) {
        SearchView mSearchView = toolbar.findViewById(R.id.mySearchView);
        mSearchView.setVisibility(View.GONE); // hide the search view
        title.setText(R.string.watchlist);
        title.setVisibility(View.VISIBLE);
    }

    private void initViews(View v) {
        empty_watchlist_layout = v.findViewById(R.id.watchlist_fragment_empty_watchlist_layout);
        watchlist_recyclerview = v.findViewById(R.id.watchlist_fragment_reciclerview);
        toolbar = getActivity().findViewById(R.id.toolbar);
        title = toolbar.findViewById(R.id.toolbar_title);
        ((MainActivity) getActivity()).showAppBarIfCollapsed();
        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
        params.setScrollFlags(0);
    }


    @Override
    public void onResume() {
        super.onResume();
        mListener.onWatchlistFragmentOpened();
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem item_search = menu.findItem(R.id.action_search).setVisible(false);
        MenuItem item_sort = menu.findItem(R.id.action_sort).setVisible(false);
        MenuItem item_select_date = menu.findItem(R.id.action_date_select).setVisible(false);
        MenuItem item_genre = menu.findItem(R.id.action_genre).setVisible(false);

    }

    @Override
    public void showRecyclerview() {
        watchlist_recyclerview.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideRecyclerview() {
        watchlist_recyclerview.setVisibility(View.GONE);
    }

    @Override
    public void showEmptyLayout() {
        empty_watchlist_layout.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideEmptyLayout() {
        empty_watchlist_layout.setVisibility(View.GONE);
    }


    public void receiveWatchlist(ArrayList<WatchlistItem> w) {
        Log.d("TAG_W", "receiveWatchlist: " + w.size());
        layoutManager = new LinearLayoutManager(getActivity());
        watchlist_recyclerview.setLayoutManager(layoutManager);
        watchlist_recyclerview.setHasFixedSize(true);
        mAdapter = new WatchlistAdapter(w, getActivity(), mWatchlistPresenter);
        watchlist_recyclerview.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void setToolbarTitle(String size) {
        Log.d("TAG_W", "setToolbarTitle: " + size);
        if (TextUtils.isEmpty(size)) {
            title.setText(R.string.watchlist);

        } else {
            title.setText(getResources().getString(R.string.watchlist) + " (" + size + ")");
        }
    }

    @Override
    public void moveToMoviePage(String id, String title) {
        MovieFragment mMovieFragment = new MovieFragment();
        Bundle bundle = new Bundle();
        bundle.putString("movie_id_key", id);
        bundle.putString("movie_title_key", title);
        mMovieFragment.setArguments(bundle);
        getActivity().getSupportFragmentManager().
                beginTransaction().
                replace(R.id.main_container, mMovieFragment).
                addToBackStack(null).
                commit();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnWatchlistFragmentInteractionListener {
        // TODO: Update argument type and name
        void onWatchlistFragmentOpened();
    }
}
