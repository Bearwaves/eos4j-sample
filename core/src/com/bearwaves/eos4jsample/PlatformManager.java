package com.bearwaves.eos4jsample;

public interface PlatformManager {
    void init();

    void tick();

    void dispose();

    LoginState getLoginState();
}
