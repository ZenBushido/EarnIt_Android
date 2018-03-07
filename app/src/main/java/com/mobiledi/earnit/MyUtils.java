package com.mobiledi.earnit;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import org.joda.time.DateTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by amitshekhar on 22/05/15.
 */
public class MyUtils {
    /**
     * Checks if user has internet connectivity
     *
     * @return
     */
    public  static DateTime converter(String date){
       String month = date.substring(0,3);
       Integer monthString;
       String day = date.substring(4,6);
       Integer dayString = Integer.parseInt(day);
       String year = date.substring(8,12);
        Integer yearString = Integer.parseInt(year);

        String tims = date.substring(13);

        monthString=1;
        switch (month) {
            case "Jan":  monthString = 1;
                break;
            case "Feb":  monthString = 2;
                break;
            case "Mar":  monthString = 3;
                break;
            case "Apr":  monthString = 4;
                break;
            case "May":  monthString = 5;
                break;
            case "Jun":  monthString = 6;
                break;
            case "Jul":  monthString = 7;
                break;
            case "Aug":  monthString = 8;
                break;
            case "Sep":  monthString = 9;
                break;
            case "Okt": monthString = 10;
                break;
            case "Nov": monthString = 11;
                break;
            case "Dec": monthString = 12;
                break;
            default:
                break;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm/DD/yyyy hh:mm:ss aaa");

        Date dateCurrent = null;
        try {
            dateCurrent = (Date) simpleDateFormat.parse(monthString+"/"+day+"/"+year+" "+tims);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        DateTime dateTime = new DateTime(dateCurrent);

        return dateTime;
    }
    public static boolean isInternetConnected(Context context) {
        final ConnectivityManager conMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo activeNetwork = conMgr.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected())
            return true;
        else
            return false;
    }
}
