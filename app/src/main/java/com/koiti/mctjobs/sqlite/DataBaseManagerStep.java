package com.koiti.mctjobs.sqlite;

import android.content.Context;

public class DataBaseManagerStep extends DataBaseManager {

    public static final String CREATE_TABLE = "CREATE TABLE "
            + StepContract.TABLE + "("
            + StepContract.KEY_ID + " INTEGER PRIMARY KEY,"
            + StepContract.KEY_ID_WORK + " INTEGER " +  DatabaseHelpher.References.ID_WORK + " NOT NULL,"
            + StepContract.KEY_TITLE + " VARYING CHARACTER(255),"
            + StepContract.KEY_MESSAGE + " TEXT,"
            + StepContract.KEY_REPORT + " INTEGER,"
            + StepContract.KEY_SEQUENCE + " INTEGER,"
            + StepContract.KEY_PAUSED + " BOOLEAN,"
            + StepContract.KEY_PAUSETIME + " TEXT,"
            + StepContract.KEY_DATE + " TEXT,"
            + StepContract.KEY_IGNORED + " BOOLEAN,"
            + StepContract.KEY_RECEIVEREMAIL + " VARYING CHARACTER(255),"
            + StepContract.KEY_EXPECTEDDATE + " TEXT,"
            + StepContract.KEY_IGNORABLE + " BOOLEAN,"
            + StepContract.KEY_PAUSABLE + " BOOLEAN,"
            + StepContract.KEY_ID_STEP + " INTEGER,"
            + StepContract.KEY_PHOTOS + " BOOLEAN,"
            + StepContract.KEY_SENDEMAIL + " BOOLEAN,"
            + StepContract.KEY_INDEXA + " INTEGER,"
            + StepContract.KEY_ID_PHASE + " INTEGER,"
            + StepContract.KEY_PHASE_TEXT + " VARYING CHARACTER(255),"
            + StepContract.KEY_STEP_NAME + " VARYING CHARACTER(255),"
            + StepContract.KEY_TEXTAREA + " BOOLEAN,"
            + StepContract.KEY_TEXTAREAMANDATORY + " BOOLEAN,"
            + StepContract.KEY_PHOTOSMANDATORY + " BOOLEAN " + ")";


    public DataBaseManagerStep(Context context) {
        super(context);
    }

    public static interface StepContract {
        public static final String TABLE = "steps";
        public static final String KEY_ID = "_id";
        public static final String KEY_ID_WORK = "id_work";
        public static final String KEY_PHASE_TEXT = "phase_text";
        public static final String KEY_SEQUENCE = "sequence";
        public static final String KEY_MESSAGE = "message";
        public static final String KEY_SENDEMAIL = "sendemail";
        public static final String KEY_PAUSABLE = "pausable";
        public static final String KEY_TITLE = "tittle";
        public static final String KEY_ID_PHASE = "id_phase";
        public static final String KEY_IGNORED = "ignored";
        public static final String KEY_IGNORABLE = "ignorable";
        public static final String KEY_STEP_NAME = "step_name";
        public static final String KEY_TEXTAREAMANDATORY = "textareamandatory";
        public static final String KEY_REPORT = "report";
        public static final String KEY_RECEIVEREMAIL = "receiveremail";
        public static final String KEY_TEXTAREA = "textarea";
        public static final String KEY_DATE = "date";
        public static final String KEY_PHOTOSMANDATORY = "photosmandatory";
        public static final String KEY_EXPECTEDDATE = "expecteddate";
        public static final String KEY_PAUSETIME = "pausetime";
        public static final String KEY_PAUSED = "paused";
        public static final String KEY_ID_STEP = "id_step";
        public static final String KEY_INDEX = "index";
        public static final String KEY_INDEXA = "indexa";
        public static final String KEY_PHOTOS = "photos";
        public static final String KEY_FIELDS = "fields";
    }
}