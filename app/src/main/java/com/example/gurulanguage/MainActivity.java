package com.example.gurulanguage;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    WebView AppView;
    CloudDataManager cloudDataManager;
    AppInterface appInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = getSharedPreferences("prefs", Context.MODE_PRIVATE);
        boolean isFirst = prefs.getBoolean("firstStart", true);
        cloudDataManager = new CloudDataManager(this);
        FirebaseUser acct = cloudDataManager.getProfile();

        if (acct != null) {
            String personName = acct.getDisplayName();
            String personEmail = acct.getEmail();
            Log.d("NAME", personName);
            Log.d("EMAIL", personEmail);
        }

        AppView = new WebView(this);

        if (isFirst) {
            Intent intent = new Intent(this, FirstStartScreen.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            return; // This is important to prevent further execution
        }

        setContentView(AppView);

        WebSettings webSetting = AppView.getSettings();

        webSetting.setJavaScriptEnabled(true);
        webSetting.setJavaScriptCanOpenWindowsAutomatically(true);
        webSetting.setSupportMultipleWindows(true);
        webSetting.setDomStorageEnabled(true);
        webSetting.setAllowFileAccess(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            webSetting.setAllowUniversalAccessFromFileURLs(true);
        }

        AppView.setWebChromeClient(new WebChromeClient());
        appInterface = new AppInterface(this, AppView);
        AppView.addJavascriptInterface(appInterface, "Android");
        AppView.loadUrl("file:///android_asset/index.html");

        AppView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }
        });
    }

    public void runThreadApp(String url) {
        this.runOnUiThread(() -> AppView.loadUrl(url));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
