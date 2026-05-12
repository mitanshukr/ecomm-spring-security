package com.mitanshu.spring_ecomm.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "username"),
        @UniqueConstraint(columnNames = "email")
})
@Data
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    // make unique
    String username;
    String name;
    String email;
    String password;
    @CreationTimestamp
    Date createdAt;
    @UpdateTimestamp
    Date updatedAt;

    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE})
    @JoinColumn(name = "role_id", referencedColumnName = "id")
//    @ToString.Exclude
    Role role;

    public User(String username, String name, String email, String password, Role role){
        this.username = username;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }
}
