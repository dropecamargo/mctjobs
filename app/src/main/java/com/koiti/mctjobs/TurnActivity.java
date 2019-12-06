package com.koiti.mctjobs;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.system.ErrnoException;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.koiti.mctjobs.fragments.MessageFragment;
import com.koiti.mctjobs.helpers.Constants;
import com.koiti.mctjobs.helpers.GPSTracker;
import com.koiti.mctjobs.helpers.RestClientApp;
import com.koiti.mctjobs.helpers.UserSessionManager;
import com.koiti.mctjobs.helpers.Utils;
import com.koiti.mctjobs.models.Office;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.conn.ConnectTimeoutException;

public class TurnActivity extends AppCompatActivity {
    private static final String TAG = TurnActivity.class.getSimpleName();

    private GPSTracker gps;

    private UserSessionManager mSession;
    private RestClientApp mRestClientApp;

    private View mFormView;
    private View mTurnFormView;
    private View mProgressView;

    // Layout vehicle
    private LinearLayout mLayoutVehicle;
    private LinearLayout mLayoutWarnings;
    private ImageView mTurnVehicle;
    private TextView turn_info_vehicle;
    private TextView turn_placa_vehicle;

    // Layout info
    private LinearLayout mLayoutInfo;
    private ImageView mTurnCity;
    private TextView turn_info_title;
    private TextView turn_info_body;

    private MaterialBetterSpinner spinner_office;
    private MaterialBetterSpinner spinner_first_destination;
    private MaterialBetterSpinner spinner_second_destination;
    private CheckBox checkbox_empty;

    private ArrayList<Office> officesList = new ArrayList<Office>();
    private ArrayList<Office> destinationsListOne = new ArrayList<Office>();
    private ArrayList<Office> destinationsListTwo = new ArrayList<Office>();

