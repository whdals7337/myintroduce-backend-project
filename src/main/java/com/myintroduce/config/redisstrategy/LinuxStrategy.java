package com.myintroduce.config.redisstrategy;

import java.io.IOException;

public class LinuxStrategy implements OSStrategy{
    @Override
    public Process executeGrepProcessCommand(int port) throws IOException {
        String command = String.format("netstat -nat | grep LISTEN|grep %d", port);
        String[] shell = {"/bin/sh", "-c", command};
        return Runtime.getRuntime().exec(shell);
    }
}
