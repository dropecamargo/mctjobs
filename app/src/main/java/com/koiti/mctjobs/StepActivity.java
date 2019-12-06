package com.koiti.mctjobs;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.system.ErrnoException;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.koiti.mctjobs.helpers.Constants;
import com.koiti.mctjobs.helpers.GPSTracker;
import com.koiti.mctjobs.helpers.RestClientApp;
import com.koiti.mctjobs.helpers.UserSessionManager;
import com.koiti.mctjobs.helpers.Utils;
import com.koiti.mctjobs.models.Job;
import com.koiti.mctjobs.models.Report;
import com.koiti.mctjobs.models.Step;
import com.koiti.mctjobs.sqlite.DataBaseManagerJob;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.conn.ConnectTimeoutException;

public class StepActivity extends AppCompatActivity {
    private static final String TAG = StepActivity.class.getSimpleName();

    private RestClientApp mRestClientApp;

    private Job job;
    private Step step;
    private GPSTracker gps;
    private int result;

    private View mFormView;
    private View mProgressView;

    private List<Step> stepList = new ArrayList<>();
    private DataBaseManagerJob mJob;
    private UserSessionManager mSession;
    private FrameLayout wrapper_layout;
    private ScrollView scroll_detail;

    private ImageView mJobImage;

    private TextView id;
    private TextView document;
    private TextView formatdate;
    private TextView details;
    private Button button_step;
    private TextView info_step;
    private LinearLayout listView;

    private FloatingActionButton start_button;
    private FloatingActionButton cancel_button;
    private FloatingActionButton step_button;
    private FloatingActionButton pause_button;
    private FloatingActionButton discard_button;
    private FloatingActionButton report_button;
    private FloatingActionButton ignore_button;
    private FloatingActionsMenu actions_menu;

