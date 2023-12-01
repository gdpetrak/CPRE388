package com.example.project2.Activities;


import androidx.annotation.NonNull;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.slider.Slider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Collection;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    private static final String POST_COLLECTION_LOCATION = "moodPosts";
    private static final String USER_COLLECTION_LOCATION = "users";
    private FirebaseFirestore mFirestore;
    private CollectionReference moodPostsCollection;
    private CollectionReference usersCollection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Layout reference init
        Button closeCreatePostPopupButton = findViewById(R.id.close_create_post);
        Button postCreatedPost = findViewById(R.id.post);
        ImageButton createPostButton = findViewById(R.id.back_button);
        ImageButton accountButton = findViewById(R.id.profile_button);
        LinearLayout createPostPopup = findViewById(R.id.create_post_popup);
        TextView usernameDisplay = findViewById(R.id.username_display);

        // Input reference init
        EditText createPostEntryInput = ((EditText) findViewById(R.id.mood_entry));
        Slider createPostMoodInput = findViewById(R.id.mood_rating);

        // Init firebase
        FirebaseFirestore.setLoggingEnabled(true);
        mFirestore = FirebaseUtil.getFirestore();
        moodPostsCollection = mFirestore.collection(POST_COLLECTION_LOCATION);
        usersCollection = mFirestore.collection(USER_COLLECTION_LOCATION);

        // Init user
        FirebaseAuth mAuth = FirebaseUtil.getAuth();
        FirebaseUser user = mAuth.getCurrentUser();

        // Init username display
        usersCollection.whereEqualTo("uid", user.getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot documentSnapshot = task.getResult();
                            List<DocumentSnapshot> documentSnapshotList = documentSnapshot.getDocuments();
                            if (documentSnapshotList.size() > 0) {
                                usernameDisplay.setText(documentSnapshotList.get(0).get("username").toString());
                            }
                        }
                    }
                });

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
                int postId = -1; // TODO get post id from server (ideally just make it count up)
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
                startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
            }
        });
    }
}