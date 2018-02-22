package com.loopbots.reversevideo.general;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.google.android.gms.ads.AdListener;
import com.loopbots.reversevideo.R;
import com.loopbots.reversevideo.home.Homepage;
import com.loopbots.reversevideo.utility.Constants;
import com.loopbots.reversevideo.utility.GlobalFunction;
import com.loopbots.reversevideo.utility.SharedPreferenceManager;

/**
 * Created by shashi on 10/1/18.
 */

public class Splash extends Activity implements BillingProcessor.IBillingHandler{

    int PERMISSION_ALL = 1;

    String[] PERMISSIONS = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE};

    BillingProcessor billingProcessor;
    GlobalFunction globalFunction;
    SharedPreferenceManager sharedPreferenceManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        globalFunction = new GlobalFunction(this);
        sharedPreferenceManager = new SharedPreferenceManager(this);
        billingProcessor = new BillingProcessor(Splash.this,
                getResources().getString(R.string.base64), this);

        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (sharedPreferenceManager.get_Remove_Ad()) {
                    Intent intent = new Intent(Splash.this, Homepage.class);
                    startActivity(intent);
                    finish();
                } else {
                    if (MyApplication.interstitial != null && MyApplication.interstitial.isLoaded()) {
                        MyApplication.interstitial.show();
                    } else {
                        if (!MyApplication.interstitial.isLoading()) {
                            MyApplication.loadIntertitialAd();
                        }
                        Intent intent = new Intent(Splash.this, Homepage.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }
        }, Constants.splash_time);

        if (!sharedPreferenceManager.get_Remove_Ad()) {
            MyApplication.interstitial.setAdListener(new AdListener(){
                @Override
                public void onAdClosed() {
                    super.onAdClosed();
                    MyApplication.loadIntertitialAd();
                    Intent intent = new Intent(Splash.this, Homepage.class);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onAdFailedToLoad(int i) {
                    super.onAdFailedToLoad(i);
                    MyApplication.loadIntertitialAd();
                }
            });
        }
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onProductPurchased(@NonNull String productId, @Nullable TransactionDetails details) {
        if (productId.equals("remove_ad")) {
            sharedPreferenceManager.set_Remove_Ad(true);
        }
    }

    @Override
    public void onPurchaseHistoryRestored() {

    }

    @Override
    public void onBillingError(int errorCode, @Nullable Throwable error) {

    }

    @Override
    public void onBillingInitialized() {
        if (billingProcessor.isPurchased("remove_ad")) {
            sharedPreferenceManager.set_Remove_Ad(true);
        }
    }
}
