package com.hogarfix.model.base;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class Auditable {

    @Column(name = "is_activo", nullable = false)
    private Boolean isActivo = true;

    @Column(name = "created_at", columnDefinition = "DATETIME")
    private LocalDateTime createdAt;  

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.isActivo = this.isActivo == null ? true : this.isActivo;
    }

}
