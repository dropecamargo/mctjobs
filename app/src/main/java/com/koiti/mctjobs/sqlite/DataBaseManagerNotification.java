package com.koiti.mctjobs.sqlite;

import android.content.ContentValues;
import android.content.Context;

import com.koiti.mctjobs.models.Notification;

public class DataBaseManagerNotification extends DataBaseManager {

    public static final String CREATE_TABLE = "CREATE TABLE "
            + NotificationContract.TABLE + "("
            + NotificationContract.KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + NotificationContract.KEY_USER + " INTEGER,"
            + NotificationContract.KEY_WORK + " INTEGER,"
            + NotificationContract.KEY_WORKSTEP + " INTEGER,"
            + NotificationContract.KEY_MODIFY_STEP + " BOOLEAN,"
            + NotificationContract.KEY_ID_WORKPHASE + " INTEGER,"
            + NotificationContract.KEY_MODIFY_PHASE + " BOOLEAN,"
            + NotificationContract.KEY_SEQUENCE + " INTEGER,"
            + NotificationContract.KEY_REPORT_DATE + " TEXT,"
            + NotificationContract.KEY_STATE + " TEXT,"
            + NotificationContract.KEY_STATEBEFORE + " TEXT,"
            + NotificationContract.KEY_PAUSED + " BOOLEAN,"
            + NotificationContract.KEY_UNPAUSED + " BOOLEAN,"
            + NotificationContract.KEY_PAUSETIME + " TEXT,"
            + NotificationContract.KEY_LATITUDE + " TEXT,"
            + NotificationContract.KEY_LONGITUDE + " TEXT,"
            + NotificationContract.KEY_MESSAGE + " TEXT,"
            + NotificationContract.KEY_TITTLE + " TEXT,"
            + NotificationContract.KEY_MESSAGE_WROTE + " TEXT,"
            + NotificationContract.KEY_IGNORE + " BOOLEAN,"
            + NotificationContract.KEY_CREATE_ACTION + " BOOLEAN,"
            + NotificationContract.KEY_ACTION + " TEXT,"
            + NotificationContract.KEY_MESSAGE_ACTION + " TEXT,"
            + NotificationContract.KEY_PICTURES + " INTEGER,"
            + NotificationContract.KEY_VIDEOS + " INTEGER,"
            + NotificationContract.KEY_REPORT_TYPE + " INTEGER,"
            + NotificationContract.KEY_DOCUMENTS + " TEXT,"
            + NotificationContract.KEY_ID_STEP_NEW + " INTEGER,"
            + NotificationContract.KEY_PROCESSING + " BOOLEAN )";

    public DataBaseManagerNotification(Context context) {
        super(context);
    }

    public static interface NotificationContract {
        public static final String TABLE = "notifications";
        public static final String KEY_ID = "_id";
        public static final String KEY_USER = "id_user";
        public static final String KEY_WORK = "id_work";
        public static final String KEY_WORKSTEP = "id_workstep";
        public static final String KEY_MODIFY_STEP = "modify_step";
        public static final String KEY_ID_WORKPHASE = "id_workphase";
        public static final String KEY_MODIFY_PHASE = "modify_phase";
        public static final String KEY_SEQUENCE = "sequence";
        public static final String KEY_REPORT_DATE = "report_date";
        public static final String KEY_STATE = "state";
        public static final String KEY_STATEBEFORE = "statebefore";
        public static final String KEY_PAUSED = "paused";
        public static final String KEY_UNPAUSED = "unpaused";
        public static final String KEY_PAUSETIME = "pausetime";
        public static final String KEY_LATITUDE = "latitude";
        public static final String KEY_LONGITUDE = "longitude";
        public static final String KEY_MESSAGE = "message";
        public static final String KEY_TITTLE = "tittle";
        public static final String KEY_MESSAGE_WROTE = "message_wrote";
        public static final String KEY_IGNORE = "ignored";
        public static final String KEY_CREATE_ACTION = "create_action";
        public static final String KEY_ACTION = "action";
        public static final String KEY_MESSAGE_ACTION = "message_action";
        public static final String KEY_PICTURES = "pictures";
        public static final String KEY_VIDEOS = "videos";
        public static final String KEY_REPORT_TYPE = "report_type";
        public static final String KEY_DOCUMENTS = "documents";
        public static final String KEY_PROCESSING = "processing";
        public static final String KEY_ID_STEP_NEW = "id_step_new";
    }
}