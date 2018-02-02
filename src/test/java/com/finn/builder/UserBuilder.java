package com.finn.builder;

import com.finn.domain.User;


public class UserBuilder {

    private Long id;
    private String name;
    private String email;
    private String password;

    public UserBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public UserBuilder setEmail(String email) {
        this.email = email;
        return this;
    }

    public UserBuilder setPassword(String password) {
        this.password = password;
        return this;
    }

    public UserBuilder setPassword(Long id) {
        this.id = id;
        return this;
    }

    public User createUser() {
        User user = new User(name, email, password);
        user.setUserId(id);
        return user;
    }

    public User createDefaultUserVasya(){
        User user = new User("Vasya", "ewqweq@qwe.ew", "1111");
        user.setUserId(1L);
        return user;
    }
}