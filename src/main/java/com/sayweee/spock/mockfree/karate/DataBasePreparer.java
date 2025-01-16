package com.sayweee.spock.mockfree.karate;

import com.intuit.karate.Http;
import com.intuit.karate.Json;
import com.intuit.karate.http.Response;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;

@SuppressWarnings("unused")
public class DataBasePreparer extends BasePreparer {
    private static final Logger log = getLogger(DataBasePreparer.class);

    private static final String DATABASE_SELECT_PATH = CONTEXT_PATH + "/database/select";
    private static final String DATABASE_UPDATE_PATH = CONTEXT_PATH + "/database/update";
    private static final String DATABASE_INSERT_PATH = CONTEXT_PATH + "/database/insert";
    private static final String DATABASE_DELETE_PATH = CONTEXT_PATH + "/database/delete";

    public List<Object> select(String instanceName, String sqlCmd) {
        Map<String, Object> body = new HashMap<>();
        body.put("instanceName", instanceName);
        body.put("sqlCmd", sqlCmd);
        Response response = Http.to(testDataApiBaseUrl + DATABASE_SELECT_PATH).post(Json.of(body));
        log.info("select response {}", response.getBodyConverted());
        return response.json().get("object");
    }

    public Integer update(String instanceName, String sqlCmd) {
        return dml(instanceName, sqlCmd, DATABASE_UPDATE_PATH);
    }

    public Integer insert(String instanceName, String sqlCmd) {
        return dml(instanceName, sqlCmd, DATABASE_INSERT_PATH);
    }

    public Integer delete(String instanceName, String sqlCmd) {
        return dml(instanceName, sqlCmd, DATABASE_DELETE_PATH);
    }

    private Integer dml(String instanceName, String sqlCmd, String cmdPath) {
        Map<String, Object> body = new HashMap<>();
        body.put("instanceName", instanceName);
        body.put("sqlCmd", sqlCmd);
        Response response = Http.to(testDataApiBaseUrl + cmdPath).post(Json.of(body));
        log.info("dml response {}", response.getBodyConverted());
        return response.json().get("object");
    }
}
