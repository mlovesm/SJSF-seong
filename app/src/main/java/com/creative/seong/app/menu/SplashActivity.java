package com.creative.seong.app.menu;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.creative.seong.app.R;

public class SplashActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro);

        Handler handler = new Handler () {
            @Override
            public void handleMessage(Message msg) {
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        };

        handler.sendEmptyMessageDelayed(0, 2500);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void onBackPressed(){}

}
