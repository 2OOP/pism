package org.toop;

import com.google.common.eventbus.Subscribe;
import org.toop.server.*;

public class LoggerListener {
    @Subscribe
    public void onCommand(CommandEvent event) {
        System.out.printf("LOG: %s args=%s -> %s%n",
                event.command(),
                String.join(",", event.args()),
                event.result());
    }
}