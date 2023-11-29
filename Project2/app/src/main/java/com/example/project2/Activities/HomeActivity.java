package com.example.project2.Activities;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.project2.Database.MoodPost;
import com.example.project2.R;
import com.example.project2.util.FirebaseUtil;
import com.google.android.material.slider.Slider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Collection;

public class HomeActivity extends AppCompatActivity {
    private static final String POST_COLLECTION_LOCATION = "moodPosts";
    private FirebaseFirestore mFirestore;
    private CollectionReference moodPostsCollection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Layout reference init
        ImageButton createPostButton = findViewById(R.id.create_post_button);
        Button closeCreatePostPopupButton = findViewById(R.id.close_create_post);
        Button postCreatedPost = findViewById(R.id.post);
        ImageButton accountButton = findViewById(R.id.profile_button);
        LinearLayout createPostPopup = findViewById(R.id.create_post_popup);

        // Input reference init
        EditText createPostEntryInput = ((EditText) findViewById(R.id.mood_entry));
        Slider createPostMoodInput = findViewById(R.id.mood_rating);

        // Init firebase
        FirebaseFirestore.setLoggingEnabled(true);
        mFirestore = FirebaseUtil.getFirestore();
        moodPostsCollection = mFirestore.collection(POST_COLLECTION_LOCATION);

        // Init user
        FirebaseAuth mAuth = FirebaseUtil.getAuth();
        FirebaseUser user = mAuth.getCurrentUser();

        // CREATE POST CODE
        createPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createPostPopup.setVisibility(View.VISIBLE);
            }
        });

        closeCreatePostPopupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createPostPopup.setVisibility(View.GONE);
            }
        });

        postCreatedPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String postId = "-1";
                String posterId = user.getUid();
                String moodEntry = createPostEntryInput.getText().toString();
                int moodRating = (int) createPostMoodInput.getValue();
                MoodPost post = new MoodPost(postId, posterId, moodEntry, moodRating);

                // Reset the edit text
                createPostEntryInput.setText("");

                // Send post to database and hide popup
                moodPostsCollection.add(post);
                createPostPopup.setVisibility(View.GONE);
            }
        });

        // ACCOUNT CODE
        accountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                mAuth.signOut();
//                startActivity(new Intent(HomeActivity.this, LandingActivity.class));
            }
        });
    }
}