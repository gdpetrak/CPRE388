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

import com.example.project2.Database.User;
import com.example.project2.R;
import com.example.project2.util.Collections;
import com.example.project2.util.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * This activity allows users to register for the app, giving them a username as well.
 */
public class RegisterActivity extends AppCompatActivity {

    /**
     * Stores the authentication value of the user.
     */
    private FirebaseAuth mAuth;

    /**
     * Reference to our Firebase.
     */
    private FirebaseFirestore mFirestore;

    /**
     * Initializes the screen when the activity is called.
     * @param savedInstanceState The previous state of the Activity.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Input init
        Button register = findViewById(R.id.register);
        EditText usernameInput = ((EditText) findViewById(R.id.username));
        EditText emailInput = ((EditText) findViewById(R.id.email));
        EditText passwordInput = ((EditText) findViewById(R.id.password));

        // Firebase stuff
        mFirestore = FirebaseUtil.getFirestore();
        mAuth = FirebaseUtil.getAuth();

        // Register failed alert
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

        register.setOnClickListener(new View.OnClickListener() {

            /**
             * Handles logic for the Firebase when the Register button is pressed.
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
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                            /**
                             * Sends new information to the Firebase based off of user input.
                             * @param task The task that is being completed.
                             */
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "createUserWithEmail:success");

                                    // Create username userid combo
                                    CollectionReference usernameReference = mFirestore.collection(Collections.USER_COLLECTION_LOCATION);
                                    User currentUser = new User(usernameInput.getText().toString(), mAuth.getUid());
                                    usernameReference.add(currentUser).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {

                                        /**
                                         * Sends the user to the HomeActivity screen.
                                         * @param task The task that is being completed.
                                         */
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentReference> task) {
                                            // Push to home screen
                                            startActivity(new Intent(RegisterActivity.this, HomeActivity.class));
                                        }
                                    });
                                } else {
                                    Log.d(TAG, "createUserWithEmail:failure", task.getException());
                                    alertBuilder.setMessage("Failed to register a new account\n" + task.getException());
                                    alertBuilder.show();
                                }
                            }
                        });
            }
        });
    }
}
