function fn() {
    karate.configure('connectTimeout', 10000);
    karate.configure('readTimeout', 10000);
    let DataBasePreparer = Java.type('com.sayweee.spock.mockfree.karate.DataBasePreparer');
    let ElasticsearchPreparer = Java.type('com.sayweee.spock.mockfree.karate.ElasticsearchPreparer');
    let RedisPreparer = Java.type('com.sayweee.spock.mockfree.karate.RedisPreparer');
    let JwtPreparer = Java.type('com.sayweee.spock.mockfree.karate.JwtPreparer');
    let ServicePreparer = Java.type('com.sayweee.spock.mockfree.karate.ServicePreparer');
    let LocalConfig = Java.type('com.sayweee.spock.mockfree.karate.LocalConfig');
    let local_server_port = karate.properties[LocalConfig.LOCAL_SERVER_PORT];
    let api_base_url = karate.properties[LocalConfig.TEST_DATA_API_BASEURL];
    return {
        baseUrl: 'http://localhost:' + local_server_port,
        apiBaseUrl: api_base_url,
        db: new DataBasePreparer(),
        es: new ElasticsearchPreparer(),
        jwt: new JwtPreparer(),
        redis: new RedisPreparer(),
        service: new ServicePreparer(),
    };
}