package com.creative.seong.app.util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.creative.seong.app.BuildConfig;
import com.creative.seong.app.menu.MainActivity;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by GS on 2016-12-09.
 */
public class UtilClass {

    public static void goHome(Activity activity) {
        Intent intent = new Intent(activity, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(intent);
    }

    //현재날짜,시간
    public static String getCurrentDate(int gubun) {
        int year, month, day, hour, plus_hour, minute, plus_minute;

        GregorianCalendar calendar = new GregorianCalendar();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day= calendar.get(Calendar.DAY_OF_MONTH);
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);
        calendar.add(Calendar.MINUTE, 30);
        plus_minute = calendar.get(Calendar.MINUTE);

        if(minute > 29){
            if(hour==23){
                plus_hour= 0;
            }else{
                plus_hour= hour+1;
            }
        }else{
            plus_hour= hour;
        }

        String returnData;
        if(gubun==1){
            String _month= UtilClass.addZero(month+1);
            String _day= UtilClass.addZero(day);
            returnData= year+"."+_month+"."+_day;
        }else if(gubun==2){
            String _month= UtilClass.addZero(month+1);
            String _day= UtilClass.addZero(day);
            returnData= year+"."+_month+".01";
        }else if(gubun==3){
            String _hour= UtilClass.addZero(hour);
            String _minute= UtilClass.addZero(minute);
            returnData= _hour+":"+_minute;
        }else{
            String _plus_hour= UtilClass.addZero(plus_hour);
            String _plus_minute= UtilClass.addZero(plus_minute);
            returnData= _plus_hour+":"+_plus_minute;
        }

        return returnData;
    }

    //날짜 한자리 0추가
    public static String addZero(int arg) {
        String val = String.valueOf(arg);
        if (arg < 10)
            val = "0" + val;

        return val;
    }

    public static void showProcessingDialog(ProgressDialog dialog) {
        dialog.setMessage("Processing...");
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCancelable(false);
        dialog.show();
    }

    public static void showProgressDialog(ProgressDialog dialog){
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("Loading...");
        dialog.setProgress(0);
        dialog.setMax(100);
        dialog.setCancelable(false);
        dialog.show();
    }

    public static void closeProgressDialog(ProgressDialog dialog){
        if (dialog.isShowing()) {
            dialog.cancel();
        }
    }

    public static final void logD (String TAG, String message) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, message);
        }
    }

    public static void hideKeyboard(Context context) {
        try {
            ((Activity) context).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            if ((((Activity) context).getCurrentFocus() != null) && (((Activity) context).getCurrentFocus().getWindowToken() != null)) {
                ((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(((Activity) context).getCurrentFocus().getWindowToken(), 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void dataNullCheckZero(HashMap<String, Object> hashMap) {
        for (Iterator iter = hashMap.entrySet().iterator(); iter.hasNext();) {
            Map.Entry entry = (Map.Entry) iter.next();
            String key = (String)entry.getKey();

            if(entry.getValue()==null){
                entry.setValue("");
            }
        }
    }

    //탭 아이콘 이미지
    public static int TEXT_SIZE  = -1;
    public static int TEXT_SIZE_BIG = -1;

    public static Drawable drawable(Context context, int id) {
        if (TEXT_SIZE == -1) {
            TEXT_SIZE = (int) new TextView(context).getTextSize();
            TEXT_SIZE_BIG = (int) (TEXT_SIZE * 1.5);
        }
        if (Build.VERSION.SDK_INT >= 21) {
            return context.getResources().getDrawable(id, context.getTheme());
        } else {
            return context.getResources().getDrawable(id);
        }
    }

    public static CharSequence iconText(Drawable icon, String text) {
        SpannableString iconText = new SpannableString(" "+text);
        icon.setBounds(0, 0, TEXT_SIZE_BIG, TEXT_SIZE_BIG);
        ImageSpan imageSpan = new ImageSpan(icon);

        iconText.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return iconText;
    }

}
