package com.info.movies;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.info.movies.constants.Common;
import com.info.movies.constants.Setting;
import com.info.movies.fargments.AboutFragment;
import com.info.movies.fargments.SettingFragment;
import com.info.movies.fargments.movie.MovieFragment;
import com.info.movies.fargments.nowplaying.NowPlayingFragment;
import com.info.movies.fargments.popularmovies.PopularFragment;
import com.info.movies.fargments.search.SearchFragment;
import com.info.movies.fargments.toprated.TopRatedFragment;
import com.info.movies.fargments.upcoming.UpcomingFragment;
import com.info.movies.fargments.watchlist.WatchlistFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements AboutFragment.OnAboutFragmentInteractionListener,
        MovieFragment.OnMovieFragmentInteractionListener,
        SettingFragment.OnSettingFragmentInteractionListener,
        NowPlayingFragment.OnNowPlayingFragmentInteractionListener,
        PopularFragment.OnPopularFragmentInteractionListener,
        TopRatedFragment.OnTopRatedFragmentInteractionListener,
        UpcomingFragment.OnUpcomingFragmentInteractionListener,
        WatchlistFragment.OnWatchlistFragmentInteractionListener,
        SearchFragment.OnSearchFragmentInteractionListener,
        MainView,
//                                                            SearchView.OnQueryTextListener,
        NavigationView.OnNavigationItemSelectedListener,
        AppBarLayout.OnOffsetChangedListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.navigation_view)
    NavigationView navigationView;

    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    @BindView(R.id.main_appbar)
    AppBarLayout appBarLayout;

    @BindView(R.id.toolbar_title)
    TextView toolbar_title;
    // ************************************************ My fragment instances
    private NowPlayingFragment mNowPlayingFragment;
    private PopularFragment mPopularFragment;
    private TopRatedFragment mTopRatedFragment;
    private UpcomingFragment mUpcomingFragment;
    private WatchlistFragment mWatchlistFragment;
    private AboutFragment mAboutFragment;
    private SettingFragment mSettingFragment;
    // ************************************************ My fragment instances

    public ActionBarDrawerToggle mDrawerToggle;
    private FragmentTransaction fragmentTransaction;
    private Context context;
    private MainPresenter mPresenter;
    private boolean isAppBarCollapsed;
    private boolean mToolBarNavigationListenerIsRegistered;
    private Setting mSetting;

    public View main_activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this); // implement ButterKnife
        mPresenter = new MainPresenterImp(this);
        context = this;
        main_activity = findViewById(R.id.main_activity_layout);

        setSupportActionBar(toolbar);
        mSetting = new Setting(this);
        addHamburgerButton();
        getSupportActionBar().setTitle("");
        mToolBarNavigationListenerIsRegistered = mSetting.getNavigationState();
        mNowPlayingFragment = new NowPlayingFragment();
        mPopularFragment = new PopularFragment();
        mTopRatedFragment = new TopRatedFragment();
        mUpcomingFragment = new UpcomingFragment();
        mWatchlistFragment = new WatchlistFragment();
        mAboutFragment = new AboutFragment();
        mSettingFragment = new SettingFragment();

        View navigation_header_view = navigationView.getHeaderView(0);
        TextView app_name = navigation_header_view.findViewById(R.id.navigation_header_text_view);
        Typeface t_font = Typeface.createFromAsset(getAssets(), "font/Quicksand-Regular.ttf");
        app_name.setTypeface(t_font);
        navigationView.setNavigationItemSelectedListener(this);
        appBarLayout.addOnOffsetChangedListener(this);

        if (getIntent().getIntExtra("movie_id", 0) != 0) {
            onNotification();
        } else {
            if (savedInstanceState == null) {// set HOME fragment on the start up.
                Log.d(TAG, "onCreate: 1");
                mPresenter.nowPlayingSelected();
            } else {
                Log.d(TAG, "onCreate: 2");
            }
        }
    }

    private void onNotification() {
        int noti_id = getIntent().getIntExtra("noti_id", 0);
        int movie_id = getIntent().getIntExtra("movie_id", 0);
        String trailer_key = getIntent().getStringExtra("trailer_key");
        String movie_title = getIntent().getStringExtra("movie_title");

        Log.d(TAG, "onNotification: noti_id = " + noti_id);
        Log.d(TAG, "onNotification: movie_id = " + movie_id);
        Log.d(TAG, "onNotification: trailer_key = " + trailer_key);
        Log.d(TAG, "onNotification: movie_title = " + movie_title);

        if (noti_id != 0 && movie_id != 0 && movie_title != null && !TextUtils.isEmpty(movie_title)
                ) { // ALL ARE NOT EXIST, movie_details
            Log.d(TAG, "onNotification: 2");


            getIntent().putExtra("noti_id", 0); //kill this notification.
            MovieFragment mMovieFragment = new MovieFragment();
            Bundle bundle = new Bundle();
            bundle.putString("movie_id_key", String.valueOf(movie_id));
            bundle.putString("movie_title_key", movie_title);
            mMovieFragment.setArguments(bundle);
            getSupportFragmentManager().
                    beginTransaction().
                    replace(R.id.main_container, mMovieFragment, getResources().getString(R.string.movie_fragment_tag)).
                    //addToBackStack(null).
                            commit();
        }


        // in case watch trailer --> hwadii el user ll movie fragment w ba3d keda hwdiih ll player
        // in case movie details --> hwdi el user ll movie fragment

    }


    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.onResume();
        showAppBarIfCollapsed();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        super.onDestroy();
        mPresenter.onDestroy();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();// synchronize the state of the action bar drawer toggle whenever the layout restart.
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
        //closeDrawer();
