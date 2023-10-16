package com.bearwaves.eos4jsample.leaderboards;

public class LeaderboardRecord {
    public final String displayName;
    public final int rank;
    public final int score;

    public LeaderboardRecord(String displayName, int rank, int score) {
        this.displayName = displayName;
        this.rank = rank;
        this.score = score;
    }
}
