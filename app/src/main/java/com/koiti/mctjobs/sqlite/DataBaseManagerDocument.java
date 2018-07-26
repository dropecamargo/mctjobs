package com.koiti.mctjobs.sqlite;

import android.content.Context;

public class DataBaseManagerDocument extends DataBaseManager {

    public static final String CREATE_TABLE = "CREATE TABLE "
            + DocumentContract.TABLE + "("
            + DocumentContract.KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + DocumentContract.KEY_WORK + " INTEGER,"
            + DocumentContract.KEY_WORKSTEP + " INTEGER,"
            + DocumentContract.KEY_REPORT + " INTEGER,"
            + DocumentContract.KEY_NAME + " TEXT,"
            + DocumentContract.KEY_TYPE + " TEXT,"
            + DocumentContract.KEY_CONTENT + " TEXT, "
            + DocumentContract.KEY_SYNC + " BOOLEAN, "
            + DocumentContract.KEY_PROCESSING + " BOOLEAN )";

    public DataBaseManagerDocument(Context context) {
        super(context);
    }

    public static interface DocumentContract {
        public static final String TABLE = "documents";
        public static final String KEY_ID = "_id";
        public static final String KEY_WORK = "id_work";
        public static final String KEY_WORKSTEP = "id_workstep";
        public static final String KEY_REPORT = "id_report";
        public static final String KEY_NAME = "name";
        public static final String KEY_TYPE = "type";
        public static final String KEY_CONTENT = "content";
        public static final String KEY_SYNC = "sync";
        public static final String KEY_PROCESSING = "processing";
    }
}