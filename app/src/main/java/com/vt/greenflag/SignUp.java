package com.vt.greenflag;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.regex.Pattern;

public class SignUp extends AppCompatActivity {

    EditText email_editText;
    EditText passw_editText;
    EditText passwCheck_editText;

    Button nextButton;
    ImageButton backButton;

    TextView passwordError_textView;
    TextView emailError_textView;
    static Pattern PASSWORD_PATERN = null;

    DbHelper db;

    boolean emailPassed = false;
    boolean passwordPassed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        PASSWORD_PATERN =
                Pattern.compile("^" +
                        "(?=.*?[A-Z])" +        //must contain one uppercase character
                        "(?=.*?[a-z])" +        //must contain one lowercase character
                        "(?=.*?[0-9])" +        //must contain one number
                        ".{8,}" +               //must be at least 8 characters
                        "$");

        email_editText = findViewById(R.id.email_editText);
        passw_editText = findViewById(R.id.password_editText);
        passwCheck_editText = findViewById(R.id.confirmPassword_editText);

        nextButton = findViewById(R.id.next_button);
        backButton = findViewById(R.id.back_Button);

        passwordError_textView = findViewById(R.id.passwordError_textView);
        emailError_textView = findViewById(R.id.emailError_textView);


        db = new DbHelper(this);
        SQLiteDatabase database = db.getWritableDatabase();

        nextButton.setEnabled(false);

        // takes us on first activity on button tap
        backButton.setOnClickListener(v -> openFirstActivity());

        // checks if the data entered is an email and if it exists in our db
        email_editText.setOnFocusChangeListener((v, hasFocus) -> {
            if(!hasFocus && email_editText.getText().length() != 0 && !isValidEmail(email_editText.getText().toString())){
                emailError_textView.setText(R.string.notEmail_error);
                emailError_textView.setVisibility(View.VISIBLE);
                nextButton.setEnabled(false);
                emailPassed = false;

            }else if(!hasFocus && email_editText.getText().length() != 0 && !db.checkemail(email_editText.getText().toString())){
                emailError_textView.setText(R.string.Email_error_string);
                emailError_textView.setVisibility(View.VISIBLE);
                nextButton.setEnabled(false);
                emailPassed = false;

            }else if(!hasFocus && email_editText.getText().length() != 0 && db.checkemail(email_editText.getText().toString()) && isValidEmail(email_editText.getText().toString())){
                emailError_textView.setVisibility(View.GONE);
                emailPassed = true;
            }

            // If you came back to change the email will check to see if it is available and a valid email address
            if(hasFocus && email_editText.getText().toString().length()!=0){
                email_editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if(!isValidEmail(email_editText.getText().toString())){
                            emailError_textView.setText(R.string.notEmail_error);
                            emailError_textView.setVisibility(View.VISIBLE);
                            nextButton.setEnabled(false);
                            emailPassed = false;
                        }else if(!db.checkemail(email_editText.getText().toString())){
                            emailError_textView.setText(R.string.Email_error_string);
                            emailError_textView.setVisibility(View.VISIBLE);
                            nextButton.setEnabled(false);
                            emailPassed = false;
                        }else if(email_editText.getText().length() != 0 && db.checkemail(email_editText.getText().toString()) && isValidEmail(email_editText.getText().toString())){
                            emailError_textView.setVisibility(View.GONE);
                            emailPassed = true;
                            if(passwordPassed) nextButton.setEnabled(true);
                        }
                    }
                });
            }
        });

        // checks if password meets requirements
        passw_editText.setOnFocusChangeListener((v, hasFocus) -> {
            if(!hasFocus && !passwordCheck(passw_editText.getText().toString())){
                passwordError_textView.setText(R.string.password_invalid_error);
                passwordError_textView.setVisibility(View.VISIBLE);
                nextButton.setEnabled(false);
                passwordPassed = false;
            }else if(!hasFocus && passwordCheck(passw_editText.getText().toString())){
                passwordError_textView.setVisibility(View.GONE);
                passwordPassed = true;
            }

            //If you come back to change the password this will check if it's good or not
            if(hasFocus && passw_editText.getText().toString().length()!=0) {
                passw_editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (!passwordCheck(s)) {
                            passwordError_textView.setText(R.string.password_invalid_error);
                            passwordError_textView.setVisibility(View.VISIBLE);
                            nextButton.setEnabled(false);
                            passwordPassed = false;
                        } else if (!s.toString().equals(passwCheck_editText.getText().toString()) ) {
                            passwordError_textView.setText(R.string.password_match_error);
                            passwordError_textView.setVisibility(View.VISIBLE);
                            nextButton.setEnabled(false);
                            passwordPassed = false;
                        } else if (passwordCheck(s) && s.toString().equals(passwCheck_editText.getText().toString())) {
                            passwordError_textView.setVisibility(View.GONE);
                            passwordPassed = true;
                            if (emailPassed) nextButton.setEnabled(true);
                        }
                    }
                });
            }

        });

        // checks if passwords match
        passwCheck_editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().equals(passw_editText.getText().toString()) && emailPassed && passwordPassed){
                    nextButton.setEnabled(true);
                }else{
                    nextButton.setEnabled(false);
                }
            }
        });

        // register user in database
        nextButton.setOnClickListener(v ->
                db.insert(email_editText.getText().toString(), passw_editText.getText().toString()));

    }

    public void openFirstActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    // checks if email is valid
    public static boolean isValidEmail(CharSequence target) {
        return target != null && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    // checks if password meets requirements
    public static boolean passwordCheck(CharSequence pass){
        return PASSWORD_PATERN.matcher(pass).matches();
    }
}