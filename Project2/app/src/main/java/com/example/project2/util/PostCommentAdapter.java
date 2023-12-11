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

/**
 * Handles rendering comments underneath a post on the PostView screen
 */
public class PostCommentAdapter extends BaseAdapter {

    /**
     * Information needed to display the comments
     */
    Context context;
    LayoutInflater inflater;

    /**
     * List of comments to display
     */
    List<String> comments;

    /**
     * Creates a new instance of this adapter
     * @param applicationContext A reference to the application context
     * @param comments A list of the comments to be displayed
     */
    public PostCommentAdapter(Context applicationContext, List<String> comments) {
        this.comments = comments;
        this.context = applicationContext;
        inflater = (LayoutInflater.from(applicationContext));
    }

    /**
     * Gets the size of the comments list and returns that
     * @return The number of comments to be displayed
     */
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

    /**
     * Handles displaying a comment view
     * @param i The position of the item within the adapter's data set of the item whose view
     *        we want.
     * @param view The old view to reuse, if possible. Note: You should check that this view
     *        is non-null and of an appropriate type before using. If it is not possible to convert
     *        this view to display the correct data, this method can create a new view.
     *        Heterogeneous lists can specify their number of view types, so that this View is
     *        always of the right type (see {@link #getViewTypeCount()} and
     *        {@link #getItemViewType(int)}).
     * @param parent The parent that this view will eventually be attached to
     * @return The created comment view
     */
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
