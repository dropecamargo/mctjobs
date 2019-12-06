package com.koiti.mctjobs;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.system.ErrnoException;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.koiti.mctjobs.helpers.Constants;
import com.koiti.mctjobs.helpers.RestClientApp;
import com.koiti.mctjobs.helpers.UserSessionManager;
import com.koiti.mctjobs.helpers.Utils;
import com.koiti.mctjobs.services.TrackerGpsService;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.conn.ConnectTimeoutException;


/**
 * A login screen that offers login via email/password.
 * 0rg0m3Z.
 */
public class LoginActivity extends AppCompatActivity {
    private static final String TAG = LoginActivity.class.getSimpleName();
    static final Integer PERMISSIONS_BASIC_GPS_STATE = 0x1;

    private UserSessionManager mSession;
    private RestClientApp mRestClientApp;

    private LinearLayout mLoginForm;
    private LinearLayout mLoginPhone;
    private LinearLayout mLoginPhoneConfirm;
    private ImageView mLoginBackground;
    private ImageView mLoginLogo;
    private ImageView mLoginType;
    private PopupMenu mPopupMenu;

    private AutoCompleteTextView mAccountView;
    private EditText mPasswordView;
    private EditText mPhoneView;
    private EditText mPhoneCodeView;
    private TextView mPhoneConfirmMessage;
    private View mProgressView;
    private View mLoginFormView;

    private Boolean isOpenModalPermissions = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Session
        mSession = new UserSessionManager(this);

        // Rest client
        mRestClientApp = new RestClientApp(this);

        // Set up the login form.

        mLoginBackground = (ImageView) findViewById(R.id.login_background);
        mLoginLogo = (ImageView) findViewById(R.id.login_logo);
        mLoginType = (ImageView) findViewById(R.id.login_type);

        mLoginForm = (LinearLayout) findViewById(R.id.account_login_form);
        mLoginPhone = (LinearLayout) findViewById(R.id.phone_login_form);
        mLoginPhoneConfirm = (LinearLayout) findViewById(R.id.phone_login_confirm);
        mPhoneConfirmMessage = (TextView) findViewById(R.id.phone_confirm_message);

