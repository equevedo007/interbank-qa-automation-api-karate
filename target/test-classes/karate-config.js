function fn() {
    var env = karate.env;
    if (!env) {
        env = 'local';
    }

    var config = {
        urlBase: 'https://petstore.swagger.io/v2',
        ContentType: 'application/json',
        authHeader: 'Basic ' + java.util.Base64.getEncoder().encodeToString('admin:admin'.getBytes('UTF-8')),

    };

    if (env == 'local') {
        config.urlBase = 'https://petstore.swagger.io/v2';
        config.ContentType  = 'application/json';
        config.authHeader = 'Basic ' + java.util.Base64.getEncoder().encodeToString('admin:admin'.getBytes('UTF-8'));



    } else if (env == 'servidor') {
        config.urlBase = 'https://petstore.swagger.io/v2';
        config.ContentType = 'application/json';
        config.authHeader = 'Basic ' + java.util.Base64.getEncoder().encodeToString('admin:admin'.getBytes('UTF-8'));

    }

    karate.configure('connectTimeout', 30000);
    karate.configure('readTimeout', 30000);
    karate.configure('logPrettyRequest', true);
    karate.configure('logPrettyResponse', true);

    return config;
}