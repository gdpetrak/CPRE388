package com.example.project2.util;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * A class that sets up the options for Firebase usage in the application.
 */
public class FirebaseUtil {

    /**
     * A reference to the Firebase.
     */
    private static FirebaseFirestore FIRESTORE;

    /**
     * A reference to the user's authentication.
     */
    private static FirebaseAuth AUTH;

    /**
     * A reference to the user's user ID inside of Firebase.
     */
    private static AuthUI AUTH_UI;

    /**
     * Obtains the current Firebase database.
     * @return FIRESTORE
     */
    public static FirebaseFirestore getFirestore() {
        if (FIRESTORE == null) {
            FIRESTORE = FirebaseFirestore.getInstance();
        }
        return FIRESTORE;
    }

    /**
     * Obtains the current user's authentication.
     * @return AUTH
     */
    public static FirebaseAuth getAuth() {
        if (AUTH == null) {
            AUTH = FirebaseAuth.getInstance();
        }
        return AUTH;
    }

    /**
     * Obtains the current user's user ID in Firebase.
     * @return AUTH_UI
     */
    public static AuthUI getAuthUI() {
        if (AUTH_UI == null) {
            AUTH_UI = AuthUI.getInstance();
        }
        return AUTH_UI;
    }

}