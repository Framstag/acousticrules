# About this directory

This directory contains a `jvm.config` file, which contains
some JDK arguments maven has to use, to get the application
build with JQAssistant.

Reason is, that JQAssistant (or better Neo4J) still uses private
APIs that have to be explicitly opened under JDK 17+.