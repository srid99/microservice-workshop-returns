#!/bin/sh

: ${CODACY_PROJECT_TOKEN?"Need to set environment variable CODACY_PROJECT_TOKEN"}

curl -sL https://github.com/jpm4j/jpm4j.installers/raw/master/dist/biz.aQute.jpm.run.jar >t.jar
java -jar t.jar -u init
rm t.jar

~/jpm/bin/jpm install com.codacy:codacy-coverage-reporter:assembly

~/jpm/bin/codacy-coverage-reporter -l Java -r target/site/jacoco/jacoco.xml
