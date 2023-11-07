package com.example.project2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SignInActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        Button signin = findViewById(R.id.sign_in);

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO attempt sign in before pushing to next screen
                startActivity(new Intent(SignInActivity.this, HomeActivity.class));
            }
        });
    }
}
