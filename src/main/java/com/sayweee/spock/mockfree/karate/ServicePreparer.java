package com.sayweee.spock.mockfree.karate;

import com.intuit.karate.Http;
import com.intuit.karate.Json;
import com.intuit.karate.http.Response;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;

public class ServicePreparer extends BasePreparer {
    private static final Logger log = getLogger(ServicePreparer.class);
    private static final String SERVICE_PATH = CONTEXT_PATH + "/service/call";

    public Object call(String serviceClassName, String serviceMethodName, String jsonBody) {
        Map<String, String> params = new HashMap<>();
        params.put("serviceClassName", serviceClassName);
        params.put("serviceMethodName", serviceMethodName);
        if (jsonBody != null) {
            params.put("jsonBody", jsonBody);
        }
        Response response = Http.to(testDataApiBaseUrl + SERVICE_PATH).post(Json.of(params));
        log.info("jwt response {}", response.getBodyConverted());
        return response.json().get("object", Object.class);
    }
}
