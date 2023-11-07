package com.example.project2.Activities;


import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.project2.Database.MoodPost;
import com.example.project2.R;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ImageButton createPostButton = findViewById(R.id.create_post_button);
        Button closeCreatePostPopupButton = findViewById(R.id.close_create_post);
        Button postCreatedPost = findViewById(R.id.post);
        LinearLayout createPostPopup = findViewById(R.id.create_post_popup);

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
                String moodEntry = ((TextView) findViewById(R.id.mood_entry)).getText().toString();
                int moodRating = -1; // TODO add mood rating slider
                MoodPost post = new MoodPost(postId, posterId, moodEntry, moodRating);

                // TODO send post to the database
                createPostPopup.setVisibility(View.GONE);
            }
        });
    }
}