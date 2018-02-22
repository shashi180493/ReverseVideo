package com.loopbots.reversevideo.general;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.multidex.MultiDex;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.loopbots.reversevideo.R;

import java.util.List;

/**
 * Created by shashi on 11/1/18.
 */

public class MyApplication extends Application implements Application.ActivityLifecycleCallbacks{

    String TAG = MyApplication.class.getSimpleName();
    public static InterstitialAd interstitial;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        registerActivityLifecycleCallbacks(this);
        interstitial = new InterstitialAd(MyApplication.this);
        interstitial.setAdUnitId(getResources().getString(R.string.intertial_ad));

        loadIntertitialAd();
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {
        /*try {
            boolean foreground = new ForegroundCheckTask().execute(getApplicationContext()).get();
            System.out.println("forground===" + foreground);
            if (!foreground) {
                Intent i = new Intent(this, NotificationBroadcastReciever.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, i, PendingIntent.FLAG_CANCEL_CURRENT);
                AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                //   am.setInexactRepeating(AlarmManager.RTC_WAKEUP, 0, 1 * 1000, pi);  *//* start Service every one milisec. *//*
                Calendar calendar = Calendar.getInstance();
                if (Build.VERSION.SDK_INT >= 23) {
                    am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() + am.INTERVAL_DAY * 7, pendingIntent);
                } else if (Build.VERSION.SDK_INT >= 19) {
                    am.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() + am.INTERVAL_DAY * 7, pendingIntent);
                } else if (Build.VERSION.SDK_INT >= 16) {
                    am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() + am.INTERVAL_DAY * 7, calendar.getTimeInMillis() + am.INTERVAL_DAY * 7, pendingIntent);
                }
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }*/
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }

    class ForegroundCheckTask extends AsyncTask<Context, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Context... params) {
            final Context context = params[0];
            return isAppOnForeground(context);
        }

        private boolean isAppOnForeground(Context context) {
            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
            if (appProcesses == null) {
                return false;
            }
            final String packageName = context.getPackageName();
            for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
                if (appProcess.importance ==
                        ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
                        && appProcess.processName.equals(packageName)) {
                    return true;
                }
            }
            return false;
        }
    }

    public static void loadIntertitialAd() {
        interstitial.loadAd(new AdRequest.Builder().build());
    }
}
