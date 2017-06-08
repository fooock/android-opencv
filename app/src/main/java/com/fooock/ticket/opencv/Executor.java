package com.fooock.ticket.opencv;

/**
 * Run actions in background thread
 */
interface Executor {

    /**
     * Execute in a background thread the given action by calling
     * {@link AbstractAction#run()}
     *
     * @param action Action to start in background thread
     */
    void execute(AbstractAction action);
}
