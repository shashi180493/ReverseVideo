package com.loopbots.reversevideo.change_language.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopbots.reversevideo.R;
import com.loopbots.reversevideo.change_language.models.All_Language;
import com.loopbots.reversevideo.general.Splash;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by Shashi on 6/24/2017.
 */

public class Languages_Adapter extends BaseAdapter {

    Activity activity;
    Context context;
    ArrayList<All_Language> Language_Arraylist_main;
    public static String id = "";
    String code = "";

    public Languages_Adapter(Context context, ArrayList<All_Language> Language_Arraylist, Activity activity) {
        this.context = context;
        this.activity = activity;
        Language_Arraylist_main = Language_Arraylist;
    }

    @Override
    public int getCount() {
        return Language_Arraylist_main.size();
    }

    @Override
    public Object getItem(int position) {
        return Language_Arraylist_main.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.cust_language_list_item, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.tv_language_name.setText(Language_Arraylist_main.get(position).getName());
        viewHolder.iv_flag.setBackgroundResource(Language_Arraylist_main.get(position).getImage());

        if (Language_Arraylist_main.get(position).getIs_selected().equals("true")) {
            viewHolder.iv_is_selected.setVisibility(View.VISIBLE);
        } else {
            viewHolder.iv_is_selected.setVisibility(View.GONE);
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                builder1.setMessage(context.getString(R.string.change_language_confirmation));
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        context.getString(R.string.btn_yes),
                        new DialogInterface.OnClickListener() {

                            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
                            public void onClick(DialogInterface dialog, int id) {

                                try {
                                    String language = Language_Arraylist_main.get(position).getName();
                                    if (language.equals(context.getString(R.string.english))) {
                                        code = "en";
                                    } else if (language.equals(context.getString(R.string.japanese))) {
                                        code = "ja";
                                    } else if (language.equals(context.getString(R.string.korean))) {
                                        code = "ko";
                                    } else if (language.equals(context.getString(R.string.chinesesimp))) {
                                        code = "zh-Hans";
                                    } else if (language.equals(context.getString(R.string.chinesetrad))) {
                                        code = "zh-Hant";
                                    } else if (language.equals(context.getString(R.string.german))) {
                                        code = "de";
                                    } else if (language.equals(context.getString(R.string.french))) {
                                        code = "fr";
                                    } else if (language.equals(context.getString(R.string.spanish))) {
                                        code = "es";
                                    } else if (language.equals(context.getString(R.string.italian))) {
                                        code = "it";
                                    } else if (language.equals(context.getString(R.string.portuguese))) {
                                        code = "pt";
                                    } else if (language.equals(context.getString(R.string.russian))) {
                                        code = "ru";
                                    } else if (language.equals(context.getString(R.string.arabic))) {
                                        code = "ar";
                                    } else {
                                        code = "en";
                                    }

                                    //viewHolder.iv_flag.setBackgroundResource(R.drawable.id);

                                    SharedPreferences settings = context.getSharedPreferences("Language", context.MODE_PRIVATE);
                                    SharedPreferences.Editor spe = settings.edit();
                                    spe.putString("language", code);
                                    spe.commit();
                                    setLocale(code);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                dialog.cancel();
                            }
                        });

                builder1.setNegativeButton(
                        context.getString(R.string.btn_no),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();
            }
        });

        return convertView;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void setLocale(String lang) {

        Resources res = context.getResources();
        Configuration conf = res.getConfiguration();

        if (lang.equals("zh-Hans")) {
            conf.setLocale(Locale.SIMPLIFIED_CHINESE);
            res.updateConfiguration(conf, res.getDisplayMetrics());
        } else if (lang.equals("zh-Hant")) {
            conf.setLocale(Locale.TRADITIONAL_CHINESE);
            res.updateConfiguration(conf, res.getDisplayMetrics());
        } else {
            DisplayMetrics dm = res.getDisplayMetrics();
            conf.setLocale(new Locale(code.toLowerCase()));
            // Log.d("conf.getLocales()",""+conf.getLocales());
            res.updateConfiguration(conf, res.getDisplayMetrics());
        }

        Intent intent = new Intent(context, Splash.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        context.startActivity(intent);

    }

    private class ViewHolder {
        ImageView iv_flag, iv_is_selected;
        TextView tv_language_name;

        public ViewHolder(View view) {

            iv_flag = (ImageView) view.findViewById(R.id.iv_flag);
            iv_is_selected = (ImageView) view.findViewById(R.id.iv_is_selected);
            tv_language_name = (TextView) view.findViewById(R.id.tv_language_name);
        }
    }
}
