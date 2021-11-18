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
    private ImageView memberImageOne, memberImageTwo,memberImageThree;
    private TextView memberNameOne, memberNameTwo,memberNameThree, memberJobOne, memberJobTwo,memberJobThree, longText, address;
    private String imageOne = "https://firebasestorage.googleapis.com/v0/b/ehsan-app.appspot.com/o/Team%2Fbelal.jpg?alt=media&token=f037ef8e-70a9-440a-8f60-8dfe3458e3e0";
    private String imageTwo = "https://firebasestorage.googleapis.com/v0/b/ehsan-app.appspot.com/o/Team%2Fbelal.jpg?alt=media&token=f037ef8e-70a9-440a-8f60-8dfe3458e3e0";
    private String imageThree = "https://firebasestorage.googleapis.com/v0/b/ehsan-app.appspot.com/o/Team%2Fgeorge.jpg?alt=media&token=f4cfdc3e-c6ca-4618-8239-06240d11a400";
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
        memberImageThree=findViewById(R.id.about_us_image_3);
        memberNameOne = findViewById(R.id.about_us_name_1);
        memberNameTwo = findViewById(R.id.about_us_name_2);
        memberNameThree=findViewById(R.id.about_us_name_3);
        memberJobOne = findViewById(R.id.about_us_job_1);
        memberJobTwo = findViewById(R.id.about_us_job_2);
        memberJobThree=findViewById(R.id.about_us_job_3);
        longText = findViewById(R.id.about_us_long_text);
        address = findViewById(R.id.about_us_address);
    }

    private void updateTitile() {

        memberNameOne.setText(getString(R.string.belal));
        memberNameTwo.setText(getString(R.string.belal));
        memberNameThree.setText(R.string.george);
        memberJobOne.setText(getString(R.string.executive_officer));
        memberJobTwo.setText(getString(R.string.marketing));
        memberJobThree.setText(R.string.analysis);
        longText.setText(getString(R.string.long_mission));
        address.setText(getString(R.string.current_address));
        UniversalImageLoader.setImage(imageOne , memberImageOne , null , "");
        UniversalImageLoader.setImage(imageTwo , memberImageTwo , null , "");
        UniversalImageLoader.setImage(imageThree , memberImageThree , null , "");

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
