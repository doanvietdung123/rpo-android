package com.spring.backend.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Access(AccessType.FIELD)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;
    @Column(name = "login", nullable = false, unique = true)
    private String login;
    @JsonIgnore
    @Column(name = "password")
    private String password;
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @JsonIgnore
    @Column(name = "salt")
    private String salt;

    @Column(name = "token")
    private String token;
    @Column(name = "activity")
    private LocalDateTime activity;
    @ManyToMany(mappedBy = "users")
    public Set<Museum> museums = new HashSet<>();

    @Transient
    public String np;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public LocalDateTime getActivity() {
        return this.activity;
    }

    public void setActivity(LocalDateTime activity) {
        this.activity = activity;
    }

    public Set<Museum> getMuseums() {
        return museums;
    }

    public void setMuseums(Set<Museum> museums) {
        this.museums = museums;
    }

    public void addMuseum(Museum museum) {
        this.museums.add(museum);
        museum.users.add(this);
    }

    public void removeMuseum(Museum museum) {
        this.museums.remove(museum);
        museum.users.remove(this);
    }
}
