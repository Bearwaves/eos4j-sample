package com.bearwaves.eos4jsample.ecom;

import java.util.Date;

public class Offer {
    public final String id;
    public final String title;
    public final String description;
    public final String longDescription;
    public final String price;
    public final Date releaseDate;
    public boolean owned;

    public Offer(String id, String title, String description, String longDescription, String price, Date releaseDate, boolean owned) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.longDescription = longDescription;
        this.price = price;
        this.releaseDate = releaseDate;
        this.owned = owned;
    }
}
