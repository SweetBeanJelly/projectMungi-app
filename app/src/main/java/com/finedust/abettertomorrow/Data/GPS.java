package com.finedust.abettertomorrow.Data;

public class GPS {

    public static double latitude; // static 클래스 변수 위도
    public static double longitube; // static 클래스 변수 경도

    public static double getLatitude() {
        return latitude;
    }

    public static void setLatitude(double latitude) {
        GPS.latitude = latitude;
    }

    public static double getLongitube() {
        return longitube;
    }

    public static void setLongitube(double longitube) {
        GPS.longitube = longitube;
    }

}
