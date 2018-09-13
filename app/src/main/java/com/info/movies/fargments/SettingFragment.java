package com.info.movies.fargments;


import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.info.movies.R;
import com.info.movies.constants.Common;
import com.info.movies.constants.Setting;
import com.info.movies.constants.local_notification.MyAlarm;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.info.movies.constants.Common.basic_geners;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingFragment extends Fragment implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    private static final String TAG = SettingFragment.class.getSimpleName();
    Context context;
    private OnSettingFragmentInteractionListener mListener;

    public SettingFragment() {
        // Required empty public constructor
    }

    @BindView(R.id.setting_average_rate_on_poster_checkBox)
    CheckBox average_rate_on_poster;
    @BindView(R.id.setting_average_rate_current_state)
    TextView average_rate_state;
    @BindView(R.id.setting_upcoming_notification_switch)
    SwitchCompat daily_notification_switch;
    @BindView(R.id.setting_daily_notification_subtitle)
    TextView daily_notification_subtitle;

    @BindView(R.id.setting_upcoming_notification_layout)
    LinearLayout upcoming_notification_layout;

    Setting mSetting;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnSettingFragmentInteractionListener) {
            mListener = (OnSettingFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnAboutFragmentInteractionListener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        this.context = getActivity();
        mSetting = new Setting(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_setting, container, false);
        vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);

        ButterKnife.bind(this, v);

        setAverageRateSetting(mSetting.getPosterInfoView());
        setDailyNotificationSetting(mSetting.getDailyNotificationState());
        average_rate_on_poster.setOnCheckedChangeListener(this);
        daily_notification_switch.setOnCheckedChangeListener(this);

        return v;
    }


    @Override
    public void onPrepareOptionsMenu(Menu menu) {

        MenuItem item_search = menu.findItem(R.id.action_search).setVisible(false);
        MenuItem item_sort = menu.findItem(R.id.action_sort).setVisible(false);
        MenuItem item_select_date = menu.findItem(R.id.action_date_select).setVisible(false);
        MenuItem item_genre = menu.findItem(R.id.action_genre).setVisible(false);

    }

    @Override
    public void onResume() {
        super.onResume();
        mListener.onSettingFragmentOpened();
    }

    private void setDailyNotificationSetting(boolean dailyNotificationState) {
        Log.d(TAG, "setDailyNotificationSetting: dailyNotificationState = " + dailyNotificationState);
        daily_notification_switch.setChecked(dailyNotificationState);
        if (dailyNotificationState) {
            daily_notification_subtitle.setText(R.string.setting_upcoming_notification_content_on);
            upcoming_notification_layout.setOnClickListener(this);
        } else {

            daily_notification_subtitle.setText(R.string.setting_upcoming_notification_content_off);
            upcoming_notification_layout.setOnClickListener(null);
        }
    }

    void setAverageRateSetting(boolean b) {
        average_rate_on_poster.setChecked(b);
        if (b) {
            average_rate_state.setText(context.getResources().getString(R.string.setting_movie_rate_shown));
        } else {
            average_rate_state.setText(context.getResources().getString(R.string.setting_movie_rate_hidden));
        }
    }

    Vibrator vibrator;

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (compoundButton.getId() == R.id.setting_average_rate_on_poster_checkBox) {
            Log.d(TAG, "onCheckedChanged: setting_average_rate_on_poster_checkBox = " + b);

            mSetting.savePosterInfoView(b);
            if (b) {
                average_rate_state.setText(context.getResources().getString(R.string.setting_movie_rate_shown));
            } else {
                average_rate_state.setText(context.getResources().getString(R.string.setting_movie_rate_hidden));
            }
        }
        if (compoundButton.getId() == R.id.setting_upcoming_notification_switch) {
            Log.d(TAG, "onCheckedChanged: trailer =  setting_upcoming_notification_switch value = " + b);
            mSetting.saveDailyNotificationState(b);
            if (b) {
                daily_notification_subtitle.setText(R.string.setting_upcoming_notification_content_on);
                MyAlarm.setAlarm(context, getResources().getInteger(R.integer.notification_alarm_set_trigger_at_minutes_power_on));

                upcoming_notification_layout.setOnClickListener(this);
                vibrator.vibrate(getActivity().getResources().getInteger(R.integer.setting_daily_notification_on_vibrate_milis));
            } else {
                MyAlarm.cancelAlarm(context);
                daily_notification_subtitle.setText(R.string.setting_upcoming_notification_content_off);
                upcoming_notification_layout.setOnClickListener(null);
            }
        }
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.setting_upcoming_notification_layout) {
            // from here i will help user to edit the selected genres .
            CreateAlertDialogWithRadioButtonGroup();
        }
    }

    int[] nums;

    private void CreateAlertDialogWithRadioButtonGroup() {
        boolean[] checkedItems = new boolean[basic_geners.size()];
        final ArrayList<Integer> temporaries = new ArrayList<>();
        nums = mSetting.getNotificationSavedGenres();

        if (nums != null) {

            for (int i : nums) {
                temporaries.add(i);
            }

            for (int i = 0; i < Common.basic_geners.size(); i++) {
                if (temporaries.contains(i)) {
                    checkedItems[i] = true;
                }
            }
        }
        Log.d(TAG, "CreateAlertDialogWithRadioButtonGroup: checkedItems = " + Arrays.toString(checkedItems));
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.MaterialThemeDialog);
        Log.d(TAG, "CreateAlertDialogWithRadioButtonGroup: selected array = " + temporaries);
        builder.setTitle("Favourite Genres");
        builder.setMultiChoiceItems(getResources().getStringArray(R.array.genres), checkedItems,
                new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {

                        Log.d(TAG, "onClick: itme no = " + which + " state  = " + isChecked);
                        if (isChecked) {
                            temporaries.add(which);
                            Log.d(TAG, "onClick: selectedItems array = " + temporaries);
                        } else {
                            if (temporaries.contains(which)) {
                                removeItem(temporaries, which);
                                Log.d(TAG, "onClick: selectedItems array = " + temporaries);
                            }
                        }
                    }
                }).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {

                Log.d(TAG, "onClick: ");
                mSetting.saveNotificationGenres(temporaries);
                dialog.dismiss();
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {

                dialog.dismiss();
            }
        });
        AlertDialog dialog_genre = builder.create();
        dialog_genre.show();
    }

    private void removeItem(ArrayList<Integer> l, int which) {
        for (int i = 0; i < l.size(); i++) {
            if (l.get(i) == which) {
                l.remove(i);
            }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnSettingFragmentInteractionListener {
        // TODO: Update argument type and name
        void onSettingFragmentOpened();
    }
}
