package com.koiti.mctjobs.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.koiti.mctjobs.R;
import com.koiti.mctjobs.helpers.GalleryData;
import com.koiti.mctjobs.helpers.UserSessionManager;
import com.koiti.mctjobs.models.Discard;
import com.koiti.mctjobs.models.Document;
import com.koiti.mctjobs.models.Field;
import com.koiti.mctjobs.models.Image;
import com.koiti.mctjobs.models.Job;
import com.koiti.mctjobs.models.Notification;
import com.koiti.mctjobs.models.Phase;
import com.koiti.mctjobs.models.Report;
import com.koiti.mctjobs.models.Step;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class DataBaseManagerJob extends DataBaseManager {

    public Context context;
    public GalleryData gallery_data;
    private UserSessionManager mSession;

    public static final String CREATE_TABLE = "CREATE TABLE "
            + JobContract.TABLE + "("
            + JobContract.KEY_ID + " INTEGER PRIMARY KEY,"
            + JobContract.KEY_ID_USER + " INTEGER,"
            + JobContract.KEY_NOTES + " TEXT,"
            + JobContract.KEY_DOCUMENT + " TEXT,"
            + JobContract.KEY_WORKTYPENAME + " TEXT,"
            + JobContract.KEY_STATE + " TEXT,"
            + JobContract.KEY_STEP + " INTEGER,"
            + JobContract.KEY_CREATED + " TEXT,"
            + JobContract.KEY_FORMATDATE + " TEXT,"
            + JobContract.KEY_ABSTRACT + " TEXT,"
            + JobContract.KEY_DETAILS + " TEXT,"
            + JobContract.KEY_AMOUNTSTEPS + " INTEGER,"
            + JobContract.KEY_NEXTSTEP + " INTEGER,"
            + JobContract.KEY_CURRENTSTEP + " INTEGER" + ")";

    public DataBaseManagerJob(Context context) {
        super(context);

        this.context = context;

        // Gallery
        gallery_data = new GalleryData(this.context);

        // Session
        mSession = new UserSessionManager(this.context);
    }

    public void store(Job job) {
        ContentValues values = new ContentValues();
        values.put(JobContract.KEY_ID, job.getId());
        values.put(JobContract.KEY_ID_USER, job.getId_user());
        values.put(JobContract.KEY_DOCUMENT, job.getDocument());
        values.put(JobContract.KEY_NOTES, job.getNotes());
        values.put(JobContract.KEY_CREATED, job.getCreated());
        values.put(JobContract.KEY_WORKTYPENAME, job.getWorktypename());
        values.put(JobContract.KEY_STATE, job.getState());
        values.put(JobContract.KEY_STEP, job.getStep());
        values.put(JobContract.KEY_FORMATDATE, job.getFormatdate());
        values.put(JobContract.KEY_ABSTRACT, job.getAbstrac());
        values.put(JobContract.KEY_DETAILS, job.getDetails());
        values.put(JobContract.KEY_AMOUNTSTEPS, job.getAmountsteps());
        values.put(JobContract.KEY_NEXTSTEP, job.getNextstep());

        super.getDb().insertOrThrow(DataBaseManagerJob.JobContract.TABLE, null, values);
    }

    public void storeDiscard(Discard discard) {
        ContentValues values = new ContentValues();
        values.put(DataBaseManagerDiscardType.DiscardContract.KEY_ID, discard.getId());
        values.put(DataBaseManagerDiscardType.DiscardContract.KEY_ID_WORK, discard.getId_work());
        values.put(DataBaseManagerDiscardType.DiscardContract.KEY_NAME, discard.getName());

        super.getDb().insertOrThrow(DataBaseManagerDiscardType.DiscardContract.TABLE, null, values);
    }

    public void storeStep(Step step) {
        ContentValues values = new ContentValues();
        values.put(DataBaseManagerStep.StepContract.KEY_ID, step.getId());
        values.put(DataBaseManagerStep.StepContract.KEY_ID_WORK, step.getId_work());
        values.put(DataBaseManagerStep.StepContract.KEY_TITLE, step.getTitle());
        values.put(DataBaseManagerStep.StepContract.KEY_MESSAGE, step.getMessage());
        values.put(DataBaseManagerStep.StepContract.KEY_REPORT, step.getReport());
        values.put(DataBaseManagerStep.StepContract.KEY_SEQUENCE, step.getSequence());
        values.put(DataBaseManagerStep.StepContract.KEY_PAUSED, step.getPaused());
        values.put(DataBaseManagerStep.StepContract.KEY_PAUSETIME, step.getPausetime());
        values.put(DataBaseManagerStep.StepContract.KEY_DATE, step.getDate());
        values.put(DataBaseManagerStep.StepContract.KEY_IGNORED, step.getIgnored());
        values.put(DataBaseManagerStep.StepContract.KEY_RECEIVEREMAIL, step.getReceiveremail());
        values.put(DataBaseManagerStep.StepContract.KEY_EXPECTEDDATE, step.getExpecteddate());
        values.put(DataBaseManagerStep.StepContract.KEY_IGNORABLE, step.getIgnorable());
        values.put(DataBaseManagerStep.StepContract.KEY_PAUSABLE, step.getPausable());
        values.put(DataBaseManagerStep.StepContract.KEY_ID_STEP, step.getId_step());
        values.put(DataBaseManagerStep.StepContract.KEY_PHOTOS, step.getPhotos());
        values.put(DataBaseManagerStep.StepContract.KEY_SENDEMAIL, step.getSendemail());
        values.put(DataBaseManagerStep.StepContract.KEY_INDEXA, step.getIndex());
        values.put(DataBaseManagerStep.StepContract.KEY_ID_PHASE, step.getId_phase());
        values.put(DataBaseManagerStep.StepContract.KEY_PHASE_TEXT, step.getPhase_text());
        values.put(DataBaseManagerStep.StepContract.KEY_STEP_NAME, step.getStep_name());
        values.put(DataBaseManagerStep.StepContract.KEY_TEXTAREA, step.getTextarea());
        values.put(DataBaseManagerStep.StepContract.KEY_TEXTAREAMANDATORY, step.getTextareamandatory());
        values.put(DataBaseManagerStep.StepContract.KEY_PHOTOSMANDATORY, step.getPhotosmandatory());

        super.getDb().insertOrThrow(DataBaseManagerStep.StepContract.TABLE, null, values);
    }

    public void storePhase(Phase phase) {
        ContentValues values = new ContentValues();
        values.put(DatabaseManagerPhase.PhaseContract.KEY_ID, phase.getId());
        values.put(DatabaseManagerPhase.PhaseContract.KEY_ID_PHASE, phase.getId_phase());
        values.put(DatabaseManagerPhase.PhaseContract.KEY_ID_WORK, phase.getId_work());
        values.put(DatabaseManagerPhase.PhaseContract.KEY_ID_STEP, phase.getStep());
        values.put(DatabaseManagerPhase.PhaseContract.KEY_NAME, phase.getName());
        values.put(DatabaseManagerPhase.PhaseContract.KEY_UNSORTED, phase.getUnsorted());
        values.put(DatabaseManagerPhase.PhaseContract.KEY_BEGINDATE, phase.getBegindate());

        super.getDb().insertOrThrow(DatabaseManagerPhase.PhaseContract.TABLE, null, values);
    }

    public void storeReport(Report report) {
        ContentValues values = new ContentValues();
        values.put(DataBaseManagerReportType.ReportContract.KEY_ID, report.getId());
        values.put(DataBaseManagerReportType.ReportContract.KEY_ID_WORK, report.getId_work());
        values.put(DataBaseManagerReportType.ReportContract.KEY_ID_STEP, report.getId_step());
        values.put(DataBaseManagerReportType.ReportContract.KEY_NAME, report.getName());

        super.getDb().insertOrThrow(DataBaseManagerReportType.ReportContract.TABLE, null, values);
    }

    public void storeField(Field field) {
        ContentValues values = new ContentValues();
        values.put(DataBaseManagerField.FieldContract.KEY_ID, field.getId());
        values.put(DataBaseManagerField.FieldContract.KEY_ID_WORK, field.getId_work());
        values.put(DataBaseManagerField.FieldContract.KEY_ID_STEP, field.getId_step());
        values.put(DataBaseManagerField.FieldContract.KEY_TITLE, field.getTitle());
        values.put(DataBaseManagerField.FieldContract.KEY_TYPE, field.getType());
        values.put(DataBaseManagerField.FieldContract.KEY_MANDATORY, field.getMandatory());
        values.put(DataBaseManagerField.FieldContract.KEY_DOMAIN, field.getDomain());
        values.put(DataBaseManagerField.FieldContract.KEY_ADEFAULT, field.get_default());
        values.put(DataBaseManagerField.FieldContract.KEY_ORDEN, field.getOrden());

        super.getDb().insertOrThrow(DataBaseManagerField.FieldContract.TABLE, null, values);
    }

    public void storeNotification(Job job, Step step, Integer id_user, Boolean modify_step, String report_date, String state,
                                  Boolean paused, Boolean unpaused, Integer pausetime, Double latitude, Double longitude, String message_wrote, Boolean ignored,
                                  Boolean create_action, String action, String message_action, Integer report_type, ArrayList<Image> pictures,
                                  Integer id_step_new, String fields) throws JSONException {

        DecimalFormatSymbols dfs = new DecimalFormatSymbols(Locale.US);
        dfs.setDecimalSeparator('.');
        DecimalFormat df = new DecimalFormat(".########", dfs);

        ContentValues values = new ContentValues();
        values.put(DataBaseManagerNotification.NotificationContract.KEY_USER, id_user);
        values.put(DataBaseManagerNotification.NotificationContract.KEY_WORK, step.getId_work());
        values.put(DataBaseManagerNotification.NotificationContract.KEY_WORKSTEP, step.getId());
        values.put(DataBaseManagerNotification.NotificationContract.KEY_MODIFY_STEP, modify_step);
        values.put(DataBaseManagerNotification.NotificationContract.KEY_ID_WORKPHASE, 0);
        values.put(DataBaseManagerNotification.NotificationContract.KEY_MODIFY_PHASE, false);
        values.put(DataBaseManagerNotification.NotificationContract.KEY_SEQUENCE, 0);
        values.put(DataBaseManagerNotification.NotificationContract.KEY_REPORT_DATE, report_date);
        values.put(DataBaseManagerNotification.NotificationContract.KEY_STATE, state);
        values.put(DataBaseManagerNotification.NotificationContract.KEY_STATEBEFORE, job.getState());
        values.put(DataBaseManagerNotification.NotificationContract.KEY_PAUSED, paused);
        values.put(DataBaseManagerNotification.NotificationContract.KEY_UNPAUSED, unpaused);
        values.put(DataBaseManagerNotification.NotificationContract.KEY_PAUSETIME, pausetime);
        values.put(DataBaseManagerNotification.NotificationContract.KEY_LATITUDE, latitude != null ? df.format(latitude) : null);
        values.put(DataBaseManagerNotification.NotificationContract.KEY_LONGITUDE, longitude != null ? df.format(longitude) : null);
        values.put(DataBaseManagerNotification.NotificationContract.KEY_MESSAGE, step.getMessage());
        values.put(DataBaseManagerNotification.NotificationContract.KEY_TITTLE, step.getTitle());
        values.put(DataBaseManagerNotification.NotificationContract.KEY_MESSAGE_WROTE, message_wrote != null ? message_wrote : null);
        values.put(DataBaseManagerNotification.NotificationContract.KEY_IGNORE, ignored);
        values.put(DataBaseManagerNotification.NotificationContract.KEY_CREATE_ACTION, create_action);
        values.put(DataBaseManagerNotification.NotificationContract.KEY_ACTION, action);
        values.put(DataBaseManagerNotification.NotificationContract.KEY_MESSAGE_ACTION, message_action);
        values.put(DataBaseManagerNotification.NotificationContract.KEY_PICTURES, pictures != null ? pictures.size() : 0);
        values.put(DataBaseManagerNotification.NotificationContract.KEY_VIDEOS, 0);
        values.put(DataBaseManagerNotification.NotificationContract.KEY_REPORT_TYPE, report_type);
        values.put(DataBaseManagerNotification.NotificationContract.KEY_ID_STEP_NEW, id_step_new);
        values.put(DataBaseManagerNotification.NotificationContract.KEY_FIELDS, fields);

        // Documents
        JSONArray documents = new JSONArray();
        ArrayList cvdocuments = new ArrayList<ContentValues>();
        if(pictures != null )
        {
            // Images
            Iterator<Image> i = pictures.iterator();
            Integer item = 1;
            while( i.hasNext() ){
                Image image = i.next();

                // Add document
                JSONObject document = new JSONObject();
                document.put("type", "IMAGE");
                document.put("number", item);
                document.put("controled", image.isControled());
                document.put("creation_date", image.getCreation());
                document.put("file", image.getAlbum() + "_" + image.getName());
                documents.put(document);

                // Prepare insert document
                ContentValues documentvalues = new ContentValues();
                documentvalues.put(DataBaseManagerDocument.DocumentContract.KEY_WORK, step.getId_work());
                documentvalues.put(DataBaseManagerDocument.DocumentContract.KEY_WORKSTEP, step.getId());
                documentvalues.put(DataBaseManagerDocument.DocumentContract.KEY_TYPE, "IMAGE");
                documentvalues.put(DataBaseManagerDocument.DocumentContract.KEY_NAME, image.getAlbum() + "_" + image.getName());
                documentvalues.put(DataBaseManagerDocument.DocumentContract.KEY_CONTENT, image.getPath());
                documentvalues.put(DataBaseManagerDocument.DocumentContract.KEY_SYNC, false);
                cvdocuments.add(documentvalues);

                // Increment item
                item++;
            }
        }
        values.put(DataBaseManagerNotification.NotificationContract.KEY_DOCUMENTS, documents.toString());
        values.put(DataBaseManagerNotification.NotificationContract.KEY_PROCESSING, false);
        Long id_report = super.getDb().insertOrThrow(DataBaseManagerNotification.NotificationContract.TABLE, null, values);

        // Insert documents
        Iterator<ContentValues> it = cvdocuments.iterator();
        while(it.hasNext()) {
            ContentValues dvalues = it.next();
            dvalues.put(DataBaseManagerDocument.DocumentContract.KEY_REPORT, id_report);

            super.getDb().insertOrThrow(DataBaseManagerDocument.DocumentContract.TABLE, null, dvalues);
        }
    }

    public Job getJob(int id) {
        Job job = null;

        Cursor cursor = super.getDb().rawQuery("SELECT "
                + JobContract.KEY_ID + ", " + JobContract.KEY_DOCUMENT + ", " + JobContract.KEY_WORKTYPENAME + ", "
                + JobContract.KEY_NOTES + ", " + JobContract.KEY_FORMATDATE + ", " + JobContract.KEY_DETAILS + ", "
                + JobContract.KEY_STATE + ", " + JobContract.KEY_STEP + ", " + JobContract.KEY_ABSTRACT + ", "
                + " COALESCE(" + JobContract.KEY_AMOUNTSTEPS + ",0), COALESCE(" + JobContract.KEY_CURRENTSTEP + ",0), "
                + "(CASE WHEN ( SELECT COALESCE( " + DataBaseManagerNotification.NotificationContract.TABLE + "." + DataBaseManagerNotification.NotificationContract.KEY_ID + ", 0)"
                + " FROM " + DataBaseManagerNotification.NotificationContract.TABLE
                + " WHERE " + DataBaseManagerNotification.NotificationContract.TABLE + "." + DataBaseManagerNotification.NotificationContract.KEY_WORK + " = " + JobContract.TABLE + "." + JobContract.KEY_ID
                + " LIMIT 1) != 0 THEN 1 ELSE 0 END) AS pending_sync, "
                + JobContract.KEY_NEXTSTEP
                + " FROM " + JobContract.TABLE + " WHERE " + JobContract.KEY_ID + "=" + id, null);

        if(cursor.getCount() > 0) {
            cursor.moveToFirst();

            job = new Job(cursor.getInt(0));
            job.setDocument(cursor.getString(1));
            job.setWorktypename(cursor.getString(2));
            job.setNotes(cursor.getString(3));
            job.setFormatdate(cursor.getString(4));
            job.setDetails(cursor.getString(5));
            job.setState(cursor.getString(6));
            job.setStep(cursor.getString(7));
            job.setAbstrac(cursor.getString(8));
            job.setAmountsteps(cursor.getInt(9));
            job.setCurrentstep(cursor.getInt(10));
            job.setPendingsync(cursor.getInt(11) > 0);
            job.setNextstep(cursor.getInt(12));
        }
        return job;
    }

    public List<Job> getJobsList(String state) {
        List<Job> list = new ArrayList<>();

        Cursor cursor = super.getDb().rawQuery("SELECT " + JobContract.KEY_ID + ", " + JobContract.KEY_DOCUMENT + ", " + JobContract.KEY_ABSTRACT + ", "
                + JobContract.KEY_WORKTYPENAME + ", " + JobContract.KEY_FORMATDATE + ", COALESCE(" + JobContract.KEY_AMOUNTSTEPS + ",0), COALESCE(" + JobContract.KEY_CURRENTSTEP + ",0), "
                + " (CASE WHEN ( SELECT COALESCE( " + DataBaseManagerNotification.NotificationContract.TABLE + "." + DataBaseManagerNotification.NotificationContract.KEY_ID + ", 0)"
                + " FROM " + DataBaseManagerNotification.NotificationContract.TABLE
                + " WHERE " + DataBaseManagerNotification.NotificationContract.TABLE + "." + DataBaseManagerNotification.NotificationContract.KEY_WORK + " = " + JobContract.TABLE + "." + JobContract.KEY_ID
                + " LIMIT 1) != 0 THEN 1 ELSE 0 END) AS pending_sync"
                + " FROM " + JobContract.TABLE
                + " WHERE " + JobContract.KEY_STATE + " = '" + state + "'"
                + " AND " + JobContract.KEY_ID_USER + " = " + mSession.getPartner()
                + " ORDER BY " + JobContract.KEY_ID + " DESC ", null);

        while (cursor.moveToNext()){
            Job job = new Job(cursor.getInt(0));
            job.setDocument(cursor.getString(1));
            job.setAbstrac(cursor.getString(2));
            job.setWorktypename(cursor.getString(3));
            job.setFormatdate(cursor.getString(4));
            job.setAmountsteps(cursor.getInt(5));
            job.setCurrentstep(cursor.getInt(6));
            job.setPendingsync(cursor.getInt(7) > 0);
            list.add(job);
        }

        return list;
    }

    public List<Job> getFilterJobsList(String state, String text) {
        List<Job> list = new ArrayList<>();

        Cursor cursor = super.getDb().rawQuery("SELECT "
                + JobContract.KEY_ID + ", " + JobContract.KEY_DOCUMENT + ", " + JobContract.KEY_ABSTRACT + ", "
                + JobContract.KEY_WORKTYPENAME + ", " + JobContract.KEY_FORMATDATE + ", "
                + " COALESCE(" + JobContract.KEY_AMOUNTSTEPS + ",0), COALESCE(" + JobContract.KEY_CURRENTSTEP + ",0), "
                + " (CASE WHEN ( SELECT COALESCE( " + DataBaseManagerNotification.NotificationContract.TABLE + "." + DataBaseManagerNotification.NotificationContract.KEY_ID + ", 0)"
                + " FROM " + DataBaseManagerNotification.NotificationContract.TABLE
                + " WHERE " + DataBaseManagerNotification.NotificationContract.TABLE + "." + DataBaseManagerNotification.NotificationContract.KEY_WORK + " = " + JobContract.TABLE + "." + JobContract.KEY_ID
                + " LIMIT 1) != 0 THEN 1 ELSE 0 END) AS pending_sync "
                + " FROM " + JobContract.TABLE
                + " WHERE " + JobContract.KEY_STATE + " = '" + state + "'"
                + " AND ( " + JobContract.KEY_DETAILS + " LIKE '%" + text + "%' OR " + JobContract.KEY_ABSTRACT + " LIKE '%" + text + "%')"
                + " AND " + JobContract.KEY_ID_USER + " = " + mSession.getPartner()
                + " ORDER BY " + JobContract.KEY_ID + " DESC" , null);

        while (cursor.moveToNext()){
            Job job = new Job(cursor.getInt(0));
            job.setDocument(cursor.getString(1));
            job.setAbstrac(cursor.getString(2));
            job.setWorktypename(cursor.getString(3));
            job.setFormatdate(cursor.getString(4));
            job.setAmountsteps(cursor.getInt(5));
            job.setCurrentstep(cursor.getInt(6));
            job.setPendingsync(cursor.getInt(7) > 0);
            list.add(job);
        }

        return list;
    }

    public Boolean exist(int id) {
        Job job = this.getJob(id);
        if(job == null) {
            return false;
        }
        return true;
    }

    public void parseExistJobs(JSONArray works) throws JSONException, NullPointerException {
        for (int i = 0; i < works.length(); i++) {
            JSONObject jsonObject = (JSONObject) works.get(i);

            this.parseJob(jsonObject, false);

            // Set current step
            Step nextstep = this.nextStep(jsonObject.getInt("id"));
            if(nextstep == null) {
                throw new NullPointerException(this.context.getResources().getString(R.string.on_failure_nextstep, jsonObject.getString("id")));
            }
            this.setNextStep(jsonObject.getInt("id"), nextstep.getId());

            // Set progress
            this.resetProgress(jsonObject.getInt("id"));
        }
    }

    public void parseJobs(JSONArray works) throws JSONException {
        for (int i = 0; i < works.length(); i++) {
            JSONObject jsonObject = (JSONObject) works.get(i);

            this.parseJob(jsonObject, true);
        }
    }

    public void parseJob(JSONObject jsonObject, Boolean notify) throws JSONException {
        // Validate Job DB
        if (!this.exist(jsonObject.getInt("id"))) {
            Job job = new Job(jsonObject.getInt("id"));
            job.setId_user(mSession.getPartner());
            job.setDocument(jsonObject.getString(JobContract.KEY_DOCUMENT));
            job.setNotes(jsonObject.getString(JobContract.KEY_NOTES));
            job.setWorktypename(jsonObject.getString(JobContract.KEY_WORKTYPENAME));
            job.setState(jsonObject.getString(JobContract.KEY_STATE));
            job.setStep(jsonObject.getString(JobContract.KEY_STEP));
            job.setCreated(jsonObject.getString(JobContract.KEY_CREATED));
            job.setFormatdate(jsonObject.getString(JobContract.KEY_FORMATDATE));
            job.setAbstrac(jsonObject.getString(JobContract.KEY_ABSTRACT));
            job.setDetails(jsonObject.getString(JobContract.KEY_DETAILS));

            // Get steps
            JSONArray steps = jsonObject.getJSONArray(DataBaseManagerJob.JobContract.KEY_WORKSSTEPS);
            job.setAmountsteps(steps.length());
            job.setCurrentstep(0);

            // Find next step
            for (int s = 0; s < steps.length(); s++) {
                JSONObject jsonStep = (JSONObject) steps.get(s);
                if(jsonStep.getInt(DataBaseManagerStep.StepContract.KEY_INDEX) == 1){
                    job.setNextstep(jsonStep.getInt("id"));
                }
            }

            // Store job
            this.store(job);

            // Get Phases
            JSONArray phases = jsonObject.getJSONArray(DataBaseManagerJob.JobContract.KEY_WORKSPHASES);
            for (int r = 0; r < phases.length(); r++) {
                JSONObject jsonPhase = (JSONObject) phases.get(r);

                Phase phase = new Phase(jsonPhase.getInt("id"));
                phase.setId_phase(jsonPhase.getInt(DatabaseManagerPhase.PhaseContract.KEY_ID_PHASE));
                phase.setId_work(job.getId());
                phase.setStep(jsonPhase.getInt(DatabaseManagerPhase.PhaseContract.KEY_ID_STEP));
                phase.setName(jsonPhase.getString(DatabaseManagerPhase.PhaseContract.KEY_NAME));
                phase.setUnsorted(jsonPhase.getBoolean(DatabaseManagerPhase.PhaseContract.KEY_UNSORTED));
                phase.setBegindate(jsonPhase.getString(DatabaseManagerPhase.PhaseContract.KEY_BEGINDATE));

                this.storePhase(phase);
            }

            // Get Discard types
            JSONArray discardtypes = jsonObject.getJSONArray(DataBaseManagerJob.JobContract.KEY_DISCARDSTYPES);
            for (int r = 0; r < discardtypes.length(); r++) {
                JSONObject jsonDiscard = (JSONObject) discardtypes.get(r);

                Discard discard = new Discard(jsonDiscard.getInt("id"));
                discard.setId_work(job.getId());
                discard.setName(jsonDiscard.getString(DataBaseManagerDiscardType.DiscardContract.KEY_NAME));

                this.storeDiscard(discard);
            }

            // Store steps
            for (int s = 0; s < steps.length(); s++) {
                JSONObject jsonStep = (JSONObject) steps.get(s);

                Step step = new Step(jsonStep.getInt("id"));
                step.setId_work(jsonStep.getInt(DataBaseManagerStep.StepContract.KEY_ID_WORK));
                step.setTitle(jsonStep.getString(DataBaseManagerStep.StepContract.KEY_TITLE));
                step.setMessage(jsonStep.getString(DataBaseManagerStep.StepContract.KEY_MESSAGE));
                step.setReport(jsonStep.getInt(DataBaseManagerStep.StepContract.KEY_REPORT));
                step.setSequence(jsonStep.getInt(DataBaseManagerStep.StepContract.KEY_SEQUENCE));
                step.setPaused(jsonStep.getBoolean(DataBaseManagerStep.StepContract.KEY_PAUSED));
                step.setPausetime(jsonStep.getInt(DataBaseManagerStep.StepContract.KEY_PAUSETIME));
                step.setDate(jsonStep.getString(DataBaseManagerStep.StepContract.KEY_DATE));
                step.setIgnored(jsonStep.getBoolean(DataBaseManagerStep.StepContract.KEY_IGNORED));
                step.setReceiveremail(jsonStep.getString(DataBaseManagerStep.StepContract.KEY_RECEIVEREMAIL));
                step.setExpecteddate(jsonStep.getString(DataBaseManagerStep.StepContract.KEY_EXPECTEDDATE));
                step.setIgnorable(jsonStep.getBoolean(DataBaseManagerStep.StepContract.KEY_IGNORABLE));
                step.setPausable(jsonStep.getBoolean(DataBaseManagerStep.StepContract.KEY_PAUSABLE));
                step.setId_step(jsonStep.getInt(DataBaseManagerStep.StepContract.KEY_ID_STEP));
                step.setPhotos(jsonStep.getBoolean(DataBaseManagerStep.StepContract.KEY_PHOTOS));
                step.setSendemail(jsonStep.getBoolean(DataBaseManagerStep.StepContract.KEY_SENDEMAIL));
                step.setIndex(jsonStep.getInt(DataBaseManagerStep.StepContract.KEY_INDEX));
                step.setId_phase(jsonStep.getInt(DataBaseManagerStep.StepContract.KEY_ID_PHASE));
                step.setPhase_text(jsonStep.getString(DataBaseManagerStep.StepContract.KEY_PHASE_TEXT));
                step.setStep_name(jsonStep.getString(DataBaseManagerStep.StepContract.KEY_STEP_NAME));
                step.setTextarea(jsonStep.getBoolean(DataBaseManagerStep.StepContract.KEY_TEXTAREA));
                step.setTextareamandatory(jsonStep.getBoolean(DataBaseManagerStep.StepContract.KEY_TEXTAREAMANDATORY));
                step.setPhotosmandatory(jsonStep.getBoolean(DataBaseManagerStep.StepContract.KEY_PHOTOSMANDATORY));

                // Store step
                this.storeStep(step);

                JSONArray reportstype = jsonStep.getJSONArray(DataBaseManagerJob.JobContract.KEY_REPORTSTYPE);
                for (int r = 0; r < reportstype.length(); r++) {
                    JSONObject jsonReport = (JSONObject) reportstype.get(r);

                    Report report = new Report(jsonReport.getInt("id"));
                    report.setId_work(job.getId());
                    report.setId_step(step.getId());
                    report.setName(jsonReport.getString(DataBaseManagerReportType.ReportContract.KEY_NAME));

                    // Store step
                    this.storeReport(report);
                }

                // Get fields
                JSONArray fields = jsonStep.getJSONArray(DataBaseManagerStep.StepContract.KEY_FIELDS);
                for (int f = 0; f < fields.length(); f++) {
                    JSONObject jsonField = (JSONObject) fields.get(f);

                    Field field = new Field(
                        jsonField.getInt(DataBaseManagerField.FieldContract.KEY_ID), job.getId(), step.getId()
                    );
                    field.setTitle(jsonField.getString(DataBaseManagerField.FieldContract.KEY_TITLE));
                    field.setType(jsonField.getString(DataBaseManagerField.FieldContract.KEY_TYPE));
                    field.setMandatory(jsonField.getBoolean(DataBaseManagerField.FieldContract.KEY_MANDATORY));
                    field.setDomain(jsonField.getString(DataBaseManagerField.FieldContract.KEY_DOMAIN));
                    field.set_default(jsonField.getString(DataBaseManagerField.FieldContract.KEY_DEFAULT));
                    field.setOrden(jsonField.getInt(DataBaseManagerField.FieldContract.KEY_ORDEN));

                    this.storeField(field);
                }
            }

            // Sync received
            if(notify) {
                this.setReceived(job);
            }
        }
    }

    public Step getStep(int id) {
        Step step = null;

        Cursor cursor = super.getDb().rawQuery("SELECT "
                + DataBaseManagerStep.StepContract.KEY_ID + ", "
                + DataBaseManagerStep.StepContract.TABLE + "." + DataBaseManagerStep.StepContract.KEY_ID_WORK + ", "
                + DataBaseManagerStep.StepContract.KEY_ID_STEP + ", "
                + DataBaseManagerStep.StepContract.TABLE + "." + DataBaseManagerStep.StepContract.KEY_ID_PHASE + ", "
                + DataBaseManagerStep.StepContract.KEY_TITLE + ", " + DataBaseManagerStep.StepContract.KEY_MESSAGE + ", "
                + DataBaseManagerStep.StepContract.KEY_TEXTAREA + ", " + DataBaseManagerStep.StepContract.KEY_TEXTAREAMANDATORY + ", "
                + DataBaseManagerStep.StepContract.KEY_PAUSABLE + ", " + DataBaseManagerStep.StepContract.KEY_IGNORABLE + ", "
                + DataBaseManagerStep.StepContract.KEY_INDEXA + ", " + DataBaseManagerStep.StepContract.KEY_PHOTOSMANDATORY + ", "
                + DataBaseManagerStep.StepContract.KEY_PAUSED + ", " + DataBaseManagerStep.StepContract.KEY_PAUSETIME + ", "
                + " COALESCE(" + DatabaseManagerPhase.PhaseContract.KEY_UNSORTED + ",0)"
                + " FROM " + DataBaseManagerStep.StepContract.TABLE
                + " INNER JOIN " + DatabaseManagerPhase.PhaseContract.TABLE + " ON "
                + DataBaseManagerStep.StepContract.TABLE + "." + DataBaseManagerStep.StepContract.KEY_ID_PHASE + " = " + DatabaseManagerPhase.PhaseContract.TABLE + "." + DatabaseManagerPhase.PhaseContract.KEY_ID_PHASE
                + " AND " + DataBaseManagerStep.StepContract.TABLE + "." + DataBaseManagerStep.StepContract.KEY_ID_WORK
                + " = " + DatabaseManagerPhase.PhaseContract.TABLE + "." + DatabaseManagerPhase.PhaseContract.KEY_ID_WORK
                + " WHERE " + DataBaseManagerStep.StepContract.KEY_ID + "=" + id, null);

        if(cursor.getCount() > 0) {
            cursor.moveToFirst();

            step = new Step(cursor.getInt(0));
            step.setId_work(cursor.getInt(1));
            step.setId_step(cursor.getInt(2));
            step.setId_phase(cursor.getInt(3));
            step.setTitle(cursor.getString(4));
            step.setMessage(cursor.getString(5));
            step.setTextarea(cursor.getInt(6) > 0);
            step.setTextareamandatory(cursor.getInt(7) > 0);
            step.setPausable(cursor.getInt(8) > 0);
            step.setIgnorable(cursor.getInt(9) > 0);
            step.setIndex(cursor.getInt(10));
            step.setPhotosmandatory(cursor.getInt(11) > 0);
            step.setPaused(cursor.getInt(12) > 0);
            step.setPausetime(cursor.getInt(13));
            step.setUnsorted(cursor.getInt(14) > 0);
        }
        return step;
    }

    public List<Step> getStepList(Job job) {
        List<Step> list = new ArrayList<>();

        Cursor cursor = super.getDb().rawQuery("SELECT " + DataBaseManagerStep.StepContract.KEY_ID + ", " + DataBaseManagerStep.StepContract.KEY_TITLE + ", "
                + DataBaseManagerStep.StepContract.KEY_DATE + ", " + DataBaseManagerStep.StepContract.KEY_IGNORED + ", "
                + "(CASE WHEN ( SELECT COALESCE( " + DataBaseManagerNotification.NotificationContract.TABLE + "." + DataBaseManagerNotification.NotificationContract.KEY_ID + ", 0)"
                + " FROM " + DataBaseManagerNotification.NotificationContract.TABLE
                + " WHERE " + DataBaseManagerNotification.NotificationContract.TABLE + "." + DataBaseManagerNotification.NotificationContract.KEY_WORK + " = " + DataBaseManagerStep.StepContract.TABLE + "." + DataBaseManagerStep.StepContract.KEY_ID_WORK
                + " AND " + DataBaseManagerNotification.NotificationContract.TABLE + "." + DataBaseManagerNotification.NotificationContract.KEY_WORKSTEP + " = " + DataBaseManagerStep.StepContract.TABLE + "." + DataBaseManagerStep.StepContract.KEY_ID
                + " LIMIT 1) != 0 THEN 1 ELSE 0 END) AS pending_sync, " + DataBaseManagerStep.StepContract.KEY_ID_WORK
                + " FROM " + DataBaseManagerStep.StepContract.TABLE
                + " WHERE " + DataBaseManagerStep.StepContract.KEY_ID_WORK + "=" + job.getId()
                + " AND " + DataBaseManagerStep.StepContract.KEY_REPORT + " = 1 "
                + " ORDER BY " + DataBaseManagerStep.StepContract.KEY_DATE + " DESC ", null);

        while (cursor.moveToNext()) {
            Step step = new Step(cursor.getInt(0));
            step.setTitle(cursor.getString(1));
            step.setDate(cursor.getString(2));
            step.setIgnored(cursor.getInt(3) > 0);
            step.setPendingsync(cursor.getInt(4) > 0);
            step.setId_work(cursor.getInt(5));
            list.add(step);
        }

        return list;
    }

    public Step nextStep(Integer job) {
        Step step = null;

        Cursor cursor = super.getDb().rawQuery("SELECT "
            + DataBaseManagerStep.StepContract.KEY_ID + ", " + DataBaseManagerStep.StepContract.KEY_ID_WORK + ", "
            + DataBaseManagerStep.StepContract.KEY_ID_STEP + ", " + DataBaseManagerStep.StepContract.KEY_ID_PHASE + ", "
            + DataBaseManagerStep.StepContract.KEY_TITLE + ", " + DataBaseManagerStep.StepContract.KEY_MESSAGE + ", "
            + DataBaseManagerStep.StepContract.KEY_TEXTAREA + ", " + DataBaseManagerStep.StepContract.KEY_TEXTAREAMANDATORY + ", "
            + DataBaseManagerStep.StepContract.KEY_PAUSABLE + ", " + DataBaseManagerStep.StepContract.KEY_IGNORABLE + ", "
            + DataBaseManagerStep.StepContract.KEY_PAUSED + ", " + DataBaseManagerStep.StepContract.KEY_PAUSETIME
            + " FROM " + DataBaseManagerStep.StepContract.TABLE
            + " WHERE " + DataBaseManagerStep.StepContract.KEY_ID_WORK + " = " + job
            + " AND " + DataBaseManagerStep.StepContract.KEY_REPORT + " = 0 "
            + " ORDER BY " + DataBaseManagerStep.StepContract.KEY_INDEXA + " ASC", null);

        if(cursor.getCount() > 0) {
            cursor.moveToFirst();

            step = new Step(cursor.getInt(0));
            step.setId_work(cursor.getInt(1));
            step.setId_step(cursor.getInt(2));
            step.setId_phase(cursor.getInt(3));
            step.setTitle(cursor.getString(4));
            step.setMessage(cursor.getString(5));
            step.setTextarea(cursor.getInt(6) > 0);
            step.setTextareamandatory(cursor.getInt(7) > 0);
            step.setPausable(cursor.getInt(8) > 0);
            step.setIgnorable(cursor.getInt(9) > 0);
            step.setPaused(cursor.getInt(10) > 0);
            step.setPausetime(cursor.getInt(11));
        }
        return step;
    }

    public boolean exitNextStep(Job job) {
        Step step = this.nextStep(job.getId());
        if(step == null) {
            return false;
        }
        return true;
    }

    public ArrayList<Report> getReporsList(Step step) {
        ArrayList<Report> list = new ArrayList<Report>();

        Cursor cursor = super.getDb().rawQuery("SELECT "
                + DataBaseManagerReportType.ReportContract.KEY_ID + ", " + DataBaseManagerReportType.ReportContract.KEY_NAME + ", "
                + DataBaseManagerReportType.ReportContract.KEY_ID_WORK + ", " + DataBaseManagerReportType.ReportContract.KEY_ID_STEP
                + " FROM " + DataBaseManagerReportType.ReportContract.TABLE
                + " WHERE " + DataBaseManagerReportType.ReportContract.KEY_ID_WORK + " = " + step.getId_work()
                + " AND " + DataBaseManagerReportType.ReportContract.KEY_ID_STEP + " = " + step.getId()
                + " ORDER BY " + DataBaseManagerReportType.ReportContract.KEY_ID + " ASC", null);

        while (cursor.moveToNext()) {
            Report report = new Report(cursor.getInt(0));
            report.setName(cursor.getString(1));
            list.add(report);
        }

        return list;
    }

    public ArrayList<Report> getDiscardList(Job job) {
        ArrayList<Report> list = new ArrayList<Report>();

        Cursor cursor = super.getDb().rawQuery("SELECT "
                + DataBaseManagerDiscardType.DiscardContract.KEY_ID + ", " + DataBaseManagerDiscardType.DiscardContract.KEY_NAME
                + " FROM " + DataBaseManagerDiscardType.DiscardContract.TABLE
                + " WHERE " + DataBaseManagerDiscardType.DiscardContract.KEY_ID_WORK + " = " + job.getId()
                + " ORDER BY " + DataBaseManagerDiscardType.DiscardContract.KEY_ID + " ASC", null);

        while (cursor.moveToNext()) {
            Report discard = new Report(cursor.getInt(0));
            discard.setName(cursor.getString(1));
            list.add(discard);
        }

        return list;
    }

    public ArrayList<Step> getPhaseStepsList(Step step) {
        ArrayList<Step> list = new ArrayList<Step>();

        Cursor cursor = super.getDb().rawQuery("SELECT "
                + DataBaseManagerStep.StepContract.KEY_ID + ", " + DataBaseManagerStep.StepContract.KEY_TITLE
                + " FROM " + DataBaseManagerStep.StepContract.TABLE
                + " WHERE " + DataBaseManagerStep.StepContract.KEY_ID_PHASE + " = " + step.getId_phase()
                + " AND " + DataBaseManagerStep.StepContract.KEY_ID_WORK + " = " + step.getId_work()
                + " AND " + DataBaseManagerStep.StepContract.KEY_REPORT + " = 0 "
                + " AND " + DataBaseManagerStep.StepContract.KEY_ID + " != " + step.getId()
                + " ORDER BY " + DataBaseManagerStep.StepContract.KEY_INDEXA + " ASC", null);

        while (cursor.moveToNext()) {
            Step item = new Step(cursor.getInt(0));
            item.setTitle(cursor.getString(1));
            list.add(item);
        }

        return list;
    }

    public void increaseStep(int id, int nextstep) {
        super.getDb().execSQL("UPDATE " + JobContract.TABLE
                + " SET " + JobContract.KEY_CURRENTSTEP + " = ( COALESCE(" + JobContract.KEY_CURRENTSTEP + ", 0) + 1), "
                + JobContract.KEY_NEXTSTEP + " = " + nextstep
                + " WHERE " + JobContract.KEY_ID + " = " + id);
    }

    public void changeStatus(int id, String state) {
        super.getDb().execSQL("UPDATE " + JobContract.TABLE + " SET " + JobContract.KEY_STATE + " = '" + state + "'"
                + " WHERE " + JobContract.KEY_ID + " = " + id);
    }

    public void changeReport(Step step, String date) {

        super.getDb().execSQL("UPDATE " + DataBaseManagerStep.StepContract.TABLE
                + " SET " + DataBaseManagerStep.StepContract.KEY_REPORT + " = 1, "
                + DataBaseManagerStep.StepContract.KEY_DATE + " = '" + date + "' "
                + " WHERE " + DataBaseManagerStep.StepContract.KEY_ID + " = " + step.getId());
    }

    public void setPause(Step step, String date) {
        super.getDb().execSQL("UPDATE " + DataBaseManagerStep.StepContract.TABLE
                + " SET " + DataBaseManagerStep.StepContract.KEY_PAUSED + " = " + (step.getPaused() ? "0" : "1") + ", "
                + DataBaseManagerStep.StepContract.KEY_PAUSETIME + " = '" + date + "' "
                + " WHERE " + DataBaseManagerStep.StepContract.KEY_ID + " = " + step.getId());
    }

    public void setIgnore(Step step, String date) {
        super.getDb().execSQL("UPDATE " + DataBaseManagerStep.StepContract.TABLE
                + " SET " + DataBaseManagerStep.StepContract.KEY_IGNORED + " = 1, "
                + DataBaseManagerStep.StepContract.KEY_REPORT + " = 1, "
                + DataBaseManagerStep.StepContract.KEY_DATE + " = '" + date + "' "
                + " WHERE " + DataBaseManagerStep.StepContract.KEY_ID + " = " + step.getId());
    }

    public void setNextStep(int job, int id) {
        super.getDb().execSQL("UPDATE " + JobContract.TABLE + " SET " + JobContract.KEY_NEXTSTEP + " = " + id
                + " WHERE " + JobContract.KEY_ID + " = " + job);
    }

    public void setReceived(Job job) throws JSONException {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = dateFormat.format(new Date());

        // Set report
        this.changeStatus(job.getId(), "NOTIFICADA");

        // First step
        Step step = this.nextStep(job.getId());
        if(step != null && step instanceof Step) {
            // Sync received
            this.storeNotification(job, step, mSession.getPartner(), false, date, "NOTIFICADA", false, false, null, null, null, null, false, false, null, null, 0, null, null, null);
        }
    }

    public void resetProgress(int job) {
        // Get progress
        Cursor cursor = super.getDb().rawQuery("SELECT COUNT( " + DataBaseManagerStep.StepContract.KEY_ID + " )"
                + " FROM " + DataBaseManagerStep.StepContract.TABLE
                + " WHERE " + DataBaseManagerStep.StepContract.KEY_ID_WORK + " = " + job
                + " AND " + DataBaseManagerStep.StepContract.KEY_REPORT + " = 1 ", null);
        cursor.moveToFirst();
        int currentstep = cursor.getInt(0);

        // Set Progress
        super.getDb().execSQL("UPDATE " + JobContract.TABLE
                + " SET " + JobContract.KEY_CURRENTSTEP + " = " + currentstep
                + " WHERE " + JobContract.KEY_ID + " = " + job);
    }

    public Notification getNextNotification() {
        Notification notification = null;
        String [] from = new String[] {
            DataBaseManagerNotification.NotificationContract.KEY_ID, DataBaseManagerNotification.NotificationContract.KEY_USER,
            DataBaseManagerNotification.NotificationContract.KEY_WORK, DataBaseManagerNotification.NotificationContract.KEY_WORKSTEP,
            DataBaseManagerNotification.NotificationContract.KEY_MODIFY_STEP, DataBaseManagerNotification.NotificationContract.KEY_ID_WORKPHASE,
            DataBaseManagerNotification.NotificationContract.KEY_MODIFY_PHASE, DataBaseManagerNotification.NotificationContract.KEY_SEQUENCE,
            DataBaseManagerNotification.NotificationContract.KEY_REPORT_DATE, DataBaseManagerNotification.NotificationContract.KEY_STATE,
            DataBaseManagerNotification.NotificationContract.KEY_STATEBEFORE, DataBaseManagerNotification.NotificationContract.KEY_PAUSED,
            DataBaseManagerNotification.NotificationContract.KEY_UNPAUSED, DataBaseManagerNotification.NotificationContract.KEY_PAUSETIME,
            DataBaseManagerNotification.NotificationContract.KEY_LATITUDE, DataBaseManagerNotification.NotificationContract.KEY_LONGITUDE,
            DataBaseManagerNotification.NotificationContract.KEY_MESSAGE, DataBaseManagerNotification.NotificationContract.KEY_TITTLE,
            DataBaseManagerNotification.NotificationContract.KEY_MESSAGE_WROTE, DataBaseManagerNotification.NotificationContract.KEY_IGNORE,
            DataBaseManagerNotification.NotificationContract.KEY_CREATE_ACTION, DataBaseManagerNotification.NotificationContract.KEY_ACTION,
            DataBaseManagerNotification.NotificationContract.KEY_MESSAGE_ACTION, DataBaseManagerNotification.NotificationContract.KEY_PICTURES,
            DataBaseManagerNotification.NotificationContract.KEY_VIDEOS, DataBaseManagerNotification.NotificationContract.KEY_REPORT_TYPE,
            DataBaseManagerNotification.NotificationContract.KEY_DOCUMENTS, DataBaseManagerNotification.NotificationContract.KEY_PROCESSING,
            DataBaseManagerNotification.NotificationContract.KEY_ID_STEP_NEW, DataBaseManagerNotification.NotificationContract.KEY_FIELDS
        };

        Cursor cursor = super.getDb().query(DataBaseManagerNotification.NotificationContract.TABLE, from,
                null, null, null, null, DataBaseManagerNotification.NotificationContract.KEY_ID + " ASC" );

        if(cursor.getCount() > 0) {
            cursor.moveToFirst();

            notification = new Notification(cursor.getInt(0));
            notification.setId_user(cursor.getInt(1));
            notification.setId_work(cursor.getInt(2));
            notification.setId_workstep(cursor.getInt(3));
            notification.setModify_step(cursor.getInt(4) > 0);
            notification.setId_workphase(cursor.getInt(5));
            notification.setModify_phase(cursor.getInt(6) > 0);
            notification.setSequence(cursor.getInt(7));
            notification.setReport_date(cursor.getString(8));
            notification.setState(cursor.getString(9));
            notification.setStatebefore(cursor.getString(10));
            notification.setPaused(cursor.getInt(11) > 0);
            notification.setUnpaused(cursor.getInt(12) > 0);
            notification.setPausetime(cursor.getString(13));
            notification.setLatitude(cursor.getString(14));
            notification.setLongitude(cursor.getString(15));
            notification.setMessage(cursor.getString(16));
            notification.setTittle(cursor.getString(17));
            notification.setMessage_wrote(cursor.getString(18));
            notification.setIgnored(cursor.getInt(19) > 0);
            notification.setCreate_action(cursor.getInt(20) > 0);
            notification.setAction(cursor.getString(21));
            notification.setMessage_action(cursor.getString(22));
            notification.setPictures(cursor.getInt(23));
            notification.setVideos(cursor.getInt(24));
            notification.setReport_type(cursor.getInt(25));
            notification.setDocuments(cursor.getString(26));
            notification.setProcessing(cursor.getInt(27) > 0);
            notification.setId_step_new(cursor.getInt(28));
            notification.setFields(cursor.getString(29));
        }

        return notification;
    }

    public Document getNextDocument() {
        Document document = null;

        Cursor cursor = super.getDb().rawQuery("SELECT "
                + DataBaseManagerDocument.DocumentContract.TABLE + "." + DataBaseManagerDocument.DocumentContract.KEY_ID + ", "
                + DataBaseManagerDocument.DocumentContract.TABLE + "." + DataBaseManagerDocument.DocumentContract.KEY_WORK + ", "
                + DataBaseManagerDocument.DocumentContract.TABLE + "." + DataBaseManagerDocument.DocumentContract.KEY_WORKSTEP + ", "
                + DataBaseManagerDocument.DocumentContract.KEY_REPORT + ", "
                + DataBaseManagerDocument.DocumentContract.TABLE + "." + DataBaseManagerDocument.DocumentContract.KEY_NAME + ", "
                + DataBaseManagerDocument.DocumentContract.KEY_TYPE + ", "
                + DataBaseManagerDocument.DocumentContract.KEY_CONTENT + ", "
                + DataBaseManagerDocument.DocumentContract.TABLE + "." + DataBaseManagerDocument.DocumentContract.KEY_PROCESSING
                + " FROM " + DataBaseManagerDocument.DocumentContract.TABLE
                + " LEFT JOIN " + DataBaseManagerNotification.NotificationContract.TABLE + " ON "
                    + DataBaseManagerDocument.DocumentContract.KEY_REPORT + " = " + DataBaseManagerNotification.NotificationContract.TABLE + "." + DataBaseManagerNotification.NotificationContract.KEY_ID
                + " WHERE " + DataBaseManagerNotification.NotificationContract.TABLE + "." + DataBaseManagerNotification.NotificationContract.KEY_ID + " IS NULL "
                + " AND (" + DataBaseManagerDocument.DocumentContract.KEY_SYNC + " = 0 OR " + DataBaseManagerDocument.DocumentContract.KEY_SYNC + " IS NULL) "
                + " ORDER BY " + DataBaseManagerDocument.DocumentContract.KEY_REPORT + " ASC", null);

        if(cursor.getCount() > 0) {
            cursor.moveToFirst();

            document = new Document(cursor.getInt(0));
            document.setId_work(cursor.getInt(1));
            document.setId_workstep(cursor.getInt(2));
            document.setId_report(cursor.getInt(3));
            document.setName(cursor.getString(4));
            document.setType(cursor.getString(5));
            document.setContent(cursor.getString(6));
            document.setProcessing(cursor.getInt(7) > 0);
        }
        return document;
    }

    public ArrayList<Field> getFieldsStep(Step step) {
        ArrayList<Field> list = new ArrayList<Field>();

        Cursor cursor = super.getDb().rawQuery("SELECT "
                + DataBaseManagerField.FieldContract.KEY_ID + ", " + DataBaseManagerField.FieldContract.KEY_ID_WORK + ", "
                + DataBaseManagerField.FieldContract.KEY_ID_STEP + ", " + DataBaseManagerField.FieldContract.KEY_TITLE + ", "
                + DataBaseManagerField.FieldContract.KEY_TYPE + ", " + DataBaseManagerField.FieldContract.KEY_MANDATORY + ", "
                + DataBaseManagerField.FieldContract.KEY_DOMAIN + ", " + DataBaseManagerField.FieldContract.KEY_ADEFAULT + ", "
                + DataBaseManagerField.FieldContract.KEY_ORDEN
                + " FROM " + DataBaseManagerField.FieldContract.TABLE
                + " WHERE " + DataBaseManagerField.FieldContract.KEY_ID_WORK + " = " + step.getId_work()
                + " AND "     + DataBaseManagerField.FieldContract.KEY_ID_STEP + " = " + step.getId()
                + " ORDER BY " + DataBaseManagerField.FieldContract.KEY_ORDEN + " ASC", null);

        while (cursor.moveToNext()) {
            Field item = new Field(cursor.getInt(0), cursor.getInt(1), cursor.getInt(2));
            item.setTitle(cursor.getString(3));
            item.setType(cursor.getString(4));
            item.setMandatory(cursor.getInt(5) > 0);
            item.setDomain(cursor.getString(6));
            item.set_default(cursor.getString(7));
            item.setOrden(cursor.getInt(8));
            list.add(item);
        }
        return list;
    }

    public void removeNotification(Notification notification) {
        super.getDb().delete(DataBaseManagerNotification.NotificationContract.TABLE,
                DataBaseManagerNotification.NotificationContract.KEY_ID + "=" + notification.getId(), null);
    }

    public void setProcessing(Notification notification, Boolean processing) {
        super.getDb().execSQL("UPDATE " + DataBaseManagerNotification.NotificationContract.TABLE
                + " SET " + DataBaseManagerNotification.NotificationContract.KEY_PROCESSING + " = " + (processing ? " 1 " : " 0 ")
                + " WHERE " + DataBaseManagerNotification.NotificationContract.KEY_ID + "=" + notification.getId());
    }

    public void resetProcessing() {
        super.getDb().execSQL("UPDATE " + DataBaseManagerNotification.NotificationContract.TABLE
                + " SET " + DataBaseManagerNotification.NotificationContract.KEY_PROCESSING + " = 0 ");
    }

    public void resetDocumentProcessing() {
        super.getDb().execSQL("UPDATE " + DataBaseManagerDocument.DocumentContract.TABLE
                + " SET " + DataBaseManagerDocument.DocumentContract.KEY_PROCESSING + " = 0 "
                + "WHERE " + DataBaseManagerDocument.DocumentContract.KEY_SYNC + " = 0 ");
    }

    public void setDocumentProcessing(Document document, Boolean processing) {
        super.getDb().execSQL("UPDATE " + DataBaseManagerDocument.DocumentContract.TABLE
                + " SET " + DataBaseManagerDocument.DocumentContract.KEY_PROCESSING + " = " + (processing ? " 1 " : " 0 ")
                + " WHERE " + DataBaseManagerDocument.DocumentContract.KEY_ID + "=" + document.getId());
    }

    public void setDocumentSynd(Document document) {
        super.getDb().execSQL("UPDATE " + DataBaseManagerDocument.DocumentContract.TABLE
                + " SET " + DataBaseManagerDocument.DocumentContract.KEY_SYNC + " = 1, "
                + DataBaseManagerDocument.DocumentContract.KEY_PROCESSING + " = 0 "
                + " WHERE " + DataBaseManagerDocument.DocumentContract.KEY_ID + " = " + document.getId());
    }

    public void removeOldJobs(String date) {

        Cursor cursor = super.getDb().rawQuery(
            " SELECT " + JobContract.KEY_ID
            + ", DATETIME(" + JobContract.KEY_CREATED + "), " + JobContract.KEY_CREATED
            + ", " + JobContract.KEY_STATE
            + " FROM " + JobContract.TABLE
            + " WHERE " + JobContract.KEY_STATE + " IN ('FINALIZADA', 'RECHAZADA', 'DESCARTADA') "
            + " AND DATETIME(" + JobContract.KEY_CREATED + ") <= '" + date + "'"
            + " AND " + JobContract.KEY_ID_USER + " = " + mSession.getPartner()
            , null);

        if(cursor.getCount() > 0) {
            while (cursor.moveToNext())
            {
                // System.out.println(" ID -> " + cursor.getInt(0) + " DATE -> " + cursor.getString(1) + " ORI -> " + cursor.getString(2) + " STA -> " + cursor.getString(3));
                super.getDb().delete(DataBaseManagerDiscardType.DiscardContract.TABLE,
                        DataBaseManagerDiscardType.DiscardContract.KEY_ID_WORK + "=" + cursor.getInt(0), null);

                super.getDb().delete(DataBaseManagerReportType.ReportContract.TABLE,
                        DataBaseManagerReportType.ReportContract.KEY_ID_WORK + "=" + cursor.getInt(0), null);

                super.getDb().delete(DatabaseManagerPhase.PhaseContract.TABLE,
                        DatabaseManagerPhase.PhaseContract.KEY_ID_WORK + "=" + cursor.getInt(0), null);

                super.getDb().delete(DataBaseManagerStep.StepContract.TABLE,
                        DataBaseManagerStep.StepContract.KEY_ID_WORK + "=" + cursor.getInt(0), null);

                super.getDb().delete(JobContract.TABLE,
                        JobContract.KEY_ID + "=" + cursor.getInt(0), null);
            }
        }
    }

    public void forwardDocuments(Step step) {
        super.getDb().execSQL("UPDATE " + DataBaseManagerDocument.DocumentContract.TABLE
            + " SET " + DataBaseManagerDocument.DocumentContract.KEY_SYNC + " = 0 "
            + " WHERE " + DataBaseManagerDocument.DocumentContract.KEY_WORK + " = " + step.getId_work()
            + " AND " + DataBaseManagerDocument.DocumentContract.KEY_WORKSTEP + " = " + step.getId()
            + " AND " + DataBaseManagerDocument.DocumentContract.KEY_SYNC + " = 1 "
        );
    }

    public int getSyncDocuments(Step step) {
        Cursor cursor = super.getDb().rawQuery("SELECT COUNT(" + DataBaseManagerDocument.DocumentContract.KEY_ID + ")"
            + " FROM " + DataBaseManagerDocument.DocumentContract.TABLE
            + " WHERE " + DataBaseManagerDocument.DocumentContract.KEY_WORKSTEP + " = " + step.getId()
            + " AND " + DataBaseManagerDocument.DocumentContract.KEY_WORK + " = " + step.getId_work()
            + " AND " + DataBaseManagerDocument.DocumentContract.KEY_SYNC + " = 1 ", null);
        cursor.moveToFirst();
        return cursor.getInt(0);
    }

    public static interface JobContract {
        public static final String TABLE = "jobs";
        public static final String KEY_ID = "_id";
        public static final String KEY_ID_USER = "id_user";
        public static final String KEY_CREATED = "created_date";
        public static final String KEY_DOCUMENT = "document";
        public static final String KEY_NOTES = "notes";
        public static final String KEY_WORKTYPENAME = "worktypename";
        public static final String KEY_STATE = "state";
        public static final String KEY_STEP = "step";
        public static final String KEY_FORMATDATE = "formatdate";
        public static final String KEY_ABSTRACT = "abstract";
        public static final String KEY_DETAILS = "details";
        public static final String KEY_WORKSSTEPS = "workssteps";
        public static final String KEY_REPORTSTYPE = "reportstype";
        public static final String KEY_AMOUNTSTEPS = "numsteps";
        public static final String KEY_CURRENTSTEP = "currentstep";
        public static final String KEY_DISCARDSTYPES = "discardstypes";
        public static final String KEY_WORKSPHASES = "worksphases";
        public static final String KEY_NEXTSTEP = "nextstep";
    }
}