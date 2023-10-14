package com.bearwaves.eos4jsample.leaderboards;

public interface GetLeaderboardDefinitionsCallback {
    void run(Result result);

    class Result {
        public final LeaderboardDefinition[] definitions;

        public Result(LeaderboardDefinition[] definitions) {
            this.definitions = definitions;
        }
    }
}
