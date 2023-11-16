package model;

import com.google.firebase.firestore.IgnoreExtraProperties;
@IgnoreExtraProperties
public class Mood {
    private String user;
    private int moodRating;
    private int moodTrend;
    private int moodDate;
    private String quote;

    public Mood(String user, int moodRating, int moodTrend, int moodDate, String quote){
        this.user = user;
        this.moodRating = moodRating;
        this.moodTrend = moodTrend;
        this.moodDate = moodDate;
        this.quote = quote;
    }

    public String getUser(){ return user;}

    public void setUser(String user){this.user = user;}

    public String getQuote(){ return quote; }

    public void setQuote(String quote) {
        this.quote = quote;
    }

    public int getMoodDate() {
        return moodDate;
    }

    public void setMoodDate(int moodDate) {
        this.moodDate = moodDate;
    }

    public int getMoodRating() {
        return moodRating;
    }

    public void setMoodRating(int moodRating) {
        this.moodRating = moodRating;
    }

    public int getMoodTrend() {
        return moodTrend;
    }

    public void setMoodTrend(int moodTrend) {
        this.moodTrend = moodTrend;
    }
}
