package org.toop.framework.utils;

import org.apache.maven.surefire.shared.utils.StringUtils;

import java.util.Iterator;

public class Utils {
    public static String returnQuotedString(Iterator<String> strings) { // TODO more places this could be useful
        return "\"" + StringUtils.join(strings, "\",\"") + "\"";
    }
}
