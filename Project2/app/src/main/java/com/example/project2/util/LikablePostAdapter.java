package com.example.project2.util;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.project2.R;

import java.util.ArrayList;

public class LikablePostAdapter extends MoodPostAdapter {
    private LikablePostAdapterDelegate delegate;

    public LikablePostAdapter(Context applicationContext, ArrayList<String> usernames,
                              ArrayList<String> moodEntry, ArrayList<String> moodRating,
                              ArrayList<String> postRef, LikablePostAdapterDelegate delegate) {
        super(applicationContext, usernames, moodEntry, moodRating, postRef);
        this.delegate = delegate;
        inflatableLayout = R.layout.activity_likablepost;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = super.getView(i, view, viewGroup);

        // Get likes display
        TextView likesDisplay = view.findViewById(R.id.likes_display);
        delegate.displayLikes(postRef.get(i), likesDisplay);

        // Send like to the database
        Button likeButton = view.findViewById(R.id.like_button);
        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                delegate.onLikePost(postRef.get(i));
            }
        });

        return view;
    }
}
