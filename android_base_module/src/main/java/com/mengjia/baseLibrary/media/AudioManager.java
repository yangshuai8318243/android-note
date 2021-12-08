package com.mengjia.baseLibrary.media;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Looper;

import androidx.annotation.NonNull;

import com.mengjia.baseLibrary.app.AppHandler;
import com.mengjia.baseLibrary.log.AppLog;

import java.io.File;
import java.io.IOException;

/**
 * Created by Android Studio.
 * User: SnapeYang
 * Date: 2020/8/25
 * Time: 9:14
 */
public class AudioManager {
    private static final String TAG = "AudioManager";
    private MediaRecorder mMediaRecorder;
    private MediaPlayer mMediaPlayer;
    private boolean isRecording;

    private int BASE = 1;
    private int SPACE = 100;// 间隔取样时间
    private long startTime = 0;

    private AppHandler appHandler = new AppHandler(Looper.getMainLooper(), null);
    private OnAudioStatusUpdateListener audioStatusUpdateListener;
    private AudioManagerStopCallBack audioManagerStopCallBack;
    private String filepath;
    private String playFilepath;
    private long playMsgId;

    public AudioManager() {
        mMediaPlayer = new MediaPlayer();
    }

    public void setAudioStatusUpdateListener(OnAudioStatusUpdateListener audioStatusUpdateListener) {
        this.audioStatusUpdateListener = audioStatusUpdateListener;
    }

    public void setAudioManagerPlayCallBack(AudioManagerStopCallBack audioManagerStopCallBack) {
        this.audioManagerStopCallBack = audioManagerStopCallBack;
    }

    public String getFilepath() {
        return filepath;
    }

    public String getPlayFilepath() {
        return playFilepath;
    }

