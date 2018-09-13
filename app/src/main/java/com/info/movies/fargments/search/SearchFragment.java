package com.info.movies.fargments.search;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.info.movies.MainActivity;
import com.info.movies.R;
import com.info.movies.adapters.ResultsRecyclerViewAdapter;
import com.info.movies.adapters.SuggestionRecyclerViewAdapter;
import com.info.movies.constants.Common;
import com.info.movies.constants.EndlessRecyclerViewScrollListener;
import com.info.movies.models.SearchSuggestionsModel;
import com.info.movies.fargments.movie.MovieFragment;
import com.info.movies.models.SearchResultsModel;

import java.util.ArrayList;

import butterknife.ButterKnife;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.info.movies.constants.Common.ADMOB_APP_ID;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment implements SearchView.OnQueryTextListener,
        MySearchView {

    public static final String TAG_VISIBILITY_RESULTS_RV = "visibility_rv_results";
    public static final String TAG_VISIBILITY_SUGGESTIONS_RV = "visibility_rv_suggestions";
    public static final String TAG_RESULTS_ARRAY = "results_array";
    public static final String TAG_DATE_BUNDLE = "data_bundle"; // ************** BUNDLE TAG
    public static final String TAG_SUGGESTIONS_ARRAY = "suggestions_array"; // ************** Array TAG
    public static final String TAG_RESULTS_PAGE_COUNTER = "results_page_counter";
    public static final String TAG_RESULTS_TOTAL_PAGES = "results_total_pages";
    public static final String TAG_LAST_SEARCH_FIELD_TEXT = "last_query_text";
    public static final String TAG_LAST_RESULT_QUERY_TEXT = "query_result_text";
    public static final String TAG_LAST_SUGGESTION_QUERY_TEXT = "query_suggestion_text";

    private Context context;
    private Activity activity;
    SearchView mSearchView;
    ImageView closeButton;
    EditText search_field;

    //    @BindView(R.id.suggestion_rv)
    RecyclerView suggestion_rv;

    //    @BindView(R.id.result_rv)
    RecyclerView result_rv;

    //  @BindView(R.id.search_pb)
    ProgressBar search_pb;

    SearchPresenter mSearchPresenter;
    private int total_pages;

    private RecyclerView.Adapter suggestionAdapter;
    private RecyclerView.LayoutManager suggestionLayoutManager;
    private RecyclerView.LayoutManager resultLayoutManager;
    private ResultsRecyclerViewAdapter resultAdapter;
    protected ArrayList<SearchResultsModel> newResults;
    protected ArrayList<SearchSuggestionsModel> newSuggestion;

    boolean createdStateInDestroyView; // // ************** to detect  when save occures
    private Bundle savedData = null; // ************** BUNDLE

    private TextView text_no_result;
    private int page_counter = 1;
    public View v;

    String last_search_field_text;
    String query_submit_variable;
    String query_change_variable;
    String suggestions_keyword;
    String results_keyword;

    private boolean isHideKsyboard;

    LinearLayout no_internet_layout;
    private boolean isResultReceiving;
    private boolean isSubmitClicked;
    private boolean isSuggestionClicked;
    private boolean isStartUp;
    private boolean isOnLoadMore;
    private OnSearchFragmentInteractionListener mListener;

    public SearchFragment() {
        // Required empty public constructor
        mSearchPresenter = new SearchPresenterIMP(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnSearchFragmentInteractionListener) {
            mListener = (OnSearchFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnAboutFragmentInteractionListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_search, container, false);
        ButterKnife.bind(this, v);
        this.v = v;

//        ((MainActivity) getActivity()).enableBackButton(true);
//        ((MainActivity) getActivity()).lockDrawer(true);
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);

        mSearchView = toolbar.findViewById(R.id.mySearchView);
        mSearchView.setVisibility(View.VISIBLE); // hide the search view
        TextView title = toolbar.findViewById(R.id.toolbar_title);
        title.setVisibility(View.GONE);
        ((MainActivity) getActivity()).showAppBarIfCollapsed();
        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
        params.setScrollFlags(0);  // clear all scroll flags

        suggestion_rv = v.findViewById(R.id.suggestion_rv);
        result_rv = v.findViewById(R.id.result_rv);
        search_pb = v.findViewById(R.id.search_pb);
        text_no_result = v.findViewById(R.id.no_result_found_text);
        no_internet_layout = v.findViewById(R.id.no_internet_layout);
        mSearchView = toolbar.findViewById(R.id.mySearchView);

        closeButton = mSearchView.findViewById(R.id.search_close_btn);
        /*int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 36, getResources().getDisplayMetrics());
        int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 36, getResources().getDisplayMetrics());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, height);
        closeButton.setLayoutParams(layoutParams);
        closeButton.setForegroundGravity();*/

        search_field = mSearchView.findViewById(R.id.search_src_text);
        search_field.setSingleLine(true);
        search_field.setEllipsize(TextUtils.TruncateAt.END);
        search_field.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Log.d("TAG", "onTouch: ");
                handleOnSearchFieldClick();
                return false;
            }
        });
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeButtonClick();
            }
        });
        mSearchView.setOnQueryTextListener(this);
        search_field.setImeOptions(EditorInfo.IME_ACTION_SEARCH | EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        search_field.setHighlightColor(getResources().getColor(R.color.colorAccent));
            /*search_field.getBackground().setColorFilter(getResources().getColor(R.color.colorAccent),
                PorterDuff.Mode.SRC_ATOP);*/
       /* try {*//*android:textCursorDrawable="@drawable/color_cursor"*//*
            // https://github.com/android/platform_frameworks_base/blob/kitkat-release/core/java/android/widget/TextView.java#L562-564
            Field f = TextView.class.getDeclaredField("mCursorDrawableRes");
            f.setAccessible(true);
            f.set(search_field, R.drawable.color_cursor);
        } catch (Exception ignored) {

        }*/
        hideSearchLogo(mSearchView);
        suggestionSetup();
        resultSetup();
        if (savedData != null && text_no_result.getVisibility() != VISIBLE) {
            if (newResults != null) {
                Log.d("TAG", "onCreateView: search fragment popped up (screen rotated) ");
                mSearchPresenter.onReCreateView(savedData);
            } else {
                Log.d("TAG", "onCreateView: search fragment popped up ");
                mSearchPresenter.onReCreateView(savedData);
                savedData = null;
            }
        } else if (savedData != null && text_no_result.getVisibility() == VISIBLE) {
            Log.d("TAG", "onCreateView: no results found is visible ");
        } else {
            Log.d("TAG", "onCreateView: startup");
            isStartUp = true;
        }

        mSearchView.setFocusable(false); // lock Search View Focus , maloosh lzma nchta3'al bl edit text focus bas
        initAdView(v, getActivity()); // AD MOB

        return v;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("TAG", "onCreate: Search Fragment ");
        setHasOptionsMenu(true);
        savedData = null;
        newSuggestion = new ArrayList<>();
        newResults = new ArrayList<>();
        if (savedInstanceState != null) {
            Log.d("TAG", "onCreate: savedInstanceState != null ");
            savedData = savedInstanceState.getBundle(TAG_DATE_BUNDLE);
        }
        context = getContext();
        activity = getActivity();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (isStartUp) {
            search_field.setText("");
            search_field.requestFocus();
            // here i need to open the soft keyboard !! :(
            isStartUp = false;
            Log.d("TAG", "onViewCreated: startup");
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        MenuItem item_search = menu.findItem(R.id.action_search).setVisible(false);
        MenuItem item_sort = menu.findItem(R.id.action_sort).setVisible(false);
        MenuItem item_select_date = menu.findItem(R.id.action_date_select).setVisible(false);
        MenuItem item_genre = menu.findItem(R.id.action_genre).setVisible(false);
    }

    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
        hideKeyboardFrom(context, v);
    }

    @Override
    public void onResume() {
        if (mAdView != null) {
            mAdView.resume();
        }
        super.onResume();
        Log.d("TAG", "onResume: Search Fragment 1");
        mListener.onSearchFragmentOpened();
        if (result_rv.getVisibility() != VISIBLE || search_field.getText().toString().isEmpty()) {
            Log.d("TAG", "onResume: Search Fragment 2 ");
            search_field.requestFocus();
            search_field.setCursorVisible(true);
            Common.showKeyboard(search_field, context);
        }
        //result_rv.getVisibility()== VISIBLE && resultAdapter.getItemCount()
        if (isOnLoadMore && result_rv.getVisibility() == VISIBLE) {
            Log.d("TAG", "onResume: Search Fragment 3 ");
            resultSetup();
            resultLayoutManager.scrollToPosition(newResults.size() - 5);
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        String s = query.trim().replaceAll("\\s+", " ");
        if (!s.isEmpty()) {
            if (s.equals(results_keyword) && !newResults.isEmpty()) { // load saved
                showResultList();
                hideSuggestionList();
                search_field.clearFocus();
                search_field.setCursorVisible(false);
            } else {
                newResults.clear();
                resultSetup();
                page_counter = 1;
                mSearchPresenter.onSubmit(query, page_counter);
                isOnLoadMore = false;
            }
            query_submit_variable = s;
            isSubmitClicked = true;
            //hideSearchFieldCursor();
            // search_field.setFocusable(false);
        }

        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        Log.d("TAG", "onQueryTextChange: 1");
        String s = newText.trim().replace("\\s+", " ");
        if (isResultReceiving) {
            Log.d("TAG", "onQueryTextChange: 2");
            isResultReceiving = false;
        } else if (isSuggestionClicked) {
            Log.d("TAG", "onQueryTextChange: 3");
            isSuggestionClicked = false;
        } else {
            Log.d("TAG", "onQueryTextChange: 4");

            if (!s.isEmpty()) {
                Log.d("TAG", "onQueryTextChange: 5");

                if (!s.equals(suggestions_keyword)) {
                    Log.d("TAG", "onQueryTextChange: 6");

                    if (s.length() > 1 && s.length() <= 100) {
                        Log.d("TAG", "onQueryTextChange: 7");

                        refreshSuggestions();
                        mSearchPresenter.onTexting(s, 1);
                    } else { // emptied Or length < 1 or >=100
                        Log.d("TAG", "onQueryTextChange: 8");
                        mSearchPresenter.onEmptiedSearchView();
                        // refreshSuggestions();
                    }
                    query_change_variable = s;
                    Log.d("TAG", "onQueryTextChange: 9");
                } else {
                    Log.d("TAG", "onQueryTextChange: 10");
                    if (suggestion_rv.getVisibility() != VISIBLE) {
                        Log.d("TAG", "onQueryTextChange: 11");
                        suggestion_rv.setVisibility(VISIBLE);
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void showResultList() {
        result_rv.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideResultList() {
        result_rv.setVisibility(GONE);
    }

    @Override
    public void showSuggestionList() {
        suggestion_rv.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideSuggestionList() {
        suggestion_rv.setVisibility(GONE);
    }

    @Override
    public void showProgress() {
        search_pb.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        search_pb.setVisibility(View.GONE);
    }

    @Override
    public void recieveErrorMessage(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void receiveSuggestions(ArrayList<SearchSuggestionsModel> suggestions, String keyword) {
        Log.d("TAG", "receiveSuggestions: 1");

        Log.d("TAG", "receiveSuggestions: suggestions.size = " + suggestions.size());
        if (keyword != null) {
            Log.d("TAG", "receiveSuggestions: 2");
            if (!suggestions.isEmpty()) {
                Log.d("TAG", "receiveSuggestions: 3");
                // pass the data to the list array
                newSuggestion.clear();
                newSuggestion.addAll(suggestions);
                suggestionAdapter.notifyDataSetChanged();
            }
            suggestions_keyword = keyword;
        } else {// server implementation
            Log.d("TAG", "receiveSuggestions: 4");
            if (!suggestions.isEmpty()) {
                Log.d("TAG", "receiveSuggestions: 5");
                newSuggestion.clear();
                newSuggestion.addAll(suggestions);
                suggestionAdapter.notifyDataSetChanged();
            }
            suggestions_keyword = query_change_variable;
        }
    }

    @Override
    public void receiveResults(ArrayList<SearchResultsModel> results, int pages, int results_page_counter, String result_keyword) {
        //  resultSetup();
        Log.d("TAG", "receiveResults: pages =" + pages + " page counter= " + page_counter);
        Log.d("TAG", "receiveResults: " + results.size());
        this.total_pages = pages;
        if (results_page_counter != 0) {
            Log.d("TAG", "receiveResults: saved data ");
            isResultReceiving = true;
            results_keyword = result_keyword;
            query_submit_variable = result_keyword;
            resultSetup();
            this.page_counter = results_page_counter - 1;
            // recieve saved data
            newResults.addAll(results);
        } else if (isOnLoadMore) {
            Log.d("TAG", "receiveResults: from server load more");
            // receive data from server
            newResults.addAll(results);
            results_keyword = query_submit_variable;
            isOnLoadMore = false;
        } else {
            Log.d("TAG", "receiveResults: from server ");
            // receive data from server
            newResults.addAll(results);
            results_keyword = query_submit_variable;
        }
        if (page_counter < pages) {
            resultAdapter.showFooter();
        } else {
            resultAdapter.hideFooter();
        }
        page_counter++;
        if (result_rv.getVisibility() == View.VISIBLE) {
            resultAdapter.notifyDataSetChanged();
        }
    }

    public void showNoInternetSnak() {
        final Snackbar snack;
        snack = Snackbar.make(result_rv, "Please check your internet connection and try again !", 10000);
        snack.setAction("ok", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snack.dismiss();
            }
        });
        snack.show();
    }

    @Override
    public void showNo(String message) {
        if (message == null) { // no internet
            Log.d("TAG", "showNo: No internet ");
            if (result_rv.getVisibility() == View.VISIBLE) // results visible
            {
                Log.d("TAG", "results visible: no internet");
                resultAdapter.hideFooter();
                resultAdapter.notifyDataSetChanged();
                showNoInternetSnak();
            } else if (suggestion_rv.getVisibility() == View.VISIBLE) // suggestions visible
            {
                Log.d("TAG", "suggestions visible: no internet");
                newSuggestion.clear();
                suggestions_keyword = null;
                suggestion_rv.setVisibility(GONE);
            } else if (query_change_variable.equals(query_submit_variable) && isSubmitClicked) // when submit clicked
            {
                Log.d("TAG", "submit clicked: no internet");
                isSubmitClicked = false;
                Toast.makeText(context, "Please check internet connection", Toast.LENGTH_SHORT).show();
            }
        } else {
            if (message == "No results found") {
                text_no_result.setText(message);
                text_no_result.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void hideNoResultFoundText() {
        text_no_result.setVisibility(GONE);
    }

    @Override
    public void searchClickedSuggestion(String suggested_title) { // howa da l by7sl...
        hideKeyboardFrom(context, v);
        onQueryTextSubmit(suggested_title);
        isSuggestionClicked = true;
        search_field.setText(suggested_title);
        search_field.clearFocus();
        search_field.setCursorVisible(false);
    }

    @Override
    public void changeSearchFieldText(String suggested_title) {
        mSearchView.setQuery(suggested_title, false);
    }

    private void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.d("TAG", "onSaveInstanceState: ");

        if (newSuggestion == null && newResults == null) {
            outState.putBundle(TAG_DATE_BUNDLE, savedData); // ************** put saved bundle by onDestroyView here.
        } else {
            Log.d("TAG", "savingData: onSaveInstanceState ");
            outState.putBundle(TAG_DATE_BUNDLE, createdStateInDestroyView ? savedData : saveData()); // ************** if list is not null (= screen not rotated & onDestroyview is not reached, then save from here)
        }
        createdStateInDestroyView = false;
        mSearchPresenter.onSaveInstanceState();
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroyView() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroyView();
        Log.d("TAG", "onDestroyView: SearchFrag. ");
        Log.d("TAG", "savingData: onDestroyView ");
        savedData = saveData(); // ************** BUT SAVES BUNDLE in savedState
        createdStateInDestroyView = true; // ************** flag that something saves
        newSuggestion = null; // ************** clear
        newResults = null; // ************** clear
        v = null;
    }

    @Override
    public boolean stateOfSavedState() { // detect weather saves data or not
        if (savedData == null || savedData.getParcelableArrayList(TAG_SUGGESTIONS_ARRAY) == null) {
            return false;
        } else return true;
    }

    @Override
    public void showNoInternetLayout() {
        no_internet_layout.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideNoInternetLayout() {
        no_internet_layout.setVisibility(GONE);
    }

    @Override
    public void hideCursor() {
        Common.hideSoftKeyboard(getActivity());
        search_field.setCursorVisible(false);
        search_field.clearFocus();
    }

    @Override
    public void showCursor() {
        Log.d("TAG", "showCursor: search field is focusable ");
        search_field.requestFocus();
        search_field.setCursorVisible(true);
        Log.d("TAG", "showCursor: search field focused");
    }

    @Override
    public void moveToMovieFragment(String id, String movie_title) {

        MovieFragment mMovieFragment = new MovieFragment();
        Bundle bundle = new Bundle();
        bundle.putString("movie_id_key", id);
        bundle.putString("movie_title_key", movie_title);
        mMovieFragment.setArguments(bundle);
        getActivity().getSupportFragmentManager().
                beginTransaction().
                replace(R.id.main_container, mMovieFragment).
                addToBackStack(null).
                commit();
// when i pop a fragment from backstack, fragment life cycle start from onCreateView()
    }

    private Bundle saveData() {
        Log.d("TAG", "saveData: ");
        Bundle state = new Bundle();
        state.putParcelableArrayList(TAG_SUGGESTIONS_ARRAY, newSuggestion);
        state.putParcelableArrayList(TAG_RESULTS_ARRAY, newResults);
        state.putInt(TAG_VISIBILITY_RESULTS_RV, result_rv.getVisibility());
        state.putInt(TAG_VISIBILITY_SUGGESTIONS_RV, suggestion_rv.getVisibility());
        state.putInt(TAG_RESULTS_PAGE_COUNTER, page_counter);
        state.putInt(TAG_RESULTS_TOTAL_PAGES, total_pages);
        String sField_text = search_field.getText().toString().trim().replace("\\s+", " ");
        if (!sField_text.equals("") || sField_text.length() < 1) {
            state.putString(TAG_LAST_SEARCH_FIELD_TEXT, sField_text);
        } else state.putString(TAG_LAST_SEARCH_FIELD_TEXT, null);

        state.putString(TAG_LAST_RESULT_QUERY_TEXT, results_keyword); //  results list  keyword
        state.putString(TAG_LAST_SUGGESTION_QUERY_TEXT, suggestions_keyword); //  suggestions list  keyword
        return state;
    }

    private void refreshSuggestions() {
        newSuggestion.clear();
        suggestionAdapter.notifyDataSetChanged();
    }

    private void hideSearchLogo(SearchView mSearchView) {
        ImageView searchViewIcon = mSearchView.findViewById(android.support.v7.appcompat.R.id.search_mag_icon);
        if (searchViewIcon != null) {
            searchViewIcon.setFocusable(false);
            ViewGroup linearLayoutSearchView = (ViewGroup) searchViewIcon.getParent();
            linearLayoutSearchView.removeView(searchViewIcon);
        }
    }

    private void suggestionSetup() {
        suggestionLayoutManager = new GridLayoutManager(getActivity(), 1);
        suggestion_rv.setLayoutManager(suggestionLayoutManager);
        suggestion_rv.setHasFixedSize(true);
        if (newSuggestion == null) {
            newSuggestion = new ArrayList<>();
        }
        suggestionAdapter = new SuggestionRecyclerViewAdapter(newSuggestion, getActivity(), mSearchPresenter);
        suggestion_rv.setAdapter(suggestionAdapter);
        suggestion_rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                hideKeyboardFrom(context, recyclerView);
                //search_field.setCursorVisible(false);
            }
        });
    }

    private void resultSetup() {
        resultLayoutManager = new LinearLayoutManager(getActivity());
        result_rv.setLayoutManager(resultLayoutManager);
        result_rv.setHasFixedSize(true);
        if (newResults == null) {
            newResults = new ArrayList<>();
        }
        resultAdapter = new ResultsRecyclerViewAdapter(newResults, getContext(), mSearchPresenter, true);
        result_rv.setAdapter(resultAdapter);
        result_rv.addOnScrollListener(new EndlessRecyclerViewScrollListener((LinearLayoutManager) resultLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {

                if (page_counter <= total_pages) {
                    mSearchPresenter.onLoadMore(results_keyword, page_counter);
                    isOnLoadMore = true;
                } else {
                    resultAdapter.hideFooter();
                    resultAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                hideKeyboardFrom(context, recyclerView);
                search_field.setCursorVisible(false);
            }

        });

    }

    private void closeButtonClick() {
        Log.d("TAG", "closeButtonClick: ");
        search_field.setText("");
        search_field.requestFocus();
        search_field.setCursorVisible(true);
        mSearchView.setQuery("", false);
        // closeButton.setEnabled(false);
        //Common.showKeyboard(search_field, context);
        mSearchPresenter.onCloseButtonClick();


    }

    private void handleOnSearchFieldClick() {
        String mSearchField = search_field.getText().toString().trim().replace("\\s+", " ");
        hideResultList();
        hideNoResultFoundText();
        if (mSearchField.equals(suggestions_keyword) && newSuggestion != null && !newSuggestion.isEmpty()) // suggestions  exists
        {
            Log.d("TAG", "onClick:search field : suggestions exist ");
            mSearchPresenter.onSearchFieldClickedSuggestionsExist();
        } else if (text_no_result.getVisibility() == VISIBLE) // no result on ui
        {
            Log.d("TAG", "onClick:search field: no results text visible ");
            search_field.requestFocus();
            search_field.setCursorVisible(true);
        } else // data not exist, go make request
        {
            Log.d("TAG", "onClick: search field");
            suggestions_keyword = null;
            onQueryTextChange(mSearchField);
            search_field.requestFocus();
            search_field.setCursorVisible(true);
        }
    }
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnSearchFragmentInteractionListener {
        // TODO: Update argument type and name
        void onSearchFragmentOpened();
    }

    com.google.android.gms.ads.AdView mAdView;
    private static final String TAG = "SearchFragment";
    private void initAdView(View v, Activity activity) {
        Log.d(TAG, "initAdView: ");
        MobileAds.initialize(activity, ADMOB_APP_ID);
        mAdView = v.findViewById(R.id.adView_search_fragment);
        if (mAdView!=null) {
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

}
