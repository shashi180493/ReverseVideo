package com.loopbots.reversevideo.utility;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Shashikant Patel on 22/9/17.
 */

public class SharedPreferenceManager {

    Context context;
    String TAG = SharedPreferenceManager.class.getSimpleName();

    public SharedPreferenceManager(Context context) {
        super();
        this.context = context;
    }

    public void clear_user_data() {
        SharedPreferences sp_loopbots;
        SharedPreferences.Editor sp_loopbots_editor;

        sp_loopbots = context.getSharedPreferences(Constants.Loopbots_sharedpreference,
                Context.MODE_PRIVATE);
        sp_loopbots_editor = sp_loopbots.edit();

        sp_loopbots_editor.clear();
        sp_loopbots_editor.apply();
    }

    public void set_Remove_Ad(Boolean remove_ad) {
        SharedPreferences sp_loopbots;
        SharedPreferences.Editor sp_loopbots_editor;

        sp_loopbots = context.getSharedPreferences(Constants.Loopbots_sharedpreference,
                Context.MODE_PRIVATE);
        sp_loopbots_editor = sp_loopbots.edit();

        sp_loopbots_editor.putBoolean(Constants.KEY_remove_ad, remove_ad);
        sp_loopbots_editor.apply();
    }

    public void set_Language(String language) {
        SharedPreferences sp_loopbots;
        SharedPreferences.Editor sp_loopbots_editor;

        sp_loopbots = context.getSharedPreferences(Constants.Loopbots_sharedpreference,
                Context.MODE_PRIVATE);
        sp_loopbots_editor = sp_loopbots.edit();

        sp_loopbots_editor.putString(Constants.KEY_language, language);
        sp_loopbots_editor.apply();
    }

    public void set_Prev_Phone_Language(String prev_phone_language) {
        SharedPreferences sp_loopbots;
        SharedPreferences.Editor sp_loopbots_editor;

        sp_loopbots = context.getSharedPreferences(Constants.Loopbots_sharedpreference,
                Context.MODE_PRIVATE);
        sp_loopbots_editor = sp_loopbots.edit();

        sp_loopbots_editor.putString(Constants.KEY_prev_phone_lang, prev_phone_language);
        sp_loopbots_editor.apply();
    }

    public Boolean get_Remove_Ad() {
        SharedPreferences sp_loopbots;

        sp_loopbots = context.getSharedPreferences(Constants.Loopbots_sharedpreference,
                Context.MODE_PRIVATE);

        return sp_loopbots.getBoolean(Constants.KEY_remove_ad, false);
    }

    public String get_Language() {
        SharedPreferences sp_loopbots;

        sp_loopbots = context.getSharedPreferences(Constants.Loopbots_sharedpreference,
                Context.MODE_PRIVATE);

        return sp_loopbots.getString(Constants.KEY_language, "en");
    }

    public String get_Prev_Phone_Language() {
        SharedPreferences sp_loopbots;

        sp_loopbots = context.getSharedPreferences(Constants.Loopbots_sharedpreference,
                Context.MODE_PRIVATE);

        return sp_loopbots.getString(Constants.KEY_prev_phone_lang, "en");
    }
}
