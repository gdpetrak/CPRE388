package com.example.project2.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.project2.Database.MoodPost;
import com.example.project2.R;
import com.example.project2.util.Collections;
import com.example.project2.util.FirebaseUtil;
import com.example.project2.util.LikablePostAdapter;
import com.example.project2.util.PostCommentAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class PostViewActivity extends AppCompatActivity {
    private FirebaseFirestore mFirestore;
    private CollectionReference moodPostsCollection;
    private CollectionReference usersCollection;

    private PostCommentAdapter postAdapter;
    private List<String> commentsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_view);

        // Get MoodPost
        Intent i = getIntent();
        String postref = i.getStringExtra("postref");
        String username = i.getStringExtra("username");

        // Init firebase
        FirebaseFirestore.setLoggingEnabled(true);
        mFirestore = FirebaseUtil.getFirestore();
        moodPostsCollection = mFirestore.collection(Collections.POST_COLLECTION_LOCATION);
        usersCollection = mFirestore.collection(Collections.USER_COLLECTION_LOCATION);

        // Init layout refs
        TextView usernameDisplay = findViewById(R.id.post_view_username_display);
        TextView entryDisplay = findViewById(R.id.post_view_mood_entry_display);
        TextView ratingDisplay = findViewById(R.id.post_view_mood_rating_display);
        Button backButton = findViewById(R.id.back_button);

        // Set up PostCommentAdapter
        commentsList = new ArrayList<>();
        ListView listView = (ListView) findViewById(R.id.post_comments);
        postAdapter = new PostCommentAdapter(getApplicationContext(), commentsList);
        listView.setAdapter(postAdapter);

        // Load the Post data
        if (postref != null && !postref.isEmpty()) {
            moodPostsCollection.document(postref).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (!task.isSuccessful()) {
                                    System.out.println("postView:failed to get post ==> post: " +
                                            postref + " " + task.getException());
                                    return;
                                }

                                DocumentSnapshot snapshot = task.getResult();
                                MoodPost post = snapshot.toObject(MoodPost.class);
                                usernameDisplay.setText(username);
                                entryDisplay.setText(post.getMoodEntry());
                                ratingDisplay.setText("Mood: " + post.getMoodRating());
                                List<String> loadedComments = post.getComments();
                                for (String comment:loadedComments) {
                                    commentsList.add(comment);
                                }
                                postAdapter.notifyDataSetChanged();
                            }
                        });

            // Set up back button
            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
    }
}
