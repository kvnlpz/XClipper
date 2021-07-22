package com.example.xclipper;

import java.util.Date;

public class Message {
    private Date date;
    private String from, message;

    public Message(String from, String message) {
//        this.date = date;
        this.from = from;
        this.message = message;
    }

    public String getFrom() {
        return from;
    }

    public String getMessage() {
        return message;
    }

    public Date getDate() {
        return date;
    }
}