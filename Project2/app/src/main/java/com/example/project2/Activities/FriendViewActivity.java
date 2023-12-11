package com.example.project2.Activities;
import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.project2.Database.User;
import com.example.project2.R;
import com.example.project2.util.Collections;
import com.example.project2.util.FirebaseUtil;
import com.example.project2.util.FriendAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import com.example.project2.Activities.ProfileSettingsActivity;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * The FriendViewActivity class cerates the Add Friend page, including the EditText that allows
 * a user to enter a friend's username, and the Add Friend Button which adds the username of a friend
 * to the User's list of friends.
 */
public class FriendViewActivity extends AppCompatActivity {

    private FirebaseFirestore mFirestore;
    private CollectionReference usersCollection;

    FriendAdapter friendAdapter;
    ArrayList<String> usernamesView = new ArrayList<>();
    ArrayList<String> friendIds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_friend);
        ListView listView = (ListView) findViewById(R.id.friend_list);
        friendAdapter = new FriendAdapter(getApplicationContext(), usernamesView);
        listView.setAdapter(friendAdapter);

        ProfileSettingsActivity profileSettingsActivity = new ProfileSettingsActivity();
        FirebaseFirestore.setLoggingEnabled(true);
        mFirestore = FirebaseUtil.getFirestore();

        usersCollection = mFirestore.collection(Collections.USER_COLLECTION_LOCATION);
        Button home = findViewById(R.id.home);
        Button addFriend = findViewById(R.id.add_friend);
        EditText userText = findViewById(R.id.userText);
        // Init user
        FirebaseAuth mAuth = FirebaseUtil.getAuth();
        FirebaseUser user = mAuth.getCurrentUser();
        String friendUid = mAuth.getUid();
        refreshFriendList();
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(FriendViewActivity.this, HomeActivity.class));
            }
        });

        addFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String friendUser = userText.getText().toString();

                usersCollection.whereEqualTo("username", friendUser)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    QuerySnapshot querySnapshot = task.getResult();
                                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                                        DocumentSnapshot userDoc = querySnapshot.getDocuments().get(0);
                                        String friendUid = userDoc.getString("uid");

                                        addFriendButton(friendUid, mFirestore);
                                    } else {
                                        Toast.makeText(FriendViewActivity.this, "User not found", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Log.e(TAG, "Error searching for user", task.getException());
                                }
                            }
                        });
            }
        });
    }

    /**
     * Helper method that adds the friend's username to Firebase and sets the usernames
     * of the friends' of the user into a list so that it may be displayed in a ListView.
     * @param friendUid
     * @param userRef
     * @param mFirestore
     * @return
     */
    private Task<Void> addFriend(String friendUid, DocumentReference userRef, FirebaseFirestore mFirestore) {
        // Push to database
        return mFirestore.runTransaction(new Transaction.Function<Void>() {
            @Nullable
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                DocumentSnapshot currUserDoc = transaction.get(userRef);
                String username = currUserDoc.getString("username");
                List<String> friends = (List<String>) currUserDoc.get("friends");

                // Assuming User class has a method like setFriends
                User currentUser = new User(username, currUserDoc.getString("uid"));

                // Add the new friend only if it doesn't exist in the list
                if (!friends.contains(friendUid)) {
                    friends.add(friendUid);
                    currentUser.setFriends(friends);
                    transaction.set(userRef, currentUser);
                }

                return null;
            }
        });
    }


    /**
     * helper method that helps create the necessary parameters needed for the addFriend method as
     * well as provide checks to see if the Task of adding a friend was successful.
     * @param friendId
     * @param mFirestore
     */
    private void addFriendButton(String friendId, FirebaseFirestore mFirestore) {
        FirebaseAuth mAuth = FirebaseUtil.getAuth();
        FirebaseUser user = mAuth.getCurrentUser();
        usersCollection.whereEqualTo("uid", user.getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot documentSnapshot = task.getResult();
                            List<DocumentSnapshot> documentSnapshotList = documentSnapshot.getDocuments();
                            if (documentSnapshotList.size() > 0) {
                                DocumentReference docRef = usersCollection.document(documentSnapshotList.get(0).getId());
                                addFriend(friendId, docRef, mFirestore)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    // Friend added successfully
                                                    Toast.makeText(FriendViewActivity.this, "Friend added successfully", Toast.LENGTH_SHORT).show();
                                                    refreshFriendList(); // Refresh the friend list after adding a friend
                                                } else {
                                                    // Failed to add friend
                                                    Toast.makeText(FriendViewActivity.this, "Failed to add friend", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }
                        }
                    }
                });
    }

    /**
     * Helper method that clears the list of usernames so that the listview is refreshed after
     * adding a new friend, adding the new username in real time.
     */
    private void refreshFriendList() {
        FirebaseAuth mAuth = FirebaseUtil.getAuth();
        FirebaseUser user = mAuth.getCurrentUser();
        usernamesView.clear(); // Clear the existing list

        // Fetch the updated list of friends from the database
        usersCollection.whereEqualTo("uid", user.getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot documentSnapshot = task.getResult();
                            List<DocumentSnapshot> documentSnapshotList = documentSnapshot.getDocuments();
                            if (documentSnapshotList.size() > 0) {
                                User currUser = documentSnapshotList.get(0).toObject(User.class);
                                List<String> friends = currUser.getFriends();
                                System.out.println(friends);

                                // Print the content of friends before updating the list
                                System.out.println("usernamesView before update: " + usernamesView);

                                // Clear the list and add all friends
                                usernamesView.clear();
                                friendIds.clear();
                                for (String friend:friends) {
                                    // Fetch the friend's username
                                   usersCollection.whereEqualTo("uid", friend).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                       @Override
                                       public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                           QuerySnapshot friendQuerySnapshot = task.getResult();
                                           if (!friendQuerySnapshot.isEmpty()) {
                                               DocumentSnapshot friendSnapshot = friendQuerySnapshot.getDocuments().get(0);
                                               String friendUsername = friendSnapshot.getString("username");
                                               usernamesView.add(friendUsername);
                                               friendIds.add(friend);
                                               friendAdapter.notifyDataSetChanged();
                                           }
                                       }
                                   });
                                }

                                // Notify the adapter of the data change
                                friendAdapter.notifyDataSetChanged();

                                // Print the content of friends after updating the list
                                System.out.println("usernamesView after update: " + usernamesView);
                            }
                        }
                    }
                });
    }

