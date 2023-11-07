package com.example.project2.Activities;


import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.project2.Database.MoodPost;
import com.example.project2.R;
import com.google.android.material.slider.Slider;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ImageButton createPostButton = findViewById(R.id.create_post_button);
        Button closeCreatePostPopupButton = findViewById(R.id.close_create_post);
        Button postCreatedPost = findViewById(R.id.post);
        LinearLayout createPostPopup = findViewById(R.id.create_post_popup);

        EditText createPostEntryInput = ((EditText) findViewById(R.id.mood_entry));
        Slider createPostMoodInput = findViewById(R.id.mood_rating);

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
                int postId = -1; // TODO get post id from server (ideally just make it count up)
                int posterId = -1; // TODO get poster id from server (requires login)
                String moodEntry = createPostEntryInput.getText().toString();
                int moodRating = (int) createPostMoodInput.getValue();
                MoodPost post = new MoodPost(postId, posterId, moodEntry, moodRating);

                // Reset the edit text
                createPostEntryInput.setText("");

                // TODO send post to the database
                System.out.printf("Post Info:\n   PostId: %d\n   PosterId: %d\n   " +
                        "Entry: %s\n   Rating: %d\n", post.getPostId(), post.getPosterId(),
                        post.getMoodEntry(), post.getMoodRating());
                createPostPopup.setVisibility(View.GONE);
            }
        });
    }
}