package com.example.project2.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.project2.Database.MoodPost;
import com.example.project2.R;

import java.util.List;

public class PostCommentAdapter extends BaseAdapter {

    Context context;
    LayoutInflater inflater;
    List<String> comments;

    public PostCommentAdapter(Context applicationContext, List<String> comments) {
        this.comments = comments;
        this.context = applicationContext;
        inflater = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return this.comments.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup parent) {
        view = inflater.inflate(R.layout.activity_moodpost, null);
        TextView usernameDisplay = view.findViewById(R.id.post_username_display);
        TextView entryDisplay = view.findViewById(R.id.post_mood_entry_display);
        TextView ratingDisplay = view.findViewById(R.id.post_mood_rating_display);
        usernameDisplay.setVisibility(View.GONE);
        entryDisplay.setText(comments.get(i));
        ratingDisplay.setVisibility(View.GONE);
        return view;
    }
}
