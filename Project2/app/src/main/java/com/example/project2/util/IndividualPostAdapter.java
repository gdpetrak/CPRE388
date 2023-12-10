package com.example.project2.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.project2.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A class that creates an adapter for implementations of Lists for usage with Firebase.
 */
public class IndividualPostAdapter extends BaseAdapter {
    // Variables needed to generate the listview

    /**
     * The context of the adapter.
     */
    Context context;

    /**
     * The inflater of the adapter.
     */
    LayoutInflater inflater;

    // Data to display in the listview

    /**
     * A string ArrayList that contains all required usernames.
     */
    ArrayList<String> usernames;

    /**
     * A string ArrayList that contains all required mood entries.
     */
    ArrayList<String> moodEntry;

    /**
     * A string ArrayList that contains all required mood ratings.
     */
    ArrayList<String> moodRating;

    /**
     * The initialization of the Adapter.
     * @param applicationContext The context of the adapter.
     * @param usernames The list of all usernames.
     * @param moodEntry The list of all mood entries.
     * @param moodRating The list of all mood ratings.
     */
    public IndividualPostAdapter(Context applicationContext, ArrayList<String> usernames, ArrayList<String> moodEntry, ArrayList<String> moodRating) {
        this.context = applicationContext;
        this.usernames = usernames;
        this.moodEntry = moodEntry;
        this.moodRating = moodRating;
        inflater = (LayoutInflater.from(applicationContext));
    }

    /**
     * Gets the size of the usernames ArrayList.
     */
    @Override
    public int getCount() {
        return usernames.size();
    }

    /**
     * Gets a specific index from the ArrayList.
     * @param i index
     * @return A mood entry.
     */
    @Override
    public Object getItem(int i) {
        return null;
    }

    /**
     * Gets a specific id from the ArrayList.
     * @param i id
     * @return A mood entry.
     */
    @Override
    public long getItemId(int i) {
        return 0;
    }

    /**
     * Obtains the proper formatting for the resulting ListView.
     * @param i Number of entries
     * @param view The view of the adapter.
     * @param viewGroup The view of the adapter's group.
     * @return The view to be shown.
     */
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.activity_individualpost, null);
        TextView usernameDisplay = view.findViewById(R.id.post_username_display);
        TextView entryDisplay = view.findViewById(R.id.post_mood_entry_display);
        TextView ratingDisplay = view.findViewById(R.id.post_mood_rating_display);
        usernameDisplay.setText(usernames.get(i));
        entryDisplay.setText(moodEntry.get(i));
        ratingDisplay.setText(moodRating.get(i));
        return view;
    }
}
