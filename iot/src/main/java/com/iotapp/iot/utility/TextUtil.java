package com.iotapp.iot.utility;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by skumari on 9/24/2017.
 */

public class TextUtil {
    public static String toFirstLtrUprCase(String word) {
        Pattern spaces = Pattern.compile("\\s+[a-z]");
        Matcher m = spaces.matcher(word);
        StringBuilder capitalWordBuilder = new StringBuilder(word.substring(0, 1).toUpperCase());
        int prevStart = 1;
        while (m.find()) {
            capitalWordBuilder.append(word.substring(prevStart, m.end() - 1));
            capitalWordBuilder.append(word.substring(m.end() - 1, m.end()).toUpperCase());
            prevStart = m.end();
        }
        capitalWordBuilder.append(word.substring(prevStart, word.length()));
        return capitalWordBuilder.toString();
    }

    public static String toFirstLetterUprCaseAdd(String input) {

        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }
}
