package com.example.project2.Database;

import java.util.Date;

/**
 * A class that sets up the fields for Mood Posts inside of the moodPosts collection in Firebase.
 */
public class MoodPost {

    /**
     * A rating from 1 to 5 on the user's mood.
     */
    private int moodRating;

    /**
     * Strings that contain the mood entry itself and the user ID.
     */
    private String moodEntry, posterId;

    /**
     * A field that holds the timestamp of when the post was created.
     */
    private Date postTime;

    /**
     * Initialization of a mood post and its fields.
     * @param posterId The user ID of the poster.
     * @param moodEntry The description of the poster's mood.
     * @param moodRating The 1 to 5 rating of the poster's mood.
     */
    public MoodPost(String posterId, String moodEntry, int moodRating) {
        this.posterId = posterId;
        this.moodEntry = moodEntry;
        this.moodRating = moodRating;
        this.postTime = new Date();
    }

    /**
     * Gets the poster ID.
     * @return posterId
     */
    public String getPosterId() {
        return posterId;
    }

    /**
     * Gets the mood rating.
     * @return moodRating
     */
    public int getMoodRating() {
        return moodRating;
    }

    /**
     * Gets the mood entry.
     * @return moodEntry
     */
    public String getMoodEntry() {
        return moodEntry;
    }

    /**
     * Gets the post time.
     * @return postTime
     */
    public Date getPostTime() {
        return postTime;
    }
}
