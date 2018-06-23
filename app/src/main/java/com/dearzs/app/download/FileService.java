package com.dearzs.app.download;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
/**
 * 业务bean
 *
 */
public class FileService {
	DBOpenHelper openHelper;
	SQLiteDatabase db;

	public FileService(Context context) {
		openHelper = new DBOpenHelper(context);
		db = openHelper.getReadableDatabase();
	}
	/**
	 * 获取每条线程已经下载的文件长度
	 * @param path
	 * @return
	 */
	public Map<Integer, Integer> getData(String path){
		Cursor cursor = null;
		Map<Integer, Integer> data = null;
		synchronized (openHelper) {
			if(!db.isOpen()) {
				db = openHelper.getReadableDatabase();
			}
			try {
				cursor = db.rawQuery("select threadid, downlength from filedownlog where downpath=?", new String[]{path});
				data = new HashMap<Integer, Integer>();
				while(cursor.moveToNext()){
					data.put(cursor.getInt(0), cursor.getInt(1));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}finally {
				cursor.close();
				db.close();
				
			}
		}
		return data;
	}
	/**
	 * 保存每条线程已经下载的文件长度
	 * @param path
	 * @param map
	 */
	public void save(String path,  Map<Integer, Integer> map){//int threadid, int position
		synchronized (openHelper) {			
			if(!db.isOpen()) {
				db = openHelper.getReadableDatabase();
			}
			db.beginTransaction();
			try{
				for(Map.Entry<Integer, Integer> entry : map.entrySet()){
					db.execSQL("insert into filedownlog(downpath, threadid, downlength) values(?,?,?)",
							new Object[]{path, entry.getKey(), entry.getValue()});
				}
				db.setTransactionSuccessful();
			}finally{
				db.endTransaction();
				db.close();
			}
		}
	}
	/**
	 * 实时更新每条线程已经下载的文件长度
	 * @param path
	 * @param map
	 */
	public void update(String path, Map<Integer, Integer> map){
		synchronized (openHelper) {			
			if(!db.isOpen()) {
				db = openHelper.getReadableDatabase();
			}
			db.beginTransaction();
			try{
				for(Map.Entry<Integer, Integer> entry : map.entrySet()){
					db.execSQL("update filedownlog set downlength=? where downpath=? and threadid=?",
							new Object[]{entry.getValue(), path, entry.getKey()});
				}
				db.setTransactionSuccessful();
			}finally{
				db.endTransaction();
				db.close();
			}
		}
	}
	/**
	 * 当文件下载完成后，删除对应的下载记录
	 * @param path
	 */
	public void delete(String path){
		synchronized (openHelper) {			
			if(!db.isOpen()) {
				db = openHelper.getReadableDatabase();
			}
			db.execSQL("delete from filedownlog where downpath=?", new Object[]{path});
//			db.endTransaction();
			db.close();
		}
	}
	
	public void close()
	{
		synchronized (openHelper) {			
			try {
				if(db != null) {
					if(db.isOpen()) {
						db.endTransaction();
						db.close();
					}
					db = null;
				}
				openHelper = null;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
}
