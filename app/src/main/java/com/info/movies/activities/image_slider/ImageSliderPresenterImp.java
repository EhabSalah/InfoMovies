package com.info.movies.activities.image_slider;

/**
 * Created by Ehab Salah on 4/18/2018.
 */

class ImageSliderPresenterImp implements ImageSliderPresenter {
    ImageSliderActivity mView;
    public ImageSliderPresenterImp(ImageSliderActivity view) {
        this.mView=view;
    }


    @Override
    public void onDestroy() {
        mView=null;
    }

    @Override
    public void onSingleTapUp() {
        if (mView!=null) {
            mView.showHideSystemUI();
        }
    }

}
