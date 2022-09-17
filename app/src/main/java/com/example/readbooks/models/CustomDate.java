package com.example.readbooks.models;

public class CustomDate {
    private int day;
    private int month;
    private int year;

    public CustomDate() {}

    public CustomDate(int day, int month, int year) {
        this.day = day;
        this.month = month;
        this.year = year;
    }

    public int getDay() {
        return day;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }
}
