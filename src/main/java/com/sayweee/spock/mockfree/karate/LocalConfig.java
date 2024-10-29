package com.sayweee.spock.mockfree.karate;

public class LocalConfig {
    public static final String TEST_DATA_API_BASEURL = "test_data_api_baseurl";
    public static final String LOCAL_SERVER_PORT = "local_server_port";

    private int localServerPort;
    private String testDataApiBaseUrl;

    public int getLocalServerPort() {
        return localServerPort;
    }

    public void setLocalServerPort(int localServerPort) {
        this.localServerPort = localServerPort;
    }

    public String getTestDataApiBaseUrl() {
        return testDataApiBaseUrl;
    }

    public void setTestDataApiBaseUrl(String testDataApiBaseUrl) {
        this.testDataApiBaseUrl = testDataApiBaseUrl;
    }
}
