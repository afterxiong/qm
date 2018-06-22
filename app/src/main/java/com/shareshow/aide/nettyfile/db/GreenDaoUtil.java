package com.shareshow.aide.nettyfile.db;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.shareshow.App;
import com.shareshow.aide.util.CacheUserInfo;
import com.shareshow.airpc.util.DebugLog;
import com.shareshow.db.AdFile;
import com.shareshow.db.AdFileDao;
import com.shareshow.db.AppUseInfo;
import com.shareshow.db.AppUseInfoDao;
import com.shareshow.db.DaoMaster;
import com.shareshow.db.DaoSession;
import com.shareshow.db.DevAdPlayRecord;
import com.shareshow.db.DevAdPlayRecordDao;
import com.shareshow.db.DevAreaUseInfo;
import com.shareshow.db.DevAreaUseInfoDao;
import com.shareshow.db.DeviceUser;
import com.shareshow.db.DeviceUserDao;
import com.shareshow.db.FileCordBean;
import com.shareshow.db.FileCordBeanDao;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by TEST on 2017/12/12.
 * 数据库管理类
 */

public class GreenDaoUtil {

    private DaoMaster mDaoMaster;

    private DaoMaster.DevOpenHelper openHelper;

    private SQLiteDatabase db;

    public static GreenDaoUtil manager ;


    private final static String AD_DB="ad_db";


    private GreenDaoUtil(){

    }


    public static GreenDaoUtil getGreenDao(){
        if(manager==null){
            synchronized(GreenDaoUtil.class){
                if(manager==null){
                    manager=new GreenDaoUtil();
                }
            }
        }
        return manager;
    }

    public SQLiteDatabase getDb(){
        return db;
    }

    public void saveAppUserInfo(AppUseInfo info){
        DaoSession mDaoSession =getWriteGreenAdDao();
        if(mDaoSession!=null){
            AppUseInfoDao appUseInfoDao = mDaoSession.getAppUseInfoDao();
            appUseInfoDao.save(info);
        }
    }

    public void saveDevAreaUseInfo(DevAreaUseInfo info){
        DaoSession mDaoSession = getWriteGreenAdDao();
        if(mDaoSession!=null){
            DevAreaUseInfoDao devDao = mDaoSession.getDevAreaUseInfoDao();
            devDao.save(info);
        }
    }

    public void savaDeviceUpdate(DeviceUser user){
        DaoSession mDaoSession = getWriteGreenAdDao();
        if(mDaoSession!=null){
         DeviceUserDao userDao = mDaoSession.getDeviceUserDao();
         DeviceUser deviceUser =userDao.queryBuilder().where(DeviceUserDao.Properties.Ids.eq(user.getIds())).build().unique();
         if(deviceUser==null){
             userDao.save(user);
         }

        }
    }

    public void deleteDeviceUserAll(){
        DaoSession mDaoSession = getReadGreenAdDao();
        if(mDaoSession!=null){
         DeviceUserDao dao =   mDaoSession.getDeviceUserDao();
         dao.deleteAll();
        }
    }


    public List<DeviceUser> getDeviceUserAll(){
        DaoSession mDaoSession = getReadGreenAdDao();
        if(mDaoSession!=null){
           DeviceUserDao deviceUserDao = mDaoSession.getDeviceUserDao();
           List<DeviceUser> users = deviceUserDao.loadAll();
          return users;
        }
        return null;
    }

    public void savaDevAdPlayRecord(DevAdPlayRecord record){
        DaoSession mDaoSession = getWriteGreenAdDao();
        if(mDaoSession!=null){
           DevAdPlayRecordDao dao = mDaoSession.getDevAdPlayRecordDao();
           dao.save(record);
        }
    }

    public void updateDevAdPlayRecord(DevAdPlayRecord record){
        DaoSession mDaoSession = getWriteGreenAdDao();
        if(mDaoSession!=null){
         DevAdPlayRecordDao devAdPlayRecordDao =  mDaoSession.getDevAdPlayRecordDao();
         DevAdPlayRecord devAdPlayRecord = devAdPlayRecordDao.queryBuilder().where(DevAdPlayRecordDao.Properties.DarDate.eq(record.getDarDate()),DevAdPlayRecordDao.Properties.DarFileid.eq(record.getDarFileid()),DevAdPlayRecordDao.Properties.DarDevid.eq(record.getDarDevid())).build().unique();
         if(devAdPlayRecord!=null){
             devAdPlayRecord.setDarClicknum(devAdPlayRecord.getDarClicknum()+record.getDarClicknum());
             devAdPlayRecord.setDarPlaynum(devAdPlayRecord.getDarPlaynum()+record.getDarPlaynum());
             devAdPlayRecord.setDarDate(record.getDarDate());
             try{
                 devAdPlayRecordDao.update(devAdPlayRecord);
                 DebugLog.showLog(this,"devAdPlayRecord更新成功!");
             }catch (Exception e){
                 e.printStackTrace();
             }

         }else{
            savaDevAdPlayRecord(record);
         }

        }
    }

