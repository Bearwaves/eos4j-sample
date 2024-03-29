package com.bearwaves.eos4jsample;

import com.bearwaves.eos4jsample.ecom.GetOffersCallback;
import com.bearwaves.eos4jsample.leaderboards.GetLeaderboardDefinitionsCallback;
import com.bearwaves.eos4jsample.leaderboards.GetLeaderboardRanksCallback;
import com.bearwaves.eos4jsample.leaderboards.GetLeaderboardUserScoresCallback;
import com.bearwaves.eos4jsample.leaderboards.LeaderboardDefinition;
import com.bearwaves.eos4jsample.stats.IngestStatCallback;
import com.bearwaves.eos4jsample.stats.Stat;
import com.bearwaves.eos4jsample.stats.GetStatsCallback;

public interface PlatformManager {
    void init();

    void tick();

    void dispose();

    LoginState getLoginState();

    String getUserId();

    void getStats(GetStatsCallback callback);

    void ingestStat(Stat stat, IngestStatCallback callback);

    void getLeaderboardDefinitions(GetLeaderboardDefinitionsCallback callback);

    void getLeaderboardRanks(String leaderboardId, GetLeaderboardRanksCallback callback);

    void getLeaderboardUserScores(LeaderboardDefinition[] definitions, GetLeaderboardUserScoresCallback callback);

    void getOffers(GetOffersCallback callback);
}
