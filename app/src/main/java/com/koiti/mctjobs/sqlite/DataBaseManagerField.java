package com.koiti.mctjobs.sqlite;

import android.content.Context;

public class DataBaseManagerField extends DataBaseManager {

    public static final String CREATE_TABLE = "CREATE TABLE "
        + FieldContract.TABLE + "("
        + FieldContract.KEY_ID + " INTEGER NOT NULL,"
        + FieldContract.KEY_ID_WORK + " INTEGER " +  DatabaseHelpher.References.ID_WORK + " NOT NULL,"
        + FieldContract.KEY_ID_STEP + " INTEGER " +  DatabaseHelpher.References.ID_STEP + " NOT NULL,"
        + FieldContract.KEY_TITLE + " VARYING CHARACTER(255),"
        + FieldContract.KEY_TYPE + " VARYING CHARACTER(255),"
        + FieldContract.KEY_MANDATORY + " BOOLEAN,"
        + FieldContract.KEY_DOMAIN + " TEXT,"
        + FieldContract.KEY_ORDEN + " INTEGER,"
        + FieldContract.KEY_ADEFAULT + " VARYING CHARACTER(255),"
        + "PRIMARY KEY ("+ FieldContract.KEY_ID + ", " + FieldContract.KEY_ID_WORK + ", " + FieldContract.KEY_ID_STEP + ") )";


    public DataBaseManagerField(Context context) {
        super(context);
    }

    public static interface FieldContract {
        public static final String TABLE = "fields";
        public static final String KEY_ID = "id";
        public static final String KEY_ID_WORK = "id_work";
        public static final String KEY_ID_STEP = "id_step";
        public static final String KEY_TITLE = "title";
        public static final String KEY_TYPE = "type";
        public static final String KEY_MANDATORY = "mandatory";
        public static final String KEY_DOMAIN = "domain";
        public static final String KEY_DEFAULT = "default";
        public static final String KEY_ADEFAULT = "_default";
        public static final String KEY_ORDEN = "orden";
    }
}
