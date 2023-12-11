package com.example.project2.Database;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A class that handles storing information to be uploaded to the database for each post
 */
public class MoodPost {
    /**
     * Information to be stored in the database for each post
     */
    private int moodRating;
    private String moodEntry, posterId;
    private Date postTime;
    private List<String> userLikes;
    private List<String> comments;

    /**
     * Creates a new empty MoodPost
     * Only to be utilized by the toObject method
     */
    public MoodPost() {

    }

    /**
     * Creates a new MoodPost with the provided postId, moodEntry, and moodRating
     * Use this when creating a post from the homescreen
     * @param posterId The uid of the user creating this post
     * @param moodEntry The text description of the users mood
     * @param moodRating The numeric rating selected on the mood slider
     */
    public MoodPost(String posterId, String moodEntry, int moodRating) {
        this.posterId = posterId;
        this.moodEntry = moodEntry;
        this.moodRating = moodRating;
        this.postTime = new Date();
        this.userLikes = new ArrayList<>();
        this.comments = new ArrayList<>();
    }

    /**
     * Gets the posterId for this post
     * @return posterId
     */
    public String getPosterId() {
        return posterId;
    }

    /**
     * Gets the moodRating for this post
     * @return moodRating
     */
    public int getMoodRating() {
        return moodRating;
    }

    /**
     * Gets the moodEntry for this post
     * @return moodEntry
     */
    public String getMoodEntry() {
        return moodEntry;
    }

    /**
     * Gets the postTime for this post
     * @return postTime
     */
    public Date getPostTime() {
        return postTime;
    }

    /**
     * Gets the userLikes for this post
     * @return userLikes
     */
    public List<String> getUserLikes() {
        return userLikes;
    }

    /**
     * Gets the comments for this post
     * @return comments
     */
    public List<String> getComments() {
        return comments;
    }

    /**
     * Gets the number of likes on a post
     * @return The size of userLikes list
     */
    public int getLikes() {
        return userLikes.size();
    }

    /**
     * Sets the posterId for this post
     * Only to be utilized by the toObject method
     * @param posterId The id of the poster
     */
    public void setPosterId(String posterId) {
        this.posterId = posterId;
    }

    /**
     * Sets the posterId for this post
     * Only to be utilized by the toObject method
     * @param moodRating The mood rating for the user
     */
    public void setMoodRating(int moodRating) {
        this.moodRating = moodRating;
    }

    /**
     * Sets the posterId for this post
     * Only to be utilized by the toObject method
     * @param moodEntry The mood entry for the user
     */
    public void setMoodEntry(String moodEntry) {
        this.moodEntry = moodEntry;
    }

    /**
     * Sets the posterId for this post
     * Only to be utilized by the toObject method
     * @param postTime The time the post was created
     */
    public void setPostTime(Date postTime) {
        this.postTime = postTime;
    }

    /**
     * Sets the posterId for this post
     * Only to be utilized by the toObject method
     * @param userLikes The list of liking users
     */
    public void setUserLikes(List<String> userLikes) {
        this.userLikes = userLikes;
    }

    /**
     * Sets the posterId for this post
     * Only to be utilized by the toObject method
     * @param comments The list of comments
     */
    public void setComments(List<String> comments) {
        this.comments = comments;
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

    /**
     * Ads a comment to the current comments list
     * @param comment The comment to add to the list of comments
     */
    public void addComment(String comment) {
        this.comments.add(comment);
    }
}
