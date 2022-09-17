package com.example.readbooks.models;

import java.io.Serializable;

public class Book implements Serializable {
    private String key;
    private String title;
    private String author;
    private String review;
    private String dateStart;
    private String dateEnd;
    // from 0 to 5
    private int vote;

    public Book() {}

    public Book(String key) {
        this.key = key;
    }

    public Book(String key, String title, String author, String review, String dateStart, String dateEnd, int vote) {
        this.key = key;
        this.title = title;
        this.author = author;
        this.review = review;
        this.dateStart = dateStart;
        this.dateEnd = dateEnd;
        this.vote = vote;
    }

    public String bookToString() {
        return this.title + " - " + this.author;
    }

//    /**
//     * Converts a date in string type to CustomDate type
//     * @param date expected a date such as "dd/mm/yyyy"
//     */
//    public CustomDate dbStringToDate(String date) {
//        String[] splitted = date.split("/");
//        int day = Integer.parseInt(splitted[0]);
//        int month = Integer.parseInt(splitted[1]);
//        int year = Integer.parseInt(splitted[2]);
//        return new CustomDate(day, month, year);
//    }

    public String getKey() {
        return this.key;
    }
    public void setKey(String key) {
        this.key = key;
    }

    public String getTitle() {
        return this.title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return this.author;
    }
    public void setAuthor(String author) {
        this.author = author;
    }

    public String getReview() {
        return this.review;
    }
    public void setReview(String review) {
        this.review = review;
    }

    public String getDateStart() {
        return this.dateStart;
    }
    public void setDateStart(String date) {
        this.dateStart = date;
    }
    public void setDateStart(int day, int month, int year) {
        this.dateStart = day + "/" + month + "/" + year;
    }

    public String getDateEnd() {
        return this.dateEnd;
    }
    public void setDateEnd(String date) {
        this.dateEnd = date;
    }
    public void setDateEnd(int day, int month, int year) {
        this.dateEnd = day + "/" + month + "/" + year;
    }

    public int getVote() {
        return this.vote;
    }
    public void setVote(int vote) {
        this.vote = vote;
    }
}
