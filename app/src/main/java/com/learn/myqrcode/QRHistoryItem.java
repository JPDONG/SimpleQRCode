package com.learn.myqrcode;

/**
 * Created by dongjiangpeng on 2016/8/5 0005.
 */
public class QRHistoryItem {
    private String title;
    private String message;
    private String date;
    private int id;

    public QRHistoryItem(String title, String message, String date, int imageId) {
        this.title = title;
        this.message = message;
        this.date = date;
        this.id = imageId;
    }

    public int getId() {
        return id;
    }

    public void setId(int imageId) {
        this.id = imageId;
    }



    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
