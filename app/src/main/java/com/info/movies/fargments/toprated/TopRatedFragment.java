package com.info.movies.fargments.toprated;


import android.app.Activity;
import android.content.Context;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.info.movies.R;
import com.info.movies.constants.EndlessRecyclerViewScrollListener;
import com.info.movies.constants.Utils;
import com.info.movies.fargments.movie.MovieFragment;
import com.info.movies.adapters.TopRatedRecyclerViewAdapter;
import com.info.movies.constants.Setting;
import com.info.movies.fargments.search.SearchFragment;
import com.info.movies.models.posters.TopRatedMovie;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static android.support.design.widget.AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS;
import static android.support.design.widget.AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL;
import static android.support.design.widget.AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP;
import static android.view.View.VISIBLE;
import static com.info.movies.constants.Common.getGenreCode;


/**
 * A simple {@link Fragment} subclass.
 */
public class TopRatedFragment extends Fragment implements TopRatedView, SwipeRefreshLayout.OnRefreshListener {

    public static final String SAVED_TOTAL_PAGES_TOPRATED = "total_pages";
    public static final String SAVED_MOVIES_BUNDLE = "bundle_tag";
    public static final String SAVED_MOVIES_ARRAY_TOPRATED = "array_tag";
    public static final String SAVED_MOVIES_PAGE_NO_TOPRATED = "page_no";
    public static final String SAVED_CURRENT_GENRE ="current_genre" ;
    public static final String SAVED_CURRENT_YEAR ="current_year" ;
    protected static final String SAVED_NO_LAYOUT ="layout_no" ;
    private static final String TAG = TopRatedFragment.class.getSimpleName();

    @BindView(R.id.top_rated_year_spinner_id_header)
    Spinner spinner_year ;
    @BindView(R.id.top_rated_genre_spinner_id_header)
    Spinner spinner_genres ;
    @BindView(R.id.top_rated_layout_spinners)
    LinearLayout spinners_layout ;
        @BindView(R.id.top_rated_fragment_progressBar)
        ProgressBar progressBar;
        @BindView(R.id.top_rated_fragment_RecyclerView)
        RecyclerView recyclerView;

        @BindView(R.id.top_rated_fragment_no_internet_layout)
        LinearLayout noInternetLayout;

        @BindView(R.id.top_rated_fragment_swipeRefreshLayout)
        SwipeRefreshLayout mRecyclerViewLayout;

    @BindView(R.id.topRated_fragment_no_movies_layout)
    LinearLayout noResultsFoundLayout;

    private RecyclerView.LayoutManager layoutManager;
        private TopRatedRecyclerViewAdapter mAdapter;
        private TopRatedPresenter mTopRatedPresenter;
    protected ArrayList<TopRatedMovie> newMovies;

    private Bundle savedState = null;
        boolean createdStateInDestroyView;
    public static int page_counter = 1;
        int curSize;
        private int col;
    protected static int total_pages;
    private boolean isOnRefesh;
    public View v;
    private Unbinder unbinder;
    protected static boolean isOnLoadMore;

    private ArrayList<String> years;
    private ArrayList<String> genres;
    protected static Setting setting;

    Context context;
    Activity activity;
    ArrayAdapter<String> list_adapter_years;
    ArrayAdapter<String> list_adapter_genres;
    private int yearPosition;
    private int genrePosition;
    protected static String selected_year;
    protected static String selected_genre;
    private boolean isSort;
    protected  String current_genre;
    protected  String current_year;
    protected static int no_visible_layout;
    private OnTopRatedFragmentInteractionListener mListener;

