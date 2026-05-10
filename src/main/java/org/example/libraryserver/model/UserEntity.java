package org.example.libraryserver.model;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String role;

    @Column(name = "security_question", nullable = false, columnDefinition = "TEXT DEFAULT ''")
    private String securityQuestion = "";

    @Column(name = "security_answer", nullable = false, columnDefinition = "TEXT DEFAULT ''")
    private String securityAnswer = "";

    public UserEntity() {}

    public int getId()                          { return id; }
    public void setId(int id)                   { this.id = id; }
    public String getUsername()                 { return username; }
    public void setUsername(String u)           { this.username = u; }
    public String getPassword()                 { return password; }
    public void setPassword(String p)           { this.password = p; }
    public String getRole()                     { return role; }
    public void setRole(String r)               { this.role = r; }
    public String getSecurityQuestion()         { return securityQuestion; }
    public void setSecurityQuestion(String q)   { this.securityQuestion = q; }
    public String getSecurityAnswer()           { return securityAnswer; }
    public void setSecurityAnswer(String a)     { this.securityAnswer = a; }
}
