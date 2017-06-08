package com.fooock.ticket.opencv;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Default implementation of {@link Executor}. Execute all actions in a
 * background thread.
 */
final class DefaultExecutor implements Executor {

    private final ThreadPoolExecutor mThreadPoolExecutor;

    private static final int CORE_POOL_SIZE = 3;
    private static final int MAX_POOL_SIZE = 5;
    private static final int KEEP_ALIVE_TIME = 1;
    private static final TimeUnit TIME_UNIT = TimeUnit.SECONDS;
    // default work queue
    private static final BlockingQueue<Runnable> WORK_QUEUE = new LinkedBlockingQueue<>();
    // singleton
    private static final Executor EXECUTOR = new DefaultExecutor();

    private DefaultExecutor() {
        mThreadPoolExecutor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE,
                KEEP_ALIVE_TIME, TIME_UNIT, WORK_QUEUE);
    }

    /**
     * @return An instance of this class
     */
    public static Executor getInstance() {
        return EXECUTOR;
    }

    @Override
    public void execute(final AbstractAction action) {
        mThreadPoolExecutor.submit(new Runnable() {
            @Override
            public void run() {
                action.run();
            }
        });
    }
}
