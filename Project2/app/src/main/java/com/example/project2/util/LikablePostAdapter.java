package com.example.project2.util;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.project2.Activities.PostViewActivity;
import com.example.project2.R;

import java.util.ArrayList;

/**
 * Adapter that handles displaying listview items for likable mood posts
 */
public class LikablePostAdapter extends MoodPostAdapter {
    /**
     * A reference to the delegate that handles liking callbacks
     */
    private LikablePostAdapterDelegate delegate;

    /**
     * Creates a new instance of a LikablePost Adapter
     * This adapter will handle rendering listview content for likable posts
     * @param applicationContext A reference to the application context
     * @param usernames A list of usernames to be displayed
     * @param moodEntry A list of mood entries to be displayed
     * @param moodRating A list of mood ratings to be displayed
     * @param postRef A list of references to the post location
     * @param delegate Delegate responsible for handling like callbacks
     */
    public LikablePostAdapter(Context applicationContext, ArrayList<String> usernames,
                              ArrayList<String> moodEntry, ArrayList<String> moodRating,
                              ArrayList<String> postRef, LikablePostAdapterDelegate delegate) {
        super(applicationContext, usernames, moodEntry, moodRating, postRef);
        this.delegate = delegate;
        inflatableLayout = R.layout.activity_likablepost;
    }

    /**
     * Creates a view for the likable post that is displayed within the listview
     * @param i The position of the item within the adapter's data set of the item whose view
     *        we want.
     * @param view The old view to reuse, if possible. Note: You should check that this view
     *        is non-null and of an appropriate type before using. If it is not possible to convert
     *        this view to display the correct data, this method can create a new view.
     *        Heterogeneous lists can specify their number of view types, so that this View is
     *        always of the right type (see {@link #getViewTypeCount()} and
     *        {@link #getItemViewType(int)}).
     * @param viewGroup The parent that this view will eventually be attached to
     * @return The created likable post view
     */
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = super.getView(i, view, viewGroup);

        // Get likes display
        TextView likesDisplay = view.findViewById(R.id.likes_display);
        delegate.displayLikes(postRef.get(i), likesDisplay);

        // Send like to the database
        Button likeButton = view.findViewById(R.id.like_button);
        likeButton.setFocusable(false);
        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                delegate.onLikePost(postRef.get(i));
            }
        });

        return view;
    }
}
