package com.example.project2.util;

import android.widget.TextView;

public interface LikablePostAdapterDelegate {
    void onLikePost(String likePostId);
    void displayLikes(String likePostId, TextView view);
}
