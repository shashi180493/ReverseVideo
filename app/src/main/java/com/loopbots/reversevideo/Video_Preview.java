package com.loopbots.reversevideo;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.loopbots.reversevideo.general.MyApplication;
import com.loopbots.reversevideo.utility.GlobalFunction;
import com.loopbots.reversevideo.utility.SharedPreferenceManager;

import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.florescu.android.rangeseekbar.RangeSeekBar;

import java.io.File;
import java.util.Arrays;

import io.apptik.widget.MultiSlider;

/**
 * Created by shashi on 10/1/18.
 */

public class Video_Preview extends Activity {

    RelativeLayout rl_actionbar;
    ImageView iv_back, iv_more;
    TextView tv_title, tv_start, tv_end;
    VideoView vv_video;
    //RangeSeekBar rsb_video;
    Button btn_extract_images;
    AdView adView;
    MultiSlider ms_video;

    String TAG = Video_Preview.class.getSimpleName();
    String filePath = "";
    Uri selectedVideoUri;
    Bundle bundle;
    int duration, start_ms, end_ms;
    private Runnable runnable;

    GlobalFunction globalFunction;
    SharedPreferenceManager sharedPreferenceManager;
    private FFmpeg ffmpeg;

    private String[] lastReverseCommand;

    int action = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        globalFunction = new GlobalFunction(this);
        sharedPreferenceManager = new SharedPreferenceManager(this);
        globalFunction.init_progress_dialog();

        setContentView(R.layout.video_preview);
        loadFFMpegBinary();

        rl_actionbar = (RelativeLayout) findViewById(R.id.rl_actionbar);

        iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_more = (ImageView) findViewById(R.id.iv_more);

        vv_video = (VideoView) findViewById(R.id.vv_video);
        //rsb_video = (RangeSeekBar) findViewById(R.id.rsb_video);
        ms_video = (MultiSlider) findViewById(R.id.ms_video);

        tv_title= (TextView) findViewById(R.id.tv_title);
        tv_start= (TextView) findViewById(R.id.tv_start);
        tv_end = (TextView) findViewById(R.id.tv_end);

        btn_extract_images = (Button) findViewById(R.id.btn_extract_images);

        adView = (AdView) findViewById(R.id.adView);

        iv_back.setVisibility(View.VISIBLE);
        iv_more.setVisibility(View.GONE);
        ms_video.setEnabled(false);

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels;
        float diff = (dpWidth * 30) / 100;
        dpWidth = dpWidth - diff;

        vv_video.getLayoutParams().height = Math.round(dpWidth);
        vv_video.requestLayout();

        bundle = getIntent().getExtras();

        if (bundle != null) {
            selectedVideoUri = Uri.parse(bundle.getString("file_path", ""));
            vv_video.setVideoURI(selectedVideoUri);
            vv_video.start();
        }

        if (sharedPreferenceManager.get_Remove_Ad()) {
            adView.setVisibility(View.GONE);
        } else {
            adView.loadAd(new AdRequest.Builder().build());
        }

        vv_video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                duration = mp.getDuration() / 1000;
                mp.setLooping(true);
                /*rsb_video.setRangeValues(0, duration);
                rsb_video.setSelectedMinValue(0);
                rsb_video.setSelectedMaxValue(duration);
                rsb_video.setEnabled(true);*/

                ms_video.setEnabled(true);
                ms_video.setMin(0, true, true);
                ms_video.setMax(duration, true, true);

