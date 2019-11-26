package com.koiti.mctjobs.helpers;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.Spanned;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;

import com.koiti.mctjobs.HomeActivity;
import com.koiti.mctjobs.LoginActivity;
import com.koiti.mctjobs.R;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Utils {

    public static String capitalize(String s) {
        if (s.length() == 0) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
    }

    public static AlertDialog.Builder dialogBuilder(Context context) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            alertDialog = new AlertDialog.Builder(context, android.R.style.Theme_Material_Light_Dialog_Alert);
        }
        return alertDialog;
    }

    public static Spanned fromHtml(String s) {
        return Html.fromHtml(s.trim().toString());
    }

    public static void showProgress(final boolean show, final View mFormView, final View mProgressView) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = Resources.getSystem().getInteger(android.R.integer.config_shortAnimTime);

            mFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    public static String encrypt(String cadena) throws UnsupportedEncodingException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException {
        Key key = new SecretKeySpec("MCTSASKEYAES128B".getBytes("UTF-8"), "AES");
        Cipher chiper = Cipher.getInstance("AES");
        chiper.init(Cipher.ENCRYPT_MODE, key);
        byte[] encVal = chiper.doFinal(cadena.getBytes());
        byte[] encryptedValue = Base64.encode(encVal, Base64.DEFAULT);
        return new String(encryptedValue);
    }

    public static List<String> getPermissionBasic(@NonNull Activity activity, String type){
        List<String> permissionList = new ArrayList<>();
        if ( Build.VERSION.SDK_INT >= 23) {

            switch (type) {
                case "BASIC":
                    String gpsPermission = Manifest.permission.ACCESS_FINE_LOCATION;
                    String statePhonePermission = Manifest.permission.READ_PHONE_STATE;
                    if (!Utils.hasPermission(activity, gpsPermission)) {
                        permissionList.add(gpsPermission);
                    }

                    if (!Utils.hasPermission(activity, statePhonePermission)) {
                        permissionList.add(statePhonePermission);
                    }
                    break;

                case "CAMERA":
                    String storagePermission = Manifest.permission.READ_EXTERNAL_STORAGE;
                    String photoPermission = Manifest.permission.CAMERA;
                    if (!Utils.hasPermission(activity, storagePermission)) {
                        permissionList.add(storagePermission);
                    }

                    if (!Utils.hasPermission(activity, photoPermission)) {
                        permissionList.add(photoPermission);
                    }
                    break;
            }

        }
        return permissionList;
    }

    public static boolean hasPermission(@NonNull Activity activity, String permission) {
        return  ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED;
    }

    public static AlertDialog.Builder permissionsDialogBuilder(Context context, String message, DialogInterface.OnClickListener negativeButton,
                                                               DialogInterface.OnClickListener positiveButton) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            alertDialog = new AlertDialog.Builder(context, android.R.style.Theme_Material_Light_Dialog_Alert);
        }
        alertDialog.setCancelable(false);
        alertDialog.setMessage(context.getResources().getString(R.string.text_permissions_basic_error, message)).setTitle(R.string.title_permissions_error);
        alertDialog.setNegativeButton("SALIR", negativeButton);
        alertDialog.setPositiveButton("CONFIGURACIÓN DE APPS", positiveButton);
        return alertDialog;
    }

    public static boolean verifyPermissions(int[] grantResults) {
        // At least one result must be checked.
        if(grantResults.length < 1){
            return false;
        }

        // Verify that each required permission has been granted, otherwise return false.
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
}
