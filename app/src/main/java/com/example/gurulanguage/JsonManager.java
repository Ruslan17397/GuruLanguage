package com.example.gurulanguage;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class JsonManager {
    AssetManager assetsFolder;
    Context ctx;
    File jsonFolder;
    byte[] BUFFER_SIZE;
    JsonManager(Context ctx){
        this.ctx = ctx;
        assetsFolder = ctx.getAssets();
        BUFFER_SIZE = new byte[1024];
        jsonFolder = new File(ctx.getFilesDir(), "json");
        if(!jsonFolder.exists()){
            jsonFolder.mkdirs();
        }
    }

    public String getJsonFile(String fileName){
        File to = new File(jsonFolder,fileName);
        String line;
        StringBuilder json = new StringBuilder();
        int length;
        if(!to.exists()){
            try {
                to.createNewFile();
                InputStream from = assetsFolder.open("template/json/"+fileName);
                FileOutputStream outputStream = new FileOutputStream(to);
                while((length = from.read(BUFFER_SIZE)) > 0){
                    outputStream.write(BUFFER_SIZE,0,length);
                }
                from.close();
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        FileInputStream jsonInputStream;
        try {
            jsonInputStream = new FileInputStream(to);
            BufferedReader reader = new BufferedReader(new InputStreamReader(jsonInputStream));
            while ((line = reader.readLine()) != null) {
                json.append(line);
                json.append('\n');
            }
            jsonInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return json.toString();


    }
    public void editJson(JSONObject json, String fileName){
        String data = json.toString();
        File to = new File(jsonFolder,fileName);
        FileOutputStream fileOutputStream;
        try {
            fileOutputStream = new FileOutputStream(to);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);
            outputStreamWriter.append(data);
            outputStreamWriter.close();

            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public boolean putWord(String original, String transcription, String translate){
        String json = getJsonFile("words.json");
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONObject learn = jsonObject.getJSONObject("learn");

            if(!learn.has(original)) {
                JSONObject config = jsonObject.getJSONObject("config");
                int learnCount = config.getInt("learnCount");
                learnCount++;
                config.put("learnCount", learnCount);
                learn.put(original, "[\""+translate+"\",\""+transcription+"\"]");
                editJson(jsonObject, "words.json");
                return true;
            }else{
                return false;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }
    public void moveWordToRepeat(String original){
        String json = getJsonFile("words.json");
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONObject learn = jsonObject.getJSONObject("learn");
            JSONObject repeat = jsonObject.getJSONObject("repeat");

            JSONObject config = jsonObject.getJSONObject("config");
            int learnCount = config.getInt("learnCount");
            int repeatCount = config.getInt("repeatCount");
            repeatCount++;
            learnCount--;
            config.put("repeatCount",repeatCount);
            config.put("learnCount",learnCount);
            repeat.put(original,learn.getString(original));
            learn.remove(original);
            editJson(jsonObject,"words.json");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void removeWordFromRepeat(String original){
        String json = getJsonFile("words.json");
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONObject repeat = jsonObject.getJSONObject("repeat");

            JSONObject config = jsonObject.getJSONObject("config");
            int repeatCount = config.getInt("repeatCount");
            repeatCount--;
            config.put("repeatCount",repeatCount);
            repeat.remove(original);
            editJson(jsonObject,"words.json");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public boolean putRepeatWord(String original, String transcription, String translate){
        String json = getJsonFile("words.json");
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONObject repeat = jsonObject.getJSONObject("repeat");


//            if(!transcription.isEmpty()){
//               tr
//            }

            if(!repeat.has(original)) {
                JSONObject config = jsonObject.getJSONObject("config");
                int repeatCount = config.getInt("repeatCount");
                repeatCount++;
                config.put("repeatCount", repeatCount);
                repeat.put(original, "["+translate+","+transcription+"]");
                editJson(jsonObject, "words.json");
                return true;
            }else{
                return false;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }
    public int xpUp(int xp){
        String json = getJsonFile("config.json");
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONObject user = jsonObject.getJSONObject("user");
            int json_xp = user.getInt("xp");
            json_xp+=xp;
            user.put("xp", json_xp);
            editJson(jsonObject, "config.json");
            return json_xp;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return xp;
    }
}