    public List<DevAdPlayRecord> getDevAdPlayRecords(){
        DaoSession mDaoSession = getReadGreenAdDao();
        if(mDaoSession!=null){
           DevAdPlayRecordDao dao = mDaoSession.getDevAdPlayRecordDao();
           List<DevAdPlayRecord> records = dao.loadAll();
           return records;
        }

        return null;
    }

    public void deleteAllDevAdPlayRecords(){
        DaoSession mDaoSession = getReadGreenAdDao();
        if(mDaoSession!=null){
            DevAdPlayRecordDao dao = mDaoSession.getDevAdPlayRecordDao();
            dao.deleteAll();
        }

    }


    public List<AppUseInfo> getAppUseInfos(){
        DaoSession mDaoSession = getReadGreenAdDao();
        if(mDaoSession!=null){
            AppUseInfoDao appUseInfoDao = mDaoSession.getAppUseInfoDao();
            List<AppUseInfo> appUseInfos =appUseInfoDao.loadAll();
            return appUseInfos;
        }
        return null;
    }

    public List<DevAreaUseInfo> getDevAreaUseInfos(){
        DaoSession mDaoSession = getReadGreenAdDao();
        if(mDaoSession!=null){
            DevAreaUseInfoDao devDao = mDaoSession.getDevAreaUseInfoDao();
            List<DevAreaUseInfo> devInfos =devDao.loadAll();
            return devInfos;
        }
        return null;
    }



    public boolean updateDevAreaUseInfo(DevAreaUseInfo info){
        DaoSession mDaoSession = getWriteGreenAdDao();
        if(mDaoSession!=null){
            DevAreaUseInfoDao devDao = mDaoSession.getDevAreaUseInfoDao();
            DevAreaUseInfo dInfo= devDao.queryBuilder().where(DevAreaUseInfoDao.Properties.DauDate.eq(info.getDauDate())).build().unique();
            if(dInfo!=null){
                dInfo.setDauTime(info.getDauTime()+dInfo.getDauTime());
                dInfo.setDauBootCount(dInfo.getDauBootCount()+info.getDauBootCount());
                dInfo.setDauBootAdShowCount(dInfo.getDauHotAdShowCount()+info.getDauBootAdShowCount());
                dInfo.setDauSaverAdShowCount(dInfo.getDauSaverAdShowCount()+info.getDauSaverAdShowCount());
                dInfo.setDauSaverAdClickCount(dInfo.getDauSaverAdClickCount()+info.getDauSaverAdClickCount());
                dInfo.setDauHotAdShowCount(dInfo.getDauHotAdShowCount()+info.getDauHotAdShowCount());
                dInfo.setDauHotAdClickCount(dInfo.getDauHotAdClickCount()+info.getDauHotAdClickCount());
                dInfo.setDauOfficialAdClickCount(dInfo.getDauOfficialAdClickCount()+info.getDauOfficialAdClickCount());
                dInfo.setDauLastusertime(info.getDauLastusertime());
                devDao.update(dInfo);
                Log.i("test","更新成功!");
                return true;
              }
        }
        return false;
    }

    public void deleteDevAreaUseInfo(DevAreaUseInfo info){
        DaoSession mDaoSession = getReadGreenAdDao();
        if(mDaoSession!=null){
            DevAreaUseInfoDao devDao = mDaoSession.getDevAreaUseInfoDao();
            devDao.delete(info);
        }
    }


    public void deleteAppUseInfo(AppUseInfo info){
        DaoSession mDaoSession =getReadGreenAdDao();
        if(mDaoSession!=null){
            AppUseInfoDao appUseInfoDao = mDaoSession.getAppUseInfoDao();
            appUseInfoDao.delete(info);
        }
    }


    public void deleteAllAppUserInfo(){
        DaoSession mDaoSession =getReadGreenAdDao();
        if(mDaoSession!=null){
            AppUseInfoDao appUseInfoDao = mDaoSession.getAppUseInfoDao();
            appUseInfoDao.deleteAll();
        }
    }


