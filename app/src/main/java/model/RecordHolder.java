package model;

import java.io.Serializable;

public class RecordHolder implements Comparable<RecordHolder>, Serializable {
    private String name = "";
    private int score = 0;
    private int rank;
    private float latitude = 0.0f;
    private float longitude = 0.0f;


    public RecordHolder() {
    }

    public int getRank() {
        return rank;
    }

    public RecordHolder setRank(int rank) {
        this.rank = rank;
        return this;
    }

    public String getName() {
        return name;
    }

    public RecordHolder setName(String name) {
        this.name = name;
        return this;
    }


    public int getScore() {
        return score;
    }

    public RecordHolder setScore(int score) {
        this.score = score;
        return this;
    }

    public float getLatitude() {
        return latitude;
    }

    public RecordHolder setLatitude(float latitude) {
        this.latitude = latitude;
        return this;
    }

    public float getLongitude() {
        return longitude;
    }

    public RecordHolder setLongitude(float longitude) {
        this.longitude = longitude;
        return this;
    }

    @Override
    public int compareTo(RecordHolder recordHolder) {
        int thisRecord = this.score;
        int otherRecord = recordHolder.getScore();
        if(thisRecord  > otherRecord){
            return 1;
        }else if(thisRecord < otherRecord){
            return -1;
        }
        return 0;
    }

    @Override
    public String toString() {
        return rank + ") name: "+ name +
                ", score: " + score;
    }
}
