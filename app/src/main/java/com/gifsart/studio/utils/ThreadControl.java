package com.gifsart.studio.utils;

import android.util.Log;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Tigran on 10/13/15.
 *
 * This class is for gif preview pause, resume
 */
public class ThreadControl {
    private final Lock lock = new ReentrantLock();

    private Condition pauseCondition = lock.newCondition();
    private boolean paused = false, cancelled = false;

    /**
     * Sets the control status to paused. Any thread that calls
     * waitIfPaused() at this point will begin waiting.
     */
    public void pause() {
        lock.lock();

        Log.v("ThreadControl", "Pausing");
        paused = true;

        lock.unlock();
    }

    /**
     * Sets the control status to resumed. Any thread that called
     * waitIfPaused() will finish waiting at this point.
     */
    public void resume() {
        lock.lock();
        try {
            Log.v("ThreadControl", "Resuming");
            if (!paused) {
                return;
            }
            paused = false;
            pauseCondition.signalAll();
        } finally {
            lock.unlock();
        }
    }

    /**
     * Sets the control status to cancelled. Any thread that called
     * waitIfPaused() will finish waiting at this point.
     */
    public void cancel() {
        lock.lock();
        try {
            Log.v("ThreadControl", "Cancelling");
            if (cancelled) {
                return;
            }
            cancelled = true;
            pauseCondition.signalAll();
        } finally {
            lock.unlock();
        }
    }

    /**
     * <p>Secondary worker threads call this method to wait indefinitely when control is paused. The wait ends when another thread
     * (most likely the main GUI thread) calls
     * either resume() or cancel(). A caller should call isCancelled() after this method returns to determine why the
     * wait ended.</p>
     * <p/>
     * <p>If control status is currently not paused, then this method returns immediately.</p>
     * <p/>
     * <p>If the control status is cancelled, this method returns immediately.</p>
     *
     * @throws InterruptedException
     */
    public void waitIfPaused() throws InterruptedException {
        lock.lock();

        try {
            while (paused && !cancelled) {
                Log.v("ThreadControl", "Going to wait");
                pauseCondition.await();
                Log.v("ThreadControl", "Done waiting");
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * Secondary worker threads should call this method to find out if
     * they should end their operations as quickly as possible.
     *
     * @return true if the control status is cancelled.
     */
    public boolean isCancelled() {
        return cancelled;
    }
}
