package com.info.movies.fargments.upcoming;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
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
import com.info.movies.adapters.UpcomingRecyclerViewAdapter;
import com.info.movies.constants.Common;
import com.info.movies.constants.EndlessRecyclerViewScrollListener;
import com.info.movies.constants.Setting;
import com.info.movies.constants.Utils;
import com.info.movies.fargments.search.SearchFragment;
import com.info.movies.fargments.upcoming.DatePick.DateSettings;
import com.info.movies.models.posters.TopRatedMovie;
import com.info.movies.fargments.movie.MovieFragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static android.support.design.widget.AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS;
import static android.support.design.widget.AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL;
import static android.support.design.widget.AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP;
import static android.view.View.VISIBLE;
import static com.info.movies.fargments.upcoming.UpComingInteractorImp.movies_filter;


/**
 * A simple {@link Fragment} subclass.
 */
public class UpcomingFragment extends Fragment implements UpComingView,
        SwipeRefreshLayout.OnRefreshListener {
    protected static final String SAVED_TOTAL_PAGES_UPCOMING = "total_pages";
    protected static final String SAVED_MOVIES_BUNDLE_UPCOMING = "bundle_tag";
    protected static final String SAVED_MOVIES_ARRAY_UPCOMING = "array_tag";
    protected static final String SAVED_MOVIES_PAGE_NO_UPCOMING = "page_no";
    protected static final String SAVED_NO_LAYOUT = "no_layout";
    private static final String TAG = UpcomingFragment.class.getSimpleName();

    @BindView(R.id.upcoming_fragment_progressBar)
    ProgressBar progressBar;

    @BindView(R.id.upcoming_fragment_invalid_date_layout)
    LinearLayout inValidDate_layout;

    @BindView(R.id.upcoming_fragment_RecyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.upcoming_fragment_no_internet_layout)
    LinearLayout noInternetLayout;

    @BindView(R.id.upcoming_fragment_swipeRefreshLayout)
    SwipeRefreshLayout mRecyclerViewLayout;

    @BindView(R.id.upcoming_fragment_no_movies_layout)
    LinearLayout noResultsFoundLayout;

    private RecyclerView.LayoutManager layoutManager;
    private static UpcomingRecyclerViewAdapter mAdapter;
    private static UpcommingPresenter mUpcommingPresenter;
    protected static ArrayList<TopRatedMovie> newMovies;
    private Bundle savedState = null;
    boolean createdStateInDestroyView;
    public static int page_counter = 1;
    protected static int total_pages;
    private Setting setting;
    private String sort;
    protected static String tomorrowDate; // minimum release date
    public static String maximumDate; // maximum release date
    private int saved_genre;
    public static boolean isDateChange;
    private boolean isOnRefesh;
    private boolean isDoGenreSort;
    private Unbinder unbinder;
    public View v;
    protected static boolean isOnLoadMore;
    protected static int upcoming_genre;
    AlertDialog alertDialog1 = null;
    protected static int no_visible_layout;
    private OnUpcomingFragmentInteractionListener mListener;

    public UpcomingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnUpcomingFragmentInteractionListener) {
            mListener = (OnUpcomingFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnAboutFragmentInteractionListener");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d(TAG, "onCreateView: main 1");

        View v = inflater.inflate(R.layout.fragment_upcoming, container, false);
        unbinder = ButterKnife.bind(this, v);
        this.v = v;

        Log.d(TAG, "onCreateView: main 2");
        initToolbar();
        mRecyclerViewLayout.setOnRefreshListener(this);
        newMovies = new ArrayList<>(); // initialize it here 3shan lma baroo7 fragment tanya w arga3 gomovies awl method bttenvoke onCreateView();

        recyclerViewSetup();
        if (savedState == null) {
            Log.d(TAG, "onCreateView: main 3");
            mUpcommingPresenter.onCreateView(tomorrowDate, maximumDate, page_counter, upcoming_genre);
        } else {
            Log.d(TAG, "onCreateView: main 4");
            mUpcommingPresenter.onReCreateView(savedState, tomorrowDate, maximumDate, page_counter, upcoming_genre);
        }
        return v;
    }

    @Override
    public void onPause() {
        super.onPause();
        mUpcommingPresenter.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mListener.onUpcomingFragmentOpened();

        Log.d(TAG, "onResume: Upcoming Fragment  1");
        if (progressBar.getVisibility() != View.VISIBLE &&
                recyclerView.getVisibility() != View.VISIBLE &&
                mRecyclerViewLayout.getVisibility() != View.VISIBLE && noResultsFoundLayout.getVisibility() != VISIBLE) {
            // when user open app then before it launch screen was locked, then app will do no connections due to onDestroyView();,
            // So when user re unlock screen app will appear, and frist method which will be invoked was onResume + progress,
            // list and layout are not visible, then from this point, we will make a new request in this case.
            Log.d(TAG, "onResume: 2");
            if (savedState == null) {
                Log.d(TAG, "onResume: startup 3");
                mUpcommingPresenter.onCreateView(tomorrowDate, maximumDate, page_counter, upcoming_genre);
            } else {
                Log.d(TAG, "onResume: not startup 4");
                mUpcommingPresenter.onReCreateView(savedState, tomorrowDate, maximumDate, page_counter, upcoming_genre);
            }
        }
        if (isOnLoadMore && recyclerView.getVisibility() == VISIBLE) {
            Log.d(TAG, "onResume: Upcoming Fragment 5 ");
            recyclerViewSetup();
            isOnLoadMore = false;
            layoutManager.scrollToPosition(newMovies.size() - 5);
        }
        if (recyclerView.getVisibility() == VISIBLE && newMovies != null && !newMovies.isEmpty()) {
            Log.d(TAG, "onResume: TopRated Fragment 6");
            if (newMovies.get(0) != null) {
                Log.d(TAG, "onResume: TopRated Fragment 7");
                if (newMovies.get(0).getOverView() == null || newMovies.get(0).getOverView().isEmpty() || newMovies.get(0).getOverView().trim().equals("")) {
                    Log.d(TAG, "onResume: 8 : Recyclerview data are missing, IMP");
                    newMovies.clear();
                    page_counter = 1;
                    mUpcommingPresenter.onCreateView(tomorrowDate, maximumDate, page_counter, upcoming_genre);
                }
            }
        }
    }

    private void initToolbar() {
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        SearchView mSearchView = toolbar.findViewById(R.id.mySearchView);
        mSearchView.setVisibility(View.GONE); // hide the search view
        TextView title = toolbar.findViewById(R.id.toolbar_title);
        if (!title.getText().equals(getResources().getString(R.string.upComingMovies))) {
            title.setText(getResources().getString(R.string.upComingMovies));
        }
        title.setVisibility(View.VISIBLE);
        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
        params.setScrollFlags(SCROLL_FLAG_SCROLL | SCROLL_FLAG_ENTER_ALWAYS | SCROLL_FLAG_SNAP);  // set scroll flags
    }

    private void recyclerViewSetup() {
        layoutManager = new GridLayoutManager(getActivity(), getResources().getInteger(R.integer.top_rated_number_of_cols));
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        mAdapter = new UpcomingRecyclerViewAdapter(newMovies, getActivity(), mUpcommingPresenter);
        recyclerView.setAdapter(mAdapter);
        recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener((GridLayoutManager) layoutManager/*,up_fab*/) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                Log.d(TAG, "onLoadMore: 1 ");
                if (!isOnLoadMore) {
                    Log.d(TAG, "onLoadMore: 2 ");
                    if (page_counter <= total_pages) {
                        Log.d(TAG, "onLoadMore: 3 ");
                        mAdapter.showFooter();
                        isOnLoadMore = false;
                        resetFlags();
                        isOnLoadMore = true;
                        mUpcommingPresenter.onLoadMore(tomorrowDate, maximumDate, page_counter, upcoming_genre);
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
    public void showRecyclerViewLayout() {
        mRecyclerViewLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideRecyclerViewLayout() {
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
    public void showNoResultsLayout() {
        noResultsFoundLayout.setVisibility(VISIBLE);
    }

    @Override
    public void hideNoResultsLayout() {
        noResultsFoundLayout.setVisibility(View.GONE);
    }

    @Override
    public void receiveUpcomingMovies(ArrayList<TopRatedMovie> upcomingMovies, int page_counter, final int total_pages) {
        Log.d(TAG, "receiveUpcomingMovies: 0 ,pages= " + total_pages + " counter= " + UpcomingFragment.page_counter + " list size=" + upcomingMovies.size());

        Log.d(TAG, "receiveUpcomingMovies: 1");
        if (page_counter == 0) // data from server
        {
            Log.d(TAG, "receiveUpcomingMovies: 3");
            UpcomingFragment.total_pages = total_pages;
            if (!upcomingMovies.isEmpty() && upcomingMovies.size() != 0) {
                Log.d(TAG, "receiveUpcomingMovies: 4");
                if (isOnRefesh) // recieved data when user refreshed layout
                {
                    Log.d(TAG, "receiveUpcomingMovies: 5");
                    UpcomingFragment.page_counter = 1;
                    newMovies.clear();
                    mAdapter.notifyDataSetChanged();
                    newMovies.addAll(upcomingMovies);
                    UpcomingFragment.total_pages = total_pages;
                    UpcomingFragment.page_counter++;
                    recyclerViewSetup();
                    isOnRefesh = false;
                } else if (isDoGenreSort) {
                    Log.d(TAG, "receiveUpcomingMovies: 5.5");
                    UpcomingFragment.page_counter = 1;
                    newMovies.clear();
                    mAdapter.notifyDataSetChanged();
                    newMovies.addAll(upcomingMovies);
                    UpcomingFragment.total_pages = total_pages;
                    UpcomingFragment.page_counter++;
                    recyclerViewSetup();
                    isDoGenreSort = false;
                } else if (isOnLoadMore) {
                    Log.d(TAG, "recieveNowPlayingMovies 3.2");
                    newMovies.addAll(upcomingMovies);
                    mAdapter.notifyDataSetChanged();
                    UpcomingFragment.page_counter++;
                    mAdapter.hideFooter();
                    mAdapter.notifyDataSetChanged();
                    isOnLoadMore = false;
                    UpcomingFragment.total_pages = total_pages;
                } else if (isDateChange) {
                    Log.d(TAG, "receiveUpcomingMovies: 2");
                    clearData();
                    recyclerViewSetup();
                    newMovies.addAll(upcomingMovies);
                    mAdapter.notifyDataSetChanged();
                    UpcomingFragment.page_counter++;
                    isDateChange = false;
                    UpcomingFragment.total_pages = total_pages;

                } else // normal server recieving
                {
                    Log.d(TAG, "receiveUpcomingMovies: 7");
                    newMovies.addAll(upcomingMovies);
                    mAdapter.notifyDataSetChanged();
                    UpcomingFragment.page_counter++;
                    UpcomingFragment.total_pages = total_pages;
                    isOnLoadMore = false;
                    resetFlags();
                }
            } else {
                Log.d(TAG, "receiveUpcomingMovies: 8");
                if (isOnLoadMore) {
                    Log.d(TAG, "receiveUpcomingMovies: 9");
                    mAdapter.hideFooter();
                    mAdapter.notifyDataSetChanged();
                    resetFlags();
                } else {
                    Log.d(TAG, "receiveUpcomingMovies: 10");
                    newMovies.clear();
                    mAdapter.notifyDataSetChanged();
                }
            }
        } else //recieve saved data
        {
            Log.d(TAG, "receiveUpcomingMovies: 11");
            UpcomingFragment.total_pages = total_pages;
            UpcomingFragment.page_counter = page_counter;
            newMovies.addAll(upcomingMovies);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void showErrorMessage(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
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


    @Override
    public void showInValidDateSelectedLayout() {
        inValidDate_layout.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideInValidDateSelectedLayout() {
        inValidDate_layout.setVisibility(View.GONE);
    }

    @Override
    public boolean noInternetLayoutVisibile() {
        return noInternetLayout.getVisibility() == View.VISIBLE;
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
    public void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "onSaveInstanceState: upcoming: 1");

        if (newMovies == null) {
            Log.d(TAG, "onSaveInstanceState: upcoming: 2");
            outState.putBundle(SAVED_MOVIES_BUNDLE_UPCOMING, savedState);
        } else {
            Log.d(TAG, "onSaveInstanceState: upcoming: 3");
            outState.putBundle(SAVED_MOVIES_BUNDLE_UPCOMING, createdStateInDestroyView ? savedState : saveState());
        }
        createdStateInDestroyView = false;
        super.onSaveInstanceState(outState);
    }

    private Bundle saveState() {
        Bundle state = new Bundle();
        state.putParcelableArrayList(SAVED_MOVIES_ARRAY_UPCOMING, newMovies);
        state.putInt(SAVED_MOVIES_PAGE_NO_UPCOMING, UpcomingFragment.page_counter);
        state.putInt(SAVED_TOTAL_PAGES_UPCOMING, total_pages);
        state.putInt(SAVED_NO_LAYOUT, no_visible_layout); //  3 is no results, 1 is invalid, 2 no internet layout
        state.putIntegerArrayList(UpComingInteractorImp.SAVED_MOVIES_FILTER, movies_filter);
        Log.d(TAG, "saveState: 2 list size = " + state.getParcelableArrayList(SAVED_MOVIES_ARRAY_UPCOMING).size());
        return state;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        savedState = saveState();
        createdStateInDestroyView = true;
        newMovies = null;
        page_counter = 1;
        this.v = null;
        Log.d(TAG, "onDestroyView Upcoming: savedState = " + savedState);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate Upcoming : 1");
        mUpcommingPresenter = new UpcomingPresenterImp(this);
        this.setting = new Setting(getActivity());
        if (savedInstanceState != null) {
            Log.d(TAG, "onCreate Upcoming : 2");
            savedState = savedInstanceState.getBundle(SAVED_MOVIES_BUNDLE_UPCOMING);
        }
        setHasOptionsMenu(true);

        checkSavedDate();
        checkSavedUpcomingGenre();
    }

    private void checkSavedUpcomingGenre() {
        upcoming_genre = setting.getUpComingGnre();
        if (upcoming_genre != 0) {
            saved_genre = getSavedGenrePosition(Common.getGenreString(upcoming_genre)) + 1;
        } else {
            saved_genre = 0;
        }
    }

    private int getSavedGenrePosition(String genreString) {
        for (int i = 0; i < Common.basic_geners.size(); i++) {
            if (Common.basic_geners.get(i).getName().equals(genreString)) {
                return i;
            }
        }
        return 0;
    }

    private void checkSavedDate() {

        // check if date saved after today's date or not, if not set it, after week, else let it.
        Log.d(TAG, "checkSavedDate: Maximm date saved " + setting.getMaximumDateSF());
        try {
            Log.d(TAG, "checkSavedDate: 1");
            Log.d(TAG, "checkSavedDate: 1.5: date saved =  " + setting.getMaximumDateSF());

            Date m = Common.sDF.parse(this.setting.getMaximumDateSF());
            Date t = Common.sDF.parse(Common.getTomorrowDateSF());
            if (t.before(m) || t.equals(m)) {
                Log.d(TAG, "checkSavedDate: 2");

                maximumDate = this.setting.getMaximumDateSF();

            } else {
                Log.d(TAG, "checkSavedDate: 3");

                maximumDate = Common.getDateAfterWeekSF();
                setting.saveMaximumDateSF(maximumDate);
            }
            tomorrowDate = Common.getTomorrowDateSF();
        } catch (ParseException e) {
            e.printStackTrace();
            Log.d(TAG, "checkSavedDate: 4");

        }

    }


    @OnClick(R.id.no_internet_fragment_button_retry)
    public void retryConnection(View view) {
        if (Utils.isNetworkAvailable(getContext())) {
            mUpcommingPresenter.onCreateView(tomorrowDate, maximumDate, page_counter, upcoming_genre);
        }
    }

    @OnClick(R.id.invalid_fragment_button_reselect_date)
    public void reSelectMaxReleaseDate(View view) {
        showDatePicker();
    }

    @Override
    public void onRefresh() {
        isOnLoadMore = false;
        resetFlags();
        isOnRefesh = true;
        mUpcommingPresenter.onRefresh(tomorrowDate, maximumDate, 1, upcoming_genre);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem item_sort = menu.findItem(R.id.action_sort).setVisible(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_genre:
                CreateAlertDialogWithRadioButtonGroup();
                return true;

            case R.id.action_date_select:
                showDatePicker();
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
                //setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out,android.R.anim.fade_in, android.R.anim.fade_out).
                        replace(R.id.main_container, new SearchFragment(), getString(R.string.search_fragment_tag)).
                addToBackStack(null).
                commit();
    }


    private void CreateAlertDialogWithRadioButtonGroup() {
        final ArrayList<String> genres = new ArrayList<>();
        genres.add("All");
        genres.addAll(Common.getGenresNamesList());

        final CharSequence[] values = genres.toArray(new CharSequence[genres.size()]);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.MaterialThemeDialog);

        builder.setTitle("Genre");
        builder.setSingleChoiceItems(values, saved_genre, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {
                if (item != 0) {
                    upcoming_genre = Common.getGenreCode(genres.get(item));
                    saved_genre = item;
                    setting.saveUpComingGnre(upcoming_genre);
                    if (!noInternetLayoutVisibile()) {
                        isOnLoadMore = false;
                        resetFlags();
                        isDoGenreSort = true;
                        mUpcommingPresenter.onDateSet(tomorrowDate, maximumDate, 1, upcoming_genre);
                    }
                } else {
                    saved_genre = 0;
                    upcoming_genre = 0;
                    setting.saveUpComingGnre(0);
                    if (!noInternetLayoutVisibile()) {
                        isOnLoadMore = false;
                        resetFlags();
                        isDoGenreSort = true;
                        mUpcommingPresenter.onDateSet(tomorrowDate, maximumDate, 1, 0);
                    }
                }
                dialog.dismiss();
            }
        });
        alertDialog1 = builder.create();
        alertDialog1.show();
    }

    @Override
    public void showSortProgress() {
        if (mRecyclerViewLayout.getVisibility() == VISIBLE) {
            showRefreshProgressAnimation();
        } else {
            showProgress();
        }
    }


    public boolean isThereDataAvailable() {
        return newMovies.size() > 0;
    }


    public static class DatePickerDialogFragmentHandler extends DialogFragment {

        SimpleDateFormat dayFormat = new SimpleDateFormat("dd");
        SimpleDateFormat monthFormat = new SimpleDateFormat("MM");
        SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
        int day, month, year;

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            DatePickerDialog datePickerDialog;
            Context context = getActivity();
            Setting setting = new Setting(context);
            String max_date = setting.getMaximumDateSF();
            try {
                Date d = Common.sDF.parse(max_date);
                day = Integer.parseInt(dayFormat.format(d));
                month = Integer.parseInt(monthFormat.format(d));
                year = Integer.parseInt(yearFormat.format(d));
                DateSettings dateSettings = new DateSettings(context, mUpcommingPresenter, tomorrowDate);
                datePickerDialog = new DatePickerDialog(context, R.style.DialogTheme, dateSettings, year, month - 1, day);
                return datePickerDialog;

            } catch (ParseException e) {
                e.printStackTrace();
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                DateSettings dateSettings = new DateSettings(context, mUpcommingPresenter, tomorrowDate);
                datePickerDialog = new DatePickerDialog(context, dateSettings, year, month, day);
                return datePickerDialog;
            }
        }
    }

    public static void clearData() {
        newMovies.clear();
        UpcomingFragment.page_counter = 1;
    }

    private void showDatePicker() {
        DatePickerDialogFragmentHandler datePickerDialogFragmentHandler = new DatePickerDialogFragmentHandler();
        datePickerDialogFragmentHandler.show(getFragmentManager(), "DATE PICKER");
    }

    @Override
    public void onLoadMoreFail() {
        mAdapter.hideFooter();
        mAdapter.notifyDataSetChanged();
        isOnLoadMore = false;
    }

    @Override
    public void resetFlags() {
        isDoGenreSort = false;
        isOnRefesh = false;
        isDateChange = false;
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
                //setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right).
                //    setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out,android.R.anim.fade_in, android.R.anim.fade_out).
                        replace(R.id.main_container, mMovieFragment).
                addToBackStack(null).
                commit();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnUpcomingFragmentInteractionListener {
        // TODO: Update argument type and name
        void onUpcomingFragmentOpened();
    }

}

