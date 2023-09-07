package com.ivanova.librarian.Models;

import com.google.firebase.Timestamp;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CommentModel {

    private String userID;
    private String userName;
    private Timestamp date;
    private String text;

    public CommentModel() {
        this.userID = "";
        this.userName = "";
        this.date = new Timestamp(new Date(2000, 1, 1, 0, 0));
        this.text = "";
    }

    public CommentModel(String userID, String userName, Timestamp date, String text) {
        this.userID = userID;
        this.userName = userName;
        this.date = date;
        this.text = text;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yy HH:mm");
        return dateFormat.format(date.toDate());
    }

    public Timestamp getDateTimestamp() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
