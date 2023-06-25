package com.socialmediabackend.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserManagement {

    @GetMapping("/users")
    public String getUsers(){
        return "Karthik";
    }

}
