#!/bin/bash
OLDIFS=$IFS
SOURCE="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
CLASSPATH=$(JARS=("$SOURCE"/../lib/*.jar); IFS=:; echo "${JARS[*]}")
java -cp $CLASSPATH:. noaa.ioos.comt.jut.App
IFS=$OLDIFS
