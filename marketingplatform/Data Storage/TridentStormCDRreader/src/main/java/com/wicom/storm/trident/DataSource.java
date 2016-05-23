package com.wicom.storm.trident;

public enum DataSource {
    CDR("cdr_post_data20130123_204_118088.dwh");
    //VEHICLE("Vehicles7904.csv"),
    //CASUALTY("Casualty7904.csv");

    public final String filename;

    DataSource(String filename) {
        this.filename = filename;
    }
}
