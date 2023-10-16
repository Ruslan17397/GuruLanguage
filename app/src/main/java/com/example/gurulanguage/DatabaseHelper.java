package com.example.gurulanguage;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {
    private final Context context;
    public static final String DATABASE_NAME = "lang.db";
    public static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME = "lang";

    SQLiteDatabase db;
    FileManager fileManager;
    public DatabaseHelper(@Nullable Context context) {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
        this.context = context;
        fileManager = new FileManager(context);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public  void  openDataBase(String dbPath,String dbName){
        assert context != null;
        dbPath = context.getFilesDir().getPath()+"/" + dbPath + "/"+dbName;
        File file = new File(dbPath);
        if(!file.exists()){
            String finalDbPath = dbPath;
            fileManager.download("http://english.ho.ua/sqlite/"+dbName, "lang.db", "lang", new FileManager.FileSuccess() {
                @Override
                public void onSuccess() {
                    openDB(finalDbPath);
                }

                @Override
                public void onFailure() {
                    Log.d("TAG","Fail");
                }

                @Override
                public void onProgress(int progress) {

                }
            });
        }else{
            openDB(dbPath);
        }

    }
    public void openDB(String dbPath){
            db = SQLiteDatabase.openDatabase(dbPath,null,SQLiteDatabase.OPEN_READONLY);
    }
    public String[] getTranslate(String word){
        Cursor c = db.query(TABLE_NAME, new String[]{"lang_to", "transcription"},"LOWER(lang_from) = ?", new String[] {word.toLowerCase()},null,null,null);
        if(c.moveToFirst()) {
            return new String[]{c.getString(0),"/"+c.getString(1)+"/"};
        }else{
            return null;
        }

    }
    public String getWordByLimit(){
        Cursor c = db.rawQuery("SELECT lang_from, lang_to FROM "+ TABLE_NAME +" LIMIT 20", null);
        if(c.moveToFirst()) {
            JSONArray jsonArray = new JSONArray();
            do {
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("original", c.getString(0));
                    jsonObject.put("translated", c.getString(1));
                    jsonArray.put(jsonObject);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            } while (c.moveToNext());
            return String.valueOf(jsonArray);
        }else{
            return null;
        }
    }
    public String getIrregulars(){
        Cursor c = db.rawQuery("SELECT * FROM irregular", null);
        if (c.moveToFirst()) {
            JSONArray jsonArray = new JSONArray();
            do {
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("Infinitive", c.getString(1));
                    jsonObject.put("Past Simple", c.getString(2));
                    jsonObject.put("Past Participle", c.getString(3));
                    jsonObject.put("Example", c.getString(4));
                    jsonObject.put("Translate", c.getString(5));
                    jsonObject.put("Level", c.getString(6));
                    jsonArray.put(jsonObject);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            } while (c.moveToNext());

            return String.valueOf(jsonArray);
        }else{
            return null;
        }
    }
    public JSONArray getIrregularsArray(){
        Cursor c = db.rawQuery("SELECT * FROM irregular", null);
        if (c.moveToFirst()) {
            ArrayList<String> irregularsArray = new ArrayList<>();
            do {
                irregularsArray.add(c.getString(1));
                irregularsArray.add(c.getString(2));
                irregularsArray.add(c.getString(3));
            } while (c.moveToNext());

            return new JSONArray(irregularsArray);
        }else{
            return null;
        }
    }
}
