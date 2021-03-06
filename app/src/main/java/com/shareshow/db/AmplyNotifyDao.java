package com.shareshow.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.shareshow.aide.retrofit.entity.AmplyNotify;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "AMPLY_NOTIFY".
*/
public class AmplyNotifyDao extends AbstractDao<AmplyNotify, String> {

    public static final String TABLENAME = "AMPLY_NOTIFY";

    /**
     * Properties of entity AmplyNotify.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property NosId = new Property(0, String.class, "nosId", true, "NOS_ID");
        public final static Property NosUserid = new Property(1, String.class, "nosUserid", false, "NOS_USERID");
        public final static Property NosOrgids = new Property(2, String.class, "nosOrgids", false, "NOS_ORGIDS");
        public final static Property NosTitle = new Property(3, String.class, "nosTitle", false, "NOS_TITLE");
        public final static Property NosOrgnames = new Property(4, String.class, "nosOrgnames", false, "NOS_ORGNAMES");
        public final static Property NosContent = new Property(5, String.class, "nosContent", false, "NOS_CONTENT");
        public final static Property NosFilepath = new Property(6, String.class, "nosFilepath", false, "NOS_FILEPATH");
        public final static Property NosFilename = new Property(7, String.class, "nosFilename", false, "NOS_FILENAME");
        public final static Property NosCreatetime = new Property(8, String.class, "nosCreatetime", false, "NOS_CREATETIME");
        public final static Property NosDel = new Property(9, String.class, "nosDel", false, "NOS_DEL");
        public final static Property NosTimelength = new Property(10, int.class, "nosTimelength", false, "NOS_TIMELENGTH");
        public final static Property NosEndtime = new Property(11, String.class, "nosEndtime", false, "NOS_ENDTIME");
    }


    public AmplyNotifyDao(DaoConfig config) {
        super(config);
    }
    
    public AmplyNotifyDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"AMPLY_NOTIFY\" (" + //
                "\"NOS_ID\" TEXT PRIMARY KEY NOT NULL ," + // 0: nosId
                "\"NOS_USERID\" TEXT," + // 1: nosUserid
                "\"NOS_ORGIDS\" TEXT," + // 2: nosOrgids
                "\"NOS_TITLE\" TEXT," + // 3: nosTitle
                "\"NOS_ORGNAMES\" TEXT," + // 4: nosOrgnames
                "\"NOS_CONTENT\" TEXT," + // 5: nosContent
                "\"NOS_FILEPATH\" TEXT," + // 6: nosFilepath
                "\"NOS_FILENAME\" TEXT," + // 7: nosFilename
                "\"NOS_CREATETIME\" TEXT," + // 8: nosCreatetime
                "\"NOS_DEL\" TEXT," + // 9: nosDel
                "\"NOS_TIMELENGTH\" INTEGER NOT NULL ," + // 10: nosTimelength
                "\"NOS_ENDTIME\" TEXT);"); // 11: nosEndtime
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"AMPLY_NOTIFY\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, AmplyNotify entity) {
        stmt.clearBindings();
 
        String nosId = entity.getNosId();
        if (nosId != null) {
            stmt.bindString(1, nosId);
        }
 
        String nosUserid = entity.getNosUserid();
        if (nosUserid != null) {
            stmt.bindString(2, nosUserid);
        }
 
        String nosOrgids = entity.getNosOrgids();
        if (nosOrgids != null) {
            stmt.bindString(3, nosOrgids);
        }
 
        String nosTitle = entity.getNosTitle();
        if (nosTitle != null) {
            stmt.bindString(4, nosTitle);
        }
 
        String nosOrgnames = entity.getNosOrgnames();
        if (nosOrgnames != null) {
            stmt.bindString(5, nosOrgnames);
        }
 
        String nosContent = entity.getNosContent();
        if (nosContent != null) {
            stmt.bindString(6, nosContent);
        }
 
        String nosFilepath = entity.getNosFilepath();
        if (nosFilepath != null) {
            stmt.bindString(7, nosFilepath);
        }
 
        String nosFilename = entity.getNosFilename();
        if (nosFilename != null) {
            stmt.bindString(8, nosFilename);
        }
 
        String nosCreatetime = entity.getNosCreatetime();
        if (nosCreatetime != null) {
            stmt.bindString(9, nosCreatetime);
        }
 
        String nosDel = entity.getNosDel();
        if (nosDel != null) {
            stmt.bindString(10, nosDel);
        }
        stmt.bindLong(11, entity.getNosTimelength());
 
        String nosEndtime = entity.getNosEndtime();
        if (nosEndtime != null) {
            stmt.bindString(12, nosEndtime);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, AmplyNotify entity) {
        stmt.clearBindings();
 
        String nosId = entity.getNosId();
        if (nosId != null) {
            stmt.bindString(1, nosId);
        }
 
        String nosUserid = entity.getNosUserid();
        if (nosUserid != null) {
            stmt.bindString(2, nosUserid);
        }
 
        String nosOrgids = entity.getNosOrgids();
        if (nosOrgids != null) {
            stmt.bindString(3, nosOrgids);
        }
 
        String nosTitle = entity.getNosTitle();
        if (nosTitle != null) {
            stmt.bindString(4, nosTitle);
        }
 
        String nosOrgnames = entity.getNosOrgnames();
        if (nosOrgnames != null) {
            stmt.bindString(5, nosOrgnames);
        }
 
        String nosContent = entity.getNosContent();
        if (nosContent != null) {
            stmt.bindString(6, nosContent);
        }
 
        String nosFilepath = entity.getNosFilepath();
        if (nosFilepath != null) {
            stmt.bindString(7, nosFilepath);
        }
 
        String nosFilename = entity.getNosFilename();
        if (nosFilename != null) {
            stmt.bindString(8, nosFilename);
        }
 
        String nosCreatetime = entity.getNosCreatetime();
        if (nosCreatetime != null) {
            stmt.bindString(9, nosCreatetime);
        }
 
        String nosDel = entity.getNosDel();
        if (nosDel != null) {
            stmt.bindString(10, nosDel);
        }
        stmt.bindLong(11, entity.getNosTimelength());
 
        String nosEndtime = entity.getNosEndtime();
        if (nosEndtime != null) {
            stmt.bindString(12, nosEndtime);
        }
    }

    @Override
    public String readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0);
    }    

    @Override
    public AmplyNotify readEntity(Cursor cursor, int offset) {
        AmplyNotify entity = new AmplyNotify( //
            cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0), // nosId
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // nosUserid
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // nosOrgids
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // nosTitle
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // nosOrgnames
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // nosContent
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // nosFilepath
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // nosFilename
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), // nosCreatetime
            cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9), // nosDel
            cursor.getInt(offset + 10), // nosTimelength
            cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11) // nosEndtime
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, AmplyNotify entity, int offset) {
        entity.setNosId(cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0));
        entity.setNosUserid(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setNosOrgids(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setNosTitle(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setNosOrgnames(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setNosContent(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setNosFilepath(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setNosFilename(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setNosCreatetime(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setNosDel(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
        entity.setNosTimelength(cursor.getInt(offset + 10));
        entity.setNosEndtime(cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11));
     }
    
    @Override
    protected final String updateKeyAfterInsert(AmplyNotify entity, long rowId) {
        return entity.getNosId();
    }
    
    @Override
    public String getKey(AmplyNotify entity) {
        if(entity != null) {
            return entity.getNosId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(AmplyNotify entity) {
        return entity.getNosId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
