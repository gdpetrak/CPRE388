package com.example.project2.Database;

import java.util.Date;

public class MoodPost {
    private int postId, posterId, moodRating;
    private String moodEntry;
    private Date postTime;

    public MoodPost(int postId, int posterId, String moodEntry, int moodRating) {
        this.postId = postId;
        this.posterId = posterId;
        this.moodEntry = moodEntry;
        this.moodRating = moodRating;
        this.postTime = new Date();
    }

    public int getPostId() {
        return postId;
    }

    public int getPosterId() {
        return posterId;
    }

    public int getMoodRating() {
        return moodRating;
    }

    public String getMoodEntry() {
        return moodEntry;
    }

    public Date getPostTime() {
        return postTime;
    }
}
