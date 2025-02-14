package com.sayweee.spock.mockfree.karate;

import com.google.common.collect.ImmutableMap;
import com.intuit.karate.Http;
import com.intuit.karate.Json;
import com.intuit.karate.http.Response;
import org.slf4j.Logger;

import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;

@SuppressWarnings("unused")
public class ElasticsearchPreparer extends BasePreparer {
    private static final Logger log = getLogger(ElasticsearchPreparer.class);

    private static final String ELASTICSEARCH_QUERY_PATH = CONTEXT_PATH + "/es/query";

    public Object query(String instance, String index, String dsl) {
        Map<String, Object> body = new ImmutableMap.Builder<String, Object>()
                .put("instance", instance)
                .put("index", index)
                .put("dsl", dsl)
                .build();
        Response response = Http.to(testDataApiBaseUrl + ELASTICSEARCH_QUERY_PATH).post(Json.of(body));
        log.info("Elasticsearch query response {}", response.getBodyConverted());
        return response.json().get("object", Object.class);
    }
}
