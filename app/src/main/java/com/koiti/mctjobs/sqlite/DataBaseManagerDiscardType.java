package com.koiti.mctjobs.sqlite;

import android.content.Context;

public class DataBaseManagerDiscardType extends DataBaseManager {

    public static final String CREATE_TABLE = "CREATE TABLE "
            + DiscardContract.TABLE + "("
            + DiscardContract.KEY_ID + " INTEGER NOT NULL,"
            + DiscardContract.KEY_ID_WORK + " INTEGER " +  DatabaseHelpher.References.ID_WORK + " NOT NULL,"
            + DiscardContract.KEY_NAME + " VARYING CHARACTER(255) )";

    public DataBaseManagerDiscardType(Context context) {
        super(context);
    }

    public static interface DiscardContract {
        public static final String TABLE = "discardtype";
        public static final String KEY_ID = "_id";
        public static final String KEY_ID_WORK = "id_work";
        public static final String KEY_NAME = "name";
    }
}
