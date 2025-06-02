package com.Makushev.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "time_limit_exceed_log")
public class TimeLimitExceedLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String signature;

    private Long TimeMs;

    private LocalDateTime timestamp;

}
