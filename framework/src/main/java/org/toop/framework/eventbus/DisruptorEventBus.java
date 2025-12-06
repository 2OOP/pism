package org.toop.framework.eventbus;

import com.lmax.disruptor.BusySpinWaitStrategy;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import org.apache.logging.log4j.Logger;
import org.toop.framework.eventbus.events.EventType;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadFactory;

public class DisruptorEventBus<T extends EventType> implements EventBus<T> {
    /** Wrapper used inside the ring buffer. */
    private static class EventHolder {
        EventType event;
    }

    private final Logger logger;
    private final EventsHolder eventsHolder;

    private final Disruptor<EventHolder> disruptor;
    private final RingBuffer<EventHolder> ringBuffer;

    public DisruptorEventBus(Logger logger, EventsHolder eventsHolder) {
        this.logger = logger;
        this.eventsHolder = eventsHolder;

        ThreadFactory threadFactory =
                r -> {
                    Thread t = new Thread(r, "EventBus-Disruptor");
                    t.setDaemon(true);
                    return t;
                };

        disruptor = getEventHolderDisruptor(threadFactory);

        disruptor.start();
        this.ringBuffer = disruptor.getRingBuffer();
    }

    private Disruptor<EventHolder> getEventHolderDisruptor(ThreadFactory threadFactory) {
        int RING_BUFFER_SIZE = 1024 * 64;
        Disruptor<EventHolder> disruptor = new Disruptor<>(
                EventHolder::new,
                RING_BUFFER_SIZE,
                threadFactory,
                ProducerType.MULTI,
                new BusySpinWaitStrategy());

        disruptor.handleEventsWith(
                (holder, _, _) -> {
                    if (holder.event != null) {
                        dispatchEvent(holder.event);
                        holder.event = null;
                    }
                });
        return disruptor;
    }

    @Override
    public void subscribe(ListenerHandler<? extends EventType> listener) {
        eventsHolder.add(listener);
    }

    @Override
    public void unsubscribe(ListenerHandler<? extends EventType> listener) {
        eventsHolder.remove(listener);
    }

    @Override
    public void post(EventType event) {
        long seq = ringBuffer.next();
        try {
            EventHolder holder = ringBuffer.get(seq);
            holder.event = event;
        } finally {
            ringBuffer.publish(seq);
        }
    }

    @Override
    public void shutdown() {
        disruptor.shutdown();
        eventsHolder.reset();
    }

    @Override
    public void reset() {
        eventsHolder.reset();
    }

    private void dispatchEvent(EventType event) {
        CopyOnWriteArrayList<ListenerHandler<?>> classListeners = (CopyOnWriteArrayList<ListenerHandler<?>>) eventsHolder.get(event.getClass());
        if (classListeners != null) {
            for (ListenerHandler<?> listener : classListeners) {
                try {
                    callListener(listener, event);
                } catch (Throwable e) {
                    logger.warn("Exception while handling event: {}", event, e);
                }
            }
        }

        CopyOnWriteArrayList<ListenerHandler<?>> genericListeners = (CopyOnWriteArrayList<ListenerHandler<?>>) eventsHolder.get(Object.class);
        if (genericListeners != null) {
            for (ListenerHandler<?> listener : genericListeners) {
                try {
                    callListener(listener, event);
                } catch (Throwable e) {
                    logger.warn("Exception while handling event: {}", event, e);
                }
            }
        }
    }


    private static <T extends EventType> void callListener(ListenerHandler<T> handler, EventType event) {
        T casted = handler.getListenerClass().cast(event);
        handler.getListener().accept(casted);
    }
}