//        setIconTextColorOfRecentAppState(this);
    }


    private void addHamburgerButton() {
        mDrawerToggle =
                new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close); //  the HAMBURGER button.
        drawerLayout.addDrawerListener(mDrawerToggle); // ADD the HAMBURGER button.
    }

    public boolean isFragmentPresent(String tag) {// return true if fragment checked is displayed and false if not.
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
        if (fragment instanceof NowPlayingFragment) {
            return true;
        } else if (fragment instanceof PopularFragment) {
            return true;
        } else if (fragment instanceof TopRatedFragment) {
            return true;
        } else if (fragment instanceof UpcomingFragment) {
            return true;
        } else if (fragment instanceof WatchlistFragment) {
            return true;
        } else if (fragment instanceof SettingFragment) {
            return true;
        } else if (fragment instanceof AboutFragment) {
            return true;
        } else {
            return false;
        }

    }


    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed: 1");
        if (drawerState()) {//drawer is open
            Log.d(TAG, "onBackPressed: 2");
            closeDrawer();
        } else if (getSupportFragmentManager().getBackStackEntryCount() == 0 && getSupportFragmentManager().findFragmentByTag(getResources().getString(R.string.movie_fragment_tag)) != null) {
            Log.d(TAG, "onBackPressed: 2.5");
            mPresenter.nowPlayingSelected();
        } else {
            Log.d(TAG, "onBackPressed: 3");
            super.onBackPressed();
        }

    }

    // View Methods ***
    @Override
    public void showNowPlayingFragment() {
        Log.d(TAG, "showNowPlayingFragment: ");
        if (isFragmentPresent(getResources().getString(R.string.nowPlayingMovies))) {
            Log.d(TAG, "showNowPlayingFragment:  this fragment is already loaded");
            closeDrawer();
        } else {
            fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.main_container, mNowPlayingFragment, getResources().getString(R.string.nowPlayingMovies));
            fragmentTransaction.commit();
        }
    }

    @Override
    public void showPopularFragment() {
        if (isFragmentPresent(getResources().getString(R.string.popularMovies))) {
            closeDrawer();
        } else {
            fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.main_container, mPopularFragment, getResources().getString(R.string.popularMovies));
            fragmentTransaction.commit();
        }
    }

    @Override
    public void showFavouriteFragment() {
        if (isFragmentPresent(getResources().getString(R.string.watchlist))) {
            closeDrawer();
        } else {
            fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.main_container, mWatchlistFragment, getResources().getString(R.string.watchlist));
            fragmentTransaction.commit();
        }

    }

    @Override
    public void showTopRatedFragment() {
        if (isFragmentPresent(getResources().getString(R.string.topRatedMovies))) {
            closeDrawer();
        } else {
            fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.main_container, mTopRatedFragment, getResources().getString(R.string.topRatedMovies));
            fragmentTransaction.commit();
        }
    }

    @Override
    public void showUpcomingFragment() {
        if (isFragmentPresent(getResources().getString(R.string.upComingMovies))) {
            closeDrawer();
        } else {
            fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.main_container, mUpcomingFragment, getResources().getString(R.string.upComingMovies));
            fragmentTransaction.commit();
        }
    }

    @Override
    public void showSettingFragment() {
        if (isFragmentPresent(getResources().getString(R.string.setting))) {
            closeDrawer();
        } else {
            fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.main_container, mSettingFragment, getResources().getString(R.string.setting));
            fragmentTransaction.addToBackStack(null).commit();
        }
    }

    @Override
    public void showAboutFragment() {

        if (isFragmentPresent(getResources().getString(R.string.about))) {
            closeDrawer();
        } else {
            fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.main_container, mAboutFragment, getResources().getString(R.string.about));
            fragmentTransaction.addToBackStack(null).commit();
        }

    }

    @Override
    public void showToolbar() {
        if (toolbar.getVisibility() == View.GONE) {
            toolbar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void hideToolbar() {
        if (toolbar.getVisibility() == View.VISIBLE) {
            toolbar.setVisibility(View.GONE);
        }
    }

    public void lockDrawer(boolean b) {
        if (b) {
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        } else {
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        }
    }

    @Override
    public void resetToolbar() // here we have to add all fragments and restore the state of navigation icon and toolbar titles.
    {
        if (getSupportFragmentManager()
                .findFragmentByTag(getResources().getString(R.string.nowPlayingMovies)) != null /*&& !mSetting.getNavigationState()*/) {
            Log.d(TAG, "resetToolbar: 1");
        } else if (getSupportFragmentManager()
                .findFragmentByTag(getResources().getString(R.string.popularMovies)) != null /*&& !mSetting.getNavigationState()*/) {
            Log.d(TAG, "resetToolbar: 2");


            mPopularFragment = (PopularFragment) getSupportFragmentManager()
                    .findFragmentByTag(getResources().getString(R.string.popularMovies));
            getSupportFragmentManager().beginTransaction().replace(R.id.main_container, mPopularFragment).commit();
        } else if (getSupportFragmentManager()
                .findFragmentByTag(getResources().getString(R.string.topRatedMovies)) != null /*&& !mSetting.getNavigationState()*/) {
            Log.d(TAG, "resetToolbar: 3");

        } else if (getSupportFragmentManager()
                .findFragmentByTag(getResources().getString(R.string.upComingMovies)) != null /*&& !mSetting.getNavigationState()*/) {
            Log.d(TAG, "resetToolbar: 4");

        } else if (getSupportFragmentManager()
                .findFragmentByTag(getResources().getString(R.string.watchlist)) != null /*&& !mSetting.getNavigationState()*/) {
            Log.d(TAG, "resetToolbar: 5");

        } else if (getSupportFragmentManager()
                .findFragmentByTag(getResources().getString(R.string.setting)) != null /*&& mSetting.getNavigationState()*/) {
            Log.d(TAG, "resetToolbar: 6");

        } else if (getSupportFragmentManager()
                .findFragmentByTag(getResources().getString(R.string.about)) != null /*&& mSetting.getNavigationState()*/) {
            Log.d(TAG, "resetToolbar: 7");

        } else if (getSupportFragmentManager()
                .findFragmentByTag(getResources().getString(R.string.search_fragment_tag)) != null /*&& mSetting.getNavigationState()*/) {
            Log.d(TAG, "resetToolbar: 8");

        } else if (getSupportFragmentManager()
                .findFragmentByTag(getResources().getString(R.string.movie_fragment_tag)) != null /*&& mSetting.getNavigationState()*/) {
            Log.d(TAG, "resetToolbar: 9");

            Log.d(TAG, "resetToolbar: movie_fragment ");
        }
        Log.d(TAG, "restoreFragmentState: ");
    }


    @Override
    public void changeToolbarTitle(int string_title_id) {
        toolbar_title.setText(string_title_id);
    }

    @Override
    public void closeDrawer() {
        if (drawerState())
            drawerLayout.closeDrawers();
    }

    @Override
    public void showAppBarIfCollapsed() {
        if (isAppBarCollapsed) {
            appBarLayout.setExpanded(true);
            Log.d(TAG, "showAppBarIfCollapsed: ");
        }
    }

    public boolean drawerState() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
            return true;
        else return false;
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.d(TAG, "onRestoreInstanceState: ");
        mPresenter.onRestoreInstanceState();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigation_item_nowPlayingMovies:
                closeDrawer();
                mPresenter.nowPlayingSelected();
                item.isChecked();
                break;

            case R.id.navigation_item_popularMovies:
                closeDrawer();
                mPresenter.popularSelected();
                item.isChecked();
                break;

            case R.id.navigation_item_topRatedMovies:
                closeDrawer();
                mPresenter.topRatedSelected();
                item.isChecked();
                break;

            case R.id.navigation_item_upComingMovies:
                closeDrawer();
                mPresenter.upcomingSelected();
                item.isChecked();
                break;

            case R.id.navigation_item_favouriteMovies:
                closeDrawer();
                mPresenter.favouriteSelected();
                item.isChecked();
                break;

            case R.id.navigation_item_about:
                mPresenter.aboutSelected();
                item.isChecked();
                closeDrawer();
                break;

            case R.id.navigation_item_setting:
                mPresenter.settingSelected();
                item.isChecked();
                closeDrawer();
                break;
            case R.id.navigation_item_share_app:
                Common.shareApp(this);
                item.isChecked();
                break;
            case R.id.navigation_item_rate_app:
                Common.rateApp(this);
                item.isChecked();
                break;
        }
        return false;
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        if (Math.abs(verticalOffset) == appBarLayout.getTotalScrollRange()) // appbar collapsed
        {
            isAppBarCollapsed = true;
        } else if (verticalOffset == 0) // appbar expanded
        {
            isAppBarCollapsed = false;
        } else isAppBarCollapsed = true;

    }

    // View Methods ***
    public void enableBackButton(boolean enable) {
        // To keep states of ActionBar and ActionBarDrawerToggle synchronized,
        // when you enable on one, you disable on the other.
        // And as you may notice, the order for this operation is disable first, then enable - VERY VERY IMPORTANT.
        if (enable) {
            // Remove hamburger
            mDrawerToggle.setDrawerIndicatorEnabled(false);
            // Show back button
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            // when DrawerToggle is disabled i.e. setDrawerIndicatorEnabled(false), navigation icon
            // clicks are disabled i.e. the UP button will not work.
            // We need to add a listener, as in below, so DrawerToggle will forward
            // click events to this listener.
            if (!mToolBarNavigationListenerIsRegistered) {
                mDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Doesn't have to be onBackPressed
                        onBackPressed();
                    }
                });

                mToolBarNavigationListenerIsRegistered = true;
                mSetting.saveToolbarNavigationState(mToolBarNavigationListenerIsRegistered);
            }

        } else {
            // Remove back button
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            // Show hamburger
            mDrawerToggle.setDrawerIndicatorEnabled(true);
            // Remove the/any drawer toggle listener
            mDrawerToggle.setToolbarNavigationClickListener(null);
            mToolBarNavigationListenerIsRegistered = false;
            mSetting.saveToolbarNavigationState(mToolBarNavigationListenerIsRegistered);
        }

        // So, one may think "Hmm why not simplify to:
        // .....
        // getSupportActionBar().setDisplayHomeAsUpEnabled(enable);
        // mDrawer.setDrawerIndicatorEnabled(!enable);
        // ......
        // To re-iterate, the order in which you enable and disable views IS important #dontSimplify.
    }


    @Override
    public void onAboutFragmentOpened() {
        Log.d(TAG, "onAboutFragmentOpened: ");
       // closeDrawer();
        showToolbar();
        changeToolbarTitle(R.string.about);
        showAppBarIfCollapsed();
        mToolBarNavigationListenerIsRegistered = false;
        enableBackButton(true);
        lockDrawer(true);
    }

    @Override
    public void onSettingFragmentOpened() {
        Log.d(TAG, "onSettingFragmentOpened: ");
      //  closeDrawer();
        showToolbar();
        changeToolbarTitle(R.string.setting);
        showAppBarIfCollapsed();
        mToolBarNavigationListenerIsRegistered = false;
        enableBackButton(true);
        lockDrawer(true);
    }

    @Override
    public void onNowPlayingFragmentOpened() {
        Log.d(TAG, "onNowPlayingFragmentOpened: ");
       // closeDrawer();
        showToolbar();
        showAppBarIfCollapsed();
        changeToolbarTitle(R.string.nowPlayingMovies);
        mToolBarNavigationListenerIsRegistered = true;
        enableBackButton(false);
        lockDrawer(false);
    }

    @Override
    public void onPopularFragmentOpened() {
        Log.d(TAG, "onPopularFragmentOpened: ");
        //closeDrawer();
        showToolbar();
        changeToolbarTitle(R.string.popularMovies);
        showAppBarIfCollapsed();
        mToolBarNavigationListenerIsRegistered = true;
        enableBackButton(false);
        lockDrawer(false);
    }

    @Override
    public void onTopRatedFragmentOpened() {
        Log.d(TAG, "onTopRatedFragmentOpened: ");
       // closeDrawer();
        showToolbar();
        changeToolbarTitle(R.string.topRatedMovies);
        showAppBarIfCollapsed();
        mToolBarNavigationListenerIsRegistered = true;
        enableBackButton(false);
        lockDrawer(false);
    }

    @Override
    public void onUpcomingFragmentOpened() {
        Log.d(TAG, "onUpComingFragmentOpened: ");
       // closeDrawer();
        showToolbar();
        changeToolbarTitle(R.string.upComingMovies);
        showAppBarIfCollapsed();
        mToolBarNavigationListenerIsRegistered = true;
        enableBackButton(false);
        lockDrawer(false);
    }

    @Override
    public void onWatchlistFragmentOpened() {
        Log.d(TAG, "onFavouriteFragmentOpened: ");
       // closeDrawer();
        showToolbar();
        //changeToolbarTitle(R.string.watchlist);
        showAppBarIfCollapsed();
        mToolBarNavigationListenerIsRegistered = true;
        enableBackButton(false);
        lockDrawer(false);
    }

    @Override
    public void onMovieFragmentOpened() {
        mPresenter.checkSessionId(this);
        mToolBarNavigationListenerIsRegistered = false;
        enableBackButton(true);
        lockDrawer(true);
    }

    @Override
    public void onSearchFragmentOpened() {
        mToolBarNavigationListenerIsRegistered = false;
        enableBackButton(true);
        lockDrawer(true);
    }
}