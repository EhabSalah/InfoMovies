package com.info.movies.fargments.movie;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.info.movies.MainActivity;
import com.info.movies.R;
import com.info.movies.activities.image_slider.ImageSliderActivity;
import com.info.movies.fargments.movie.adapters.MovieRecommendationsAdapter;
import com.info.movies.constants.Common;
import com.info.movies.constants.GlideApp;
import com.info.movies.constants.Setting;
import com.info.movies.db.DBHelper;
import com.info.movies.fargments.movie.adapters.MovieCastAdapter;
import com.info.movies.fargments.movie.adapters.MovieImagesAdapter;
import com.info.movies.models.movie_page.ListItemCastCrew;
import com.info.movies.models.movie_page.ListItemVideo;
import com.info.movies.models.movie_page.MoviePage;

import com.info.movies.fargments.movie.adapters.MovieVideosAdapter;
import com.info.movies.models.posters.MoviePosterAR;
import com.google.android.youtube.player.YouTubeApiServiceUtil;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.info.movies.constants.Common.ADMOB_APP_ID;


/**
 * A simple {@link Fragment} subclass.
 */
public class MovieFragment extends Fragment implements MovieView, View.OnClickListener {
    private static final String TAG = MovieFragment.class.getSimpleName();
    private static final String SAVED_DATA_KEY = "saved_data";
    private static String TITLE;
    private static String ID;
    private Toolbar toolbar;

    private CardView image_layout;
    private NestedScrollView details_layout;
    private CardView video_layout;
    private CardView cast_crew_director_layout;
    private CardView belongs_to_layout;
    private CardView overview_layout;
    private CardView main_info_layout;
    private LinearLayout cast_layout;
    private LinearLayout director_layout;
    private CoordinatorLayout trailer_layout;
    private CardView externals_media_layout;


    private ProgressBar pb;

    private ImageView trailer_thumnail;
    private ImageView trailer_ic_play;

    private LinearLayout genres_layout;
    private LinearLayout release_date_layout;
    private LinearLayout runtime_layout;
    private LinearLayout vote_average_layout;
    private LinearLayout language_layout;
    private TextView title_tv;
    private TextView genres_tv;
    private TextView release_date_tv;
    private TextView runtime_tv;
    private TextView vote_average_tv;
    private TextView vote_count_tv;
    private TextView language_tv;
    private ImageView poster_iv;

    private TextView movie_overview;

    private RecyclerView videos_recyclerview;
    private RecyclerView images_recyclerview;

    private RecyclerView cast_crew_recyclerview;

    private TextView director_name_tv;

    private TextView belongs_to_tv;

    private MoviePresenter mMoviePresenter;
    public View v;


    private String trailer_key;
    private ImageView imdp_logo;
    private LinearLayout revenue_layout;
    private TextView revenue_tv;
    private LinearLayout home_page_layout;

    private CardView production_layout;
    private LinearLayout companies_layout;
    private LinearLayout countries_layout;
    private CardView tagline_layout;

    private TextView companies_tv;
    private TextView countries_tv;
    private TextView tagline_tv;
    private Button add_to_watchlist_btn;
    private OnMovieFragmentInteractionListener mListener;
    RatingBar rate_bar;
    ProgressBar rating_pb;
    Button btn_send_rate;
    float rateValue;
    LinearLayout rate_layout;
    String rated = null;
    private CardView layout_recommendations;
    private RecyclerView recommendations_recyclerview;
    Bundle savedState;

    public MovieFragment() {
        // Required empty public constructor
    }

