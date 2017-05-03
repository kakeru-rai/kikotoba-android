package net.snow69it.listeningworkout.audio;

import java.util.ArrayList;

public class Track {

    private static String assetFormat = "audio/en/voa_%03d.mp3";
//    private static String assetFormat = "audio/en/alice01_%02d%02d%02d_%02d.mp3";

    public static ArrayList<Track> createTrackList() {
        ArrayList<Track> trackList = new ArrayList<Track>();
        trackList.add(new Track(0, 0, 0, 1));
        trackList.add(new Track(0, 0, 0, 2));
        trackList.add(new Track(0, 0, 0, 3));
        trackList.add(new Track(0, 0, 0, 4));
        trackList.add(new Track(0, 0, 0, 5));
        trackList.add(new Track(0, 0, 0, 6));
        trackList.add(new Track(0, 0, 0, 7));
        trackList.add(new Track(0, 0, 0, 8));
        trackList.add(new Track(0, 0, 0, 9));
        trackList.add(new Track(0, 0, 0, 10));
        return trackList;
    }

    private int hour;
    private int minute;
    private int second;
    private int milliSecond;

    private String fileName;

    public Track(int hour, int minute, int second, int milliSecond) {
        this.hour = hour;
        this.minute = minute;
        this.second = second;
        this.milliSecond = milliSecond;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public int getSecond() {
        return second;
    }

    public void setSecond(int second) {
        this.second = second;
    }

    public int getMilliSecond() {
        return milliSecond;
    }

    public void setMilliSecond(int milliSecond) {
        this.milliSecond = milliSecond;
    }

    public String getFileName() {
        return String.format(assetFormat, milliSecond);
//        return String.format(assetFormat, hour, minute, second, milliSecond);
    }


}
