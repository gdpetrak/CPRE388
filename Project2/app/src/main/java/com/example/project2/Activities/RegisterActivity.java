package com.example.project2.Activities;

import static android.content.ContentValues.TAG;

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

public class RegisterActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Input init
        Button register = findViewById(R.id.register);
        EditText emailInput = ((EditText) findViewById(R.id.username));
        EditText passwordInput = ((EditText) findViewById(R.id.password));

        // Firebase stuff
        mAuth = FirebaseUtil.getAuth();

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
                                    startActivity(new Intent(RegisterActivity.this, HomeActivity.class));
                                } else {
                                    Log.d(TAG, "createUserWithEmail:failure", task.getException());
                                    // TODO make a pop up here telling user why it failed.
                                }
                            }
                        });
            }
        });
    }
}
