package com.koiti.mctjobs.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelpher extends SQLiteOpenHelper {

    private static final String TAG = DatabaseHelpher.class.getSimpleName();
    private static final String DB_NAME = "mctjobs.db";
    private static int DB_SCHEME_VERSION = 11;

    public DatabaseHelpher(Context context) {
        super(context, DB_NAME, null, DB_SCHEME_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DataBaseManagerJob.CREATE_TABLE);
        db.execSQL(DataBaseManagerStep.CREATE_TABLE);
        db.execSQL(DataBaseManagerReportType.CREATE_TABLE);
        db.execSQL(DataBaseManagerDiscardType.CREATE_TABLE);
        db.execSQL(DataBaseManagerNotification.CREATE_TABLE);
        db.execSQL(DataBaseManagerDocument.CREATE_TABLE);
        db.execSQL(DatabaseManagerPhase.CREATE_TABLE);
        db.execSQL(DataBaseManagerField.CREATE_TABLE);

        Log.d(TAG,"Base de Datos Creada " + DatabaseHelpher.DB_NAME);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG,"Base de Datos Actualizada "+ DatabaseHelpher.DB_NAME);

        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + DataBaseManagerNotification.NotificationContract.TABLE + " ADD COLUMN "
                    + DataBaseManagerNotification.NotificationContract.KEY_DOCUMENTS + " TEXT;");

            db.execSQL("ALTER TABLE " + DataBaseManagerNotification.NotificationContract.TABLE + " ADD COLUMN "
                    + DataBaseManagerNotification.NotificationContract.KEY_PROCESSING + " BOOLEAN;");
        }

        if (oldVersion < 3) {
            db.execSQL("ALTER TABLE " + DataBaseManagerNotification.NotificationContract.TABLE + " ADD COLUMN "
                    + DataBaseManagerNotification.NotificationContract.KEY_IGNORE + " BOOLEAN;");

            db.execSQL("ALTER TABLE " + DataBaseManagerNotification.NotificationContract.TABLE + " ADD COLUMN "
                    + DataBaseManagerNotification.NotificationContract.KEY_CREATE_ACTION + " BOOLEAN;");

            db.execSQL("ALTER TABLE " + DataBaseManagerNotification.NotificationContract.TABLE + " ADD COLUMN "
                    + DataBaseManagerNotification.NotificationContract.KEY_ACTION + " TEXT;");

            db.execSQL("ALTER TABLE " + DataBaseManagerNotification.NotificationContract.TABLE + " ADD COLUMN "
                    + DataBaseManagerNotification.NotificationContract.KEY_MESSAGE_ACTION + " TEXT;");

            db.execSQL("ALTER TABLE " + DataBaseManagerNotification.NotificationContract.TABLE + " ADD COLUMN "
                    + DataBaseManagerNotification.NotificationContract.KEY_PICTURES + " INTEGER;");

            db.execSQL("ALTER TABLE " + DataBaseManagerNotification.NotificationContract.TABLE + " ADD COLUMN "
                    + DataBaseManagerNotification.NotificationContract.KEY_VIDEOS + " INTEGER;");

            db.execSQL("ALTER TABLE " + DataBaseManagerNotification.NotificationContract.TABLE + " ADD COLUMN "
                    + DataBaseManagerNotification.NotificationContract.KEY_REPORT_TYPE + " INTEGER;");
        }
        if (oldVersion < 4) {
            db.execSQL("ALTER TABLE " + DataBaseManagerJob.JobContract.TABLE + " ADD COLUMN "
                    + DataBaseManagerJob.JobContract.KEY_CURRENTSTEP + " INTEGER;");

            db.execSQL("ALTER TABLE " + DataBaseManagerJob.JobContract.TABLE + " ADD COLUMN "
                    + DataBaseManagerJob.JobContract.KEY_AMOUNTSTEPS + " INTEGER;");
        }
        if (oldVersion < 5) {
            db.execSQL(DataBaseManagerDiscardType.CREATE_TABLE);
        }
        if (oldVersion < 6) {
            db.execSQL(DataBaseManagerDocument.CREATE_TABLE);
        }
        if (oldVersion < 7) {
            db.execSQL(DatabaseManagerPhase.CREATE_TABLE);

            db.execSQL("ALTER TABLE " + DataBaseManagerJob.JobContract.TABLE + " ADD COLUMN "
                    + DataBaseManagerJob.JobContract.KEY_NEXTSTEP + " INTEGER;");

            db.execSQL("ALTER TABLE " + DataBaseManagerNotification.NotificationContract.TABLE + " ADD COLUMN "
                    + DataBaseManagerNotification.NotificationContract.KEY_ID_STEP_NEW + " INTEGER;");
        }
        if (oldVersion < 8) {
            db.execSQL("ALTER TABLE " + DataBaseManagerNotification.NotificationContract.TABLE + " ADD COLUMN "
                    + DataBaseManagerNotification.NotificationContract.KEY_FIELDS + " TEXT;");

            db.execSQL(DataBaseManagerField.CREATE_TABLE);

            db.execSQL("ALTER TABLE " + DataBaseManagerDocument.DocumentContract.TABLE + " ADD COLUMN "
                    + DataBaseManagerDocument.DocumentContract.KEY_SYNC + " BOOLEAN;");
        }
        if(oldVersion < 11) {
            db.execSQL("UPDATE " + DataBaseManagerDocument.DocumentContract.TABLE
                    + " SET " + DataBaseManagerDocument.DocumentContract.KEY_PROCESSING + " = 0 "
                    + " WHERE " + DataBaseManagerDocument.DocumentContract.KEY_SYNC + " = 1");
        }
    }

    public static  interface References {
        String ID_WORK = String.format("REFERENCES %s(%s)",  DataBaseManagerJob.JobContract.TABLE, DataBaseManagerJob.JobContract.KEY_ID);
        String ID_STEP = String.format("REFERENCES %s(%s)",  DataBaseManagerStep.StepContract.TABLE, DataBaseManagerStep.StepContract.KEY_ID);
    }
}