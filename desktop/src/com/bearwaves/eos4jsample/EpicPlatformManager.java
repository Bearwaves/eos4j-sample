package com.bearwaves.eos4jsample;

import com.badlogic.gdx.Gdx;
import com.bearwaves.eos4j.EOS;
import com.bearwaves.eos4j.EOSAchievements;
import com.bearwaves.eos4j.EOSAuth;
import com.bearwaves.eos4j.EOSConnect;
import com.bearwaves.eos4j.EOSEcom;
import com.bearwaves.eos4j.EOSException;
import com.bearwaves.eos4j.EOSLeaderboards;
import com.bearwaves.eos4j.EOSLogging;
import com.bearwaves.eos4j.EOSPlatform;
import com.bearwaves.eos4j.EOSResultCode;
import com.bearwaves.eos4j.EOSStats;
import com.bearwaves.eos4jsample.ecom.GetOffersCallback;
import com.bearwaves.eos4jsample.ecom.Offer;
import com.bearwaves.eos4jsample.leaderboards.GetLeaderboardDefinitionsCallback;
import com.bearwaves.eos4jsample.leaderboards.GetLeaderboardRanksCallback;
import com.bearwaves.eos4jsample.leaderboards.GetLeaderboardUserScoresCallback;
import com.bearwaves.eos4jsample.leaderboards.LeaderboardDefinition;
import com.bearwaves.eos4jsample.leaderboards.LeaderboardRecord;
import com.bearwaves.eos4jsample.leaderboards.LeaderboardUserScore;
import com.bearwaves.eos4jsample.stats.IngestStatCallback;
import com.bearwaves.eos4jsample.stats.Stat;
import com.bearwaves.eos4jsample.stats.GetStatsCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EpicPlatformManager implements PlatformManager {

    private LoginState loginState;
    private EOSPlatform eosPlatform;
    private EOSConnect eosConnect;
    private EOSAuth eosAuth;
    private EOSStats eosStats;
    private EOSLeaderboards eosLeaderboards;
    private EOSAchievements eosAchievements;
    private EOSEcom eosEcom;
    private EOSAuth.IdToken idToken;
    private EOSConnect.IdToken connectToken;
    private EOS.ProductUserId localUserId;
    private EOS.EpicAccountId epicAccountId;
    private String userIdString;

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

    @Override
    public String getUserId() {
        return userIdString;
    }

    @Override
    public void getStats(GetStatsCallback callback) {
        this.eosStats.queryStats(
                new EOSStats.QueryStatsOptions(this.localUserId, null, null, null, this.localUserId),
                result -> {
                    try {
                        EOS.throwIfErrorCode(result.resultCode);
                    } catch (EOSException e) {
                        Gdx.app.error("EpicPlatformManager", "Failed to query stats", e);
                        callback.run(null);
                        return;
                    }
                    int statsCount = eosStats.getStatsCount(localUserId);
                    Stat[] stats = new Stat[statsCount];
                    for (int i = 0; i < statsCount; i++) {
                        try {
                            EOSStats.Stat stat = eosStats.copyStatByIndex(new EOSStats.CopyStatByIndexOptions(localUserId, i));
                            stats[i] = new Stat(stat.name, stat.value);
                            stat.release();
                        } catch (EOSException e) {
                            Gdx.app.error("EpicPlatformManager", "Failed to copy stat", e);
                            callback.run(null);
                            return;
                        }
                    }
                    callback.run(new GetStatsCallback.Result(stats));
                }
        );
    }

    @Override
    public void ingestStat(Stat stat, IngestStatCallback callback) {
        this.eosStats.ingestStat(
                new EOSStats.IngestStatOptions(
                        localUserId,
                        localUserId,
                        new EOSStats.IngestData[]{new EOSStats.IngestData(stat.name, stat.value)}
                ),
                result -> {
                    try {
                        EOS.throwIfErrorCode(result.resultCode);
                        callback.run(true);
                    } catch (EOSException e) {
                        Gdx.app.error("EpicPlatformManager", "Failed to ingest stat", e);
                        callback.run(false);
                    }
                }
        );
    }

    @Override
    public void getLeaderboardDefinitions(GetLeaderboardDefinitionsCallback callback) {
        eosLeaderboards.queryLeaderboardDefinitions(
                new EOSLeaderboards.QueryLeaderboardDefinitionsOptions(localUserId, null, null),
                result -> {
                    try {
                        EOS.throwIfErrorCode(result.resultCode);
                    } catch (EOSException e) {
                        Gdx.app.error("EpicPlatformManager", "Failed to query LB definitions", e);
                        callback.run(null);
                        return;
                    }
                    int defsCount = eosLeaderboards.getLeaderboardDefinitionCount();
                    LeaderboardDefinition[] defs = new LeaderboardDefinition[defsCount];
                    for (int i = 0; i < defsCount; i++) {
                        try {
                            EOSLeaderboards.LeaderboardDefinition def = eosLeaderboards.copyLeaderboardDefinitionByIndex(new EOSLeaderboards.CopyLeaderboardDefinitionByIndexOptions(i));
                            defs[i] = new LeaderboardDefinition(def.leaderboardId, def.statName, LeaderboardDefinition.Aggregation.values()[def.aggregation.ordinal()], def.startTime, def.endTime);
                            def.release();
                        } catch (EOSException e) {
                            Gdx.app.error("EpicPlatformManager", "Failed to copy LB definition", e);
                            callback.run(null);
                            return;
                        }
                    }
                    callback.run(new GetLeaderboardDefinitionsCallback.Result(defs));
                }
        );
    }

    @Override
    public void getLeaderboardRanks(String leaderboardId, GetLeaderboardRanksCallback callback) {
        eosLeaderboards.queryLeaderboardRanks(
                new EOSLeaderboards.QueryLeaderboardRanksOptions(leaderboardId, this.localUserId),
                result -> {
                    try {
                        EOS.throwIfErrorCode(result.resultCode);
                    } catch (EOSException e) {
                        Gdx.app.error("EpicPlatformManager", "Failed to query leaderboard ranks", e);
                        callback.run(null);
                        return;
                    }
                    int numRecords = eosLeaderboards.getLeaderboardRecordCount();
                    LeaderboardRecord[] records = new LeaderboardRecord[numRecords];
                    for (int i = 0; i < numRecords; i++) {
                        try {
                            EOSLeaderboards.LeaderboardRecord record = eosLeaderboards.copyLeaderboardRecordByIndex(
                                    new EOSLeaderboards.CopyLeaderboardRecordByIndexOptions(i)
                            );
                            records[i] = new LeaderboardRecord(record.userDisplayName, record.rank, record.score);
                            record.release();
                        } catch (EOSException e) {
                            Gdx.app.error("EpicPlatformManager", "Failed to copy leaderboard record", e);
                            callback.run(null);
                            return;
                        }
                    }
                    callback.run(new GetLeaderboardRanksCallback.Result(records));
                }
        );
    }

    @Override
    public void getLeaderboardUserScores(LeaderboardDefinition[] definitions, GetLeaderboardUserScoresCallback callback) {
        EOSLeaderboards.UserScoresQueryStatInfo[] info = new EOSLeaderboards.UserScoresQueryStatInfo[definitions.length];
        for (int i = 0; i < definitions.length; i++) {
            info[i] = new EOSLeaderboards.UserScoresQueryStatInfo(definitions[i].statName, EOSLeaderboards.Aggregation.values()[definitions[i].aggregation.ordinal()]);
        }
        eosLeaderboards.queryLeaderboardUserScores(
                new EOSLeaderboards.QueryLeaderboardUserScoresOptions(
                        new EOS.ProductUserId[]{localUserId},
                        info,
                        null, null,
                        localUserId
                ),
                result -> {
                    try {
                        EOS.throwIfErrorCode(result.resultCode);
                    } catch (EOSException e) {
                        Gdx.app.error("EpicPlatformManager", "Failed to query user scores", e);
                        callback.run(null);
                        return;
                    }
                    HashMap<String, List<LeaderboardUserScore>> scores = new HashMap<>();
                    for (LeaderboardDefinition definition : definitions) {
                        ArrayList<LeaderboardUserScore> scoresList = new ArrayList<>();
                        int userScoresCount = eosLeaderboards.getLeaderboardUserScoreCount(new EOSLeaderboards.GetLeaderboardUserScoreCountOptions(definition.statName));
                        for (int i = 0; i < userScoresCount; i++) {
                            try {
                                EOSLeaderboards.LeaderboardUserScore score = eosLeaderboards.copyLeaderboardUserScoreByIndex(
                                        new EOSLeaderboards.CopyLeaderboardUserScoreByIndexOptions(i, definition.statName)
                                );
                                scoresList.add(new LeaderboardUserScore(score.userId.stringValue(), score.score));
                                score.release();
                            } catch (EOSException e) {
                                Gdx.app.error("EpicPlatformManager", "Failed to copy user score", e);
                                callback.run(null);
                                return;
                            }
                        }
                        scores.put(definition.statName, scoresList);
                    }
                    callback.run(new GetLeaderboardUserScoresCallback.Result(scores));
                }
        );
    }

    @Override
    public void getOffers(GetOffersCallback callback) {
        Gdx.app.log("EpicPlatformManager", "Num transactions: " + eosEcom.getTransactionCount(new EOSEcom.GetTransactionCountOptions(epicAccountId)));

        eosEcom.queryOffers(new EOSEcom.QueryOffersOptions(
                epicAccountId,
                null
        ), callbackInfo -> {
            try {
                EOS.throwIfErrorCode(callbackInfo.resultCode);
            } catch (EOSException e) {
                Gdx.app.error("EpicPlatformManager", "Failed to query offers", e);
                callback.run(null);
                return;
            }
            int offerCount = eosEcom.getOfferCount(new EOSEcom.GetOfferCountOptions(epicAccountId));
            String[] offerIds = new String[offerCount];
            Map<String, Offer> offersById = new HashMap<>();
            Gdx.app.log("EpicPlatformManager", "Found " + offerCount + " offers.");
            for (int i = 0; i < offerCount; i++) {
                try {
                    EOSEcom.CatalogOffer offer = eosEcom.copyOfferByIndex(new EOSEcom.CopyOfferByIndexOptions(epicAccountId, i));
                    EOS.throwIfErrorCode(offer.priceResult);
                    Gdx.app.log("EpicPlatformManager", "Found offer: " + offer.titleText + ": " + offer.id);
                    offerIds[i] = offer.id;
                    float cost = (float) (offer.currentPrice / (Math.pow(10, offer.decimalPoint)));
                    offersById.put(offer.id, new Offer(
                            offer.id, offer.titleText, offer.descriptionText, offer.longDescriptionText, cost + offer.currencyCode, offer.releaseDate, false
                    ));
                    offer.release();
                } catch (EOSException e) {
                    Gdx.app.error("EpicPlatformManager", "Failed to copy offer", e);
                    callback.run(null);
                    return;
                }
            }

            eosEcom.queryOwnership(
                    new EOSEcom.QueryOwnershipOptions(epicAccountId, offerIds, null),
                    ownershipCallbackData -> {
                        try {
                            EOS.throwIfErrorCode(ownershipCallbackData.resultCode);
                        } catch (EOSException e) {
                            Gdx.app.error("EpicPlatformManager", "Failed to query ownership", e);
                            callback.run(null);
                            return;
                        }
                        for (EOSEcom.ItemOwnership ownership : ownershipCallbackData.itemOwnerships) {
                            if (ownership.ownershipStatus == EOSEcom.OwnershipStatus.OWNED) {
                                offersById.get(ownership.id).owned = true;
                            }
                        }
                        Gdx.app.log("EpicPlatformManager", "Queried ownership successfully.");
                        eosEcom.queryOwnershipToken(
                                new EOSEcom.QueryOwnershipTokenOptions(epicAccountId, offerIds, null),
                                tokenCallback -> {
                                    try {
                                        EOS.throwIfErrorCode(tokenCallback.resultCode);
                                    } catch (EOSException e) {
                                        Gdx.app.error("EpicPlatformManager", "Failed to query ownership token", e);
                                        callback.run(null);
                                        return;
                                    }
                                    Gdx.app.log("EpicPlatformManager", "Got ownership token: " + tokenCallback.ownershipToken);
                                    callback.run(new GetOffersCallback.Result(offersById.values().toArray(new Offer[]{})));
                                }
                        );
                    }
            );
        });
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
                    epicAccountId = callbackInfo.selectedAccountId;
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
        this.localUserId = localUserId;
        try {
            this.userIdString = this.localUserId.stringValue();
        } catch (EOSException e) {
            Gdx.app.error("EpicPlatformManager", "Failed to get String representation of local user ID", e);
            this.loginState = LoginState.FAILED;
            return;
        }
        this.loginState = LoginState.LOGGED_IN;
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
            eosLeaderboards = eosPlatform.getLeaderboardsHandle();
        } catch (EOSException e) {
            Gdx.app.error("EpicPlatformManager", "Failed to get EOS Leaderboards handle", e);
            return;
        }

        try {
            eosAchievements = eosPlatform.getAchievementsHandle();
        } catch (EOSException e) {
            Gdx.app.error("EpicPlatformManager", "Failed to get EOS Achievements handle", e);
        }

        try {
            eosEcom = eosPlatform.getEcomHandle();
        } catch (EOSException e) {
            Gdx.app.error("EpicPlatformManager", "Failed to get EOS Ecom handle", e);
        }
    }
}
