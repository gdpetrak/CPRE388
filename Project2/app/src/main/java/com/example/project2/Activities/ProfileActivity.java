package com.example.project2.Activities;
import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
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
    private static final String POST_COLLECTION_LOCATION = "moodPosts";
    private static final String USER_COLLECTION_LOCATION = "users";
    private FirebaseFirestore mFirestore;
    private CollectionReference moodPostsCollection;
    private CollectionReference usersCollection;
    int[] userX = new int[5];
    int[] userY = new int[5];
    int i = 4;
    Timestamp[] timestamps = new Timestamp[5];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Init firebase
        FirebaseAuth mAuth = FirebaseUtil.getAuth();
        FirebaseUser user = mAuth.getCurrentUser();

        FirebaseFirestore.setLoggingEnabled(true);
        mFirestore = FirebaseUtil.getFirestore();
        for (int j = 0; j < 5; j++) {
            timestamps[j] = new Timestamp(0, 0);
        }

        moodPostsCollection = mFirestore.collection(POST_COLLECTION_LOCATION);
        usersCollection = mFirestore.collection(USER_COLLECTION_LOCATION);

        // Init alert builder
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setMessage("Are you sure you want to delete your account?\n" +
                "(Once an account is deleted, there is no way to recover it)")
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
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
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        Dialog deleteAccountAlert = alertBuilder.create();

        // Init layout references
        Button backButton = findViewById(R.id.back_button);
        Button signOutButton = findViewById(R.id.sign_out);
        Button deleteAccountButton = findViewById(R.id.delete_account);
        TextView motivationalQuotes = findViewById(R.id.motivation);
        TextView usernameDisplay = findViewById(R.id.username_display);

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

        // Init button on click actions
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
                deleteAccountAlert.show();
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
                                System.out.println("DOCUMENT" + document.getLong("moodRating").toString());
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
                        } else {
                            System.out.println("moodpostretrieval: task failed");
                            System.out.println("moodpostretrieval: " + task.getException());
                        }
                    }
                });
    }
}
