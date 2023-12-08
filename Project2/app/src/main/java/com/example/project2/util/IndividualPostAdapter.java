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

public class IndividualPostAdapter extends BaseAdapter {
    // Variables needed to generate the listview
    Context context;
    LayoutInflater inflater;

    // Data to display in the listview
    ArrayList<String> usernames;
    ArrayList<String> moodEntry;
    ArrayList<String> moodRating;

    public IndividualPostAdapter(Context applicationContext, ArrayList<String> usernames, ArrayList<String> moodEntry, ArrayList<String> moodRating) {
        this.context = applicationContext;
        this.usernames = usernames;
        this.moodEntry = moodEntry;
        this.moodRating = moodRating;
        inflater = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return moodEntry.size();
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