//    private void handleUnfollow(String friendUsername) {
//        FirebaseAuth mAuth = FirebaseUtil.getAuth();
//        FirebaseUser user = mAuth.getCurrentUser();
//
//        DocumentReference userRef = usersCollection.document(user.getUid());
//
//        // Fetch the current user document
//        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if (task.isSuccessful()) {
//                    DocumentSnapshot document = task.getResult();
//                    if (document.exists()) {
//                        // Assuming User class has a method like removeFriend
//                        User currentUser = document.toObject(User.class);
//                        currentUser.removeFriend(friendUsername);
//
//                        // Update the user document
//                        userRef.set(currentUser)
//                                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                    @Override
//                                    public void onSuccess(Void aVoid) {
//                                        // Remove the friend from the local list
//                                        usernamesView.remove(friendUsername);
//                                        // Notify the adapter of the data change
//                                        friendAdapter.notifyDataSetChanged();
//                                        Toast.makeText(FriendViewActivity.this, "Unfollowed " + friendUsername, Toast.LENGTH_SHORT).show();
//                                    }
//                                })
//                                .addOnFailureListener(new OnFailureListener() {
//                                    @Override
//                                    public void onFailure(@NonNull Exception e) {
//                                        Log.e(TAG, "Error updating user document after unfollow", e);
//                                    }
//                                });
//                    }
//                } else {
//                    Log.e(TAG, "Error fetching current user document", task.getException());
//                }
//            }
//        });
//    }


}