    private FloatingActionsMenu options_menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step);

        // Session
        mSession = new UserSessionManager(this);
        // Check user login
        if(mSession.checkLogin()) {
            finish();
            return;
        }

        // Rest client
        mRestClientApp = new RestClientApp(this);

        // Set result default
        result = Constants.RESULT_CANCEL;

        // Database
        mJob = new DataBaseManagerJob(this);

        // References
        mFormView = findViewById(R.id.form_view);
        mProgressView = findViewById(R.id.progress_view);

        // References
        wrapper_layout = (FrameLayout) findViewById(R.id.wrapper_layout);
        scroll_detail = (ScrollView) findViewById(R.id.scroll_detail);

        id = (TextView) findViewById(R.id.id);
        document = (TextView) findViewById(R.id.document);
        formatdate = (TextView) findViewById(R.id.formatdate);
        details = (TextView) findViewById(R.id.details);
        button_step = (Button) findViewById(R.id.button_step);
        info_step = (TextView) findViewById(R.id.info_step);
        mJobImage = (ImageView) findViewById(R.id.job_title_image);

        // References actions
        actions_menu = (FloatingActionsMenu) findViewById(R.id.actions_menu);
        cancel_button = (FloatingActionButton) findViewById(R.id.cancel_button);
        start_button = (FloatingActionButton) findViewById(R.id.start_button);
        step_button = (FloatingActionButton) findViewById(R.id.step_button);
        pause_button = (FloatingActionButton) findViewById(R.id.pause_button);
        discard_button = (FloatingActionButton) findViewById(R.id.discard_button);
        report_button = (FloatingActionButton) findViewById(R.id.report_button);
        ignore_button = (FloatingActionButton) findViewById(R.id.ignore_button);
        options_menu = (FloatingActionsMenu) findViewById(R.id.options_menu);

        // Load image vehicle
        ImageLoader loaderTitle = ImageLoader.getInstance();
        DisplayImageOptions optionsTitle = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisc(true).resetViewBeforeLoading(true).build();
        loaderTitle.displayImage("drawable://" + R.drawable.job_background, mJobImage, optionsTitle);

        // Toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Show a progress spinner, and kick off a background task to
        // perform the user login attempt.
        Utils.showProgress(true, mFormView, mProgressView);

        // Job
        Integer work = Integer.parseInt(getIntent().getExtras().get("JOB").toString());
        job = mJob.getJob( work );
        if(job == null) {
            // Get Job API
            starJob( work );
        }else{
            prepareJob();
        }
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

    @Override
    protected void onDestroy() {
        mJob.close();

        super.onDestroy();
    }

    public void openMenu(View view) {
        if(!job.getState().equals("FINALIZADA")) {
            actions_menu.expand();
        }
    }

    public void starJob(final Integer job) {
        try {
            mRestClientApp.getAccessToken(new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    evaluateJob(job, response);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
                    onFailureAccessToken(throwable, response);
                }
            });
        } catch (JSONException | IOException | NoSuchAlgorithmException | KeyManagementException | UnrecoverableKeyException |
                CertificateException | KeyStoreException e ) {
            Log.e(TAG, e.getMessage());
            Toast.makeText(StepActivity.this, R.string.on_failure, Toast.LENGTH_LONG).show();
            finish();
            return;
        }
    }

    public void evaluateJob(final Integer work, JSONObject oaut)
    {
        try {
            mRestClientApp.getJob(mSession.getPartner(), work, oaut, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    // System.out.println(" onSuccess -> " + response.toString());
                    mJob.getDb().beginTransaction();
                    try
                    {
                        Boolean sucessfull = response.getBoolean("successful");
                        Boolean close_session = response.getBoolean("close_session");

                        // Valid response close session
                        if(close_session) {
                            mSession.logout();
                            return;
                        }

                        if( sucessfull ) {
                            JSONObject data = response.getJSONObject("data");

                            mJob.parseJob(data, true);

                            // Transaction successful
                            mJob.getDb().setTransactionSuccessful();

                            // Get job db
                            job = mJob.getJob( work );
                            if(job == null) {
                                Toast.makeText(StepActivity.this,R.string.on_failure, Toast.LENGTH_LONG).show();
                                finish();
                                return;
                            }

                            // Prepare
                            prepareJob();
                        }else{
                            JSONObject error = response.getJSONObject("error");
                            Toast.makeText(StepActivity.this, error.getString("msg"), Toast.LENGTH_LONG).show();
                            finish();
                        }
                    } catch (JSONException | NullPointerException e ) {
                        Log.e(TAG, e.getMessage());

                        // Tracker exception
                        Crashlytics.logException(e);

                        Toast.makeText(StepActivity.this, R.string.on_failure, Toast.LENGTH_LONG).show();
                        finish();
                    }finally {
                        // End transaction
                        mJob.getDb().endTransaction();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
                    try
                    {
                        if(response == null) {
                            throw new NullPointerException(getResources().getString(R.string.on_null_server_exception));
                        }

                        JSONObject error = response.getJSONObject("error");
                        if( !error.getString("msg").isEmpty() ) {
                            Toast.makeText(StepActivity.this, error.getString("msg"), Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException | NullPointerException e ) {
                        Log.e(TAG, e.getMessage());

                        // Tracker exception
                        Crashlytics.logException(e);

                        Toast.makeText(StepActivity.this, R.string.on_failure, Toast.LENGTH_LONG).show();
                    } finally {
                        // Hide progress.
                        finish();
                    }
                }
            });
        } catch (JSONException | UnsupportedEncodingException e ) {
            Log.e(TAG, e.getMessage());
            Toast.makeText(StepActivity.this, R.string.on_failure, Toast.LENGTH_LONG).show();

            finish();
        }
    }

    public void prepareJob() {
        // Hide progress.
        Utils.showProgress(false, mFormView, mProgressView);

        getSupportActionBar().setTitle(Utils.capitalize(job.getWorktypename()));
        getSupportActionBar().setSubtitle("(" + Integer.toString(job.getId()) + ") " + job.getDocument());

        // Data steps, list view
        stepList = mJob.getStepList(job);
        listView = (LinearLayout) findViewById(R.id.list_steps);
        Integer contador = stepList.size();
        for (int i = 0; i < stepList.size(); i++) {
            final Step step = stepList.get(i);
            View view = getLayoutInflater().inflate(R.layout.step_list_row, null);
            view.setBackgroundDrawable(getResources().getDrawable(R.drawable.step_list_border));

            TextView number = (TextView) view.findViewById(R.id.number);
            TextView title = (TextView) view.findViewById(R.id.title);
            TextView date = (TextView) view.findViewById(R.id.date);
            TextView ignore = (TextView) view.findViewById(R.id.ignore);
            ImageView pending_sync = (ImageView) view.findViewById(R.id.pending_sync);

            number.setText(Integer.toString(contador));
            title.setText(step.getTitle());
            date.setText(step.getDate());
            if (step.getIgnored()) {
                ignore.setText(R.string.step_ignore);
            }

            pending_sync.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_sync));
            if (step.getPendingsync()) {
                pending_sync.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_pending_sync));
            }

            // Implement it's on click listener.
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {

                    CharSequence colors[] = new CharSequence[] {"Reenviar imágenes"};
                    AlertDialog.Builder builder = new AlertDialog.Builder(StepActivity.this);
                    builder.setTitle(step.getTitle());
                    builder.setItems(colors, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case Constants.STEP_FORWARD_DOCUMENTS:
                                    if(mJob.getSyncDocuments(step) > 0){
                                        mJob.forwardDocuments(step);
                                        Toast.makeText(StepActivity.this, R.string.forward_document_sucessfull, Toast.LENGTH_LONG).show();
                                    }else{
                                        Toast.makeText(StepActivity.this, R.string.forward_document_error, Toast.LENGTH_LONG).show();
                                    }
                            }
                        }
                    });
                    builder.show();

                    return true;
                }
            });

            listView.addView(view);
            contador--;
        }

        id.setText(Integer.toString(job.getId()));
        formatdate.setText(job.getFormatdate());
        document.setText(job.getDocument());
        details.setText(Utils.fromHtml(job.getDetails()));

        if (job.getState().equals("FINALIZADA")) {
            // Button
            button_step.setEnabled(false);
            button_step.setText(R.string.job_finish);

        } else if (job.getState().equals("NOTIFICADA") || job.getState().equals("ACEPTADA")) {

            // Toogle menu report
            actions_menu.setVisibility(View.VISIBLE);
            actions_menu.setOnFloatingActionsMenuUpdateListener(new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener() {
                @Override
                public void onMenuExpanded() {
                    options_menu.collapse();
                    wrapper_layout.setBackgroundColor(getResources().getColor(R.color.primaryTransparent));
                }

                @Override
                public void onMenuCollapsed() {
                    wrapper_layout.setBackgroundColor(Color.TRANSPARENT);
                }
            });

            // Toogle menu options
            options_menu.setVisibility(View.VISIBLE);
            options_menu.setOnFloatingActionsMenuUpdateListener(new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener() {
                @Override
                public void onMenuExpanded() {
                    actions_menu.collapse();
                    wrapper_layout.setBackgroundColor(getResources().getColor(R.color.primaryTransparent));
                }

                @Override
                public void onMenuCollapsed() {
                    wrapper_layout.setBackgroundColor(Color.TRANSPARENT);
                }
            });

            // Next step
            if( job.getNextstep() != null && (int) job.getNextstep() > 0){
                step = mJob.getStep(job.getNextstep());
            }else{
                step = mJob.nextStep(job.getId());
            }

            if (step == null) {
                Toast.makeText(StepActivity.this, R.string.on_failure, Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            // Button
            button_step.setText(step.getTitle() + "\n" + step.getMessage());

            if (job.getState().equals("NOTIFICADA")) {
                start_button.setVisibility(View.VISIBLE);
                cancel_button.setVisibility(View.VISIBLE);

            } else if (job.getState().equals("ACEPTADA")) {
                step_button.setVisibility(View.VISIBLE);
                report_button.setVisibility(View.VISIBLE);

                if (step.getPausable()) {
                    pause_button.setVisibility(View.VISIBLE);
                    if (step.getPaused()) {
                        step_button.setVisibility(View.GONE);

                        pause_button.setIcon(R.drawable.ic_action_play);
                        pause_button.setTitle(getResources().getString(R.string.action_unpause));
                    }
                }

                if (step.getIgnorable() && !step.getPaused()) {
                    ignore_button.setVisibility(View.VISIBLE);
                }

                if (step.getPaused()) {
                    info_step.setVisibility(View.VISIBLE);
                    info_step.setText(R.string.step_paused);
                }

                // Discard option
                ArrayList<Report> discardList = mJob.getDiscardList(job);
                if (discardList.size() > 0) {
                    discard_button.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    public void cancelStep(View view) {

        Intent intent = new Intent(StepActivity.this, ReportActivity.class);
        intent.putExtra("STEP", step.getId());
        intent.putExtra("ACTION", Constants.INTENT_CANCEL);
        startActivityForResult(intent, Constants.PICK_REPORT_REQUEST);
    }

    public void discardJob(View view) {

        Intent intent = new Intent(StepActivity.this, ReportActivity.class);
        intent.putExtra("STEP", step.getId());
        intent.putExtra("ACTION", Constants.INTENT_DISCARD);
        startActivityForResult(intent, Constants.PICK_REPORT_REQUEST);
    }

    public void pauseStep(View view) {

        Intent intent = new Intent(StepActivity.this, ReportActivity.class);
        intent.putExtra("STEP", step.getId());
        intent.putExtra("ACTION", Constants.INTENT_PAUSE);
        startActivityForResult(intent, Constants.PICK_REPORT_REQUEST);
    }

    public void ignoreStep(View view) {

        Intent intent = new Intent(StepActivity.this, ReportActivity.class);
        intent.putExtra("STEP", step.getId());
        intent.putExtra("ACTION", Constants.INTENT_IGNORE);
        startActivityForResult(intent, Constants.PICK_REPORT_REQUEST);
    }

    public void reportStep(View view) {

        Intent intent = new Intent(StepActivity.this, ReportActivity.class);
        intent.putExtra("ACTION", Constants.INTENT_REPORT);
        intent.putExtra("STEP", step.getId());
        startActivityForResult(intent, Constants.PICK_REPORT_REQUEST);
    }

    public void sendStep(View view) {

        Intent intent = new Intent(StepActivity.this, ReportActivity.class);
        intent.putExtra("STEP", step.getId());
        startActivityForResult(intent, Constants.PICK_REPORT_REQUEST);
    }

    public void startStep(View view) {
        gps = new GPSTracker(this);
        if (gps.canGetLocation()) {

            // Location
            final double latitude = gps.getLatitude();
            final double longitude = gps.getLongitude();

            // Confirm action
            AlertDialog.Builder alertDialog = Utils.dialogBuilder(this);
            alertDialog.setTitle(R.string.job_start_title);
            alertDialog.setMessage(R.string.job_start_message);
            alertDialog.setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    mJob.getDb().beginTransaction();
                    try {
                        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String date = dateFormat.format(new Date());

                        // Set report
                        mJob.changeStatus(job.getId(), "ACEPTADA");
                        mJob.changeReport(step, date);

                        // Increase Step
                        Step nextstep = mJob.nextStep(job.getId());
                        mJob.increaseStep(job.getId(), (nextstep != null ? nextstep.getId() : 0));

                        // Sync report server
                        mJob.storeNotification(job, step, mSession.getPartner(), true, date, "ACEPTADA",
                                false, false, null, latitude, longitude, null, false, false, null, null, 0, null, null, null);

                        // Transaction successful
                        mJob.getDb().setTransactionSuccessful();

                        // Set RESULT_REFRESH_ATTENDING
                        result = Constants.RESULT_REFRESH_ATTENDING;
                        Toast.makeText(StepActivity.this, R.string.job_accept, Toast.LENGTH_LONG).show();

                        // Nextstep
                        nextStep();
                    }catch (Exception e) {
                        Log.e(TAG, e.getMessage());

                        // Tracker exception
                        Crashlytics.logException(e);

                        Toast.makeText(StepActivity.this, R.string.on_failure, Toast.LENGTH_LONG).show();
                    }finally {
                        // End transaction
                        mJob.getDb().endTransaction();
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
        } else {
            gps.showSettingsAlert();
        }
    }

    public void startAction(View view) {
        gps = new GPSTracker(this);
        if (gps.canGetLocation()) {

            // Location
            final double latitude = gps.getLatitude();
            final double longitude = gps.getLongitude();

            switch (view.getId()) {
                case R.id.panic_button:
                    sendAction("sendPanic", R.string.panic_title, R.string.panic_message, R.string.panic_successful, latitude, longitude, "PANICO");
                    break;
                case R.id.call_button:
                    sendAction("getCall", R.string.call_title, R.string.call_message, R.string.call_successful, latitude, longitude, "LLAMADA");
                    break;
                case R.id.instructions_button:
                    sendAction("getInstructions", R.string.instructions_title, R.string.instructions_message, R.string.instructions_successful, latitude, longitude, "INSTRUCCIONES");
                    break;
                default:
                    Toast.makeText(StepActivity.this, R.string.action_undefined, Toast.LENGTH_LONG).show();
                    break;
            }
        } else {
            gps.showSettingsAlert();
        }
    }

    public void turnAction(View view) {
        gps = new GPSTracker(this);
        if (gps.canGetLocation()) {
            Intent intent = new Intent(StepActivity.this, TurnActivity.class);
            startActivity(intent);
        } else {
            gps.showSettingsAlert();
        }
    }

    public void sendAction(final String action, Integer title, Integer message, final Integer successful, final double latitude,
                           final double longitude, final String message_action)
    {
        // Confirm action
        AlertDialog.Builder alertDialog = Utils.dialogBuilder(this);
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                mJob.getDb().beginTransaction();
                try {
                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String date = dateFormat.format(new Date());

                    // Sync report step
                    mJob.storeNotification(job, step, mSession.getPartner(), false, date, job.getState(),
                            false, false, null, latitude, longitude, null, false, false, action, message_action, 0, null, null, null);

                    // Transaction successful
                    mJob.getDb().setTransactionSuccessful();

                    Toast.makeText(StepActivity.this, successful, Toast.LENGTH_LONG).show();

                }catch (Exception e) {
                    Log.e(TAG, e.getMessage());

                    // Tracker exception
                    Crashlytics.logException(e);

                    Toast.makeText(StepActivity.this, R.string.on_failure, Toast.LENGTH_LONG).show();
                }finally {
                    // End transaction
                    mJob.getDb().endTransaction();

                    // Collapse menu
                    options_menu.collapse();
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

    public void nextStep() {

        Intent intent = new Intent();
        intent.putExtra("JOB", job.getId());
        setResult(result, intent);

        finish();
    }

    @Override
    public  void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(Constants.PICK_REPORT_REQUEST == requestCode) {
            switch(resultCode) {
                case Constants.RESULT_NEXT_STEP:
                    // Set RESULT_NEXT_STEP
                    result = Constants.RESULT_NEXT_STEP;
                    nextStep();
                    break;

                case Constants.RESULT_CANCEL:
                    // Set RESULT_REFRESH_ATTENDING
                    result = Constants.RESULT_REFRESH_JOBS;
                    nextStep();
                    break;
                case Constants.RESULT_DISCARD:
                    // Set RESULT_REFRESH_DISCARD_ATTENDING
                    result = Constants.RESULT_REFRESH_DISCARD_ATTENDING;
                    nextStep();
                    break;

                case Constants.RESULT_FINISHED:
                    // Set RESULT_REFRESH_FINISHED
                    result = Constants.RESULT_REFRESH_FINISHED;
                    nextStep();
                    break;
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
                Toast.makeText(StepActivity.this, R.string.on_host_exception, Toast.LENGTH_LONG).show();
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (throwable.getCause() instanceof ErrnoException) {
                    Toast.makeText(StepActivity.this, R.string.on_host_exception, Toast.LENGTH_LONG).show();
                }
            }
        }catch (Exception e) {
            Toast.makeText(StepActivity.this, R.string.on_host_exception, Toast.LENGTH_LONG).show();

            // Tracker exception
            Crashlytics.logException(e);
        }finally {
            // Hide progress.
            finish();
        }
    }
}