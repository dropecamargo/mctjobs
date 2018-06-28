package com.koiti.mctjobs.sqlite;

import android.content.Context;

public class DatabaseManagerPhase extends DataBaseManager {

    public static final String CREATE_TABLE = "CREATE TABLE "
            + PhaseContract.TABLE + "("
            + PhaseContract.KEY_ID + " INTEGER PRIMARY KEY,"
            + PhaseContract.KEY_ID_PHASE + " INTEGER,"
            + PhaseContract.KEY_ID_WORK + " INTEGER " +  DatabaseHelpher.References.ID_WORK + " NOT NULL,"
            + PhaseContract.KEY_ID_STEP + " INTEGER " +  DatabaseHelpher.References.ID_STEP + " NOT NULL,"
            + PhaseContract.KEY_NAME + " TEXT,"
            + PhaseContract.KEY_BEGINDATE + " TEXT,"
            + PhaseContract.KEY_UNSORTED + " BOOLEAN " + ")";

    public DatabaseManagerPhase(Context context) {
        super(context);
    }

    public static interface PhaseContract {
        public static final String TABLE = "phases";
        public static final String KEY_ID = "id";
        public static final String KEY_ID_PHASE = "id_phase";
        public static final String KEY_ID_WORK = "id_work";
        public static final String KEY_ID_STEP = "step";
        public static final String KEY_NAME = "name";
        public static final String KEY_UNSORTED = "unsorted";
        public static final String KEY_BEGINDATE = "begindate";
    }
}
