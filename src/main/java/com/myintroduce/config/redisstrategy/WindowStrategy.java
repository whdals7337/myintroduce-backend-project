package com.myintroduce.config.redisstrategy;

import java.io.IOException;

public class WindowStrategy implements OSStrategy{
    @Override
    public Process executeGrepProcessCommand(int port) throws IOException {
        String command = String.format("netstat -nao | find \"LISTEN\" | find \"%d\"", port);
        String[] shell = {"cmd.exe", "/y", "/c", command};
        return Runtime.getRuntime().exec(shell);
    }
}
