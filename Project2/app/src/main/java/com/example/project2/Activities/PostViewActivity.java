package com.example.project2.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity for viewing an individual post and all of the comments on that post
 */
public class PostViewActivity extends AppCompatActivity {
    /**
     * Firestore references
     */
    private FirebaseFirestore mFirestore;
    private CollectionReference moodPostsCollection;

    /**
     * Comments List references
     */
    private PostCommentAdapter postAdapter;
    private List<String> commentsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_view);

        // Get MoodPost
        Intent i = getIntent();
        String postid = i.getStringExtra("postref");
        String username = i.getStringExtra("username");

        // Init firebase
        FirebaseFirestore.setLoggingEnabled(true);
        mFirestore = FirebaseUtil.getFirestore();
        moodPostsCollection = mFirestore.collection(Collections.POST_COLLECTION_LOCATION);

        // Init layout refs
        TextView usernameDisplay = findViewById(R.id.post_view_username_display);
        TextView entryDisplay = findViewById(R.id.post_view_mood_entry_display);
        TextView ratingDisplay = findViewById(R.id.post_view_mood_rating_display);
        Button backButton = findViewById(R.id.back_button);
        Button createCommentButton = findViewById(R.id.create_comment_button);
        Button closeCommentButton = findViewById(R.id.close_create_comment);
        Button postCommentButton = findViewById(R.id.post_comment_button);
        LinearLayout commentPopup = findViewById(R.id.create_comment_popup);
        EditText commentEntry = findViewById(R.id.comment_entry);

        // Set up PostCommentAdapter
        commentsList = new ArrayList<>();
        ListView listView = (ListView) findViewById(R.id.post_comments);
        postAdapter = new PostCommentAdapter(getApplicationContext(), commentsList);
        listView.setAdapter(postAdapter);

        // Load the Post data
        if (postid != null && !postid.isEmpty()) {
            moodPostsCollection.document(postid).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (!task.isSuccessful()) {
                                    System.out.println("postView:failed to get post ==> post: " +
                                            postid + " " + task.getException());
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

            // Set up buttons
            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

            createCommentButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    commentPopup.setVisibility(View.VISIBLE);
                }
            });

            closeCommentButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    commentPopup.setVisibility(View.GONE);
                }
            });

            postCommentButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String newComment = commentEntry.getText().toString();
                    createComment(newComment, postid);
                    commentEntry.setText("");
                    commentPopup.setVisibility(View.GONE);
                }
            });
        }
    }

    /**
     * A method that is called whenever a new comment is to be created
     * Runs a transaction that adds the comment to the list of comments for a post
     * @param comment The String entered in the comment text box
     * @param postid The post that the comment is being left on
     */
    private void createComment(String comment, String postid) {
        mFirestore.runTransaction(new Transaction.Function<Transaction>() {
            @Nullable
            @Override
            public Transaction apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                DocumentSnapshot currPostSnap = transaction.get(moodPostsCollection.document(postid));
                MoodPost currPost = currPostSnap.toObject(MoodPost.class);
                currPost.addComment(comment);
                commentsList.add(comment);
                postAdapter.notifyDataSetChanged();
                return transaction.set(moodPostsCollection.document(postid), currPost);
            }
        });
    }
}
