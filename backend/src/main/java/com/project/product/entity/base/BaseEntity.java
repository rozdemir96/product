package com.project.product.entity.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(BaseEntityListener.class) // Listener eklendi
public abstract class BaseEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "created_user_id", updatable = false)
    private Long createdUserId;

    @Column(name = "updated_user_id")
    private Long updatedUserId;

    @Column(name = "deleted_user_id")
    private Long deletedUserId;

    @JsonIgnore
    @Column(name = "createdDate", updatable = false)
    private Instant createdDate;

    @JsonIgnore
    @Column(name = "updatedDate")
    private Instant updatedDate;

    @JsonIgnore
    @Column(name = "deletedDate")
    private Instant deletedDate;
}
