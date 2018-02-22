package com.loopbots.reversevideo.home;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.PopupMenu;
import android.util.DisplayMetrics;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.loopbots.reversevideo.R;
import com.loopbots.reversevideo.Video_Preview;
import com.loopbots.reversevideo.change_language.Change_Language;
import com.loopbots.reversevideo.general.About_Us;
import com.loopbots.reversevideo.general.MyApplication;
import com.loopbots.reversevideo.utility.Constants;
import com.loopbots.reversevideo.utility.GlobalFunction;
import com.loopbots.reversevideo.utility.SharedPreferenceManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shashi on 10/1/18.
 */

public class Homepage extends Activity {

    RelativeLayout rl_actionbar, rl_select_video,rl_capture_video,rl_extracted_image, rl_menu;
    TextView tv_title;
    ImageView iv_more;

    AdView adView;

    SharedPreferenceManager sharedPreferenceManager;
    GlobalFunction globalFunction;

    int action = 0;
    String TAG = Homepage.class.getSimpleName();
    String file_path = "";



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferenceManager = new SharedPreferenceManager(this);
        globalFunction = new GlobalFunction(this);

        setContentView(R.layout.home);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        rl_actionbar = (RelativeLayout)findViewById(R.id.rl_actionbar);
        rl_select_video = (RelativeLayout)findViewById(R.id.rl_select_video);
        rl_capture_video = (RelativeLayout)findViewById(R.id.rl_capture_video);
        rl_extracted_image = (RelativeLayout)findViewById(R.id.rl_extracted_image);
        rl_menu = (RelativeLayout)findViewById(R.id.rl_menu);

        tv_title = (TextView) findViewById(R.id.tv_title);

        iv_more = (ImageView) findViewById(R.id.iv_more);

        adView = (AdView) findViewById(R.id.adView);

        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / 2;
        float diff = (dpWidth * 35) / 100;
        dpWidth = dpWidth - diff;

        rl_select_video.getLayoutParams().height = Math.round(dpWidth);
        rl_select_video.getLayoutParams().width = Math.round(dpWidth);
        rl_select_video.requestLayout();

        rl_capture_video.getLayoutParams().height = Math.round(dpWidth);
        rl_capture_video.getLayoutParams().width = Math.round(dpWidth);
        rl_capture_video.requestLayout();

        rl_extracted_image.getLayoutParams().height = Math.round(dpWidth);
        rl_extracted_image.getLayoutParams().width = Math.round(dpWidth);
        rl_extracted_image.requestLayout();

        if (sharedPreferenceManager.get_Remove_Ad()) {
            adView.setVisibility(View.GONE);
        } else {
            adView.loadAd(new AdRequest.Builder().build());
        }

