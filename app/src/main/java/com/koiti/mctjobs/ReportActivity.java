package com.koiti.mctjobs;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.gun0912.tedpicker.Config;
import com.gun0912.tedpicker.ImagePickerActivity;
import com.koiti.mctjobs.adapters.GridViewAdapter;
import com.koiti.mctjobs.helpers.Constants;
import com.koiti.mctjobs.helpers.ExpandableHeightGridView;
import com.koiti.mctjobs.helpers.GPSTracker;
import com.koiti.mctjobs.helpers.GalleryData;
import com.koiti.mctjobs.helpers.UserSessionManager;
import com.koiti.mctjobs.helpers.Utils;
import com.koiti.mctjobs.models.Discard;
import com.koiti.mctjobs.models.Image;
import com.koiti.mctjobs.models.Job;
import com.koiti.mctjobs.models.Report;
import com.koiti.mctjobs.models.Step;
import com.koiti.mctjobs.sqlite.DataBaseManagerJob;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class ReportActivity extends ActionBarActivity {
    private static final String TAG = ReportActivity.class.getSimpleName();

    private Job job;
    private Step step;
    private GPSTracker gps;
    private int result;
    private int action;

    private UserSessionManager mSession;
    private DataBaseManagerJob mJob;
    private Tracker tracker;

    private ImageView mReportImage;
    private TextView message_step;
    private TextView text_step;
    private TextView info_step;
    private EditText edit_step;
    private TextView reporttype_text;
    private Spinner reporttype_step;
    private TextView text_gallery_step;
    private ExpandableHeightGridView grid_gallery_step;
    private Button report_step;

    private GridViewAdapter gAdapter;
    public GalleryData gallery_data;
    public ArrayList<Image> gallery_images  = new ArrayList<Image>();
    public String mCurrentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        // Get tracker.
        tracker = ((Application) getApplication()).getTracker();

        // Session
        mSession = new UserSessionManager(this);
        // Check user login
        if(mSession.checkLogin()) {
            finish();
        }

        // Database
        mJob = new DataBaseManagerJob(this);

        // Gallery
        gallery_data = new GalleryData(ReportActivity.this);

        // References
        message_step = (TextView) findViewById(R.id.message_step);
        text_step = (TextView) findViewById(R.id.text_step);
        info_step = (TextView) findViewById(R.id.info_step);
        edit_step = (EditText) findViewById(R.id.edit_step);
        reporttype_text = (TextView) findViewById(R.id.reporttype_text);
        reporttype_step = (Spinner) findViewById(R.id.reporttype_step);
        text_gallery_step = (TextView) findViewById(R.id.text_gallery_step);
        grid_gallery_step = (ExpandableHeightGridView) findViewById(R.id.gallery_step);
        report_step = (Button) findViewById(R.id.report_step);
        mReportImage = (ImageView) findViewById(R.id.step_title_image);

        // Intent
        Intent intent = getIntent();

        // Action
        action = (Integer) intent.getIntExtra("ACTION", 0);

        // Step
        step = mJob.getStep((Integer) intent.getIntExtra("STEP", 0));
        if (step == null) {
            Toast.makeText(this, R.string.on_failure, Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Job
        job = mJob.getJob(step.getId_work());
        if (job == null) {
            Toast.makeText(this, R.string.on_failure, Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Load image vehicle
        ImageLoader loaderTitle = ImageLoader.getInstance();
        DisplayImageOptions optionsTitle = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisc(true).resetViewBeforeLoading(true).build();
        loaderTitle.displayImage("drawable://" + R.drawable.job_background, mReportImage, optionsTitle);

        // Message
        message_step.setText(step.getMessage());

        // Textarea
        if(step.getTextarea() || Arrays.asList(new Integer[]{ Constants.INTENT_REPORT, Constants.INTENT_CANCEL, Constants.INTENT_DISCARD }).contains( action )) {
            text_step.setVisibility(View.VISIBLE);
            edit_step.setVisibility(View.VISIBLE);
        }

        // Spinner, Spinner discard types
        ArrayList<Report> reportList;
        switch ( action ) {
            case Constants.INTENT_DISCARD:
                reportList = mJob.getDiscardList(job);
                break;
            default:
                reportList = mJob.getReporsList(step);
                break;
        }

        if(reportList.size() > 0) {
            ArrayAdapter<Report> adapter = new ArrayAdapter<Report>(this, android.R.layout.simple_spinner_item, reportList);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            reporttype_step.setAdapter(adapter);

            reporttype_text.setVisibility(View.VISIBLE);
            reporttype_step.setVisibility(View.VISIBLE);
        }

        // Info
        if( Arrays.asList(new Integer[]{ Constants.INTENT_IGNORE, Constants.INTENT_PAUSE, Constants.INTENT_UNPAUSE,
                Constants.INTENT_REPORT, Constants.INTENT_CANCEL, Constants.INTENT_DISCARD }).contains( action )  )
        {
            info_step.setVisibility(View.VISIBLE);
            switch ( action ) {
                case Constants.INTENT_IGNORE:
                    info_step.setText(R.string.text_ignore);
                    break;

                case Constants.INTENT_PAUSE:
                    if( step.getPaused() ) {
                        info_step.setText(R.string.text_unpause);
                        report_step.setText(R.string.job_unpause_title);
                    }else {
                        info_step.setText(R.string.text_pause);
                        report_step.setText(R.string.job_pause_title);

                    }
                    break;

                case Constants.INTENT_UNPAUSE:
                    info_step.setText(R.string.text_unpause);
                    report_step.setText(R.string.job_unpause_title);
                    break;

                case Constants.INTENT_REPORT:
                    info_step.setText(R.string.text_report);
                    break;

                case Constants.INTENT_CANCEL:
                    info_step.setText(R.string.text_cancel);
                    report_step.setText(R.string.job_cancel_title);
                    break;

                case Constants.INTENT_DISCARD:
                    info_step.setText(R.string.text_discard);
                    report_step.setText(R.string.job_discard_title);
                    break;
            }
        }

        // Gallery
        // gallery_images = gallery_data.getImagesGallery( step );
        gAdapter = new GridViewAdapter(this, R.layout.gallery_list_row, gallery_images);
        grid_gallery_step.setExpanded(true);
        grid_gallery_step.setAdapter(gAdapter);

        text_gallery_step.setVisibility(View.VISIBLE);
        grid_gallery_step.setVisibility(View.VISIBLE);

        // Toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle( Utils.capitalize(step.getTitle()) );
        getSupportActionBar().setSubtitle( "(" + Integer.toString(job.getId()) + ") " + job.getDocument() );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.report, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.action_photo:
                Config config = new Config();
                config.setToolbarTitleRes(R.string.action_image);
                config.setSavedDirectoryName(R.string.gallery_name);
                ImagePickerActivity.setConfig(config);

                Intent intent  = new Intent(ReportActivity.this, ImagePickerActivity.class);
                startActivityForResult(intent, Constants.RESULT_SELECT_PICTURE);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void nextStep() {

        Intent intent = new Intent();
        intent.putExtra("JOB", job.getId());
        setResult(result, intent);

        finish();
    }

    public void reportStep(View view) {
        gps = new GPSTracker(this);
        if (gps.canGetLocation()) {
            // Location
            final double latitude = gps.getLatitude();
            final double longitude = gps.getLongitude();

            // Report type
            final Report reporttype = (Report) reporttype_step.getSelectedItem();

            // Message wrote textarea
            String text_wrote = null;
             if( step.getTextarea() || Arrays.asList(new Integer[]{ Constants.INTENT_REPORT, Constants.INTENT_CANCEL, Constants.INTENT_DISCARD }).contains( action ) ) {
                text_wrote = edit_step.getText().toString();
                if( (step.getTextareamandatory() || Arrays.asList(new Integer[]{ Constants.INTENT_REPORT, Constants.INTENT_CANCEL, Constants.INTENT_DISCARD }).contains( action ))
                        && text_wrote.isEmpty() ) {
                    Toast.makeText(this, R.string.text_required, Toast.LENGTH_LONG).show();
                    return;
                }
             }
            final String message_wrote = text_wrote;

            // Images
            if( step.getPhotosmandatory() && !Arrays.asList(new Integer[]{ Constants.INTENT_REPORT, Constants.INTENT_CANCEL, Constants.INTENT_DISCARD }).contains( action )
                    && gallery_images.size() == 0 ){
                Toast.makeText(this, R.string.photo_required, Toast.LENGTH_LONG).show();
                return;
            }

            // Title modal
            String title = getResources().getString(R.string.job_report_title);
            String message = step.getMessage();
            if( Arrays.asList(new Integer[]{ Constants.INTENT_CANCEL }).contains(action) ) {
                title = getResources().getString(R.string.job_cancel_title);
                message = getResources().getString(R.string.job_cancel_message);

            }else if( Arrays.asList(new Integer[]{ Constants.INTENT_DISCARD }).contains(action) ) {
                title = getResources().getString(R.string.job_discard_title);
                message = getResources().getString(R.string.job_discard_message);

            }else if( Arrays.asList(new Integer[]{ Constants.INTENT_PAUSE }).contains(action) ) {
                if( step.getPaused() ) {
                    title = getResources().getString(R.string.job_unpause_title);
                    message = getResources().getString(R.string.job_unpause_message);
                }else {
                    title = getResources().getString(R.string.job_pause_title);
                    message = getResources().getString(R.string.job_pause_message);
                }
            }

            // Confirm action
            AlertDialog.Builder alertDialog = Utils.dialogBuilder(this);
            alertDialog.setTitle(title);
            alertDialog.setMessage(message);

            alertDialog.setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog,int which) {
                    mJob.getDb().beginTransaction();
                    try {
                        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String date = dateFormat.format(new Date());

                        // Cancel
                        if( Arrays.asList(new Integer[]{ Constants.INTENT_CANCEL }).contains( action )) {
                            mJob.changeStatus(job.getId(), "RECHAZADA");

                            // Sync report cancel
                            mJob.storeNotification(job, step, mSession.getPartner(), true, date, "RECHAZADA",
                                    false, false, null, latitude, longitude, message_wrote, false, false, null, null, reporttype.getId(), gallery_images);

                            // Set RESULT_NEXT_STEP
                            result = Constants.RESULT_CANCEL;

                        // Descartar
                        }else if( Arrays.asList(new Integer[]{ Constants.INTENT_DISCARD }).contains( action )) {
                            mJob.changeStatus(job.getId(), "DESCARTADA");

                            // Sync report cancel
                            mJob.storeNotification(job, step, mSession.getPartner(), false, date, "DESCARTADA",
                                    false, false, null, latitude, longitude, message_wrote, false, false, null, null, reporttype.getId(), gallery_images);

                            // Set RESULT_NEXT_STEP
                            result = Constants.RESULT_DISCARD;

                        // Pause
                        }else if( Arrays.asList(new Integer[]{ Constants.INTENT_PAUSE }).contains( action )) {
                            // Set pause
                            mJob.setPause(step, date);

                            // Sync report pause
                            mJob.storeNotification(job, step, mSession.getPartner(), false, date, job.getState(), (step.getPaused() ? false : true),
                                    (step.getPaused() ? true : false), 0, latitude, longitude, message_wrote, false, false, null, null, reporttype.getId(), gallery_images);

                            // Set RESULT_NEXT_STEP
                            result = Constants.RESULT_NEXT_STEP;

                        // Ignorar
                        }else if( Arrays.asList(new Integer[]{ Constants.INTENT_IGNORE }).contains( action )) {
                            // Set ignore step
                            mJob.setIgnore(step, date);
                            mJob.increaseStep(job.getId());

                            // Sync report step
                            mJob.storeNotification(job, step, mSession.getPartner(), true, date, job.getState(),
                                    false, false, null, latitude, longitude, message_wrote, true, false, null, null, reporttype.getId(), gallery_images);

                            // Set RESULT_NEXT_STEP
                            result = Constants.RESULT_NEXT_STEP;

                        // Novedad
                        }else if( Arrays.asList(new Integer[]{ Constants.INTENT_REPORT }).contains( action ) ) {
                            // Sync report step
                            mJob.storeNotification(job, step, mSession.getPartner(), false, date, job.getState(),
                                    false, false, null, latitude, longitude, message_wrote, false, false, null, null, reporttype.getId(), gallery_images);

                            // Set RESULT_NEXT_STEP
                            result = Constants.RESULT_NEXT_STEP;

                            Toast.makeText(ReportActivity.this, R.string.step_send_report, Toast.LENGTH_SHORT).show();

                            // Default step
                        }else {
                            // Set report step
                            mJob.changeReport(step, date);
                            mJob.increaseStep(job.getId());

                            // Sync report step
                            mJob.storeNotification(job, step, mSession.getPartner(), true, date, ((!mJob.exitNextStep(job) || Arrays.asList(new Integer[]{ Constants.INTENT_CLOSE_WORK }).contains(reporttype.getId())) ? "FINALIZADA" : job.getState()),
                                    false, false, null, latitude, longitude, message_wrote, false, false, null, null, reporttype.getId(), gallery_images);

                            // Set RESULT_NEXT_STEP
                            result = Constants.RESULT_NEXT_STEP;

                            // Last step or type close work
                            if(!mJob.exitNextStep(job) || Arrays.asList(new Integer[]{ Constants.INTENT_CLOSE_WORK }).contains( reporttype.getId() ))
                            {
                                mJob.changeStatus(job.getId(), "FINALIZADA");

                                // Set RESULT_NEXT_STEP
                                result = Constants.RESULT_FINISHED;

                                Toast.makeText(ReportActivity.this, R.string.job_finish, Toast.LENGTH_SHORT).show();
                            }
                        }

                        // Transaction successful
                        mJob.getDb().setTransactionSuccessful();

                        // Nextstep
                        nextStep();

                    }catch (Exception e) {
                        Log.e(TAG, e.getMessage());

                        tracker.send(new HitBuilders.ExceptionBuilder()
                                .setDescription(String.format("%s:%s", TAG, e.getLocalizedMessage()))
                                .setFatal(false)
                                .build());

                        Toast.makeText(ReportActivity.this, R.string.on_failure, Toast.LENGTH_LONG).show();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.RESULT_SELECT_PICTURE && resultCode == Activity.RESULT_OK) {
            // List images
            ArrayList<Uri> uris = data.getParcelableArrayListExtra(ImagePickerActivity.EXTRA_IMAGE_URIS);
            // Add images
            for (Uri uri : uris) {
                // Copy file
                File source = new File( uri.getPath() );
                if( source.isFile() ) {
                    // Add image
                    Image image = new Image(source.getName(), Constants.GALLERY_NAME, source.getPath(), false);
                    image.setCreation(source.lastModified());
                    gallery_images.add(0, image);
                }
            }
            gAdapter.notifyDataSetChanged();
        }
    }
}
