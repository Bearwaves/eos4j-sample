package com.bearwaves.eos4jsample.stats;

public interface GetStatsCallback {

    void run(Result result);

    class Result {
        public final Stat[] stats;

        public Result(Stat[] stats) {
            this.stats = stats;
        }
    }
}
