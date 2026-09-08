package com.ecommerce.model;

public abstract class User {
    protected String id;
    protected String name;
    protected String email;
    protected String role;

    public User(String id, String name, String email, String role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public abstract String getRoleDetails();

    @Override
    public String toString() {
        return String.format("[%s] ID: %s | Name: %s | Email: %s", role, id, name, email);
    }
}
