package com.myintroduce.utill;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DateUtil {

    // 오늘의 날짜를 원하는 포맷으로 만들어서 리턴
    public static String getTodayByCustomFormat(String format) {
        SimpleDateFormat fm = new SimpleDateFormat(format);
        Calendar cal = Calendar.getInstance();
        return fm.format(cal.getTime());
    }
}
