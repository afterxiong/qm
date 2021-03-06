package com.shareshow.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.shareshow.aide.retrofit.entity.StudyMaterialsVisitingMaterials;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "STUDY_MATERIALS_VISITING_MATERIALS".
*/
public class StudyMaterialsVisitingMaterialsDao extends AbstractDao<StudyMaterialsVisitingMaterials, String> {

    public static final String TABLENAME = "STUDY_MATERIALS_VISITING_MATERIALS";

    /**
     * Properties of entity StudyMaterialsVisitingMaterials.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property SfId = new Property(0, String.class, "sfId", true, "SF_ID");
        public final static Property SfFilename = new Property(1, String.class, "sfFilename", false, "SF_FILENAME");
        public final static Property SfUrl = new Property(2, String.class, "sfUrl", false, "SF_URL");
        public final static Property SfSpid = new Property(3, String.class, "sfSpid", false, "SF_SPID");
        public final static Property SfDel = new Property(4, int.class, "sfDel", false, "SF_DEL");
        public final static Property SfType = new Property(5, int.class, "sfType", false, "SF_TYPE");
        public final static Property LocalPath = new Property(6, String.class, "localPath", false, "LOCAL_PATH");
        public final static Property Date = new Property(7, Long.class, "date", false, "DATE");
    }


    public StudyMaterialsVisitingMaterialsDao(DaoConfig config) {
        super(config);
    }
    
    public StudyMaterialsVisitingMaterialsDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"STUDY_MATERIALS_VISITING_MATERIALS\" (" + //
                "\"SF_ID\" TEXT PRIMARY KEY NOT NULL ," + // 0: sfId
                "\"SF_FILENAME\" TEXT," + // 1: sfFilename
                "\"SF_URL\" TEXT," + // 2: sfUrl
                "\"SF_SPID\" TEXT," + // 3: sfSpid
                "\"SF_DEL\" INTEGER NOT NULL ," + // 4: sfDel
                "\"SF_TYPE\" INTEGER NOT NULL ," + // 5: sfType
                "\"LOCAL_PATH\" TEXT," + // 6: localPath
                "\"DATE\" INTEGER);"); // 7: date
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"STUDY_MATERIALS_VISITING_MATERIALS\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, StudyMaterialsVisitingMaterials entity) {
        stmt.clearBindings();
 
        String sfId = entity.getSfId();
        if (sfId != null) {
            stmt.bindString(1, sfId);
        }
 
        String sfFilename = entity.getSfFilename();
        if (sfFilename != null) {
            stmt.bindString(2, sfFilename);
        }
 
        String sfUrl = entity.getSfUrl();
        if (sfUrl != null) {
            stmt.bindString(3, sfUrl);
        }
 
        String sfSpid = entity.getSfSpid();
        if (sfSpid != null) {
            stmt.bindString(4, sfSpid);
        }
        stmt.bindLong(5, entity.getSfDel());
        stmt.bindLong(6, entity.getSfType());
 
        String localPath = entity.getLocalPath();
        if (localPath != null) {
            stmt.bindString(7, localPath);
        }
 
        Long date = entity.getDate();
        if (date != null) {
            stmt.bindLong(8, date);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, StudyMaterialsVisitingMaterials entity) {
        stmt.clearBindings();
 
        String sfId = entity.getSfId();
        if (sfId != null) {
            stmt.bindString(1, sfId);
        }
 
        String sfFilename = entity.getSfFilename();
        if (sfFilename != null) {
            stmt.bindString(2, sfFilename);
        }
 
        String sfUrl = entity.getSfUrl();
        if (sfUrl != null) {
            stmt.bindString(3, sfUrl);
        }
 
        String sfSpid = entity.getSfSpid();
        if (sfSpid != null) {
            stmt.bindString(4, sfSpid);
        }
        stmt.bindLong(5, entity.getSfDel());
        stmt.bindLong(6, entity.getSfType());
 
        String localPath = entity.getLocalPath();
        if (localPath != null) {
            stmt.bindString(7, localPath);
        }
 
        Long date = entity.getDate();
        if (date != null) {
            stmt.bindLong(8, date);
        }
    }

    @Override
    public String readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0);
    }    

    @Override
    public StudyMaterialsVisitingMaterials readEntity(Cursor cursor, int offset) {
        StudyMaterialsVisitingMaterials entity = new StudyMaterialsVisitingMaterials( //
            cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0), // sfId
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // sfFilename
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // sfUrl
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // sfSpid
            cursor.getInt(offset + 4), // sfDel
            cursor.getInt(offset + 5), // sfType
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // localPath
            cursor.isNull(offset + 7) ? null : cursor.getLong(offset + 7) // date
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, StudyMaterialsVisitingMaterials entity, int offset) {
        entity.setSfId(cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0));
        entity.setSfFilename(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setSfUrl(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setSfSpid(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setSfDel(cursor.getInt(offset + 4));
        entity.setSfType(cursor.getInt(offset + 5));
        entity.setLocalPath(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setDate(cursor.isNull(offset + 7) ? null : cursor.getLong(offset + 7));
     }
    
    @Override
    protected final String updateKeyAfterInsert(StudyMaterialsVisitingMaterials entity, long rowId) {
        return entity.getSfId();
    }
    
    @Override
    public String getKey(StudyMaterialsVisitingMaterials entity) {
        if(entity != null) {
            return entity.getSfId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(StudyMaterialsVisitingMaterials entity) {
        return entity.getSfId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
