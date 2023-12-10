package com.example.project2.util;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.project2.R;

import java.util.ArrayList;
public class FriendAdapter extends BaseAdapter {
    // Variables needed to generate the listview
    Context context;
    LayoutInflater inflater;

    // Data to display in the listview
    ArrayList<String> usernames;

    public FriendAdapter(Context applicationContext, ArrayList<String> usernames){
        this.context = applicationContext;
        this.usernames = usernames;
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
        view = inflater.inflate(R.layout.activity_friendview, null);
        TextView usernameDisplay = view.findViewById(R.id.friend_username);
        Button unfollow = view.findViewById(R.id.remove_friend);
        usernameDisplay.setText(usernames.get(i));
        unfollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO Logic for removing friend
            }
        });
        return view;
    }
}
