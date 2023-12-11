package com.example.project2.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.project2.R;
import com.example.project2.util.FirebaseUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Handles either automatically signing the user in if they have signed in previously
 * or shows them an option between registering and signing in
 */
public class LandingActivity extends AppCompatActivity {
    /**
     * A reference to the Firebase Auth functionality
     */
    private FirebaseAuth mAuth;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        // Layout reference init
        Button signin = findViewById(R.id.sign_in);
        Button register = findViewById(R.id.register);

        // Firebase auth check for already logged in account
        mAuth = FirebaseUtil.getAuth();

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LandingActivity.this, SignInActivity.class));
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LandingActivity.this, RegisterActivity.class));
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        // Check if user is already signed in
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            // User is signed in, push to home screen
            startActivity(new Intent(LandingActivity.this, HomeActivity.class));
        }
    }
}
