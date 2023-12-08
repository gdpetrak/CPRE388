package com.example.project2.Database;

import java.util.Date;

public class MoodPost {
    private int moodRating;
    private String moodEntry, posterId;
    private Date postTime;

    public MoodPost(String posterId, String moodEntry, int moodRating) {
        this.posterId = posterId;
        this.moodEntry = moodEntry;
        this.moodRating = moodRating;
        this.postTime = new Date();
    }

    public String getPosterId() {
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
