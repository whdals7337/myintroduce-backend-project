package com.myintroduce.config.redisstrategy;

import java.io.IOException;

public interface OSStrategy {
    public Process executeGrepProcessCommand(int port) throws IOException;
}
