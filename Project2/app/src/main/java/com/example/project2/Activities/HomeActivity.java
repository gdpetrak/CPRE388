package com.example.project2.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.project2.Database.MoodPost;
import com.example.project2.R;
import com.example.project2.util.Collections;
import com.example.project2.util.FirebaseUtil;
import com.example.project2.util.LikablePostAdapter;
import com.example.project2.util.LikablePostAdapterDelegate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.slider.Slider;
import com.google.firebase.Timestamp;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HomeActivity extends AppCompatActivity implements LikablePostAdapterDelegate {
    private FirebaseFirestore mFirestore;
    private CollectionReference moodPostsCollection;
    Timestamp[] timestamps = new Timestamp[3];
    private int i;
    private int[] rating = new int[3];
    private String[] moodEntry = new String[3];
    private CollectionReference usersCollection;
    private String userId;
    LikablePostAdapter postAdapter;
    ArrayList<String> usernamesView = new ArrayList<>();
    ArrayList<String> moodEntryView = new ArrayList<>();
    ArrayList<String> moodRatingView = new ArrayList<>();
    ArrayList<String> postRefs = new ArrayList<>();

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

        // Post list init
        ListView listView = (ListView) findViewById(R.id.post_list);
        postAdapter = new LikablePostAdapter(getApplicationContext(), usernamesView, moodEntryView,
                moodRatingView, postRefs, this);
        listView.setAdapter(postAdapter);
        listView.setClickable(true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(HomeActivity.this, PostViewActivity.class);
                intent.putExtra("postref", postRefs.get(position));
                intent.putExtra("username", usernamesView.get(position));
                startActivity(intent);
            }
        });

        // Input reference init
        EditText createPostEntryInput = ((EditText) findViewById(R.id.mood_entry));
        Slider createPostMoodInput = findViewById(R.id.mood_rating);

        // Init firebase
        FirebaseFirestore.setLoggingEnabled(true);
        mFirestore = FirebaseUtil.getFirestore();
        moodPostsCollection = mFirestore.collection(Collections.POST_COLLECTION_LOCATION);
        usersCollection = mFirestore.collection(Collections.USER_COLLECTION_LOCATION);

        // Init user
        FirebaseAuth mAuth = FirebaseUtil.getAuth();
        FirebaseUser user = mAuth.getCurrentUser();
        userId = user.getUid();

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

        updatePostDisplay();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updatePostDisplay();
    }

    private void createPost(String posterId, String moodEntry, int moodRating) {
        MoodPost post = new MoodPost(posterId, moodEntry, moodRating);
        moodPostsCollection.add(post);
        updatePostDisplay();
    }

    private void createPost(MoodPost post) {
        moodPostsCollection.add(post);
        updatePostDisplay();
    }

    private void updatePostDisplay() {
        usernamesView.clear();
        moodEntryView.clear();
        moodRatingView.clear();
        postRefs.clear();
        moodPostsCollection.orderBy("postTime", Query.Direction.DESCENDING).limit(50).get()
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
                                postRefs.add(post.getId());
                            }
                            postAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    @Override
    public void onLikePost(String likePostId) {
        DocumentReference postRef = moodPostsCollection.document(likePostId);
        mFirestore.runTransaction(new Transaction.Function<Transaction>() {
            @Nullable
            @Override
            public Transaction apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                DocumentSnapshot currPost = transaction.get(postRef);
                MoodPost updatedMoodPost = currPost.toObject(MoodPost.class);
                updatedMoodPost.addLike(userId);
                return transaction.set(moodPostsCollection.document(likePostId), updatedMoodPost);
            }
        });
    }

    @Override
    public void displayLikes(String likePostId, TextView likesDisplay) {
        DocumentReference postRef = moodPostsCollection.document(likePostId);
        mFirestore.runTransaction(new Transaction.Function<Void>() {
            @Nullable
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                DocumentSnapshot snap = transaction.get(postRef);
                MoodPost post = snap.toObject(MoodPost.class);
                likesDisplay.setText("Likes: " + post.getLikes());
                return null;
            }
        });
    }
}