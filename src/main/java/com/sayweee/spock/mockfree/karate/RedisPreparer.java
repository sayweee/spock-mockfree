package com.sayweee.spock.mockfree.karate;

import com.intuit.karate.Http;
import com.intuit.karate.Json;
import com.intuit.karate.http.Response;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;

@SuppressWarnings("unused")
public class RedisPreparer extends BasePreparer {
    private static final Logger log = getLogger(RedisPreparer.class);

    private static final String REDIS_SET_PATH = CONTEXT_PATH + "/redis/set";
    private static final String REDIS_GET_PATH = CONTEXT_PATH + "/redis/get";
    private static final String REDIS_HSET_PATH = CONTEXT_PATH + "/redis/hset";
    private static final String REDIS_HGET_PATH = CONTEXT_PATH + "/redis/hget";
    private static final String REDIS_EXPIRE_PATH = CONTEXT_PATH + "/redis/expire";

    public void set(int db, String key, String value) {
        Map<String, Object> params = new HashMap<>();
        params.put("redisDb", db);
        params.put("key", key);
        params.put("value", value);
        doCmd(params, REDIS_SET_PATH);
    }

    public String get(int db, String key) {
        Map<String, Object> params = new HashMap<>();
        params.put("redisDb", db);
        params.put("key", key);
        return doCmd(params, REDIS_GET_PATH);
    }

    public void expire(int db, String key, int seconds) {
        Map<String, Object> params = new HashMap<>();
        params.put("redisDb", db);
        params.put("key", key);
        params.put("expire", seconds);
        doCmd(params, REDIS_EXPIRE_PATH);
    }

    public void hset(int db, String key, String hashKey, String hashValue) {
        Map<String, Object> params = new HashMap<>();
        params.put("redisDb", db);
        params.put("key", key);
        params.put("hkey", hashKey);
        params.put("hvalue", hashValue);
        doCmd(params, REDIS_HSET_PATH);
    }

    public String hget(int db, String key, String hashKey) {
        Map<String, Object> params = new HashMap<>();
        params.put("redisDb", db);
        params.put("key", key);
        params.put("hkey", hashKey);
        return doCmd(params, REDIS_HSET_PATH);
    }

    private String doCmd(Map<String, Object> params, String path) {
        Response response = Http.to(testDataApiBaseUrl + path).post(Json.of(params));
        log.info("redis response {}", response.getBodyConverted());
        return response.json().get("object", String.class);
    }
}
