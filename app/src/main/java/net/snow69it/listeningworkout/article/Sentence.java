package net.snow69it.listeningworkout.article;

/**
 * Created by raix on 2017/01/24.
 */

public class Sentence {
    private String text;
    private int paragraph;
    private int track;
    private float fromSec;
    private float toSec;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getParagraph() {
        return paragraph;
    }

    public void setParagraph(int paragraph) {
        this.paragraph = paragraph;
    }

    public int getTrack() {
        return track;
    }

    public void setTrack(int track) {
        this.track = track;
    }

    public float getFromSec() {
        return fromSec;
    }

    public void setFromSec(float fromSec) {
        this.fromSec = fromSec;
    }

    public float getToSec() {
        return toSec;
    }

    public void setToSec(float toSec) {
        this.toSec = toSec;
    }
}
