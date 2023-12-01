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
    int i = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        FirebaseAuth mAuth = FirebaseUtil.getAuth();
        FirebaseUser user = mAuth.getCurrentUser();

        FirebaseFirestore.setLoggingEnabled(true);
        mFirestore = FirebaseUtil.getFirestore();
        moodPostsCollection = mFirestore.collection(POST_COLLECTION_LOCATION);

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

        Button backButton = findViewById(R.id.back_button);
        Button signOutButton = findViewById(R.id.sign_out);
        Button deleteAccountButton = findViewById(R.id.delete_account);

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

        moodPostsCollection.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (document.getString("posterId").equals(user.getUid())) {
                            // Need to establish order once postId has been correctly implemented
                            userY[i] = document.getLong("moodRating").intValue();
                            i--;
                            if (i < 0) {
                                break;
                            }
                        }
                    }
                    // on below line we are initializing our graph view.
                    graphView = findViewById(R.id.idGraphView);
                    LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(new DataPoint[]{
                            // on below line we are adding
                            // each point on our x and y axis.
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
    }
}
