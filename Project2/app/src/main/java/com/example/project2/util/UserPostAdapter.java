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
    /**
     * Reference to the mood post location on the database
     */
    private CollectionReference moodPostsCollection;

    /**
     * A reference to the delegate that handles editing callbacks
     */
    private UserPostAdapterDelegate delegate;

    /**
     * Creates a new instance of a UserPostAdapter
     * This adapter will handle rendering listview content for editable posts
     * @param applicationContext A reference to the application context
     * @param usernames A list of usernames to be displayed
     * @param moodEntry A list of mood entries to be displayed
     * @param moodRating A list of mood ratings to be displayed
     * @param postRef A list of references to the post location
     * @param delegate Delegate responsible for handling like callbacks
     */
    public UserPostAdapter(Context applicationContext, ArrayList<String> usernames,
                           ArrayList<String> moodEntry, ArrayList<String> moodRating,
                           ArrayList<String> postRef, CollectionReference posts,
                           UserPostAdapterDelegate delegate) {
        super(applicationContext, usernames, moodEntry, moodRating, postRef);
        this.moodPostsCollection = posts;
        this.delegate = delegate;
        inflatableLayout = R.layout.activity_userpost;
    }

    /**
     * Creates a view for the editable post that is displayed within the listview
     * @param i The position of the item within the adapter's data set of the item whose view
     *        we want.
     * @param view The old view to reuse, if possible. Note: You should check that this view
     *        is non-null and of an appropriate type before using. If it is not possible to convert
     *        this view to display the correct data, this method can create a new view.
     *        Heterogeneous lists can specify their number of view types, so that this View is
     *        always of the right type (see {@link #getViewTypeCount()} and
     *        {@link #getItemViewType(int)}).
     * @param viewGroup The parent that this view will eventually be attached to
     * @return The created editable post view
     */
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