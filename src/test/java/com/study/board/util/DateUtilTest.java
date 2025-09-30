package com.study.board.util;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class DateUtilTest {

    @Test
    void formatDateTime_정상날짜_포맷된문자열반환() {
        // Given
        LocalDateTime dateTime = LocalDateTime.of(2023, 12, 25, 15, 30, 45);

        // When
        String result = DateUtil.formatDateTime(dateTime);

        // Then
        assertEquals("2023-12-25 15:30:45", result);
    }

    @Test
    void formatDateTime_null_빈문자열반환() {
        // When
        String result = DateUtil.formatDateTime(null);

        // Then
        assertEquals("", result);
    }

    @Test
    void formatDate_정상날짜_날짜만반환() {
        // Given
        LocalDateTime dateTime = LocalDateTime.of(2023, 12, 25, 15, 30, 45);

        // When
        String result = DateUtil.formatDate(dateTime);

        // Then
        assertEquals("2023-12-25", result);
    }

    @Test
    void formatTime_정상날짜_시간만반환() {
        // Given
        LocalDateTime dateTime = LocalDateTime.of(2023, 12, 25, 15, 30, 45);

        // When
        String result = DateUtil.formatTime(dateTime);

        // Then
        assertEquals("15:30:45", result);
    }

    @Test
    void getRelativeTime_30초전_방금전반환() {
        // Given
        LocalDateTime dateTime = LocalDateTime.now().minusSeconds(30);

        // When
        String result = DateUtil.getRelativeTime(dateTime);

        // Then
        assertEquals("방금 전", result);
    }

    @Test
    void getRelativeTime_5분전_분단위반환() {
        // Given
        LocalDateTime dateTime = LocalDateTime.now().minusMinutes(5);

        // When
        String result = DateUtil.getRelativeTime(dateTime);

        // Then
        assertEquals("5분 전", result);
    }

    @Test
    void getRelativeTime_2시간전_시간단위반환() {
        // Given
        LocalDateTime dateTime = LocalDateTime.now().minusHours(2);

        // When
        String result = DateUtil.getRelativeTime(dateTime);

        // Then
        assertEquals("2시간 전", result);
    }

    @Test
    void getRelativeTime_3일전_일단위반환() {
        // Given
        LocalDateTime dateTime = LocalDateTime.now().minusDays(3);

        // When
        String result = DateUtil.getRelativeTime(dateTime);

        // Then
        assertEquals("3일 전", result);
    }
}
