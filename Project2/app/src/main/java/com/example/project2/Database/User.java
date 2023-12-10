package com.example.project2.Database;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * A class that sets up the fields for users inside of the users collection in Firebase.
 */
public class User {

    /**
     * String that holds the current user's username.
     */
    private String username;

    /**
     * String that holds the current user's user ID.
     */
    private String uid;
//    private ArrayList<User> friends;

    /**
     * A list of strings containing all of the user's friends.
     */
    private List<String> friends;

    /**
     * Initialization of a user and its fields.
     * @param username The username of the user.
     * @param uid The user ID of the user.
     */
    public User(String username, String uid) {
        this.username = username;
        this.uid = uid;
        friends = new ArrayList<>();
    }

    /**
     * Updates the user's username.
     */
    public void updateUsername(String username) {
        this.username = username;
        // TODO push to database
    }

    /**
     * Gets the user's username.
     * @return username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Gets the user's user ID.
     * @return uid
     */
    public String getUid() {
        return uid;
    }

    /**
     * Gets the list of the user's friends.
     * @return friends
     */
    public List<String> getFriends() {
        return friends;
    }

    /**
     * Adds a user to the user's list of friends.
     */
    public void addFriend(String friendUid) {
        friends.add(friendUid);
    }
}
