package com.ehsandonation.setting;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;

import com.ehsandonation.R;

public class SettingActivity extends AppCompatActivity {


    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        setupToolbar();

    }


    private void setupToolbar() {
        toolbar = findViewById(R.id.setting_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getString(R.string.setting));
    }

}
