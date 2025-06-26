package com.Makushev.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "clients")
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    @NotBlank(message = "Name cannot be empty")
    private String name;

    @Column(nullable = false, length = 100)
    @NotBlank(message = "Surname cannot be empty")
    private String surname;

    @Column(length = 100)
    private String patronymic;

    @Column(name = "client_id", nullable = false, unique = true)
    private Long clientId;

}