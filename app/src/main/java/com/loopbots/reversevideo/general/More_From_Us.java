package com.loopbots.reversevideo.general;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.Toast;

import com.loopbots.reversevideo.R;

/**
 * Created by shashi on 3/19/2016.
 */
public class More_From_Us extends Activity {
    WebView webview;
    ProgressDialog pdia;
    ImageView imageView_actionbar_left;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.more_from_us);

        pdia = new ProgressDialog(this);

        imageView_actionbar_left = (ImageView) findViewById(R.id.imageView_actionbar_left);
        webview = (WebView) findViewById(R.id.webview);

        pdia.setCanceledOnTouchOutside(false);
        pdia.setCancelable(false);
        pdia.setMessage(getString(R.string.loading));

        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setAppCacheEnabled(false);
        webview.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webview.loadUrl("http://moreapps.loopbots.com/android.html");

        webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                    return true;
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), getString(R.string.install_play_store),
                            Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                    return false;
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                pdia.dismiss();
            }
        });

        imageView_actionbar_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}
