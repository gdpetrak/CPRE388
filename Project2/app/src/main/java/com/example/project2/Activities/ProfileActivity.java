package com.example.project2.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.project2.R;
import com.example.project2.util.Collections;
import com.example.project2.util.FirebaseUtil;
import com.example.project2.util.IndividualPostAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ProfileActivity extends AppCompatActivity {

    String[] quotes = {"It does not matter how slowly you go as long as you do not stop.",
            "Quality is not an act, it is a habit.",
            "Life is 10% what happens to you and 90% how you react to it.",
            "It always seems impossible until it's done.",
            "Good, better, best. Never let it rest. 'Til your good is better and your better is best.",
            "With the new day comes new strength and new thoughts.",
            "When something is important enough, you do it even if the odds are not in your favor.",
            "Our greatest weakness lies in giving up. The most certain way to succeed is always to try just one more time.",
            "Ever tried. Ever failed. No matter. Try again. Fail again. Fail better.",
            "If you can dream it, you can do it."};

    GraphView graphView;
    private FirebaseFirestore mFirestore;
    private CollectionReference moodPostsCollection;
    private CollectionReference usersCollection;
    int[] userX = new int[5];
    int[] userY = new int[5];
    int i = 4;

    IndividualPostAdapter postAdapter;
    ArrayList<String> usernamesView = new ArrayList<>();
    ArrayList<String> moodEntryView = new ArrayList<>();
    ArrayList<String> moodRatingView = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Init firebase
        FirebaseAuth mAuth = FirebaseUtil.getAuth();
        FirebaseUser user = mAuth.getCurrentUser();

        FirebaseFirestore.setLoggingEnabled(true);
        mFirestore = FirebaseUtil.getFirestore();

        moodPostsCollection = mFirestore.collection(Collections.POST_COLLECTION_LOCATION);
        usersCollection = mFirestore.collection(Collections.USER_COLLECTION_LOCATION);

        // Init layout references
        Button backButton = findViewById(R.id.back_button);
        Button settingsButton = findViewById(R.id.settings_button);
        TextView motivationalQuotes = findViewById(R.id.motivation);
        TextView usernameDisplay = findViewById(R.id.username_display);

        // Post list init
        ListView listView = (ListView) findViewById(R.id.post_list);
        postAdapter = new IndividualPostAdapter(getApplicationContext(), usernamesView, moodEntryView, moodRatingView);
        listView.setAdapter(postAdapter);

        String uid = user.getUid();

        // Init username display
        usersCollection.whereEqualTo("uid", uid).get()
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

        // Init button on click actions
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileActivity.this, ProfileSettingsActivity.class));
            }
        });

        // Set up the quotes
        Random r = new Random();
        motivationalQuotes.setText(quotes[r.nextInt(quotes.length)]);

        // Render the graphs
        System.out.println("moodpostretrieval: task starting");
        moodPostsCollection.whereEqualTo("posterId", user.getUid())
                .orderBy("postTime", Query.Direction.DESCENDING)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        System.out.println("moodpostretrieval: task complete");
                        if (task.isSuccessful()) {
                            System.out.println("moodpostretrieval: task successful");
                            int i = 4;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (i < 0)
                                    break;

                                userY[i] = document.getLong("moodRating").intValue();
                                i--;
                            }

                            // on below line we are initializing our graph view.
                            graphView = findViewById(R.id.idGraphView);
                            LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(new DataPoint[]{
                                    // on below line we are adding
                                    // each point on our x and y axis.
                                    new DataPoint(1, userY[0]),
                                    new DataPoint(2, userY[1]),
                                    new DataPoint(3, userY[2]),
                                    new DataPoint(4, userY[3]),
                                    new DataPoint(5, userY[4])
                            });

                            // after adding data to our line graph series.
                            // on below line we are setting
                            // title for our graph view.
                            graphView.setTitle("Mood Trend");

                            // on below line we are setting
                            // text color to our graph view.
                            graphView.setTitleColor(R.color.black);

                            // on below line we are setting
                            // our title text size.
                            graphView.setTitleTextSize(45);

                            // on below line we are adding
                            // data series to our graph view.
                            graphView.addSeries(series);

                            graphView.getViewport().setMinY(1);
                            graphView.getViewport().setMaxY(5);
                            graphView.getViewport().setYAxisBoundsManual(true);
                        } else {
                            System.out.println("moodpostretrieval: task failed");
                            System.out.println("moodpostretrieval: " + task.getException());
                        }
                    }
                });

        updatePostDisplay(uid);
    }

    private void updatePostDisplay(String uid) {
        moodPostsCollection.whereEqualTo("posterId", uid).orderBy("postTime", Query.Direction.DESCENDING).limit(50).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot post : task.getResult()) {
                                usersCollection.whereEqualTo("uid", post.get("posterId").toString()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            List<DocumentSnapshot> documentSnapshotList = task.getResult().getDocuments();
                                            if (documentSnapshotList.size() > 0) {
                                                usernamesView.add(documentSnapshotList.get(0).get("username").toString());
                                            } else {
                                                usernamesView.add("Deleted User");
                                            }
                                        } else {
                                            usernamesView.add("Deleted User");
                                        }
                                        postAdapter.notifyDataSetChanged();
                                    }
                                });
                                moodEntryView.add(post.get("moodEntry").toString());
                                moodRatingView.add(post.get("moodRating").toString());
                            }
                            postAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }
}
