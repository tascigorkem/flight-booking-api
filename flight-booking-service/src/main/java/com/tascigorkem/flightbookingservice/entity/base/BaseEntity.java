package com.tascigorkem.flightbookingservice.entity.base;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@SuperBuilder
@MappedSuperclass
public abstract class BaseEntity {

    @Id
    @Column(name = "id", columnDefinition = "uuid", updatable = false)
    protected UUID id;

    @CreationTimestamp
    @Column(name = "creation_time")
    protected LocalDateTime creationTime;

    @UpdateTimestamp
    @Column(name = "update_time")
    protected LocalDateTime updateTime;

    @Column(name = "deletion_time")
    protected LocalDateTime deletionTime;
}
