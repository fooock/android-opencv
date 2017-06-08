package com.fooock.ticket.opencv;

import android.support.annotation.WorkerThread;

/**
 * This abstract class implement common code for all actions. All actions are
 * executed in a {@link Executor} and the result of his execution is returned
 * to the {@link MainThread}
 */
abstract class AbstractAction {

    private final Executor mExecutor;
    final MainThread mMainThread;

    /**
     * Create this object
     *
     * @param executor   implementation of {@link Executor}
     * @param mainThread implementation of {@link MainThread}
     */
    AbstractAction(final Executor executor, final MainThread mainThread) {
        mExecutor = executor;
        mMainThread = mainThread;
    }

    /**
     * Implement all business logic in this method. Not call directly because this method
     * need to be called in a worker thread. Normally to execute an actions you need
     * to call {@link AbstractAction#execute()}
     * <p/>
     * If you call this method the operation runs sync
     */
    @WorkerThread
    abstract void run();

    final void execute() {
        // start this action in new thread
        mExecutor.execute(this);
    }
}
