package com.example.lostandfound.api;

public class SignupRequest {

    String name;
    String email;
    String password;

    public SignupRequest(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }
}