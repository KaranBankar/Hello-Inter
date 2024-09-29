package com.example.hellointern;

public class HelperClass {
    String name,password,mobile;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public HelperClass(String name, String password, String mobile) {
        this.name = name;
        this.password = password;
        this.mobile = mobile;
    }

    public HelperClass(){

    }
}