                /*final Handler handler = new Handler();
                handler.postDelayed(runnable = new Runnable() {
                    @Override
                    public void run() {

                        if (vv_video.getCurrentPosition() >= rsb_video.getSelectedMaxValue().intValue() * 1000)
                            vv_video.seekTo(rsb_video.getSelectedMinValue().intValue() * 1000);
                        handler.postDelayed(runnable, 1000);
                    }
                }, 1000);*/
            }
        });

        /*rsb_video.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener() {
            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar bar, Object minValue, Object maxValue) {
                vv_video.seekTo((int) minValue * 1000);

                tv_start.setText(getTime((int) bar.getSelectedMinValue()));
                tv_end.setText(getTime((int) bar.getSelectedMaxValue()));
            }
        });*/

        ms_video.setOnThumbValueChangeListener(new MultiSlider.SimpleChangeListener() {
            @Override
            public void onValueChanged(MultiSlider multiSlider, MultiSlider.Thumb thumb, int thumbIndex, int value) {

                if (thumbIndex == 0) {
                    start_ms = value;
                    tv_start.setText(getTime(start_ms));
                } else {
                    end_ms = value;
                    tv_end.setText(getTime(end_ms));
                }

                vv_video.seekTo(start_ms * 1000);

            }
        });

        btn_extract_images.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (start_ms == 0 && end_ms == duration) {
                    action = 1;
                    String yourRealPath = getPath(Video_Preview.this, selectedVideoUri);
                    splitVideoCommand(yourRealPath);
                } else {
                    action = 4;
                    executeCutVideoCommand(start_ms * 1000,
                            end_ms * 1000);
                }
            }
        });

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        if (!sharedPreferenceManager.get_Remove_Ad()) {
            MyApplication.interstitial.setAdListener(new AdListener(){
                @Override
                public void onAdClosed() {
                    super.onAdClosed();
                    MyApplication.loadIntertitialAd();
                    Intent intent = new Intent(Video_Preview.this, Modified_Video.class);
                    intent.putExtra("file_path", filePath);
                    startActivity(intent);
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
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private String getTime(int seconds) {
        int hr = seconds / 3600;
        int rem = seconds % 3600;
        int mn = rem / 60;
        int sec = rem % 60;
        return String.format("%02d", hr) + ":" + String.format("%02d", mn) + ":" + String.format("%02d", sec);
    }

    private void splitVideoCommand(String path) {
        File moviesDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_MOVIES);

        String filePrefix = "split_video";
        String fileExtn = ".mp4";
        String yourRealPath = path;

        File dir = new File(moviesDir, ".VideoSplit");
        if (dir.exists())
            deleteDir(dir);
        dir.mkdir();
        File dest = new File(dir, filePrefix + "%03d" + fileExtn);

        String[] complexCommand = {"-i", yourRealPath, "-c:v", "libx264", "-crf", "22", "-map", "0", "-segment_time", "6", "-g", "9", "-sc_threshold", "0", "-force_key_frames", "expr:gte(t,n_forced*6)", "-f", "segment", dest.getAbsolutePath()};
        execFFmpegBinary(complexCommand);
    }

    private void reverseVideoCommand() {
        File moviesDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_MOVIES
        );
        File srcDir = new File(moviesDir, ".VideoSplit");
        File[] files = srcDir.listFiles();
        String filePrefix = "reverse_video";
        String fileExtn = ".mp4";
        File destDir = new File(moviesDir, ".VideoPartsReverse");
        if (destDir.exists())
            deleteDir(destDir);
        destDir.mkdir();
        for (int i = 0; i < files.length; i++) {
            File dest = new File(destDir, filePrefix + i + fileExtn);
            String command[] = {"-i", files[i].getAbsolutePath(), "-vf", "reverse", "-af", "areverse", dest.getAbsolutePath()};
            if (i == files.length - 1)
                lastReverseCommand = command;
            execFFmpegBinary(command);
        }


    }

    private void concatVideoCommand() {
        File moviesDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_MOVIES
        );
        File srcDir = new File(moviesDir, ".VideoPartsReverse");
        File[] files = srcDir.listFiles();
        if (files != null && files.length > 1) {
            Arrays.sort(files, LastModifiedFileComparator.LASTMODIFIED_REVERSE);
        }
        StringBuilder stringBuilder = new StringBuilder();
        StringBuilder filterComplex = new StringBuilder();
        filterComplex.append("-filter_complex,");
        for (int i = 0; i < files.length; i++) {
            stringBuilder.append("-i" + "," + files[i].getAbsolutePath() + ",");
            filterComplex.append("[").append(i).append(":v").append(i).append("] [").append(i).append(":a").append(i).append("] ");

        }
        filterComplex.append("concat=n=").append(files.length).append(":v=1:a=1 [v] [a]");
        String[] inputCommand = stringBuilder.toString().split(",");
        String[] filterCommand = filterComplex.toString().split(",");

        String filePrefix = "reverse_video";
        String fileExtn = ".mp4";
        File dest = new File(moviesDir, filePrefix + fileExtn);
        int fileNo = 0;
        while (dest.exists()) {
            fileNo++;
            dest = new File(moviesDir, filePrefix + fileNo + fileExtn);
        }
        filePath = dest.getAbsolutePath();
        String[] destinationCommand = {"-map", "[v]", "-map", "[a]", dest.getAbsolutePath()};
        execFFmpegBinary(combine(inputCommand, filterCommand, destinationCommand));
    }

    public static String[] combine(String[] arg1, String[] arg2, String[] arg3) {
        String[] result = new String[arg1.length + arg2.length + arg3.length];
        System.arraycopy(arg1, 0, result, 0, arg1.length);
        System.arraycopy(arg2, 0, result, arg1.length, arg2.length);
        System.arraycopy(arg3, 0, result, arg1.length + arg2.length, arg3.length);
        return result;
    }

    private void executeCutVideoCommand(int startMs, int endMs) {
        File moviesDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_MOVIES
        );

        String filePrefix = "cut_video";
        String fileExtn = ".mp4";
        String yourRealPath = getPath(Video_Preview.this, selectedVideoUri);
        File dest = new File(moviesDir, filePrefix + fileExtn);
        int fileNo = 0;
        while (dest.exists()) {
            fileNo++;
            dest = new File(moviesDir, filePrefix + fileNo + fileExtn);
        }

        Log.d(TAG, "startTrim: src: " + yourRealPath);
        Log.d(TAG, "startTrim: dest: " + dest.getAbsolutePath());
        Log.d(TAG, "startTrim: startMs: " + startMs);
        Log.d(TAG, "startTrim: endMs: " + endMs);
        filePath = dest.getAbsolutePath();
        //String[] complexCommand = {"-i", yourRealPath, "-ss", "" + startMs / 1000, "-t", "" + endMs / 1000, dest.getAbsolutePath()};
        String[] complexCommand = {"-ss", "" + startMs / 1000, "-y", "-i", yourRealPath, "-t", "" + (endMs - startMs) / 1000,"-vcodec", "mpeg4", "-b:v", "2097152", "-b:a", "48000", "-ac", "2", "-ar", "22050", filePath};

        execFFmpegBinary(complexCommand);

    }

    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            if (children != null) {
                for (int i = 0; i < children.length; i++) {
                    boolean success = deleteDir(new File(dir, children[i]));
                    if (!success) {
                        return false;
                    }
                }
            }
        }
        return dir.delete();
    }

    private void execFFmpegBinary(final String[] command) {
        try {
            ffmpeg.execute(command, new ExecuteBinaryResponseHandler() {
                @Override
                public void onFailure(String s) {
                    Log.d(TAG, "FAILED with output : " + s);
                }

                @Override
                public void onSuccess(String s) {

                    if (action == 1) {
                        action = 2;
                        reverseVideoCommand();
                    }else if (Arrays.equals(command, lastReverseCommand)) {
                        action = 3;
                        concatVideoCommand();
                    } else if (action == 3) {

                        File moviesDir = Environment.getExternalStoragePublicDirectory(
                                Environment.DIRECTORY_MOVIES
                        );
                        File destDir = new File(moviesDir, ".VideoPartsReverse");
                        File dir = new File(moviesDir, ".VideoSplit");
                        if (dir.exists())
                            deleteDir(dir);
                        if (destDir.exists())
                            deleteDir(destDir);

                        Intent intent = new Intent(Video_Preview.this, Modified_Video.class);
                        intent.putExtra("file_path", filePath);
                        startActivity(intent);
                    } else if (action == 4) {
                        action = 1;
                        String yourRealPath = getPath(Video_Preview.this, Uri.parse(filePath));
                        Log.d(TAG, "filePath->" + filePath);
                        Log.d(TAG, "yourRealPath->" + yourRealPath);
                        splitVideoCommand(filePath);
                    }
                }

                @Override
                public void onProgress(String s) {
                    Log.d(TAG, "Started command : ffmpeg " + command);
                    Log.d(TAG, "Progress : " + s);
                }

                @Override
                public void onStart() {
                    Log.d(TAG, "Started command : ffmpeg " + command);
                    globalFunction.show_progress_dialog();
                }

                @Override
                public void onFinish() {
                    Log.d(TAG, "Finished command : ffmpeg " + command);
                    globalFunction.hide_progress_dialog();
                }
            });
        } catch (FFmpegCommandAlreadyRunningException e) {
            e.printStackTrace();
        }
    }

    private void loadFFMpegBinary() {
        try {
            if (ffmpeg == null) {
                Log.d(TAG, "ffmpeg : era nulo");
                ffmpeg = FFmpeg.getInstance(this);
            }
            ffmpeg.loadBinary(new LoadBinaryResponseHandler() {
                @Override
                public void onFailure() {
                    Log.d(TAG, "Unsuported");
                }

                @Override
                public void onSuccess() {
                    Log.d(TAG, "ffmpeg : correct Loaded");
                }
            });
        } catch (FFmpegNotSupportedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    private boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    private boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }
    private boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    private String getDataColumn(Context context, Uri uri, String selection,
                                 String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }
}