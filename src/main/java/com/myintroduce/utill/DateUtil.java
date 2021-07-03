package com.myintroduce.utill;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtil {

    private DateUtil() {
        throw new IllegalStateException("Utility class");
    }

    // 오늘의 날짜를 원하는 포맷으로 만들어서 리턴
    public static String getTodayByCustomFormat(String format) {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern(format));
    }
}
