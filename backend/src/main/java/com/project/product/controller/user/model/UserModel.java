package com.project.product.controller.user.model;

import com.project.product.constants.Role;
import com.project.product.model.BaseModel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserModel extends BaseModel {
    private String name;
    private String surname;
    private String email;
    private String mobilePhone;
    private String address;
    private String username;
    private String password;
    private Boolean enabled;
    private Role role;  // Kullanıcı rolü (ADMIN, EMPLOYEE, CUSTOMER)

    // BaseModel alanlarını başlangıçta NULL olarak setleyen Constructor
    public UserModel(String name, String surname, String email, String mobilePhone, String address, String username, String password, Boolean enabled, Role role) {
        super(null, null, null, null, null, null, null); // BaseModel alanlarını başlangıçta NULL olarak setler
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.mobilePhone = mobilePhone;
        this.address = address;
        this.username = username;
        this.password = password;
        this.enabled = enabled;
        this.role = role;
    }
}

