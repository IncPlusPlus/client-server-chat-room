#!/usr/bin/env bash
./mvnw -q compile exec:java -Dexec.mainClass="io.github.incplusplus.chatroom.server.Server" -Dexec.cleanupDaemonThreads=false