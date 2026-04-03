package com.taxpro.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "wppw_users")
public class User {

    @Id
    @Column(name = "ID")
    private Long id;

    @Column(name = "user_login")
    private String userLogin;

    @Column(name = "user_pass")
    private String userPass;

    @Column(name = "display_name")
    private String displayName;

    @Column(name = "user_status")
    private Integer userStatus;

    public Long getId()            { return id; }
    public String getUserLogin()   { return userLogin; }
    public String getUserPass()    { return userPass; }
    public String getDisplayName() { return displayName; }
    public Integer getUserStatus() { return userStatus; }
}