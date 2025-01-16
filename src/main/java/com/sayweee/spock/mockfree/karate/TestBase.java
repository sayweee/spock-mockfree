package com.sayweee.spock.mockfree.karate;

import com.intuit.karate.Results;
import com.intuit.karate.Runner;
import io.qameta.allure.karate.AllureKarate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.sayweee.spock.mockfree.karate.LocalConfig.LOCAL_SERVER_PORT;
import static com.sayweee.spock.mockfree.karate.LocalConfig.TEST_DATA_API_BASEURL;
import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class TestBase {

    @BeforeEach
    void setProperties() {
        LocalConfig localConfig = localConfig();
        System.setProperty(LOCAL_SERVER_PORT, String.valueOf(localConfig.getLocalServerPort()));
        System.setProperty(TEST_DATA_API_BASEURL, localConfig.getTestDataApiBaseUrl());
    }

    protected abstract LocalConfig localConfig();

    @Test
    void apiTest() {
        Results results = Runner
                .path("classpath:features")
                .relativeTo(getClass())
                .hook(new AllureKarate())
                .outputCucumberJson(false)
                .parallel(1);
        assertEquals(0, results.getFailCount(), results.getErrorMessages());
    }

}
