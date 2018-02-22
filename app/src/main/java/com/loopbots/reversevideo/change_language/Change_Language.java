package com.loopbots.reversevideo.change_language;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loopbots.reversevideo.R;
import com.loopbots.reversevideo.change_language.adapters.Languages_Adapter;
import com.loopbots.reversevideo.change_language.models.All_Language;

import java.util.ArrayList;

/**
 * Created by shashi on 6/24/2017.
 */

public class Change_Language extends Activity {

    RelativeLayout rl_actionbar;
    TextView tv_title;
    ImageView iv_back, iv_more;
    ListView lv_languages;
    Languages_Adapter languages_adapter;
    ArrayList<All_Language> languages = new ArrayList<All_Language>();
    Toolbar toolbar;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_language);

        rl_actionbar = (RelativeLayout) findViewById(R.id.rl_actionbar);

        iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_more = (ImageView) findViewById(R.id.iv_more);

        tv_title = (TextView) findViewById(R.id.tv_title);

        rl_actionbar.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
        tv_title.setText(getResources().getString(R.string.change_language));
        iv_back.setVisibility(View.VISIBLE);
        iv_more.setVisibility(View.GONE);

        int[] flags = {R.drawable.uk,
                R.drawable.jp,
                R.drawable.kr,
                R.drawable.cn,
                R.drawable.tw,
                R.drawable.de,
                R.drawable.fr,
                R.drawable.es,
                R.drawable.it,
                R.drawable.pt,
                R.drawable.ru,
                R.drawable.ae};

        String[] names = {getResources().getString(R.string.english),
                getResources().getString(R.string.japanese),
                getResources().getString(R.string.korean),
                getResources().getString(R.string.chinesesimp),
                getResources().getString(R.string.chinesetrad),
                getResources().getString(R.string.german),
                getResources().getString(R.string.french),
                getResources().getString(R.string.spanish),
                getResources().getString(R.string.italian),
                getResources().getString(R.string.portuguese),
                getResources().getString(R.string.russian),
                getResources().getString(R.string.arabic)};

        String[] alpha2 = {"en",
                "ja",
                "ko",
                "zh-Hans",
                "zh-Hant",
                "de",
                "fr",
                "es",
                "it",
                "pt",
                "ru",
                "ar"};

        lv_languages = (ListView) findViewById(R.id.lv_languages);
        SharedPreferences sp_settings = getSharedPreferences("Language", MODE_PRIVATE);

        for (int i = 0; i < names.length; i++) {
            if (sp_settings.getString("language", "").equals(alpha2[i])) {
                languages.add(new All_Language(names[i], flags[i], alpha2[i], "true"));
            } else {
                languages.add(new All_Language(names[i], flags[i], alpha2[i], "false"));
            }
        }

        languages_adapter = new Languages_Adapter(Change_Language.this, languages, Change_Language.this);
        lv_languages.setAdapter(languages_adapter);
        languages_adapter.notifyDataSetChanged();

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
