package com.koiti.mctjobs.sqlite;

import android.content.Context;

public class DataBaseManagerReportType extends DataBaseManager {

    public static final String CREATE_TABLE = "CREATE TABLE "
            + ReportContract.TABLE + "("
            + ReportContract.KEY_ID + " INTEGER NOT NULL,"
            + ReportContract.KEY_ID_WORK + " INTEGER " +  DatabaseHelpher.References.ID_WORK + " NOT NULL,"
            + ReportContract.KEY_ID_STEP + " INTEGER " +  DatabaseHelpher.References.ID_STEP + " NOT NULL,"
            + ReportContract.KEY_NAME + " VARYING CHARACTER(255) )";

    public DataBaseManagerReportType(Context context) {
        super(context);
    }

    public static interface ReportContract {
        public static final String TABLE = "reports";
        public static final String KEY_ID = "_id";
        public static final String KEY_ID_WORK = "id_work";
        public static final String KEY_ID_STEP = "id_step";
        public static final String KEY_NAME = "name";
    }
}
