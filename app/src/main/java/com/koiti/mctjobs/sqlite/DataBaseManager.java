package com.koiti.mctjobs.sqlite;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class DataBaseManager {

    private DatabaseHelpher mHelpher;
    private SQLiteDatabase db;

    public DataBaseManager(Context context) {
        mHelpher = new DatabaseHelpher(context);
        db = mHelpher.getWritableDatabase();
    }

    public void close(){
        db.close();
    }

    public DatabaseHelpher getmHelpher() {
        return mHelpher;
    }

    public void setmHelpher(DatabaseHelpher mHelpher) {
        this.mHelpher = mHelpher;
    }

    public SQLiteDatabase getDb() {
        return db;
    }

    public void setDb(SQLiteDatabase db) {
        this.db = db;
    }
}
