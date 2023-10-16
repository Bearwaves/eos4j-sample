package com.bearwaves.eos4jsample.leaderboards;

public interface GetLeaderboardRanksCallback {

    void run(Result result);

    class Result {
        public final LeaderboardRecord[] records;

        public Result(LeaderboardRecord[] records) {
            this.records = records;
        }
    }

}
