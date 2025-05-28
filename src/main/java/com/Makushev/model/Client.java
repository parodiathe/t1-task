package com.Makushev.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

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

    @Column(nullable = false, unique = true)
    @NotNull(message = "Client ID cannot be null")
    private UUID clientId;

    public Client(String name, String surname, String patronymic) {
        this.name = name;
        this.surname = surname;
        this.patronymic = patronymic;
        this.clientId = UUID.randomUUID();
    }
}
