package com.spring.backend.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "museums")
@Access(AccessType.FIELD)
public class Museum {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "name",nullable = false, unique = true)
    private String name;

    @Column(name= "location", nullable = false)
    private String location;


    @JsonIgnore
    @OneToMany(mappedBy = "museum")
    public List<Painting> paintings = new ArrayList<>();

    @JsonIgnore
    @ManyToMany
    @JoinTable(name = "users_museums", joinColumns = @JoinColumn(name = "id_museum"),
            inverseJoinColumns = @JoinColumn(name = "id_user"))
    public Set<User> users = new HashSet<>();
    public Museum(){}
    public Museum(Long id){
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public List<Painting> getPaintings() {
        return paintings;
    }

    public void setPaintings(List<Painting> paintings) {
        this.paintings = paintings;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

}
