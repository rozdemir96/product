package com.project.product.service;

import com.project.product.controller.user.model.UserModel;

import java.util.List;
import java.util.Optional;

public interface UserService {
    UserModel save(UserModel userModel);

    /**
     * ID’ye göre kullanıcı getirir.
     */
    Optional<UserModel> getById(Long id);

    /**
     * Tüm kullanıcıları getirir.
     */
    List<UserModel> getList();

    /**
     * Kullanıcı bilgilerini günceller.
     */
    UserModel update(UserModel userModel);

    /**
     * Kullanıcıyı ID’ye göre siler.
     */
    void delete(Long id);

    /**
     * E-posta sistemde kayıtlı mı?
     */
    boolean isEmailExists(String email);

    /**
     * Kullanıcı adı sistemde kayıtlı mı?
     */
    boolean isUsernameExists(String username);
}
