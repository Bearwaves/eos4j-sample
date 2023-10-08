package com.bearwaves.eos4jsample;

import com.badlogic.gdx.Gdx;
import com.bearwaves.eos4j.EOS;
import com.bearwaves.eos4j.EOSAchievements;
import com.bearwaves.eos4j.EOSAuth;
import com.bearwaves.eos4j.EOSConnect;
import com.bearwaves.eos4j.EOSException;
import com.bearwaves.eos4j.EOSLogging;
import com.bearwaves.eos4j.EOSPlatform;
import com.bearwaves.eos4j.EOSStats;

public class EpicPlatformManager implements PlatformManager {

    private EOSPlatform eosPlatform;
    private EOSConnect eosConnect;
    private EOSAuth eosAuth;
    private EOSStats eosStats;
    private EOSAchievements eosAchievements;

    @Override
    public void init() {
        // Initialise the EOS SDK.
        try {
            EOS.initialize(new EOS.InitializeOptions("eos4j-sample", "1.0.0"));
        } catch (EOSException e) {
            Gdx.app.error("EpicPlatformManager", "Failed to initialise EOS SDK", e);
            return;
        }

        // Set log level to verbose.
        try {
            EOSLogging.setLogLevel(EOSLogging.LogCategory.ALL_CATEGORIES, EOSLogging.LogLevel.VERBOSE);
        } catch (EOSException e) {
            Gdx.app.error("EpicPlatformManager", "Failed to set log level", e);
            return;
        }

        // Connect the SDK logs to Gdx logs.
        try {
            EOSLogging.setCallback(message -> {
                switch (message.level) {
                    case FATAL:
                    case ERROR:
                        Gdx.app.error("EOS::" + message.category, message.message);
                        break;
                    case WARNING:
                    case INFO:
                        Gdx.app.log("EOS::" + message.category, message.message);
                        break;
                    case VERBOSE:
                    case VERY_VERBOSE:
                        Gdx.app.debug("EOS::" + message.category, message.message);
                        break;
                }
            });
        } catch (EOSException e) {
            Gdx.app.error("EpicPlatformManager", "Failed to add logging callback", e);
            return;
        }

        createEOSInterfaces();
    }

    @Override
    public void tick() {
        if (eosPlatform != null) {
            eosPlatform.tick();
        }
    }

    @Override
    public void dispose() {
        if (eosPlatform != null) {
            eosPlatform.release();
        }
        try {
            EOS.shutdown();
        } catch (EOSException e) {
            Gdx.app.error("EpicPlatformManager", "Got error shutting down EOS SDK.", e);
        }
    }

    private void createEOSInterfaces() {
        try {
            eosPlatform = new EOSPlatform(new EOSPlatform.Options(
                    EpicVars.PRODUCT_ID, EpicVars.SANDBOX_ID, EpicVars.DEPLOYMENT_ID, EpicVars.CLIENT_ID, EpicVars.CLIENT_SECRET,
                    EOSPlatform.Flag.WINDOWS_ENABLE_OVERLAY_OPENGL.getValue()
            ));
        } catch (EOSException e) {
            Gdx.app.error("EpicPlatformManager", "Failed to create EOS Platform interface", e);
            return;
        }

        try {
            eosAuth = eosPlatform.getAuthHandle();
        } catch (EOSException e) {
            Gdx.app.error("EpicPlatformManager", "Failed to get EOS Auth handle", e);
            return;
        }

        try {
            eosConnect = eosPlatform.getConnectHandle();
        } catch (EOSException e) {
            Gdx.app.error("EpicPlatformManager", "Failed to get EOS Connect handle", e);
            return;
        }

        try {
            eosStats = eosPlatform.getStatsHandle();
        } catch (EOSException e) {
            Gdx.app.error("EpicPlatformManager", "Failed to get EOS Stats handle", e);
            return;
        }

        try {
            eosAchievements = eosPlatform.getAchievementsHandle();
        } catch (EOSException e) {
            Gdx.app.error("EpicPlatformManager", "Failed to get EOS Achievements handle", e);
        }
    }
}
