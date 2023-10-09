package com.bearwaves.eos4jsample;

import com.badlogic.gdx.Gdx;
import com.bearwaves.eos4j.EOS;
import com.bearwaves.eos4j.EOSAchievements;
import com.bearwaves.eos4j.EOSAuth;
import com.bearwaves.eos4j.EOSConnect;
import com.bearwaves.eos4j.EOSException;
import com.bearwaves.eos4j.EOSLogging;
import com.bearwaves.eos4j.EOSPlatform;
import com.bearwaves.eos4j.EOSResultCode;
import com.bearwaves.eos4j.EOSStats;

public class EpicPlatformManager implements PlatformManager {

    private LoginState loginState;
    private EOSPlatform eosPlatform;
    private EOSConnect eosConnect;
    private EOSAuth eosAuth;
    private EOSStats eosStats;
    private EOSAchievements eosAchievements;
    private EOSAuth.IdToken idToken;
    private EOSConnect.IdToken connectToken;

    EpicPlatformManager() {
        this.loginState = LoginState.NOT_LOGGED_IN;
    }

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
        doEpicAuthLogin();
    }

    @Override
    public void tick() {
        if (eosPlatform != null) {
            eosPlatform.tick();
        }
    }

    @Override
    public void dispose() {
        if (connectToken != null) {
            connectToken.release();
        }
        if (idToken != null) {
            idToken.release();
        }
        if (eosPlatform != null) {
            eosPlatform.release();
        }
        try {
            EOS.shutdown();
        } catch (EOSException e) {
            Gdx.app.error("EpicPlatformManager", "Got error shutting down EOS SDK.", e);
        }
    }

    @Override
    public LoginState getLoginState() {
        return this.loginState;
    }

    private void doEpicAuthLogin() {
        loginState = LoginState.LOGGING_IN_EPIC;
        eosAuth.login(
                new EOSAuth.LoginOptions(
                        new EOSAuth.Credentials(EOS.LoginCredentialType.DEVELOPER, "localhost:1234", "eos4j"),
                        0
                ),
                callbackInfo -> {
                    try {
                        EOS.throwIfErrorCode(callbackInfo.resultCode);
                    } catch (EOSException e) {
                        Gdx.app.error("EpicPlatformManager", "Auth login failed", e);
                        loginState = LoginState.FAILED;
                        return;
                    }
                    Gdx.app.log("EpicPlatformManager", "Login success");
                    loginState = LoginState.LOGGING_IN_CONNECT;
                    try {
                        idToken = eosAuth.copyIdToken(callbackInfo.selectedAccountId);
                    } catch (EOSException e) {
                        Gdx.app.error("EpicPlatformManager", "Failed to copy id token", e);
                        loginState = LoginState.FAILED;
                        return;
                    }
                    Gdx.app.debug("EpicPlatformManager", "Got ID token: " + idToken.getJsonWebToken());
                    doEOSConnectLogin();
                }
        );
    }

    private void doEOSConnectLogin() {
        eosConnect.login(
                new EOSConnect.LoginOptions(
                        new EOSConnect.Credentials(EOS.ExternalCredentialType.EPIC_ID_TOKEN, idToken.getJsonWebToken()), null
                ),
                loginCallbackInfo -> {
                    if (loginCallbackInfo.resultCode == EOSResultCode.INVALID_USER.getValue()) {
                        // Invalid user - needs to be created
                        Gdx.app.log("EpicPlatformManager", "Creating user");
                        eosConnect.createUser(
                                loginCallbackInfo.continuanceToken,
                                createUserCallbackInfo -> handleConnectCallback(createUserCallbackInfo.resultCode, createUserCallbackInfo.localUserId)
                        );
                        return;
                    }
                    handleConnectCallback(loginCallbackInfo.resultCode, loginCallbackInfo.localUserId);
                }
        );
    }

    private void handleConnectCallback(int resultCode, EOS.ProductUserId localUserId) {
        try {
            EOS.throwIfErrorCode(resultCode);
        } catch (EOSException e) {
            Gdx.app.error("EpicPlatformManager", "EOS Connect callback failed", e);
            loginState = LoginState.FAILED;
            return;
        }
        try {
            connectToken = eosConnect.copyIdToken(localUserId);
        } catch (EOSException e) {
            Gdx.app.error("EpicPlatformManager", "Failed to copy Connect token", e);
            loginState = LoginState.FAILED;
            return;
        }
        Gdx.app.log("EpicPlatformManager", "EOS Connect successful");
        loginState = LoginState.LOGGED_IN;
        eosConnect.addNotifyAuthExpiration(data -> {
            Gdx.app.log("EpicPlatformManager", "Connect token expired; refreshing");
            doEOSConnectLogin();
        });
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
