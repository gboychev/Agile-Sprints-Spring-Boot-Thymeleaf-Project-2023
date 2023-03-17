package com.example.project62434.services;


import com.example.project62434.models.User;

public interface AuthService {
    User login(String username, String password);
}
