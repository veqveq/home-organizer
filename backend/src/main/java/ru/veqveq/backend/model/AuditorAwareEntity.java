package ru.veqveq.backend.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.time.Instant;

@Getter
@Setter
@ToString
@NoArgsConstructor
@MappedSuperclass
@FieldNameConstants
public abstract class AuditorAwareEntity {
    /**
     * Время создания
     */
    @CreationTimestamp
    @Column(name = "create_stamp", updatable = false)
    private Instant createStamp;

    /**
     * Время изменения
     */
    @UpdateTimestamp
    @Column(name = "update_stamp")
    private Instant updateStamp;
}
