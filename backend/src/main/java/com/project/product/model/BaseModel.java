package com.project.product.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseModel implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;

    private Long createdUserId;
    private Long updatedUserId;
    private Long deletedUserId;

    private Instant createdDate;
    private Instant updatedDate;
    private Instant deletedDate;
}
