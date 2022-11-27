package com.github.flotskiy.FlotskiyBookShopApp.util;

import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;

@Component
public class CustomStringHandler {

    private final DateTimeFormatter formatter;

    public CustomStringHandler() {
        formatter = initFormatter();
    }

    public DateTimeFormatter getFormatter() {
        return formatter;
    }

    public String[] getCookieSlugs(String slugContents) {
        slugContents = slugContents.startsWith("/") ? slugContents.substring(1) : slugContents;
        slugContents =
                slugContents.endsWith("/") ? slugContents.substring(0, slugContents.length() - 1) : slugContents;
        return slugContents.split("/");
    }

    private DateTimeFormatter initFormatter() {
        return new DateTimeFormatterBuilder().appendPattern("dd.MM.yyyy[ HH:mm]")
                .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
                .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
                .toFormatter();
    }
}
