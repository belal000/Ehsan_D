package com.ehsandonation.contact_us;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ehsandonation.R;
import com.ehsandonation.utils.Const;
import com.ehsandonation.utils.Tools;

public class ContactActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private Button sendEmail;
    private EditText message;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        initWodgets();
        setupToolbar();

    }

    private void initWodgets() {
        Tools.setSystemBarColor(this, R.color.blue_grey_900);
        setTitle(getString(R.string.contact_us));
        sendEmail = findViewById(R.id.send_email);
        sendEmail.setOnClickListener(this);
        message = findViewById(R.id.message);
    }

    private void setupToolbar() {
        toolbar = findViewById(R.id.contact_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.contact_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.call:
                permissionCheckCall();
                break;
            default:
                return false;
        }
        return true;
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.send_email:

                sendEmail();

                break;

        }

    }

    private void permissionCheckCall() {

        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE);

        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            makeCall();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, Const.MY_PERMISIONS_REQUEST_MAKE_CALL);
        }

    }

    private void makeCall() {
        Intent intent = new Intent(Intent.ACTION_CALL);
        String phone_number = String.valueOf("00962799092535");

        if (phone_number.trim().isEmpty()) {
            Tools.showMessage(ContactActivity.this, getString(R.string.cannot_call));
        } else {
            intent.setData(Uri.parse("tel:" + phone_number));
        }
        if (ActivityCompat.checkSelfPermission(ContactActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            permissionCheckCall();
        } else {
            startActivity(intent);
        }
    }

    /**
     * this method for send email to admin.
     */
    private void sendEmail() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"RandTahat@Ehsan.com"});
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.communicate)+ ".");
        intent.putExtra(Intent.EXTRA_TEXT, message.getText().toString());

        intent.setPackage("com.google.android.gm");
        if (intent.resolveActivity(getPackageManager()) != null)
            startActivity(intent);
        else
            Toast.makeText(this, R.string.no_gmail, Toast.LENGTH_SHORT).show();
    }

}
