package com.sayweee.spock.mockfree.karate;

import static com.sayweee.spock.mockfree.karate.LocalConfig.TEST_DATA_API_BASEURL;

public abstract class BasePreparer {
    protected String testDataApiBaseUrl;

    BasePreparer() {
        this.testDataApiBaseUrl = System.getProperty(TEST_DATA_API_BASEURL);
    }

    public void setBaseUrl(String testDataApiBaseUrl) {
        this.testDataApiBaseUrl = testDataApiBaseUrl;
    }
}
