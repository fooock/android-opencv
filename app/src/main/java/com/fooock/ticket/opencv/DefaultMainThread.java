package com.fooock.ticket.opencv;

import android.os.Handler;
import android.os.Looper;

/**
 * Default implementation of {@link MainThread}
 */
final class DefaultMainThread implements MainThread {

    // singleton
    private final static MainThread MAIN_THREAD = new DefaultMainThread();

    private final Handler mHandler;

    /**
     * Create the default {@link Handler} using the main thread by calling
     * {@link Looper#getMainLooper()}
     */
    private DefaultMainThread() {
        mHandler = new Handler(Looper.getMainLooper());
    }

    /**
     * @return An instance of this class
     */
    public static MainThread getInstance() {
        return MAIN_THREAD;
    }

    @Override
    public final void execute(final Runnable runnable) {
        mHandler.post(runnable);
    }
}
