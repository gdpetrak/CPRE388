package com.example.project2.Activities;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.project2.Database.User;
import com.example.project2.R;
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

public class ProfileSettingsActivity extends AppCompatActivity {
    private static final String USER_COLLECTION_LOCATION = "users";
    private CollectionReference usersCollection;
    private FirebaseFirestore mFirestore;
    private FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_settings);

        // Init firebase
        FirebaseAuth mAuth = FirebaseUtil.getAuth();
        user = mAuth.getCurrentUser();
        FirebaseFirestore.setLoggingEnabled(true);
        mFirestore = FirebaseUtil.getFirestore();
        usersCollection = mFirestore.collection(USER_COLLECTION_LOCATION);

        // Init alert builder
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setMessage("Are you sure you want to delete your account?\n" +
                        "(Once an account is deleted, there is no way to recover it)")
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        assert user != null;
                        user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
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
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        Dialog deleteAccountAlert = alertBuilder.create();

        // Init layout references
        Button signOutButton = findViewById(R.id.sign_out);
        Button deleteAccountButton = findViewById(R.id.delete_account);
        Button backButton = findViewById(R.id.back_button);

        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                startActivity(new Intent(ProfileSettingsActivity.this, LandingActivity.class));
            }
        });

        deleteAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteAccountAlert.show();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                finish();
                addFriendButton(mFirestore);
                System.out.println("addingFriend: finishedButton");
            }
        });
    }

    private void addFriendButton(FirebaseFirestore mFirestore) {
        usersCollection.whereEqualTo("uid", user.getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot documentSnapshot = task.getResult();
                    List<DocumentSnapshot> documentSnapshotList = documentSnapshot.getDocuments();
                    if (documentSnapshotList.size() > 0) {
                        DocumentReference docRef = usersCollection.document(documentSnapshotList.get(0).getId());
                        System.out.println("addingFriend: docref found " + docRef);
                        Task<Void> tsk = addFriend("vYMg4DfmXzRQC0SQ4dyLbHIfZ7G3", docRef, mFirestore);
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

    private Task<Void> addFriend(String friendUid, DocumentReference userRef, FirebaseFirestore mFirestore) {
        System.out.println("addingFriend: addFriendCalled");
        // Push to database
        return mFirestore.runTransaction(new Transaction.Function<Void>() {
            @Nullable
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                System.out.println("addingFriend: applyTransaction");
//                User currentUser = transaction.get(userRef).toObject(User.class);
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
}