    // Layout message
    private LinearLayout mLayoutMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_turn);

        // Session
        mSession = new UserSessionManager(this);

        // Check user login
        if(mSession.checkLogin()) {
            finish();
            return;
        }

        gps = new GPSTracker(this);
        if (!gps.canGetLocation()) {
            finish();
            return;
        }

        // Rest client
        mRestClientApp = new RestClientApp(this);

        // References
        mFormView = findViewById(R.id.form_view);
        mTurnFormView = findViewById(R.id.turn_form);
        mProgressView = findViewById(R.id.progress_view);

        // Layout vehicle
        mLayoutVehicle = (LinearLayout) findViewById(R.id.layout_vehicle);
        mLayoutWarnings = (LinearLayout) findViewById(R.id.layout_warnings);
        mTurnVehicle = (ImageView) findViewById(R.id.turn_icon_vehicle);

        // Layout info
        mLayoutInfo = (LinearLayout) findViewById(R.id.layout_info);
        mTurnCity = (ImageView) findViewById(R.id.turn_logo_city);
        turn_info_title = (TextView) findViewById(R.id.turn_info_title);
        turn_info_body = (TextView) findViewById(R.id.turn_info_body);

        turn_info_vehicle = (TextView) findViewById(R.id.turn_info_vehicle);
        turn_placa_vehicle = (TextView) findViewById(R.id.turn_placa_vehicle);

        // Layout form
        spinner_office = (MaterialBetterSpinner) findViewById(R.id.spinner_office);
        spinner_first_destination = (MaterialBetterSpinner) findViewById(R.id.spinner_first_destination);
        spinner_second_destination = (MaterialBetterSpinner) findViewById(R.id.spinner_second_destination);
        checkbox_empty = (CheckBox) findViewById(R.id.checkbox_empty);

        // Layout message
        mLayoutMessage = (LinearLayout) findViewById(R.id.layout_message);

        // Show a progress spinner, and kick off a background task to
        // perform the user login attempt.
        Utils.showProgress(true, mFormView, mProgressView);

        // Toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle( getResources().getString(R.string.action_turn) );

        // Layout vehicle
        // Load image placa
        ImageLoader loaderVehiclePlaca = ImageLoader.getInstance();
        DisplayImageOptions optionsVehiclePlaca = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisc(true).resetViewBeforeLoading(true).build();
        loaderVehiclePlaca.loadImage("drawable://" + R.drawable.turn_placa_icon, null, optionsVehiclePlaca, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                BitmapDrawable drawable = new BitmapDrawable(getResources(), loadedImage);
                mLayoutVehicle.setBackgroundDrawable(drawable);
            }
        });

        // Load image vehicle
        ImageLoader loaderVehicle = ImageLoader.getInstance();
        DisplayImageOptions optionsVehicle = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisc(true).resetViewBeforeLoading(true).build();
        loaderVehicle.displayImage("drawable://" + R.drawable.turn_red_car, mTurnVehicle, optionsVehicle);

        // Start turn
        startTurn();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void startTurn() {
        try {
            mRestClientApp.getAccessToken(new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    prepareTurn(response);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
                    onFailureAccessToken(throwable, response);
                }
            });
        } catch (JSONException | IOException | NoSuchAlgorithmException | KeyManagementException | UnrecoverableKeyException |
                CertificateException | KeyStoreException e ) {
            Log.e(TAG, e.getMessage());
            Toast.makeText(TurnActivity.this, R.string.on_failure, Toast.LENGTH_LONG).show();
        }
    }

    public void prepareTurn(JSONObject oaut) {
        try {
            mRestClientApp.prepareTurn(mSession.getPartner(), gps.getLatitude(), gps.getLongitude(), oaut, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    // System.out.println("onSuccess -> " + response.toString());
                    evaluateTurn(response);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
                    System.out.println("onFailure -> " + response.toString());
                    try
                    {
                        if(response == null) {
                            throw new NullPointerException(getResources().getString(R.string.on_null_server_exception));
                        }

                        JSONObject error = response.getJSONObject("error");
                        if( !error.getString("message").isEmpty() ) {
                            Toast.makeText(TurnActivity.this, error.getString("message"), Toast.LENGTH_LONG).show();

                        }else {
                            JSONObject data = response.getJSONObject("data");
                            if (!data.getString("mensaje").isEmpty()) {
                                Toast.makeText(TurnActivity.this, data.getString("mensaje"), Toast.LENGTH_LONG).show();
                            }
                        }
                    } catch (JSONException | NullPointerException e ) {
                        Log.e(TAG, e.getMessage());

                        // Tracker exception
                        Crashlytics.logException(e);

                        Toast.makeText(TurnActivity.this, R.string.on_failure, Toast.LENGTH_LONG).show();
                    } finally {
                        // Hide progress.
                        finish();
                    }
                }
            });
        } catch (JSONException | UnsupportedEncodingException e ) {
            Log.e(TAG, e.getMessage());
            Toast.makeText(TurnActivity.this, R.string.on_failure, Toast.LENGTH_LONG).show();

            Utils.showProgress(false, mFormView, mProgressView);
        }
    }

    public void evaluateTurn(JSONObject response) {
        // System.out.println("evaluateTurn -> " + response.toString());
        try {
            // Errors
            if(response == null) {
                throw new NullPointerException(getResources().getString(R.string.on_null_server_exception));
            }

            // Valid response close session
            Boolean close_session = response.getBoolean("close_session");
            if(close_session) {
                mSession.logout();
                return;
            }

            // Data form
            Boolean sucessfull = response.getBoolean("sucessfull");
            JSONObject data = response.getJSONObject("data");
            JSONObject datavehicle = data.getJSONObject("datavehicle");
            String placa = datavehicle.getString("placa");
            String clase = datavehicle.getString("clase");

            turn_placa_vehicle.setText( placa );
            turn_info_vehicle.setText( clase );

            // Message
            JSONObject fracmensaje = data.getJSONObject("fracmensaje");
            String title = fracmensaje.getString("title");
            String body = fracmensaje.getString("body");

            // Evaluate turn
            Boolean turn = data.getBoolean("enturnar");
            if(turn) {
                // Layout info
                ImageLoader loaderCity = ImageLoader.getInstance();
                DisplayImageOptions optionsLogo = new DisplayImageOptions.Builder().cacheInMemory(true)
                        .cacheOnDisc(true).resetViewBeforeLoading(true).build();
                loaderCity.displayImage("drawable://" + R.drawable.turn_city_icon, mTurnCity, optionsLogo);

                // Show layout info
                turn_info_title.setText( title );
                turn_info_body.setText( Utils.fromHtml(body.toString()) );
                mLayoutInfo.setVisibility(View.VISIBLE);

                // Layout form
                JSONObject basicData = data.getJSONObject("basicData");
                JSONArray offices = basicData.getJSONArray("Offices");
                JSONArray destinations = basicData.getJSONArray("Destinos");

                if(offices == null || offices.length() == 0) {
                    Toast.makeText(TurnActivity.this, R.string.on_failure_offices, Toast.LENGTH_LONG).show();
                    finish();
                    return;
                }

                // Set offices
                for (int i = 0; i < offices.length(); i++) {
                    JSONObject jsonObject = (JSONObject) offices.get(i);
                    officesList.add( new Office(jsonObject.getInt("id"), jsonObject.getString("nombre")) );
                }
                ArrayAdapter<Office> officesAdapter = new ArrayAdapter<Office>(this, android.R.layout.simple_list_item_1, officesList);
                spinner_office.setAdapter(officesAdapter);

                // Set destinations
                for (int i = 0; i < destinations.length(); i++) {
                    JSONObject jsonObject = (JSONObject) destinations.get(i);
                    Office one_office = new Office(jsonObject.getInt("id"), jsonObject.getString("nombre"));
                    destinationsListOne.add(one_office);

                    // Any destination not available in second spinner
                    if (one_office.getId() != Constants.TURN_ANY_DESTINATION) {
                        destinationsListTwo.add(one_office);
                    }
                }
                final ArrayAdapter<Office> destinationsAdapterOne = new ArrayAdapter<Office>(this, android.R.layout.simple_list_item_1, destinationsListOne);
                final ArrayAdapter<Office> destinationsAdapterTwo = new ArrayAdapter<Office>(this, android.R.layout.simple_list_item_1, destinationsListTwo);
                spinner_first_destination.setAdapter(destinationsAdapterOne);
                spinner_second_destination.setAdapter(destinationsAdapterTwo);

                // Listeners
                spinner_first_destination.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        return;
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if(s != null && !s.toString().isEmpty()) {
                            Office first_destination = Office.filter(destinationsListOne, s.toString());
                            if (first_destination == null) {
                                spinner_first_destination.setText("");
                                Toast.makeText(TurnActivity.this, R.string.on_failure, Toast.LENGTH_LONG).show();
                                return;
                            }

                            if(first_destination.getId() == Constants.TURN_ANY_DESTINATION) {
                                spinner_second_destination.setVisibility(View.GONE);
                            }else{
                                spinner_second_destination.setVisibility(View.VISIBLE);
                            }
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        return;
                    }
                });

                // Show layout form
                mTurnFormView.setVisibility(View.VISIBLE);

            }else{
                // Show message
                showMessage(sucessfull, title, body);

                // Show layout message
                mLayoutMessage.setVisibility(View.VISIBLE);

                // Show warning message
                JSONObject enturnamiento = data.getJSONObject("enturnamiento");
                JSONObject validations = enturnamiento.getJSONObject("validaciones");
                JSONArray warnings = validations.getJSONArray("warnings");

                if(warnings.length() > 0) {
                    showWarnings(warnings);
                }
            }
        } catch (JSONException | NullPointerException e ) {
            Log.e(TAG, e.getMessage());

            // Tracker exception
            Crashlytics.logException(e);

            Toast.makeText(TurnActivity.this, R.string.on_failure, Toast.LENGTH_LONG).show();

            // Close activity
            finish();
        } finally {
            // Hide progress.
            Utils.showProgress(false, mFormView, mProgressView);
        }
    }

    public void turnValidate(View view) {
        // Check for a valid form.
        boolean cancel = false;
        View focusView = null;

        // Office
        final Office office = Office.filter(officesList, spinner_office.getText().toString());
        if (office == null ) {
            spinner_office.setError(getString(R.string.error_field_required));
            focusView = spinner_office;
            cancel = true;
        }

        // First destination
        final Office first_destination = Office.filter(destinationsListOne, spinner_first_destination.getText().toString());
        if (first_destination == null ) {
            spinner_first_destination.setError(getString(R.string.error_field_required));
            focusView = spinner_first_destination;
            cancel = true;
        }

        // Second destination
        Office second_destination = Office.filter(destinationsListOne, spinner_second_destination.getText().toString());
        if (second_destination == null && (first_destination != null && first_destination.getId() != Constants.TURN_ANY_DESTINATION)) {
            spinner_second_destination.setError(getString(R.string.error_field_required));
            focusView = spinner_second_destination;
            cancel = true;
        }else{
            if( first_destination != null && first_destination.getId() != Constants.TURN_ANY_DESTINATION
                    && first_destination.getId() == second_destination.getId()){
                spinner_second_destination.setError(getString(R.string.error_field_other_destination));
                focusView = spinner_second_destination;
                cancel = true;
            }
        }

        if (cancel) {
            // form field with an error.
            focusView.requestFocus();
        }else {
            if( first_destination.getId() == Constants.TURN_ANY_DESTINATION ){
                second_destination = first_destination;
            }
            final Office second_destination_ = second_destination;

            // Hide progress.
            Utils.showProgress(true, mFormView, mProgressView);

            try {
                mRestClientApp.getAccessToken(new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        turnOn(office.getId(), first_destination.getId(), second_destination_.getId(), checkbox_empty.isChecked(), response);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
                        onFailureAccessToken(throwable, response);
                    }
                });
            } catch (JSONException | IOException | NoSuchAlgorithmException | KeyManagementException | UnrecoverableKeyException |
                    CertificateException | KeyStoreException e ) {
                Toast.makeText(TurnActivity.this, R.string.on_failure, Toast.LENGTH_LONG).show();

                // Tracker exception
                Crashlytics.logException(e);

                // Hide progress.
                Utils.showProgress(false, mFormView, mProgressView);
            }
        }
    }

    public void turnOn(Integer oficina, Integer destino1, Integer destino2, Boolean vacio, JSONObject oaut) {
        try {
            mRestClientApp.turnOn(mSession.getPartner(), gps.getLatitude(), gps.getLongitude(), oficina, destino1, destino2, vacio,
                    oaut, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    // System.out.println(" onSuccess " + response.toString());
                    try {

                        if( response.getBoolean("sucessfull") == true) {
                            JSONObject data = response.getJSONObject("data");
                            JSONObject turn = data.getJSONObject("enturnamiento");
                            Boolean sucessfull = response.getBoolean("sucessfull");

                            // Message
                            JSONObject fracmensaje = turn.getJSONObject("fracmensaje");
                            String title = fracmensaje.getString("title");
                            String body = fracmensaje.getString("body");

                            // Hide form
                            mLayoutInfo.setVisibility(View.GONE);
                            mTurnFormView.setVisibility(View.GONE);
                            // Show message
                            showMessage(sucessfull, title, body);
                            // Show layout message
                            mLayoutMessage.setVisibility(View.VISIBLE);

                            JSONObject validations = turn.getJSONObject("validaciones");
                            JSONArray warnings = validations.getJSONArray("warnings");
                            if(warnings.length() > 0) {
                                showWarnings(warnings);
                            }
                        }
                    } catch (JSONException | NullPointerException e ) {
                        Log.e(TAG, e.getMessage());

                        // Tracker exception
                        Crashlytics.logException(e);

                        Toast.makeText(TurnActivity.this, R.string.on_failure, Toast.LENGTH_LONG).show();

                        // Close activity
                        finish();
                    } finally {
                        // Hide progress.
                        Utils.showProgress(false, mFormView, mProgressView);
                    }

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
                    try
                    {
                        if(response == null) {
                            throw new NullPointerException(getResources().getString(R.string.on_null_server_exception));
                        }

                        if( response.getBoolean("sucessfull") == false) {
                            Toast.makeText(TurnActivity.this, response.getString("msg"), Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException | NullPointerException e ) {
                        Log.e(TAG, e.getMessage());

                        // Tracker exception
                        Crashlytics.logException(e);

                        Toast.makeText(TurnActivity.this, R.string.on_failure, Toast.LENGTH_LONG).show();
                    } finally {
                        // Hide progress.
                        Utils.showProgress(false, mFormView, mProgressView);
                    }
                }
            });
        } catch (JSONException | UnsupportedEncodingException e ) {
            Toast.makeText(TurnActivity.this, R.string.on_failure, Toast.LENGTH_LONG).show();

            // Tracker exception
            Crashlytics.logException(e);

            // Hide progress.
            Utils.showProgress(false, mFormView, mProgressView);
        }
    }

    public void onFailureAccessToken(Throwable throwable, JSONObject response) {
        try {
            if (response == null) {
                throw new NullPointerException(getResources().getString(R.string.on_null_server_exception));
            }

            if (throwable.getCause() instanceof ConnectTimeoutException ) {
                Toast.makeText(TurnActivity.this, R.string.on_host_exception, Toast.LENGTH_LONG).show();
            }

            // Connect timeout exception
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (throwable.getCause() instanceof ErrnoException) {
                    Toast.makeText(TurnActivity.this, R.string.on_host_exception, Toast.LENGTH_LONG).show();
                }
            }
        }catch (Exception e) {
            Toast.makeText(TurnActivity.this, R.string.on_host_exception, Toast.LENGTH_LONG).show();

            // Tracker exception
            Crashlytics.logException(e);

        }finally {
            // Hide progress.
            Utils.showProgress(false, mFormView, mProgressView);
        }
    }

    public void showMessage(Boolean sucessfull, String title, String body)
    {
        // Parameters
        Bundle messageParameters = new Bundle();
        messageParameters.putBoolean(Constants.MESSAGE_SUCCESS, sucessfull);
        messageParameters.putString(Constants.MESSAGE_TITLE, title);
        messageParameters.putString(Constants.MESSAGE_BODY, body);

        if (!isFinishing()) {
            // Inflate message
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            MessageFragment messageFragment = new MessageFragment();
            messageFragment.setArguments(messageParameters);
            fragmentTransaction.add(R.id.layout_message, messageFragment, TurnActivity.TAG);
            fragmentTransaction.commit();
        }
    }

    public void showWarnings(JSONArray warnings) throws JSONException {

        // Load image warnings
        ImageLoader loaderWarnings = ImageLoader.getInstance();
        DisplayImageOptions optionsWarnings = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisc(true).resetViewBeforeLoading(true).build();
        loaderWarnings.loadImage("drawable://" + R.drawable.turn_alert_background, null, optionsWarnings, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                BitmapDrawable drawable = new BitmapDrawable(getResources(), loadedImage);
                mLayoutWarnings.setBackgroundDrawable(drawable);
            }
        });

        for (int i = 0; i < warnings.length(); i++) {
            JSONObject jsonObject = (JSONObject) warnings.get(i);
            View view = getLayoutInflater().inflate(R.layout.warning_list_row, null);

            TextView title = (TextView) view.findViewById(R.id.title);
            TextView subtitle = (TextView) view.findViewById(R.id.subtitle);

            LinearLayout mLayoutFecha = (LinearLayout) view.findViewById(R.id.info_fecha);
            TextView date = (TextView) view.findViewById(R.id.fecha);
            TextView typedate = (TextView) view.findViewById(R.id.tipofecha);

            title.setText(Utils.capitalize(jsonObject.getString("objeto")));
            subtitle.setText(jsonObject.getString("texto"));

            String fecha = jsonObject.getString("fecha");
            if ( !fecha.equals(null) && !fecha.equals("null") && !fecha.isEmpty()) {
                date.setText(fecha);
                typedate.setText(jsonObject.getString("tipofechavalida"));

                mLayoutFecha.setVisibility(View.VISIBLE);
            }

            mLayoutWarnings.addView(view);
        }
        mLayoutWarnings.setVisibility(View.VISIBLE);
    }
}
