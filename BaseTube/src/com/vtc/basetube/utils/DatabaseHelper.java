package com.vtc.basetube.utils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import com.vtc.basetube.model.ItemVideo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

	// The Android's default system path of your application database.
	String DB_PATH = null;

	public static String DB_NAME = "qtvdb.sqlite";
	public static String TB_DATA = "tblData";
	public static String TB_SEARCH = "tblQuerySearch";

	public static String COLLUM_VIDEOID = "videoid";
	public static String COLLUM_TYPE = "type";
	public static String COLLUM_TITLE = "title";
	public static String COLLUM_THUMANIL = "thumnail";
	public static String COLLUM_DURATION = "Duration";
	public static String COLLUM_TXTQUERY = "txtQuery";
	public static String COLLUM_TIME = "time";
	public static String COLLUM_COUNTVIEW = "countview";

	private SQLiteDatabase myDataBase;

	private final Context myContext;

	/**
	 * Constructor Takes and keeps a reference of the passed context in order to
	 * access to the application assets and resources.
	 * 
	 * @param context
	 */
	public DatabaseHelper(Context context) {

		super(context, DB_NAME, null, 1);
		this.myContext = context;
		DB_PATH = "/data/data/" + context.getPackageName() + "/" + "databases/";
	}

	/**
	 * Creates a empty database on the system and rewrites it with your own
	 * database.
	 * */
	public void createDataBase() throws IOException {

		boolean dbExist = checkDataBase();

		if (dbExist) { // do nothing - database already exist
		} else {

			// By calling this method and empty database will be created into
			// the
			// default system path
			// of your application so we are gonna be able to overwrite that
			// database with our database.
			this.getReadableDatabase();

			try {
				copyDataBase();

			} catch (IOException e) {

				throw new Error("Error copying database");

			}
		}

	}

	/**
	 * Check if the database already exist to avoid re-copying the file each
	 * time you open the application.
	 * 
	 * @return true if it exists, false if it doesn't
	 */
	private boolean checkDataBase() {

		SQLiteDatabase checkDB = null;

		try {
			String myPath = DB_PATH + DB_NAME;
			checkDB = SQLiteDatabase.openDatabase(myPath, null,
					SQLiteDatabase.OPEN_READWRITE);

		} catch (SQLiteException e) {

			// database does't exist yet.

		}

		return checkDB != null ? true : false;
	}

	/**
	 * Copies your database from your local assets-folder to the just created
	 * empty database in the system folder, from where it can be accessed and
	 * handled. This is done by transfering bytestream.
	 * */
	private void copyDataBase() throws IOException {

		// Open your local db as the input stream
		InputStream myInput = myContext.getAssets().open(DB_NAME);

		// Path to the just created empty db
		String outFileName = DB_PATH + DB_NAME;

		// Open the empty db as the output stream
		OutputStream myOutput = new FileOutputStream(outFileName);

		// transfer bytes from the inputfile to the outputfile
		byte[] buffer = new byte[1024];
		int length;
		while ((length = myInput.read(buffer)) > 0) {
			myOutput.write(buffer, 0, length);
		}

		// Close the streams
		myOutput.flush();
		myOutput.close();
		myInput.close();

	}

	public void openDataBase() throws SQLException {

		// Open the database
		String myPath = DB_PATH + DB_NAME;

		myDataBase = SQLiteDatabase.openDatabase(myPath, null,
				SQLiteDatabase.OPEN_READWRITE);

	}

	@Override
	public synchronized void close() {

		if (myDataBase != null)
			myDataBase.close();

		super.close();

	}

	@Override
	public void onCreate(SQLiteDatabase db) {

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

	public void sqlExcute(String sql) {
		myDataBase.execSQL(sql);
	}

	// public int deleteAccount(String table, String cateId) {
	// return myDataBase.delete(table, COLLUM_VIDEOID + "='" + cateId + "'",
	// null);
	// }

	public int deleteLikeVideo(String table, int id) {
		return myDataBase.delete(table, COLLUM_VIDEOID + "='" + id + "'", null);
	}

	// public void updateEntry(String table, String accountName,
	// String numberPhone, String vertifycode) {
	// // Define the updated row content.
	// ContentValues updatedValues = new ContentValues();
	// // Assign values for each row.
	// updatedValues.put(COLLUM_CATID, accountName);
	// updatedValues.put(COLLUM_TITLE, numberPhone);
	// updatedValues.put(COLLUM_VIDEOID, vertifycode);
	//
	// String where = DatabaseHelper.COLLUM_CATID + " = ?";
	// myDataBase.update(table, updatedValues, where,
	// new String[] { accountName });
	// }
	//
	// public void updateEntryCount(String table, String count) {
	// // Define the updated row content.
	// ContentValues updatedValues = new ContentValues();
	// // Assign values for each row.
	// updatedValues.put(COLLUM_COUNT, count);
	//
	// String where = DatabaseHelper.COLLUM_ID + " = ?";
	// myDataBase.update(table, updatedValues, where, new String[] { "0" });
	// }

	public int getCountRow(String query) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(query, null);
		int cnt = cursor.getCount();
		cursor.close();
		return cnt;
	}

	public long insertQuerySearch(String value) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(COLLUM_TXTQUERY, value);

		return myDataBase.insert(TB_SEARCH, null, initialValues);
	}

	public long insertVideoLike(ItemVideo item, int type) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(COLLUM_VIDEOID, item.getId());
		initialValues.put(COLLUM_TYPE, type);
		initialValues.put(COLLUM_TITLE, item.getTitle());
		initialValues.put(COLLUM_THUMANIL, item.getThumbnail());
		initialValues.put(COLLUM_DURATION, item.getDuration());
		initialValues.put(COLLUM_COUNTVIEW, item.getViewCount());

		return myDataBase.insert(TB_DATA, null, initialValues);
	}

	public static ArrayList<ItemVideo> getVideoData(String sql,
			DatabaseHelper myDbHelper) {
		ArrayList<ItemVideo> listAccount = null;
		try {
			Cursor c = myDbHelper.query(DatabaseHelper.TB_DATA, null, null,
					null, null, null, null);
			c = myDbHelper.rawQuery(sql);
			listAccount = new ArrayList<ItemVideo>();

			if (c.moveToFirst()) {

				do {
					ItemVideo item = new ItemVideo();
					item.setId(c.getString(0));
					item.setType(c.getInt(1));
					item.setTitle(c.getString(2));
					item.setThumbnail(c.getString(3));
					item.setDuration(c.getString(4));
					item.setTime(c.getString(5));
					item.setViewCount(c.getString(6));

					listAccount.add(item);
				} while (c.moveToNext());
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return listAccount;
	}

	public Cursor query(String table, String[] columns, String selection,
			String[] selectionArgs, String groupBy, String having,
			String orderBy) {
		return myDataBase.query(table, null, null, null, null, null, null);
	}

	public Cursor rawQuery(String sql) {
		SQLiteDatabase db = this.getWritableDatabase();
		return db.rawQuery(sql, null);
	}

}