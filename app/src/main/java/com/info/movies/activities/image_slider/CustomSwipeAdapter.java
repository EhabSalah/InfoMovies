package com.info.movies.activities.image_slider;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.info.movies.R;
import com.info.movies.constants.GlideApp;

import java.util.ArrayList;

/**
 * Created by EhabSalah on 7/27/2017.
 */

public class CustomSwipeAdapter extends PagerAdapter {

    private static final String TAG = CustomSwipeAdapter.class.getSimpleName();
    private ImageSliderPresenter mPresenter;
    private ArrayList<String> images;
    Context ctx;
    LayoutInflater layoutInflater;
    int position;
    Activity activity;
    private GestureDetectorCompat mGestureDetector;

    public CustomSwipeAdapter(Context ctx, ArrayList<String> images, int position, ImageSliderPresenter mPresenter) {
        this.ctx = ctx;
        this.images = images;
        this.position = position;
        this.activity = (Activity) ctx;
        this.mPresenter = mPresenter;
        mGestureDetector = new GestureDetectorCompat(ctx, new GestureListener());

    }

    @Override
    public int getCount() { //return number of slides.
        return images.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {//in this method we have to make validation.
        return (view == object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        //from this method we have to return an object that will represent each of the swipe view.

        layoutInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.image_slider_layout, container, false);

        final SubsamplingScaleImageView imageView = view.findViewById(R.id.view_pager_slider_imageView);
        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mGestureDetector.onTouchEvent(event);
                return false;
            }
        });
        GlideApp.
                with(activity).
                asBitmap().
                diskCacheStrategy(DiskCacheStrategy.ALL).
                load("https://image.tmdb.org/t/p/w1280" + images.get(position)).
                into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        try {
                            imageCashed(position);
                            imageView.setImage(ImageSource.bitmap(resource));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
        container.addView(view);
        return view;
    }

    private void imageCashed(int position) {
        if (ImageSliderActivity.cashed_images == null) {
            ImageSliderActivity.cashed_images = new ArrayList<>();
        }
        if (!ImageSliderActivity.cashed_images.contains(position)) {
            ImageSliderActivity.cashed_images.add(position);
        }
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        /*from this method we have to destroy each of the slide if the user moved from one slide to
         another, we have to destroy the previous slide.
		THIS will Free Some of heap memory, and the application will work faster.*/
        Log.d(TAG, "destroyItem: ");
        container.removeView((LinearLayout) object);  // this is because our custom layout root element is a LinearLayout
    }


    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            Log.d(TAG, "onSingleTapUp: ");
            mPresenter.onSingleTapUp();
            return true;
        }
    }

}