    /**
     * 开始录音 使用amr格式
     * 录音文件
     *
     * @param filepath     音频文件路径
     * @param erroCallBack 异常回调
     */
    public void startRecord(@NonNull String filepath, @NonNull final AudioManagerErrorCallBack erroCallBack) {
        mMediaRecorder = new MediaRecorder();
        //设置音频的来源
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        //设置音频的输出格式
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);//设置输出文件的格式
        //设置音频文件的编码
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);//设置音频文件的编码

        if (mMediaPlayer.isPlaying()) {
            stopPlay();
            audioManagerStopCallBack.stopPlay(playMsgId);
        }


        //如果正在录制，就返回了
        if (isRecording) {
            return;
        }
        this.filepath = filepath;

        mMediaRecorder.setOutputFile(filepath);
        startTime = System.currentTimeMillis();
        try {
            //录制前准备工作
            mMediaRecorder.prepare();
            //开始录制
            mMediaRecorder.start();
            mMediaRecorder.setOnErrorListener(new MediaRecorder.OnErrorListener() {
                @Override
                public void onError(MediaRecorder mr, int what, int extra) {
                    String message = "未知错误";
                    switch (what) {
                        case MediaRecorder.MEDIA_RECORDER_ERROR_UNKNOWN:
                            message = "语音录制未知错误！！";
                            AppLog.e(TAG, message);
                            break;
                        case MediaRecorder.MEDIA_ERROR_SERVER_DIED:
                            message = "媒体服务卡死！！";
                            AppLog.e(TAG, message);
                            break;
                    }
                    erroCallBack.onError(message);
                    mr.stop();
                    mr.reset();
                }
            });

            mMediaRecorder.setOnInfoListener(new MediaRecorder.OnInfoListener() {
                @Override
                public void onInfo(MediaRecorder mr, int what, int extra) {
                    String message = "未知错误";
                    switch (what) {
                        case MediaRecorder.MEDIA_RECORDER_INFO_UNKNOWN:
                            message = "语音录制未知错误！！";
                            AppLog.i(TAG, message);
                            break;
                        case MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED:
                            message = "录制超时！！";
                            AppLog.i(TAG, message);
                            break;
                        case MediaRecorder.MEDIA_RECORDER_INFO_MAX_FILESIZE_REACHED:
                            message = "录制文件超过指定大小！！";
                            AppLog.i(TAG, message);
                            break;

                    }
                    erroCallBack.onError(message);
                    mr.stop();
                    mr.reset();
                }
            });
            isRecording = true;
            //开始监听
            updateMicStatus();

            AppLog.v(TAG, "startRecord record succ...");
        } catch (Exception e) {
            erroCallBack.onError("录音异常");
            AppLog.v(TAG, "startRecord record fail:" + e.toString());
        }
    }

    public interface OnAudioStatusUpdateListener {
        /**
         * 录音中...
         *
         * @param db   当前声音分贝
         * @param time 录音时长
         */
        void onUpdate(double db, long time);
    }

    private Runnable mUpdateMicStatusTimer = new Runnable() {
        public void run() {
            updateMicStatus();
        }
    };

    /**
     * 更新麦克状态
     */
    private void updateMicStatus() {
        if (mMediaRecorder != null && isRecording) {
            double ratio = (double) mMediaRecorder.getMaxAmplitude() / BASE;
            double db;// 分贝
            if (ratio >= 0) {
                db = 20 * Math.log10(ratio);
                if (null != audioStatusUpdateListener) {
                    audioStatusUpdateListener.onUpdate(db, System.currentTimeMillis() - startTime);
                }
            }
            appHandler.postDelayed(mUpdateMicStatusTimer, SPACE);
        }
    }


    /**
     * 停止录制
     */
    public long stopRecord() {
        if (mMediaRecorder != null && isRecording) {
            release();
            isRecording = false;
            return System.currentTimeMillis() - startTime;
        }
        return 0;
    }

    /**
     * 释放资源
     */
    public void release() {
        mMediaRecorder.setOnErrorListener(null);
        mMediaRecorder.setOnInfoListener(null);
        mMediaRecorder.setPreviewDisplay(null);
        try {
            //停止录制
            mMediaRecorder.stop();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //重新开始
        mMediaRecorder.release();
        //注意：可以通过返回setAudioSource（）步骤来重用该对象
        //mMediaRecorder.release();注意：这个对象不能再次被使用，如果此次再次录制，就会报错
        mMediaRecorder = null;
    }

    /**
     * 取消（释放资源+删除文件）
     */
    public void cancel() {
        stopRecord();
        if (filepath != null) {
            File file = new File(filepath);
            boolean delete = file.delete();//删除录音文件
            AppLog.i(TAG, "删除录音文件：" + delete);
            filepath = null;
        }
    }

    /**
     * 获取音频时长
     *
     * @param filepath 音频路径
     * @return 返回音频长度
     */
    public int getAudioTime(@NonNull String filepath) {
        if (mMediaPlayer.isPlaying()) {
            return -1;
        }
        //设置数据源
        try {
            mMediaPlayer.setDataSource(filepath);
            //这个准备工作必须要做
            mMediaPlayer.prepare();
            int duration = mMediaPlayer.getDuration();
            return duration;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            mMediaPlayer.reset();
        }
        return -1;
    }

    /**
     * 停止播放当前音频
     */
    public void stopPlay() {
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
            audioManagerStopCallBack.stopPlay(playMsgId);
            mMediaPlayer.reset();
            playFilepath = "";
        }
    }

    /**
     * 播放音频文件
     *
     * @param filepath     文件长度
     * @param erroCallBack 异常回调
     */
    public void play(long msgId, @NonNull String filepath,
                     @NonNull AudioManagerErrorCallBack erroCallBack,
                     @NonNull final AudioManagerPlayCallBack audioManagerPlayCallBack) {
        try {
            playMsgId = msgId;
            //如果正在播放，然后在播放其他文件就直接崩溃了
            if (mMediaPlayer.isPlaying()) {
                stopPlay();
            }
            playFilepath = filepath;

            //设置数据源
            mMediaPlayer.setDataSource(filepath);
            //这个准备工作必须要做
            mMediaPlayer.prepare();
            mMediaPlayer.start();
            audioManagerPlayCallBack.playing();
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    //播放完毕再重置一下状态，下次播放可以再次使用
                    mp.reset();
                    audioManagerStopCallBack.stopPlay(playMsgId);
                    playFilepath = "";
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            erroCallBack.onError("IO 异常");
            mMediaPlayer.reset();
        }
    }


    public interface AudioManagerPlayCallBack {
        void playing();
    }

    public interface AudioManagerErrorCallBack {
        void onError(String err);
    }

    public interface AudioManagerStopCallBack {
        void stopPlay(long magId);
    }
}
