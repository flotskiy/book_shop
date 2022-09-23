package com.github.flotskiy.FlotskiyBookShopApp.util;

import org.springframework.stereotype.Component;

@Component
public class CustomStringHandler {

    public String[] getCookieSlugs(String slugContents) {
        slugContents = slugContents.startsWith("/") ? slugContents.substring(1) : slugContents;
        slugContents =
                slugContents.endsWith("/") ? slugContents.substring(0, slugContents.length() - 1) : slugContents;
        return slugContents.split("/");
    }
}
