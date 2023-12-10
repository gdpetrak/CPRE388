package com.example.project2.Activities;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.project2.Database.User;
import com.example.project2.R;
import com.example.project2.util.Collections;
import com.example.project2.util.FirebaseUtil;
import com.example.project2.util.FriendAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.example.project2.Activities.ProfileSettingsActivity;
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
import java.util.List;
public class FriendViewActivity extends AppCompatActivity {

    private FirebaseFirestore mFirestore;
    private CollectionReference usersCollection;
    FriendAdapter friendAdapter;
    ArrayList<String> usernamesView = new ArrayList<>();

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
        // Init user
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
                                User currUser = documentSnapshotList.get(0).toObject(User.class);
                                List<String> friends = currUser.getFriends();
                                for (String friend:friends) {
                                    usernamesView.add(friend);
                                }
//                                for(int i=0; i<friends.size()-1; i++){
//                                    usernamesView.add(friends.get(i));
//                                }
                            }

                        }
                    }
                });
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(FriendViewActivity.this, HomeActivity.class));
            }
        });

        addFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // addFriend()
            }
        });
    }
    private Task<Void> addFriend(String friendUid, DocumentReference userRef, FirebaseFirestore mFirestore) {
        // Push to database
        return mFirestore.runTransaction(new Transaction.Function<Void>() {
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
}
