package com.example.sony.group09_hw08;

import java.io.Serializable;

/**
 * Created by Sony on 4/19/2016.
 */
public class Message implements Serializable
{

    String timeStamp, messageText, receiver, sender, uid;
    boolean messageRead;

    public Message() {
    }

    public Message(String timeStamp, String messageText, String receiver, String sender, boolean messageRead) {
        this.timeStamp = timeStamp;
        this.messageText = messageText;
        this.receiver = receiver;
        this.sender = sender;
        this.messageRead = messageRead;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public boolean isMessageRead() {
        return messageRead;
    }

    public void setMessageRead(boolean messageRead) {
        this.messageRead = messageRead;
    }

    @Override
    public String toString() {
        return "Message{" +
                "timeStamp='" + timeStamp + '\'' +
                ", messageText='" + messageText + '\'' +
                ", receiver='" + receiver + '\'' +
                ", sender='" + sender + '\'' +
                ", messageRead=" + messageRead +
                '}';
    }
}
