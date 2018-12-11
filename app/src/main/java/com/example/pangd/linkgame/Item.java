package com.example.pangd.linkgame;

public class Item {
    private int imageId;

    private int backgroundId;

    public Item(int imageId, int backgroundId){
        this.imageId = imageId;
        this.backgroundId = backgroundId;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public int getBackgroundId() {
        return backgroundId;
    }

    public void setBackgroundId(int backgroundId) {
        this.backgroundId = backgroundId;
    }
}
