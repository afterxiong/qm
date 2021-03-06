package com.shareshow.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;

import org.greenrobot.greendao.AbstractDaoMaster;
import org.greenrobot.greendao.database.StandardDatabase;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseOpenHelper;
import org.greenrobot.greendao.identityscope.IdentityScopeType;


// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/**
 * Master of DAO (schema version 24): knows all DAOs.
 */
public class DaoMaster extends AbstractDaoMaster {
    public static final int SCHEMA_VERSION = 24;

    /** Creates underlying database table using DAOs. */
    public static void createAllTables(Database db, boolean ifNotExists) {
        AmplyNotifyDao.createTable(db, ifNotExists);
        OrgAndDeptDao.createTable(db, ifNotExists);
        StudyMaterialsVisitingMaterialsDao.createTable(db, ifNotExists);
        TeamMorningDataDao.createTable(db, ifNotExists);
        VisitDataDao.createTable(db, ifNotExists);
        AdFileDao.createTable(db, ifNotExists);
        AppUseInfoDao.createTable(db, ifNotExists);
        DevAdPlayRecordDao.createTable(db, ifNotExists);
        DevAreaUseInfoDao.createTable(db, ifNotExists);
        DeviceUserDao.createTable(db, ifNotExists);
        FileCordBeanDao.createTable(db, ifNotExists);
        NotityBeanDao.createTable(db, ifNotExists);
        ReadVisitTrackIdDao.createTable(db, ifNotExists);
    }

    /** Drops underlying database table using DAOs. */
    public static void dropAllTables(Database db, boolean ifExists) {
        AmplyNotifyDao.dropTable(db, ifExists);
        OrgAndDeptDao.dropTable(db, ifExists);
        StudyMaterialsVisitingMaterialsDao.dropTable(db, ifExists);
        TeamMorningDataDao.dropTable(db, ifExists);
        VisitDataDao.dropTable(db, ifExists);
        AdFileDao.dropTable(db, ifExists);
        AppUseInfoDao.dropTable(db, ifExists);
        DevAdPlayRecordDao.dropTable(db, ifExists);
        DevAreaUseInfoDao.dropTable(db, ifExists);
        DeviceUserDao.dropTable(db, ifExists);
        FileCordBeanDao.dropTable(db, ifExists);
        NotityBeanDao.dropTable(db, ifExists);
        ReadVisitTrackIdDao.dropTable(db, ifExists);
    }

    /**
     * WARNING: Drops all table on Upgrade! Use only during development.
     * Convenience method using a {@link DevOpenHelper}.
     */
    public static DaoSession newDevSession(Context context, String name) {
        Database db = new DevOpenHelper(context, name).getWritableDb();
        DaoMaster daoMaster = new DaoMaster(db);
        return daoMaster.newSession();
    }

    public DaoMaster(SQLiteDatabase db) {
        this(new StandardDatabase(db));
    }

    public DaoMaster(Database db) {
        super(db, SCHEMA_VERSION);
        registerDaoClass(AmplyNotifyDao.class);
        registerDaoClass(OrgAndDeptDao.class);
        registerDaoClass(StudyMaterialsVisitingMaterialsDao.class);
        registerDaoClass(TeamMorningDataDao.class);
        registerDaoClass(VisitDataDao.class);
        registerDaoClass(AdFileDao.class);
        registerDaoClass(AppUseInfoDao.class);
        registerDaoClass(DevAdPlayRecordDao.class);
        registerDaoClass(DevAreaUseInfoDao.class);
        registerDaoClass(DeviceUserDao.class);
        registerDaoClass(FileCordBeanDao.class);
        registerDaoClass(NotityBeanDao.class);
        registerDaoClass(ReadVisitTrackIdDao.class);
    }

    public DaoSession newSession() {
        return new DaoSession(db, IdentityScopeType.Session, daoConfigMap);
    }

    public DaoSession newSession(IdentityScopeType type) {
        return new DaoSession(db, type, daoConfigMap);
    }

    /**
     * Calls {@link #createAllTables(Database, boolean)} in {@link #onCreate(Database)} -
     */
    public static abstract class OpenHelper extends DatabaseOpenHelper {
        public OpenHelper(Context context, String name) {
            super(context, name, SCHEMA_VERSION);
        }

        public OpenHelper(Context context, String name, CursorFactory factory) {
            super(context, name, factory, SCHEMA_VERSION);
        }

        @Override
        public void onCreate(Database db) {
            Log.i("greenDAO", "Creating tables for schema version " + SCHEMA_VERSION);
            createAllTables(db, false);
        }
    }

    /** WARNING: Drops all table on Upgrade! Use only during development. */
    public static class DevOpenHelper extends OpenHelper {
        public DevOpenHelper(Context context, String name) {
            super(context, name);
        }

        public DevOpenHelper(Context context, String name, CursorFactory factory) {
            super(context, name, factory);
        }

        @Override
        public void onUpgrade(Database db, int oldVersion, int newVersion) {
            Log.i("greenDAO", "Upgrading schema from version " + oldVersion + " to " + newVersion + " by dropping all tables");
            dropAllTables(db, true);
            onCreate(db);
        }
    }

}
