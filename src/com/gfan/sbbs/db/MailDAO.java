package com.gfan.sbbs.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.gfan.sbbs.bean.Mail;
import com.gfan.sbbs.db2.MailTable;

public class MailDAO {

	private static final String TAG = "MAILDAO";
	private SQLiteOpenHelper mSQLiteOpenHelper;
	
	public MailDAO(Context context){
		mSQLiteOpenHelper = SBBSDataBase.getInstance(context).getmDataBaseHelper();
	}
	
	public long insertMail(Mail mail,int type){
		SQLiteDatabase db = mSQLiteOpenHelper.getWritableDatabase();
		return db.insert(MailTable.TABLE_NAME, null, mail2ContentValue(mail, type));
	}
	
	public int insertMail(List<Mail> list,int type){
		int result = 0;
		for(Mail mail:list){
			long id = insertMail(mail, type);
			if(-1 == id){
				Log.w(TAG,"can not insert mail "+mail.getTitle());
			}else{
				++result;
			}
		}
		return result;
	}
	
	public int deleteMail(Mail mail,int type){
		SQLiteDatabase db = mSQLiteOpenHelper.getWritableDatabase();
		String where = MailTable.MAIL_ID+"="+mail.getNum()+" AND "+MailTable.TYPE+"="+type;
		return db.delete(MailTable.TABLE_NAME, where, null);
	}
	
	public int deleteMail(List<Mail> list,int type){
		int result = 0;
		for(Mail mail:list){
			int id = deleteMail(mail, type);
			if(-1 == id){
				Log.w(TAG,"can not delete mail "+mail.getTitle());
			}else{
				Log.i(TAG,"delete mail "+mail.getTitle());
				++result;
			}
		}
		return result;
	}
	public int deleteMail(int type){
		SQLiteDatabase db = mSQLiteOpenHelper.getWritableDatabase();
		String where = MailTable.TYPE +"="+type;
		return db.delete(MailTable.TABLE_NAME, where, null);
	}
	public List<Mail> fetchMail(int type){
		SQLiteDatabase db = mSQLiteOpenHelper.getReadableDatabase();
		List<Mail> mailList = new ArrayList<Mail>();
		String where = MailTable.TYPE+"="+type;
		final Cursor cursor = db.query(MailTable.TABLE_NAME, null, where, null, null, null, null);
		while(cursor.moveToNext()){
			Mail mail = mRowMapper.mapRow(cursor, 1);
			mailList.add(mail);
		}
		cursor.close();
		db.close();
		return mailList;
	}
	
	private ContentValues mail2ContentValue(Mail mail,int type){
		ContentValues v = new ContentValues();
		v.put(MailTable.MAIL_ID, mail.getNum());
		v.put(MailTable.AUTHOR, mail.getFrom());
		v.put(MailTable.STATUS, String.valueOf(mail.isUnRead()));
		v.put(MailTable.TIME, mail.getDate());
		v.put(MailTable.TITLE, mail.getTitle());
		v.put(MailTable.TYPE, String.valueOf(type));
		return v;
	}
	
	public interface RowMapper<T> {
		public T mapRow(Cursor cursor,int rowNum);
	}
	
	private RowMapper<Mail> mRowMapper = new RowMapper<Mail>() {

		@Override
		public Mail mapRow(Cursor cursor, int rowNum) {
			return MailTable.parseCursor(cursor);
		}
	};

}
