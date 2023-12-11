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
 * Handles storing and managing information that is stored on the database for each user
 */
public class User {
    /**
     * Values to be stored on the database for each user
     */
    private String username;
    private String uid;
    private List<String> friends;

    /**
     * Creates a new user
     * To be used when creating a new user on the register screen
     * @param username The entered username for the new user
     * @param uid The uid generated for the new user
     */
    public User(String username, String uid) {
        this.username = username;
        this.uid = uid;
        friends = new ArrayList<>();
    }

    /**
     * Creates a user instances for an already created user
     * Only to be utilized by the toObject method
     */
    public User(){
            friends = new ArrayList<>();
    }

    /**
     * Gets the value of the users username
     * @return username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Gets the users uid
     * @return uid
     */
    public String getUid() {
        return uid;
    }

    /**
     * Gets the users list of friends
     * @return friends
     */
    public List<String> getFriends() {
        return friends;
    }

    /**
     * Adds a singular friend to the list of friends
     * @param friendUid The uid of the friend being added
     */
    public void addFriend(String friendUid) {
        friends.add(friendUid);
    }

    /**
     * Removes a friend from the list of friends
     * @param friendUid The uid of the friend being removed
     */
    public void removeFriend(String friendUid) {
        friends.remove(friendUid);
    }

    /**
     * Sets the username for the user
     * @param username The new username
     */
    public void setUsername(String username){
        this.username = username;
    }

    /**
     * Sets the uid for this user
     * Only to be utilized by the toObject method
     * @param uid The users uid
     */
    public void setUid(String uid){
        this.uid = uid;
    }

    /**
     * Sets the friends list for this user
     * Only to be utilized by the toObject method
     * @param friends The users friends
     */
    public void setFriends(List<String> friends){
        this.friends = friends;
    }



}
