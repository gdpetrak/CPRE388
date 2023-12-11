package com.example.project2.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.project2.R;

import java.util.ArrayList;

/**
 * A generic mood post adapter that is utilized by LikablePostAdapter and UserPostAdapter
 * Handles setting up the generic information used by both adapters
 */
public class MoodPostAdapter extends BaseAdapter {
    /**
     * Variables needed to generate the listview
     */
    Context context;
    LayoutInflater inflater;
    int inflatableLayout;

    /**
     * Data to display in the listview
     */
    ArrayList<String> usernames;
    ArrayList<String> moodEntry;
    ArrayList<String> moodRating;
    ArrayList<String> postRef;

    /**
     * Creates a new instance of a MoodPostAdapter
     * @param applicationContext A reference to the application context
     * @param usernames A list of usernames to be displayed
     * @param moodEntry A list of mood entries to be displayed
     * @param moodRating A list of mood ratings to be displayed
     * @param postRef A list of references to the post location
     */
    public MoodPostAdapter(Context applicationContext, ArrayList<String> usernames,
                           ArrayList<String> moodEntry, ArrayList<String> moodRating,
                           ArrayList<String> postRef) {
        this.context = applicationContext;
        this.usernames = usernames;
        this.moodEntry = moodEntry;
        this.moodRating = moodRating;
        this.postRef = postRef;
        this.inflatableLayout = R.layout.activity_moodpost;
        inflater = (LayoutInflater.from(applicationContext));
    }

    /**
     * Gets the size of the usernames list and returns that as only posts
     * with valid usernames should be displayed
     * @return The number of posts to be displayed
     */
    @Override
    public int getCount() {
        return usernames.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    /**
     * Handles displaying the generic information displayed on both LikablePostAdapter posts and
     * UserPostAdapter posts
     * @param i The position of the item within the adapter's data set of the item whose view
     *        we want.
     * @param view The old view to reuse, if possible. Note: You should check that this view
     *        is non-null and of an appropriate type before using. If it is not possible to convert
     *        this view to display the correct data, this method can create a new view.
     *        Heterogeneous lists can specify their number of view types, so that this View is
     *        always of the right type (see {@link #getViewTypeCount()} and
     *        {@link #getItemViewType(int)}).
     * @param viewGroup The parent that this view will eventually be attached to
     * @return The created generic view
     */
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(inflatableLayout, null);
        TextView usernameDisplay = view.findViewById(R.id.post_username_display);
        TextView entryDisplay = view.findViewById(R.id.post_mood_entry_display);
        TextView ratingDisplay = view.findViewById(R.id.post_mood_rating_display);
        usernameDisplay.setText(usernames.get(i));
        entryDisplay.setText(moodEntry.get(i));
        ratingDisplay.setText("Mood: " + moodRating.get(i));
        return view;
    }
}