    public TopRatedFragment() {
        // Required empty public constructor
        mTopRatedPresenter = new TopRatedPresenterImp(this);
        Log.d(TAG, "TopRatedFragment: CONSTRUCTOR");
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnTopRatedFragmentInteractionListener) {
            mListener = (OnTopRatedFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnAboutFragmentInteractionListener");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_top_rated, container, false);
        col = getResources().getInteger(R.integer.top_rated_number_of_cols);
        unbinder= ButterKnife.bind(this, v);
        this.v = v;
        newMovies = new ArrayList<>();
        initToolbar();

        mRecyclerViewLayout.setOnRefreshListener(this);
        recyclerViewSetup();
        if (savedState==null) {
            Log.d(TAG, "onCreateView: Top rated  startup");
            mTopRatedPresenter.onCreateView(page_counter,selected_year.equals(setting.spinners_default_word) ? null : selected_year,selected_genre.equals(setting.spinners_default_word) ? 0 : getGenreCode(selected_genre));
        }
        else {
            Log.d(TAG, "onCreateView: Top rated NOT startup ");
            mTopRatedPresenter.onRecreateView(savedState,page_counter,selected_year.equals(setting.spinners_default_word) ? null : selected_year,selected_genre.equals(setting.spinners_default_word) ? 0 : getGenreCode(selected_genre));
        }
        initSpinner();
        return v;
    }

    @Override
    public void onPause() {
        super.onPause();
        mTopRatedPresenter.onPause();
    }
    @Override
    public void onResume() {
        super.onResume();
        mListener.onTopRatedFragmentOpened();

        Log.d(TAG, "onResume: TopRated Fragment 1 ");

        if (isOnLoadMore && recyclerView.getVisibility() == VISIBLE) {
            Log.d(TAG, "onResume: TopRated Fragment 2 ");
            recyclerViewSetup();
            isOnLoadMore = false;
            layoutManager.scrollToPosition(newMovies.size() - 5);
        }
        if (progressBar.getVisibility()!= View.VISIBLE &&
                recyclerView.getVisibility()!=View.VISIBLE &&
                mRecyclerViewLayout.getVisibility()!=View.VISIBLE&& noResultsFoundLayout.getVisibility()!=VISIBLE) {
            // when user open app then before it launch screen was locked, then app will do no connections due to onDestroyView();,
            // So when user re unlock screen app will appear, and frist method which will be invoked was onResume + progress,
            // list and layout are not visible, then from this point, we will make a new request in this case.
            Log.d(TAG, "onResume: 3");
            if (savedState==null) {
                Log.d(TAG, "onResume: Top rated  startup 4");
                mTopRatedPresenter.onCreateView(page_counter,selected_year.equals(setting.spinners_default_word) ? null : selected_year,selected_genre.equals(setting.spinners_default_word) ? 0 : getGenreCode(selected_genre));
            }
            else {
                Log.d(TAG, "onResume: Top rated NOT startup  5");
                mTopRatedPresenter.onRecreateView(savedState,page_counter,selected_year.equals(setting.spinners_default_word) ? null : selected_year,selected_genre.equals(setting.spinners_default_word) ? 0 : getGenreCode(selected_genre));
            }
        }
        if (recyclerView.getVisibility() == VISIBLE && !newMovies.isEmpty()) {
            Log.d(TAG, "onResume: TopRated Fragment 6 ");
            if (!current_genre.equals(spinner_genres.getSelectedItem().toString()) || !current_year.equals(spinner_year.getSelectedItem().toString())) {
                Log.d(TAG, "onResume: TopRated Fragment 7 ");
                showRefreshProgress();
                onRefresh();
            }
        }
        if (recyclerView.getVisibility() == VISIBLE && newMovies != null && !newMovies.isEmpty()) {
            Log.d(TAG, "onResume: TopRated Fragment 8");
            if (newMovies.get(0)!=null) {
                Log.d(TAG, "onResume: TopRated Fragment 9");

                if (newMovies.get(0).getOverView() == null || newMovies.get(0).getOverView().isEmpty() || newMovies.get(0).getOverView().trim().equals("")) {
                    Log.d(TAG, "onResume: 10 : Recyclerview data are missing, IMP");
                    newMovies.clear();
                    page_counter = 1;
                    mTopRatedPresenter.onCreateView(page_counter,selected_year.equals(setting.spinners_default_word) ? null : selected_year,selected_genre.equals(setting.spinners_default_word) ? 0 : getGenreCode(selected_genre));
                }
            }
        }

    }

    private void initToolbar() {
        Toolbar toolbar  = getActivity().findViewById(R.id.toolbar);
        SearchView mSearchView = toolbar.findViewById(R.id.mySearchView);
        mSearchView.setVisibility(View.GONE); // hide the search view
        TextView title =  toolbar.findViewById(R.id.toolbar_title);
        if (!title.getText().equals(getResources().getString(R.string.topRatedMovies))) {
            title.setText(getResources().getString(R.string.topRatedMovies));
        }
        title.setVisibility(View.VISIBLE);
        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
        params.setScrollFlags(SCROLL_FLAG_SCROLL|SCROLL_FLAG_ENTER_ALWAYS|SCROLL_FLAG_SNAP);  // set scroll flags
    }
    private void recyclerViewSetup()
    {
            layoutManager = new GridLayoutManager(getActivity(), col);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setHasFixedSize(true);
            mAdapter = new TopRatedRecyclerViewAdapter(newMovies, getActivity(), mTopRatedPresenter);
            recyclerView.setAdapter(mAdapter);
            recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener((GridLayoutManager) layoutManager/*,up_fab*/) {
                @Override
                public void onLoadMore(int page, int totalItemsCount)
                {
                    Log.d(TAG, "onLoadMore: 1 ");
                    if (!isOnLoadMore) {
                        Log.d(TAG, "onLoadMore: 2 ");
                        if (page_counter <= total_pages)
                        {
                            Log.d(TAG, "onLoadMore: 3 ");
                            mAdapter.showFooter();
                            isOnLoadMore= false;
                            resetFlags();
                            isOnLoadMore = true;
                            mTopRatedPresenter.onLoadMore(page_counter,selected_year.equals(setting.spinners_default_word) ? null : selected_year,selected_genre.equals(setting.spinners_default_word) ? 0 : getGenreCode(selected_genre));
                            mAdapter.notifyDataSetChanged();
                        }
                        else
                        {
                            Log.d(TAG, "onLoadMore: 4 ");
                            mAdapter.hideFooter();
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                    else
                    {
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
    public void hideSpinners() {
        spinners_layout.setVisibility(View.GONE);
    }

    @Override
    public void showSpinners() {
        spinners_layout.setVisibility(View.VISIBLE);
    }

    @Override
    public void receiveTopRatedMovies(ArrayList<TopRatedMovie> topRatedMovies, int counter, int total_pages, String year, String genre) {
        Log.d(TAG, "receiveTopRatedMovies: year = "+year+" genre "+genre);


        if (counter == 0) // data from server
        {
            Log.d(TAG, "receiveTopRatedMovies: 1");
            TopRatedFragment.total_pages = total_pages;
            if (!topRatedMovies.isEmpty() && topRatedMovies.size() != 0)
            {
                Log.d(TAG, "receiveTopRatedMovies: 2");
                if(isOnRefesh) // recieved data when user refreshed layout
                {
                    Log.d(TAG, "receiveTopRatedMovies: 3");
                    page_counter = 1;
                    newMovies.clear();
                    newMovies.addAll(topRatedMovies);
                    TopRatedFragment.total_pages = total_pages;
                    page_counter++;
                    recyclerViewSetup();
                    isOnRefesh = false;
                }
                else if(isSort)
                {
                    Log.d(TAG, "receiveTopRatedMovies: 3.1");
                    page_counter = 1;
                    newMovies.clear();
                    newMovies.addAll(topRatedMovies);
                    TopRatedFragment.total_pages = total_pages;
                    page_counter++;
                    recyclerViewSetup();
                    isSort = false;
                }
                else if (isOnLoadMore)
                {
                    Log.d(TAG, "recieveNowPlayingMovies 3.2");
                    newMovies.addAll(topRatedMovies);
                    mAdapter.notifyDataSetChanged();
                    page_counter++;
                    isOnLoadMore = false;
                    mAdapter.hideFooter();
                    mAdapter.notifyDataSetChanged();
                }
                else // normal server recieving
                {
                    Log.d(TAG, "receiveTopRatedMovies: 4");
                    newMovies.addAll(topRatedMovies);
                    mAdapter.notifyDataSetChanged();
                    page_counter++;
                }
            }
            else // empty list
            {
                Log.d(TAG, "receiveTopRatedMovies: 5");
            }
        }
        else //recieve saved data
        {
            Log.d(TAG, "receiveTopRatedMovies: 8");
            TopRatedFragment.total_pages = total_pages;
            page_counter =counter;
            newMovies.addAll(topRatedMovies);
            mAdapter.notifyDataSetChanged();
        }
        current_genre = genre;
        current_year = year;
        Log.d(TAG, "receiveTopRatedMovies: current_genre = "+current_genre+" current_year "+current_year);

    }

    @Override
    public void showErrorMessage(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showNoInternetLayout() {
        noInternetLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideNoInternetLayout() {
        noInternetLayout.setVisibility(View.GONE);
    }

    @Override
    public void hideRefreshProgress() {
        mRecyclerViewLayout.setRefreshing(false);
    }

    @Override
    public void showNoInternetSnak() {
        final Snackbar snack = Snackbar.make(recyclerView, R.string.no_internet_snack_message,10000);
        snack.setAction("ok", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snack.dismiss();
            }
        });
        snack.show();
    }

    /*  @Override
      public void showNotice(boolean isConnected) {
          String message = "Check internet connection";
          if(!isConnected)
          {
              new Common().showSnackAlert(ButterKnife.findById(getActivity(),R.id.main_activity_layout),message);
              savedState = null;
          }
      }*/
    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        Log.d(TAG, "onSaveInstanceState: 1");

        if (newMovies == null) {
            Log.d(TAG, "onSaveInstanceState: top rated: 2");
            outState.putBundle(SAVED_MOVIES_BUNDLE, savedState);
        } else {
            Log.d(TAG, "onSaveInstanceState: top rated: 3");
            outState.putBundle(SAVED_MOVIES_BUNDLE, createdStateInDestroyView ? savedState : saveState());
        }
        createdStateInDestroyView = false;
        super.onSaveInstanceState(outState);
    }
    private Bundle saveState()
    {
        Log.d(TAG, "saveState: 1 state ");
        Bundle state = new Bundle();
        state.putParcelableArrayList(SAVED_MOVIES_ARRAY_TOPRATED, newMovies);
        state.putInt(SAVED_MOVIES_PAGE_NO_TOPRATED,page_counter);
        state.putInt(SAVED_TOTAL_PAGES_TOPRATED, total_pages);
        state.putString(SAVED_CURRENT_GENRE, current_genre);
        state.putString(SAVED_CURRENT_YEAR, current_year);
        state.putIntegerArrayList(TopRatedInteractorImp.SAVED_MOVIES_FILTER, TopRatedInteractorImp.movies_filter);
        state.putInt(SAVED_NO_LAYOUT, no_visible_layout); //  3 is no results, 1 is invalid, 2 no internet layout

        Log.d(TAG, "saveState: 2 state = " + state);
        return state;
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        unbinder.unbind();
        this.v=null;
        savedState = saveState();
        createdStateInDestroyView = true;
        newMovies = null;
        page_counter = 1;
        Log.d(TAG, "onDestroyView: savedState = "+savedState );
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: top rated 1");


        context = getContext();
        activity = getActivity();
        this.setting = new Setting(context);

        if (savedInstanceState != null)
        {
            Log.d(TAG, "onCreate: top rated 2");
            savedState = savedInstanceState.getBundle(SAVED_MOVIES_BUNDLE);
        }
        setHasOptionsMenu(true);
        this.years = setting.getYears();
        this.genres = setting.getGenres();
        selected_genre = setting.getTopRatedGenre();
        genrePosition = checkSavedGenrePosition(selected_genre);
        selected_year = setting.getTopRatedYear();
        yearPosition = checkSavedYearPosition(selected_year);
    }

    private int checkSavedGenrePosition(String selected_genre) {
        int i = 0;
        for (String s:genres)
        {
            if (s.equals(selected_genre)) {
                return i;
            }
            i++;
        }
        return 0;
    }



    private  int checkSavedYearPosition(String topRatedYear) {
        int i = 0;
        for (String s:years)
        {
            if (s.equals(topRatedYear)) {
                return i;
            }
            i++;
        }
        return 0;
    }

    private void initSpinner(){
        list_adapter_years = new ArrayAdapter<>(context,android.R.layout.simple_spinner_item,years);
        list_adapter_years.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_year.setAdapter(list_adapter_years);
        spinner_year .setSelection(yearPosition);
        Log.d(TAG, "initSpinner: selection of yearPosition ="+ yearPosition);

        spinner_year.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // if user selected an item from the spinner list this method will be invoked
                if (yearPosition !=position&& spinners_layout.getVisibility()==VISIBLE) {
                    selected_year = parent.getItemAtPosition(position).toString();
                    setting.saveTopRatedYear(selected_year);
                    yearPosition = position;
                    if (noInternetLayout.getVisibility() != VISIBLE) {
                        isOnLoadMore = false;
                        resetFlags();
                        isSort = true;
                        mTopRatedPresenter.onSort(1, selected_year.equals(setting.spinners_default_word) ? null : selected_year, selected_genre.equals(setting.spinners_default_word) ? 0 : getGenreCode(selected_genre));
                        Log.d(TAG, "onItemSelected: initSpinner years: item selected  position: " + position);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.d(TAG, "onItemSelected: initSpinner: Nothing Selected");
                return;
            }
        });

        list_adapter_genres = new ArrayAdapter<>(context,android.R.layout.simple_spinner_item,genres);
        //list_adapter_genres = ArrayAdapter.createFromResource(context,R.array.Names,android.R.layout.simple_spinner_item);
        list_adapter_genres.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_genres.setAdapter(list_adapter_genres);
        spinner_genres.setSelection(genrePosition);
        Log.d(TAG, "initSpinner: selection of genrePosition ="+ genrePosition);
        spinner_genres.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // if user selected an item from the spinner list this method will be invoked
                if (genrePosition !=position && spinners_layout.getVisibility()==VISIBLE) {
                    selected_genre = parent.getItemAtPosition(position).toString();
                    genrePosition = position;
                    setting.saveTopRatedGenre(selected_genre);
                    if (noInternetLayout.getVisibility()!=VISIBLE) {
                        isOnLoadMore= false;
                        resetFlags();
                        isSort = true;
                        mTopRatedPresenter.onSort(1,selected_year.equals(setting.spinners_default_word) ? null : selected_year,selected_genre.equals(setting.spinners_default_word) ? 0 : getGenreCode(selected_genre));
                        Log.d(TAG, "onItemSelected: initSpinner genres: item selected  position:"+position);
                    }
                  }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.d(TAG, "onItemSelected: initSpinner: Nothing Selected");
                return;
            }
        });
    }
    @Override
    public void showSortProgress(){
        if (mRecyclerViewLayout.getVisibility()==VISIBLE) {
            showRefreshProgress();
        }
        else
        {
            showProgress();
        }
    }
    @OnClick(R.id.no_internet_fragment_button_retry)
    public void retryConnection(View view)
    {
        if (Utils.isNetworkAvailable(getContext())) {
            mTopRatedPresenter.onCreateView(page_counter,selected_year.equals(setting.spinners_default_word) ? null : selected_year,selected_genre.equals(setting.spinners_default_word) ? 0 : getGenreCode(selected_genre));
        }
    }

    @Override
    public void onRefresh() {
        Log.d(TAG, "onRefresh: 1");
        isOnLoadMore= false;
        resetFlags();
        isOnRefesh = true;
        mTopRatedPresenter.onRefresh(1,selected_year.equals(setting.spinners_default_word) ? null : selected_year,selected_genre.equals(setting.spinners_default_word) ? 0 : getGenreCode(selected_genre));
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem item_sort = menu.findItem(R.id.action_sort).setVisible(false);
        MenuItem item_select_date = menu.findItem(R.id.action_date_select).setVisible(false);
        MenuItem item_genre = menu.findItem(R.id.action_genre).setVisible(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
       switch (item.getItemId())
       {
           case R.id.action_search:
               moveToSearch();
                return true;
           default:
               return super.onOptionsItemSelected(item);
       }
    }
    public  void moveToSearch()
    {
        getActivity().getSupportFragmentManager().
                beginTransaction().
                //setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out,android.R.anim.fade_in, android.R.anim.fade_out).
                replace(R.id.main_container,new SearchFragment(),getString(R.string.search_fragment_tag)).
                addToBackStack(null).
                commit();
    }

    public boolean isThereDataAvailable()
    {
        return newMovies.size() > 0;
    }

    @Override
    public void onLoadMoreFail() {
        mAdapter.hideFooter();
        mAdapter.notifyDataSetChanged();
        isOnLoadMore= false;
    }
    @Override
    public void resetFlags() {
        isOnRefesh = false;
        isSort = false;
    }

    @Override
    public void showRefreshProgress() {
        mRecyclerViewLayout.setRefreshing(true);
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
    public void moveToMovieFragment(String movie_id, String title) {
        MovieFragment mMovieFragment = new MovieFragment();
        Bundle bundle = new Bundle();
        bundle.putString("movie_id_key",movie_id);
        bundle.putString("movie_title_key",title);
        mMovieFragment.setArguments(bundle);
        getActivity().getSupportFragmentManager().
                beginTransaction().
                //setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right).
                //        setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out,android.R.anim.fade_in, android.R.anim.fade_out).
                replace(R.id.main_container,mMovieFragment).
                addToBackStack(null).
                commit();
    }
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnTopRatedFragmentInteractionListener {
        // TODO: Update argument type and name
        void onTopRatedFragmentOpened();
    }
}