        rl_select_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT >= 23)
                    getPermission();
                else
                    uploadVideo();

            }
        });

        rl_capture_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakeVideoIntent();
            }
        });

        rl_extracted_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        iv_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup(rl_menu);
            }
        });

        if (!sharedPreferenceManager.get_Remove_Ad()) {
            MyApplication.interstitial.setAdListener(new AdListener(){
                @Override
                public void onAdClosed() {
                    super.onAdClosed();
                    MyApplication.loadIntertitialAd();
                    if (action == 1) {
                        Intent intent = new Intent(Homepage.this, Video_Preview.class);
                        intent.putExtra("file_path", file_path);
                        startActivity(intent);
                    } else if (action == 2) {
                        Intent intent = new Intent(Homepage.this, About_Us.class);
                        startActivity(intent);
                    } else if (action == 3) {
                        Intent intent = new Intent(Homepage.this, Change_Language.class);
                        startActivity(intent);
                    }
                }

                @Override
                public void onAdFailedToLoad(int i) {
                    super.onAdFailedToLoad(i);
                    MyApplication.loadIntertitialAd();
                }
            });
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_CODE_selectvideo && data != null) {
            action = 1;
            file_path = data.getData().toString();

            if (sharedPreferenceManager.get_Remove_Ad()) {
                Intent intent = new Intent(Homepage.this, Video_Preview.class);
                intent.putExtra("file_path", file_path);
                startActivity(intent);
            } else {
                if (MyApplication.interstitial != null && MyApplication.interstitial.isLoaded()) {
                    MyApplication.interstitial.show();
                } else {
                    if (!MyApplication.interstitial.isLoading()) {
                        MyApplication.loadIntertitialAd();
                    }
                    Intent intent = new Intent(Homepage.this, Video_Preview.class);
                    intent.putExtra("file_path", file_path);
                    startActivity(intent);
                }
            }

        } else if (requestCode == Constants.REQUEST_CODE_recordvideo && data != null) {

        } else if (requestCode == Constants.REQUEST_VIDEO_CAPTURE && data != null) {

            action = 1;
            file_path = data.getData().toString();

            if (sharedPreferenceManager.get_Remove_Ad()) {
                Intent intent = new Intent(Homepage.this, Video_Preview.class);
                intent.putExtra("file_path", file_path);
                startActivity(intent);
            } else {
                if (MyApplication.interstitial != null && MyApplication.interstitial.isLoaded()) {
                    MyApplication.interstitial.show();
                } else {
                    if (!MyApplication.interstitial.isLoading()) {
                        MyApplication.loadIntertitialAd();
                    }
                    Intent intent = new Intent(Homepage.this, Video_Preview.class);
                    intent.putExtra("file_path", file_path);
                    startActivity(intent);
                }
            }
        }
    }

    @SuppressLint("RestrictedApi")
    public void showPopup(View v) {
        Context wrapper = new ContextThemeWrapper(this, R.style.CustomPopupTheme);
        PopupMenu popup = new PopupMenu(wrapper, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_main, popup.getMenu());
        popup.show();
    }

    public void open_About_Us(MenuItem item) {
        action = 2;
        if (sharedPreferenceManager.get_Remove_Ad()) {
            Intent intent = new Intent(Homepage.this, About_Us.class);
            startActivity(intent);
        } else {
            if (MyApplication.interstitial != null && MyApplication.interstitial.isLoaded()) {
                MyApplication.interstitial.show();
            } else {
                if (!MyApplication.interstitial.isLoading()) {
                    MyApplication.loadIntertitialAd();
                }
                Intent intent = new Intent(Homepage.this, About_Us.class);
                startActivity(intent);
            }
        }
    }

    public void open_Change_Langauge(MenuItem item) {
        action = 3;
        if (sharedPreferenceManager.get_Remove_Ad()) {
            Intent intent = new Intent(Homepage.this, Change_Language.class);
            startActivity(intent);
        } else {
            if (MyApplication.interstitial != null && MyApplication.interstitial.isLoaded()) {
                MyApplication.interstitial.show();
            } else {
                if (!MyApplication.interstitial.isLoading()) {
                    MyApplication.loadIntertitialAd();
                }
                Intent intent = new Intent(Homepage.this, Change_Language.class);
                startActivity(intent);
            }
        }
    }

    private void getPermission() {
        String[] params = null;
        String writeExternalStorage = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        String readExternalStorage = Manifest.permission.READ_EXTERNAL_STORAGE;

        int hasWriteExternalStoragePermission = ActivityCompat.checkSelfPermission(this, writeExternalStorage);
        int hasReadExternalStoragePermission = ActivityCompat.checkSelfPermission(this, readExternalStorage);
        List<String> permissions = new ArrayList<String>();

        if (hasWriteExternalStoragePermission != PackageManager.PERMISSION_GRANTED)
            permissions.add(writeExternalStorage);
        if (hasReadExternalStoragePermission != PackageManager.PERMISSION_GRANTED)
            permissions.add(readExternalStorage);

        if (!permissions.isEmpty()) {
            params = permissions.toArray(new String[permissions.size()]);
        }
        if (params != null && params.length > 0) {
            ActivityCompat.requestPermissions(Homepage.this,
                    params,
                    100);
        } else
            uploadVideo();
    }

    private void uploadVideo() {
        try {
            Intent intent = new Intent();
            intent.setType("video/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Video"),
                    Constants.REQUEST_CODE_selectvideo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void dispatchTakeVideoIntent() {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takeVideoIntent, Constants.REQUEST_VIDEO_CAPTURE);
        }
    }
}
