#!/usr/bin/env bash
./mvnw -q compile exec:java -Dexec.mainClass="io.github.incplusplus.chatroom.client.ClientWindow" -Dexec.cleanupDaemonThreads=false