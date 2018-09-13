package com.info.movies.fargments;


import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.info.movies.R;
import com.info.movies.constants.Common;


/**
 * A simple {@link Fragment} subclass.
 */
public class AboutFragment extends Fragment {
    private OnAboutFragmentInteractionListener mListener;


    public AboutFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnAboutFragmentInteractionListener) {
            mListener = (OnAboutFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnAboutFragmentInteractionListener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_about, container, false);
        // ButterKnife.bind(this,v);

        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
        params.setScrollFlags(0);  // clear all scroll flags

        Typeface style_bold = Typeface.createFromAsset(getActivity().getAssets(), "font/Quicksand-Bold.ttf");
        Typeface style_normal = Typeface.createFromAsset(getActivity().getAssets(), "font/Quicksand-Regular.ttf");

        TextView text_1 = v.findViewById(R.id.about_fragment_text_1);
        text_1.setTypeface(style_bold);
        TextView text_2 = v.findViewById(R.id.about_fragment_text_2);
        text_2.setTypeface(style_bold);

        TextView text_3 = v.findViewById(R.id.about_fragment_text_3);
        text_3.setTypeface(style_normal);

        TextView text_4 = v.findViewById(R.id.about_fragment_text_4);
        text_4.setTypeface(style_normal);

        TextView text_5 = v.findViewById(R.id.about_fragment_text_5);
        text_5.setTypeface(style_normal);

        TextView text_6 = v.findViewById(R.id.about_fragment_text_6);
        text_6.setTypeface(style_normal);

        TextView text_7 = v.findViewById(R.id.about_fragment_text_7);
        text_7.setTypeface(style_normal);

        TextView text_8 = v.findViewById(R.id.about_fragment_text_8);
        text_8.setTypeface(style_normal);

        TextView text_9 = v.findViewById(R.id.about_fragment_text_9);
        text_9.setPaintFlags(text_9.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        text_9.setTypeface(style_normal);


        TextView text_10 = v.findViewById(R.id.about_fragment_text_10);
        text_10.setTypeface(style_normal);

        LinearLayout thanks_layout = v.findViewById(R.id.about_fragment_thanks_layout);
        thanks_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://jeshoots.com")));
            }
        });
        LinearLayout share_app = v.findViewById(R.id.about_fragment_share_app);
        share_app.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.shareApp(getContext());
            }
        });
        LinearLayout rate_app = v.findViewById(R.id.about_fragment_rate_app);
        rate_app.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.rateApp(getContext());
            }
        });
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        mListener.onAboutFragmentOpened();
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem item_search = menu.findItem(R.id.action_search).setVisible(false);
        MenuItem item_genre = menu.findItem(R.id.action_genre).setVisible(false);
        MenuItem item_sort = menu.findItem(R.id.action_sort).setVisible(false);
        MenuItem item_select_date = menu.findItem(R.id.action_date_select).setVisible(false);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnAboutFragmentInteractionListener {
        // TODO: Update argument type and name
        void onAboutFragmentOpened();
    }

}
