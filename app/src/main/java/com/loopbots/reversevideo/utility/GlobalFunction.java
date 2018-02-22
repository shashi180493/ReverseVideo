package com.loopbots.reversevideo.utility;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.loopbots.reversevideo.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Created by Shashikant Patel on 22/9/17.
 */

public class GlobalFunction {

    Activity activity;
    FirebaseAnalytics mFirebaseAnalytics;
    SharedPreferenceManager sharedPreferenceManager;
    Dialog dialog_progress;

    public GlobalFunction(Activity activity) {
        this.activity = activity;
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(activity);
        sharedPreferenceManager = new SharedPreferenceManager(activity);
    }

    public void showToast(String message) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
    }

    public void hideKeyboard(View view) {
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void sendAnalyticsData(String name, String text) {
        Bundle params = new Bundle();
        params.putString("Screen_Name", name);
        params.putString("Text", text);
        mFirebaseAnalytics.logEvent(name+"_logevent", params);
    }

    public void set_locale_language(){

        String[] lang_list = { "en", "  ja", "ko", "it", "zh-Hans", "zh-Hant", "de", "fr", "es", "it", "pt", "ru", "ar" ,"zh"};
        String default_lang = "", app_language = "", prev_phone_lang = "";

        default_lang= Locale.getDefault().getLanguage();
        String default_value = Locale.getDefault().toString();
        app_language = sharedPreferenceManager.get_Language();
        prev_phone_lang = sharedPreferenceManager.get_Prev_Phone_Language();

        if (app_language.equals(""))
        {
            if (!default_lang.equals(prev_phone_lang))
            {
                if (Arrays.asList(lang_list).contains(default_lang))
                {
                    sharedPreferenceManager.set_Prev_Phone_Language(default_lang);
                    setLocale(default_lang,default_value);
                }
                else
                {
                    setLocale("en",default_value);
                }

            }
            else
            {
                setLocale(prev_phone_lang,default_value);
            }
        }
        else
        {
            if (default_lang.equals(app_language))
            {
                setLocale(app_language,default_value);
            }
            else if (default_lang.equals(prev_phone_lang))
            {
                if (Arrays.asList(lang_list).contains(default_lang))
                {
                    setLocale(app_language,default_value);
                }
                else
                {
                    setLocale("en",default_value);
                }

            }
            else if (!default_lang.equals(prev_phone_lang))
            {
                if (Arrays.asList(lang_list).contains(default_lang))
                {
                    sharedPreferenceManager.set_Prev_Phone_Language(default_lang);
                    setLocale(default_lang,default_value);
                }
                else
                {
                    setLocale("en",default_value);
                }
            }
        }
    }

    public void setLocale(String lang,String default_val) {

        String[] lang_list = { "en", "ja", "ko", "it", "zh-Hans", "zh-Hant", "de", "fr", "es", "it", "pt", "ru", "ar" ,"zh"};

        if (default_val.trim().equalsIgnoreCase("zh_CN"))
        {
            sharedPreferenceManager.set_Language("zh-Hans");
        }
        else if (default_val.trim().equalsIgnoreCase("zh_TW"))
        {
            sharedPreferenceManager.set_Language("zh-Hant");
        }
        else
        {
            if (Arrays.asList(lang_list).contains(lang))
            {
                sharedPreferenceManager.set_Language(lang);
            }
            else
            {
                sharedPreferenceManager.set_Language("en");
            }
        }

        Resources res = activity.getResources();
        Configuration conf = res.getConfiguration();

        if (lang.equals("zh"))
        {

            if (default_val.trim().equalsIgnoreCase("zh_CN"))
            {
                conf.setLocale(Locale.SIMPLIFIED_CHINESE);
                res.updateConfiguration(conf, res.getDisplayMetrics());
            }
            else if (default_val.trim().equalsIgnoreCase("zh_TW"))
            {
                conf.setLocale(Locale.TRADITIONAL_CHINESE);
                res.updateConfiguration(conf, res.getDisplayMetrics());
            }
            else
            {
                conf.setLocale(new Locale(lang.toLowerCase()));
                res.updateConfiguration(conf, res.getDisplayMetrics());
            }

        }
        else
        {
            if (Build.VERSION.SDK_INT >= 17)
            {
                if (lang.equals("zh-Hans"))
                {
                    conf.setLocale(Locale.SIMPLIFIED_CHINESE);
                    res.updateConfiguration(conf, res.getDisplayMetrics());
                }
                else if (lang.equals("zh-Hant"))
                {
                    conf.setLocale(Locale.TRADITIONAL_CHINESE);
                    res.updateConfiguration(conf, res.getDisplayMetrics());
                }
                else
                {
                    conf.setLocale(new Locale(lang.toLowerCase()));
                    res.updateConfiguration(conf, res.getDisplayMetrics());
                }
            }
        }
    }

    public boolean isConnectingToInternet()
    {
        ConnectivityManager connectivity = (ConnectivityManager)
                activity.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivity != null)
        {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();

            if (info != null)

                for (int i = 0; i < info.length; i++)

                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        return true;
                    }

        }
        return false;
    }

    public void share_app() {
        List<Intent> targetedShareIntents = new ArrayList<Intent>();
        Intent sharingIntent = new Intent(
                Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareBody = activity.getString(R.string.invite_friends_share_other_message) +
                activity.getPackageName();

        PackageManager pm = activity.getPackageManager();
        List<ResolveInfo> activityList = pm.queryIntentActivities(
                sharingIntent, 0);
        for (final ResolveInfo app : activityList) {

            String packageName = app.activityInfo.packageName;
            Intent targetedShareIntent = new Intent(
                    Intent.ACTION_SEND);
            targetedShareIntent.setType("text/plain");
            targetedShareIntent.putExtra(
                    Intent.EXTRA_SUBJECT, "share");
            if (TextUtils.equals(packageName, "com.facebook.katana")) {
                targetedShareIntent.putExtra(
                        Intent.EXTRA_TEXT,
                        shareBody);
            } else {
                targetedShareIntent.putExtra(
                        Intent.EXTRA_TEXT, shareBody);
            }

            targetedShareIntent.setPackage(packageName);
            targetedShareIntents.add(targetedShareIntent);

        }

        Intent chooserIntent = Intent.createChooser(
                targetedShareIntents.remove(0), "Share Via");

        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS,
                targetedShareIntents.toArray(new Parcelable[]{}));
        activity.startActivity(chooserIntent);
    }

    public void rate_App() {
        try {
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri
                    .parse("market://details?id="
                            + activity.getPackageName())));
        } catch (android.content.ActivityNotFoundException anfe) {
            activity.startActivity(new Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id="
                            + activity.getPackageName())));
        }
    }

    public void send_Feedback() {
        String version_name = "";
        try {
            version_name = activity.getPackageManager().
                    getPackageInfo(activity.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", "contact@loopbots.com", null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, activity.getString(R.string.feedback_appname) +
                " (v" + version_name + ")");
        emailIntent.putExtra(Intent.EXTRA_TEXT, activity.getString(R.string.give_feedback));
        activity.startActivity(Intent.createChooser(emailIntent, "Send email..."));
    }

    public void init_progress_dialog()
    {
        dialog_progress = new Dialog(activity);
        dialog_progress.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog_progress.setContentView(R.layout.dialog_progressbar);

        // dialog_progress.setCancelable(false);
        dialog_progress.setCanceledOnTouchOutside(false);

        Window window = dialog_progress.getWindow();
        window.setBackgroundDrawableResource(android.R.color.transparent);
        dialog_progress.getWindow().setDimAmount(0);


        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog_progress.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;

        dialog_progress.getWindow().setAttributes(lp);

        final ProgressBar pb_progress = (ProgressBar) dialog_progress.findViewById(R.id.pb_progress);
        final TextView tv_progress = (TextView) dialog_progress.findViewById(R.id.tv_progress);

    }

    public void show_progress_dialog()
    {
        if (dialog_progress != null)
        {
            if (!dialog_progress.isShowing())
            {
                dialog_progress.show();
            }
        }
    }

    public void hide_progress_dialog()
    {
        if (dialog_progress != null)
        {
            if (dialog_progress.isShowing())
            {
                dialog_progress.dismiss();
            }
        }
    }
}
