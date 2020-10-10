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

## Todo
* Docker 
    * Java 13+
* Create an actual Reporter
    * Stdout
    * Email
    * SMS
* Add CLI options
    * Reporters
    * DB file location
    * Choose reporters
* Super simple GUI with graphs over results
* Remove results after X time
* Validate timeout and interval values
* Change timeout and interval to `ISO8601` format