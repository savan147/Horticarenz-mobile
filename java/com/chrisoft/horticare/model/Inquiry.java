package com.chrisoft.horticare.model;

/**
 * Created by insavan2000 on 15/03/2016.
 */
public class Inquiry {
    private String id; //UNIQUE ID
    private String name; //USER NAME

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    private String email; //EMAIL - MUST BE UNIQUE
    private String phone; //USER PASSWORD
    private String message; //USER ADDRESS

}
