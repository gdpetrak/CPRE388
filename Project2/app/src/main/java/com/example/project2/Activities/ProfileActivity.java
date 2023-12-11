package com.example.project2.Activities;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.project2.Database.MoodPost;
import com.example.project2.R;
import com.example.project2.util.Collections;
import com.example.project2.util.FirebaseUtil;
import com.example.project2.util.UserPostAdapter;
import com.example.project2.util.UserPostAdapterDelegate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.slider.Slider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Java Class that will handle the logic of the Profile Activity page.
 * When the Profile page is created, it will display a graph to the user
 * showing the trend of their last 5 Mood entries. The page will also have display
 * the last 50 mood entries that the user has posted. The page has a Settings button
 * and a Home button.
 */
public class ProfileActivity extends AppCompatActivity implements UserPostAdapterDelegate {
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
    /**
     * graphView will be the graph that is displayed showing the Mood trend of a user.
     */
    GraphView graphView;
    /**
     * mFirestore allows the app to communicate with the database and allow
     * transactions.
     */
    private FirebaseFirestore mFirestore;
    /**
     * moodPostsCollection reference to access the mood collection
     */
    private CollectionReference moodPostsCollection;
    /**
     * userCollection reference to access the user collection
     */
    private CollectionReference usersCollection;

    /**
     * Integer array that will hold the values of the latest mood ratings for the Mood trend graph.
     */
    int[] userY = new int[5];
    int i = 4;

    /**
     * ListView Adapter variables
     * Data that needs to be displayed from within the adapter
     */
    UserPostAdapter postAdapter;
    ArrayList<String> usernamesView = new ArrayList<>();
    ArrayList<String> moodEntryView = new ArrayList<>();
    ArrayList<String> moodRatingView = new ArrayList<>();
    ArrayList<String> postRef = new ArrayList<>();

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
        Button shareButton = findViewById(R.id.share_button);
        Button shareGraphButton = findViewById(R.id.share_graph_button);
        TextView motivationalQuotes = findViewById(R.id.motivation);
        TextView usernameDisplay = findViewById(R.id.username_display);

        // Post list init
        ListView listView = (ListView) findViewById(R.id.post_list);
        postAdapter = new UserPostAdapter(getApplicationContext(), usernamesView, moodEntryView,
                moodRatingView, postRef, moodPostsCollection, this);
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

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, motivationalQuotes.getText().toString());
                sendIntent.setType("text/plain");

                Intent shareIntent = Intent.createChooser(sendIntent, "Share Your Motivational Quote");
                startActivity(shareIntent);
            }
        });

        shareGraphButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Convert the graph to an image and get the uri
                graphView.setDrawingCacheEnabled(true);
                Bitmap graphBitmap = Bitmap.createBitmap(graphView.getDrawingCache());
                graphView.setDrawingCacheEnabled(false);
                Uri graphUri = getImageUri(graphBitmap);

                // Actually create the share intent with the graph uri
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.setClipData(ClipData.newRawUri("Mood Graph", graphUri));
                shareIntent.putExtra(Intent.EXTRA_STREAM, graphUri);
                shareIntent.setType("image/png");
                Intent chooser = Intent.createChooser(shareIntent, "Share Your Mood Graph");

                List<ResolveInfo> resInfoList = ProfileActivity.this.getPackageManager().queryIntentActivities(chooser, PackageManager.MATCH_DEFAULT_ONLY);

                for (ResolveInfo resolveInfo : resInfoList) {
                    String packageName = resolveInfo.activityInfo.packageName;
                    ProfileActivity.this.grantUriPermission(packageName, graphUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }

                startActivity(chooser);
            }
        });

        // Set up the quotes
        Random r = new Random();
        motivationalQuotes.setText(quotes[r.nextInt(quotes.length)]);

        // Render the graphs
        System.out.println("moodpostretrieval: task starting");
        moodPostsCollection.whereEqualTo("posterId", user.getUid())
                .orderBy("postTime", Query.Direction.DESCENDING).limit(5)
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

    /**
     * A callback that is triggered when a user clicks on the edit button of a post
     * Sets the edit post popup to be visible and initializes onClickListeners for the buttons
     * Registers the post id to keep track of which post is being edited
     * Once the Edit Post button is pressed the onClickListener is fired and a transaction starts
     * to send the edited post to the database
     * @param editPostId The id of the post being edited
     */
    @Override
    public void onEditPost(String editPostId) {
        // Set popup to be visible
        LinearLayout editPostPopup = findViewById(R.id.edit_post_popup);
        editPostPopup.setVisibility(View.VISIBLE);

        // Set up the inputs
        EditText editPostEntryInput = ((EditText) findViewById(R.id.mood_entry));
        Slider editPostMoodInput = findViewById(R.id.mood_rating);
        Button editPostClose = findViewById(R.id.close_create_post);
        Button editPostFinish = findViewById(R.id.finish_edit_post);

        // Fill the default entries with the old post text
        int postIndex = postRef.indexOf(editPostId);
        String defaultEntry = moodEntryView.get(postIndex);
        Float defaultMood = Float.parseFloat(moodRatingView.get(postIndex));
        editPostEntryInput.setText(defaultEntry);
        editPostMoodInput.setValue(defaultMood);

        // Set the onClick functionality for when they finish editing a post
        editPostClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editPostPopup.setVisibility(View.GONE);
            }
        });

        editPostFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Task<Transaction> result = editPost(moodPostsCollection.document(editPostId),
                        editPostEntryInput.getText().toString(), (int) editPostMoodInput.getValue());

                // Unreliable, always says failure TODO get proper results from the transaction
                if (result.isSuccessful())
                    Log.d(TAG, "editPost:success");
                else
                    Log.w(TAG, "editPost:failure ==>", result.getException());

                // Update Display
                moodEntryView.set(postIndex, editPostEntryInput.getText().toString());
                moodRatingView.set(postIndex, Integer.toString((int) editPostMoodInput.getValue()));
                postAdapter.notifyDataSetChanged();

                // Hide the popup and reset inputs
                editPostPopup.setVisibility(View.GONE);
                editPostEntryInput.setText("");
                editPostMoodInput.setValue(3);
            }

            private Task<Transaction> editPost(DocumentReference editRef, String entry, int mood) {
                return mFirestore.runTransaction(new Transaction.Function<Transaction>() {
                    @Nullable
                    @Override
                    public Transaction apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                        DocumentSnapshot currPost = transaction.get(editRef);
                        MoodPost updatedMoodPost = new MoodPost(currPost.get("posterId").toString(),
                                entry, mood);
                        return transaction.set(moodPostsCollection.document(editPostId), updatedMoodPost);
                    }
                });
            }
        });
    }

    /**
     * Handles updating the list of posts below the graphview
     * Only displays posts that match the current uid
     * @param uid The uid of the current user
     */
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
                                postRef.add(post.getId());
                            }
                            postAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    public Uri getImageUri(Bitmap image) {
        File imagesFolder = new File(getCacheDir(), "images");
        Uri uri = null;
        try {
            imagesFolder.mkdirs();
            File file = new File(imagesFolder, "shared_image.png");

            FileOutputStream stream = new FileOutputStream(file);
            image.compress(Bitmap.CompressFormat.PNG, 90, stream);
            stream.flush();
            stream.close();
            uri = FileProvider.getUriForFile(this, "Project2.provider", file);

        } catch (IOException e) {
            Log.d(TAG, "IOException while trying to write file for sharing: " + e.getMessage());
        }
        return uri;
    }
}
