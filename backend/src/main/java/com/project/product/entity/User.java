package com.project.product.entity;

import com.project.product.constants.Role;
import com.project.product.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
@SQLDelete(sql = "UPDATE users SET deleted_date = CURRENT_TIMESTAMP, deleted_user_id = ? WHERE id = ?")
@Where(clause = "deleted_date IS NULL")
public class User extends BaseEntity {
    private String name;
    private String surname;
    private String email;
    private String mobilePhone;
    private String address;
    private String username;
    private String password;
    private Boolean enabled;

    @Enumerated(EnumType.STRING)
    private Role role;  // Kullanıcı rolü (ADMIN, EMPLOYEE, CUSTOMER)r
}
