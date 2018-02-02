package com.finn.domain;

import org.hibernate.validator.constraints.Email;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


public class UserDTO {

    @NotNull
    @Size(min = 4, max = 16)
    String password;

    @NotNull
    @Size(min = 4, max = 20)
    String name;

    @NotNull
    @Email
    String email;

    public UserDTO() {
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String nickname) {
        this.name = nickname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
