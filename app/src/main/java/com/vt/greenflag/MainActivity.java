package com.vt.greenflag;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Button signUp_buttone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        signUp_buttone = findViewById(R.id.signUp_button);
        signUp_buttone.setOnClickListener(v -> openSecondActivity());

    }

    public void openSecondActivity() {
        Intent intent = new Intent(this, SignUp.class);
        startActivity(intent);
    }
}