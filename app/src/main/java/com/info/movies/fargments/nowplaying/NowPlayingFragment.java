package com.info.movies.fargments.nowplaying;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.info.movies.R;
import com.info.movies.constants.Utils;
import com.info.movies.fargments.movie.MovieFragment;
import com.info.movies.adapters.NowPlayingRecyclerViewAdapter;
import com.info.movies.constants.Common;
import com.info.movies.constants.EndlessRecyclerViewScrollListener;
import com.info.movies.constants.Setting;
import com.info.movies.fargments.search.SearchFragment;
import com.info.movies.models.posters.MoviePosterAR;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static android.support.design.widget.AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS;
import static android.support.design.widget.AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL;
import static android.support.design.widget.AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP;
import static android.view.View.VISIBLE;
import static com.info.movies.constants.Common.basic_geners;
import static com.info.movies.constants.Common.getGenreCode;
import static com.info.movies.constants.Common.getGenreString;
import static com.info.movies.constants.Common.getGenresNamesList;
import static com.info.movies.fargments.nowplaying.NowPlayingInteractorImp.movies_filter;


/**
 * A simple {@link Fragment} subclass.
 */
public class NowPlayingFragment extends Fragment implements NowPlayingView, SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = NowPlayingFragment.class.getSimpleName();
    public static final String SAVED_TOTAL_PAGES_NOWPLAYING = "total_pages";
    public static final String SAVED_MOVIES_BUNDLE_NOWPLAYING = "bundle_tag"; // ************** BUNDLE TAG
    public static final String SAVED_MOVIES_ARRAY_NOWPLAYING = "array_tag"; // ************** BUNDLE TAG
    public static final String SAVED_MOVIES_PAGE_NO_NOWPLAYING = "page_no";
    protected static final String SAVED_NO_LAYOUT = "no_layout";

    @BindView(R.id.nowPlaying_fragment_progressBar)
    ProgressBar progressBar;
    @BindView(R.id.nowPlaying_fragment_RecyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.nowPlaying_fragment_no_internet_layout)
    LinearLayout noInternetLayout;

    @BindView(R.id.nowPlaying_fragment_swipeRefreshLayout)
    SwipeRefreshLayout mRecyclerViewLayout;
    @BindView(R.id.now_playing_fragment_no_movies_layout)
    LinearLayout noResultsFoundLayout;

    private RecyclerView.LayoutManager layoutManager;
    protected NowPlayingRecyclerViewAdapter mAdapter;
    private NowPlayingPresenter mNowPlayingPresenter;

    ArrayList<MoviePosterAR> newMovies;


    private Bundle savedState = null; // ************** BUNDLE
    boolean createdStateInDestroyView; // // ************** to detect  when save occures
    public static int page_counter = 1;

    int curSize;
    public int total_pages;
    protected static String before_month_date;
    protected static String current_date;
    private Setting setting;
    private int dialog_saved_genre;
    private boolean isOnRefesh;
    private Unbinder unbinder;

    public View v;
    protected static boolean isOnLoadMore;
    protected static int nowplaying_genre;
    private boolean isDoGenre;
    protected static int no_visible_layout;
    Activity activity;
    private OnNowPlayingFragmentInteractionListener mListener;

    public NowPlayingFragment() {
        Log.d(TAG, "CONSTRUCTOR");
        mNowPlayingPresenter = new NowPlayingPresenterImp(this/*,getContext()*/);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnNowPlayingFragmentInteractionListener) {
            mListener = (OnNowPlayingFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnAboutFragmentInteractionListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_now_playing, container, false);
        // ViewCompat.setNestedScrollingEnabled(v, false);
        unbinder = ButterKnife.bind(this, v);
        this.v = v;
        newMovies = new ArrayList<>();
        initToolbar();
        mRecyclerViewLayout.setOnRefreshListener(this);
        recyclerViewSetup();
        if (savedState == null) {
            Log.d(TAG, "onCreateView:   startup");
            mNowPlayingPresenter.onCreateView(page_counter, before_month_date, current_date, nowplaying_genre);
        } else {
            Log.d(TAG, "onCreateView:  NOT startup ");
            mNowPlayingPresenter.onRecreateView(savedState, page_counter, before_month_date, current_date, nowplaying_genre);
        }
        return v;
    }

    private void initToolbar() {
        Toolbar toolbar = activity.findViewById(R.id.toolbar);
        SearchView mSearchView = toolbar.findViewById(R.id.mySearchView);
        mSearchView.setVisibility(View.GONE); // hide the search view
        TextView title = toolbar.findViewById(R.id.toolbar_title);
        if (!title.getText().equals(getResources().getString(R.string.nowPlayingMovies))) {
            title.setText(getResources().getString(R.string.nowPlayingMovies));
        }
        title.setVisibility(View.VISIBLE);
        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
        params.setScrollFlags(SCROLL_FLAG_SCROLL | SCROLL_FLAG_ENTER_ALWAYS | SCROLL_FLAG_SNAP);  // set scroll flags
    }

    private void recyclerViewSetup() {
        layoutManager = new GridLayoutManager(activity, activity.getResources().getInteger(R.integer.number_of_cols));
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        mAdapter = new NowPlayingRecyclerViewAdapter(newMovies, activity, mNowPlayingPresenter);
        recyclerView.setAdapter(mAdapter);
        recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener((GridLayoutManager) layoutManager/*,up_fab*/) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                Log.d(TAG, "onLoadMore: 1 ");
                if (!isOnLoadMore) {
                    Log.d(TAG, "onLoadMore: 2 ");
                    if (page_counter <= total_pages) {
                        Log.d(TAG, "onLoadMore: 3 ");

                        mNowPlayingPresenter.onLoadMore(page_counter, before_month_date, current_date, nowplaying_genre);
                        mAdapter.showFooter();
                        isOnLoadMore = false;
                        resetFlags();
                        isOnLoadMore = true;
                        mAdapter.notifyDataSetChanged();
                    } else {
                        Log.d(TAG, "onLoadMore: 4 ");
                        mAdapter.hideFooter();
                        mAdapter.notifyDataSetChanged();
                    }
                } else {
                    Log.d(TAG, "onLoadMore: 5 ");
                }
            }
        });
    }

    @Override
    public void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void showNowPlayinLayout() {
        mRecyclerViewLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideNowPlayingRecyclerViewLayout() {
        mRecyclerViewLayout.setVisibility(View.GONE);
    }

    @Override
    public void showRecyclerView() {
        recyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideRecyclerView() {
        recyclerView.setVisibility(View.GONE);
    }


    @Override
    public void recieveNowPlayingMovies(final ArrayList<MoviePosterAR> nowPlayingMovies, int counter, int total_pages) {
        Log.d(TAG, "recieveNowPlayingMovies: nowPlayingMovies.size() 1 = " + nowPlayingMovies.size());
        if (counter == 0) // data from server
        {
            Log.d(TAG, "recieveNowPlayingMovies: 1");
            this.total_pages = total_pages;
            if (!nowPlayingMovies.isEmpty() && nowPlayingMovies.size() != 0) {
                Log.d(TAG, "recieveNowPlayingMovies 2");
                if (isOnRefesh) // recieved data when user refreshed layout
                {
                    Log.d(TAG, "recieveNowPlayingMovies 3");
                    page_counter = 1;
                    newMovies.clear();
                    newMovies.addAll(nowPlayingMovies);
                    this.total_pages = total_pages;
                    page_counter++;
                    recyclerViewSetup();
                    isOnRefesh = false;
                } else if (isDoGenre) {
                    Log.d(TAG, "recieveNowPlayingMovies 3.1");
                    newMovies.clear();
                    page_counter = 1;
                    newMovies.addAll(nowPlayingMovies);
                    this.total_pages = total_pages;
                    page_counter++;
                    recyclerViewSetup();
                    isDoGenre = false;
                } else if (isOnLoadMore) {
                    Log.d(TAG, "recieveNowPlayingMovies 3.2");
                    newMovies.addAll(nowPlayingMovies);
                    mAdapter.notifyDataSetChanged();
                    page_counter++;
                    mAdapter.hideFooter();
                    mAdapter.notifyDataSetChanged();
                    isOnLoadMore = false;
                } else // normal server recieving
                {
                    Log.d(TAG, "recieveNowPlayingMovies 4");
                    newMovies.addAll(nowPlayingMovies);
                    mAdapter.notifyDataSetChanged();
                    page_counter++;
                    isOnLoadMore = false;
                    resetFlags();
                }
            } else // empty list
            {
                Log.d(TAG, "recieveNowPlayingMovies 5");
            }
        } else //recieve saved data
        {
            Log.d(TAG, "recieveNowPlayingMovies 8");
            this.total_pages = total_pages;
            page_counter = counter;
            newMovies.addAll(nowPlayingMovies);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void showErrorMessage(String message) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showNoInternetLayout() {
        if (noInternetLayout.getVisibility() == View.GONE) {
            noInternetLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void hideNoInternetLayout() {
        if (noInternetLayout.getVisibility() == View.VISIBLE) {
            noInternetLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void hideRefreshProgressAnimation() {
        mRecyclerViewLayout.setRefreshing(false);
    }

    @Override
    public void showRefreshProgressAnimation() {
        mRecyclerViewLayout.setRefreshing(true);
    }


    /* @Override
     public void showNotice(boolean isConnected) {
         String message = "Check internet connection";
         if(!isConnected)
         {
             new Common().showSnackAlert(ButterKnife.findById(activity,R.id.main_activity_layout),message);
             savedState = null;
         }
     }
 */
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "onSaveInstanceState: : 1");

        if (newMovies == null) {
            Log.d(TAG, "onSaveInstanceState: : 2");

            outState.putBundle(SAVED_MOVIES_BUNDLE_NOWPLAYING, savedState); // ************** put saved bundle by onDestroyView here.
        } else {
            Log.d(TAG, "onSaveInstanceState: 3");
            outState.putBundle(SAVED_MOVIES_BUNDLE_NOWPLAYING, createdStateInDestroyView ? savedState : saveState()); // ************** if list is not null (= screen not rotated & onDestroyview is not reached, then save from here)
        }
        createdStateInDestroyView = false;
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView: 1");
        unbinder.unbind();
        v = null;
        savedState = saveState(); // ************** BUT SAVES BUNDLE in savedState
        createdStateInDestroyView = true; // ************** flag that something saves
        newMovies = null; // ************** clear
        page_counter = 1; // ************** clear
        Log.d(TAG, "onDestroyView: 3");

    }

    private Bundle saveState() // ************** SAVE
    {
        Log.d(TAG, "saveState 1 ");

        Bundle state = new Bundle();
        state.putParcelableArrayList(SAVED_MOVIES_ARRAY_NOWPLAYING, newMovies);
        state.putInt(SAVED_MOVIES_PAGE_NO_NOWPLAYING, page_counter);
        state.putInt(SAVED_TOTAL_PAGES_NOWPLAYING, total_pages);
        state.putInt(SAVED_NO_LAYOUT, no_visible_layout); //  3 is no results, 1 is invalid, 2 no internet layout
        state.putIntegerArrayList(NowPlayingInteractorImp.SAVED_MOVIES_FILTER, movies_filter);
        Log.d(TAG, "saveState: layout =  " + state.getInt(SAVED_NO_LAYOUT));
        return state;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate 1");
        activity = getActivity();
        if (savedInstanceState != null) {
            Log.d(TAG, "onCreate 2");
            savedState = savedInstanceState.getBundle(SAVED_MOVIES_BUNDLE_NOWPLAYING);
        }
        setting = new Setting(activity);
        setHasOptionsMenu(true);
        current_date = Common.getCurrentDateSF();
        before_month_date = Common.getDateBeforeMonthSF();
        checkSavedNowPlayingingGenre();
    }

    private void checkSavedNowPlayingingGenre() {
        nowplaying_genre = setting.getNowPlayingGenre();
        if (nowplaying_genre != 0) {
            dialog_saved_genre = getSavedGenrePosition(getGenreString(nowplaying_genre)) + 1;
        } else {
            dialog_saved_genre = 0;
        }
    }

    private int getSavedGenrePosition(String genreString) {
        for (int i = 0; i < basic_geners.size(); i++) {
            if (basic_geners.get(i).getName().equals(genreString)) {
                return i;
            }
        }
        return 0;
    }

    @OnClick(R.id.no_internet_fragment_button_retry)
    public void retryConnection(View view) {
        if (Utils.isNetworkAvailable(getContext())) {
            mNowPlayingPresenter.onCreateView(page_counter, before_month_date, current_date, nowplaying_genre);
        }
    }

    @Override
    public void onRefresh() {
        // fetch movies from server .
        isOnLoadMore = false;
        resetFlags();
        isOnRefesh = true;
        mNowPlayingPresenter.onRefresh(1, before_month_date, current_date, nowplaying_genre);
    }

    @Override
    public void onPause() {
        super.onPause();
        mNowPlayingPresenter.onPause();
    }


    @Override
    public void onResume() {
        super.onResume();
        mListener.onNowPlayingFragmentOpened();
        if (setting.getPosterInfoView()) {
            mAdapter.showInfo();
        } else mAdapter.hideInfo();
        if (noInternetLayout.getVisibility() != View.VISIBLE &&
                recyclerView.getVisibility() != View.VISIBLE &&
                mRecyclerViewLayout.getVisibility() != View.VISIBLE &&
                progressBar.getVisibility() != View.VISIBLE) {
            Log.d(TAG, "onResume: 2");
        }

        if (progressBar.getVisibility() != View.VISIBLE &&
                recyclerView.getVisibility() != View.VISIBLE &&
                mRecyclerViewLayout.getVisibility() != View.VISIBLE && noResultsFoundLayout.getVisibility() != VISIBLE) {
            // when user open app then before it launch screen was locked, then app will do no connections due to onDestroyView();,
            // So when user re unlock screen app will appear, and frist method which will be invoked was onResume + progress,
            // list and layout are not visible, then from this point, we will make a new request in this case.
            Log.d(TAG, "onResume: 3");
            if (savedState == null) {
                Log.d(TAG, "onResume:  startup");
                mNowPlayingPresenter.onCreateView(page_counter, before_month_date, current_date, nowplaying_genre);
            } else {
                Log.d(TAG, "onResume: NOT startup ");
                mNowPlayingPresenter.onRecreateView(savedState, page_counter, before_month_date, current_date, nowplaying_genre);
            }
        }
        if (isOnLoadMore && recyclerView.getVisibility() == VISIBLE) {
            Log.d(TAG, "onResume: Fragment 4 ");
            recyclerViewSetup();
            isOnLoadMore = false;
            layoutManager.scrollToPosition(newMovies.size() - 5);
        }

        if (recyclerView.getVisibility() == VISIBLE && newMovies != null && !newMovies.isEmpty()) {
            Log.d(TAG, "onResume:   5");
            if (newMovies.get(0) != null) {
                Log.d(TAG, "onResume:   6");

                if (newMovies.get(0).getMovie_id() == null || TextUtils.isEmpty(newMovies.get(0).getMovie_id()) || newMovies.get(0).getTitle() == null || TextUtils.isEmpty(newMovies.get(0).getTitle().trim())) { // el trim d b t throw null pointer wxception
                    Log.d(TAG, "onResume: 7 : Recyclerview data are missing");
                    newMovies.clear();
                    page_counter = 1;
                    mNowPlayingPresenter.onCreateView(page_counter, before_month_date, current_date, nowplaying_genre);
                }
            }
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem item_sort = menu.findItem(R.id.action_sort).setVisible(false);
        MenuItem item_select_date = menu.findItem(R.id.action_date_select).setVisible(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_genre:
                CreateAlertDialogWithRadioButtonGroup();
                return true;
            case R.id.action_search:
                moveToSearch();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void moveToSearch() {
        getActivity().getSupportFragmentManager().
                beginTransaction().
                replace(R.id.main_container, new SearchFragment(), getString(R.string.search_fragment_tag)).
                addToBackStack(null).
                commit();
    }

    private void CreateAlertDialogWithRadioButtonGroup() {

        final ArrayList<String> genres = new ArrayList<>();
        genres.add("All");
        genres.addAll(getGenresNamesList());

        final CharSequence[] values = genres.toArray(new CharSequence[genres.size()]);

        AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.MaterialThemeDialog);

        builder.setTitle("Genre");
        builder.setSingleChoiceItems(values, dialog_saved_genre, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {

                if (item != 0) {
                    nowplaying_genre = getGenreCode(genres.get(item));
                    dialog_saved_genre = item;
                    setting.saveNowPlayingGenre(nowplaying_genre);
                    if (noInternetLayout.getVisibility() != VISIBLE) {
                        isOnLoadMore = false;
                        resetFlags();
                        isDoGenre = true;
                        mNowPlayingPresenter.onDateSet(1, current_date, before_month_date, nowplaying_genre);
                    }
                } else {
                    dialog_saved_genre = 0;
                    nowplaying_genre = 0;
                    setting.saveNowPlayingGenre(0);
                    if (noInternetLayout.getVisibility() != VISIBLE) {
                        isOnLoadMore = false;
                        resetFlags();
                        isDoGenre = true;
                        mNowPlayingPresenter.onDateSet(1, current_date, before_month_date, nowplaying_genre);
                    }
                }
                dialog.dismiss();
            }
        });
        AlertDialog dialog_genre = builder.create();
        dialog_genre.show();
    }


    public boolean isThereDataAvailable() {
        return newMovies.size() > 0;
    }

    @Override
    public void showNoInternetSnak() {
        final Snackbar snack = Snackbar.make(recyclerView, R.string.no_internet_snack_message, 10000);
        snack.setAction("ok", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snack.dismiss();
            }
        });
        snack.show();
    }


    @Override
    public void onLoadMoreFail() {
        mAdapter.hideFooter();
        mAdapter.notifyDataSetChanged();
        isOnLoadMore = false;
    }

    @Override
    public void resetFlags() {
        isDoGenre = false;
        isOnRefesh = false;
    }

    @Override
    public void hideNoResultsLayout() {
        noResultsFoundLayout.setVisibility(View.GONE);
    }


    @Override
    public void showNoResultsLayout() {
        noResultsFoundLayout.setVisibility(VISIBLE);
    }

    @Override
    public void showSortProgress() {
        if (mRecyclerViewLayout.getVisibility() == VISIBLE) {
            showRefreshProgressAnimation();
        } else {
            showProgress();
        }
    }

    @Override
    public void moveToMovieFragment(String movie_id, String title) {
        MovieFragment mMovieFragment = new MovieFragment();
        Bundle bundle = new Bundle();
        bundle.putString("movie_id_key", movie_id);
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

    public interface OnNowPlayingFragmentInteractionListener {
        // TODO: Update argument type and name
        void onNowPlayingFragmentOpened();
    }
}
