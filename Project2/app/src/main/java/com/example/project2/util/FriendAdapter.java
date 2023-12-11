package com.example.project2.util;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import com.example.project2.Activities.FriendViewActivity;

import com.example.project2.R;

import java.util.ArrayList;

/**
 * FriendAdapter class creates the listview of friends a user has and adds them
 * to the Add friend screen.
 */
public class FriendAdapter extends BaseAdapter {
    // Variables needed to generate the listview
    Context context;
    LayoutInflater inflater;

    // Data to display in the listview
    ArrayList<String> usernames;

    /**
     * Constructor that initializes the applicationContext and the usernames ArrayList
     * @param applicationContext
     * @param usernames
     */
    public FriendAdapter(Context applicationContext, ArrayList<String> usernames){
        this.context = applicationContext;
        this.usernames = usernames;
        inflater = (LayoutInflater.from(applicationContext));
    }

    /**
     * getCount returns the size of the usernames arrayList
     * @return
     */
    @Override
    public int getCount() {
        return usernames.size();
    }

    /**
     * getItem returns a value of the usernames ArrayList at position i
     * @param i
     * @return
     */
    @Override
    public Object getItem(int i) {
        return usernames.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }


    /**
     * getview helps create the Listview and populates it with the usernames of the friends
     * a user is following
     * @param i
     * @param convertView
     * @param viewGroup
     * @return
     */
    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.activity_friendview, null);
            holder = new ViewHolder();
            holder.usernameDisplay = convertView.findViewById(R.id.friend_username);
//            holder.unfollow = convertView.findViewById(R.id.remove_friend);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String friendUsername = usernames.get(i);

        holder.usernameDisplay.setText(friendUsername);

//        holder.unfollow.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                handleUnfollow(friendUsername);
//
//            }
//        });

        return convertView;
    }


    /**
     * ViewHolder is called in getView in order to help with the creation of the ListView
     */
    static class ViewHolder {
        TextView usernameDisplay;
        Button unfollow;
    }

}
