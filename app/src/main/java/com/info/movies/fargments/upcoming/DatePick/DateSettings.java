package com.info.movies.fargments.upcoming.DatePick;

import android.app.DatePickerDialog;
import android.content.Context;
import android.util.Log;
import android.widget.DatePicker;

import com.info.movies.constants.Common;
import com.info.movies.constants.Setting;
import com.info.movies.fargments.upcoming.UpcomingFragment;
import com.info.movies.fargments.upcoming.UpcommingPresenter;

import java.text.ParseException;
import java.util.Calendar;

import static com.info.movies.fargments.upcoming.UpcomingFragment.isDateChange;
import static com.info.movies.fargments.upcoming.UpcomingFragment.maximumDate;
import static com.info.movies.fargments.upcoming.UpcomingFragment.page_counter;

/**
 * Created by Ehab Salah A. LAtif on 6/17/2017.
 */

public class DateSettings implements DatePickerDialog.OnDateSetListener {
    protected int upcoming_genre;
    Context context;
    private UpcommingPresenter mUpcommingPresenter;
    private Setting setting;
    private String min_date;

    public DateSettings(Context context, UpcommingPresenter mUpcommingPresenter, String min_date) {
        this.context = context;
        this.mUpcommingPresenter = mUpcommingPresenter;
        this.min_date = min_date;
        this.setting = new Setting(context);
        // HERE INITIALIZE THE GENRE
        upcoming_genre = setting.getUpComingGnre();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Log.d("TAG", "onDateSet: 1 ");
        boolean isValidDate = false;
        // this method will be invoked when user set date by datePicker
        if (view.isShown()) {
            Log.d("TAG", "onDateSet: 2");

            if (!mUpcommingPresenter.noInternetLayoutVisibility()) {
                Log.d("TAG", "onDateSet: 3 ");


                String date_selected = year + "-" + (month + 1) + "-" + dayOfMonth;
                try {
                    Log.d("TAG", "onDateSet: 4");

                    isValidDate = Calendar.getInstance().getTime().before(Common.sDF.parse(date_selected)); // selected date after current date !
                } catch (ParseException e) {
                    Log.d("TAG", "onDateSet: 5 ");

                    e.printStackTrace();
                }
                if (isValidDate) {
                    Log.d("TAG", "onDateSet: 6 ");
                    UpcomingFragment.page_counter = 1;
                    maximumDate = date_selected;
                    setting.saveMaximumDateSF(date_selected);
                    mUpcommingPresenter.onDateSet(min_date, date_selected, page_counter, upcoming_genre);
                    isDateChange = true;
                } else {
                    Log.d("TAG", "onDateSet: 7 ");
                    mUpcommingPresenter.onInValidDateSelected();
                }
            }
        }
    }

}