    private String dataSource = null;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnMovieFragmentInteractionListener) {
            mListener = (OnMovieFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnAboutFragmentInteractionListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d(TAG, "onCreateView:  ");
        View v = inflater.inflate(R.layout.fragment_movie, container, false);
        toolbar = getActivity().findViewById(R.id.toolbar);
        initToolbar(toolbar, TITLE);
        initLayouts(v);
        this.v = v;


        if (savedInstanceState == null) // not rotated
        {
            Log.d(TAG, "onCreateView: 1");
            if (savedState != null) { // data exists
                Log.d(TAG, "onCreateView: 2");
                if (savedState.getString(SAVED_DATA_KEY) != null) { // data object not empty.
                    Log.d(TAG, "onCreateView: 3 ID = " + ID);
                    Log.d(TAG, "onCreateView: 3 TITLE = " + TITLE);
                    Log.d(TAG, "onCreateView: 3 savedState.getString(SAVED_DATA_KEY) = " + savedState.getString(SAVED_DATA_KEY));

                    MoviePage m = new Gson().fromJson(savedState.getString(SAVED_DATA_KEY), MoviePage.class);
                    mMoviePresenter.onRecreateView(m);
                } else // problem and data object is empty
                {
                    Log.d(TAG, "onCreateView: 4");
                    if (ID != null && !ID.isEmpty()) { // ID exists and not empty
                        mMoviePresenter.onCreateView(ID);
                    } else // ID not exists
                    {
                        Log.d(TAG, "onCreateView: 5");
                        getActivity().onBackPressed();
                    }
                }
            } else {
                Log.d(TAG, "onCreateView: 6");
                mMoviePresenter.onCreateView(ID);
            }
        } else { // rotated and savedinstance not empty
            Log.d(TAG, "onCreateView: 7");
            MoviePage m = new Gson().fromJson(savedInstanceState.getString(SAVED_DATA_KEY), MoviePage.class);
            if (m != null) { // data exists
                Log.d(TAG, "onCreateView: 8");
                mMoviePresenter.onRecreateView(m);
            } else { // problem and data not exists!
                Log.d(TAG, "onCreateView: 9");
                mMoviePresenter.onCreateView(ID);
            }
        }
        initAdView(v, getActivity()); // AD MOB
        return v;
    }

    com.google.android.gms.ads.AdView mAdView;

    private void initAdView(View v, Activity activity) {
        Log.d(TAG, "initAdView: ");
        MobileAds.initialize(activity, ADMOB_APP_ID);
        mAdView = v.findViewById(R.id.adView);
        if (mAdView != null) {
            AdRequest adRequest = new AdRequest.Builder()/*.addTestDevice("A49D9EE3BA544C51A0673A3E0679B57D")*/
                    .build();

            mAdView.setAdListener(new com.google.android.gms.ads.AdListener() {

                @Override
                public void onAdLoaded() {
                    Log.d(TAG, "initAdView: 1");
                    mAdView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAdFailedToLoad(int error) {
                    Log.d(TAG, "initAdView: 2");
                    mAdView.setVisibility(View.GONE);
                }

            });
            mAdView.loadAd(adRequest);
        }
    }

    private void initLayouts(View v) {
        layout_recommendations = v.findViewById(R.id.movie_fragment_recommendations_layout);
        recommendations_recyclerview = v.findViewById(R.id.movie_fragment_recommendations_recyclerview);
        details_layout = v.findViewById(R.id.movie_fragment_details_layout);
        image_layout = v.findViewById(R.id.movie_fragment_images_layout);
        video_layout = v.findViewById(R.id.movie_fragment_videos_layout);
        cast_crew_director_layout = v.findViewById(R.id.movie_fragment_cast_crew_director_layout);
        director_layout = v.findViewById(R.id.movie_fragment_director_layout);
        overview_layout = v.findViewById(R.id.movie_fragment_overview_layout);
        main_info_layout = v.findViewById(R.id.movie_fragment_main_info_layout);
        trailer_layout = v.findViewById(R.id.trailer_thumbnail_layout);
        externals_media_layout = v.findViewById(R.id.movie_externals_media_layout);
        belongs_to_layout = v.findViewById(R.id.movie_fragment_belongs_to_layout);
        home_page_layout = v.findViewById(R.id.movie_fragment_home_page_layout);

        pb = v.findViewById(R.id.movie_fragment_pb);

        trailer_thumnail = v.findViewById(R.id.movie_fragment_trailer_thumbnail);
        trailer_ic_play = v.findViewById(R.id.movie_fragment_trailer_ic_play);
        trailer_ic_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // here i have to check the existance of youtube app is allready installed, or not, if not show a dialog
                // also have to check if the youtube is enabled or disabled ?, if disabled show dialog.
                // if it is installed and enabled run youtube code
                Log.d(TAG, "onClick trailer: ");
                mMoviePresenter.onPlayTrailerClick(getActivity(), trailer_key);

            }
        });

        genres_layout = v.findViewById(R.id.genres_layout);
        release_date_layout = v.findViewById(R.id.release_date_layout);
        runtime_layout = v.findViewById(R.id.runtime_layout);
        vote_average_layout = v.findViewById(R.id.vote_average_layout);
        vote_average_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rated = Common.MovieRate(ID, getContext());
                if (rated == null) {
                    Log.d(TAG, "onClick: vote_average_layout 1");
                    mMoviePresenter.onRateLayoutClicked();
                } else {

                    Log.d(TAG, "onClick: vote_average_layout 2");
                    Toast.makeText(getContext(), "You Already Rated " + String.format(Locale.ENGLISH, "%.1f", Float.valueOf(rated)), Toast.LENGTH_SHORT).show();
                }
            }
        });

        language_layout = v.findViewById(R.id.language_layout);
        revenue_layout = v.findViewById(R.id.revenue_layout);

        title_tv = v.findViewById(R.id.movie_fragment_title);
        genres_tv = v.findViewById(R.id.movie_fragment_genres);
        release_date_tv = v.findViewById(R.id.movie_fragment_release_date);
        runtime_tv = v.findViewById(R.id.movie_fragment_runtime);
        vote_average_tv = v.findViewById(R.id.movie_fragment_vote_average);
        vote_count_tv = v.findViewById(R.id.movie_fragment_vote_count);
        language_tv = v.findViewById(R.id.movie_fragment_language);
        poster_iv = v.findViewById(R.id.movie_fragment_poster);
        revenue_tv = v.findViewById(R.id.movie_fragment_revenue);

        // TODO: RATING VIEWS
        rate_layout = v.findViewById(R.id.movie_fragment_rate_layout);
        rate_bar = v.findViewById(R.id.movie_fragment_rate);
        rate_bar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float ratingValue, boolean fromUser) {
                Log.d(TAG, "onRatingChanged: fromUser = " + fromUser);
                Log.d(TAG, "onRatingChanged: ratingValue = " + ratingValue);
                rateValue = ratingValue;
            }
        });
        rating_pb = v.findViewById(R.id.movie_fragment_rating_pb);
        btn_send_rate = v.findViewById(R.id.movie_fragment_send_rate);
        btn_send_rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: btn_send_rate");
                mMoviePresenter.sendRate(ID, rateValue);
            }
        });

        movie_overview = v.findViewById(R.id.movie_fragment_overview);
        movie_overview.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Common.CopyText(movie_overview, getActivity());
                return true;
            }
        });

        videos_recyclerview = v.findViewById(R.id.movie_fragment_videos_recyclerview);

        images_recyclerview = v.findViewById(R.id.movie_fragment_images_recyclerview);

        cast_layout = v.findViewById(R.id.movie_fragment_cast_layout);

        cast_crew_recyclerview = v.findViewById(R.id.movie_fragment_cast_crew_recyclerview);
        director_name_tv = v.findViewById(R.id.movie_fragment_director_name);

        imdp_logo = v.findViewById(R.id.movie_fragment_imdp_logo);
        belongs_to_tv = v.findViewById(R.id.belongs_to_tv);

        production_layout = v.findViewById(R.id.movie_fragment_production_layout);
        companies_layout = v.findViewById(R.id.movie_fragment_production_companies_layout);
        countries_layout = v.findViewById(R.id.movie_fragment_production_states_layout);
        companies_tv = v.findViewById(R.id.productions_companies_tv);
        countries_tv = v.findViewById(R.id.productions_states_tv);

        tagline_layout = v.findViewById(R.id.movie_fragment_tagline_layout);
        tagline_tv = v.findViewById(R.id.tagline_tv);
        tagline_tv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Common.CopyText(tagline_tv, getActivity());
                return true;
            }
        });

    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: ");
        setHasOptionsMenu(true);
        Bundle arguments = getArguments();
        ID = arguments.getString("movie_id_key");
        TITLE = arguments.getString("movie_title_key");
        Log.d(TAG, "onCreate: ID = " + ID);
        Log.d(TAG, "onCreate: TITLE = " + TITLE);
        mMoviePresenter = new MoviePresenterIMP(this, getContext());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "onSaveInstanceState: 1");
        if (dataSource != null) {
            outState.putString(SAVED_DATA_KEY, dataSource);
        }
        super.onSaveInstanceState(outState);
        mMoviePresenter.onSaveInstanceState();
    }

    @Override
    public void onDestroyView() {
        Log.d(TAG, "onDestroyView: 1");
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroyView();
        savedState = new Bundle();
        savedState.putString(SAVED_DATA_KEY, dataSource);
        mMoviePresenter.onDestroyView();
        if (snack != null) {
            snack.dismiss();
        }
    }

    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        if (mAdView != null) {
            mAdView.resume();
        }
        super.onResume();
        Log.d(TAG, "onResume: ");
        mListener.onMovieFragmentOpened();
        showAppBar(toolbar);

        add_to_watchlist_btn = v.findViewById(R.id.movie_fragment_add_to_watchlist_btn);
        if (Common.isMovieAddedToWatchlist(ID, getContext())) {
            add_to_watchlist_btn.setBackground(getResources().getDrawable(R.drawable.added_to_watchlist));
            add_to_watchlist_btn.setText(R.string.added_watchlist);
        } else {
            add_to_watchlist_btn.setBackground(getResources().getDrawable(R.drawable.add_to_watch_list_background));
            add_to_watchlist_btn.setText(R.string.add_watchlist);
        }
