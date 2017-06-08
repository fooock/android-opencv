package com.fooock.ticket.opencv;

/**
 * Run certain operations in the main thread
 */
interface MainThread {

    /**
     * Execute in the main thread the given {@code Runnable}
     *
     * @param runnable {@link Runnable} to execute in the main thread
     */
    void execute(Runnable runnable);
}