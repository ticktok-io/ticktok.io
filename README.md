# Ticktok.io
[![CircleCI](https://circleci.com/gh/ticktok-io/ticktok.io.svg?style=svg)](https://circleci.com/gh/ticktok-io/ticktok.io)
[![Release](https://img.shields.io/github/release-pre/ticktok-io/ticktok.io.svg)](https://github.com/ticktok-io/ticktok.io/releases/0.2.1)
[![License](http://img.shields.io/:license-apache2.0-red.svg)](http://doge.mit-license.org)

Ticktok.io is an efficient, managed and clustered scheduler that allows 
apps to receive accurate and reliable clock signals at scale. 

![screenshot](https://raw.githubusercontent.com/ticktok-io/brand/master/screenshots/screenshot_clocks_list_v2.png)

## Quick start
```
docker run 
  -e RABBIT_URI=<rabbit uri for messaging bus>
  -e MONGO_URI=<mongo db uri>
  -e ACCESS_TOKEN=<sha1 encoded token>
  ticktok/ticktok:0.2.1
```

Documentation: https://ticktok.io/docs
	
## Community
Have some questions/ideas? chat with us on [Slack](https://join.slack.com/t/ticktokio/shared_invite/enQtNTE0MzExNTY5MjIzLThjNDU3NjIzYzQxZTY0YTM5ODE2OWFmMWU3YmQ1ZTViNDVmYjZkNWUzMWU5NWU0YmU5NWYxMWMxZjlmNGQ1Y2U)

## Official SDKs
* [ticktok-io/ticktok-java-client](https://github.com/ticktok-io/ticktok-java-client)
* [ticktok-io/ticktok-clojure-client](https://github.com/ticktok-io/ticktok-clojure-client)
* [ticktok-io/ticktok.js](https://github.com/ticktok-io/ticktok.js)
* [ticktok-io/ticktok-python](https://github.com/ticktok-io/ticktok-python)
