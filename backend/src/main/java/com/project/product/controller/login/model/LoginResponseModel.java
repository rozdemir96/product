package com.project.product.controller.login.model;

import lombok.Getter;

@Getter
public class LoginResponseModel {
    private final String token;

    public LoginResponseModel(String token) {
        this.token = token;
    }
}
