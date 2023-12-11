package com.example.project2.util;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * A singleton class that stores information about the Firestore to ensure
 * that instances do not have to be gotten more than once.
 */
public class FirebaseUtil {
    /**
     * Variables storing the database instances
     */
    private static FirebaseFirestore FIRESTORE;
    private static FirebaseAuth AUTH;
    private static AuthUI AUTH_UI;

    /**
     * Handles getting the Firestore instance on the first call and simply
     * returns the already retrieved instance on subsequent calls
     * @return The current instance of the Firestore
     */
    public static FirebaseFirestore getFirestore() {
        if (FIRESTORE == null) {
            FIRESTORE = FirebaseFirestore.getInstance();
        }
        return FIRESTORE;
    }

    /**
     * Handles getting the Firebase Auth instance on the first call and simply
     * returns the already retrieved instance on subsequent calls
     * @return The current instance of the Firebase Auth
     */
    public static FirebaseAuth getAuth() {
        if (AUTH == null) {
            AUTH = FirebaseAuth.getInstance();
        }
        return AUTH;
    }

    /**
     * Handles getting the Firebase AuthUI on the first call and simply
     * returns the already retrieved instance on subsequent calls
     * @return The current instance of the Firebase AuthUI
     */
    public static AuthUI getAuthUI() {
        if (AUTH_UI == null) {
            AUTH_UI = AuthUI.getInstance();
        }
        return AUTH_UI;
    }

}