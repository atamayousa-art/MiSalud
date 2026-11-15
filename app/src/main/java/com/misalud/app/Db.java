package com.misalud.app;

import android.content.*;
import android.database.Cursor;
import android.database.sqlite.*;
import java.util.*;

public class Db extends SQLiteOpenHelper {
    public Db(Context c) { super(c,"misalud.db",null,2); }
    @Override public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE patients(_id INTEGER PRIMARY KEY AUTOINCREMENT, full_name TEXT NOT NULL, short_name TEXT NOT NULL, age INTEGER NOT NULL, sex TEXT NOT NULL, weight TEXT, height TEXT, allergies TEXT, birth_date TEXT)");
        db.execSQL("CREATE TABLE medicines(_id INTEGER PRIMARY KEY AUTOINCREMENT, patient_id INTEGER NOT NULL, name TEXT NOT NULL, presentation TEXT, instructions TEXT, purpose TEXT, every_hours INTEGER NOT NULL, start_at INTEGER NOT NULL, end_at INTEGER NOT NULL, photo TEXT, active INTEGER DEFAULT 1)");
        db.execSQL("CREATE TABLE doses(_id INTEGER PRIMARY KEY AUTOINCREMENT, medicine_id INTEGER NOT NULL, scheduled_at INTEGER NOT NULL, recorded_at INTEGER NOT NULL, status TEXT NOT NULL, note TEXT, UNIQUE(medicine_id,scheduled_at) ON CONFLICT REPLACE)");
        db.execSQL("CREATE TABLE vitals(_id INTEGER PRIMARY KEY AUTOINCREMENT, patient_id INTEGER NOT NULL, kind TEXT NOT NULL, value1 REAL NOT NULL, value2 REAL, unit TEXT NOT NULL, measured_at INTEGER NOT NULL, note TEXT)");
        db.execSQL("CREATE TABLE symptoms(_id INTEGER PRIMARY KEY AUTOINCREMENT, patient_id INTEGER NOT NULL, name TEXT NOT NULL, started_at INTEGER NOT NULL, severity TEXT, note TEXT)");
        db.execSQL("CREATE TABLE visits(_id INTEGER PRIMARY KEY AUTOINCREMENT, patient_id INTEGER NOT NULL, title TEXT NOT NULL, doctor TEXT, location TEXT, visit_at INTEGER NOT NULL, notes TEXT, diagnosis_file TEXT, prescription_file TEXT, remind_minutes INTEGER NOT NULL DEFAULT 1440)");
        db.execSQL("CREATE TABLE exams(_id INTEGER PRIMARY KEY AUTOINCREMENT, patient_id INTEGER NOT NULL, name TEXT NOT NULL, exam_at INTEGER NOT NULL, result TEXT, attachment TEXT)");
    }
    @Override public void onUpgrade(SQLiteDatabase db,int oldV,int newV) { if(oldV<2)db.execSQL("ALTER TABLE patients ADD COLUMN birth_date TEXT"); }
    public long add(String table,ContentValues values){return getWritableDatabase().insertOrThrow(table,null,values);}
    public void update(String table,long id,ContentValues v){getWritableDatabase().update(table,v,"_id=?",new String[]{String.valueOf(id)});}
    public void delete(String table,long id){getWritableDatabase().delete(table,"_id=?",new String[]{String.valueOf(id)});}
    public ArrayList<ContentValues> rows(String table,String where,String[] args,String order){
        ArrayList<ContentValues> list=new ArrayList<>();
        try(Cursor c=getReadableDatabase().query(table,null,where,args,null,null,order)){
            while(c.moveToNext()){
                ContentValues v=new ContentValues();
                for(int i=0;i<c.getColumnCount();i++){
                    switch(c.getType(i)){
                        case Cursor.FIELD_TYPE_INTEGER: v.put(c.getColumnName(i),c.getLong(i));break;
                        case Cursor.FIELD_TYPE_FLOAT: v.put(c.getColumnName(i),c.getDouble(i));break;
                        case Cursor.FIELD_TYPE_STRING: v.put(c.getColumnName(i),c.getString(i));break;
                        default: v.putNull(c.getColumnName(i));
                    }
                }
                list.add(v);
            }
        }
        return list;
    }
    public ContentValues one(String table,long id){ArrayList<ContentValues> r=rows(table,"_id=?",new String[]{""+id},null);return r.isEmpty()?null:r.get(0);}
    public static String str(ContentValues v,String key){return v==null?"":v.getAsString(key)==null?"":v.getAsString(key);}
    public static long num(ContentValues v,String key){Long n=v==null?null:v.getAsLong(key);return n==null?0:n;}
}
