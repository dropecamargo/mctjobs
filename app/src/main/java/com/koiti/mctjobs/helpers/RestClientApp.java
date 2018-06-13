package com.koiti.mctjobs.helpers;

import android.content.Context;
import android.provider.Settings.Secure;

import com.google.firebase.iid.FirebaseInstanceId;
import com.koiti.mctjobs.BuildConfig;
import com.koiti.mctjobs.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.MySSLSocketFactory;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

import cz.msebera.android.httpclient.entity.ByteArrayEntity;

public class RestClientApp {

    private Context context;
    private static final String FICHERO_CERT = "mct.cer";

    public RestClientApp(Context context) {
        this.context = context;
    }

    public void getAccessToken(JsonHttpResponseHandler jsonHttpResponseHandler) throws JSONException, IOException, NoSuchAlgorithmException, CertificateException,
            KeyStoreException, UnrecoverableKeyException, KeyManagementException {

        // Load cert
        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
        trustStore.load(null, null);

        Certificate cert;
        CertificateFactory cf = CertificateFactory.getInstance("X.509");

        InputStream caInput = context.getResources().openRawResource(R.raw.mct);
        try {
            cert = cf.generateCertificate(caInput);
        } finally {
            caInput.close();
        }
        trustStore.setCertificateEntry("ca", cert);

        MySSLSocketFactory sf = new MySSLSocketFactory(trustStore);
        sf.setHostnameVerifier(MySSLSocketFactory.STRICT_HOSTNAME_VERIFIER);

        // Prepare params
        RequestParams params = new RequestParams();
        params.put("grant_type", Constants.OAUTH_GRANT_TYPE);
        params.put("username", Constants.OAUTH_USERNAME);
        params.put("password", Constants.OAUTH_PASSWORD);

        // Call API
        AsyncHttpClient client = new AsyncHttpClient();
        client.setSSLSocketFactory(sf);
        client.setMaxRetriesAndTimeout(Constants.DEFAULT_MAX_RETRIES, Constants.DEFAULT_TIMEOUT);
        client.addHeader("Authorization", Constants.OAUTH_AUTHORIZATION);
        client.addHeader("Content-Type", "application/x-www-form-urlencoded");

        client.post(Constants.URL_OAUTH_TOKEN, params, jsonHttpResponseHandler);
    }

    public void getSyncAccessToken(JsonHttpResponseHandler jsonHttpResponseHandler) throws JSONException, IOException, NoSuchAlgorithmException, CertificateException,
            KeyStoreException, UnrecoverableKeyException, KeyManagementException {

        // Load cert
        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
        trustStore.load(null, null);

        Certificate cert;
        CertificateFactory cf = CertificateFactory.getInstance("X.509");

        InputStream caInput = context.getResources().openRawResource(R.raw.mct);
        try {
            cert = cf.generateCertificate(caInput);
        } finally {
            caInput.close();
        }
        trustStore.setCertificateEntry("ca", cert);

        MySSLSocketFactory sf = new MySSLSocketFactory(trustStore);
        sf.setHostnameVerifier(MySSLSocketFactory.STRICT_HOSTNAME_VERIFIER);

        // Prepare params
        RequestParams params = new RequestParams();
        params.put("grant_type", Constants.OAUTH_GRANT_TYPE);
        params.put("username", Constants.OAUTH_USERNAME);
        params.put("password", Constants.OAUTH_PASSWORD);

        // Call API Login
        SyncHttpClient client = new SyncHttpClient();
        client.setSSLSocketFactory(sf);
        client.setMaxRetriesAndTimeout(Constants.DEFAULT_MAX_RETRIES, Constants.DEFAULT_TIMEOUT);
        client.addHeader("Authorization", Constants.OAUTH_AUTHORIZATION);
        client.addHeader("Content-Type", "application/x-www-form-urlencoded");

        ByteArrayEntity entity = new ByteArrayEntity(params.toString().getBytes("UTF-8"));
        jsonHttpResponseHandler.setUsePoolThread(true);
        client.post(null, Constants.URL_OAUTH_TOKEN, entity, "application/json", jsonHttpResponseHandler);
    }

    public void loginAccount(String account, String password, JSONObject oaut, JsonHttpResponseHandler jsonHttpResponseHandler) throws JSONException, UnsupportedEncodingException {
        // Params
        JSONObject params = new JSONObject();
        params.put("usuario", account);
        params.put("contrasena", password);
        params.put("android_id", Secure.getString(this.context.getContentResolver(), Secure.ANDROID_ID));
        params.put("token", FirebaseInstanceId.getInstance().getToken());

        ByteArrayEntity entity = new ByteArrayEntity(params.toString().getBytes("UTF-8"));

        // Call API Login
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("Authorization", "Bearer " + oaut.getString("access_token"));
        client.setMaxRetriesAndTimeout(Constants.DEFAULT_MAX_RETRIES, Constants.DEFAULT_TIMEOUT);
        client.post(null, Constants.URL_LOGIN_API, entity, "application/json", jsonHttpResponseHandler);
    }

