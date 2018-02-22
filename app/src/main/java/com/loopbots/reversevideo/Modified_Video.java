package com.loopbots.reversevideo;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.loopbots.reversevideo.utility.GlobalFunction;
import com.loopbots.reversevideo.utility.SharedPreferenceManager;

import org.florescu.android.rangeseekbar.RangeSeekBar;

/**
 * Created by shashi on 11/1/18.
 */

public class Modified_Video extends Activity {

    RelativeLayout rl_actionbar;
    ImageView iv_back, iv_more;
    TextView tv_title;
    VideoView vv_video;
    AdView adView;

    SharedPreferenceManager sharedPreferenceManager;
    GlobalFunction globalFunction;

    String TAG = Modified_Video.class.getSimpleName();
    Uri selectedVideoUri;

    Bundle bundle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.modified_video);

        sharedPreferenceManager= new SharedPreferenceManager(this);
        globalFunction = new GlobalFunction(this);

        rl_actionbar = (RelativeLayout) findViewById(R.id.rl_actionbar);

        iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_more = (ImageView) findViewById(R.id.iv_more);

        tv_title = (TextView) findViewById(R.id.tv_title);

        vv_video = (VideoView) findViewById(R.id.vv_video);

        adView = (AdView) findViewById(R.id.adView);

        iv_back.setVisibility(View.VISIBLE);
        iv_more.setVisibility(View.GONE);

        bundle = getIntent().getExtras();

        if (bundle != null) {
            selectedVideoUri = Uri.parse(bundle.getString("file_path", ""));
            vv_video.setVideoURI(selectedVideoUri);
            vv_video.start();

            Log.d(TAG, "selectedVideoUri->" + selectedVideoUri);
        }

        if (sharedPreferenceManager.get_Remove_Ad()) {
            adView.setVisibility(View.GONE);
        } else {
            adView.loadAd(new AdRequest.Builder().build());
        }

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        vv_video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
