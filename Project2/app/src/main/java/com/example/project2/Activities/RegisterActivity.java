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
 * Activity that handles registering a new user
 * Three edit texts are initialized for input for the username, email, and password
 * Once the register button is pressed a new User is created and uploaded to the database
 */
public class RegisterActivity extends AppCompatActivity {
    /**
     * References to the Firebase Auth and Firestore
     */
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;

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
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
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
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "createUserWithEmail:success");

                                    // Create username userid combo
                                    CollectionReference usernameReference = mFirestore.collection(Collections.USER_COLLECTION_LOCATION);
                                    User currentUser = new User(usernameInput.getText().toString(), mAuth.getUid());
                                    usernameReference.add(currentUser).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
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
