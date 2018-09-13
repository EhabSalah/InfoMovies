package com.info.movies.activities.image_slider;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.info.movies.R;
import com.info.movies.constants.GlideApp;
import com.info.movies.models.movie_page.ListItemCastCrew;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ImageSliderActivity extends AppCompatActivity implements ImageSliderView,
        View.OnSystemUiVisibilityChangeListener {
    private static final String TAG = ImageSliderActivity.class.getSimpleName();
    public static ArrayList<Integer> cashed_images;
    ViewPager viewPager;
    CustomSwipeAdapter adapter;
    String title;
    ArrayList<String> images;
    List<ListItemCastCrew> cast;
    ImageSliderPresenter mPresenter;
    View decorView;
    RelativeLayout toolbar;
    private boolean isSystemUIHidden;
    private boolean isToolbarHidden;
    private boolean isImage;
    private boolean isImageReady;
    String image;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_slider);
        init();
        initBars(savedInstanceState);
    }

    @Override
    protected void onPause() {
        super.onPause();
//        setIconTextColorOfRecentAppState(this);
    }

    private void initBars(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            Log.d(TAG, "initBars: RECREATE");
            isSystemUIHidden = savedInstanceState.getBoolean("isSystemUIHidden");
            isToolbarHidden = savedInstanceState.getBoolean("isToolbarHidden");


            if (savedInstanceState.getBoolean("isSystemUIHidden")) {
                hideSystemUI();
            } else showSystemUI();

            if (isToolbarHidden) {
                hideToolbar();
            } else showToolbar(isSystemUIHidden);

        } else {
            showSystemUI();
            showToolbar(isToolbarHidden);
        }
    }

    private void init() {
        cashed_images = new ArrayList<>();
        mPresenter = new ImageSliderPresenterImp(this);
        if (getIntent().getStringExtra("cast") != null && !TextUtils.isEmpty(getIntent().getStringExtra("cast"))) {
            cast = new Gson().fromJson(getIntent().getStringExtra("cast"), new TypeToken<List<ListItemCastCrew>>() {
            }.getType());
            Log.d(TAG, "init: cast = " + new Gson().toJson(cast));
        }
        title = getIntent().getStringExtra("title");
        images = getIntent().getStringArrayListExtra("images");
        image = getIntent().getStringExtra("image");
        final int position = getIntent().getIntExtra("position", 0);

        if (image != null && !TextUtils.isEmpty(image)) {
            isImage = true;
            Log.d(TAG, "init: image = " + image);
            final SubsamplingScaleImageView imageView = findViewById(R.id.imageView);
            final GestureDetectorCompat mGestureDetector = new GestureDetectorCompat(this, new GestureListener());

            imageView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    mGestureDetector.onTouchEvent(event);
                    return false;
                }
            });
            imageView.setVisibility(View.VISIBLE);
            GlideApp.
                    with(this).
                    asBitmap().
                    load("https://image.tmdb.org/t/p/w1280" + image).
                    into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            isImageReady = true;
                            try {
                                imageView.setImage(ImageSource.bitmap(resource));
                            } catch (Exception e) {
                                imageView.setImage(ImageSource.resource(R.drawable.movie_images_placeholder));
                                e.printStackTrace();
                            }
                        }
                    });

        } else if (images != null && !images.isEmpty()) {
            isImage = false;
            viewPager = findViewById(R.id.view_pager);
            viewPager.setVisibility(View.VISIBLE);
            adapter = new CustomSwipeAdapter(this, images, position, mPresenter);
            viewPager.setAdapter(adapter);
            viewPager.setCurrentItem(position);
        }

        toolbar = findViewById(R.id.custom_toolbar);
        decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(this);
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            Log.d(TAG, "onSingleTapUp: ");
            mPresenter.onSingleTapUp();
            return true;
        }
    }

    @Override
    public void showHideSystemUI() {
        if (isSystemUIHidden) {
            showSystemUI();
        } else {
            hideSystemUI();
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            if (isToolbarHidden) {
                showToolbar(true);

            } else {
                hideToolbar();
            }

        }
    }

    private void hideToolbar() {
        Log.d(TAG, "hideToolbar: ");
        toolbar.setVisibility(View.GONE);
        isToolbarHidden = true;
    }

    private void showToolbar(boolean isSystemUIHidden) {
        Log.d(TAG, "showToolbar: ");
        CoordinatorLayout.MarginLayoutParams params = (CoordinatorLayout.LayoutParams) toolbar.getLayoutParams();

        if (!isSystemUIHidden) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                if (getScreenOrientation() == 1) {
                    Log.d(TAG, "showToolbar: 1");
                    params.setMargins(convertDpIntoPixels(16), convertDpIntoPixels(16), convertDpIntoPixels(16)/*dp*/, 0);
                } else {
                    Log.d(TAG, "showToolbar: 2");
                    if (hasNavBar(getResources())) {
                        Log.d(TAG, "showToolbar: 3");
                        params.setMargins(convertDpIntoPixels(16), convertDpIntoPixels(16), convertDpIntoPixels(16)/*dp*/, 0);
                    } else {
                        Log.d(TAG, "showToolbar: 4");
                        params.setMargins(convertDpIntoPixels(16), convertDpIntoPixels(16), convertDpIntoPixels(16)/*dp*/, 0);
                    }

                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                if (getScreenOrientation() == 1) {
                    Log.d(TAG, "showToolbar: 1");
                    params.setMargins(convertDpIntoPixels(16), getStatusBarHeight() + convertDpIntoPixels(16), convertDpIntoPixels(16)/*dp*/, 0);
                } else {
                    Log.d(TAG, "showToolbar: 2");
                    if (hasNavBar(getResources())) {
                        Log.d(TAG, "showToolbar: 3");
                        params.setMargins(convertDpIntoPixels(16), getStatusBarHeight() + convertDpIntoPixels(16), getNavigationBarHeight() + convertDpIntoPixels(16)/*dp*/, 0);
                    } else {
                        Log.d(TAG, "showToolbar: 4");
                        params.setMargins(convertDpIntoPixels(16), getStatusBarHeight() + convertDpIntoPixels(16), convertDpIntoPixels(16)/*dp*/, 0);
                    }

                }
            }
        }

        toolbar.requestLayout();
        toolbar.setVisibility(View.VISIBLE);
        this.isToolbarHidden = false;
    }

    void showSystemUI() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Log.d(TAG, "showSystemUI: 1");
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN

            );
        }
    }

    void hideSystemUI() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Log.d(TAG, "hideSystemUI: 1");
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE |     //Stable when using multiple flags
                            View.SYSTEM_UI_FLAG_IMMERSIVE |
                            View.SYSTEM_UI_FLAG_FULLSCREEN | //Full screen mode
                            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | //avoid artifacts when FLAG_FULLSCREEN
                            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            );
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                findViewById(android.R.id.content).setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("isSystemUIHidden", isSystemUIHidden);
        outState.putBoolean("isToolbarHidden", isToolbarHidden);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.onDestroy();

    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(this, message + "", Toast.LENGTH_SHORT).show();
    }

    private int convertDpIntoPixels(int i) {
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, i, getResources().getDisplayMetrics());
        return Math.round(px);
    }

    public boolean hasNavBar(Resources resources) {
        int id = resources.getIdentifier("config_showNavigationBar", "bool", "android");
        return id > 0 && resources.getBoolean(id);
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public int getNavigationBarHeight() {
        int navigationBarHeight = 0;
        int resourceId = getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            navigationBarHeight = getResources().getDimensionPixelSize(resourceId);
        }
        return navigationBarHeight;
    }

    public int getScreenOrientation() {
        return getResources().getConfiguration().orientation; // 1 port 2 land
    }

    @Override
    public void onSystemUiVisibilityChange(int visibility) {
        if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {// TODO: The system bars are visible.
            Log.d(TAG, "onSystemUiVisibilityChange:system bars ARE VISIBLE ");
            isSystemUIHidden = false;
            showToolbar(isSystemUIHidden);
        } else // TODO: The system bars are not visible.
        {
            Log.d(TAG, "onSystemUiVisibilityChange:system bars ARE NOT VISIBLE ");
            isSystemUIHidden = true;
            hideToolbar();
        }
    }

    private static final int REQUEST = 112;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    saveImageToDeviceStorage();
                } else {
                    Toast.makeText(this, getResources().getString(R.string.app_name)+" was not allowed to save in your storage!", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public void btns_click(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                onBackPressed();
                break;
            case R.id.btn_download:
                if (Build.VERSION.SDK_INT >= 23) {
                    String[] PERMISSIONS = {android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
                    if (!hasPermissions(this, PERMISSIONS)) {
                        ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST);
                    } else {
                        //do here
                        saveImageToDeviceStorage();
                    }
                } else { // <M
                    //do here
                    saveImageToDeviceStorage();
                }
                break;
        }
    }

    private void saveImageToDeviceStorage() {
        if (isImage) {
            if (isImageReady) {
                GlideApp.
                        with(this).
                        asBitmap().
                        load("https://image.tmdb.org/t/p/w1280" + image).
                        into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                Log.d(TAG, "onResourceReady: ");
                                try {
                                    saveImage(resource);
                                } catch (Exception x) {
                                    x.printStackTrace();
                                }

                            }
                        });
            } else {
                Toast.makeText(this, "Image not ready!", Toast.LENGTH_SHORT).show();
            }

        } else {
            if (cashed_images.contains(viewPager.getCurrentItem())) {
                Log.d(TAG, "saveImageToDeviceStorage: 1");
                GlideApp.
                        with(this).
                        asBitmap().
                        load("https://image.tmdb.org/t/p/w1280" + images.get(viewPager.getCurrentItem())).
                        into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                Log.d(TAG, "onResourceReady: ");
                                try {
                                    saveImage(resource);
                                } catch (Exception x) {
                                    x.printStackTrace();
                                }
                            }
                        });
            } else {
                Toast.makeText(this, "Image not ready!", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void saveImage(Bitmap imageToSave) {
        new saveTask().execute(imageToSave);
    }

    private class saveTask extends AsyncTask<Bitmap, Void, String> {

        @Override
        protected String doInBackground(Bitmap... bitmaps) {
            String fileName = title + "_" + System.currentTimeMillis() + ".jpg";

            File direct = new File(Environment.getExternalStorageDirectory() + "/" + getString(R.string.app_name));

            if (!direct.exists()) {
                Log.d(TAG, "saveImage: 1");
                File wallpaperDirectory = new File("/sdcard/" + getString(R.string.app_name) + "/");
                wallpaperDirectory.mkdirs();
            }

            File file = new File(new File("/sdcard/" + getString(R.string.app_name) + "/"), fileName);
            if (file.exists()) {
                Log.d(TAG, "saveImage: 2");
                file.delete();
            }
            try {
                Log.d(TAG, "saveImage: 3");

                FileOutputStream out = new FileOutputStream(file);
                bitmaps[0].compress(Bitmap.CompressFormat.JPEG, 100, out);
                out.flush();
                out.close();

                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.TITLE, fileName);
                values.put(MediaStore.Images.Media.DESCRIPTION, "Image from " + title + " movie.");
                values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
                values.put(MediaStore.Images.Media.BUCKET_ID, file.toString().toLowerCase(Locale.US).hashCode());
                values.put(MediaStore.Images.Media.BUCKET_DISPLAY_NAME, file.toString().toLowerCase(Locale.US).hashCode());
                values.put("_data", file.getAbsolutePath());

                ContentResolver cr = getContentResolver();
                cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                return "Image saved";
            } catch (Exception e) {
                Log.d(TAG, "saveImage: 4");
                e.printStackTrace();
                return "Image not saved!";
            }
        }

        @Override
        protected void onPostExecute(String aVoid) {
            Toast.makeText(ImageSliderActivity.this, aVoid, Toast.LENGTH_SHORT).show();
        }
    }

}
