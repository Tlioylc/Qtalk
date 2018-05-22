package qumi.com.qumitalk.service.Db;


import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import qumi.com.qumitalk.service.DataBean.QMMessageBean;

public class ChatMsgDao {
	private DBHelper helper;

	protected ChatMsgDao(Context context) {
		helper = new DBHelper(context);
	}

	protected ChatMsgDao(Context context, int version) {
		helper = new DBHelper(context, version);
	}

	/**
	 * 添加新信息
	 * @param QMMessageBean
	 */
	public int insert(QMMessageBean QMMessageBean) {
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(DBcolumns.MSG_FROM, QMMessageBean.getFromUser());
		values.put(DBcolumns.MSG_TO, QMMessageBean.getToUser());
		values.put(DBcolumns.MSG_TYPE, QMMessageBean.getType());
		values.put(DBcolumns.MSG_CHATTYPE, QMMessageBean.getChatType());
		values.put(DBcolumns.MSG_SENDUSER, QMMessageBean.getSendUser());
		values.put(DBcolumns.MSG_CONTENT, QMMessageBean.getContent());
		values.put(DBcolumns.MSG_ISCOMING, QMMessageBean.getIsComing());
		values.put(DBcolumns.MSG_DATE, QMMessageBean.getDate());
		values.put(DBcolumns.MSG_ISREADED, QMMessageBean.getIsReaded());
		values.put(DBcolumns.MSG_OTHER, QMMessageBean.getAttributeJson());
		 db.insert(DBcolumns.TABLE_MSG, null, values);
		db.close();
		int msgid=queryTheLastMsgId();//返回新插入记录的id
		return msgid;
	}
	

	/**
	 * 清空所有聊天记录
	 */
	public void deleteTableData() {
		SQLiteDatabase db = helper.getWritableDatabase();
		db.delete(DBcolumns.TABLE_MSG, null, null);
		db.close();
	}
	
	
	/**
	 * 根据msgid，删除对应聊天记录
	 * @return
	 */
	public long deleteMsgById(int msgid) {
		SQLiteDatabase db = helper.getWritableDatabase();
		long row = db.delete(DBcolumns.TABLE_MSG, DBcolumns.MSG_ID + " = ?", new String[] { ""+msgid });
		db.close();
		return row;
	}

	/**
	 * 查询列表,每页返回15条,依据id逆序查询，将时间最早的记录添加进list的最前面
	 * @return
	 */
	public ArrayList<QMMessageBean> queryMsg(String from, String to, int offset) {
		ArrayList<QMMessageBean> list = new ArrayList<QMMessageBean>();
		SQLiteDatabase db = helper.getWritableDatabase();
		String sql = "select * from " + DBcolumns.TABLE_MSG + " where "+DBcolumns.MSG_FROM+"=? and "+DBcolumns.MSG_TO+"=? order by " + DBcolumns.MSG_ID + " desc limit ?,?";
		String[] args = new String[] {from,to,String.valueOf(offset),"15" };
		Cursor cursor = db.rawQuery(sql, args);
		QMMessageBean qMMessageBean = null;
		while (cursor.moveToNext()) {
			qMMessageBean = QMMessageBean.createEmptyMessage();
			qMMessageBean.setMsgId(cursor.getInt(cursor.getColumnIndex(DBcolumns.MSG_ID)));
			qMMessageBean.setFromUser(cursor.getString(cursor.getColumnIndex(DBcolumns.MSG_FROM)));
			qMMessageBean.setToUser(cursor.getString(cursor.getColumnIndex(DBcolumns.MSG_TO)));
			qMMessageBean.setType(cursor.getInt(cursor.getColumnIndex(DBcolumns.MSG_TYPE)));
			qMMessageBean.setChatType(cursor.getInt(cursor.getColumnIndex(DBcolumns.MSG_CHATTYPE)));
			qMMessageBean.setSendUser(cursor.getString(cursor.getColumnIndex(DBcolumns.MSG_SENDUSER)));
			qMMessageBean.setContent(cursor.getString(cursor.getColumnIndex(DBcolumns.MSG_CONTENT)));
			qMMessageBean.setIsComing(cursor.getInt(cursor.getColumnIndex(DBcolumns.MSG_ISCOMING)));
			qMMessageBean.setDate(cursor.getString(cursor.getColumnIndex(DBcolumns.MSG_DATE)));
			qMMessageBean.setIsReaded(cursor.getString(cursor.getColumnIndex(DBcolumns.MSG_ISREADED)));
			qMMessageBean.setAttributeJson(cursor.getString(cursor.getColumnIndex(DBcolumns.MSG_OTHER)));
			list.add(0, qMMessageBean);
		}
		cursor.close();
		db.close();
		return list;
	}
	
