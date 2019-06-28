package com.example.iswakt.Data;

public class Users {
    String mName;
    String mEmail;
    String mId;

    public Users() {
    }

    public Users(String id, String name, String email) {
        this.mId=id;
        this.mName = name;
        this.mEmail = email;
    }

    public String getName() {
        return mName;
    }

    public String getEmail() {
        return mEmail;
    }

    public String getmId() {
        return mId;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public void setmEmail(String mEmail) {
        this.mEmail = mEmail;
    }

    public void setmId(String mId) {
        this.mId = mId;
    }
}