    public void loginPhone(String phone, JSONObject oaut, JsonHttpResponseHandler jsonHttpResponseHandler) throws JSONException, UnsupportedEncodingException {
        // Params
        JSONObject params = new JSONObject();
        params.put("celular", phone);
        params.put("android_id", Secure.getString(this.context.getContentResolver(), Secure.ANDROID_ID));
        params.put("token", FirebaseInstanceId.getInstance().getToken());

        ByteArrayEntity entity = new ByteArrayEntity(params.toString().getBytes("UTF-8"));

        // Call API Login
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("Authorization", "Bearer " + oaut.getString("access_token"));
        client.setMaxRetriesAndTimeout(Constants.DEFAULT_MAX_RETRIES, Constants.DEFAULT_TIMEOUT);
        client.post(null, Constants.URL_LOGIN_PHONE_API, entity, "application/json", jsonHttpResponseHandler);
    }

    public void syncReport(ByteArrayEntity entity, JSONObject oaut, JsonHttpResponseHandler jsonHttpResponseHandler) throws UnsupportedEncodingException, JSONException {

        AsyncHttpClient client = new AsyncHttpClient();
        client.setMaxRetriesAndTimeout(Constants.DEFAULT_MAX_RETRIES, Constants.DEFAULT_TIMEOUT);
        client.addHeader("Authorization", "Bearer " + oaut.getString("access_token"));
        client.post(null, Constants.URL_POST_REPORT_API, entity, "application/json", jsonHttpResponseHandler);
    }

    public void syncDocument(ByteArrayEntity entity, JSONObject oaut, JsonHttpResponseHandler jsonHttpResponseHandler) throws UnsupportedEncodingException, JSONException {

        AsyncHttpClient client = new AsyncHttpClient();
        client.setMaxRetriesAndTimeout(Constants.DEFAULT_MAX_RETRIES, Constants.DEFAULT_TIMEOUT);
        client.addHeader("Authorization", "Bearer " + oaut.getString("access_token"));
        client.post(null, Constants.URL_POST_DOCUMENT_API, entity, "application/json", jsonHttpResponseHandler);
    }

    public void sendSmsVerification(String phone, String country_code, JsonHttpResponseHandler jsonHttpResponseHandler) throws JSONException, UnsupportedEncodingException {
        // Params
        JSONObject params = new JSONObject();
        params.put("api_key", Constants.TWILIO_API_KEY);
        params.put("phone_number", phone);
        params.put("country_code", country_code);
        params.put("via", "sms");
        params.put("locale", "es");

        ByteArrayEntity entity = new ByteArrayEntity(params.toString().getBytes("UTF-8"));

        AsyncHttpClient client = new AsyncHttpClient();
        client.setMaxRetriesAndTimeout(Constants.DEFAULT_MAX_RETRIES, Constants.DEFAULT_TIMEOUT);
        client.post(null, Constants.URL_POST_SEND_SMS_VERIFICATION, entity, "application/json", jsonHttpResponseHandler);
    }

    public void checkCodeVerification(String phone, String code, String country_code, JsonHttpResponseHandler jsonHttpResponseHandler) throws JSONException, UnsupportedEncodingException {
        // Params
        RequestParams params = new RequestParams();
        params.put("api_key", Constants.TWILIO_API_KEY);
        params.put("phone_number", phone);
        params.put("country_code", country_code);
        params.put("verification_code", code);

        AsyncHttpClient client = new AsyncHttpClient();
        client.setMaxRetriesAndTimeout(Constants.DEFAULT_MAX_RETRIES, Constants.DEFAULT_TIMEOUT);
        client.get(Constants.URL_GET_CHECK_VERIFICATION, params, jsonHttpResponseHandler);
    }

    public void prepareTurn(Integer partner, Double latitude, Double longitude, JSONObject oaut, JsonHttpResponseHandler jsonHttpResponseHandler) throws JSONException, UnsupportedEncodingException {
        // Params
        JSONObject params = new JSONObject();
        params.put("terceroCodigo", partner);
        params.put("latitude", latitude);
        params.put("longitude", longitude);
        params.put("android_id", Secure.getString(this.context.getContentResolver(), Secure.ANDROID_ID));
        params.put("version", BuildConfig.VERSION_NAME);

        ByteArrayEntity entity = new ByteArrayEntity(params.toString().getBytes("UTF-8"));

        // Call API Login
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("Authorization", "Bearer " + oaut.getString("access_token"));
        client.setMaxRetriesAndTimeout(Constants.DEFAULT_MAX_RETRIES, Constants.DEFAULT_TIMEOUT);
        client.post(null, Constants.URL_TURN_API, entity, "application/json", jsonHttpResponseHandler);
    }

