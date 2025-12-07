package org.toop.framework.eventbus.holder;

import org.toop.framework.eventbus.subscriber.Subscriber;

import java.util.concurrent.ConcurrentHashMap;

public class DefaultSubscriberStore implements SubscriberStore {

    private static final Subscriber<?, ?>[] EMPTY = new Subscriber[0];

    private final ConcurrentHashMap<Class<?>, Subscriber<?, ?>[]> listeners =
            new ConcurrentHashMap<>();

    @Override
    public void add(Subscriber<?, ?> sub) {
        listeners.compute(sub.event(), (_, arr) -> {
            if (arr == null || arr.length == 0) {
                return new Subscriber<?, ?>[]{sub};
            }

            int len = arr.length;
            Subscriber<?, ?>[] newArr = new Subscriber[len + 1];
            System.arraycopy(arr, 0, newArr, 0, len);
            newArr[len] = sub;
            return newArr;
        });
    }

    @Override
    public void remove(Subscriber<?, ?> sub) {
        listeners.computeIfPresent(sub.event(), (_, arr) -> {
            int len = arr.length;

            if (len == 1) {
                return arr[0].equals(sub) ? null : arr;
            }

            int keep = 0;
            for (Subscriber<?, ?> s : arr) {
                if (!s.equals(sub)) keep++;
            }

            if (keep == len) {
                return arr;
            }
            if (keep == 0) {
                return null;
            }

            Subscriber<?, ?>[] newArr = new Subscriber[keep];
            int i = 0;
            for (Subscriber<?, ?> s : arr) {
                if (!s.equals(sub)) {
                    newArr[i++] = s;
                }
            }

            return newArr;
        });
    }

    @Override
    public Subscriber<?, ?>[] get(Class<?> event) {
        return listeners.getOrDefault(event, EMPTY);
    }

    @Override
    public void reset() {
        listeners.clear();
    }
}