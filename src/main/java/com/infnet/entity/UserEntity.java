package com.infnet.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "usuario")
public class UserEntity extends PanacheEntity {

    @Column(nullable = false, length = 120)
    public String nome;

    @Column(nullable = false, unique = true, length = 180)
    public String email;

    @Column(nullable = false, length = 20)
    public String senha;

    public UserEntity() {
    }

    public UserEntity(String nome, String email, String senha) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public String getSenha() {
        return senha;
    }
}