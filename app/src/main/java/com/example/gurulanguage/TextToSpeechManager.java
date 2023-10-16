package com.example.gurulanguage;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.webkit.WebView;
import com.example.gurulanguage.Service.OnClearFromRecentService;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class TextToSpeechManager  implements Playable, TextToSpeech.OnInitListener{

    ArrayList<String> sentences = new ArrayList<String>();
    int index = 0;
    String book_id;
    int tab_id;
    int first_word_id = 0;
    int word_id = 0;
    float speechRate = 0.5f;
    TextToSpeech tts;
    WebView web;
    HashMap<String, String> paramsMap = new HashMap<>();
    NotificationManager notificationManager;
    boolean isPlaying = false;
    Context ctx;
    FileManager fileManager;
    Bitmap poster;
    String bookTitle, bookAuthor;

    TextToSpeechManager(Context ctx, WebView web) {
        this.web = web;
        this.ctx = ctx;
        fileManager = new FileManager(ctx);
        paramsMap.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "ThreeArgument");



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            createChannel();
            ctx.registerReceiver(broadcastReceiver, new IntentFilter("TRACKS_TRACKS"));
            ctx.startService(new Intent(ctx, OnClearFromRecentService.class));
        }
    }
    public ArrayList<String> extractSentences(String htmlString) {
        ArrayList<String> sentences = new ArrayList<>();

        Document document = Jsoup.parse(htmlString);
        Elements paragraphs = document.select(".indent");

        for (Element paragraph : paragraphs) {
            String sentence = paragraph.text();
            sentences.add(sentence);
        }

        return sentences;
    }
    public void TabToText(String book_id, int tab_id){
        this.book_id = book_id;
        this.tab_id = tab_id;
        try {
            JSONObject jsonObject = new JSONObject(fileManager.readFromFile(fileManager.offlineJson));
            JSONObject books = jsonObject.getJSONObject("books");
            JSONObject book = books.getJSONObject(book_id);
            String posterPath = book.getString("poster");
            bookTitle = book.getString("title");
            bookAuthor = book.getString("author");
            poster = fileManager.getBitmap(posterPath);
            sentences = extractSentences(fileManager.getTab(book_id, tab_id));
            onTrackPlay();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private void createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(CreateNotification.CHANNEL_ID,
                    "KOD Dev", NotificationManager.IMPORTANCE_LOW);

            notificationManager = ctx.getSystemService(NotificationManager.class);
            if (notificationManager != null){
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    //populate list with tracks


    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getExtras().getString("actionname");

            switch (action){
                case CreateNotification.ACTION_PLAY:
                    if (isPlaying){
                        onTrackPause();
                    } else {
                        onTrackPlay();
                    }
                    break;
                case CreateNotification.ACTION_STOP:
                    onTrackStop();
            }
        }
    };


    @Override
    public void onTrackPlay() {
        if(index == sentences.size()) index = 0;
        tts = new TextToSpeech(ctx, this);
        CreateNotification.createNotification(ctx, R.drawable.ic_baseline_pause_24, poster, bookTitle, bookAuthor);
        CreateNotification.setMediaPlaybackState(PlaybackStateCompat.STATE_PLAYING);
        isPlaying = true;
    }

    @Override
    public void onTrackPause() {
        tts.stop();
        tts.shutdown();
        CreateNotification.createNotification(ctx, R.drawable.ic_baseline_play_arrow_24, poster, bookTitle, bookAuthor);
        CreateNotification.setMediaPlaybackState(PlaybackStateCompat.STATE_PAUSED);
        isPlaying = false;
        word_id = first_word_id;
    }
    @Override
    public void onTrackStop() {
        tts.stop();
        tts.shutdown();
        isPlaying = false;
        index = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            notificationManager.cancelAll();
        }
    }

    public void toVoice(String text){
        if(isPlaying) onTrackStop();
        tts = new TextToSpeech(ctx, initStatus -> {
            if (initStatus == TextToSpeech.SUCCESS) {
                tts.setLanguage(Locale.ENGLISH);
                tts.setSpeechRate(1.0f);
                tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);

            }
        });
        tts.stop();
        tts.shutdown();
    }



    public void setSpeed(float speed) {
        speechRate = speed / 2;
        tts.setSpeechRate(speechRate);
        onTrackPause();
        onTrackPlay();
    }

    public void setHighLight(int id, int tab_id){
        web.post(new Runnable() {
            @Override
            public void run() {
                web.evaluateJavascript("document.querySelector(\".library\").contentWindow.postMessage(JSON.stringify({\"type\":\"highlight\",\"data\": [\"" + String.valueOf(tab_id)+ "\",\""+String.valueOf(id)+"\"]}))", null);
            }
        });
    }

    @Override
    public void onDestroy() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            notificationManager.cancelAll();
        }

        ctx.unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onInit(int i) {
        tts.setLanguage(Locale.ENGLISH);
        tts.setSpeechRate(speechRate);

        tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String s) {
                setHighLight(index, tab_id);
            }

            @Override
            public void onDone(String s) {
                index++;
                if(index == sentences.size()){
                    TabToText(book_id, tab_id+1);
                }
            }

            @Override
            public void onError(String s) {
                Log.d("TAG", "error");
            }
        });
        if (sentences.size() > 0) {
            for (String sentence: sentences) {
                    tts.speak(sentence, TextToSpeech.QUEUE_ADD, paramsMap);
            }
        }

    }
}
