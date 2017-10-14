package com.koiti.mctjobs.helpers;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.koiti.mctjobs.LoginActivity;
import com.koiti.mctjobs.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class UserSessionManager {

    private SharedPreferences pref;
    private Editor editor;
    private Context context;

    private static final String PREFER_NAME = "MCTUserPreferences";
    private static final String IS_USER_LOGIN = "IsUserLoggedIn";
    private static final String IS_USER_TERMS = "IsUserAcceptTerms";

    public static final String KEY_PARTNER = "id_partner";
    public static final String KEY_NAME = "name";
    public static final String KEY_ROL = "rol";
    public static final String KEY_PHOTO = "photo";
    public static final String KEY_PHONE = "phone";

    // Constructor
    public UserSessionManager(Context context){
        this.context = context;
        pref = context.getSharedPreferences(PREFER_NAME, Constants.PRIVATE_MODE);
        editor = pref.edit();
    }

    public void startSession(Integer partner, String name, String rol, String photo, String phone) {
        // Storing login value as TRUE
        editor.putBoolean(IS_USER_LOGIN, true);
        editor.putBoolean(IS_USER_TERMS, false);

        editor.putString(KEY_NAME, name);
        editor.putInt(KEY_PARTNER, partner);
        editor.putString(KEY_ROL, rol);
        editor.putString(KEY_PHOTO, photo);
        editor.putString(KEY_PHONE, phone);

        // Commit changes
        editor.commit();
    }

    /**
     * Clear session details
     * */
    public void logout() {

        // Clearing all user data from Shared Preferences
        editor.clear();
        editor.commit();

        // After logout redirect user to Login Activity
        Intent intent = new Intent(context, LoginActivity.class);
        // Closing all the Activities
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        // CLear all the Activities from stack
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        // Add new Flag to start new Activity
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // Staring Login Activity
        context.startActivity(intent);
    }

    /**
     * Check login method will check user login status
     * If false it will redirect user to login page
     * Else do anything
     * */
    public boolean checkLogin(){
        // Check login status
        if(!this.isUserLoggedIn()) {

            // User is not logged in redirect him to Login Activity
            Intent intent = new Intent(context, LoginActivity.class);
            // Closing all the Activities from stack
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            // CLear all the Activities from stack
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            // Add new Flag to start new Activity
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            // Staring Login Activity
            context.startActivity(intent);

            return true;
        }
        return false;
    }

    public boolean getTerms() {
        return pref.getBoolean(IS_USER_TERMS, false);
    }

    public void setTerms(Boolean terms) {
        editor.putBoolean(IS_USER_TERMS, terms);
        editor.commit();
    }

    public boolean isUserLoggedIn() {
        return pref.getBoolean(IS_USER_LOGIN, false);
    }

    public Integer getPartner() {
        return pref.getInt(KEY_PARTNER, 0);
    }

    public String getName() {
        return pref.getString(KEY_NAME, "");
    }

    public String getImage() {
        return pref.getString(KEY_PHOTO, "");
    }

    public String getRol() {
        return pref.getString(KEY_ROL, "");
    }
}
