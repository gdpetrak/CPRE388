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
 * This activity is the home page for users that are currently not signed in.
 */
public class LandingActivity extends AppCompatActivity {

    /**
     * Stores the authentication value of the user.
     */
    private FirebaseAuth mAuth;

    /**
     * Initializes the screen when the activity is called.
     * @param savedInstanceState The previous state of the Activity.
     */
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

            /**
             * A button that allows the user to switch to a View that allows them to sign in.
             * @param v A reference to the button's view.
             */
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LandingActivity.this, SignInActivity.class));
            }
        });

        register.setOnClickListener(new View.OnClickListener() {

            /**
             * A button that allows the user to switch to a View that allows them to register.
             * @param v A reference to the button's view.
             */
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LandingActivity.this, RegisterActivity.class));
            }
        });
    }

    /**
     * Method that checks if the user has already been signed in. If they are, send them to HomeActivity.
     */
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
