package com.example.project2.util;

import android.widget.TextView;

public interface LikablePostAdapterDelegate {
    /**
     * A callback that handles sending like data to the database
     * @param likePostId The id of the post being liked
     */
    void onLikePost(String likePostId);

    /**
     * Handles displaying the likes for a post
     * @param likePostId The id of the post being displayed
     * @param view A reference to the TextView where likes are displayed
     */
    void displayLikes(String likePostId, TextView view);
}
