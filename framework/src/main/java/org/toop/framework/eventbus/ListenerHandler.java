package org.toop.framework.eventbus;

import org.toop.framework.eventbus.events.EventType;

public class ListenerHandler {
    private Object listener = null;
//    private boolean unsubscribeAfterSuccess = true;

//    public ListenerHandler(Object listener, boolean unsubAfterSuccess) {
//        this.listener = listener;
//        this.unsubscribeAfterSuccess = unsubAfterSuccess;
//    }

    public ListenerHandler(Object listener) {
        this.listener = listener;
    }

    public Object getListener() {
        return this.listener;
    }

//    public boolean isUnsubscribeAfterSuccess() {
//        return this.unsubscribeAfterSuccess;
//    }

}
