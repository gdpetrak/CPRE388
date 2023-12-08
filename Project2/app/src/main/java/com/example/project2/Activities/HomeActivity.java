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
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class HomeActivity extends AppCompatActivity {
    private static final String USERNAME_COLLECTION = "users";
    private static final String POST_COLLECTION_LOCATION = "moodPosts";
    private static final String USER_COLLECTION_LOCATION = "users";
    private FirebaseFirestore mFirestore;
    private CollectionReference moodPostsCollection;
    Timestamp[] timestamps = new Timestamp[3];
    private int i;
    private int[] rating = new int[3];
    private String[] moodEntry = new String[3];
    private CollectionReference usersCollection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        for (int j = 0; j < 3; j++) {
            timestamps[j] = new Timestamp(0, 0);

        }
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
                // Create the post
                createPost(user.getUid(), createPostEntryInput.getText().toString(),
                        (int) createPostMoodInput.getValue());

                // Hide popup and reset text
                createPostEntryInput.setText("");
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



//        TextView recentPost1 = findViewById(R.id.post1);
//        TextView recentPost2 = findViewById(R.id.post2);
//        TextView recentPost3 = findViewById(R.id.post3);
//
//        moodPostsCollection.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                if (task.isSuccessful()) {
//                    for (QueryDocumentSnapshot document : task.getResult()) {
//                        if (document.getString("posterId").equals(user.getUid())) {
//                            i = 2;
//                            while (i > 0) {
//                                if (document.get("postTime", Timestamp.class).compareTo(timestamps[i]) > 0) {
//                                    shiftTimestamps(i, document.get("postTime", Timestamp.class));
//                                    rating[i] = document.getLong("moodRating").intValue();
//                                    moodEntry[i] = document.getString("moodEntry");
//                                    i = -1;
//                                } else {
//                                    i--;
//                                }
//                            }
//                            if (i == 0) {
//                                if (document.get("postTime", Timestamp.class).compareTo(timestamps[i]) > 0) {
//                                    timestamps[i] = document.get("postTime", Timestamp.class);
//                                    rating[i] = document.getLong("moodRating").intValue();
//                                    moodEntry[i] = document.getString("moodEntry");
//                                }
//                            }
//                        }
//                    }
//                    if(moodEntry[0].isEmpty()){
//                        recentPost3.setText("Make a Mood Entry to see it appear here!");
//                    }else{
//                        recentPost3.setText("Date posted: " + timestamps[0].toDate() + '\n' + '\n' + "Your mood entry: " + moodEntry[0]+ '\n' + '\n' +"Your mood rating: " + rating[0]);
//
//                    }
//                    if(moodEntry[1].isEmpty()){
//                        recentPost2.setText("Make a mood entry to see it appear here!");
//                    }else{
//                        recentPost2.setText("Date posted: " + timestamps[1].toDate() + '\n' + '\n' +"Your mood entry: " + moodEntry[1]+ '\n' + '\n' + "Your mood rating: " + rating[1]);
//
//                    }
//                    if (moodEntry[2].isEmpty()){
//                        recentPost1.setText("Make a mood entry to see it appear here!");
//                    }else {
//                        recentPost1.setText("Date posted: " + timestamps[2].toDate() + '\n' + '\n' + "Your mood entry: " + moodEntry[2]+ '\n' + '\n' + "Your mood rating: " + rating[2]);
//                    }
//                }
//            }
//
//        });

    }

    private void createPost(String posterId, String moodEntry, int moodRating) {
        MoodPost post = new MoodPost(posterId, moodEntry, moodRating);
        moodPostsCollection.add(post);
    }

    private void createPost(MoodPost post) {
        moodPostsCollection.add(post);
    }
}