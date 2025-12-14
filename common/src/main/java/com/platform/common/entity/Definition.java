package com.platform.common.entity;

import com.platform.common.entity.base.BaseAudit;
import com.platform.common.enums.Status;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "definition", indexes = @Index(name = "idx_definition_code", columnList = "code"))
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
public class Definition extends BaseAudit {

    @EmbeddedId
    private Id id;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private Status status;

    @Builder.Default
    @Column(name = "is_current", nullable = false)
    private boolean isCurrent = false;

    @Lob
    @Column(name = "schema_lob", nullable = false)
    private String schemaLob;

    @Embeddable
    public record Id(
            @Column(name = "code", length = 100, nullable = false)
            String code,

            @Column(name = "version", nullable = false)
            int version
    ) {
    }
}
