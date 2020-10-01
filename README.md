# juppy

Java uptime monitor.

* Should be able to manage, see result of runners
* Should run in docker
* Should be able to send notifications when something goes wrong


## todo
* Write to DB (sqlite)
* Exception handling
    * 2 kinds of exceptions, internal and external: internal should be logged and then converted to external if it was triggered by a user action, otherwise just logged. 
* Custom Exceptions
* Unit testing
* Create an actual Reporter
* Better logging
* Super simple GUI with graphs over results