    public void deleteAllDevAreaUseInfo(){
        DaoSession mDaoSession = getReadGreenAdDao();
        if(mDaoSession!=null){
            DevAreaUseInfoDao devDao = mDaoSession.getDevAreaUseInfoDao();
            devDao.deleteAll();
        }
    }

    public void saveFileCordBean(FileCordBean bean){
        DaoSession mDaoSession =getWriteGreenAdDao();
        if(mDaoSession!=null){
            FileCordBeanDao fileDao = mDaoSession.getFileCordBeanDao();
            fileDao.save(bean);
        }
    }

    public void deletAllFileCordBean(){
        DaoSession mDaoSession =getReadGreenAdDao();
        if(mDaoSession!=null){
            FileCordBeanDao fileDao = mDaoSession.getFileCordBeanDao();
            fileDao.deleteAll();
        }
    }

    public void deletFileCordBean(FileCordBean bean){
        DaoSession mDaoSession =getReadGreenAdDao();
        if(mDaoSession!=null){
            FileCordBeanDao fileDao = mDaoSession.getFileCordBeanDao();
            fileDao.delete(bean);
        }
    }

    public List<FileCordBean> getFileCordBeans(){
        DaoSession mDaoSession = getReadGreenAdDao();
        if(mDaoSession!=null){
            FileCordBeanDao devDao = mDaoSession.getFileCordBeanDao();
            List<FileCordBean> devInfos =devDao.loadAll();
            return devInfos;
        }
        return null;
    }


    public void saveAdFile(AdFile adFile){
        DaoSession daoSession =  getWriteGreenAdDao();
        if(daoSession!=null){
            AdFileDao adFileDao = daoSession.getAdFileDao();
            AdFile af=adFileDao.queryBuilder().where(AdFileDao.Properties.FileId.eq(adFile.getFileId())).build().unique();
            if(af==null){
                adFileDao.save(adFile);
            }else{
                adFileDao.delete(af);
                adFileDao.save(adFile);
            }
        }
    }


    public List<AdFile> getAdFileList(){
        DaoSession daoSession =  getReadGreenAdDao();
        if(daoSession!=null){
            AdFileDao dao = daoSession.getAdFileDao();
            List<AdFile> files =dao.queryBuilder().where(AdFileDao.Properties.Phone.eq(CacheUserInfo.get().getUserPhone())).list();
            return files;

        }
        return null;
    }


    public List<String> getAdFileNames(){
        List<String> fileNames = new ArrayList<String>();
       List<AdFile> adFiles = getAdFileList();
        for(AdFile adFile : adFiles){
            fileNames.add(adFile.getFileName());
        }
        return fileNames;
    }



    public boolean deleteAdFile(AdFile file){
        DaoSession daoSession = getReadGreenAdDao();
        if(daoSession!=null){
            AdFileDao dao = daoSession.getAdFileDao();
            AdFile adFile = dao.queryBuilder().where(AdFileDao.Properties.FileId.eq(file.getFileId())).build().unique();
            if(adFile!=null){
                dao.delete(adFile);
                return true;
            }
        }
        return false;
    }


    public boolean deleteAdFile(String fileId){
        DaoSession daoSession = getReadGreenAdDao();
        if(daoSession!=null){
            AdFileDao dao = daoSession.getAdFileDao();
            AdFile adFile = dao.queryBuilder().where(AdFileDao.Properties.FileId.eq(fileId)).build().unique();
            if(adFile!=null){
                dao.delete(adFile);
                return true;
            }
        }
        return false;
    }


    public void deleteAdFileAll(){
        DaoSession daoSession = getReadGreenAdDao();
        if(daoSession!=null){
            daoSession.getAdFileDao().deleteAll();
        }
    }

    private DaoSession getWriteGreenAdDao() {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase(AD_DB));
        return daoMaster.newSession();
    }

    private DaoSession getReadGreenAdDao() {
        DaoMaster daoMaster = new DaoMaster(getReadableDatabase(AD_DB));
        return daoMaster.newSession();
    }



    /**
     * 获取可读数据库
     */
    private SQLiteDatabase getReadableDatabase(String dbName){
        if (openHelper == null) {
            openHelper = new DaoMaster.DevOpenHelper(App.getApp(), dbName, null);
        }
        SQLiteDatabase db = openHelper.getReadableDatabase();
        return db;
    }

    /**
     * 获取可写数据库
     */
    private SQLiteDatabase getWritableDatabase(String dbName) {
        if (openHelper == null) {
            openHelper = new DaoMaster.DevOpenHelper(App.getApp(), dbName, null);
        }
        SQLiteDatabase db = openHelper.getWritableDatabase();
        return db;
    }

}