	/**
	 * 查询最新一条记录
	 * @return
	 */
	public QMMessageBean queryTheLastMsg() {
		SQLiteDatabase db = helper.getWritableDatabase();
		String sql = "select * from " + DBcolumns.TABLE_MSG + " order by " + DBcolumns.MSG_ID + " desc limit 1";
		String[] args = new String[] {};
		Cursor cursor = db.rawQuery(sql, args);
		
		QMMessageBean qMMessageBean = null;
		while (cursor.moveToNext()) {
			qMMessageBean = QMMessageBean.createEmptyMessage();
			qMMessageBean.setMsgId(cursor.getInt(cursor.getColumnIndex(DBcolumns.MSG_ID)));
			qMMessageBean.setFromUser(cursor.getString(cursor.getColumnIndex(DBcolumns.MSG_FROM)));
			qMMessageBean.setToUser(cursor.getString(cursor.getColumnIndex(DBcolumns.MSG_TO)));
			qMMessageBean.setType(cursor.getInt(cursor.getColumnIndex(DBcolumns.MSG_TYPE)));
			qMMessageBean.setChatType(cursor.getInt(cursor.getColumnIndex(DBcolumns.MSG_CHATTYPE)));
			qMMessageBean.setSendUser(cursor.getString(cursor.getColumnIndex(DBcolumns.MSG_SENDUSER)));
			qMMessageBean.setContent(cursor.getString(cursor.getColumnIndex(DBcolumns.MSG_CONTENT)));
			qMMessageBean.setIsComing(cursor.getInt(cursor.getColumnIndex(DBcolumns.MSG_ISCOMING)));
			qMMessageBean.setDate(cursor.getString(cursor.getColumnIndex(DBcolumns.MSG_DATE)));
			qMMessageBean.setIsReaded(cursor.getString(cursor.getColumnIndex(DBcolumns.MSG_ISREADED)));
			qMMessageBean.setAttributeJson(cursor.getString(cursor.getColumnIndex(DBcolumns.MSG_OTHER)));
		}
		cursor.close();
		db.close();
		return qMMessageBean;
	}
	
	/**
	 * 查询最新一条记录的id
	 * @return
	 */
	public int queryTheLastMsgId() {
		SQLiteDatabase db = helper.getWritableDatabase();
		String sql = "select "+DBcolumns.MSG_ID+" from " + DBcolumns.TABLE_MSG + " order by " + DBcolumns.MSG_ID + " desc limit 1";
		String[] args = new String[] {};
		Cursor cursor = db.rawQuery(sql, args);
		int id =-1;
		if (cursor.moveToNext()) {
			id=cursor.getInt(cursor.getColumnIndex(DBcolumns.MSG_ID));
		}
		cursor.close();
		db.close();
		return id;
	}
	
	/**
	 * 查询所有信息未读数量
	 */
	public int queryAllNotReadCount() {
		SQLiteDatabase db = helper.getWritableDatabase();
		int count = 0;
		Cursor cursor = db.rawQuery("select count(*) from "+ DBcolumns.TABLE_MSG + " where " + DBcolumns.MSG_ISREADED+ " = 0  ", null);
		cursor.moveToFirst();
		count = cursor.getInt(0);
		cursor.close();
		return count;
	}
	
	/**
	 * 更新所有信息为已读
	 * 
	 * @param
	 */
	public long updateAllMsgToRead(String from,String to) {
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(DBcolumns.MSG_ISREADED, "1");
		long row = db.update(DBcolumns.TABLE_MSG, values, DBcolumns.MSG_FROM+ " =? and "+DBcolumns.MSG_TO+ " =? and "+DBcolumns.MSG_ISREADED+"=0 ", new String[] { from,to });
		db.close();
		return row;
	}
	
	public long deleteAllMsg(String from, String to) {
		SQLiteDatabase db = helper.getWritableDatabase();
		long row = db.delete(DBcolumns.TABLE_MSG, DBcolumns.MSG_FROM + " = ?"+" AND " + DBcolumns.MSG_TO + " = ?", new String[] { from,to });
		db.close();
		return row;
	}
	
}
