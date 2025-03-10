package com.project.product.entity.base;

import com.project.product.security.AuthenticationFacade;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreRemove;
import jakarta.persistence.PreUpdate;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class BaseEntityListener {

    private final AuthenticationFacade authenticationFacade;

    public BaseEntityListener(AuthenticationFacade authenticationFacade) {
        this.authenticationFacade = authenticationFacade;
    }

    @PrePersist
    public void prePersist(BaseEntity entity) {
        Long currentUserId = authenticationFacade.getCurrentUserId();
        entity.setCreatedUserId(currentUserId);
        entity.setUpdatedUserId(currentUserId);
        entity.setCreatedDate(Instant.now());
        entity.setUpdatedDate(Instant.now());
    }

    @PreUpdate
    public void preUpdate(BaseEntity entity) {
        Long currentUserId = authenticationFacade.getCurrentUserId();
        entity.setUpdatedUserId(currentUserId);
        entity.setUpdatedDate(Instant.now());
    }

    @PreRemove
    public void preRemove(BaseEntity entity) {
        Long currentUserId = authenticationFacade.getCurrentUserId();
        entity.setDeletedUserId(currentUserId);
        entity.setDeletedDate(Instant.now());
    }
}