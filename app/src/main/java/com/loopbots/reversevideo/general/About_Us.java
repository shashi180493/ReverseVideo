package com.loopbots.reversevideo.general;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.loopbots.reversevideo.R;
import com.loopbots.reversevideo.utility.GlobalFunction;
import com.loopbots.reversevideo.utility.SharedPreferenceManager;

/**
 * Created by shashi on 19/7/16.
 */
public class About_Us extends AppCompatActivity implements BillingProcessor.IBillingHandler {

    private CollapsingToolbarLayout collapsingToolbarLayout = null;
    TextView tv_version, tv_copyright, tv_rate, tv_more_from_us, tv_share, tv_removeadds, tv_feedback, tv_price_ad_unlimited;
    ImageView iv_logo;
    RelativeLayout ll_remove_ads;

    BillingProcessor billingProcessor;

    String version_name = "";

    SharedPreferenceManager sharedPreferenceManager;
    GlobalFunction globalFunction;

    Typeface tf;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setTheme(R.style.AppTheme1);

        sharedPreferenceManager = new SharedPreferenceManager(this);
        globalFunction = new GlobalFunction(this);

        if (globalFunction.isConnectingToInternet()) {
            billingProcessor = new BillingProcessor(About_Us.this, getResources().getString(R.string.base64), this);
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
        }

        setContentView(R.layout.about_us);

        tv_version = (TextView) findViewById(R.id.tv_version);
        tv_copyright = (TextView) findViewById(R.id.tv_copyright);
        tv_more_from_us = (TextView) findViewById(R.id.btn_more_from_us);
        tv_rate = (TextView) findViewById(R.id.btn_rate);
        tv_share = (TextView) findViewById(R.id.btn_share);
        tv_removeadds = (TextView) findViewById(R.id.btn_removeadds);
        tv_feedback = (TextView) findViewById(R.id.btn_feedback);
        iv_logo = (ImageView) findViewById(R.id.iv_loopbots);
        tv_price_ad_unlimited = (TextView) findViewById(R.id.price_ad_unlimited);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        ll_remove_ads = (RelativeLayout) findViewById(R.id.ll_remove_ads);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back);

        tv_version.setTypeface(tf);
        tv_copyright.setTypeface(tf);
        tv_more_from_us.setTypeface(tf);
        tv_rate.setTypeface(tf);
        tv_share.setTypeface(tf);
        tv_removeadds.setTypeface(tf);
        tv_feedback.setTypeface(tf);
        tv_price_ad_unlimited.setTypeface(tf);

        try {
            version_name = getPackageManager().getPackageInfo(getApplicationContext().getPackageName(), 0).versionName;
            tv_version.setText(getString(R.string.version) + " " + version_name);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        dynamicToolbarColor();
        toolbarTextAppernce();

        if (sharedPreferenceManager.get_Remove_Ad()) {
            tv_price_ad_unlimited.setText(getString(R.string.purchased));
            tv_price_ad_unlimited.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.green));
        }

        ll_remove_ads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (globalFunction.isConnectingToInternet()) {
                    if (billingProcessor != null) {
                        billingProcessor.purchase(About_Us.this, "remove_ad");
                    }

                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                }

            }
        });

        tv_more_from_us.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (globalFunction.isConnectingToInternet()) {
                    Intent intent = new Intent(getApplicationContext(), More_From_Us.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                }

            }
        });

        tv_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                globalFunction.share_app();
            }
        });

        tv_rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                globalFunction.rate_App();
            }
        });

        tv_feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                globalFunction.send_Feedback();
            }
        });

    }

    private void dynamicToolbarColor() {

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
                R.mipmap.ic_launcher);
        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(Palette palette) {
                collapsingToolbarLayout.setContentScrimColor(getResources().getColor(R.color.colorPrimary));
                collapsingToolbarLayout.setStatusBarScrimColor(getResources().getColor(R.color.colorPrimaryDark));
            }
        });
    }

    private void toolbarTextAppernce() {
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.collapsedappbar);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.expandedappbar);
    }

    @Override
    public void onProductPurchased(String s, TransactionDetails transactionDetails) {

        if (transactionDetails.productId.equals("remove_ad")) {
            sharedPreferenceManager.set_Remove_Ad(true);
            tv_price_ad_unlimited.setText(getString(R.string.purchased));
            tv_price_ad_unlimited.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.green));
        }
    }

    @Override
    public void onPurchaseHistoryRestored() {
    }

    @Override
    public void onBillingError(int i, Throwable throwable) {
    }

    @Override
    public void onBillingInitialized() {

        if (billingProcessor.isPurchased("remove_ad")) {
            sharedPreferenceManager.set_Remove_Ad(true);
            tv_price_ad_unlimited.setText(getString(R.string.purchased));
            tv_price_ad_unlimited.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.green));
        } else {
            tv_price_ad_unlimited.setText(billingProcessor.getPurchaseListingDetails("remove_ad").priceText);
            tv_price_ad_unlimited.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!billingProcessor.handleActivityResult(requestCode, resultCode, data))
            super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        if (billingProcessor != null)
            billingProcessor.release();
        super.onDestroy();
    }

    public void onBackPressed() {
        Log.d("backpress", "backpress");
        finish();
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}