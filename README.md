# Juppy

![Java CI](https://github.com/Auo/juppy/workflows/Java%20CI/badge.svg)

A Java uptime monitor for personal use, currently there is no authentication or role handling.


Inspired by a [tweet](https://twitter.com/levelsio/status/1303812935773556736) from @levelsio, stating that
uptime monitors shouldn't be built because it's a saturated market.

I couldn't say no to that, it just seemed like a little fun project to tinker with.

## Endpoints

|Method | Path | Description |
|-------|------|------------|
| `GET` | /runners | get all runners |
| `GET` | /runners/:id | get specific runner |
| `DELETE` | /runners/:id | remove runner |
| `POST` | /runners | create runner to ping URL |
| `GET` | /results/:id | results of runner |
| `GET` | /health-check | 200 OK if running |


## Configuration

A path needs to be pass along to the service when launching it. This path should point out a configuration file.

### Example config file

```properties
# path to sqlite database, otherwise memory database will be used
sqlite.path=/path/to/dbfile.db
logback.path=/path/logback.xml
# port to run service on
server.port=3000
# user agent for runner
runner.user-agent=

# mail settings
# all settings prefix with "mail." will be used
mail.auth=true
mail.smtp.host=host
mail.smtp.port=port
mail.smtp.starttls.enable=true

# who result is mailed from
mail.from=
# where result should be mailed
mail.to=
# should be the same as mail.from ( if authenticaion is used )
# these credentials will be used when authentication with the smtp server
mail.auth.username=
mail.auth.password=
```

### Example logback.xml

```xml
<configuration debug="true">

    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>

    <root level="debug">
        <appender-ref ref="STDOUT" />
    </root>
</configuration>
```

## Run
Build the project locally with maven, `mvn clean install` and then go to the `target` directory.

To launch the application, run `java -jar juppy-1.0-SNAPSHOT-jar-with-dependencies.jar /path/to/config.properties`

