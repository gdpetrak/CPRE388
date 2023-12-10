package com.example.project2.Activities;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.project2.Database.User;
import com.example.project2.R;
import com.example.project2.util.Collections;
import com.example.project2.util.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;

import java.util.List;

/**
 * This activity allows the user to change their username, sign out, or delete their account.
 */
public class ProfileSettingsActivity extends AppCompatActivity {

    /**
     * Reference to the collection 'users'.
     */
    private CollectionReference usersCollection;

    /**
     * Reference to our Firebase.
     */
    private FirebaseFirestore mFirestore;

    /**
     * Reference to the current user in Firebase.
     */
    private FirebaseUser user;

    /**
     * Initializes the screen when the activity is called.
     * @param savedInstanceState The previous state of the Activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_settings);

        // Init firebase
        FirebaseAuth mAuth = FirebaseUtil.getAuth();
        user = mAuth.getCurrentUser();
        FirebaseFirestore.setLoggingEnabled(true);
        mFirestore = FirebaseUtil.getFirestore();
        usersCollection = mFirestore.collection(Collections.USER_COLLECTION_LOCATION);

        // Init alert builder
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);

        // Build delete account alert
        alertBuilder.setMessage("Are you sure you want to delete your account?\n" +
                        "(Once an account is deleted, there is no way to recover it)")
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {

                    /**
                     * A button that asks for confirmation if deleting their account is what they actually want.
                     * @param dialogInterface This is tied to the listener.
                     * @param i
                     */
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        assert user != null;
                        user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {

                            /**
                             * Sends the newly deleted user back to LandingActivity
                             * so they can create another account.
                             * @param task
                             */
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "deleteAccount:success");
                                    startActivity(new Intent(ProfileSettingsActivity.this, LandingActivity.class));
                                } else {
                                    Log.d(TAG, "deleteAccount:failed ==> " + task.getException());
                                }
                            }
                        });
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                    /**
                     * A button that allows the user to cancel their account deletion.
                     * @param dialogInterface This is tied to the listener.
                     * @param i
                     */
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        Dialog deleteAccountAlert = alertBuilder.create();

        // Build change username alert
        final EditText userInput = new EditText(this);
        userInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        alertBuilder.setView(userInput);
        alertBuilder.setMessage("Please enter a new username.")
                .setPositiveButton("Change Username", new DialogInterface.OnClickListener() {

                    /**
                     * A button that confirms that the user wants to change their username.
                     * @param dialogInterface This is tied to the listener.
                     * @param i
                     */
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        updateUsernameButton(userInput.getText().toString(), mFirestore);
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                    /**
                     * A button that allows the user to cancel changing their username.
                     * @param dialogInterface This is tied to the listener.
                     * @param i
                     */
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        Dialog changeUsernameAlert = alertBuilder.create();

        // Init layout references
        Button signOutButton = findViewById(R.id.sign_out);
        Button deleteAccountButton = findViewById(R.id.delete_account);
        Button backButton = findViewById(R.id.back_button);
        Button changeUsernameButton = findViewById(R.id.change_username_button);

        changeUsernameButton.setOnClickListener(new View.OnClickListener() {
            /**
             * A button that allows the user to change their username.
             * @param view A reference to the button's view.
             */
            @Override
            public void onClick(View view) {
                changeUsernameAlert.show();
            }
        });

        signOutButton.setOnClickListener(new View.OnClickListener() {

            /**
             * A button that allows the user to sign out.
             * @param view A reference to the button's view.
             */
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                startActivity(new Intent(ProfileSettingsActivity.this, LandingActivity.class));
            }
        });

        deleteAccountButton.setOnClickListener(new View.OnClickListener() {
            /**
             * A button that allows the user to delete their account.
             * @param view A reference to the button's view.
             */
            @Override
            public void onClick(View view) {
                deleteAccountAlert.show();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {

            /**
             * A button that allows the user to return to their profile.
             * @param view A reference to the button's view.
             */
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    /**
     * A button that allows the user to add a new friend.
     * @param friendId The username of the friend they are trying to add.
     * @param mFirestore A reference to the Firebase.
     */
    private void addFriendButton(String friendId, FirebaseFirestore mFirestore) {
        usersCollection.whereEqualTo("uid", user.getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                    /**
                     * Searches if the username entered is present in the Firebase.
                     * If so, the username is added to the user's list of friends.
                     * @param task
                     */
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot documentSnapshot = task.getResult();
                            List<DocumentSnapshot> documentSnapshotList = documentSnapshot.getDocuments();
                            if (documentSnapshotList.size() > 0) {
                                DocumentReference docRef = usersCollection.document(documentSnapshotList.get(0).getId());
                                Task<Void> tsk = addFriend(friendId, docRef, mFirestore);
                                if (tsk.isSuccessful()) {
                                    System.out.println("addingFriend: taskSuccess");
                                } else {
                                    System.out.println("addingFriend: taskFailed");
                                }
                            }
                        }
                    }
        });
    }

    /**
     * Handles the logic behind adding a friend.
     * @param friendUid The user ID of the friend to be added.
     * @param userRef A reference to the user.
     * @param mFirestore A reference to the Firebase.
     * @return A task, likely null.
     */
    private Task<Void> addFriend(String friendUid, DocumentReference userRef, FirebaseFirestore mFirestore) {
        // Push to database
        return mFirestore.runTransaction(new Transaction.Function<Void>() {

            /**
             * Applies proper information about the user, friend, and Firebase for adding a friend.
             * @param transaction The transaction between the user and Firebase.
             * @return A task, likely null.
             * @throws FirebaseFirestoreException Exception if the Firebase is unruly accessed.
             */
            @Nullable
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                DocumentSnapshot currUserDoc = transaction.get(userRef);
                String username = currUserDoc.get("username").toString();
                String uid = currUserDoc.get("uid").toString();
                List<String> friends = (List<String>) currUserDoc.get("friends");

                User currentUser = new User(username, uid);
                for (String friend : friends) {
                    currentUser.addFriend(friend);
                }

                // Update current user friends list
                currentUser.addFriend(friendUid);

                transaction.set(userRef, currentUser);
                return null;
            }
        });
    }

    /**
     * The logic that goes behind pressing the button to change your username.
     * @param username The new username to be entered in for the user.
     * @param mFirestore A reference to the Firebase.
     */
    private void updateUsernameButton(String username, FirebaseFirestore mFirestore) {
        usersCollection.whereEqualTo("uid", user.getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                    /**
                     * Updates the references for the previous username when it comes to the user's old posts.
                     * @param task The task that is being completed.
                     */
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot documentSnapshot = task.getResult();
                            List<DocumentSnapshot> documentSnapshotList = documentSnapshot.getDocuments();
                            if (documentSnapshotList.size() > 0) {
                                DocumentReference docRef = usersCollection.document(documentSnapshotList.get(0).getId());
                                Task<Void> tsk = updateUsername(username, docRef, mFirestore);
                                if (tsk.isSuccessful()) {
                                    System.out.println("updatingUsername: taskSuccess");
                                } else {
                                    System.out.println("updatingUsername: taskFailed");
                                }
                            }
                        }
                    }
                });
    }

    /**
     * Handles logic behind updating a user's username is correlation to their friends list.
     * @param username The new username.
     * @param userRef The reference to the user.
     * @param mFirestore A reference to the Firebase.
     * @return A task, likely null.
     */
    private Task<Void> updateUsername(String username, DocumentReference userRef, FirebaseFirestore mFirestore) {
        // Push to database
        return mFirestore.runTransaction(new Transaction.Function<Void>() {

            /**
             * Applies proper information about the user, user's friends, and Firebase for changing their username.
             * @param transaction The transaction between the user and Firebase.
             * @return A task, likely null.
             * @throws FirebaseFirestoreException Exception if the Firebase is unruly accessed.
             */
            @Nullable
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                DocumentSnapshot currUserDoc = transaction.get(userRef);
                String uid = currUserDoc.get("uid").toString();
                List<String> friends = (List<String>) currUserDoc.get("friends");

                // Recreate the user
                User currentUser = new User(username, uid);
                for (String friend : friends) {
                    currentUser.addFriend(friend);
                }

                transaction.set(userRef, currentUser);
                return null;
            }
        });
    }
}