        mPhoneView = (EditText) findViewById(R.id.phone);
        mPhoneCodeView = (EditText) findViewById(R.id.phone_code_confirm);
        mAccountView = (AutoCompleteTextView) findViewById(R.id.account);
        mPasswordView = (EditText) findViewById(R.id.password);

        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    attemptLogin();
                }
                return false;
            }
        });

        // actionSend phone
        mPhoneView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    attemptLoginPhone();
                }
                return false;
            }
        });

        // actionSend phone
        mPhoneCodeView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    attemptLoginPhoneConfirm();
                }
                return false;
            }
        });

        // Account sign in button
        Button accountSignInButton = (Button) findViewById(R.id.sign_in_button);
        accountSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        // Phone sign in button
        Button phoneSignInButton = (Button) findViewById(R.id.sign_in_button_phone);
        phoneSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLoginPhone();
            }
        });

        // Phone confirm back sign in button
        Button phoneConfirmBackButton = (Button) findViewById(R.id.sign_in_button_phone_back);
        phoneConfirmBackButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mLoginForm.setVisibility(View.GONE);
                mLoginPhoneConfirm.setVisibility(View.GONE);
                mLoginPhone.setVisibility(View.VISIBLE);
                // Clean code
                mPhoneCodeView.setText("");
            }
        });

        // Phone confirm back sign in button
        Button phoneConfirmButton = (Button) findViewById(R.id.sign_in_button_phone_confirm);
        phoneConfirmButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLoginPhoneConfirm();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        // Load image background
        ImageLoader loaderBackground = ImageLoader.getInstance();
        DisplayImageOptions optionsBackground = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisc(true).resetViewBeforeLoading(true).build();
        loaderBackground.displayImage("drawable://" + R.drawable.login_background, mLoginBackground, optionsBackground);

        // Load image logo
        ImageLoader loaderLogo = ImageLoader.getInstance();
        DisplayImageOptions optionsLogo = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisc(true).resetViewBeforeLoading(true).build();
        loaderLogo.displayImage("drawable://" + R.drawable.logo_mtc_white, mLoginLogo, optionsLogo);

        // Popup type menu
        mPopupMenu = new PopupMenu(LoginActivity.this, mLoginType);
        MenuInflater menuInflater = mPopupMenu.getMenuInflater();
        menuInflater.inflate(R.menu.login, mPopupMenu.getMenu());
        mPopupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.login_internal:
                        mLoginForm.setVisibility(View.VISIBLE);
                        mLoginPhone.setVisibility(View.GONE);
                        mLoginPhoneConfirm.setVisibility(View.GONE);
                        break;
                    case R.id.login_external:
                        mLoginForm.setVisibility(View.GONE);
                        mLoginPhoneConfirm.setVisibility(View.GONE);
                        mLoginPhone.setVisibility(View.VISIBLE);
                        break;
                }
                return true;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Check permissions
        if(!isOpenModalPermissions) {
            checkPermissions();
        }
    }

    public void checkPermissions() {
        List<String> permissionList  = Utils.getPermissionBasic(LoginActivity.this, "BASIC");
        if (!permissionList.isEmpty()){
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(LoginActivity.this, permissions, PERMISSIONS_BASIC_GPS_STATE);
        }
    }

    public void typeLogin(View view) {
        mPopupMenu.show();
    }

    /**
     * Attempts to sign in phone
     */
    private void attemptLoginPhone() {
        // Reset errors.
        mPhoneView.setError(null);

        // Store values at the time of the login attempt.
        final String phone = mPhoneView.getText().toString();
        final String country_code = Constants.COUNTRY_CODE;

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(phone)) {
            mPhoneView.setError(getString(R.string.error_invalid_phone));
            focusView = mPhoneView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {

            // Confirm action
            AlertDialog.Builder alertDialog = Utils.dialogBuilder(this);
            alertDialog.setMessage("Procederemos a verificar el número:\n +" + country_code + " " + phone
                    + "\n¿Quieres continuar o deseas editar el número?");
            alertDialog.setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // Show a progress spinner, and kick off a background task to
                    // perform the user login attempt.
                    Utils.showProgress(true, mLoginFormView, mProgressView);

                    try {
                        mRestClientApp.getAccessToken(new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                validCellphone(phone, country_code, response);
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
                                onFailureAccessToken(throwable, response);
                            }
                        });
                    } catch (JSONException | IOException | NoSuchAlgorithmException | KeyManagementException | UnrecoverableKeyException |
                            CertificateException | KeyStoreException e ) {
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(LoginActivity.this, R.string.on_failure, Toast.LENGTH_LONG).show();
                        Utils.showProgress(false, mLoginFormView, mProgressView);
                    }
                }
            });

            // on pressing cancel button
            alertDialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            // Showing Alert Message
            alertDialog.show();
        }
    }

    public void validCellphone(final String phone, final String country_code, JSONObject oaut)
    {
        try {
            mRestClientApp.validCellphone(phone, oaut, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    // Valid response
                    try
                    {
                        if( response.getBoolean("successful") == true) {
                            // sendSmsVerification
                            sendSmsVerification(phone, country_code);
                        }
                    } catch (JSONException | NullPointerException e ) {
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(LoginActivity.this, R.string.on_failure, Toast.LENGTH_LONG).show();

                        // Hide progress.
                        Utils.showProgress(false, mLoginFormView, mProgressView);
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
                    // Valid response
                    try
                    {
                        if(response == null) {
                            throw new NullPointerException(getResources().getString(R.string.on_null_server_exception));
                        }

                        if( response.getBoolean("successful") == false) {
                            Toast.makeText(LoginActivity.this, response.getString("error"), Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException | NullPointerException e ) {
                        Log.e(TAG, e.getMessage());

                        // Tracker exception
                        Crashlytics.logException(e);

                        Toast.makeText(LoginActivity.this, R.string.on_failure, Toast.LENGTH_LONG).show();
                    } finally {
                        // Hide progress.
                        Utils.showProgress(false, mLoginFormView, mProgressView);
                    }
                }
            });
        } catch (JSONException | UnsupportedEncodingException e ) {
            Log.e(TAG, e.getMessage());
            Toast.makeText(LoginActivity.this, R.string.on_failure, Toast.LENGTH_LONG).show();
        }
    }

    public void sendSmsVerification(String phone, String country_code)
    {
        try {
            mRestClientApp.sendSmsVerification(phone, country_code, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    try
                    {
                        if(response == null) {
                            throw new NullPointerException(getResources().getString(R.string.on_null_server_twilio_exception));
                        }

                        String message = response.getString("message");
                        mPhoneConfirmMessage.setText(message);

                        mLoginPhone.setVisibility(View.GONE);
                        mLoginPhoneConfirm.setVisibility(View.VISIBLE);

                    } catch (JSONException | NullPointerException e ) {
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(LoginActivity.this, R.string.on_failure, Toast.LENGTH_LONG).show();

                    }finally {
                        // Hide progress.
                        Utils.showProgress(false, mLoginFormView, mProgressView);
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
                    onFailureTwilio(response);
                }
            });
        } catch (JSONException | UnsupportedEncodingException e ) {
            Log.e(TAG, e.getMessage());
            Toast.makeText(LoginActivity.this, R.string.on_failure, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Attempts to sign in phone confirm
     */
    private void attemptLoginPhoneConfirm() {
        // Reset errors.
        mPhoneCodeView.setError(null);

        // Store values at the time of the login attempt.
        final String phone = mPhoneView.getText().toString();
        final String code = mPhoneCodeView.getText().toString();
        final String country_code = Constants.COUNTRY_CODE;

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(code)) {
            mPhoneCodeView.setError(getString(R.string.error_invalid_phone_code));
            focusView = mPhoneCodeView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            Utils.showProgress(true, mLoginFormView, mProgressView);

            try {
                mRestClientApp.checkCodeVerification(phone, code, country_code, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            mRestClientApp.getAccessToken(new JsonHttpResponseHandler() {
                                @Override
                                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                    loginPhone(phone, response);
                                }

                                @Override
                                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
                                    onFailureAccessToken(throwable, response);
                                }
                            });
                        } catch (JSONException | IOException | NoSuchAlgorithmException | KeyManagementException | UnrecoverableKeyException |
                                CertificateException | KeyStoreException e ) {
                            Utils.showProgress(false, mLoginFormView, mProgressView);

                            Log.e(TAG, e.getMessage());
                            Toast.makeText(LoginActivity.this, R.string.on_failure, Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
                        onFailureTwilio(response);
                    }
                });
            } catch (JSONException | UnsupportedEncodingException e ) {
                Log.e(TAG, e.getMessage());
                Toast.makeText(LoginActivity.this, R.string.on_failure, Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        // Reset errors.
        mAccountView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        final String account = mAccountView.getText().toString();
        final String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(account)) {
            mAccountView.setError(getString(R.string.error_field_required));
            focusView = mAccountView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            Utils.showProgress(true, mLoginFormView, mProgressView);

            try {
                mRestClientApp.getAccessToken(new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        loginAccount(account, password, response);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
                        onFailureAccessToken(throwable, response);
                    }
                });
            } catch (JSONException | IOException | NoSuchAlgorithmException | KeyManagementException | UnrecoverableKeyException |
                    CertificateException | KeyStoreException e ) {
                Log.e(TAG, e.getMessage());
                Toast.makeText(LoginActivity.this, R.string.on_failure, Toast.LENGTH_LONG).show();
            }
        }
    }

    public void onFailureAccessToken(Throwable throwable, JSONObject response) {
        try {
            if (response == null) {
                throw new NullPointerException(getResources().getString(R.string.on_null_server_exception));
            }

            // Connect timeout exception
            if (throwable.getCause() instanceof ConnectTimeoutException) {
                Toast.makeText(LoginActivity.this, R.string.on_host_exception, Toast.LENGTH_LONG).show();
            }

            // Connect timeout exception
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (throwable.getCause() instanceof ErrnoException) {
                    Toast.makeText(LoginActivity.this, R.string.on_host_exception, Toast.LENGTH_LONG).show();
                }
            }
        }catch (Exception e) {
            Toast.makeText(LoginActivity.this, R.string.on_host_exception, Toast.LENGTH_LONG).show();

            // Tracker exception
            Crashlytics.logException(e);
        }finally {
            // Hide progress.
            Utils.showProgress(false, mLoginFormView, mProgressView);
        }
    }

    public void onFailureTwilio(JSONObject response) {
        try
        {
            if(response == null) {
                throw new NullPointerException(getResources().getString(R.string.on_null_server_twilio_exception));
            }

            String message = response.getString("message");
            if( !message.isEmpty() ) {
                Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
            }
        } catch (JSONException | NullPointerException e ) {
            Log.e(TAG, e.getMessage());

            // Tracker exception
            Crashlytics.logException(e);

            Toast.makeText(LoginActivity.this, R.string.on_failure, Toast.LENGTH_LONG).show();
        } finally {
            // Hide progress.
            Utils.showProgress(false, mLoginFormView, mProgressView);
        }
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    public void loginAccount(String account, String password, JSONObject oaut) {
        try {
            mRestClientApp.loginAccount(account, Utils.encrypt(password), oaut, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    // System.out.println("onSuccess: " + response.toString());
                    // Valid response
                    try
                    {
                        if( response.getBoolean("successful") == true) {
                            // Start session
                            JSONObject data = response.getJSONObject("data");
                            JSONObject partner = data.getJSONObject("partner");
                            mSession.startSession(partner.getInt("id_partner"), partner.getString("fullname"), partner.getString("rol"), partner.getString("photo"),
                                    null);

                            // HomeActivity
                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            startActivity(intent);
                            finish();
                        }else{
                            JSONObject error = response.getJSONObject("error");
                            Toast.makeText(LoginActivity.this, error.getString("msg"), Toast.LENGTH_LONG).show();

                            // Hide progress.
                            Utils.showProgress(false, mLoginFormView, mProgressView);
                        }
                    } catch (JSONException | NullPointerException e ) {
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(LoginActivity.this, R.string.on_failure, Toast.LENGTH_LONG).show();

                        // Hide progress.
                        Utils.showProgress(false, mLoginFormView, mProgressView);
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
                    // System.out.println("onFailure: " + response.toString());
                    // Valid response
                    try
                    {
                        if(response == null) {
                            throw new NullPointerException(getResources().getString(R.string.on_null_server_exception));
                        }

                        if( response.getBoolean("successful") == false) {
                            JSONObject error = response.getJSONObject("error");
                            Toast.makeText(LoginActivity.this, error.getString("msg"), Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException | NullPointerException e ) {
                        Log.e(TAG, e.getMessage());

                        // Tracker exception
                        Crashlytics.logException(e);

                        Toast.makeText(LoginActivity.this, R.string.on_failure, Toast.LENGTH_LONG).show();
                    } finally {
                        // Hide progress.
                        Utils.showProgress(false, mLoginFormView, mProgressView);
                    }
                }
            });
        } catch (JSONException | UnsupportedEncodingException | BadPaddingException | NoSuchPaddingException |
                InvalidKeyException | NoSuchAlgorithmException | IllegalBlockSizeException e ) {
            Log.e(TAG, e.getMessage());
            Toast.makeText(LoginActivity.this, R.string.on_failure, Toast.LENGTH_LONG).show();
        }
    }

    public void loginPhone(final String phone, JSONObject oaut) {
        try {
            mRestClientApp.loginPhone(phone, oaut, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    // Valid response
                    try
                    {
                        if( response.getBoolean("successful") == true) {
                            // Start session
                            JSONObject data = response.getJSONObject("data");
                            JSONObject partner = data.getJSONObject("partner");
                            JSONObject vehicle = data.getJSONObject("vehicle");

                            // Start session
                            mSession.startSession(partner.getInt("id_partner"), partner.getString("fullname"), partner.getString("rol"), partner.getString("photo"),
                                    phone);

                            // HomeActivity
                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            startActivity(intent);
                            finish();
                        }else{
                            JSONObject error = response.getJSONObject("error");
                            Toast.makeText(LoginActivity.this, error.getString("message"), Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException | NullPointerException e ) {
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(LoginActivity.this, R.string.on_failure, Toast.LENGTH_LONG).show();

                    }finally {
                        // Hide progress.
                        Utils.showProgress(false, mLoginFormView, mProgressView);
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
                    // Valid response
                    try
                    {
                        if(response == null) {
                            throw new NullPointerException(getResources().getString(R.string.on_null_server_exception));
                        }

                        if( response.getBoolean("successful") == false) {
                            JSONObject error = response.getJSONObject("error");
                            Toast.makeText(LoginActivity.this, error.getString("message"), Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException | NullPointerException e ) {
                        Log.e(TAG, e.getMessage());

                        // Tracker exception
                        Crashlytics.logException(e);

                        Toast.makeText(LoginActivity.this, R.string.on_failure, Toast.LENGTH_LONG).show();
                    } finally {
                        // Hide progress.
                        Utils.showProgress(false, mLoginFormView, mProgressView);
                    }
                }
            });
        } catch (JSONException | UnsupportedEncodingException e ) {
            Log.e(TAG, e.getMessage());
            Toast.makeText(LoginActivity.this, R.string.on_failure, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSIONS_BASIC_GPS_STATE) {
            if (Utils.verifyPermissions(grantResults)) {
                startService(new Intent(LoginActivity.this, TrackerGpsService.class));

            } else {
                AlertDialog.Builder permissionsDialog = Utils.permissionsDialogBuilder(LoginActivity.this,
                        "\"Ubicación y Teléfono\"",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                isOpenModalPermissions = false;
                                finish();
                            }
                        },
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                isOpenModalPermissions = false;

                                Intent intent = new Intent();
                                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", getPackageName(), null);
                                intent.setData(uri);
                                startActivity(intent);
                            }
                        }
                );
                isOpenModalPermissions = true;
                permissionsDialog.show();
            }

        }
    }
}

