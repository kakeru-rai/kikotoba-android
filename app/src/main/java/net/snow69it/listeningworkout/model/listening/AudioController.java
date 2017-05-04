package net.snow69it.listeningworkout.model.listening;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import net.snow69it.listeningworkout.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AudioController
        extends FrameLayout
{
    private static final String TAG = "AudioController";

    private Context mContext;
    private SeekPositionCallback mSeekPositionCallback;

    @BindView(R.id.play) ImageButton mPlayButton;
    @BindView(R.id.pause) ImageButton mPauseButton;
    @BindView(R.id.next) ImageButton mNextButton;
    @BindView(R.id.prev) ImageButton mPrevButton;
    @BindView(R.id.rew) ImageButton mRewButton;
    @BindView(R.id.buttonTranscript) Button mTranscriptButton;

    private View mRoot;

    public AudioController(Context context, AttributeSet attrs) {
        super(context, attrs);
        mRoot = this;
        mContext = context;

        LayoutInflater inflate = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflate.inflate(R.layout.custom_media_controller, this);
    }

    public AudioController(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    public void onFinishInflate() {
        if (mRoot != null) {
            initControllerView(mRoot);
        }
    }

    private void initControllerView(View rootView) {
        ButterKnife.bind(this, rootView);
        updatePausePlay(false);
    }

    public void updatePausePlay(boolean isPlaying) {
        if (isPlaying) {
            mPlayButton.setVisibility(View.GONE);
            mPauseButton.setVisibility(View.VISIBLE);
        } else {
            mPlayButton.setVisibility(View.VISIBLE);
            mPauseButton.setVisibility(View.GONE);
        }
    }

    public void setPlayer(final Player player) {
        mPlayButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                player.play();
            }
        });
        mPauseButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                player.pause();
            }
        });
        mNextButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                player.next();
            }
        });
        mPrevButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                player.prev();
            }
        });
        mRewButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                player.rew();
            }
        });
        mTranscriptButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                player.popup();
            }
        });
    }

    public interface SeekPositionCallback {
        void position(int hour, int minute, int second, int miliSecond, int trackIndex);
    }

    public interface Player {
        public void play();
        public void pause();
        public void next();
        public void prev();
        public void rew();
        public void popup();
    }

}
