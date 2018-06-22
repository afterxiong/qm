package com.shareshow.airpc.util;

import android.content.Context;
import android.os.Environment;

import com.shareshow.airpc.model.LancherFile;

import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

public class QMDbUtil {

	private DbManager manager;
	private DbManager.DaoConfig config = new DbManager.
			DaoConfig().
			setDbDir(Environment.getExternalStorageDirectory()).
			setDbName("file.db").
			setDbVersion(2);

	private static QMDbUtil qmDbUtil=null;

	private QMDbUtil(Context mContext){
		super();
		manager = x.getDb(config);
	}

	public static  QMDbUtil getIntance(Context mContext){

		if(qmDbUtil==null){
			synchronized (QMDbUtil.class){
				if(qmDbUtil==null){
					qmDbUtil=new QMDbUtil(mContext);
				}
			}
		}

		return qmDbUtil;
	}

	//add
	public void insert(LancherFile mLancherFile) throws DbException {
		manager.save(mLancherFile);
	}

	public ArrayList<LancherFile> getAll() throws DbException {
		List<LancherFile> mLancherFile = new ArrayList<LancherFile>();
//		mLancherFile=dbUtil.findAll(Selector.from(LancherFile.class));
		mLancherFile = manager.selector(LancherFile.class).findAll();
		return (ArrayList<LancherFile>) mLancherFile;

	}

	public void delete(int id) {
		try {
			manager.deleteById(LancherFile.class, id);
		} catch (DbException e) {
			e.printStackTrace();
		}
	}

	public void update(LancherFile mLancherFile) {
		switch (mLancherFile.getPermit()) {
			case 0:
				mLancherFile.setPermit(1);
				break;
			case 1:
				mLancherFile.setPermit(0);
				break;
		}
		try {
			manager.update(mLancherFile, new String[]{"permit"});
		} catch (DbException e) {
			e.printStackTrace();
		}
	}

	public boolean hashName(String name) {
		LancherFile mm = null;
		try {
//            mm = dbUtil.findFirst(Selector.from(LancherFile.class).where("name", "=", name));
//            mm=dbUtil.selector(LancherFile.class).select("name", "=", name)).;
			mm = manager.selector(LancherFile.class).where("name", "=", name).findFirst();
		} catch (DbException e) {
			e.printStackTrace();
		}
		if (mm == null)
			return false;
		else
			return true;
	}

	public boolean hasPath(String path, int permit) {
		LancherFile mm = null;
		try {
			mm = manager.selector(LancherFile.class).where("path", "=", path).findFirst();
		} catch (DbException e) {
			e.printStackTrace();
		}

		if (mm == null)
			return false;
		else {
			try {
				mm.setPermit(permit);
				manager.update(mm, "permit");
			} catch (DbException e) {
				e.printStackTrace();
			}
			return true;
		}
	}

	public int getFir(String name) {
		LancherFile mm = null;
		try {
			mm = manager.selector(LancherFile.class).where("name", "=", name).findFirst();
		} catch (DbException e) {
			e.printStackTrace();
		}
		if (mm == null)
			return 1;
		else
			return mm.getPermit();
	}
}
