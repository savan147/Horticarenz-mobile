package com.chrisoft.horticare.model;

import java.io.Serializable;

/**
 * Model used to store user information
 * Created by Lester on 19/08/2015.
 */
public class User implements Serializable{
    private String id; //UNIQUE ID
    private String name; //USER NAME
    private String email; //EMAIL - MUST BE UNIQUE
    private String password; //USER PASSWORD
    private String address; //USER ADDRESS
    private String country; //USER COUNTRY

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
