package com.Makushev.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@Table(name = "data_source_error_log")
public class DataSourceErrorLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "exception_text", columnDefinition = "TEXT", nullable = false)
    private String exceptionText;

    @Column(nullable = false)
    private String message;

    @Column(nullable = false)
    private String signature;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    public DataSourceErrorLog() {

    }
}
