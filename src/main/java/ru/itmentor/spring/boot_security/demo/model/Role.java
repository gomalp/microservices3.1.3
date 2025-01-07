package ru.itmentor.spring.boot_security.demo.model;

import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

//GrantedAuthority сущность описывающая права юзера
@Entity
@Table(name = "roles")
public class Role implements GrantedAuthority { //сущность, описывающая права юзера в spring security
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Использование автоинкремента
    private Long id;

    private String role;

    public Role() {}

    public Role(String role) {
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
    @Override
    public String getAuthority() {
        return "ROLE_" + this.role;
    }

    @Override
    public String toString() {
        return "Role{" +
                "id=" + id +
                ", role='" + role + '\'' +
                '}';
    }
}
