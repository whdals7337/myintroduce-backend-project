package com.myintroduce.utill;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class DateUtilTest {

    @Test
    void getTodayByCustomFormat_test() {
        // given

        // when
        String formatToday = DateUtil.getTodayByCustomFormat("yyyy-MM-dd");

        // then
        String today = "";
        int month = LocalDateTime.now().getMonthValue();
        int date = LocalDateTime.now().getDayOfMonth();
        today += LocalDateTime.now().getYear();
        today += "-";
        today += month < 10 ? "0"+month : month;
        today += "-";
        today += date < 10 ? "0"+date : date;
        assertThat(formatToday).isEqualTo(today);
    }
}