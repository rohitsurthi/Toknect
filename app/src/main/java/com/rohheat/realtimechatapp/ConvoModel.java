package com.rohheat.realtimechatapp;

public class ConvoModel {

    private boolean seen;
    private long timestamp;

    public ConvoModel(){

    }

    public ConvoModel(boolean seen, long timestamp) {
        this.seen = seen;
        this.timestamp = timestamp;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
