package com.shareshow.db;

import android.database.sqlite.SQLiteDatabase;

import com.shareshow.App;

/**
 * Created by xiongchengguang on 2017/12/16.
 */

public class GreenDaoManager {

    private static final String database_name = "share_show_db";

    private static DaoSession daoSession;
    private static DaoMaster.DevOpenHelper devOpenHelper = null;
    public static void initGreenDao(){
        if (devOpenHelper == null) {
            devOpenHelper = new DaoMaster.DevOpenHelper(App.getApp(), "share_show_db", null);
        }
        SQLiteDatabase database = devOpenHelper.getWritableDatabase();
        DaoMaster master = new DaoMaster(database);
        daoSession = master.newSession();
    }

    public static void deleteSQL(){
        SQLiteDatabase database = devOpenHelper.getWritableDatabase();
        DaoMaster master = new DaoMaster(database);
        DaoMaster.dropAllTables(master.getDatabase(),true);
        DaoMaster.createAllTables(master.getDatabase(),true);
    }


    public static DaoSession getDaoSession() {
        if (daoSession == null) {
            initGreenDao();
        }
        return daoSession;
    }
}
