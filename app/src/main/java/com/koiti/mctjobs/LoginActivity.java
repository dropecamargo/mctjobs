package com.koiti.mctjobs;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.system.ErrnoException;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.koiti.mctjobs.helpers.Constants;
import com.koiti.mctjobs.helpers.RestClientApp;
import com.koiti.mctjobs.helpers.UserSessionManager;
import com.koiti.mctjobs.helpers.Utils;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.conn.ConnectTimeoutException;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends ActionBarActivity {
    private static final String TAG = LoginActivity.class.getSimpleName();

    private UserSessionManager mSession;
    private RestClientApp mRestClientApp;
    private Tracker tracker;

    private LinearLayout mLoginForm;
    private LinearLayout mLoginPhone;
    private LinearLayout mLoginPhoneConfirm;
    private RadioButton mInternal;
    private RadioButton mExternal;
    private AutoCompleteTextView mAccountView;
    private EditText mPasswordView;
    private EditText mPhoneView;
    private EditText mPhoneCodeView;
    private TextView mPhoneConfirmMessage;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Get tracker.
        tracker = ((Application) getApplication()).getTracker();

        // Session
        mSession = new UserSessionManager(this);

        // Rest client
        mRestClientApp = new RestClientApp(this);

        // Set up the login form.
        mLoginForm = (LinearLayout) findViewById(R.id.account_login_form);
        mLoginPhone = (LinearLayout) findViewById(R.id.phone_login_form);
        mLoginPhoneConfirm = (LinearLayout) findViewById(R.id.phone_login_confirm);
        mInternal = (RadioButton) findViewById(R.id.type_internal);
        mExternal = (RadioButton) findViewById(R.id.type_external);
        mPhoneConfirmMessage = (TextView) findViewById(R.id.phone_confirm_message);

        mPhoneView = (EditText) findViewById(R.id.phone);
        mPhoneCodeView = (EditText) findViewById(R.id.phone_code_confirm);
        mAccountView = (AutoCompleteTextView) findViewById(R.id.account);
        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
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

        // Toolbar
        getSupportActionBar().setTitle( R.string.title_activity_login );
        getSupportActionBar().setSubtitle( getResources().getString(R.string.app_name) + " - " + getResources().getString(R.string.app_def_name) );
    }

    public void typeLogin(View view) {
        if( mInternal.isChecked() ) {
            mLoginForm.setVisibility(View.VISIBLE);
            mLoginPhone.setVisibility(View.GONE);
            mLoginPhoneConfirm.setVisibility(View.GONE);
        }else {
            mLoginForm.setVisibility(View.GONE);
            mLoginPhone.setVisibility(View.VISIBLE);

            mPhoneView.requestFocus();
        }
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

                    // Send sms
                    sendSmsVerification(phone, country_code);
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
            if (throwable.getCause() instanceof ConnectTimeoutException || throwable.getCause() instanceof ErrnoException) {
                Toast.makeText(LoginActivity.this, R.string.on_host_exception, Toast.LENGTH_LONG).show();
            }
        }catch (Exception e) {
            Toast.makeText(LoginActivity.this, R.string.on_host_exception, Toast.LENGTH_LONG).show();

            // Tracker exception
            tracker.send(new HitBuilders.ExceptionBuilder()
                    .setDescription(String.format("%s:AccessToken:%s", TAG, e.getLocalizedMessage()))
                    .setFatal(false)
                    .build());
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
            tracker.send(new HitBuilders.ExceptionBuilder()
                    .setDescription(String.format("%s:LoginAccount:%s", TAG, e.getLocalizedMessage()))
                    .setFatal(false)
                    .build());

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
            mRestClientApp.loginAccount(account, password, oaut, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    // Valid response
                    try
                    {
                        if( response.getBoolean("successful") == true) {
                            // Start session
                            JSONObject data = response.getJSONObject("data");
                            JSONObject partner = data.getJSONObject("partner");
                            mSession.startSession(partner.getInt("id_partner"), partner.getString("fullname"), partner.getString("rol"), partner.getString("photo"),
                                    null);

                            // MainActivity
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
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
                            JSONObject error = response.getJSONObject("error");
                            Toast.makeText(LoginActivity.this, error.getString("message"), Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException | NullPointerException e ) {
                        Log.e(TAG, e.getMessage());

                        // Tracker exception
                        tracker.send(new HitBuilders.ExceptionBuilder()
                                .setDescription(String.format("%s:LoginAccount:%s", TAG, e.getLocalizedMessage()))
                                .setFatal(false)
                                .build());

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

                            // MainActivity
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
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
                        tracker.send(new HitBuilders.ExceptionBuilder()
                                .setDescription(String.format("%s:LoginAccount:%s", TAG, e.getLocalizedMessage()))
                                .setFatal(false)
                                .build());

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
}

