package org.toop.framework.networking.server.parsing;

import java.util.LinkedList;
import java.util.List;

public class Parser {
    public static ParsedMessage parse(String msg) {
        // TODO, what if empty string.

        if (msg.isEmpty()) return null;

        msg = msg.trim().toLowerCase();

        List<String> parts = new LinkedList<>(List.of(msg.split(" ")));

        if (parts.size() > 1) {
            String command = parts.removeFirst();
            return new ParsedMessage(command, parts.toArray(String[]::new));
        }
        else {
            return new ParsedMessage(msg);
        }
    }
}
