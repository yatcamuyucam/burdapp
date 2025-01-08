package com.example.burdapp.Domain;

public class HelperClass {

    // Alanları private yapıyoruz
    private String name, email, password, gender;

    // Boş Constructor (Firebase için gereklidir)
    public HelperClass() {
    }

    // username olmadan kullanılan Constructor
    public HelperClass(String name, String email, String password, String gender) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.gender = gender;
    }

    // Getter ve Setter metotları
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
