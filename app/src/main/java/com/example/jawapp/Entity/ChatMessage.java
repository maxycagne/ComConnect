package com.example.jawapp.Entity;

import java.io.Serializable;

public class ChatMessage implements Serializable {

    private String senderId;
    private String message;


    public ChatMessage() {} // Needed for Firebase


    public ChatMessage(String senderId, String message) {
        this.senderId = senderId;
        this.message = message;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

