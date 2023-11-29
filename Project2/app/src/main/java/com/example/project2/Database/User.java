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

public class User {

    private String username;
    private String uid;
//    private ArrayList<User> friends;
    private List<String> friendlist;

    public User(String username, String uid) {
        this.username = username;
        this.uid = uid;
        friendlist = new ArrayList<>();
        friendlist.add(uid);
    }

    public void updateUsername(String username) {
        this.username = username;
        // TODO push to database
    }

    public String getUsername() {
        return username;
    }

    public String getUid() {
        return uid;
    }

    public List<String> getFriends() {
        return friendlist;
    }

    public void addFriend(String friendUid) {
        friendlist.add(friendUid);
    }

    public Task<Void> addFriend(String friendUid, DocumentReference userRef, FirebaseFirestore mFirestore) {
        // Push to database
        return mFirestore.runTransaction(new Transaction.Function<Void>() {
            @Nullable
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                User currentUser = transaction.get(userRef).toObject(User.class);

                // Update current user friends list
                currentUser.addFriend(friendUid);
                addFriend(friendUid);

                transaction.set(userRef, currentUser);
                return null;
            }
        });
    }
}
