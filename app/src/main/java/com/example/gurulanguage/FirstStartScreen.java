package com.example.gurulanguage;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;


import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FirstStartScreen extends AppCompatActivity {
    CloudDataManager cloudDataManager;
    WebView startScreen;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_start_screen);

        startScreen = (WebView) findViewById(R.id.firstStart);
        WebSettings webSetting = startScreen.getSettings();

        webSetting.setJavaScriptEnabled(true);

        cloudDataManager = new CloudDataManager(FirstStartScreen.this);


        startScreen.addJavascriptInterface(new StartScreenInterface(), "Android");
        startScreen.loadUrl("file:///android_asset/FirstScreen/index.html");
    }
    public void showAuth(){
        startScreen.post(new Runnable() {
            @Override
            public void run() {
                startScreen.evaluateJavascript("showAuth()", null);
            }
        });
        }
    public void launchMain() {
        SharedPreferences prefs = getSharedPreferences("prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("firstStart", false);
        editor.apply();
        //-----------------------------------------------------
        Intent intent = new Intent(FirstStartScreen.this, MainActivity.class);
        startActivity(intent);
    }
    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if(result.getResultCode() == Activity.RESULT_OK){
            Intent data = result.getData();

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try{
                GoogleSignInAccount account = task.getResult(ApiException.class);
                cloudDataManager.userLogIn(account.getIdToken(), task1 -> {
                    launchMain();
                });
            } catch (ApiException e) {
                throw new RuntimeException(e);
            }
        }
    });


    public class StartScreenInterface {
        FileManager fileManager;
        JsonManager jsonManager;

        public StartScreenInterface() {
            jsonManager = new JsonManager(FirstStartScreen.this);
            fileManager = new FileManager(FirstStartScreen.this);
        }
        @JavascriptInterface
        public void googleAuth() {
            cloudDataManager.startLoginActivity(activityResultLauncher);
        }

        @JavascriptInterface
        public void firstLaunchSuccess(){
            launchMain();
        }
        @JavascriptInterface
        public String getData(String url) {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            try {
                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "";
        }

        @JavascriptInterface
        public void setStartConfig(String from, String to, String code1, String code2) {
            final int[] lastProgress = {0};
            fileManager.download("http://english.ho.ua/sqlite/"+code1+"_"+code2+".db", "lang.db", "lang", new FileManager.FileSuccess() {
                @Override
                public void onSuccess() {
                    showAuth();
                }

                @Override
                public void onFailure() {

                }

                @Override
                public void onProgress(int progress) {
                    if(lastProgress[0] != progress) {
                        startScreen.post(new Runnable() {
                            @Override
                            public void run() {
                                startScreen.evaluateJavascript(String.format("updateProgress(%d)", progress), null);
                            }
                        });
                        lastProgress[0] = progress;
                    }
                }

            });
            String json =  jsonManager.getJsonFile("config.json");
            try {
                JSONObject jsonObject = new JSONObject(json);
                JSONObject languages = jsonObject.getJSONObject("settings").getJSONObject("language");
                languages.put("from", from);
                languages.put("to",to);
                languages.put("code1", code1);
                languages.put("code2", code2);
                jsonManager.editJson(jsonObject, "config.json");
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}