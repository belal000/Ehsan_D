package com.ehsandonation.splash_screen;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.ehsandonation.R;
import com.ehsandonation.login.LoginActivity;
import com.ehsandonation.steppers.StepperActivity;
import com.ehsandonation.utils.Const;
import com.ehsandonation.utils.Tools;

public class SplashScreenActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        Tools.setSystemBarColor(this, R.color.blue_grey_900);
        startSplashScreen();
    }

    private void startSplashScreen() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent mainIntent = new Intent(SplashScreenActivity.this, StepperActivity.class);
                SplashScreenActivity.this.startActivity(mainIntent);
                SplashScreenActivity.this.finish();
            }
        }, Const.SPLASH_DISPLAY_LENGTH);
    }
}

