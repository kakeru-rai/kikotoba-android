package net.snow69it.listeningworkout.audio;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.MediaController;

import net.snow69it.listeningworkout.R;

import java.io.IOException;
import java.util.ArrayList;

public class CustomMediaController
        extends FrameLayout
//		extends MediaController
{
    private static final String TAG = "CustomMediaController";

    private static int REPEAT_MODE_OFF = 1;
    private static int REPEAT_MODE_SENTENCE = 2;
//    private static int REPEAT_MODE_PARAGRAPH = 3;

    final public static int GAP_MODE_LISTENING = 1;
    final public static int GAP_MODE_SHADOWING_LOW = 2;
    final public static int GAP_MODE_SHADOWING_HIGH = 3;

    private Context mContext;
    private Handler mHandler = new Handler();
    private ArrayList<Track> mTrackList;
    private MediaPlayer mMediaPlayer;
    private AudioControl mAudioControl;
    private SeekPositionCallback mSeekPositionCallback;
    private int repeatMode = REPEAT_MODE_OFF;
    private int gapMode = GAP_MODE_LISTENING;

    private int mCurrentTrack = 0;

    private ImageButton mPauseButton;
    private ImageButton mNextButton;
    private ImageButton mPrevButton;
    private Button mTranscriptButton;

    private View mRoot;

    private OnClickListener mPauseListener = new OnClickListener() {
        public void onClick(View v) {
            doPauseResume();
        }
    };

    private MediaPlayer.OnPreparedListener mediaPreparedListener = new MediaPlayer.OnPreparedListener() {
        public void onPrepared(final MediaPlayer mediaPlayer) {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        Thread.sleep(calcGap());

                        mediaPlayer.start();
                        Track track = mTrackList.get(mCurrentTrack);
                        mSeekPositionCallback.position(
                                track.getHour(),
                                track.getMinute(),
                                track.getSecond(),
                                track.getMilliSecond(),
                                mCurrentTrack
                        );
                        mHandler.post(new Runnable() {
                            public void run() {
                                updatePausePlay();
                            }
                        });
                    } catch (InterruptedException | IllegalStateException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    };

    private MediaPlayer.OnCompletionListener mediCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            Log.v(TAG, "onCompletion");
            if (repeatMode == REPEAT_MODE_SENTENCE) {
                start();
            } else {
                playNext();
            }
        }
    };

    private MediaPlayer.OnErrorListener mediaErrorListener = new MediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            Log.v(TAG, "onError");
            return false;
        }
    };

    private OnClickListener nextClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            playNext();
        }
    };

    private OnClickListener prevClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            playPrev();
        }
    };


    public CustomMediaController(Context context, AttributeSet attrs) {
        super(context, attrs);
        mRoot = this;
        mContext = context;

        LayoutInflater inflate = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflate.inflate(R.layout.custom_media_controller, this);

//        initControllerView(this);
    }

    public CustomMediaController(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    public void onFinishInflate() {
        if (mRoot != null)
            initControllerView(mRoot);
    }

    private void initControllerView(View v) {
        mPauseButton = (ImageButton) v.findViewById(R.id.pause);
        mPauseButton.setOnClickListener(mPauseListener);
        mPrevButton = (ImageButton) v.findViewById(R.id.prev);
        mNextButton = (ImageButton) v.findViewById(R.id.next);
        mTranscriptButton = (Button) v.findViewById(R.id.buttonTranscript);

        mAudioControl = new AudioControl();
        setPrevNextListeners(nextClickListener, prevClickListener);
    }

    private void doPauseResume() {
        if (mPlayer.isPlaying()) {
            mPlayer.pause();
        } else {
            mPlayer.start();
        }
        updatePausePlay();
    }

    private void updatePausePlay() {
        if (mPlayer.isPlaying()) {
            mPauseButton.setImageResource(android.R.drawable.ic_media_pause);
        } else {
            mPauseButton.setImageResource(android.R.drawable.ic_media_play);
        }
    }

    private void setPrevNextListeners(OnClickListener next, OnClickListener prev) {
        mNextButton.setOnClickListener(next);
        mPrevButton.setOnClickListener(prev);
    }

    public void setShowTranscriptListener(OnClickListener listener) {
        mTranscriptButton.setOnClickListener(listener);
    }

    private void playNext()
    {
        if (mCurrentTrack < mTrackList.size() - 1) {
            mCurrentTrack += 1;
            try {
                mAudioControl.prepareTrack(getCurrentTrackAsset());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void playPrev()
    {
        if (mCurrentTrack > 0) {
            mCurrentTrack -= 1;
            try {
                mAudioControl.prepareTrack(getCurrentTrackAsset());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setupMediaPlayer() {
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnPreparedListener(mediaPreparedListener);
        mMediaPlayer.setOnCompletionListener(mediCompletionListener);
        mMediaPlayer.setOnErrorListener(mediaErrorListener);

        mAudioControl.setMediaPlayer(mMediaPlayer);
        setMediaPlayer(mAudioControl);
    }
    private MediaController.MediaPlayerControl mPlayer;
    public void setMediaPlayer(MediaController.MediaPlayerControl player) {
        mPlayer = player;
    }

    public boolean isSetup(){
        return mMediaPlayer != null;
    }

    public void destroy() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    public void toggleRepeatMode() {
        if (repeatMode == REPEAT_MODE_OFF) {
            repeatMode = REPEAT_MODE_SENTENCE;
        } else {
            repeatMode = REPEAT_MODE_OFF;
        }
    }

    public void setGapMode(int gapMode) {
        this.gapMode = gapMode;
    }

    public void start() {
        try {
            mAudioControl.prepareTrack(getCurrentTrackAsset());
//            initSoundPool();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setPlayList(ArrayList<Track> audioList) {
        mTrackList = audioList;
    }

//    private int soundId;
//    private SoundPool soundPool;
//    private float playbackSpeed=0.8f;
//
//    /**
//     * SoundPoolは1MB分のデータ（デコード後で）しか再生できない
//     * 速度毎のソースを用意した方が早そう
//     * @throws IOException
//     */
//    private void initSoundPool() throws IOException {
//        soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 100);
//
//        soundId = soundPool.exec(getCurrentTrackAsset(), 1);
//        AudioManager mgr = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
//        final float volume = mgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
//
//        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
//            @Override
//            public void onLoadComplete(SoundPool arg0, int arg1, int arg2)
//            {
//                soundPool.play(soundId, volume, volume, 1, 2, playbackSpeed);
//            }
//        });
//    }

    private AssetFileDescriptor getCurrentTrackAsset() throws IOException {
        return mContext.getAssets().openFd(mTrackList.get(mCurrentTrack).getFileName());
    }

    public void setSeekPositionCallback(SeekPositionCallback callback) {
        mSeekPositionCallback = callback;
    }

    public interface SeekPositionCallback {
        void position(int hour, int minute, int second, int miliSecond, int trackIndex);
    }

    public void changeTrack(int trackIndex) {
        if (trackIndex < mTrackList.size() - 1) {
            mCurrentTrack = trackIndex;
            try {
                mAudioControl.prepareTrack(getCurrentTrackAsset());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private int calcGap() {
        switch (gapMode) {
            case GAP_MODE_LISTENING:
                return 0;
            case GAP_MODE_SHADOWING_LOW:
                return mAudioControl.getDuration() / 2;
            case GAP_MODE_SHADOWING_HIGH:
                return mAudioControl.getDuration() / 4;
        }
        return 0;
    }


}
