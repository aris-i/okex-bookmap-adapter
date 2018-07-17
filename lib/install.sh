#!/bin/sh
mvn install:install-file -Dfile=bm-l1api.jar -DgroupId=com.bookmap \
    -DartifactId=bm-l1api -Dversion=7.0 -Dpackaging=jar
