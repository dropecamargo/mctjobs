package com.koiti.mctjobs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.CardView;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.gun0912.tedpicker.Config;
import com.gun0912.tedpicker.ImagePickerActivity;
import com.koiti.mctjobs.adapters.GridViewAdapter;
import com.koiti.mctjobs.fragments.DatePickerFragment;
import com.koiti.mctjobs.fragments.TimePickerFragment;
import com.koiti.mctjobs.helpers.Constants;
import com.koiti.mctjobs.helpers.ExpandableHeightGridView;
import com.koiti.mctjobs.helpers.GPSTracker;
import com.koiti.mctjobs.helpers.GalleryData;
import com.koiti.mctjobs.helpers.UserSessionManager;
import com.koiti.mctjobs.helpers.Utils;
import com.koiti.mctjobs.models.Field;
import com.koiti.mctjobs.models.Image;
import com.koiti.mctjobs.models.Item;
import com.koiti.mctjobs.models.Job;
import com.koiti.mctjobs.models.Report;
import com.koiti.mctjobs.models.Step;
import com.koiti.mctjobs.sqlite.DataBaseManagerJob;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

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
    private TextView changestep_text;
    private Spinner changestep_step;
    private TextView text_gallery_step;
    private ExpandableHeightGridView grid_gallery_step;
    private Button report_step;
    private TextView title_dynamic_form;

    private CardView cardview_dynamic_form;
    private LinearLayout layout_dynamic_form;
    private ArrayList<Field> fieldList;

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
            return;
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
        changestep_text = (TextView) findViewById(R.id.changestep_text);
        changestep_step = (Spinner) findViewById(R.id.changestep_step);
        text_gallery_step = (TextView) findViewById(R.id.text_gallery_step);
        grid_gallery_step = (ExpandableHeightGridView) findViewById(R.id.gallery_step);
        report_step = (Button) findViewById(R.id.report_step);
        mReportImage = (ImageView) findViewById(R.id.step_title_image);

        title_dynamic_form = (TextView) findViewById(R.id.title_dynamic_form);
        cardview_dynamic_form = (CardView) findViewById(R.id.cardview_dynamic_form);
        layout_dynamic_form = (LinearLayout) findViewById(R.id.layout_dynamic_form);

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
        if( Arrays.asList(new Integer[]{ Constants.INTENT_DEFAULT, Constants.INTENT_IGNORE, Constants.INTENT_PAUSE, Constants.INTENT_UNPAUSE,
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

                    // Change step
                    if(step.getUnsorted()) {
                        // Get pending steps phase
                        ArrayList<Step> stepList = mJob.getPhaseStepsList(step);

                        // Add default selection
                        Step defaultStep = new Step(0);
                        defaultStep.setTitle("NO");
                        stepList.add(defaultStep);

                        // Add adapter steps
                        ArrayAdapter<Step> adapter = new ArrayAdapter<Step>(this, android.R.layout.simple_spinner_item, stepList);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        changestep_step.setAdapter(adapter);

                        changestep_text.setVisibility(View.VISIBLE);
                        changestep_step.setVisibility(View.VISIBLE);
                        changestep_step.setSelection(adapter.getPosition(defaultStep));
                    }
                    break;

                case Constants.INTENT_CANCEL:
                    info_step.setText(R.string.text_cancel);
                    report_step.setText(R.string.job_cancel_title);
                    break;

                case Constants.INTENT_DISCARD:
                    info_step.setText(R.string.text_discard);
                    report_step.setText(R.string.job_discard_title);
                    break;

                default:
                    info_step.setVisibility(View.GONE);

                    // Dynamic fields
                    fieldList = mJob.getFieldsStep(step);
                    if(fieldList.size() > 0) {
                        title_dynamic_form.setText(R.string.text_info_fields);
                        cardview_dynamic_form.setVisibility(View.VISIBLE);
                    }

                    for (int i = 0; i < fieldList.size(); i++) {
                        Field field = fieldList.get(i);

                        // Add label
                        TextView textView = new TextView(this);
                        textView.setText((i+1)+". " + field.getTitle());
                        layout_dynamic_form.addView(textView);

                        switch (field.getType()){
                            case "CHAR":
                                EditText charText = new EditText(this);
                                charText.setTag("chartext_" + field.getId());
                                charText.setLayoutParams(new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT));
                                layout_dynamic_form.addView(charText);
                                break;

                            case "NUMBER":
                                EditText numberText = new EditText(this);
                                numberText.setTag("numbertext_" + field.getId());
                                numberText.setLayoutParams(new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT));
                                numberText.setInputType(InputType.TYPE_CLASS_NUMBER);
                                layout_dynamic_form.addView(numberText);
                                break;

                            case "NUMBERSPINER":
                                ArrayList<Item> spinnerArray = new ArrayList<Item>();
                                spinnerArray.add(new Item(0, "Seleccione"));

                                try {
                                    JSONArray jsonArray = new JSONArray(field.getDomain());
                                    for (int j = 0; j < jsonArray.length(); ++j) {
                                        spinnerArray.add(new Item(jsonArray.getInt(j), jsonArray.getString(j)));
                                    }
                                } catch (JSONException e) {
                                    Log.e(TAG, e.getMessage());

                                    tracker.send(new HitBuilders.ExceptionBuilder()
                                            .setDescription(String.format("%s:%s NUMBERSPINER", TAG, e.getLocalizedMessage()))
                                            .setFatal(false)
                                            .build());
                                }finally {
                                    ArrayAdapter<Item> spinnerAdapter = new ArrayAdapter<Item>(this, android.R.layout.simple_spinner_item, spinnerArray);
                                    spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                                    Spinner spinner = new Spinner(this, Spinner.MODE_DIALOG);
                                    spinner.setTag("spinner_" + field.getId());
                                    spinner.setLayoutParams(new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT));
                                    spinner.setAdapter(spinnerAdapter);
                                    layout_dynamic_form.addView(spinner);
                                }
                                break;

                            case "BOOLEAN":
                                CheckBox checkBox = new CheckBox(this);
                                checkBox.setTag("checkbox_" + field.getId());
                                layout_dynamic_form.addView(checkBox);
                                break;

                            case "DATE":
                                final EditText dateText = new EditText(this);
                                dateText.setTag("datetext_" + field.getId());
                                dateText.setLayoutParams(new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT));
                                dateText.setClickable(true);
                                dateText.setFocusable(false);
                                dateText.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
                                dateText.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        DatePickerFragment newFragment = DatePickerFragment.newInstance(dateText.getText().toString(), new DatePickerDialog.OnDateSetListener() {
                                            @Override
                                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                                // +1 because january is zero
                                                month++;

                                                StringBuffer date = new StringBuffer();
                                                date.append(year).append("-");
                                                date.append( (month < 10) ? "0" + month : month ).append("-");
                                                date.append( (day < 10) ? "0" + day : day );


                                                dateText.setText(date.toString());
                                            }
                                        });
                                        newFragment.show(getFragmentManager(), "datePicker");
                                    }
                                });

                                layout_dynamic_form.addView(dateText);
                                break;

                            case "DATETIME":
                                // Add Date
                                final EditText datetText = new EditText(this);
                                datetText.setTag("datettext_" + field.getId());
                                datetText.setLayoutParams(new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT));
                                datetText.setClickable(true);
                                datetText.setFocusable(false);
                                datetText.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
                                datetText.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        DatePickerFragment newFragment = DatePickerFragment.newInstance(datetText.getText().toString(), new DatePickerDialog.OnDateSetListener() {
                                            @Override
                                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                                // +1 because january is zero
                                                month++;

                                                StringBuffer date = new StringBuffer();
                                                date.append(year).append("-");
                                                date.append( (month < 10) ? "0" + month : month ).append("-");
                                                date.append( (day < 10) ? "0" + day : day );


                                                datetText.setText(date.toString());
                                            }
                                        });
                                        newFragment.show(getFragmentManager(), "datePicker");
                                    }
                                });
                                layout_dynamic_form.addView(datetText);

                                // Add time
                                final EditText timeText = new EditText(this);
                                timeText.setTag("timetext_" + field.getId());
                                timeText.setLayoutParams(new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT));
                                timeText.setClickable(true);
                                timeText.setFocusable(false);
                                timeText.setText(new SimpleDateFormat("HH:mm").format(new Date()));
                                timeText.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        TimePickerFragment newFragment = TimePickerFragment.newInstance(timeText.getText().toString(), new TimePickerDialog.OnTimeSetListener() {
                                            @Override
                                            public void onTimeSet(TimePicker view, int hour, int minute) {
                                                StringBuffer time = new StringBuffer();
                                                time.append((hour < 10) ? "0" + hour : hour).append(":")
                                                        .append((minute < 10) ? "0" + minute : minute);
                                                timeText.setText(time.toString());
                                            }
                                        });
                                        newFragment.show(getFragmentManager(), "timePicker");
                                    }
                                });
                                layout_dynamic_form.addView(timeText);
                                break;

                            case "SELECT":
                                ArrayList<Item> selectArray = new ArrayList<Item>();
                                selectArray.add(new Item(0, "Seleccione"));

                                try {
                                    JSONArray jsonArray = new JSONArray(field.getDomain());
                                    for (int j = 0; j < jsonArray.length(); ++j) {
                                        JSONObject json = (JSONObject) jsonArray.get(j);
                                        selectArray.add(new Item(json.getInt("id"), json.getString("name")));
                                    }
                                } catch (JSONException e) {
                                    Log.e(TAG, e.getMessage());

                                    tracker.send(new HitBuilders.ExceptionBuilder()
                                        .setDescription(String.format("%s:%s SELECT", TAG, e.getLocalizedMessage()))
                                        .setFatal(false)
                                        .build());
                                }finally {
                                    ArrayAdapter<Item> selectAdapter = new ArrayAdapter<Item>(this, android.R.layout.simple_spinner_item, selectArray);
                                    selectAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                                    Spinner select = new Spinner(this, Spinner.MODE_DIALOG);
                                    select.setTag("select_" + field.getId());
                                    select.setLayoutParams(new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT));
                                    select.setAdapter(selectAdapter);
                                    layout_dynamic_form.addView(select);
                                }
                                break;
                        }
                    }
                    break;
            }
        }

        // Gallery
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
                                    false, false, null, latitude, longitude, message_wrote, false, false, null, null, reporttype.getId(), gallery_images, null, null);

                            // Set RESULT_NEXT_STEP
                            result = Constants.RESULT_CANCEL;

                        // Descartar
                        }else if( Arrays.asList(new Integer[]{ Constants.INTENT_DISCARD }).contains( action )) {
                            mJob.changeStatus(job.getId(), "DESCARTADA");

                            // Sync report cancel
                            mJob.storeNotification(job, step, mSession.getPartner(), false, date, "DESCARTADA",
                                    false, false, null, latitude, longitude, message_wrote, false, false, null, null, reporttype.getId(), gallery_images, null, null);

                            // Set RESULT_NEXT_STEP
                            result = Constants.RESULT_DISCARD;

                        // Pause
                        }else if( Arrays.asList(new Integer[]{ Constants.INTENT_PAUSE }).contains( action )) {
                            // Set pause
                            mJob.setPause(step, date);

                            // Sync report pause
                            mJob.storeNotification(job, step, mSession.getPartner(), false, date, job.getState(), (step.getPaused() ? false : true),
                                    (step.getPaused() ? true : false), 0, latitude, longitude, message_wrote, false, false, null, null, reporttype.getId(), gallery_images, null, null);

                            // Set RESULT_NEXT_STEP
                            result = Constants.RESULT_NEXT_STEP;

                        // Ignorar
                        }else if( Arrays.asList(new Integer[]{ Constants.INTENT_IGNORE }).contains( action )) {
                            // Set ignore step
                            mJob.setIgnore(step, date);

                            // Increase Step
                            Step nextstep = mJob.nextStep(job.getId());
                            mJob.increaseStep(job.getId(), (nextstep != null ? nextstep.getId() : 0));

                            // Sync report step
                            mJob.storeNotification(job, step, mSession.getPartner(), true, date, job.getState(),
                                    false, false, null, latitude, longitude, message_wrote, true, false, null, null, reporttype.getId(), gallery_images, null, null);

                            // Set RESULT_NEXT_STEP
                            result = Constants.RESULT_NEXT_STEP;

                        // Novedad
                        }else if( Arrays.asList(new Integer[]{ Constants.INTENT_REPORT }).contains( action ) ) {
                            // Set netxstep
                            Step nextstep = (Step) changestep_step.getSelectedItem();;
                            if(step.getUnsorted() && nextstep != null && nextstep.getId() != 0) {
                                mJob.setNextStep(job.getId(), nextstep.getId());
                            }

                            // Sync report step
                            mJob.storeNotification(job, step, mSession.getPartner(), false, date, job.getState(),
                                    false, false, null, latitude, longitude, message_wrote, false, false, null, null, reporttype.getId(), gallery_images,
                                    ((step.getUnsorted() && nextstep != null && nextstep.getId() != 0) ? nextstep.getId() : null), null);

                            // Set RESULT_NEXT_STEP
                            result = Constants.RESULT_NEXT_STEP;

                            Toast.makeText(ReportActivity.this, R.string.step_send_report, Toast.LENGTH_SHORT).show();

                        }else {
                            // Prepare dynamic fields
                            Map<String, String> resultfields = prepareValuesFields();
                            if(resultfields.get("status").toString().equals("error")) {
                                Toast.makeText(ReportActivity.this, resultfields.get("error").toString(), Toast.LENGTH_LONG).show();
                                return;
                            }
                            String fields = resultfields.get("values").toString();

                            // Set report step
                            mJob.changeReport(step, date);

                            // Increase Step
                            Step nextstep = mJob.nextStep(job.getId());
                            mJob.increaseStep(job.getId(), (nextstep != null ? nextstep.getId() : 0));

                            // Sync report step
                            mJob.storeNotification(job, step, mSession.getPartner(), true, date, ((!mJob.exitNextStep(job) || Arrays.asList(new Integer[]{ Constants.INTENT_CLOSE_WORK }).contains(reporttype.getId())) ? "FINALIZADA" : job.getState()),
                                    false, false, null, latitude, longitude, message_wrote, false, false, null, null, reporttype.getId(), gallery_images, null, fields);

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

    public Map prepareValuesFields() throws JSONException {
        Map<String, String> result = new HashMap<>();
        JSONArray values = new JSONArray();

        fieldList = mJob.getFieldsStep(step);
        if(fieldList.size() > 0)
        {
            for (int i = 0; i < fieldList.size(); i++) {
                Field field = fieldList.get(i);

                JSONObject value = new JSONObject();
                value.put("id_field", field.getId().toString());

                switch (field.getType()) {
                    case "CHAR":
                        TextView textView =  (TextView) layout_dynamic_form.findViewWithTag("chartext_" + field.getId());
                        value.put("value", textView.getText().toString());
                        break;
                    case "NUMBER":
                        EditText numberText = (EditText) layout_dynamic_form.findViewWithTag("numbertext_" + field.getId());
                        value.put("value", numberText.getText().toString());
                        break;

                    case "NUMBERSPINER":
                        Spinner spinner = (Spinner) layout_dynamic_form.findViewWithTag("spinner_" + field.getId());
                        Item item = (Item) spinner.getSelectedItem();
                        value.put("value", item.getId());
                        break;

                    case "BOOLEAN":
                        CheckBox checkBox = (CheckBox) layout_dynamic_form.findViewWithTag("checkbox_" + field.getId());
                        value.put("value", checkBox.isChecked());
                        break;

                    case "DATE":
                        EditText dateText = (EditText) layout_dynamic_form.findViewWithTag("datetext_" + field.getId());
                        value.put("value", dateText.getText().toString());
                        break;

                    case "DATETIME":
                        EditText EditText = (EditText) layout_dynamic_form.findViewWithTag("datettext_" + field.getId());
                        EditText timeText = (EditText) layout_dynamic_form.findViewWithTag("timetext_" + field.getId());
                        value.put("value", EditText.getText().toString() + " " + timeText.getText().toString());
                        break;

                    case "SELECT":
                        Spinner select = (Spinner) layout_dynamic_form.findViewWithTag("select_" + field.getId());
                        Item itemselect = (Item) select.getSelectedItem();
                        value.put("value", itemselect.getId());
                        break;
                }

                // Valid mandatory field
                if(field.getMandatory() && (value.getString("value") == null || value.getString("value").isEmpty())){
                    result.put("status", "error");
                    result.put("error", getResources().getString(R.string.error_field_dynamic_required, field.getTitle()));
                    return result;
                }
                values.put(value);
            }
        }

        result.put("status", "success");
        result.put("values", values.toString());
        return result;
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
