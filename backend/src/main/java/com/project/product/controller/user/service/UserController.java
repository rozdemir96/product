package com.project.product.controller.user.service;

import com.project.product.constants.Role;
import com.project.product.controller.user.model.UserModel;
import com.project.product.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Kullanıcı Yönetimi", description = "Kullanıcı CRUD işlemleri yönetilir.")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Yeni kullanıcı oluşturma
     */
    @PostMapping("/save")
    @PreAuthorize("isAnonymous()") // Sadece giriş yapmamış kullanıcılar kayıt olabilir
    public ResponseEntity<UserModel> save(@RequestBody UserModel userModel) {
        userModel.setRole(Role.CUSTOMER);
        userModel.setEnabled(true);

        UserModel savedUser = userService.save(userModel);
        return ResponseEntity.ok(savedUser);
    }

    /**
     * Yeni Employee (Çalışan) oluşturma - Sadece ADMIN erişebilir.
     */
    @PostMapping("/saveEmployee")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserModel> saveEmployee(@RequestBody UserModel userModel) {
        userModel.setRole(Role.EMPLOYEE); // Rolü otomatik olarak EMPLOYEE yap
        userModel.setEnabled(true); // Varsayılan olarak aktif olsun

        UserModel savedUser = userService.save(userModel);
        return ResponseEntity.ok(savedUser);
    }

    /**
     * ID’ye göre kullanıcı getirme
     */
    @GetMapping("/get{id}")
    public ResponseEntity<UserModel> getById(@PathVariable Long id) {
        return userService.getById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Tüm kullanıcıları getirme
     */
    @GetMapping("/list")
    public ResponseEntity<List<UserModel>> getList() {
        List<UserModel> users = userService.getList();
        return ResponseEntity.ok(users);
    }

    /**
     * Kullanıcı bilgilerini güncelleme
     */
    @PutMapping("/update")
    public ResponseEntity<UserModel> updateUser(@RequestBody UserModel userModel) {
        UserModel updatedUser = userService.update(userModel);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * Kullanıcıyı ID’ye göre silme
     */
    @DeleteMapping("/delete{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * E-posta kontrolü: Email sistemde kayıtlı mı?
     */
    @GetMapping("/checkEmail")
    public ResponseEntity<Boolean> checkEmailExists(@RequestParam String email) {
        boolean exists = userService.isEmailExists(email);
        return ResponseEntity.ok(exists);
    }

    /**
     * Kullanıcı adı kontrolü: Username sistemde kayıtlı mı?
     */
    @GetMapping("/checkUsername")
    public ResponseEntity<Boolean> checkUsernameExists(@RequestParam String username) {
        boolean exists = userService.isUsernameExists(username);
        return ResponseEntity.ok(exists);
    }
}