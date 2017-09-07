package com.koiti.mctjobs;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.koiti.mctjobs.helpers.Constants;
import com.koiti.mctjobs.helpers.GPSTracker;
import com.koiti.mctjobs.helpers.UserSessionManager;
import com.koiti.mctjobs.helpers.Utils;
import com.koiti.mctjobs.models.Job;
import com.koiti.mctjobs.models.Step;
import com.koiti.mctjobs.sqlite.DataBaseManagerJob;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StepActivity extends ActionBarActivity {
    private static final String TAG = StepActivity.class.getSimpleName();

    private Job job;
    private Step step;
    private GPSTracker gps;
    private int result;

    private List<Step> stepList = new ArrayList<>();
    private DataBaseManagerJob mJob;
    private UserSessionManager mSession;
    private FrameLayout wrapper_layout;
    private Tracker tracker;

    private TextView id;
    private TextView details;
    private TextView formatdate;
    private TextView title_step;
    private TextView message_step;
    private TextView info_step;
    private LinearLayout listView;

    private FloatingActionButton start_button;
    private FloatingActionButton cancel_button;
    private FloatingActionButton step_button;
    private FloatingActionButton pause_button;
    private FloatingActionButton report_button;
    private FloatingActionButton ignore_button;
    private FloatingActionsMenu actions_menu;

    private FloatingActionsMenu options_menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step);

        // Get tracker
        tracker = ((Application) getApplication()).getTracker();

        // Session
        mSession = new UserSessionManager(this);
        // Check user login
        if(mSession.checkLogin()) {
            finish();
        }

        // Set result default
        result = Constants.RESULT_CANCEL;

        // Database
        mJob = new DataBaseManagerJob(this);

        // Job
        Intent intent = getIntent();
        job = mJob.getJob((Integer) intent.getIntExtra("JOB", 0));
        if(job == null) {
            Toast.makeText(this,R.string.on_failure, Toast.LENGTH_LONG).show();
            finish();
        }

        // Data steps, list view
        stepList = mJob.getStepList(job);
        listView = (LinearLayout) findViewById(R.id.list_steps);
        Integer bg = R.color.white;
        for (int i=0; i< stepList.size(); i++) {
            Step step = stepList.get(i);
            View view = getLayoutInflater().inflate(R.layout.step_list_row, null);
            RelativeLayout container = (RelativeLayout) view.findViewById(R.id.container);
            container.setBackgroundColor(getResources().getColor(bg));

            TextView title = (TextView) view.findViewById(R.id.title);
            TextView date = (TextView) view.findViewById(R.id.date);
            TextView ignore = (TextView) view.findViewById(R.id.ignore);
            ImageView pending_sync = (ImageView) view.findViewById(R.id.pending_sync);

            title.setText(step.getTitle());
            date.setText(step.getDate());
            if( step.getIgnored() ) {
                ignore.setText(R.string.step_ignore);
            }
            if ( step.getPendingsync() ) {
                pending_sync.setVisibility(View.VISIBLE);
            }

            listView.addView(view);
            bg = (bg == R.color.greyLight) ? R.color.white : R.color.greyLight;
        }

        // References
        wrapper_layout = (FrameLayout) findViewById(R.id.wrapper_layout);

        id = (TextView) findViewById(R.id.id);
        details = (TextView) findViewById(R.id.details);
        formatdate = (TextView) findViewById(R.id.formatdate);

        title_step = (TextView) findViewById(R.id.title_step);
        message_step = (TextView) findViewById(R.id.message_step);
        info_step = (TextView) findViewById(R.id.info_step);

        // Data
        id.setText( "(" + Integer.toString(job.getId()) + ") " + job.getDocument() );
        details.setText( Utils.fromHtml(job.getDetails()) );
        formatdate.setText( job.getFormatdate() );

        // Actions
        actions_menu = (FloatingActionsMenu) findViewById(R.id.actions_menu);
        cancel_button = (FloatingActionButton) findViewById(R.id.cancel_button);
        start_button = (FloatingActionButton) findViewById(R.id.start_button);
        step_button = (FloatingActionButton) findViewById(R.id.step_button);
        pause_button = (FloatingActionButton) findViewById(R.id.pause_button);
        report_button = (FloatingActionButton) findViewById(R.id.report_button);
        ignore_button = (FloatingActionButton) findViewById(R.id.ignore_button);
        options_menu = (FloatingActionsMenu) findViewById(R.id.options_menu);

        if(job.getState().equals("FINALIZADA")) {
            // Title
            title_step.setText(R.string.job_finish);

        }else if (job.getState().equals("NOTIFICADA") || job.getState().equals("ACEPTADA")){
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
            step = mJob.nextStep(job);
            if(step == null) {
                Toast.makeText(StepActivity.this, R.string.on_failure, Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            // Title
            title_step.setText(step.getTitle());

            // Message
            message_step.setVisibility(View.VISIBLE);
            message_step.setText(step.getMessage());

            if(job.getState().equals("NOTIFICADA")) {
                start_button.setVisibility(View.VISIBLE);
                cancel_button.setVisibility(View.VISIBLE);

            } else if(job.getState().equals("ACEPTADA")) {
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
            }
        }

        // Toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle( Utils.capitalize(job.getWorktypename()) );
        getSupportActionBar().setSubtitle( "(" + Integer.toString(job.getId()) + ") " + job.getDocument() );
    }

    public void openMenu(View view) {
        if(!job.getState().equals("FINALIZADA")) {
            actions_menu.expand();
        }
    }

    public void cancelStep(View view) {

        Intent intent = new Intent(StepActivity.this, ReportActivity.class);
        intent.putExtra("STEP", step.getId());
        intent.putExtra("ACTION", Constants.INTENT_CANCEL);
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

                        // Sync report server
                        mJob.storeNotification(job, step, mSession.getPartner(), true, date, "ACEPTADA",
                                false, false, null, latitude, longitude, null, false, false, null, null, 0, null);

                        // Transaction successful
                        mJob.getDb().setTransactionSuccessful();

                        // Set RESULT_REFRESH_ATTENDING
                        result = Constants.RESULT_REFRESH_ATTENDING;
                        Toast.makeText(StepActivity.this, R.string.job_accept, Toast.LENGTH_LONG).show();

                        // Nextstep
                        nextStep();
                    }catch (Exception e) {
                        Log.e(TAG, e.getMessage());

                        tracker.send(new HitBuilders.ExceptionBuilder()
                                .setDescription(String.format("%s:%s", TAG, e.getLocalizedMessage()))
                                .setFatal(false)
                                .build());

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
                            false, false, null, latitude, longitude, null, false, false, action, message_action, 0, null);

                    // Transaction successful
                    mJob.getDb().setTransactionSuccessful();

                    Toast.makeText(StepActivity.this, successful, Toast.LENGTH_LONG).show();

                }catch (Exception e) {
                    Log.e(TAG, e.getMessage());

                    tracker.send(new HitBuilders.ExceptionBuilder()
                            .setDescription(String.format("%s:%s", TAG, e.getLocalizedMessage()))
                            .setFatal(false)
                            .build());

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

                case Constants.RESULT_FINISHED:
                    // Set RESULT_REFRESH_FINISHED
                    result = Constants.RESULT_REFRESH_FINISHED;
                    nextStep();
                    break;
            }
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
}
