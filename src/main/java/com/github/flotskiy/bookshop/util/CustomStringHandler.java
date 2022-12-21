package com.github.flotskiy.bookshop.util;

import org.springframework.context.i18n.LocaleContextHolder;
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

    private static DateTimeFormatter initFormatter() {
        return new DateTimeFormatterBuilder().appendPattern("dd.MM.yyyy[ HH:mm]")
                .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
                .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
                .toFormatter();
    }

    public String getSearchBookCountMessage(int booksCount) {
        String lang = LocaleContextHolder.getLocale().getLanguage();
        if (lang.equals("ru")) {
            return getSearchBookCountMessageRu(booksCount);
        } else {
            return getSearchBookCountMessageEn(booksCount);
        }
    }

    public String getSearchBookCountMessageRu(int booksCount) {
        String result = "Найден";
        String resultEnd = "о %d %s";
        String[] bookWordsArrayRu = {"книга", "книги", "книг"};
        int preLastDigit = booksCount % 100 / 10;
        if (preLastDigit == 1) {
            result += String.format(resultEnd, booksCount, bookWordsArrayRu[2]);
        } else {
            int lastDigit = booksCount % 10;
            switch (lastDigit) {
                case 1:
                    result += String.format("а %d %s", booksCount, bookWordsArrayRu[0]);
                    break;
                case 2:
                case 3:
                case 4:
                    result += String.format(resultEnd, booksCount, bookWordsArrayRu[1]);
                    break;
                default:
                    result += String.format(resultEnd, booksCount, bookWordsArrayRu[2]);
            }
        }
        return result;
    }

    public String getSearchBookCountMessageEn(int booksCount) {
        if (booksCount == 1) {
            return booksCount + " book found";
        } else {
            return booksCount + " books found";
        }
    }

    public String getFileSizeString(int size) {
        char[] chars = {'B', 'K', 'M', 'G', 'T'};
        double result;
        for (int i = 0; i < chars.length; i++) {
            result = size / Math.pow(1024, i);
            if (result < 1024) {
                if (i == 0) {
                    return result + "\u00A0" + chars[i];
                }
                return String.format("%.1f\u00A0%sB", result, chars[i]);
            }
        }
        return "";
    }
}
