package com.earl.db;

public class Ticket {
    private String pkg;
    private String email;
    private String msg;

    public Ticket(String pkg, String email, String msg) {
        this.msg = msg == null ? "null" : msg;
        this.email = email == null ? "null": email;
        this.pkg = pkg == null ? "null" : pkg;
    }

    public String getPkg() {
        return pkg;
    }

    public String getMsg() {
        return msg;
    }

    public String getEmail() {
        return email;
    }
}
