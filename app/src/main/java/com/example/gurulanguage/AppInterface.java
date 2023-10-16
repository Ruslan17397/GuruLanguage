package com.example.gurulanguage;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class AppInterface {
        Context ctx;
        Translator translator;
        public  String[] translate;
        JsonManager jsonManager;
        FileManager fileManager;
        WebView web;
        Handler mainHandler;
        String code = "";
        TextToSpeechManager textToSpeechManager;
    AppInterface(Context ctx, WebView web) {
        this.ctx = ctx;
        this.web = web;
        translator = new Translator(ctx);
        jsonManager = new JsonManager(ctx);
        fileManager = new FileManager(ctx);
        mainHandler = new Handler(Looper.getMainLooper());
        textToSpeechManager = new TextToSpeechManager(ctx, web);
    }

        @JavascriptInterface
        public void startReader(String book_id, int tab_id){
            textToSpeechManager.TabToText(book_id, tab_id);
        }
        @JavascriptInterface
        public void setSpeed(String speed){
            textToSpeechManager.setSpeed(Float.parseFloat(speed));
        }


        @JavascriptInterface
        public String getConfigJson() {
            return jsonManager.getJsonFile("config.json");
                }
        @JavascriptInterface
        public int xpUp(int xp) {
            return jsonManager.xpUp(xp);
    }
        @JavascriptInterface
        public boolean putWord(String original, String transcription, String translate) {
           return jsonManager.putWord(original.replaceAll("[^\\-'\\w—]+", ""), transcription, translate);
        }

        @JavascriptInterface
        public boolean putRepeatWord(String original, String transcription, String translate) {
            return jsonManager.putRepeatWord(original.replaceAll("[^\\-'\\w—]+", ""),transcription, translate);
        }

    @JavascriptInterface
    public void moveWordToRepeat(String original) {
        jsonManager.moveWordToRepeat(original);
    }
    @JavascriptInterface
    public void removeWordFromRepeat(String original) {
        jsonManager.removeWordFromRepeat(original);
    }

        @JavascriptInterface
        public String getWordJson() {
            return jsonManager.getJsonFile("words.json");
        }

    @JavascriptInterface
    public String getData(String url) {
        return fileManager.getData(url);
    }
    @JavascriptInterface
    public String getTab(String book_id, int tab_id) {
        if(!book_id.equals(textToSpeechManager.book_id) && textToSpeechManager.isPlaying){
            textToSpeechManager.onTrackStop();
        }
        return fileManager.getTab(book_id, tab_id);
    }

    @JavascriptInterface
    public String getBooks(String url) {
        return fileManager.getBooks(url);
    }

    @JavascriptInterface
    public String getDescription(String id, String url) {
        return fileManager.getBookDescription(id, url);
    }

    @JavascriptInterface
    public boolean downloadBook(String id, String lang) {
        if(!fileManager.hasBook(id)) {
            fileManager.downloadBook(id, lang, new FileManager.FileSuccess() {
                @Override
                public void onSuccess() {

                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            web.evaluateJavascript("document.querySelector(\".library\").contentWindow.postMessage(JSON.stringify({\"type\":\"loaded\",\"data\": " + id + "}))", null);
                        }
                    });
                }

                @Override
                public void onFailure() {
                    Log.d("INFO", "Failure");
                }

                @Override
                public void onProgress(int progress) {

                }
            });
            return false;
        }else return true;

    }
    @JavascriptInterface
    public String getImage(String url){
        return new File(ctx.getFilesDir(), url).getAbsolutePath();

    }

    @JavascriptInterface
    public void setLastReadingBookTab(String book_id, String tab_id){
        fileManager.setLastReadingBookTab(book_id, Integer.valueOf(tab_id));
    }


    @JavascriptInterface
    public String getBook(String id){
        return String.valueOf(fileManager.getBook(id));

    }

    @JavascriptInterface
    public void setLastReadingBook(String id){
        fileManager.setLastReadingBook(id);

    }
    @JavascriptInterface
    public String continueReading(){
        return fileManager.continueReading();
    }
    @JavascriptInterface
    public void openTelegramBot() {
        Intent telegram = new Intent(Intent.ACTION_VIEW , Uri.parse("tg://resolve?domain=tsytsiurka1bot"));
        ctx.startActivity(telegram);
    }
    @JavascriptInterface
    public boolean isFirst() {
            SharedPreferences prefs = ctx.getSharedPreferences("prefs", Context.MODE_PRIVATE);
            return prefs.getBoolean("firstStart", true);
    }
    @JavascriptInterface
    public String getIrregulars() {
        return translator.databaseHelper.getIrregulars();
    }

    @JavascriptInterface
    public String getIrregularsArray() {
        return translator.databaseHelper.getIrregularsArray().toString();
    }


        @JavascriptInterface
        public String getCountryCode() {
            String json =  jsonManager.getJsonFile("config.json");
            try {
                JSONObject jsonObject = new JSONObject(json);
                return jsonObject.getJSONObject("settings").getJSONObject("language").getString("code2");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return json;
        }
        @JavascriptInterface
        public void toVoice(String text) {
            textToSpeechManager.toVoice(text);
        }

        @JavascriptInterface
        public String getWord(String word) {
            if(code == "") {
                code = getCountryCode();
            }
            try {
            translate = new String[2];
            translator.translate(word, code, value -> translate = value);

            return "[\""+translate[0]+"\",\""+translate[1]+"\"]";
        } catch (Exception e) {
            e.printStackTrace();
        }

            return String.valueOf(translate);
        }
    @JavascriptInterface
    public String getWordByLimit(){
        return translator.databaseHelper.getWordByLimit();

    }

}
