package br.com.clickjogos.ssoexample;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class ConnectActivity extends Activity {
    WebView webView;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_connect);

        webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setSupportZoom(false);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Uri callbackUri = Uri.parse(url);
                String uid = callbackUri.getQueryParameter("uid");
                if (uid != null) {
                    System.out.println("uid: " + uid);
                    Intent callbackIntent = new Intent(ConnectActivity.this, OauthCallbackActivity.class);
                    callbackIntent.putExtra("uid", uid);
                    startActivity(callbackIntent);
                    finish();
                    return false;
                }
                return true;
            }
        });

        webView.loadUrl(Constants.AUTHORIZE_URL);
    }
}