//        ✔  Added to watchlist +  Add To Watchlist

        add_to_watchlist_btn.setOnClickListener(this);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        Log.d(TAG, "onPrepareOptionsMenu: ");
        MenuItem item_search = menu.findItem(R.id.action_search).setVisible(false);
        MenuItem item_genre = menu.findItem(R.id.action_genre).setVisible(false);
        MenuItem item_sort = menu.findItem(R.id.action_sort).setVisible(false);
        MenuItem item_select_date = menu.findItem(R.id.action_date_select).setVisible(false);
    }

    void initToolbar(Toolbar toolbar, String toolbar_title) {
        SearchView mSearchView = toolbar.findViewById(R.id.mySearchView);
        mSearchView.setVisibility(View.GONE); // hide the search view
        TextView title = toolbar.findViewById(R.id.toolbar_title);
        title.setText(toolbar_title);
        title.setVisibility(View.VISIBLE);
    }

    void showAppBar(Toolbar toolbar) {
        ((MainActivity) getActivity()).showAppBarIfCollapsed();
        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
        params.setScrollFlags(0);  // clear all scroll flags
    }

    @Override
    public void showMainInfoLayout() {
        main_info_layout.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideMainInfoLayout() {
        main_info_layout.setVisibility(View.GONE);
    }

    @Override
    public void showExternalMediaLayout() {
        externals_media_layout.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideExternalMediaLayout() {
        externals_media_layout.setVisibility(View.GONE);
    }

    @Override
    public void showDetailesLayout() {
        details_layout.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideDetailesLayout() {
        details_layout.setVisibility(View.GONE);
    }

    @Override
    public void showTrailerLayout() {
        trailer_layout.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideTrailerLayout() {
        trailer_layout.setVisibility(View.GONE);
    }

    @Override
    public void showOverviewLayout() {
        overview_layout.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideOverviewLayout() {
        overview_layout.setVisibility(View.GONE);
    }

    @Override
    public void showVideoLayout() {
        video_layout.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideVideoLayout() {
        video_layout.setVisibility(View.GONE);
    }

    @Override
    public void showImageLayout() {
        image_layout.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideImageLayout() {
        image_layout.setVisibility(View.GONE);
    }

    @Override
    public void showCastCrewDirectorLayout() {
        cast_crew_director_layout.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideCastCrewDirectorLayout() {
        cast_crew_director_layout.setVisibility(View.GONE);
    }

    @Override
    public void showProgress() {
        pb.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        pb.setVisibility(View.GONE);
    }

    @Override
    public void showCastLayout() {
        cast_layout.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideCastLayout() {
        cast_layout.setVisibility(View.GONE);
    }

    @Override
    public void showDirectorLayout() {
        director_layout.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideDirectorLayout() {
        director_layout.setVisibility(View.GONE);
    }

    @Override
    public void receiveTrailerKey(String trailer_key) {
        this.trailer_key = trailer_key;
        GlideApp.
                with(getActivity()).
                load("https://i1.ytimg.com/vi/" + trailer_key + "/hqdefault.jpg").
//                placeholder(R.drawable.movie_images_placeholder).
//                error(R.drawable.placeholder_video).
        transition(DrawableTransitionOptions.withCrossFade(getResources().getInteger(R.integer.load_cross_fade_posters))).

                into(trailer_thumnail);
        trailer_thumnail.setVisibility(View.VISIBLE);
        trailer_ic_play.setVisibility(View.VISIBLE);
    }

    @Override
    public void receiveMainInfo(final String title, final String poster_path, String genres, String release_date, String runtime, String vote_average, String vote_count, String languages, String revenue) {

        if (title != null) {
            title_tv.setText(title);
        }

        if (poster_path != null) {
            poster_iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getContext().startActivity(new Intent(getActivity(), ImageSliderActivity.class).putExtra("image", poster_path).putExtra("title", TITLE));
                }
            });
            GlideApp.
                    with(getActivity()).
                    load("https://image.tmdb.org/t/p/w500/" + poster_path).
                    placeholder(R.drawable.poster_placeholder).
                    error(R.drawable.poster_placeholder)
                    .into(poster_iv);
        } else {
            GlideApp.
                    with(getActivity()).
                    load("").
                    placeholder(R.drawable.poster_placeholder).
                    error(R.drawable.poster_placeholder)
                    .into(poster_iv);
        }


        if (genres != null) {
            // lw fi  data
            genres_tv.setText(genres);
            genres_layout.setVisibility(View.VISIBLE);

        } else genres_layout.setVisibility(View.GONE);

        if (release_date != null) {
            release_date_tv.setText(release_date);
            release_date_layout.setVisibility(View.VISIBLE);
        } else release_date_layout.setVisibility(View.GONE);

        if (runtime != null) {
            String rt = runtime + getString(R.string.movie_fragment_runtime_mins_word);
            runtime_tv.setText(rt);
            runtime_layout.setVisibility(View.VISIBLE);
        } else runtime_layout.setVisibility(View.GONE);

        if (vote_average != null) {
            // lw fi  data
            vote_average_tv.setText(vote_average);
            vote_average_layout.setVisibility(View.VISIBLE);

        } else vote_average_layout.setVisibility(View.GONE);

        if (vote_count != null) {
            // lw fi  data
            vote_count_tv.setText(vote_count);
            vote_count_tv.setVisibility(View.VISIBLE);
        } else vote_count_tv.setVisibility(View.GONE);

        if (languages != null) {

            language_tv.setText(languages);
            language_layout.setVisibility(View.VISIBLE);
        } else language_layout.setVisibility(View.GONE);

        if (revenue != null && !revenue.isEmpty()) {
            revenue_layout.setVisibility(View.VISIBLE);
            revenue_tv.setText(revenue);
        } else revenue_layout.setVisibility(View.GONE);

    }

    @Override
    public void recieveOverview(final String overview) {
        movie_overview.setText(overview);
        movie_overview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                movie_overview.setMaxLines(50);
                movie_overview.setEllipsize(null);
            }
        });
    }

    @Override
    public void recieveVideos(List<ListItemVideo> videos) {
        Log.d(TAG, "recieveVideos: ");
        MovieVideosAdapter mAdapter = new MovieVideosAdapter(videos, getContext(), mMoviePresenter);
        videos_recyclerview.setHasFixedSize(true);
        videos_recyclerview.setAdapter(mAdapter);
    }

    @Override
    public void recieveImages(List<String> images) {
        images_recyclerview.setHasFixedSize(true);
        images_recyclerview.setAdapter(new MovieImagesAdapter(images, getContext(), mMoviePresenter));

    }

    @Override
    public void receiveCrewCastDirector(List<ListItemCastCrew> cast, String director_name) {
        RecyclerView.Adapter mMovieCastAdapter;
        if (cast != null) {
            mMovieCastAdapter = new MovieCastAdapter(cast, getContext(), mMoviePresenter);
            cast_crew_recyclerview.setHasFixedSize(true);
            cast_crew_recyclerview.setAdapter(mMovieCastAdapter);
            mMovieCastAdapter.notifyDataSetChanged();
        }
        if (director_name != null) {
            director_name_tv.setText(director_name);
        }
    }

    @Override
    public void recieveExternalMedia(final String home_page, final String imdp_id) {
        if (home_page != null) {
            home_page_layout.setVisibility(View.VISIBLE);
            home_page_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String url = home_page;
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    getActivity().startActivity(i);
                }
            });
        } else home_page_layout.setVisibility(View.GONE);

        if (imdp_id != null) {
            imdp_logo.setVisibility(View.VISIBLE);
            imdp_logo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String url = "http://www.imdb.com/title/" + imdp_id;
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    getActivity().startActivity(i);
                }
            });
        } else {
            imdp_logo.setVisibility(View.GONE);
        }
    }

    @Override
    public void hideBelongsToLayout() {
        belongs_to_layout.setVisibility(View.GONE);
    }

    @Override
    public void showBelongsToLayout() {
        belongs_to_layout.setVisibility(View.VISIBLE);
    }

    @Override
    public void recieveBelongsTo(String belongs_to) {
        Log.d(TAG, "recieveBelongsTo: = " + belongs_to);
        belongs_to_tv.setText(belongs_to);
    }

    @Override
    public void hideProductionLayout() {
        production_layout.setVisibility(View.GONE);
    }

    @Override
    public void showProductionLayout() {
        production_layout.setVisibility(View.VISIBLE);
    }

    @Override
    public void receiveProduction(List<String> companies, List<String> countries) {
        if (companies != null && !companies.isEmpty()) {
            companies_layout.setVisibility(View.VISIBLE);
            for (int i = 0; i < companies.size(); i++) {
                companies_tv.setText(companies.get(i) + "\n");
            }
        } else companies_layout.setVisibility(View.GONE);
        if (countries != null && !countries.isEmpty()) {
            for (int i = 0; i < countries.size(); i++) {
                countries_tv.setText(countries.get(i) + "\n");
            }
        } else countries_layout.setVisibility(View.GONE);
    }

    @Override
    public void showTaglineLayout() {
        tagline_layout.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideTaglineLayout() {
        tagline_layout.setVisibility(View.GONE);
    }

    @Override
    public void receiveTagline(String tagline) {
        Log.d(TAG, "receiveMainInfo: tagline = " + tagline);

        tagline_tv.setText(tagline);
    }

    Snackbar snack;

    @Override
    public void showSnackAlert(String s) {
        snack = Snackbar.make(((MainActivity) getActivity()).main_activity, s, Snackbar.LENGTH_INDEFINITE);
        snack.setAction("Re-try", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snack.dismiss();
                mMoviePresenter.onCreateView(ID);
            }
        });
        snack.show();
    }

    @Override
    public void receiveSourceOfDate(MoviePage moviePage) {
        toolbar = getActivity().findViewById(R.id.toolbar);
        TITLE = moviePage.getTitle();
        ID = moviePage.getMovie_id();
        initToolbar(toolbar, TITLE);
        if (moviePage.getImages() != null) {
            Log.d(TAG, "receiveSourceOfDate: moviePage.getImages(): " + moviePage.getImages());
        }
        dataSource = new Gson().toJson(moviePage);
        Log.d(TAG, "receiveSourceOfDate: received data = " + dataSource);
    }

    @Override
    public void showYoutubeDisabledDialog() {
        Log.d("TAG", "showYoutubeDisabledDialog: 1");
        final YouTubeInitializationResult result = YouTubeApiServiceUtil.isYouTubeApiServiceAvailable(getActivity());
        if (result != YouTubeInitializationResult.SUCCESS) { //
            //If there are any issues we can show an error dialog.
            result.getErrorDialog(getActivity(), 0).show();
        } else {
            Log.d("TAG", "showYoutubeDisabledDialog: 2");
        }
    }

    @Override
    public void showRatePd() {
        rating_pb.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideRatePd() {
        rating_pb.setVisibility(View.GONE);
    }

    @Override
    public void showMessage(String s) {
        Toast toast = Toast.makeText(getContext(), s, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    @Override
    public void hideRatingLayout() {
        rate_layout.setVisibility(View.GONE);
    }

    @Override
    public void showRatingLayout() {
        rate_layout.setVisibility(View.VISIBLE);
    }

    @Override
    public void disableRateButton() {
        btn_send_rate.setActivated(false);
        btn_send_rate.setEnabled(false);
    }

    @Override
    public void enableRateButton() {
        btn_send_rate.setActivated(true);
        btn_send_rate.setEnabled(true);
    }

    @Override
    public void showRecommendationsLayout() {
        layout_recommendations.setVisibility(View.VISIBLE);
    }

    @Override
    public void receiveRecommendations(List<MoviePosterAR> recommendations) {
        Log.d(TAG, "receiveRecommendations: 1");
        MovieRecommendationsAdapter mAdapter = new MovieRecommendationsAdapter(recommendations, getContext(), mMoviePresenter);
        recommendations_recyclerview.setHasFixedSize(true);
        recommendations_recyclerview.setAdapter(mAdapter);
        if (new Setting(getContext()).getPosterInfoView()) {
            mAdapter.showInfo();
        } else mAdapter.hideInfo();
    }

    @Override
    public void hideRecommendationsLayout() {
        layout_recommendations.setVisibility(View.GONE);
    }

    @Override
    public void moveToMovie(String movie_id, String title) {
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
    public void showImageSlider(int position, List<String> images) {
        MoviePage m = new Gson().fromJson(dataSource, MoviePage.class);
        ArrayList<String> i = (ArrayList<String>) images;
        getContext().startActivity(new Intent(getContext(), ImageSliderActivity.class)
                .putExtra("title", m.getOriginal_title())
                .putStringArrayListExtra("images", i)
                .putExtra("position", position)
        );
    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.movie_fragment_add_to_watchlist_btn) { // add button ?
            Log.d(TAG, "onClick: add 1");
            if (Common.isMovieAddedToWatchlist(ID, getContext())) {
                Log.d(TAG, "onClick: add  2 ");

                DBHelper watchlistDBHelper = new DBHelper(getContext()); // db declaration
                SQLiteDatabase sqLiteDatabase = watchlistDBHelper.getWritableDatabase();
                if (watchlistDBHelper.daleteFromWatchlist(sqLiteDatabase, ID)) {
                    Log.d(TAG, "onClick: add  3 ");

                    add_to_watchlist_btn.setText(getResources().getString(R.string.add_watchlist));
                    add_to_watchlist_btn.setBackground(getResources().getDrawable(R.drawable.add_to_watch_list_background));
                } else {
                    Log.d(TAG, "onClick: add 4");
                }
                watchlistDBHelper.close();
            } else {
                Log.d(TAG, "onClick: add 5");

                MoviePage m = new Gson().fromJson(dataSource, MoviePage.class);
                DBHelper watchlistDBHelper = new DBHelper(getContext()); // db declaration
                SQLiteDatabase sqLiteDatabase = watchlistDBHelper.getWritableDatabase();
                if (watchlistDBHelper.insertMovie(ID, TITLE, m.getPoster_path(), m.getGenres(), sqLiteDatabase)) {
                    Log.d(TAG, "onClick: add 6");
                    add_to_watchlist_btn.setText(getResources().getString(R.string.added_watchlist));
                    add_to_watchlist_btn.setBackground(getResources().getDrawable(R.drawable.added_to_watchlist));
                    Toast toast = Toast.makeText(getContext(), "Added to Watchlist!", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                } else {
                    Log.d(TAG, "onClick: add 7");
                }
                watchlistDBHelper.close(); // close connection to database.
            }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnMovieFragmentInteractionListener {
        // TODO: Update argument type and name
        void onMovieFragmentOpened();
    }
}
