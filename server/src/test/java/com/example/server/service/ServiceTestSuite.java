package com.example.server.service;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        StatisticsServiceTest.class,
        NotificationSubscriptionServiceTest.class
})
class ServiceTestSuite {
}
