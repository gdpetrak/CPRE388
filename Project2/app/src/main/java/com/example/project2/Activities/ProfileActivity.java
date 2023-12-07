package com.example.project2.Activities;
import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;

public class ProfileActivity extends AppCompatActivity{
    GraphView graphView;
    private static final String POST_COLLECTION_LOCATION = "moodPosts";
    private static final String POST_TIME_FIELD = "postTime";
    private FirebaseFirestore mFirestore;
    private CollectionReference moodPostsCollection;
    Timestamp[] timestamps = new Timestamp[5];
    int[] userY = new int[5];
    int i = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        FirebaseAuth mAuth = FirebaseUtil.getAuth();
        FirebaseUser user = mAuth.getCurrentUser();

        FirebaseFirestore.setLoggingEnabled(true);
        mFirestore = FirebaseUtil.getFirestore();
        for (int j = 0; j < 5; j++) {
            timestamps[j] = new Timestamp(0, 0);
        }

        moodPostsCollection = mFirestore.collection(POST_COLLECTION_LOCATION);

        Button backButton = findViewById(R.id.back_button);
        Button signOutButton = findViewById(R.id.sign_out);
        Button deleteAccountButton = findViewById(R.id.delete_account);

        TextView motivation = findViewById(R.id.quoteText);
        Random rand = new Random();
        int randNum = rand.nextInt();
        if ((randNum % 10) == 0) {
            motivation.setText(R.string.mq1);
        } else if ((randNum % 10) == 1) {
            motivation.setText(R.string.mq2);
        } else if ((randNum % 10) == 2) {
            motivation.setText(R.string.mq3);
        } else if ((randNum % 10) == 3) {
            motivation.setText(R.string.mq4);
        } else if ((randNum % 10) == 4) {
            motivation.setText(R.string.mq5);
        } else if ((randNum % 10) == 5) {
            motivation.setText(R.string.mq6);
        } else if ((randNum % 10) == 6) {
            motivation.setText(R.string.mq7);
        } else if ((randNum % 10) == 7) {
            motivation.setText(R.string.mq8);
        } else if ((randNum % 10) == 8) {
            motivation.setText(R.string.mq9);
        } else if ((randNum % 10) == 9) {
            motivation.setText(R.string.mq10);
        }

        moodPostsCollection.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (document.getString("posterId").equals(user.getUid())) {
                            i = 4;
                            while (i > 0) {
                                if (document.get("postTime", Timestamp.class).compareTo(timestamps[i]) > 0) {
                                    shiftTimestamps(i, document.get("postTime", Timestamp.class));
                                    userY[i] = document.getLong("moodRating").intValue();
                                    i = -1;
                                } else {
                                    i--;
                                }
                            }
                            if (i == 0) {
                                if (document.get("postTime", Timestamp.class).compareTo(timestamps[i]) > 0) {
                                    timestamps[i] = document.get("postTime", Timestamp.class);
                                    userY[i] = document.getLong("moodRating").intValue();
                                }
                            }
                        }
                    }
                    // on below line we are initializing our graph view.
                    graphView = findViewById(R.id.idGraphView);
                    LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(new DataPoint[]{
                            // on below line we are adding
                            // each point on our x and y axis.
                            new DataPoint(1, 1),
                            new DataPoint(1, 5),
                            new DataPoint(1, userY[0]),
                            new DataPoint(2, userY[1]),
                            new DataPoint(3, userY[2]),
                            new DataPoint(4, userY[3]),
                            new DataPoint(5, userY[4])
                    });

                    // after adding data to our line graph series.
                    // on below line we are setting
                    // title for our graph view.
                    graphView.setTitle("My Graph View");

                    // on below line we are setting
                    // text color to our graph view.
                    graphView.setTitleColor(R.color.black);

                    // on below line we are setting
                    // our title text size.
                    graphView.setTitleTextSize(18);

                    // on below line we are adding
                    // data series to our graph view.
                    graphView.addSeries(series);
                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileActivity.this, HomeActivity.class));
            }
        });

        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                startActivity(new Intent(ProfileActivity.this, LandingActivity.class));
            }
        });

        deleteAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                assert user != null;
                user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "deleteAccount:success");
                            startActivity(new Intent(ProfileActivity.this, LandingActivity.class));
                        } else {
                            Log.d(TAG, "deleteAccount:failed ==> " + task.getException());
                        }
                    }
                });
            }
        });
    }

    private void shiftTimestamps(int i, Timestamp postTime) {
        for (int j = 0; j < i; j++) {
            timestamps[j] = timestamps[j+1];
            userY[j] = userY[j+1];
        }
        timestamps[i] = postTime;
    }
}
