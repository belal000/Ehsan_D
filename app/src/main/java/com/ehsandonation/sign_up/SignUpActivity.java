package com.ehsandonation.sign_up;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ehsandonation.R;
import com.ehsandonation.firebase.FirebaseServices;
import com.ehsandonation.login.LoginActivity;
import com.ehsandonation.model.User;
import com.ehsandonation.utils.Tools;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText fullName, email, password, address, phoneNumber;
    private TextView login;
    private Button signUpBtn;
    private FirebaseServices firebaseServices;
    private ProgressBar signUpLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        initWidgets();

    }

    private void initWidgets() {

        firebaseServices = FirebaseServices.getFirebaseServicesInstance();
        firebaseServices.setContext(SignUpActivity.this);

        Tools.setSystemBarColor(this, R.color.blue_grey_900);
        fullName = findViewById(R.id.full_name_edit_text);
        fullName.addTextChangedListener(new RealTimeTextWatcher(fullName));

        email = findViewById(R.id.email_edit_text);
        email.addTextChangedListener(new RealTimeTextWatcher(email));

        password = findViewById(R.id.password_edit_text);
        password.addTextChangedListener(new RealTimeTextWatcher(password));

        address = findViewById(R.id.address_edittext);
        address.addTextChangedListener(new RealTimeTextWatcher(address));

        phoneNumber = findViewById(R.id.phone_number_edit_text);
        phoneNumber.addTextChangedListener(new RealTimeTextWatcher(phoneNumber));

        login = findViewById(R.id.login_text);
        login.setOnClickListener(this);

        signUpBtn = findViewById(R.id.sign_up_btn);
        signUpBtn.setOnClickListener(this);

        signUpLoading = findViewById(R.id.sign_up_loading);
    }

    private boolean validateFullName() {
        boolean valid = true;

        String inputFullName = fullName.getText().toString();
        if (inputFullName.isEmpty() || fullName.length() < 3) {
            fullName.setError(getString(R.string.at_least_three_character));
            valid = false;
        } else {
            fullName.setError(null);
        } // end else

        return valid;
    }

    private boolean validateEmail() {

        boolean valid = true;

        String inputEmail = email.getText().toString();
        if (inputEmail.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(inputEmail).matches()) {
            email.setError(getString(R.string.valid_email));
            valid = false;
        } // end if from email
        else {
            email.setError(null);
        } // end else

        return valid;

    }

    private boolean validatePassword() {
        boolean valid = true;

        String inputPassword = password.getText().toString();
        if (inputPassword.isEmpty() || inputPassword.length() < 6) {
            password.setError(getString(R.string.at_least_six_character));
            valid = false;
        } else {
            password.setError(null);
        } // end else

        return valid;
    }

    private boolean validateAddress() {
        boolean valid = true;

        String inputAddress = address.getText().toString();
        if (inputAddress.isEmpty() || inputAddress.length() < 3) {
            address.setError(getString(R.string.at_least_three_character));
            valid = false;
        } else {
            address.setError(null);
        } // end else

        return valid;
    }

    private boolean validatePhoneNumber() {

        boolean valid = true;

        String inputPhone = phoneNumber.getText().toString();
        if (inputPhone.isEmpty()) {
            phoneNumber.setError(getString(R.string.cannot_be_empty));
            valid = false;
        } else {
            phoneNumber.setError(null);
        } // end else

        return valid;

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.login_text:

                backToLogin();

                break;

            case R.id.sign_up_btn:

                signUp();

                break;

        }

    }

    private void backToLogin() {
        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
        startActivity(intent);
        SignUpActivity.this.finish();
    }

    private void signUp() {
        if (!validateFullName() || !validateEmail() || !validatePassword()
                || !validateAddress() || !validatePhoneNumber()) {

        } else {
            Tools.hideKeyboard(SignUpActivity.this);
            signUpLoading.setVisibility(View.VISIBLE);
            firebaseServices.createUser(generateUser(), password.getText().toString(), signUpLoading);
        }
    }

    private User generateUser() {
        User user = new User();
        user.setFullName(fullName.getText().toString());
        user.setEmailAddress(email.getText().toString());
        user.setAddress(address.getText().toString());
        user.setPhoneNumber(phoneNumber.getText().toString());

        return user;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        backToLogin();
    }

    public class RealTimeTextWatcher implements TextWatcher {

        private View view;

        public RealTimeTextWatcher(View view) {
            this.view = view;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {

            switch (view.getId()) {
                case R.id.full_name_edit_text:
                    validateFullName();
                    break;

                case R.id.email_edit_text:
                    validateEmail();
                    break;

                case R.id.password_edit_text:
                    validatePassword();
                    break;

                case R.id.address_edittext:
                    validateAddress();
                    break;


                case R.id.phone_number_edit_text:
                    validatePhoneNumber();
                    break;

            }
        }
    }

}
