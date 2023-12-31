package com.bearwaves.eos4jsample;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.bearwaves.eos4j.EOS;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
    public static void main(String[] arg) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setForegroundFPS(60);
        config.setTitle("eos4j Sample");

        // Load eos4j
        if (!EOS.loadLibraries()) {
            throw new RuntimeException("Couldn't load EOS libraries");
        }

        PlatformManager epicPlatform = new EpicPlatformManager();

        new Lwjgl3Application(new GdxGame(epicPlatform), config);
    }
}
