package com.creative.seong.app.menu;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.creative.seong.app.R;
import com.creative.seong.app.util.BackPressCloseSystem;

import butterknife.ButterKnife;

public class TestActivity extends AppCompatActivity {

    private BackPressCloseSystem backPressCloseSystem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        ButterKnife.bind(this);
        backPressCloseSystem = new BackPressCloseSystem(this);


    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        backPressCloseSystem.onBackPressed();
    }

}
