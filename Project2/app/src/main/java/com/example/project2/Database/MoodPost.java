package com.example.project2.Database;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MoodPost {
    private int moodRating;
    private String moodEntry, posterId;
    private Date postTime;
    private List<String> userLikes;

    public MoodPost() {

    }

    public MoodPost(String posterId, String moodEntry, int moodRating) {
        this.posterId = posterId;
        this.moodEntry = moodEntry;
        this.moodRating = moodRating;
        this.postTime = new Date();
        this.userLikes = new ArrayList<>();
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

    public List<String> getUserLikes() {
        return userLikes;
    }

    public int getLikes() {
        return userLikes.size();
    }

    public void setPosterId(String posterId) {
        this.posterId = posterId;
    }

    public void setMoodRating(int moodRating) {
        this.moodRating = moodRating;
    }

    public void setMoodEntry(String moodEntry) {
        this.moodEntry = moodEntry;
    }

    public void setPostTime(Date postTime) {
        this.postTime = postTime;
    }

    public void setUserLikes(List<String> userLikes) {
        this.userLikes = userLikes;
    }


    /**
     * Add the current user to the list of users who have liked a post
     * @param likingUser The uid of the current user
     * @return Return true if liking was successful
     */
    public boolean addLike(String likingUser) {
        if (userLikes.contains(likingUser))
            return false;

        userLikes.add(likingUser);
        return true;
    }
}
