package com.example.project2.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.project2.R;

import java.util.ArrayList;

public class MoodPostAdapter extends BaseAdapter {
    // Variables needed to generate the listview
    Context context;
    LayoutInflater inflater;
    int inflatableLayout;

    // Data to display in the listview
    ArrayList<String> usernames;
    ArrayList<String> moodEntry;
    ArrayList<String> moodRating;
    ArrayList<String> postRef;

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
