package com.example.project2.util;

public interface UserPostAdapterDelegate {
    /**
     * A callback that handles sending an edited posts update info to the database
     * @param editPostId The id of the post being edited
     */
    void onEditPost(String editPostId);
}
