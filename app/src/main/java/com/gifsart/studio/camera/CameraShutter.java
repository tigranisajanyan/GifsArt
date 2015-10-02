package com.gifsart.studio.camera;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;


import com.gifsart.studio.R;

import java.util.HashMap;

public class CameraShutter implements Callback {

    enum ShutterMode {
        NORMAL, TIMER, BURST, TIME_LAPSE, STABLE
    }

    private final static int TIMER_SOUND = 0;
    private final static int TIMER_LAST_SECONDS_SOUND = 1;

    private ShutterMode mode = ShutterMode.NORMAL;

    private ModeParams timerParams;
    private ModeParams timerLapseParams;
    private BurstModeParams burstModeParams;

    private ShotCallback shotCallback;

    private static final int MSG_TIMER_SHOT = 1;

    private boolean modeRuning = false;
    private boolean isBackPressed = false;
    private SoundPool soundPool;
    private int playedStreamId = -1;
    private HashMap<Integer, Integer> sounds;
    private boolean isPlaySound = true;

    Handler handler = new Handler(this);

    public CameraShutter(Context context, ShotCallback callback) {
        timerParams = new TimerModeParams();
        timerLapseParams = new TimeLapseModeParams();
        burstModeParams = new BurstModeParams();
        this.shotCallback = callback;
        soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 100);
        sounds = new HashMap<>();
        sounds.put(TIMER_SOUND, soundPool.load(context, R.raw.camera_timer_sound, 1));
        sounds.put(TIMER_LAST_SECONDS_SOUND, soundPool.load(context, R.raw.camera_timer_last_seconds_sound, 1));
    }

    public void shot() {
        if (shotCallback == null) throw new IllegalStateException("shot callback must not be null");
        if (mode == ShutterMode.NORMAL) {
            shotCallback.onShot();
        } else if (mode == ShutterMode.TIMER) {
            if (timerParams.getTimerValue() == 0) {
                shotCallback.onUpdate(timerParams);
                shotCallback.onShot();
                modeRuning = false;
            } else {
                modeRuning = true;
                handler.removeMessages(MSG_TIMER_SHOT);
                Message msg = handler.obtainMessage(MSG_TIMER_SHOT);
                msg.obj = timerParams;
                handler.sendMessageDelayed(msg, 1000);
                shotCallback.onUpdate(timerParams);
            }
        } else if (mode == ShutterMode.TIME_LAPSE) {
            if (timerLapseParams.getTimerValue() == 0) {
                shotCallback.onUpdate(timerLapseParams);
                shotCallback.onShot();
                timerLapseParams.reset();
            } else {
                modeRuning = true;
                handler.removeMessages(MSG_TIMER_SHOT);
                Message msg = handler.obtainMessage(MSG_TIMER_SHOT);
                msg.obj = timerLapseParams;
                handler.sendMessageDelayed(msg, 1000);
                shotCallback.onUpdate(timerLapseParams);
            }
        }
        if (mode == ShutterMode.BURST) {
            if (burstModeParams.getTimerValue() > 0) {
                if (!paused) {
                    modeRuning = true;
                    if (burstModeParams.getTimerValue() > 0) shotCallback.onUpdate(burstModeParams);
                    burstModeParams.setTimerValue(burstModeParams.getTimerValue() - 1);

                    shotCallback.onShot();
                }
            } else {
                modeRuning = false;
                shotCallback.onUpdate(burstModeParams);
                burstModeParams.reset();
            }
        }
    }

    public void setMode(ShutterMode mode) {
        this.mode = mode;
        paused = false;
        modeRuning = false;
        isBackPressed = false;
        resetMode();
    }

    public ShutterMode getMode() {
        return mode;
    }

    public void resetMode() {
        if (mode == ShutterMode.TIMER) {
            timerParams.reset();
        } else if (mode == ShutterMode.TIME_LAPSE) {
            timerLapseParams.reset();
        } else if (mode == ShutterMode.BURST) {
            burstModeParams.reset();
        }
    }

    public void updateTimerParams() {
        if (mode == ShutterMode.TIMER) {
            timerParams.setNextTimer();
            shotCallback.onUpdate(timerParams);
        } else if (mode == ShutterMode.TIME_LAPSE) {
            timerLapseParams.setNextTimer();
            shotCallback.onUpdate(timerLapseParams);
        } else if (mode == ShutterMode.BURST) {
            burstModeParams.setNextTimer();
            shotCallback.onUpdate(burstModeParams);
        }
    }

    private boolean paused = false;

    public void pause() {
        if (handler.hasMessages(MSG_TIMER_SHOT)) {
            handler.removeMessages(MSG_TIMER_SHOT);
            paused = true;
        }
        if (mode == ShutterMode.BURST) {
            paused = true;
        }

    }

    public void resume() {
        if (paused) {
            paused = false;
            if ((mode != ShutterMode.BURST || modeRuning) && !isBackPressed) {
                shot();
            }
            isBackPressed = false;
        }

    }

    public static interface ModeParams {
        public void reset();

        public int getTimerValue();

        public void setTimerValue(int newVal);

        public void setNextTimer();

    }

    private abstract static class AbsTimerModeParams implements ModeParams {
        protected int[] timesSeconds = {2, 5, 10, 20, 30};
        protected int timeSeconds;
        public int selectedIndex = 0;

        public AbsTimerModeParams() {
            reset();
        }

        @Override
        public void reset() {
            timeSeconds = timesSeconds[selectedIndex];
        }

        @Override
        public int getTimerValue() {
            return timeSeconds;
        }

        @Override
        public void setTimerValue(int newVal) {
            if (newVal < 0) newVal = 0;
            this.timeSeconds = newVal;
        }

        @Override
        public void setNextTimer() {
            if (selectedIndex >= timesSeconds.length - 1)
                selectedIndex = 0;
            else selectedIndex++;
            setTimerValue(timesSeconds[selectedIndex]);
            Log.d("timer value ", timeSeconds + "");
        }

        @Override
        public String toString() {
            return "timeSeconds: " + timeSeconds;
        }
    }

    public class TimerModeParams extends AbsTimerModeParams {
        public TimerModeParams() {
            super();
            timesSeconds = new int[6];
            timesSeconds[0] = 2;
            timesSeconds[1] = 5;
            timesSeconds[2] = 10;
            timesSeconds[3] = 15;
            timesSeconds[4] = 20;
            timesSeconds[5] = 30;
        }
    }

    public class TimeLapseModeParams extends TimerModeParams {
        public TimeLapseModeParams() {
            super();
            timesSeconds = new int[5];
            timesSeconds[0] = 1;
            timesSeconds[1] = 2;
            timesSeconds[2] = 5;
            timesSeconds[3] = 15;
            timesSeconds[4] = 30;
        }
    }

    private class BurstModeParams implements ModeParams {
        public int[] framesCounts = {2, 5, 10, 20, 30};
        private int currentFramesCount;
        private int selectedIndex = 0;

        @Override
        public int getTimerValue() {
            return currentFramesCount;
        }

        @Override
        public void setTimerValue(int newVal) {
            if (newVal < 0) newVal = 0;
            this.currentFramesCount = newVal;
        }

        @Override
        public void setNextTimer() {
            if (selectedIndex >= framesCounts.length - 1)
                selectedIndex = 0;
            else selectedIndex++;
            setTimerValue(framesCounts[selectedIndex]);
        }

        @Override
        public void reset() {
            currentFramesCount = framesCounts[selectedIndex];

        }
    }

    public static interface ShotCallback {
        public void onUpdate(ModeParams params);

        public void onShot();
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_TIMER_SHOT:

                ModeParams curModeParams = (ModeParams) msg.obj;
                if (isPlaySound) {
                    playSoundPool(curModeParams.getTimerValue());
                }

                curModeParams.setTimerValue(curModeParams.getTimerValue() - 1);
                shot();
                break;
        }
        return false;
    }

    public void notifyUpdate() {
        if (mode == ShutterMode.TIMER) {
            shotCallback.onUpdate(timerParams);
        } else if (mode == ShutterMode.TIME_LAPSE) {
            shotCallback.onUpdate(timerLapseParams);
        } else if (mode == ShutterMode.BURST) {
            shotCallback.onUpdate(burstModeParams);
        }
    }

    public boolean isModeRunning() {
        return modeRuning;
    }

    public void setModeRunnning(boolean modeRuning) {
        this.modeRuning = modeRuning;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
        this.isBackPressed = false;
    }

    public void setBackPressed(boolean isBackPressed) {
        this.isBackPressed = isBackPressed;
    }

    public int getCurrentModeTimerValue() {
        if (mode == ShutterMode.BURST) {
            return burstModeParams.getTimerValue();
        } else if (mode == ShutterMode.TIME_LAPSE) {
            return timerLapseParams.getTimerValue();
        } else if (mode == ShutterMode.TIMER) {
            return timerParams.getTimerValue();
        }
        return 0;
    }

    private void playSoundPool(int seconds) {
        if (playedStreamId != -1) {
            soundPool.pause(playedStreamId);
        }

        int priority = 1;
        int no_loop = 0;
        float normal_playback_rate = 1f;
        if (seconds <= 3) {
            playedStreamId = soundPool.play(sounds.get(TIMER_LAST_SECONDS_SOUND), 1.f, 1.f, priority, no_loop, normal_playback_rate);
        } else {
            playedStreamId = soundPool.play(sounds.get(TIMER_SOUND), 1.f, 1.f, priority, no_loop, normal_playback_rate);
        }

    }

    public void setPlaySound(boolean isPlaySound) {
        this.isPlaySound = isPlaySound;
    }

    public void destroy() {
        soundPool.release();
    }
}