    public void turnOn(Integer partner, Double latitude, Double longitude, Integer oficina, Integer destino1, Integer destino2, Boolean vacio, JSONObject oaut, JsonHttpResponseHandler jsonHttpResponseHandler) throws UnsupportedEncodingException, JSONException {
        // Params
        JSONObject params = new JSONObject();
        params.put("terceroCodigo", partner);
        params.put("latitude", latitude);
        params.put("longitude", longitude);
        params.put("oficina", oficina);
        params.put("destino1", destino1);
        params.put("destino2", destino2);
        params.put("vacio", vacio);
        params.put("token", FirebaseInstanceId.getInstance().getToken());

        ByteArrayEntity entity = new ByteArrayEntity(params.toString().getBytes("UTF-8"));

        // Call API Login
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("Authorization", "Bearer " + oaut.getString("access_token"));
        client.setMaxRetriesAndTimeout(Constants.DEFAULT_MAX_RETRIES, Constants.DEFAULT_TIMEOUT);
        client.post(null, Constants.URL_TURN_ON_API, entity, "application/json", jsonHttpResponseHandler);
    }

    public void termsOn(Integer partner, String fecha, JSONObject oaut, JsonHttpResponseHandler jsonHttpResponseHandler) throws UnsupportedEncodingException, JSONException {
        // Params
        JSONObject params = new JSONObject();
        params.put("tercero_codigo", partner);
        params.put("fecha", fecha);

        ByteArrayEntity entity = new ByteArrayEntity(params.toString().getBytes("UTF-8"));

        // Call API Login
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("Authorization", "Bearer " + oaut.getString("access_token"));
        client.setMaxRetriesAndTimeout(Constants.DEFAULT_MAX_RETRIES, Constants.DEFAULT_TIMEOUT);
        client.post(null, Constants.URL_TERMS_ON_API, entity, "application/json", jsonHttpResponseHandler);
    }

    public void validCellphone(String phone, JSONObject oaut, JsonHttpResponseHandler jsonHttpResponseHandler) throws JSONException, UnsupportedEncodingException {
        // Params
        JSONObject params = new JSONObject();
        params.put("celular", phone);
        params.put("version", BuildConfig.VERSION_NAME);

        ByteArrayEntity entity = new ByteArrayEntity(params.toString().getBytes("UTF-8"));

        // Call API Login
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("Authorization", "Bearer " + oaut.getString("access_token"));
        client.setMaxRetriesAndTimeout(Constants.DEFAULT_MAX_RETRIES, Constants.DEFAULT_TIMEOUT);
        client.post(null, Constants.URL_LOGIN_VALID_PHONE_API, entity, "application/json", jsonHttpResponseHandler);
    }

    public void sendTokenFirebase(String token, JSONObject oaut, JsonHttpResponseHandler jsonHttpResponseHandler) throws UnsupportedEncodingException, JSONException {
        // Params
        JSONObject params = new JSONObject();
        params.put("token", token);

        // Call API Login
        SyncHttpClient client = new SyncHttpClient();
        client.addHeader("Authorization", "Bearer " + oaut.getString("access_token"));

        ByteArrayEntity entity = new ByteArrayEntity(params.toString().getBytes("UTF-8"));
        jsonHttpResponseHandler.setUsePoolThread(true);

        client.post(null, Constants.URL_TOKEN_PUSH_API, entity, "application/json", jsonHttpResponseHandler);
    }

    public void getJob(Integer partner, Integer job, JSONObject oaut, JsonHttpResponseHandler jsonHttpResponseHandler) throws UnsupportedEncodingException, JSONException {
        // Params
        JSONObject params = new JSONObject();
        params.put("id_work", job);
        params.put("id_partner", job);
        params.put("android_id", Secure.getString(this.context.getContentResolver(), Secure.ANDROID_ID));

        ByteArrayEntity entity = new ByteArrayEntity(params.toString().getBytes("UTF-8"));

        AsyncHttpClient client = new AsyncHttpClient();
        client.setMaxRetriesAndTimeout(Constants.DEFAULT_MAX_RETRIES, Constants.DEFAULT_TIMEOUT);
        client.addHeader("Authorization", "Bearer " + oaut.getString("access_token"));
        client.post(null, Constants.URL_GET_WORK_API, entity, "application/json", jsonHttpResponseHandler);
    }
}