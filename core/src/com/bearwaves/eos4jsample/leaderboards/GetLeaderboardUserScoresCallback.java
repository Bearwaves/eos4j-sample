package com.bearwaves.eos4jsample.leaderboards;

import java.util.List;
import java.util.Map;

public interface GetLeaderboardUserScoresCallback {

    void run(Result result);

    class Result {
        public final Map<String, List<LeaderboardUserScore>> userScores;

        public Result(Map<String, List<LeaderboardUserScore>> userScores) {
            this.userScores = userScores;
        }
    }
}
