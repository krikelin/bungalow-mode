package com.aleros.bungalowmode;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class BungalowDatabase extends SQLiteOpenHelper {

	public BungalowDatabase(Context context) {
		super(context, "bungalow", null, 1);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL("CREATE TABLE periods (id integer primary key autoincrement, startTime INTEGER, endTime INTEGER INTEGER, lockout int not null, silence int not null);");
		
		// Morning
		db.execSQL("INSERT INTO periods (startTime, endTime, lockout, silence) VALUES (60*9 + 15, 60*12, 0, 1);");
		
		// Afternoon
		db.execSQL("INSERT INTO periods (startTime, endTime, lockout, silence) VALUES (60*13, 60*15, 0, 1);");
		
		// Night
		db.execSQL("INSERT INTO periods (startTime, endTime, lockout, silence) VALUES (60*23, 60*23 + 59, 0, 1);");
		db.execSQL("INSERT INTO periods (startTime, endTime, lockout, silence) VALUES (60*0, 7*0, 0, 1);");
		// Insert test data
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}
	

}
