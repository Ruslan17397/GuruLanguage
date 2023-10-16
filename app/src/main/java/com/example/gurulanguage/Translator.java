package com.example.gurulanguage;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Translator {
    OkHttpClient client;
    DatabaseHelper databaseHelper;
    Translator(Context ctx){
        client = new OkHttpClient();
        databaseHelper = new DatabaseHelper(ctx);
        databaseHelper.openDataBase("lang","lang.db");
    }
    public void translate(String word, String to,final TranslateCallback callback){
        String[] result = databaseHelper.getTranslate(word.replaceAll("[^\\-'\\w—]+", ""));
        if(result != null){
            callback.onSuccess(result);
        }else {
            String url = "http://translate.googleapis.com/translate_a/single?client=gtx&dt=t&dt=rm&ie=UTF-8&oe=UTF-8&sl=en&tl="+to+"&q="+word.replaceAll("[^\\-'\\w—]+", "");
            Request request = new Request.Builder().addHeader("Content-Type", "application/json; charset=UTF-8")
                    .url(url)
                    .build();
            try {
                Response response = client.newCall(request).execute();
                assert response.body() != null;
                String translate = response.body().string();
                JSONArray js = new JSONArray(translate).getJSONArray(0);
                String word_translate = js.getJSONArray(0).getString(0);
                String word_transcription;
                if(js.getJSONArray(1).length() == 4){
                    word_transcription = "/"+js.getJSONArray(1).getString(3)+"/";
                }else {
                    word_transcription = "";
                }
                callback.onSuccess(new String[]{word_translate, word_transcription});
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

        }
    }
    public interface TranslateCallback{
        void onSuccess(String[] result);
    }
}
