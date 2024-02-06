package com.bearwaves.eos4jsample.ecom;

public interface GetOffersCallback {
    void run(Result result);

    class Result {
        public final Offer[] offers;

        public Result(Offer[] offers) {
            this.offers = offers;
        }
    }
}
