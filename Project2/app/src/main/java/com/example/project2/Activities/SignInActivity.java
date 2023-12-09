package com.example.project2.Activities;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.project2.R;
import com.example.project2.util.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

/**
 * This activity allows users to sign in to the app.
 */
public class SignInActivity extends AppCompatActivity {

    /**
     * Stores the authentication value of the user.
     */
    private FirebaseAuth mAuth;

    /**
     * Initializes the screen when the activity is called.
     * @param savedInstanceState The previous state of the Activity.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        // Layout reference init
        Button signin = findViewById(R.id.sign_in);
        EditText emailInput = ((EditText) findViewById(R.id.username));
        EditText passwordInput = ((EditText) findViewById(R.id.password));

        // Firebase stuff
        mAuth = FirebaseUtil.getAuth();

        // Sign in failed alert
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {

            /**
             * Handles possible errors in the registration.
             * @param dialogInterface This is tied to the listener.
             * @param i
             */
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        signin.setOnClickListener(new View.OnClickListener() {

            /**
             * Handles logic for the Firebase when the Sign In button is pressed.
             * @param v A reference to the button's view.
             */
            @Override
            public void onClick(View v) {
                // Check if email or password are invalid
                String email = emailInput.getText().toString();
                String password = passwordInput.getText().toString();
                if (email.isEmpty() || password.isEmpty()) {
                    return;
                }

                // Create user account
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                            /**
                             * Assigns values and updates user data into the app.
                             * @param task The task that is being completed.
                             */
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "signInWithEmail:success");
                                    startActivity(new Intent(SignInActivity.this, HomeActivity.class));
                                } else {
                                    Log.d(TAG, "signInWithEmail:failure", task.getException());
                                    alertBuilder.setMessage("Failed to sign in with email\n" + task.getException());
                                    alertBuilder.show();
                                }
                            }
                        });
            }
        });
    }
}
