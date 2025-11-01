package com.example.recipemanager.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "users")
public class User {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String username;
    private String email;
    private String password;
    private String fullName;

    public User(String username, String email, String password, String fullName) {
        this.username = username;
        this.email = email;
        this.password = password; // In a real app, this should be hashed
        this.fullName = fullName;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
