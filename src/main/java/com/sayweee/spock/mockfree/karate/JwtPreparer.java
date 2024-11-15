package com.sayweee.spock.mockfree.karate;

import com.intuit.karate.Http;
import com.intuit.karate.Json;
import com.intuit.karate.http.Response;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;

@SuppressWarnings("unused")
public class JwtPreparer extends BasePreparer {
    private static final Logger log = getLogger(JwtPreparer.class);

    private static final String JWT_PATH = CONTEXT_PATH + "/token/generate";

    public String gen(String userId) {
        Map<String, String> body = new HashMap<>();
        body.put("userId", userId);
        return getJwt(body);
    }

    public String gen(String userId, String tokenType) {
        Map<String, String> body = new HashMap<>();
        body.put("userId", userId);
        body.put("tokenType", tokenType);
        return getJwt(body);
    }

    public String gen(Map<String, String> body) {
        return getJwt(body);
    }

    private String getJwt(Map<String, String> params) {
        Response response = Http.to(testDataApiBaseUrl + JWT_PATH).post(Json.of(params));
        log.info("jwt response {}", response.getBodyConverted());
        return response.json().get("object", String.class);
    }
}
