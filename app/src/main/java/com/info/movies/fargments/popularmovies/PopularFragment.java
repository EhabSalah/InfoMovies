package com.info.movies.fargments.popularmovies;


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
import com.info.movies.adapters.PopularPosterRecyclerViewAdapter;
import com.info.movies.constants.Utils;
import com.info.movies.fargments.movie.MovieFragment;
import com.info.movies.constants.EndlessRecyclerViewScrollListener;
import com.info.movies.constants.Setting;
import com.info.movies.fargments.search.SearchFragment;
import com.info.movies.models.posters.PopularMovie;

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


/**
 * A simple {@link Fragment} subclass.
 */
public class PopularFragment extends Fragment implements PopularView, SwipeRefreshLayout.OnRefreshListener {
    public static final String SAVED_TOTAL_PAGES_POPULAR = "total_pages";

    protected static final String SAVED_MOVIES_BUNDLE = "bundle_tag";
    protected static  final String SAVED_MOVIES_ARRAY_POPULAR = "array_tag";
    protected static final String SAVED_MOVIES_PAGE_NO_POPULAR = "page_no";
    protected static final String SAVED_RANK_NO_POPULAR = "rank_no";
    protected static final String SAVED_NO_LAYOUT ="layout_no" ;
    private static final String TAG = PopularFragment.class.getSimpleName();

    @BindView(R.id.popular_fragment_progressBar)
    ProgressBar progressBar;
    @BindView(R.id.popular_fragment_RecyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.popular_fragment_no_internet_layout)
    LinearLayout noInternetLayout;

    @BindView(R.id.popular_fragment_swipeRefreshLayout)
    SwipeRefreshLayout mRecyclerViewLayout;

    @BindView(R.id.popular_fragment_no_movies_layout)
    LinearLayout noResultsFoundLayout;

    private RecyclerView.LayoutManager layoutManager;
    protected PopularPosterRecyclerViewAdapter mAdapter;
    private PopularPresenter mPopularPresenter;
    protected static ArrayList<PopularMovie> newMovies;



    private Bundle savedState = null;
    boolean createdStateInDestroyView;
    public static int page_counter = 1;
    int curSize;

    public static int movie_rank = 1;
    protected static int total_pages;
    private Setting setting;
    private AlertDialog dialog_genre;
    private boolean isOnRefesh ;
    public View v;
    private Unbinder unbinder;
    protected static boolean isOnLoadMore;
    private boolean isDoGenre;
    protected static int popular_genre;
    private int saved_genre;
    protected Activity activity;
    protected static int no_visible_layout;
    private OnPopularFragmentInteractionListener mListener;

