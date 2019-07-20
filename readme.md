# cslabs-esxi

## Introduction

A lightweight webserver to serve as a simplified esxi api for the needs of the project.
The web services api could only be consumed from .net or java but unfortunately there is
no documentation for .net core. Therefor communication with the ESXI host will happen 
through this lightweight api.

## Requirements

**Gradle** - Head over to [gradle.org](https://gradle.org/) if you don't have it installed.

## Building

```
gradle build
```

## Running

You can either run the application form intellij or build a jar with

```bash
gradle buildJar
```

Navigate to `build/libs`.

Run it using the following template:

```bash
java -jar cslabs-esxi-all-1.0-SNAPSHOT.jar [host] [username] [password]
```

The webserver will start on `http://localhost:4567`.

Get VM console tickets by navigating to:

```
http://localhost:4567/acquireTicket
```


## Starting the websocket server

This part requires [docker](https://www.docker.com/) or an ngninx server

Replace `esxi.home.local` in teh nginx.conf and replace it with the esxi server that you will be using.

Run the following to start the nginx proxy.
```
docker-compose up
```

Add the `server.crt` and the `rootCA.crt` to Trusted Root authorities on windows so the browser will trust it.

Add the following to your host file.

```
127.0.0.1  websocket.home.local
```

If you point the frontend to that domain name it should proxy the connection.