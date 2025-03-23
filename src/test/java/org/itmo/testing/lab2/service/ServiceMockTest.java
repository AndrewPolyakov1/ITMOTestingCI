package org.itmo.testing.lab2.service;

import jdk.jfr.Description;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ServiceMockTest {

    private UserAnalyticsService userAnalyticsService;
    private UserStatusService userStatusService;

    @BeforeEach
    void setUp() {
        userAnalyticsService = mock(UserAnalyticsService.class);
        userStatusService = new UserStatusService(userAnalyticsService);
    }

    @Test
    public void testGetUserStatusInactive() {
        // Настроим поведение mock-объекта
        when(userAnalyticsService.getTotalActivityTime("user123")).thenReturn(10L);

        String status = userStatusService.getUserStatus("user123");

        assertEquals("Inactive", status);
        verify(userAnalyticsService).getTotalActivityTime("user123");
    }

    @Test
    public void testGetUserStatusHighlyActive() {
        // Настроим поведение mock-объекта
        when(userAnalyticsService.getTotalActivityTime("user123")).thenReturn(300L);

        String status = userStatusService.getUserStatus("user123");

        assertEquals("Highly active", status);
        verify(userAnalyticsService).getTotalActivityTime("user123");
    }


    @Test
    public void getUserLastSessionDateTestMultipleSessions() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        // Настроим поведение mock-объекта
        LocalDateTime testTime = LocalDateTime.now();
        when(userAnalyticsService.getUserSessions("user1")).thenReturn(List.of(
                new UserAnalyticsService.Session(testTime.minusDays(1).minusHours(1), testTime.minusHours(1)),
                new UserAnalyticsService.Session(testTime.minusHours(1), testTime)
        ));
        String lastDate = userStatusService.getUserLastSessionDate("user1").orElseThrow();
        assertEquals(formatter.format(testTime), lastDate);
        verify(userAnalyticsService).getUserSessions("user1");
    }

    @Test
    public void getUserLastSessionDateTestSingleSessions() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        // Настроим поведение mock-объекта
        LocalDateTime testTime = LocalDateTime.now();
        when(userAnalyticsService.getUserSessions("user1")).thenReturn(List.of(
                new UserAnalyticsService.Session(testTime.minusHours(1), testTime)
        ));
        String lastDate = userStatusService.getUserLastSessionDate("user1").orElseThrow();
        assertEquals(formatter.format(testTime), lastDate);
        verify(userAnalyticsService).getUserSessions("user1");
    }

    @Test
    @Disabled
    public void getUserLastSessionDateTestEmptyList() {
        when(userAnalyticsService.getUserSessions("user1")).thenReturn(Collections.emptyList());
        Optional<String> lastDate = userStatusService.getUserLastSessionDate("user1");
        assertTrue(lastDate.isEmpty());
        verify(userAnalyticsService).getUserSessions("user1");
    }

}
