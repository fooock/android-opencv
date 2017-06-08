package com.fooock.ticket.opencv;

import android.app.Application;

/**
 * Main {@link Application}
 */
public class MainApplication extends Application {

    public Executor executor() {
        return DefaultExecutor.getInstance();
    }

    public MainThread mainThread() {
        return DefaultMainThread.getInstance();
    }
}
