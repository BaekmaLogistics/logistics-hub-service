package com.sparta.logistics.application.port;

public interface GraphVersionStore {
    long increment();

    long getCurrentVersion();
}
