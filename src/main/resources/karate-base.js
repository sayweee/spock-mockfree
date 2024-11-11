function fn() {
    karate.configure('connectTimeout', 10000);
    karate.configure('readTimeout', 10000);
    let DataBasePreparer = Java.type('com.sayweee.spock.mockfree.karate.DataBasePreparer');
    let RedisPreparer = Java.type('com.sayweee.spock.mockfree.karate.RedisPreparer');
    let JwtPreparer = Java.type('com.sayweee.spock.mockfree.karate.JwtPreparer');
    let LocalConfig = Java.type('com.sayweee.spock.mockfree.karate.LocalConfig');
    let local_server_port = karate.properties[LocalConfig.LOCAL_SERVER_PORT];
    return {
        baseUrl: 'http://localhost:' + local_server_port,
        db: new DataBasePreparer(),
        jwt: new JwtPreparer(),
        redis: new RedisPreparer(),
    };
}