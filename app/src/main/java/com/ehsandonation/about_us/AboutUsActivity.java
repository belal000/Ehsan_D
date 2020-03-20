package com.ehsandonation.about_us;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ehsandonation.R;
import com.ehsandonation.utils.Tools;
import com.ehsandonation.utils.UniversalImageLoader;

public class AboutUsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ImageView memberImageOne, memberImageTwo;
    private TextView memberNameOne, memberNameTwo, memberJobOne, memberJobTwo, longText, address;
    private String imageOne = "https://firebasestorage.googleapis.com/v0/b/ehsan-app.appspot.com/o/Team%2Frandd.PNG?alt=media&token=6345f827-4e71-4dae-a767-f602a2f5f6d8";
    private String imageTwo = "https://firebasestorage.googleapis.com/v0/b/ehsan-app.appspot.com/o/Team%2Fyara.jpg?alt=media&token=b7affea4-7462-48cb-9870-cb1c98841515";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        initWidgets();
        updateTitile();
        setupToolbar();
    }

    private void initWidgets() {
        setTitle(getString(R.string.about_us));
        Tools.setSystemBarColor(this, R.color.blue_grey_900);
        Tools.initImageLoader(AboutUsActivity.this);
        memberImageOne = findViewById(R.id.about_us_image_1);
        memberImageTwo = findViewById(R.id.about_us_image_2);
        memberNameOne = findViewById(R.id.about_us_name_1);
        memberNameTwo = findViewById(R.id.about_us_name_2);
        memberJobOne = findViewById(R.id.about_us_job_1);
        memberJobTwo = findViewById(R.id.about_us_job_2);
        longText = findViewById(R.id.about_us_long_text);
        address = findViewById(R.id.about_us_address);
    }

    private void updateTitile() {

        memberNameOne.setText(getString(R.string.rand));
        memberNameTwo.setText(getString(R.string.yara));
        memberJobOne.setText(getString(R.string.executive_officer));
        memberJobTwo.setText(getString(R.string.marketing));
        longText.setText(getString(R.string.long_mission));
        address.setText(getString(R.string.current_address));
        UniversalImageLoader.setImage(imageOne , memberImageOne , null , "");
        UniversalImageLoader.setImage(imageTwo , memberImageTwo , null , "");

    }

    private void setupToolbar() {
        toolbar = findViewById(R.id.about_us_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getString(R.string.about_us));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

}
