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
