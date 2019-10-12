package com.example.foodcustomerapp.Helper;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.example.foodcustomerapp.Login;
import com.example.foodcustomerapp.MainActivity;

import java.util.HashMap;

public class SessionManagement {
    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    SharedPreferences.Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "FoodcustomerappSession";

    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";

    // User name (make variable public to access from outside)
    public static final String KEY_CustomerNo = "CustomerNo";

    // Email address (make variable public to access from outside)
    public static final String KEY_CustomerId = "CustomerId";

    public static final String KEY_CustomerName = "CustomerName";

    // Constructor
    public SessionManagement(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }



    /**
     * Create login session
     * */
    public void createLoginSession(String cstId, String cstName,String cstNumber){
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_CustomerNo, cstNumber);
        editor.putString(KEY_CustomerId, cstId);
        editor.putString(KEY_CustomerName, cstName);
        editor.commit();
    }


    /**
     * Get stored session data
     * */
    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<String, String>();
        user.put(KEY_CustomerNo, pref.getString(KEY_CustomerNo, null));
        user.put(KEY_CustomerId, pref.getString(KEY_CustomerId, null));
        user.put(KEY_CustomerName, pref.getString(KEY_CustomerName, null));

        // return user
        return user;
    }



    public void logoutUser(){
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();

        // After logout redirect user to Loing Activity
        Intent i = new Intent(_context, MainActivity.class);
        // Closing all the Activities
        i.addFlags(i.FLAG_ACTIVITY_CLEAR_TOP | i.FLAG_ACTIVITY_CLEAR_TASK |i.FLAG_ACTIVITY_NEW_TASK);
        // Staring Login Activity
        _context.startActivity(i);

    }


    /**
     * Check login method wil check user login status
     * If false it will redirect user to login page
     * Else won't do anything
     * */
    public void checkLogin(){
        // Check login status
        if(!this.isLoggedIn()){
            // user is not logged in redirect him to Login Activity
            Intent i = new Intent(_context, Login.class);
            // Closing all the Activities
            i.addFlags(i.FLAG_ACTIVITY_CLEAR_TOP | i.FLAG_ACTIVITY_CLEAR_TASK |i.FLAG_ACTIVITY_NEW_TASK);
            // Staring Login Activity
            _context.startActivity(i);

        }

    }






    /**
     * Quick check for login
     * **/
    // Get Login State
    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }
}
