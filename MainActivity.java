package com.consistencyboard.app;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends Activity {

    private static final int PICK_FILE = 1001;
    private WebView web;
    private ValueCallback<Uri[]> filePathCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        web = new WebView(this);
        web.setBackgroundColor(Color.parseColor("#040720"));

        WebSettings settings = web.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true); // keeps localStorage, so your board is saved on the phone

        web.addJavascriptInterface(new Bridge(), "AndroidBridge");

        web.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Uri uri = request.getUrl();
                if ("file".equals(uri.getScheme())) {
                    return false;
                }
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, uri));
                } catch (Exception ignored) {
                }
                return true;
            }
        });

        web.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onShowFileChooser(WebView view, ValueCallback<Uri[]> callback,
                                             FileChooserParams params) {
                if (filePathCallback != null) {
                    filePathCallback.onReceiveValue(null);
                }
                filePathCallback = callback;
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("*/*");
                try {
                    startActivityForResult(Intent.createChooser(intent, "Choose backup file"), PICK_FILE);
                } catch (Exception e) {
                    filePathCallback = null;
                    return false;
                }
                return true;
            }
        });

        setContentView(web);
        web.loadUrl("file:///android_asset/index.html");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_FILE) {
            if (filePathCallback != null) {
                Uri[] result = null;
                if (resultCode == RESULT_OK && data != null && data.getData() != null) {
                    result = new Uri[]{data.getData()};
                }
                filePathCallback.onReceiveValue(result);
                filePathCallback = null;
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onBackPressed() {
        String js = "(function(){"
                + "var m=document.getElementById('modal'),mn=document.getElementById('menu');"
                + "if((m&&!m.hidden)||(mn&&!mn.hidden)){document.dispatchEvent(new KeyboardEvent('keydown',{key:'Escape',bubbles:true}));return 'handled';}"
                + "if(document.body.dataset.view!=='home'){var h=document.querySelector('.bnav [data-view=\"home\"]');if(h){h.click();return 'handled';}}"
                + "return 'exit';})()";
        web.evaluateJavascript(js, new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String value) {
                if (value == null || !value.contains("handled")) {
                    finish();
                }
            }
        });
    }

    private class Bridge {
        @JavascriptInterface
        public void shareBackup(final String name, final String json) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Intent send = new Intent(Intent.ACTION_SEND);
                    send.setType("text/plain");
                    send.putExtra(Intent.EXTRA_SUBJECT, name);
                    send.putExtra(Intent.EXTRA_TEXT, json);
                    startActivity(Intent.createChooser(send, "Save backup to"));
                }
            });
        }
    }
}
