package com.bearwaves.eos4jsample;

import com.bearwaves.eos4jsample.leaderboards.GetLeaderboardDefinitionsCallback;
import com.bearwaves.eos4jsample.stats.IngestStatCallback;
import com.bearwaves.eos4jsample.stats.Stat;
import com.bearwaves.eos4jsample.stats.GetStatsCallback;

public interface PlatformManager {
    void init();

    void tick();

    void dispose();

    LoginState getLoginState();

    void getStats(GetStatsCallback callback);

    void ingestStat(Stat stat, IngestStatCallback callback);

    void getLeaderboardDefinitions(GetLeaderboardDefinitionsCallback callback);

}