    public PopularFragment() {
        // Required empty public constructor
        mPopularPresenter = new PopularPresenterImp(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnPopularFragmentInteractionListener) {
            mListener = (OnPopularFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnAboutFragmentInteractionListener");
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_popular, container, false);
        unbinder= ButterKnife.bind(this, v);
        this.v=v;
        newMovies = new ArrayList<>();
        initToolbar();
        mRecyclerViewLayout.setOnRefreshListener(this);
        recyclerViewSetup();
        if (savedState==null)
        {
            Log.d(TAG, "onCreateView Popular: Startup");
            mPopularPresenter.onCreateView(page_counter,popular_genre);
        }
        else
        {
            Log.d(TAG, "onCreateView Popular: Not Startup");
            mPopularPresenter.onReCreateView(savedState, page_counter,popular_genre);
        }


        return v;
    }
    @Override
    public void onPause() {
        super.onPause();
        mPopularPresenter.onPause();
    }
    @Override
    public void onResume()
    {
        super.onResume();
        mListener.onPopularFragmentOpened();
        Log.d(TAG, "onResume: Popular Fragment  1");
        if (progressBar.getVisibility()!= View.VISIBLE &&
                recyclerView.getVisibility()!=View.VISIBLE &&
                mRecyclerViewLayout.getVisibility()!=View.VISIBLE&& noResultsFoundLayout.getVisibility()!=VISIBLE) {
            // when user open app then before it launch screen was locked, then app will do no connections due to onDestroyView();,
            // So when user re unlock screen app will appear, and frist method which will be invoked was onResume + progress,
            // list and layout are not visible, then from this point, we will make a new request in this case.
            Log.d(TAG, "onResume: 3");
            if (savedState==null)
            {
                Log.d(TAG, "onResume Startup");
                mPopularPresenter.onCreateView(page_counter,popular_genre);
            }
            else
            {
                Log.d(TAG, "onResume Not Startup");
                mPopularPresenter.onReCreateView(savedState, page_counter,popular_genre);
            }
        }
        if (setting.getPosterInfoView())
        {
            mAdapter.showInfo();
        }
        else mAdapter.hideInfo();


        if (isOnLoadMore && recyclerView.getVisibility()== VISIBLE) {
            Log.d(TAG, "onResume: 2 ");
            recyclerViewSetup();
            isOnLoadMore = false;
            layoutManager.scrollToPosition(newMovies.size()-5);
        }
        if (recyclerView.getVisibility() == VISIBLE && newMovies != null && !newMovies.isEmpty()) {
            Log.d(TAG, "onResume:   3");
            if (newMovies.get(0) != null) {
                Log.d(TAG, "onResume:   4");

                if (newMovies.get(0).getMovie_id() == null || newMovies.get(0).getMovie_id().isEmpty() ||newMovies.get(0).getTitle()==null || TextUtils.isEmpty(newMovies.get(0).getTitle().trim())) {
                    Log.d(TAG, "onResume: 5 : Recyclerview data are missing");
                    newMovies.clear();
                    page_counter = 1;
                    mPopularPresenter.onCreateView(page_counter,popular_genre);
                }
            }
        }

    }
    private void initToolbar() {
        Toolbar toolbar  = activity.findViewById(R.id.toolbar);
        SearchView mSearchView = toolbar.findViewById(R.id.mySearchView);
        mSearchView.setVisibility(View.GONE); // hide the search view
        TextView title =  toolbar.findViewById(R.id.toolbar_title);
        if (!title.getText().equals(getResources().getString(R.string.popularMovies))) {
            title.setText(getResources().getString(R.string.popularMovies));
        }
        title.setVisibility(View.VISIBLE);
        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
        params.setScrollFlags(SCROLL_FLAG_SCROLL|SCROLL_FLAG_ENTER_ALWAYS|SCROLL_FLAG_SNAP);  // set scroll flags
    }
    private void recyclerViewSetup(){
        layoutManager = new GridLayoutManager(activity,activity.getResources().getInteger(R.integer.number_of_cols));
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        mAdapter = new PopularPosterRecyclerViewAdapter(newMovies, activity, mPopularPresenter);
        recyclerView.setAdapter(mAdapter);
        recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener((GridLayoutManager) layoutManager/*,up_fab*/) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                Log.d(TAG, "onLoadMore: 1 ");
                if (!isOnLoadMore)
                {
                    Log.d(TAG, "onLoadMore: 2");

                    if(page_counter<=total_pages)
                    {
                        Log.d(TAG, "onLoadMore:3 ");
                        mPopularPresenter.onLoadMore(page_counter,popular_genre);
                        mAdapter.showFooter();
                        isOnLoadMore= false;
                        resetFlags();
                        isOnLoadMore = true;
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
    public void showRecyclerView()  {
                recyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideRecyclerView() {
            recyclerView.setVisibility(View.GONE);
    }

    @Override
    public void receivePopularMovies(ArrayList<PopularMovie> popularMovies, int page_counter, int total_pages) {
        Log.d(TAG, "receivePopularMovies: popularMovies size ="+popularMovies.size());
        if (page_counter == 0) // data from server
        {
            Log.d(TAG, "receivePopularMovies: 3");

            PopularFragment.total_pages = total_pages;
            if (!popularMovies.isEmpty() && popularMovies.size() != 0)
            {
                Log.d(TAG, "receivePopularMovies: 4");
                if(isOnRefesh) // recieved data when user refreshed layout
                {
                    Log.d(TAG, "receivePopularMovies: 5");
                    PopularFragment.page_counter =1;
                    movie_rank=1;
                    newMovies.clear();
                    newMovies.addAll(popularMovies);
                    PopularFragment.total_pages = total_pages;
                    PopularFragment.page_counter++;
                    recyclerViewSetup();
                    isOnRefesh = false;
                }
                else if(isDoGenre)
                {
                    Log.d(TAG, "receivePopularMovies: 5.1");
                    PopularFragment.page_counter = 1;
                    movie_rank = 1;
                    newMovies.clear();
                    newMovies.addAll(popularMovies);
                    PopularFragment.total_pages = total_pages;
                    PopularFragment.page_counter++;
                    recyclerViewSetup();
                    isDoGenre = false;
                }
                else if (isOnLoadMore)
                {
                    Log.d(TAG, "recieveNowPlayingMovies 3.2");
                    newMovies.addAll(popularMovies);
                    mAdapter.notifyDataSetChanged();
                    PopularFragment.page_counter++;
                    mAdapter.hideFooter();
                    mAdapter.notifyDataSetChanged();
                    isOnLoadMore = false;
                }
                else // normal server recieving
                {
                    Log.d(TAG, "receivePopularMovies: 7");
                    newMovies.addAll(popularMovies);
                    mAdapter.notifyDataSetChanged();
                    PopularFragment.page_counter++;
                    isOnLoadMore= false;
                    resetFlags();
                }
            }
            else
            {
                Log.d(TAG, "receivePopularMovies: 8");
            }
        }
        else //recieve saved data
        {
            Log.d(TAG, "receivePopularMovies: 11");
            PopularFragment.total_pages = total_pages;
            PopularFragment.page_counter =page_counter;
            newMovies.addAll(popularMovies);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void showErrorMessage(String message) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
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
    public void hideRefreshProgressAnimation() {
        mRecyclerViewLayout.setRefreshing(false);
    }

    @Override
    public void showRefreshProgressAnimation() {
        mRecyclerViewLayout.setRefreshing(true);
    }


    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        Log.d(TAG, "onSaveInstanceState: popular: 1");
        if (newMovies == null) {
            Log.d(TAG, "onSaveInstanceState: popular: 2");

            outState.putBundle(SAVED_MOVIES_BUNDLE, savedState);
        } else {
            Log.d(TAG, "onSaveInstanceState: popular: 3");
            outState.putBundle(SAVED_MOVIES_BUNDLE, createdStateInDestroyView ? savedState : saveState());
        }
        createdStateInDestroyView = false;
        super.onSaveInstanceState(outState);
    }
    private Bundle saveState()
    {
        Bundle state = new Bundle();
        state.putParcelableArrayList(SAVED_MOVIES_ARRAY_POPULAR, newMovies);
        state.putInt(SAVED_MOVIES_PAGE_NO_POPULAR,page_counter);
        state.putInt(SAVED_RANK_NO_POPULAR,movie_rank);
        state.putInt(SAVED_TOTAL_PAGES_POPULAR, total_pages);
        state.putIntegerArrayList(PopularInteractorImp.SAVED_MOVIES_FILTER, PopularInteractorImp.movies_filter);
        state.putInt(SAVED_NO_LAYOUT, no_visible_layout); //  3 is no results, 1 is invalid, 2 no internet layout
        Log.d(TAG, "saveState popular: 1 state = " + state);
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
        movie_rank = 1;
        Log.d(TAG, "onDestroyView Popular" );
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate Popular : 1");
        this.activity = getActivity();
        setting = new Setting(activity);
        if (savedInstanceState != null) {
            Log.d(TAG, "onCreate Popular : 2");
            savedState = savedInstanceState.getBundle(SAVED_MOVIES_BUNDLE);
        }
        setHasOptionsMenu(true);
        checkSavedPopularGenre();
    }
    private void checkSavedPopularGenre()
    {
        popular_genre = setting.getPopularGnre();
        if(popular_genre !=0)
        {
            saved_genre = getSavedGenrePosition(getGenreString(popular_genre))+1;
        }
        else
        {
            saved_genre = 0;
        }
    }
    private int getSavedGenrePosition(String genreString)
    {
        for (int i = 0; i <basic_geners.size() ; i++)
        {
            if (basic_geners.get(i).getName().equals(genreString))
            {
                return i;
            }
        }
        return 0;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_genre:
                CreateGenresDialog() ;
                return true;
            case R.id.action_search:
                moveToSearch();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public  void moveToSearch(){
        getActivity().getSupportFragmentManager().
                beginTransaction().
              //  setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out,android.R.anim.fade_in, android.R.anim.fade_out).
                replace(R.id.main_container,new SearchFragment(),getString(R.string.search_fragment_tag)).
                addToBackStack(null).
                commit();
    }
    private void CreateGenresDialog() // THIS DIALOG WILL BE USED TO SHOW GENRES || YEARS
    {
        final ArrayList<String> genres = new ArrayList<>();
        genres.add("All");
        genres.addAll(getGenresNamesList());

        final CharSequence[] values = genres.toArray(new CharSequence[genres.size()]);

        AlertDialog.Builder builder = new AlertDialog.Builder(activity,R.style.MaterialThemeDialog);

        builder.setTitle("Genre");
        builder.setSingleChoiceItems(values, saved_genre, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {
                if(item!=0)
                {
                    popular_genre = getGenreCode(genres.get(item));
                    saved_genre = item;
                    setting.savePopularGnre(popular_genre);
                    if (noInternetLayout.getVisibility()!=VISIBLE)
                    {
                        isOnLoadMore= false;
                        resetFlags();
                        isDoGenre = true;
                        mPopularPresenter.onDataSet(1, popular_genre);
                    }
                }
                else
                {
                    saved_genre = 0;
                    popular_genre = 0;
                    setting.savePopularGnre(0);
                    if (noInternetLayout.getVisibility()!=VISIBLE)
                    {
                        isOnLoadMore= false;
                        resetFlags();
                        isDoGenre = true;
                        mPopularPresenter.onDataSet( 1, 0);
                    }
                }
                dialog.dismiss();
            }
        });
        dialog_genre = builder.create();
        dialog_genre.show();
    }

    @OnClick(R.id.no_internet_fragment_button_retry)
    public void retryConnection(View view) {
        if (Utils.isNetworkAvailable(getContext())) {
            mPopularPresenter.onCreateView(page_counter,popular_genre);
        }
    }

    @Override
    public void onRefresh()
    {
            isOnLoadMore= false;
            resetFlags();
            isOnRefesh = true;
            mPopularPresenter.onRefresh( 1,popular_genre);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem item_sort = menu.findItem(R.id.action_sort).setVisible(false);
        MenuItem item_select_date = menu.findItem(R.id.action_date_select).setVisible(false);
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
    public boolean isThereDataAvailable() {
        return newMovies.size()>0;
    }
    @Override
    public void onLoadMoreFail() {
        mAdapter.hideFooter();
        mAdapter.notifyDataSetChanged();
        isOnLoadMore= false;
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
        if (mRecyclerViewLayout.getVisibility()==VISIBLE) {
            showRefreshProgressAnimation();
        }
        else
        {
            showProgress();
        }
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
               //         setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out,android.R.anim.fade_in, android.R.anim.fade_out).
                replace(R.id.main_container,mMovieFragment).
                addToBackStack(null).
                commit();
    }
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnPopularFragmentInteractionListener {
        // TODO: Update argument type and name
        void onPopularFragmentOpened();
    }
}
