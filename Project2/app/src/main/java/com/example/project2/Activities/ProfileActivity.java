package com.example.project2.Activities;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class ProfileActivity extends AppCompatActivity{
    GraphView graphView;
    private static final String POST_COLLECTION_LOCATION = "moodPosts";
    private FirebaseFirestore mFirestore;
    private CollectionReference moodPostsCollection;
    int[] userX = new int[5];
    int[] userY = new int[5];
    int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        FirebaseAuth mAuth = FirebaseUtil.getAuth();
        FirebaseUser user = mAuth.getCurrentUser();

        FirebaseFirestore.setLoggingEnabled(true);
        mFirestore = FirebaseUtil.getFirestore();
        moodPostsCollection = mFirestore.collection(POST_COLLECTION_LOCATION);

        ImageButton backButton = findViewById(R.id.back_button);
        ImageButton accountButton = findViewById(R.id.profile_button);
        moodPostsCollection.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (document.getString("posterId").equals(user.getUid())) {
                            // Need to establish order once postId has been correctly implemented
                            userY[i] = document.getLong("moodRating").intValue();
                            i++;
                            if (i > 4) {
                                break;
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
                            new DataPoint(1, userY[4]),
                            new DataPoint(2, userY[3]),
                            new DataPoint(3, userY[2]),
                            new DataPoint(4, userY[1]),
                            new DataPoint(5, userY[0])
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

        accountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                startActivity(new Intent(ProfileActivity.this, LandingActivity.class));
            }
        });

<<<<<<< Updated upstream
        // on below line we are initializing our graph view.
        graphView = findViewById(R.id.idGraphView);

        // on below line we are adding data to our graph view.
        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(new DataPoint[]{
                // on below line we are adding
                // each point on our x and y axis.

//                new DataPoint(1, 1),
//                new DataPoint(1, 5),
                new DataPoint(1, 3),
                new DataPoint(2, 4),
                new DataPoint(3, 5),
                new DataPoint(4, 5),
                new DataPoint(5, 3),

        });

        // after adding data to our line graph series.
        // on below line we are setting
        // title for our graph view.
        graphView.setTitle("My Mood Trend");

        // on below line we are setting
        // text color to our graph view.
        graphView.setTitleColor(R.color.black);

        // on below line we are setting
        // our title text size.
        graphView.setTitleTextSize(50);

        // on below line we are adding
        // data series to our graph view.
        graphView.addSeries(series);
=======
>>>>>>> Stashed changes
    }
}
