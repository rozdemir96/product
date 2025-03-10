package com.project.product.controller.login.model;

import com.project.product.constants.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoResponseModel {
    private Long userId;
    private String username;
    private Role role;
}
