package com.kelsix.mymoviefinder.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TrailerModel {

    @SerializedName("id")
    private int id;

    @SerializedName("results")
    private List<Trailer> results;

    public int getId() {
        return id;
    }

    public List<Trailer> getResults() {
        return results;
    }

    public static class Trailer {
        @SerializedName("key")
        private String key;

        @SerializedName("name")
        private String name;

        public String getKey() {
            return key;
        }

        public String getName() {
            return name;
        }
    }
}
