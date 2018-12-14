# Ticktok.io (Alpha)
[![CircleCI](https://circleci.com/gh/ticktok-io/ticktok.io.svg?style=svg)](https://circleci.com/gh/ticktok-io/ticktok.io)

Ticktok.io is an efficient, managed and clustered scheduler that allows 
apps to receive accurate and reliable clock signals at scale. 

![screenshot](https://raw.githubusercontent.com/ticktok-io/brand/master/screenshots/screenshot_clocks_list.png)

## Installation
### Prerequisites
* [MongoDB](https://www.mongodb.com)
* [RabbitMQ](https://www.rabbitmq.com)

### Usage
```
docker run 
  -e RABBIT_URI=<rabbit uri for messaging bus>
  -e MONGO_URI=<mongo db uri>
  -e SELF_DOMAIN=<the domain name of the service>
  -e ACCESS_TOKEN=<sha1 encoded token>
  ticktok/ticktok:1.1.0 

```

## Official SDKs
* [Java](https://github.com/ticktok-io/ticktok-java-client) (wip)
* [Clojure](https://github.com/ticktok-io/ticktok-clojure-client) (wip)
* [JavaScript](https://github.com/ticktok-io/ticktok-js-client) (wip)
