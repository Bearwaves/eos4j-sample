package com.bearwaves.eos4jsample.leaderboards;

import java.util.Date;

public class LeaderboardDefinition {
    public final String id;
    public final String statName;
    public final Aggregation aggregation;
    public final Date startTime;
    public final Date endTime;

    public LeaderboardDefinition(String id, String statName, Aggregation aggregation, Date startTime, Date endTime) {
        this.id = id;
        this.statName = statName;
        this.aggregation = aggregation;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public enum Aggregation {
        MIN, MAX, SUM, LATEST
    }
}
