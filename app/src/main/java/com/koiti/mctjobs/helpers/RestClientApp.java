package com.koiti.mctjobs.helpers;

import android.content.Context;

import com.koiti.mctjobs.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.MySSLSocketFactory;
import com.loopj.android.http.RequestParams;

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
        params.put("grant_type", "password");
        params.put("username", "appworks");
        params.put("password", "appworks");

        // Call API Login
        AsyncHttpClient client = new AsyncHttpClient();
        client.setSSLSocketFactory(sf);
        client.setMaxRetriesAndTimeout(Constants.DEFAULT_MAX_RETRIES, Constants.DEFAULT_TIMEOUT);

        client.addHeader("Content-Type", "application/x-www-form-urlencoded");
        client.addHeader("Authorization", "Basic WkZXRncyc1B2OGZON09SeVBzalc0NGVROUIwYTpMdUdSVXNFazNkb29zQVg3d1o3UVEzWTVQeGth");
        client.post(Constants.URL_OAUTH_TOKEN, params, jsonHttpResponseHandler);
    }

    public void loginAccount(String account, String password, JSONObject oaut, JsonHttpResponseHandler jsonHttpResponseHandler) throws JSONException, UnsupportedEncodingException {
        // Params
        JSONObject params = new JSONObject();
        params.put("usuario", account);
        params.put("contrasena", password);

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

        ByteArrayEntity entity = new ByteArrayEntity(params.toString().getBytes("UTF-8"));

        // Call API Login
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("Authorization", "Bearer " + oaut.getString("access_token"));
        client.setMaxRetriesAndTimeout(Constants.DEFAULT_MAX_RETRIES, Constants.DEFAULT_TIMEOUT);
        client.post(null, Constants.URL_LOGIN_PHONE_API, entity, "application/json", jsonHttpResponseHandler);
    }

    public void syncReport(ByteArrayEntity entity, JsonHttpResponseHandler jsonHttpResponseHandler) throws UnsupportedEncodingException, JSONException {

        AsyncHttpClient client = new AsyncHttpClient();
        client.setMaxRetriesAndTimeout(Constants.DEFAULT_MAX_RETRIES, Constants.DEFAULT_TIMEOUT);
        client.post(null, Constants.URL_POST_REPORT_API, entity, "application/json", jsonHttpResponseHandler);
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

        ByteArrayEntity entity = new ByteArrayEntity(params.toString().getBytes("UTF-8"));

        // Call API Login
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("Authorization", "Bearer " + oaut.getString("access_token"));
        client.setMaxRetriesAndTimeout(Constants.DEFAULT_MAX_RETRIES, Constants.DEFAULT_TIMEOUT);
        client.post(null, Constants.URL_TURN_ON_API, entity, "application/json", jsonHttpResponseHandler);
    }
}