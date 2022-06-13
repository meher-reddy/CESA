package com.techster_media.cesa.SendNotificationPack;

public class Data {
    private String Title;
    private String Message;
    private String Uid;


    public Data(String title, String message, String uid) {
        Title = title;
        Message = message;
        Uid = uid;

    }

    public Data() {
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }
    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }




}
