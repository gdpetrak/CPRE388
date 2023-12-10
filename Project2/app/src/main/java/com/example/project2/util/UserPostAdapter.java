package com.example.project2.util;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;

import com.example.project2.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;

import java.util.ArrayList;

public class UserPostAdapter extends MoodPostAdapter {
    private CollectionReference moodPostsCollection;
    private UserPostAdapterDelegate delegate;

    public UserPostAdapter(Context applicationContext, ArrayList<String> usernames,
                           ArrayList<String> moodEntry, ArrayList<String> moodRating,
                           ArrayList<String> postRef, CollectionReference posts,
                           UserPostAdapterDelegate delegate) {
        super(applicationContext, usernames, moodEntry, moodRating, postRef);
        this.moodPostsCollection = posts;
        this.delegate = delegate;
        inflatableLayout = R.layout.activity_userpost;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = super.getView(i, view, viewGroup);
        Button deleteButton = view.findViewById(R.id.delete_post_button);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deletePost(i);
            }
        });

        Button editButton = view.findViewById(R.id.edit_post_button);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                delegate.onEditPost(postRef.get(i));
            }
        });
        return view;
    }

    /**
     * Deletes the i-th post on the screen from the database
     * This method will delete the document in the database
     * then remove the document from the ArrayLists before
     * calling notifyDataSetChanged() to update the display
     * @param i The index of the post on screen to be deleted
     */
    private void deletePost(int i) {
        moodPostsCollection.document(postRef.get(i)).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "postDelete:successfullyDeletedPost");

                        // Remove post from screen
                        usernames.remove(i);
                        moodEntry.remove(i);
                        moodRating.remove(i);
                        postRef.remove(i);
                        UserPostAdapter.this.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "postDelete:errorDeletingPost =>", e);
                    }
                });
    }
}